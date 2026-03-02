package implementazione;

import adventure.GameDescription;
import adventure.GameObserver;
import constants.GameIDs;
import static constants.GameIDs.INTERRUTTORE_LUCE;
import static constants.GameIDs.SCANTINATO_SEGRETO_2025;
import exceptions.ActionException;
import parser.ParserOutput;
import type.AdvObject;
import type.CommandType;
import type.Room;

/**
 * L'observer LightObserver gestisce l'azione di accendere la luce
 * in stanze specifiche, in particolare lo scantinato segreto.
 * Intercetta i comandi di tipo {@link type.CommandType#TURN_ON} e {@link type.CommandType#PUSH}
 * (se l'oggetto è l'interruttore della luce) e modifica lo stato della stanza di conseguenza.
 *
 * @author simona
 */
public class LightObserver implements GameObserver {

    /**
     * Implementazione del metodo update dell'interfaccia {@link adventure.GameObserver}.
     * Questo metodo viene richiamato quando il giocatore esegue un'azione.
     * Se il comando è "accendi" o "spingi interruttore" e il giocatore si trova nello scantinato segreto,
     * tenta di accendere la luce, aggiunge il terminale e rimuove l'interruttore dalla stanza.
     *
     * @param game L'istanza corrente di {@link adventure.GameDescription} che rappresenta lo stato del gioco.
     * @param p L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e i suoi elementi.
     * @return Una {@link java.lang.String} che contiene il messaggio di feedback per il client,
     * inclusi i dati per un aggiornamento ritardato della stanza, se l'azione ha successo.
     * @throws ActionException se l'azione non può essere eseguita (es. non c'è nulla da accendere,
     * la luce è già accesa, o il comando non è pertinente a questo observer).
     */
    @Override
    public String update(GameDescription game, ParserOutput p) throws ActionException {
        boolean isTurnOnCommand = p.getCommand().getType() == CommandType.TURN_ON;
        boolean isPushSwitchCommand = p.getCommand().getType() == CommandType.PUSH && p.getObject() != null && p.getObject().getId() == INTERRUTTORE_LUCE;

        if (!isTurnOnCommand && !isPushSwitchCommand) {
            return "";
        }
        Room currentRoom = game.getCurrentRoom();
        if (currentRoom.getId() != SCANTINATO_SEGRETO_2025) {
            throw new ActionException("Non c'è niente da accendere o premere qui.");
        }
        
        Kronox1914 k = (Kronox1914) game;
        if (k.isBasementLit()) {
            throw new ActionException("La luce è già accesa.");
        } else {
            k.setBasementLit(true);
            AdvObject terminale = k.findObjectById(GameIDs.TERMINALE_KRONOX);
            if (terminale != null) currentRoom.getObjects().add(terminale);
            currentRoom.getObjects().removeIf(obj -> obj.getId() == INTERRUTTORE_LUCE);
            
            Room litBasement = k.findRoomById(GameIDs.SCANTINATO_SEGRETO_2025);
            StringBuilder response = new StringBuilder();
            response.append("Azioni la levetta dell'interruttore. Dopo un paio di tremolii, una lampadina si accende, rivelando la stanza.");
            response.append("\nDELAYED_ROOM_UPDATE");
            response.append("\n").append(litBasement.getName());
            response.append("\n").append(litBasement.getDescription());
            response.append("\nIMAGE:").append(litBasement.getImage());
    
            return response.toString();
        }
    }
}