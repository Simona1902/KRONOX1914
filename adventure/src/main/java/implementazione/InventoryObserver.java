package implementazione;

import adventure.GameDescription;
import adventure.GameObserver;
import parser.ParserOutput;
import type.AdvObject;
import type.CommandType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Observer che gestisce il comando INVENTORY.
 * Formatta l'elenco degli oggetti dell'inventario nel formato "INVENTORY_DATA:nome|descrizione,..."
 * richiesto dal client per visualizzare il dialogo dell'inventario.
 *
 * @author simona
 */
public class InventoryObserver implements GameObserver {

    /**
     * Se il comando è INVENTORY, costruisce e restituisce la stringa
     * con i dati dell'inventario formattati per il client.
     *
     * @param game l'istanza corrente del gioco
     * @param p l'output del parser con il comando utente
     * @return una stringa formattata per il client o una stringa vuota
     */
    @Override
    public String update(GameDescription game, ParserOutput p) {
        // Controlla se il comando è quello per l'inventario
        if (p.getCommand().getType() == CommandType.INVENTORY) {
            List<AdvObject> inventory = game.getInventory();

            // Caso di inventario vuoto: invia comunque il prefisso corretto
            if (inventory.isEmpty()) {
                return "INVENTORY_DATA:";
            } else {
                // Trasforma la lista di oggetti in una stringa "nome|descrizione,nome2|descrizione2,..."
                String items = inventory.stream()
                        .map(item -> {
                            // Pulisce la descrizione da caratteri che potrebbero rompere il parsing del client
                            String description = item.getDescription()
                                    .replace("\n", " ") // Sostituisce i ritorni a capo con spazi
                                    .replace("|", "/")   // Sostituisce i pipe (usati come nostro separatore)
                                    .replace(",", ";");  // Sostituisce le virgole (usate come nostro separatore)
                            
                            // Crea la coppia "nome|descrizione" per ogni oggetto
                            return item.getName() + "|" + description;
                        })
                        .collect(Collectors.joining(",")); // Unisce le coppie con la virgola

                // Restituisce la stringa finale con il prefisso corretto atteso dal client
                return "INVENTORY_DATA:" + items;
            }
        }
        
        // Se non è il comando inventario, non fa nulla
        return "";
    }
}