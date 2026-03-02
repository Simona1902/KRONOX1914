package implementazione;

import adventure.GameDescription;
import adventure.GameObserver;
import exceptions.ActionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import parser.ParserOutput;
import type.AdvObject;
import type.CommandType;

/**
 * L'observer UseObserver gestisce il comando "usa" ({@link type.CommandType#USE}).
 * Questo observer è responsabile di coordinare le azioni complesse che coinvolgono
 * l'uso di oggetti (dall'inventario o nella stanza) con altri oggetti o con input testuale (es. password).
 * Delega l'esecuzione delle logiche specifiche ad azioni personalizzate (lambda) definite sugli oggetti.
 *
 * @author simona
 */
public class UseObserver implements GameObserver {

    /**
     * Implementazione del metodo update dell'interfaccia {@link adventure.GameObserver}.
     * Questo metodo viene richiamato quando il giocatore esegue il comando "usa".
     * Gestisce tre scenari principali:
     * 1. Uso di un oggetto dell'inventario su un oggetto nella stanza.
     * 2. Uso di testo su un oggetto (sia esso nella stanza o nell'inventario).
     *
     * @param game L'istanza corrente di {@link adventure.GameDescription} che rappresenta lo stato del gioco.
     * @param p L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e i suoi elementi
     * (oggetto inventario, oggetto stanza, testo extra).
     * @return Una {@link java.lang.String} che rappresenta il messaggio di feedback per il client.
     * Restituisce una stringa vuota se un'azione personalizzata gestisce già il messaggio.
     * @throws ActionException Se l'azione di uso non è valida o non è supportata per gli oggetti specificati.
     */
    @Override
    public String update(GameDescription game, ParserOutput p) throws ActionException {
        if (p.getCommand().getType() != CommandType.USE) {
            return "";
        }

        AdvObject invObj = p.getInvObject(); // Oggetto dall'inventario
        AdvObject roomObj = p.getObject();   // Oggetto nella stanza
        String extra = p.getExtra();          // Testo extra (per password, ecc.)

        // CASO 1: USA [oggetto inventario] SU [oggetto stanza]
        if (invObj != null && roomObj != null) {
            Consumer<GameDescription> action = invObj.getUseWithObjectAction(roomObj.getId());
            if (action != null) {
                action.accept(game);
                return ""; // L'azione gestisce il messaggio
            }
        }

        // CASO 2: USA [testo] SU [oggetto] (in stanza o inventario)
        AdvObject targetObject = roomObj != null ? roomObj : invObj;
        if (targetObject != null && extra != null && !extra.trim().isEmpty()) {
            BiConsumer<GameDescription, String> action = targetObject.getOnUseWithTextAction();
            if (action != null) {
                action.accept(game, extra);
                return ""; // L'azione gestisce il messaggio
            }
        }
        
        // Se nessun caso ha funzionato, lancia un'eccezione
        throw new ActionException("Non puoi farlo in questo modo.");
    }
}