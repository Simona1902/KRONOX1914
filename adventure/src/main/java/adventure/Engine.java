package adventure;

import utils.Utils;
import parser.Parser;
import parser.ParserOutput;
import type.CommandType;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;
import exceptions.GameException;
import database.H2SaveSlotRepository;
import implementazione.Kronox1914;
import java.io.InputStream;
import database.SaveSlotRepository;

/**
 * La classe Engine è il cuore logico del gioco.
 * Gestisce l'interazione con il parser, l'esecuzione dei comandi,
 * l'aggiornamento dello stato del gioco e la comunicazione con il client.
 * @author simona
 */
public class Engine {

    private final GameDescription game;
    private final Parser parser;

    /**
     * Costruttore della classe Engine.
     * @param game L'istanza di GameDescription che rappresenta la logica del gioco.
     */
    public Engine(GameDescription game) {
        this.game = game;
        Set<String> stopwords = null;
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("stopwords");
            if (stream == null) {
                throw new IOException("File stopwords non trovato.");
            }
            stopwords = Utils.loadFileListInSet(stream);
        } catch (IOException e) {
            System.err.println("Errore caricamento stopwords: " + e.getMessage());
            stopwords = new java.util.HashSet<>(); // Usa un set vuoto per evitare il crash
        }
        this.parser = new Parser(stopwords);
    }

    /**
     * Avvia e gestisce il ciclo principale di esecuzione del gioco.
     * Questo metodo si occupa dell'inizializzazione, dell'introduzione (se nuovo gioco),
     * dell'elaborazione dei comandi utente e della gestione della fine del gioco.
     * @param in Il {@link java.util.Scanner} per leggere l'input dal client.
     * @param out Il {@link java.io.PrintWriter} per inviare output al client.
     * @param slotId L'ID dello slot di salvataggio corrente.
     * @param isNewGame {@code true} se si tratta di una nuova partita, {@code false} altrimenti.
     */
    public void execute(Scanner in, PrintWriter out, int slotId, boolean isNewGame) {
        try {
            if (game instanceof Kronox1914){
                ((Kronox1914) game).setClientOut(out);
            }
            game.init(slotId, isNewGame);
            if (isNewGame) {
                // --- FASE INTRO ---
                String welcomeHtml = "<html><body style='font-family: Segoe UI, sans-serif; font-size: 14pt; color: #FF9900; background-color: #000000; padding: 20px;'>"
                                 + game.getWelcomeMsg().replace("\n", "<br>")
                                 + "</body></html>";
                out.println("SHOW_INTRO:" + welcomeHtml);

                // Attendo che il client sia pronto (dopo che l'utente ha premuto Invio)
                String startCommand = "";
                while (!"start_game".equals(startCommand) && in.hasNextLine()) {
                    startCommand = in.nextLine();
                }
            }
            // --- FASE DI GIOCO ---
            sendRoomUpdate(out, game.getCurrentRoom());

            // Ciclo principale del gioco
            while (true) {
                out.println("\n--> ");
                if (!in.hasNextLine()) break;

                String command = in.nextLine();
                if ("request_room_update".equals(command)) {
                    sendRoomUpdate(out, game.getCurrentRoom());
                    continue;
                }
                ParserOutput p = parser.parse(command, game.getCommands(), game.getCurrentRoom().getObjects(), game.getInventory());

                if (p == null || p.getCommand() == null) {
                    out.println("Non capisco quello che mi vuoi dire.");
                } else if (p.getCommand().getType() == CommandType.END) {
                    out.println("Salvataggio dei progressi in corso...Addio!");
                    break;
                } else {
                    game.nextMove(p, out);

                    if (game.isEnd()) {
                        // --- FASE FINALE ---
                        String finalHtml = "<html><body style='font-family: Segoe UI, sans-serif; font-size: 16pt; color: #FF9900; background-color: #000000; text-align: center; padding: 50px;'>"
                                         + game.getFinalMessage().replace("\n", "<br>")
                                         + "</body></html>";
                        out.println("SHOW_OUTRO:" + finalHtml);

                        try {
                            SaveSlotRepository repository = new H2SaveSlotRepository();
                            repository.markAsCompleted(slotId);
                        } catch (GameException ex) {
                            System.err.println("ATTENZIONE: Impossibile segnare lo slot come completato. " + ex.getMessage());
                        }
                        break; // Esce dal ciclo di gioco
                    }

                    if (game.getCurrentRoom() == null) {
                        out.println("La tua avventura termina qui!");
                        break;
                    }
                }
            }
        } catch (GameException e) {
            System.err.println("Si è verificato un errore nel gioco: " + e.getMessage());
        }
    }

    /**
     * Metodo di supporto per inviare l'aggiornamento della stanza al client.
     */
    private void sendRoomUpdate(PrintWriter out, type.Room room) {
        if (room == null) return;
        
        // Controlla se siamo nello scantinato buio
        if (room.getId() == constants.GameIDs.SCANTINATO_SEGRETO_2025) {
            Kronox1914 kGame = (Kronox1914) game;
            if (!kGame.isBasementLit()) {
                out.println("ROOM_UPDATE");
                out.println("Oscurità Totale");
                out.println("Scendi la scala a pioli e ti trovi nell'oscurità totale. L'aria è fredda e umida. Senti il bisogno istintivo di trovare una fonte di luce.");
                out.println("IMAGE:images/interruttore.jpg");
                return;
            }
        }
        
        // Altrimenti, invia i dati normali della stanza
        out.println("ROOM_UPDATE");
        out.println(room.getName());
        out.println(room.getDescription());
        if (room.getImage() != null && !room.getImage().isEmpty()) {
            out.println("IMAGE:" + room.getImage());
        } else {
            out.println("IMAGE:"); // Invia comunque il segnale per l'immagine, anche se vuoto
        }
    }
}