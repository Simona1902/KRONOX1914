package type;

import java.util.List;
import java.util.Set;

/**
 * La classe ObjectData è un modello di dati (POJO) utilizzato per deserializzare
 * le informazioni sugli oggetti di gioco da file di configurazione (es. JSON).
 * Contiene i dati statici (blueprint) di un oggetto, come il suo ID, nome, descrizione,
 * proprietà interattive e gli oggetti che può contenere inizialmente.
 * 
 * @author simona
 */
public class ObjectData {

    /**
     * L'ID numerico univoco dell'oggetto.
     */
    public int id;

    /**
     * L'epoca a cui l'oggetto appartiene.
     */
    public int epoch;

    /**
     * Il nome dell'oggetto.
     */
    public String name;

    /**
     * La descrizione testuale dell'oggetto.
     */
    public String description;

    /**
     * Il percorso dell'immagine associata all'oggetto (opzionale).
     */
    public String image;

    /**
     * Un set di alias per l'oggetto, utili per il riconoscimento del parser.
     */
    public Set<String> aliases;

    /**
     * Indica se l'oggetto è raccoglibile.
     * Può essere "true" o un messaggio di errore.
     *
     */
    public String pickupable;

    /**
     * Indica se l'oggetto è apribile.
     * Può essere "true" o un messaggio di errore.
     *
     */
    public String openable;

    /**
     * Indica se l'oggetto è spingibile.
     * Può essere "true" o un messaggio di errore.
     *
     */
    public String pushable;

    /**
     * Indica se l'oggetto è utilizzabile.
     * Può essere "true" o un messaggio di errore.
     *
     */
    public String useable;

    /**
     * Indica se l'oggetto è bloccato.
     * Può essere "false" (sbloccato), "true" (bloccato genericamente) o un messaggio di blocco personalizzato.
     *
     */
    public String locked;

    /**
     * Una lista di ID stringa degli oggetti che questo contenitore contiene inizialmente.
     * Se {@code null} o vuota, l'oggetto non è un contenitore.
     *
     */
    public List<String> contains;
}
