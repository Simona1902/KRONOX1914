package type;

import java.util.List;
import java.util.Map;

/**
 * La classe RoomData è un modello di dati (POJO) utilizzato per deserializzare
 * le informazioni sulle stanze di gioco da file di configurazione (es. JSON).
 * Contiene i dati statici (blueprint) di una stanza, come il suo ID, nome, descrizioni,
 * collegamenti ad altre stanze e gli oggetti che contiene inizialmente.
 * 
 * @author simona
 */
public class RoomData {

    /**
     * L'ID numerico univoco della stanza.
     */
    public int id;

    /** 
     * L'epoca a cui la stanza appartiene (es. "presente", "passato").
     */
    public String epoch;

    /**
     * Il nome della stanza.
     */
    public String name;

    /**
     * La descrizione breve della stanza.
     */
    public String description;

    /**
     * La descrizione estesa della stanza (usata per il comando "osserva stanza").
     *
     */
    public String look;

    /**
     * Il percorso dell'immagine di sfondo associata alla stanza (opzionale).
     */
    public String image;

    /**
     * Una mappa che definisce le uscite della stanza.
     * La chiave è la direzione (es. "nord", "sud") e il valore è l'ID stringa della stanza di destinazione.
     *
     */
    public Map<String, String> exits;

    /**
     * Una lista di ID stringa degli oggetti che la stanza contiene inizialmente.
     */
    public List<String> objects;

    /**
     * Una mappa delle uscite bloccate della stanza.
     * La chiave è la direzione (es. "nord") e il valore è l'ID stringa dell'oggetto che blocca il passaggio.
     *
     */
    public Map<String, String> blockedExits;
}
