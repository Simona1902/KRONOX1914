package adventure;

import exceptions.GameException;
import implementazione.Kronox1914;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;
import database.DBManager;
import type.SlotMetadata;
import database.H2SaveSlotRepository;
import database.SaveSlotRepository;
import utils.AtmosphereThread;

/**
 * La classe Server gestisce l'avvio del server di gioco,
 * l'accettazione delle connessioni dei client e la gestione delle sessioni di gioco individuali.
 * Si occupa inoltre della gestione degli slot di salvataggio del gioco.
 * @author simona
 */
public class Server {
    private static ServerSocket serverSocketInstance;

    /**
     * Metodo principale per l'avvio del server di gioco.
     * Inizializza il server socket, accetta le connessioni dei client
     * e gestisce la logica di selezione/creazione degli slot di salvataggio
     * prima di avviare il motore di gioco per ciascun client.
     *
     * @param args Argomenti della riga di comando.
     */
    public static void main(String[] args) {
        int port = 12345;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nCatturato segnale di shutdown. Avvio chiusura pulita del server...");
            try {
                // Chiudi la connessione al database (gestita da DBManager)
                DBManager.closeConnection();
                System.out.println("Connessione al database chiusa.");

                // Chiudi il ServerSocket se è stato aperto e non è già chiuso
                if (serverSocketInstance != null && !serverSocketInstance.isClosed()) {
                    serverSocketInstance.close();
                    System.out.println("ServerSocket chiuso.");
                }
                System.out.println("Server spento pulitamente.");
            } catch (IOException e) {
                System.err.println("Errore durante la chiusura pulita del server: " + e.getMessage());
            }
        }));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("SERVER DI GIOCO AVVIATO - In attesa sulla porta " + port);
            serverSocketInstance = serverSocket;
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread atmosphereThread = null;
                
                try {
                    System.out.println(">> Giocatore connesso da: " + clientSocket.getInetAddress().getHostAddress());
                    DBManager.startConnection();
                    
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    Scanner in = new Scanner(clientSocket.getInputStream());

                    SaveSlotRepository slotRepository = new H2SaveSlotRepository();
                    int chosenSlotId = -1;
                    boolean isNewGame = false;

                    List<SlotMetadata> slots = slotRepository.loadAllSlotsMetadata();
                    // Mostra il menu degli slot ad ogni iterazione (per chiarezza in caso di "no" o errore)
                        out.println("      BENVENUTO! Scegli uno slot di salvataggio: ");
                        for (SlotMetadata slot : slots) {
                            String slotName = slot.getSlotName();
                            if (slot.isCompleted() && !slotName.startsWith("[PARTITA COMPLETATA]")) {
                                slotName = "[PARTITA COMPLETATA] " + slotName;
                            }
                            Timestamp lastSaved = slot.getLastSaved();
                            String dateString = (lastSaved != null) ? " (Salvato il: " + lastSaved.toString().substring(0, 19) + ")" : "";
                            out.println(slot.getSlotId() + ". " + slotName + dateString);
                        }
                    while (chosenSlotId == -1) {
                        out.println("\n-->");

                        if (!in.hasNextLine()) break;
                        String choiceStr = in.nextLine();
                        try {
                            int choice = Integer.parseInt(choiceStr);
                            if (choice >= 1 && choice <= 10) {
                                SlotMetadata selectedSlot = slots.get(choice - 1);

                                if ("[Nuova Partita]".equals(selectedSlot.getSlotName())) {
                                    // 1. Slot vuoto: Chiede il nome, inizia nuova partita
                                    isNewGame = true;
                                    chosenSlotId = choice; // Slot valido, esci dal loop di selezione
                                } else if (selectedSlot.isCompleted()) {
                                    // 3. Slot con partita completata: Chiede conferma per sovrascrivere
                                    out.println("ATTENZIONE: Questo slot contiene una partita completata.");
                                    out.println("Sei sicuro di volerla sovrascrivere e iniziare una nuova partita? (sì/no)");
                                    out.println("\n-->");
                                    if (!in.hasNextLine()) break;
                                    String confirmation = in.nextLine().trim().toLowerCase();
                                    
                                    if ("sì".equals(confirmation) || "si".equals(confirmation)) {
                                        isNewGame = true;
                                        chosenSlotId = choice; // Slot valido, esci dal loop
                                    } else {
                                        out.println("Operazione annullata. Ritorno alla selezione degli slot.");
                                        // Non assegnare chosenSlotId, il loop continua
                                    }
                                } else {
                                    // 2. Slot con partita in corso: Chiede se continuare o sovrascrivere
                                    out.println("Questo slot contiene una partita in corso.");
                                    out.println("Vuoi continuare la partita salvata o iniziare una nuova e sovrascriverla? (continua/nuova)");
                                    out.println("\n-->");
                                    if (!in.hasNextLine()) break;
                                    String actionChoice = in.nextLine().trim().toLowerCase();

                                    if (null == actionChoice) {
                                        out.println("Scelta non valida. Inserisci 'continua' o 'nuova'. Ritorno alla selezione degli slot.");
                                        // Non assegnare chosenSlotId, il loop continua
                                    } else switch (actionChoice) {
                                        case "continua":
                                            isNewGame = false; // Non è una nuova partita
                                            chosenSlotId = choice; // Slot valido, esci dal loop
                                            break;
                                        case "nuova":
                                            isNewGame = true; // Si vuole iniziare una nuova partita
                                            chosenSlotId = choice; // Slot valido, esci dal loop
                                            break;
                                        default:
                                            out.println("Scelta non valida. Inserisci 'continua' o 'nuova'. Ritorno alla selezione degli slot.");
                                            // Non assegnare chosenSlotId, il loop continua
                                            break;
                                    }
                                }
                            } else {
                                out.println("Scelta non valida. Inserisci un numero da 1 a 10.");
                            }
                        } catch (NumberFormatException e) {
                            out.println("Input non valido. Inserisci un numero.");
                        }
                    }
                    
                    if (chosenSlotId > 0) { // Se è stato scelto uno slot valido
                        // Chiedi il nome solo se si sta iniziando una NUOVA partita (inclusa la sovrascrittura)
                        if (isNewGame) {
                            String playerName = "";
                            boolean nameSet = false;
                            while (!nameSet) {
                                out.println("Inserisci il nome per il tuo salvataggio (max 30 caratteri):");
                                out.println("\n-->");
                                if (!in.hasNextLine()) {
                                    chosenSlotId = -2; // Sentinella per uscire
                                    break;
                                }
                                playerName = in.nextLine().trim();
                                if (playerName.length() > 0 && playerName.length() <= 30) {
                                    slotRepository.updateSlotName(chosenSlotId, "Partita di " + playerName);
                                    nameSet = true;
                                } else if (playerName.length() > 30) {
                                    out.println("Nome troppo lungo. Inserisci un nome con massimo 30 caratteri.");
                                } else {
                                    out.println("Il nome non può essere vuoto. Riprova.");
                                }
                            }
                            if (chosenSlotId == -2) break; // Esci se il client si disconnette durante l'inserimento nome
                        }
                        
                        out.println("Avvio del gioco sullo slot " + chosenSlotId + "...");
                        
                        GameDescription game = new Kronox1914();
                        Engine engine = new Engine(game);
                  
                        engine.execute(in, out, chosenSlotId, isNewGame);
                    }

                } catch (GameException | IOException e) {
                    System.err.println("Errore durante la sessione di gioco: " + e.getMessage());
                } finally {
                    if (atmosphereThread != null){
                        atmosphereThread.interrupt();
                    }
                    DBManager.closeConnection();
                    if (clientSocket != null && !clientSocket.isClosed()){
                        try {
                            clientSocket.close();
                        } catch (IOException e){
                            // Non fare nulla, la connessione è già chiusa
                        }
                    }
                    System.out.println(">> Giocatore disconnesso. Connessione chiusa.");
                }
            }
        } catch (java.net.SocketException e) {
            System.out.println("ServerSocket interrotto durante l'ascolto: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Errore critico del server, impossibile avviare: " + e.getMessage());
        }
    }
}