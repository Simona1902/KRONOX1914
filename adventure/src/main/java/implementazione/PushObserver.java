package implementazione;

import adventure.GameDescription;
import adventure.GameObserver;
import exceptions.ActionException;
import java.util.function.Consumer;
import parser.ParserOutput;
import type.AdvObject;
import type.CommandType;

/**
 * L'observer PushObserver gestisce il comando "spingi" ({@link type.CommandType#PUSH}).
 * Questo observer tenta prima di eseguire un'azione personalizzata definita sull'oggetto ({@code onPushAction}).
 * Se un'azione personalizzata non esiste, gestisce la logica di fallback per oggetti genericamente "spingibili".
 *
 * @author simona
 */
public class PushObserver implements GameObserver {

    /**
     * Implementazione del metodo update dell'interfaccia {@link adventure.GameObserver}.
     * Questo metodo viene richiamato quando il giocatore esegue il comando "spingi".
     * Tenta di spingere l'oggetto specificato, utilizzando prima un'azione personalizzata
     * se definita, altrimenti la logica generica.
     *
     * @param game L'istanza corrente di {@link adventure.GameDescription} che rappresenta lo stato del gioco.
     * @param p L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e l'oggetto da spingere.
     * @return Una {@link java.lang.String} che rappresenta il messaggio di feedback per il client.
     * Restituisce una stringa vuota se l'azione personalizzata gestisce il messaggio.
     * @throws ActionException Se l'oggetto non può essere spinto o non è specificato.
     */
    @Override
    public String update(GameDescription game, ParserOutput p) throws ActionException {
        if (p.getCommand().getType() != CommandType.PUSH) {
            return "";
        }
        AdvObject objectToPush = p.getObject();
        if (objectToPush == null) {
            throw new ActionException("Se non mi dici cosa vuoi spingere come ti dovrei aiutare?.");
        }

        // Tenta di eseguire un'azione personalizzata definita in ActionBinder
        Consumer<GameDescription> action = objectToPush.getOnPushAction();
        if (action != null) {
            action.accept(game);
            return ""; // Il messaggio viene gestito dall'azione stessa
        }

        // Logica di fallback per oggetti genericamente "spingibili"
        String pushableProperty = objectToPush.getPushable();
        if ("true".equalsIgnoreCase(pushableProperty)) {
            throw new ActionException("Hai spinto " + objectToPush.getName() + ", ma non succede nulla di speciale.");
        } else if (pushableProperty != null) {
            // Messaggio personalizzato dal JSON per oggetti spingibili ma con un motivo per non muoversi
            throw new ActionException(pushableProperty);
        } else {
            // L'oggetto non è affatto spingibile
            throw new ActionException("Non puoi spingere '" + objectToPush.getName() + "'.");
        }
    }
}