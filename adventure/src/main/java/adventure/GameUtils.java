package adventure;

import type.AdvObject;
import java.util.List;

/**
 * La classe GameUtils fornisce metodi di utilità generici
 * per facilitare operazioni comuni all'interno della logica di gioco.
 * @author simona
 */
public class GameUtils {

    /**
     * Cerca un oggetto specifico all'interno dell'inventario tramite il suo ID.
     * Questo metodo è utile per recuperare rapidamente un {@link type.AdvObject}
     * basandosi sul suo identificativo numerico unico.
     *
     * @param inventory Una {@link java.util.List} di {@link type.AdvObject} che rappresenta l'inventario in cui cercare.
     * @param id L'ID numerico dell'oggetto {@link type.AdvObject} da trovare.
     * @return L'oggetto {@link type.AdvObject} trovato con l'ID specificato, o {@code null} se nessun oggetto corrisponde.
     */
    public static AdvObject getObjectFromInventory(List<AdvObject> inventory, int id) {
        for (AdvObject o : inventory) {
            if (o.getId() == id) {
                return o;
            }
        }
        return null;
    }

}
