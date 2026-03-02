package implementazione;

import adventure.GameDescription;
import adventure.GameObserver;
import constants.GameIDs;
import exceptions.ActionException;
import java.util.Map;
import parser.ParserOutput;
import type.AdvObject;
import type.CommandType;
import type.Room;

/**
 * * L'observer MoveObserver gestisce i comandi di movimento del giocatore (NORD, SUD, EST, OVEST, SU, GIU).
 * Si occupa di verificare la validità dello spostamento, gestire blocchi specifici
 * e aggiornare la stanza corrente del gioco, inviando le informazioni appropriate al client.
 *
 * @author simona
 */
public class MoveObserver implements GameObserver {

    /**
     * Converte un {@link type.CommandType} di movimento nella corrispondente stringa di direzione.
     *
     * @param type Il {@link type.CommandType} di movimento (es. NORD, SOUTH).
     * @return La {@link java.lang.String} che rappresenta la direzione (es. "nord", "sud"),
     * o {@code null} se il tipo di comando non è di movimento.
     */
    private String getDirectionFromCommand(CommandType type) {
        switch (type) {
            case NORD: return "nord";
            case SOUTH: return "sud";
            case EAST: return "est";
            case WEST: return "ovest";
            case UP: return "su";
            case DOWN: return "giu";
            default: return null;
        }
    }

    /**
     * Implementazione del metodo update dell'interfaccia {@link adventure.GameObserver}.
     * Questo metodo viene richiamato quando il giocatore esegue un comando di movimento.
     * Gestisce le logiche di movimento, inclusi i controlli sui blocchi e gli eventi narrativi
     * legati al cambio di stanza.
     *
     * @param game L'istanza corrente di {@link adventure.GameDescription} che rappresenta lo stato del gioco.
     * @param p L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e i suoi elementi.
     * @return Una {@link java.lang.String} che contiene i dati per l'aggiornamento della stanza
     * per il client ({@code ROOM_UPDATE} o {@code SHOW_NARRATIVE_SCENE}),
     * o una stringa vuota se il comando non è di movimento.
     * @throws ActionException Se il movimento non è consentito a causa di blocchi o condizioni specifiche.
     */
    @Override
    public String update(GameDescription game, ParserOutput p) throws ActionException {
        String direction = getDirectionFromCommand(p.getCommand().getType());
        if (direction == null) {
            return ""; 
        }

        Room currentRoom = game.getCurrentRoom();
        Kronox1914 kg = (Kronox1914) game;

        // --- 1. CONTROLLI DI BLOCCO PRIMA DEL MOVIMENTO ---
        if (currentRoom.getId() == GameIDs.CRIPTA_SOTTERRANEA_1914 && "su".equals(direction) && currentRoom.getExit("su") == null) {
            throw new ActionException("La statua ha richiuso il passaggio, non puoi uscire.");
        }
        if (currentRoom.getId() == GameIDs.UFFICIO_NUNZIO_1914 && !kg.isPastMidday()) {
            if ("est".equals(direction) || "ovest".equals(direction) || "sud".equals(direction)) {
                throw new ActionException("Il Nunzio alza lo sguardo, fulminandoti. 'Le mie istruzioni non erano forse chiare? Torni al suo lavoro.'");
            }
        }

        // Blocco generico per uscite inesistenti
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom == null) {
            throw new ActionException("Non puoi andare in quella direzione.");
        }

        // Blocco generico per uscite bloccate da oggetti (porte, etc.)
        Map<String, String> blockedExits = currentRoom.getBlockedExits();
        if (blockedExits != null && blockedExits.containsKey(direction)) {
            String blockingObjectId = blockedExits.get(direction);
            AdvObject blockingObject = game.findObjectByStringId(blockingObjectId);

            if (blockingObject != null && blockingObject.isCurrentlyLocked()) {
                String lockMessage = blockingObject.getLocked();
                if ("true".equalsIgnoreCase(lockMessage)) {
                    throw new ActionException("Il passaggio è bloccato da '" + blockingObject.getName() + "'.");
                } else {
                    throw new ActionException(lockMessage);
                }
            }
        }
        
        // --- 2. ESECUZIONE DEL MOVIMENTO ---
        
        game.setCurrentRoom(nextRoom);

        // --- 3. CREAZIONE DELL'OUTPUT POST-MOVIMENTO ---

        StringBuilder output = new StringBuilder("ROOM_UPDATE\n");
        Room nuovaStanza = game.getCurrentRoom();
        if (nuovaStanza.getId() == GameIDs.CRIPTA_SOTTERRANEA_1914 && currentRoom.getId() == GameIDs.GIARDINO_1914) {
            if (nuovaStanza.getUp() != null) {
                // Rimuovo l'uscita per intrappolare il giocatore
                Room giardino = kg.findRoomById(GameIDs.GIARDINO_1914);
                if (giardino != null) {
                    giardino.setDown(null);
                }
                nuovaStanza.setUp(null);

                // Invio il comando speciale per il testo a schermo intero
                StringBuilder response = new StringBuilder("SHOW_NARRATIVE_SCENE:");
                String narrativeText = "<html><body style='font-family: Segoe UI, sans-serif; font-size: 14pt; color: #FF9900; background-color: #000000; padding: 20px;'>"
                        + "Scendi i gradini nell'oscurità e ti nascondi rapidamente dietro una colonna. Senti delle voci sommesse e autoritarie.<br><br>"
                        + "'Il piano è stabilito. Il nostro agente, nome in codice Columba, è un giovane fanatico serbo di nome Gavrilo Princip. Colpirà domani. La sicurezza dell'Arciduca è una farsa, non sospetteranno nulla.' - dice uno dei sette uomini riuniti.<br><br>"
                        + "'Eccellente. Questa guerra sarà il raccolto che riporterà il potere a Roma. Che Dio benedica la nostra santa causa.' - risponde un altro.<br><br>"
                        + "Vedi i cospiratori firmare un documento e andarsene in fretta. Sei rimasto solo. Il passaggio sopra di te si è richiuso con un tonfo sordo.<br><br>Devi scoprire chi vogliono far fuori, ma in fretta."
                        + "</body></html>";
                response.append(narrativeText);
                return response.toString();
            }
        }
        if (nuovaStanza.getId() == GameIDs.SCANTINATO_SEGRETO_2025 && !kg.isBasementLit()){
            output.append("Oscurità Totale\n");
            output.append("Scendi la scala a pioli e ti trovi nell'oscurità totale. L'aria è fredda e umida. Senti il bisogno istintivo di trovare una fonte di luce.");
            output.append("\nIMAGE:images/interruttore.jpg");
            return output.toString();
        }
        
        // Output standard
        output.append(nuovaStanza.getName()).append("\n");
        output.append(nuovaStanza.getDescription());
            
        // Eventi narrativi speciali in base alla stanza
        if (nuovaStanza.getId() == GameIDs.UFFICIO_NUNZIO_1914) { 
            if (!kg.isPastMidday()) {
                output.append("<br><br>Il Nunzio è seduto alla sua scrivania e ti ha visto arrivare. Sembra voglia dirti qualcosa...");
            } else {
                output.append("<br><br>Il Nunzio non c'è, probabilmente è con i suoi ospiti. Via libera.");
            }
        }
        
        // Aggiungo il comando per l'immagine
        if (nuovaStanza.getImage() != null && !nuovaStanza.getImage().isEmpty()) {
            output.append("\nIMAGE:").append(nuovaStanza.getImage());
        }

        return output.toString();
    }
}