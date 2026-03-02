package implementazione;

import adventure.GameDescription;
import adventure.GameObserver;
import exceptions.ActionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import parser.ParserOutput;
import type.AdvObject;
import type.AdvObjectContainer;
import type.CommandType;

/**
 * Observer che gestisce il comando "osserva".
 * Nel nuovo sistema, agisce come un dispatcher: prima controlla se l'oggetto
 * ha un'azione personalizzata (onLookAction). Se non c'è, esegue la
 * logica di default per mostrare descrizione, immagine e contenuto.
 * @author simona
 */
public class LookAtObserver implements GameObserver {

    /**
     * Implementazione del metodo update dell'interfaccia {@link adventure.GameObserver}.
     * Questo metodo viene richiamato quando il giocatore esegue il comando "osserva".
     *
     * @param game L'istanza corrente di {@link adventure.GameDescription} che rappresenta lo stato del gioco.
     * @param p L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e i suoi elementi.
     * @return Una {@link java.lang.String} che rappresenta la descrizione dell'oggetto osservato,
     * i suoi contenuti (se applicabile), o la descrizione della stanza,
     * inclusi eventuali comandi speciali per la UI del client (es. SHOW_IMAGE).
     * @throws ActionException Se l'oggetto da osservare non è riconosciuto o specificato correttamente.
     */
    @Override
    public String update(GameDescription game, ParserOutput p) throws ActionException {
        if (p.getCommand().getType() != CommandType.LOOK_AT) {
            return "";
        }

        // --- GESTIONE "OSSERVA [OGGETTO]" ---
        AdvObject objectToLookAt = p.getObject() != null ? p.getObject() : p.getInvObject();
        if (objectToLookAt != null) {
            
            // 1. TENTA DI ESEGUIRE UN'AZIONE PERSONALIZZATA
            Function<GameDescription, String> action = objectToLookAt.getOnLookAction();
            if (action != null) {
                String result = action.apply(game);
                if (result != null && !result.isEmpty()) {
                    // Se l'azione restituisce un testo, usa quello e finisce qui.
                    // Perfetto per descrizioni che SOSTITUISCONO quella di default.
                    return result;
                }
            }

            // 2. SE NON C'È AZIONE SPECIALE, ESEGUI LA LOGICA DI DEFAULT
            StringBuilder output = new StringBuilder();
            
            // Mostra l'immagine, se esiste
            if (objectToLookAt.getImage() != null && !objectToLookAt.getImage().isEmpty()){
                output.append("SHOW_IMAGE\n");
                output.append(objectToLookAt.getName()).append("\n");
                output.append(objectToLookAt.getImage()).append("\n");
            }
            
            // Mostra la descrizione standard dell'oggetto
            output.append(objectToLookAt.getDescription());
            
            // Se è un contenitore aperto, mostra il suo contenuto
            if (objectToLookAt instanceof AdvObjectContainer) {
                AdvObjectContainer container = (AdvObjectContainer) objectToLookAt;
                if (container.isOpen() && !container.getList().isEmpty()) {
                    output.append("\nContiene: ").append(container.getList().stream().map(AdvObject::getName).collect(Collectors.joining(", ")));
                } else if (container.isOpen()) {
                    output.append("\nÈ vuoto.");
                }
            }
            return output.toString();
        }

        // --- GESTIONE "OSSERVA STANZA" ---
        String extra = p.getExtra();
        if (extra == null || extra.trim().equalsIgnoreCase("stanza") || extra.trim().equalsIgnoreCase("intorno") || extra.trim().isEmpty()) {
            return game.getCurrentRoom().getLook();
        }

        throw new ActionException("Non vedo l'oggetto che vuoi osservare.");
    }
}