package implementazione;

import adventure.GameDescription;
import adventure.GameObserver;
import exceptions.ActionException;
import java.util.function.Function;
import parser.ParserOutput;
import type.AdvObject;
import type.CommandType;

/**
 * Observer che gestisce il comando "parla".
 * Nel nuovo sistema, agisce come un dispatcher: controlla se l'oggetto
 * ha un'azione di dialogo personalizzata (onTalkAction) e la esegue.
 * Non contiene più logica di gioco specifica.
 * 
 * @author simona
 */
public class TalkObserver implements GameObserver {

    /**
     * Implementazione del metodo update dell'interfaccia {@link adventure.GameObserver}.
     * Questo metodo viene richiamato quando il giocatore esegue il comando "parla".
     * Tenta di avviare un dialogo con l'oggetto specificato, eseguendo un'azione
     * personalizzata se definita sull'oggetto.
     *
     * @param game L'istanza corrente di {@link adventure.GameDescription} che rappresenta lo stato del gioco.
     * @param p L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e il personaggio con cui parlare.
     * @return Una {@link java.lang.String} che rappresenta il messaggio di dialogo o l'esito dell'interazione.
     * @throws ActionException Se il personaggio non è specificato o non è possibile parlare con esso.
     */
    @Override
    public String update(GameDescription game, ParserOutput p) throws ActionException {
        if (p.getCommand().getType() != CommandType.TALK_TO) {
            return "";
        }

        AdvObject character = p.getObject();
        if (character == null) {
            throw new ActionException("Se non mi dici con chi vuoi parlare come ti dovrei aiutare?");
        }

        Function<GameDescription, String> action = character.getOnTalkAction();

        if (action != null) {
            return action.apply(game);
        } else {
            throw new ActionException("Non puoi parlare con " + character.getName() + ".");
        }
    }
}