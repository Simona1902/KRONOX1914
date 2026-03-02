package implementazione;

import adventure.GameDescription;
import parser.ParserOutput;
import type.CommandType;
import adventure.GameObserver;
import exceptions.ActionException;
import type.AdvObject;

/**
 * L'observer PickUpObserver gestisce il comando "raccogli" ({@link type.CommandType#PICK_UP}).
 * Questo observer verifica se un oggetto è raccoglibile e, in caso affermativo,
 * lo sposta dalla stanza corrente all'inventario del giocatore.
 *
 * @author simona
 */
public class PickUpObserver implements GameObserver {       

    /**
     * Implementazione del metodo update dell'interfaccia {@link adventure.GameObserver}.
     * Questo metodo viene richiamato quando il giocatore esegue il comando "raccogli".
     * Verifica la proprietà {@code pickupable} dell'oggetto per determinare se può essere raccolto.
     *
     * @param description L'istanza corrente di {@link adventure.GameDescription} che rappresenta lo stato del gioco.
     * @param p L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e l'oggetto da raccogliere.
     * @return Una {@link java.lang.String} che rappresenta il messaggio di feedback per il client
     * (es. "Hai raccolto: [nome oggetto].").
     * @throws ActionException Se l'oggetto non è visibile, non è raccoglibile,
     * o se la sua proprietà {@code pickupable} contiene un messaggio di errore specifico.
     */
    @Override
    public String update(GameDescription description, ParserOutput p) throws ActionException {
        if (p.getCommand().getType() != CommandType.PICK_UP) {
            return "";
        }
        AdvObject objectToPickUp = p.getObject();
        
        if (objectToPickUp == null) {
            throw new ActionException("Non vedo questo oggetto qui.");
        }
        
        String pickupableProperty = objectToPickUp.getPickupable();
        if ("true".equalsIgnoreCase(pickupableProperty)){
            description.getCurrentRoom().getObjects().remove(objectToPickUp);
            description.getInventory().add(objectToPickUp);
            return "Hai raccolto: " + objectToPickUp.getName() + ".";
        } else if (pickupableProperty != null){
            throw new ActionException(pickupableProperty);
        } else{
            throw new ActionException("Non puoi raccogliere '" + objectToPickUp.getName() + "'.");
        }
    }
}