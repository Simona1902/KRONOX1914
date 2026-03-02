package type;

/**
 * /**
 * La classe ObjectState incapsula lo stato dinamico di un singolo oggetto di gioco.
 * Questo include la sua posizione corrente e le sue proprietà interattive
 * che possono cambiare durante il gioco (es. se è aperto o bloccato).
 * Viene utilizzata per il salvataggio e il caricamento dello stato del gioco.
 *
 * @author simona
 */
public class ObjectState {
    private final String objectId;
    private final String location;
    private final String openable;
    private final String pushable;
    private final String locked;
    
    /**
     * Costruisce una nuova istanza di ObjectState con i parametri specificati.
     *
     * @param objectId L'ID stringa dell'oggetto a cui si riferisce questo stato.
     * @param location La posizione attuale dell'oggetto (es. ID della stanza, "inventory", "container_ID").
     * @param openable Lo stato di "apribile" dell'oggetto.
     * @param pushable Lo stato di "spingibile" dell'oggetto.
     * @param locked Lo stato di "bloccato" dell'oggetto.
     */
    public ObjectState(String objectId, String location, String openable, String pushable, String locked){
        this.objectId = objectId;
        this.location = location;
        this.openable = openable;
        this.pushable = pushable;
        this.locked = locked;
    }
    
    /**
     * Restituisce l'ID stringa dell'oggetto a cui si riferisce questo stato.
     *
     * @return L'ID stringa dell'oggetto.
     */
    public String getObjectId(){
        return objectId;
    }
    
    /**
     * Restituisce la posizione corrente dell'oggetto.
     *
     * @return La posizione dell'oggetto (es. "stanza_id", "inventory", "container_id").
     */
    public String getLocation(){
        return location;
    }
    
    /**
     * Restituisce lo stato corrente della proprietà "openable" dell'oggetto.
     *
     * @return Lo stato "openable".
     */
    public String getOpenable() {
        return openable;
    }
    
    /**
     * Restituisce lo stato corrente della proprietà "pushable" dell'oggetto.
     *
     * @return Lo stato "pushable".
     */
    public String getPushable(){
        return pushable;
    }

    /**
     * Restituisce lo stato corrente della proprietà "locked" dell'oggetto.
     *
     * @return Lo stato "locked".
     */
    public String getLocked() {
        return locked;
    }
}
