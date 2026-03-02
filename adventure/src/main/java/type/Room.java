package type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * La classe Room rappresenta una singola stanza nel mondo di gioco.
 * Ogni stanza ha un ID unico, un nome, una descrizione, un'epoca di appartenenza,
 * e collegamenti ad altre stanze (uscite) in diverse direzioni.
 * Può contenere oggetti interattivi.
 *
 * @author simona
 */
public class Room {
    private final int id;
    private final String stringId;
    private type.GameEpoch epoch;
    private final String name;
    private final String description;
    private String look;
    private String image;
    private Room south = null;
    private Room north = null;
    private Room east = null;
    private Room west = null;
    private Room up;
    private Room down;
    private final List<AdvObject> objects = new ArrayList<>();
    private Map<String, String> blockedExits;

    /**
     * Costruisce una nuova istanza di Room.
     *
     * @param id L'ID numerico unico della stanza.
     * @param stringId L'ID testuale univoco della stanza (usato per riferimento in mappe).
     * @param name Il nome della stanza.
     * @param description La descrizione breve della stanza.
     */
    public Room(int id, String stringId, String name, String description) {
        this.id = id;
        this.stringId = stringId;
        this.name = name;
        this.description = description;
    }

    /**
     * Restituisce l'ID numerico unico della stanza.
     *
     * @return L'ID della stanza.
     */
    public int getId() { return id; }

    /**
     * Restituisce l'ID testuale unico della stanza.
     *
     * @return L'ID testuale della stanza.
     */
    public String getStringId() { return stringId; }

    /**
     * Restituisce l'epoca temporale a cui appartiene la stanza.
     *
     * @return L'epoca.
     */
    public type.GameEpoch getEpoch() { return epoch; }

    /**
     * Imposta l'epoca temporale a cui appartiene la stanza.
     *
     * @param epoch L'epoca.
     */
    public void setEpoch(type.GameEpoch epoch) { this.epoch = epoch; }

    /**
     * Restituisce il nome della stanza.
     *
     * @return Il nome della stanza.
     */
    public String getName() { return name; }

    /**
     * Restituisce la descrizione breve della stanza.
     *
     * @return La descrizione della stanza.
     */
    public String getDescription() { return description; }

    /**
     * Restituisce la descrizione estesa della stanza (usata per il comando "osserva stanza").
     *
     * @return La descrizione estesa della stanza.
     */
    public String getLook() { return look; }

    /**
     * Imposta la descrizione estesa della stanza.
     *
     * @param look La nuova descrizione estesa della stanza.
     */
    public void setLook(String look) { this.look = look; }

    /**
     * Restituisce la stanza collegata all'uscita sud.
     *
     * @return La {@link type.Room} a sud, o {@code null} se non c'è uscita.
     */
    public Room getSouth() { return south; }

    /**
     * Imposta la stanza collegata all'uscita sud.
     *
     * @param south La {@link type.Room} a sud.
     */
    public void setSouth(Room south) { this.south = south; }

    /**
     * Restituisce la stanza collegata all'uscita nord.
     *
     * @return La {@link type.Room} a nord, o {@code null} se non c'è uscita.
     */
    public Room getNorth() { return north; }

    /**
     * Imposta la stanza collegata all'uscita nord.
     *
     * @param north La {@link type.Room} a nord.
     */
    public void setNorth(Room north) { this.north = north; }

    /**
     * Restituisce la stanza collegata all'uscita est.
     *
     * @return La {@link type.Room} a est, o {@code null} se non c'è uscita.
     */
    public Room getEast() { return east; }

    /**
     * Imposta la stanza collegata all'uscita est.
     *
     * @param east La {@link type.Room} a est.
     */
    public void setEast(Room east) { this.east = east; }

    /**
     * Restituisce la stanza collegata all'uscita ovest.
     *
     * @return La {@link type.Room} a ovest, o {@code null} se non c'è uscita.
     */
    public Room getWest() { return west; }

    /**
     * Imposta la stanza collegata all'uscita ovest.
     *
     * @param west La {@link type.Room} a ovest.
     */
    public void setWest(Room west) { this.west = west; }

    /**
     * Restituisce la lista degli oggetti presenti in questa stanza.
     *
     * @return Una {@link java.util.List} di {@link type.AdvObject} presenti nella stanza.
     */
    public List<AdvObject> getObjects() { return objects; }

    /**
     * Restituisce il percorso dell'immagine di sfondo della stanza.
     *
     * @return Il percorso {@link java.lang.String} dell'immagine.
     */
    public String getImage() {return image;}

    /**
     * Imposta il percorso dell'immagine di sfondo della stanza.
     *
     * @param image Il percorso {@link java.lang.String} dell'immagine.
     */
    public void setImage(String image) {this.image = image;}

    /**
     * Calcola il codice hash per questa stanza, basato sul suo ID.
     *
     * @return Il codice hash della stanza.
     */
    @Override
    public int hashCode() { return this.id; }

    /**
     * Compara questa stanza con l'oggetto specificato per l'uguaglianza.
     * Due stanze sono considerate uguali se hanno lo stesso ID.
     *
     * @param obj L'oggetto da confrontare.
     * @return {@code true} se gli oggetti sono uguali, {@code false} altrimenti.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return this.id == ((Room) obj).id;
    }

    /**
     * Restituisce la stanza collegata all'uscita superiore (es. salire una scala).
     *
     * @return La {@link type.Room} sopra, o {@code null} se non c'è uscita.
     */
    public Room getUp() {
        return up;
    }

    /**
     * Imposta la stanza collegata all'uscita superiore.
     *
     * @param up La {@link type.Room} sopra.
     */
    public void setUp(Room up) {
        this.up = up;
    }

    /**
     * Restituisce la stanza collegata all'uscita inferiore (es. scendere una botola).
     *
     * @return La {@link type.Room} sotto, o {@code null} se non c'è uscita.
     */
    public Room getDown() {
        return down;
    }

    /**
     * Imposta la stanza collegata all'uscita inferiore.
     *
     * @param down La {@link type.Room} sotto.
     */
    public void setDown(Room down) {
        this.down = down;
    }

    /**
     * Restituisce la mappa delle uscite bloccate da oggetti.
     * La chiave della mappa è la direzione (es. "nord", "sud") e il valore è l'ID stringa dell'oggetto che blocca.
     *
     * @return Una {@link java.util.Map} di {@link java.lang.String} che rappresenta le uscite bloccate.
     */
    public Map<String, String> getBlockedExits() {
        return blockedExits;
    }

    /**
     * Imposta la mappa delle uscite bloccate da oggetti.
     *
     * @param blockedExits La {@link java.util.Map} di {@link java.lang.String} da impostare come uscite bloccate.
     */
    public void setBlockedExits(Map<String, String> blockedExits) {
        this.blockedExits = blockedExits;
    }

    /**
     * Metodo di supporto per ottenere la stanza collegata a una data direzione.
     *
     * @param direction La {@link java.lang.String} che indica la direzione del movimento (es. "nord", "sud", "su", "giu").
     * @return La {@link type.Room} nella direzione specificata, o {@code null} se non esiste un'uscita in quella direzione.
     */
    public Room getExit(String direction) {
        if (direction == null) return null;
        switch (direction.toLowerCase()) {
            case "nord": return this.getNorth();
            case "sud": return this.getSouth();
            case "est": return this.getEast();
            case "ovest": return this.getWest();
            case "su": return this.getUp();
            case "giu": return this.getDown();
            default: return null;
        }
    }
}