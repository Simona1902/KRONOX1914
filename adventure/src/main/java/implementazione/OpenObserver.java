package implementazione;

import adventure.GameDescription;
import adventure.GameObserver;
import exceptions.ActionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import parser.ParserOutput;
import type.AdvObject;
import type.AdvObjectContainer;
import type.CommandType;

/**
 * L'observer OpenObserver gestisce il comando "apri" ({@link type.CommandType#OPEN}).
 * Questo observer agisce come un dispatcher: tenta prima di eseguire un'azione personalizzata
 * definita sull'oggetto ({@code onOpenAction}). Se un'azione personalizzata non esiste,
 * o l'oggetto è un contenitore senza un'azione specifica, esegue la logica di default
 * per aprire contenitori generici.
 *
 * @author simona
 */
public class OpenObserver implements GameObserver {

    /**
     * Gestisce l'apertura di un {@link type.AdvObjectContainer} generico.
     * Se il contenitore è già aperto, lancia un'eccezione. Altrimenti, lo apre,
     * aggiorna la sua descrizione e sposta il suo contenuto nella stanza corrente del giocatore.
     *
     * @param container L'{@link type.AdvObjectContainer} da aprire.
     * @param game L'istanza corrente di {@link adventure.GameDescription}.
     * @param customMessage Il messaggio di base da restituire dopo l'apertura (es. "Hai aperto X").
     * @return Una {@link java.lang.String} che descrive l'esito dell'apertura e gli eventuali oggetti trovati.
     * @throws ActionException Se il contenitore è già aperto.
     */
    private String openGenericContainer(AdvObjectContainer container, GameDescription game, String customMessage) throws ActionException {
        if (container.isOpen()) {
            throw new ActionException("Questo è già aperto.");
        }
        container.setOpen(true);
        container.setDescription(container.getDescription().replace("chiuso", "aperto"));
        
        if (container.getList().isEmpty()) {
            return customMessage + ", ma dentro è vuoto.";
        }
        
        String foundObjects = container.getList().stream().map(AdvObject::getName).collect(Collectors.joining(" e "));
        game.getCurrentRoom().getObjects().addAll(container.getList());
        container.getList().clear();
        
        return customMessage + " e dentro trovi: " + foundObjects + ".";
    }

    /**
     * Implementazione del metodo update dell'interfaccia {@link adventure.GameObserver}.
     * Questo metodo viene richiamato quando il giocatore esegue il comando "apri".
     * Tenta di aprire l'oggetto specificato, utilizzando prima un'azione personalizzata
     * se definita, altrimenti la logica generica per i contenitori.
     *
     * @param game L'istanza corrente di {@link adventure.GameDescription} che rappresenta lo stato del gioco.
     * @param p L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e i suoi elementi.
     * @return Una {@link java.lang.String} che rappresenta il messaggio di feedback per il client.
     * @throws ActionException Se l'oggetto non può essere aperto o non è specificato.
     */
    @Override
    public String update(GameDescription game, ParserOutput p) throws ActionException {
        if (p.getCommand().getType() != CommandType.OPEN) {
            return "";
        }
        AdvObject objectToOpen = p.getObject() != null ? p.getObject() : p.getInvObject();
        if (objectToOpen == null) {
            throw new ActionException("Se non mi dici cosa vuoi aprire come ti dovrei aiutare?");
        }

        // Tenta di eseguire un'azione personalizzata
        Consumer<GameDescription> action = objectToOpen.getOnOpenAction();
        if (action != null) {
            action.accept(game);
            return "";
        }

        // Logica di fallback per contenitori generici senza azione custom
        if (objectToOpen instanceof AdvObjectContainer) {
            return openGenericContainer((AdvObjectContainer) objectToOpen, game, "Hai aperto " + objectToOpen.getName());
        }
        
        // Messaggio di fallback dal JSON
        if (objectToOpen.getOpenable() != null && !"true".equalsIgnoreCase(objectToOpen.getOpenable())) {
            throw new ActionException(objectToOpen.getOpenable());
        }

        throw new ActionException("Non puoi aprire '" + objectToOpen.getName() + "'.");
    }
}