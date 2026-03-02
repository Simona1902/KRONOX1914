package type;

import adventure.GameDescription;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * La classe AdvObject rappresenta un oggetto generico interattivo nel gioco di avventura.
 * Contiene proprietà come ID, nome, descrizione, alias e flag di stato (aperto, raccoglibile, ecc.).
 * Supporta anche l'associazione di azioni personalizzate (tramite lambda)
 * per diversi tipi di interazioni (apri, spingi, osserva, parla, usa con oggetto, usa con testo).
 *
 * @author simona
 */
public class AdvObject {
    private final int id;
    private String name;
    private String description;
    private String image;
    private Set<String> alias = new HashSet<>();
    private boolean open = false;
    private String pickupable;
    private String openable;
    private String pushable;
    private String useable;
    private String locked = "false";
    private Consumer<GameDescription> onOpenAction = null;
    private Consumer<GameDescription> onPushAction = null;
    private Function<GameDescription, String> onLookAction = null;
    private Function<GameDescription, String> onTalkAction = null;
    private BiConsumer<GameDescription, String> onUseWithTextAction = null;
    private final Map<Integer, Consumer<GameDescription>> useWithObjectActions = new HashMap<>();
    
    /**
     * Costruisce una nuova istanza di AdvObject con ID, nome e descrizione.
     *
     * @param id L'ID numerico unico dell'oggetto.
     * @param name Il nome dell'oggetto.
     * @param description La descrizione dell'oggetto.
     */
    public AdvObject(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    /**
     * Costruisce una nuova istanza di AdvObject con ID, nome, descrizione e alias.
     *
     * @param id L'ID numerico unico dell'oggetto.
     * @param name Il nome dell'oggetto.
     * @param description La descrizione dell'oggetto.
     * @param alias Un {@link java.util.Set} di {@link java.lang.String} contenente gli alias per l'oggetto.
     */
    public AdvObject(int id, String name, String description, Set<String> alias){
        this.id = id;
        this.name = name;
        this.description = description;
        this.alias = alias;
    }

    /**
     * Restituisce la proprietà {@code pickupable} dell'oggetto.
     * Questa stringa può essere "true" o un messaggio di errore se l'oggetto non è raccoglibile.
     *
     * @return La {@link java.lang.String} che indica se l'oggetto è raccoglibile.
     */
    public String getPickupable() { return pickupable; }

    /**
     * Imposta la proprietà {@code pickupable} dell'oggetto.
     *
     * @param pickupable La {@link java.lang.String} per la proprietà pickupable.
     */
    public void setPickupable(String pickupable) { this.pickupable = pickupable; }

    /**
     * Restituisce la proprietà {@code openable} dell'oggetto.
     * Questa stringa può essere "true" o un messaggio di errore se l'oggetto non è apribile.
     *
     * @return La {@link java.lang.String} che indica se l'oggetto è apribile.
     */
    public String getOpenable() { return openable; }

    /**
     * Imposta la proprietà {@code openable} dell'oggetto.
     *
     * @param openable La {@link java.lang.String} per la proprietà openable.
     */
    public void setOpenable(String openable) { this.openable = openable; }

    /**
     * Restituisce la proprietà {@code pushable} dell'oggetto.
     * Questa stringa può essere "true" o un messaggio di errore se l'oggetto non è spingibile.
     *
     * @return La {@link java.lang.String} che indica se l'oggetto è spingibile.
     */
    public String getPushable() { return pushable; }

    /**
     * Imposta la proprietà {@code pushable} dell'oggetto.
     *
     * @param pushable La {@link java.lang.String} per la proprietà pushable.
     */
    public void setPushable(String pushable) { this.pushable = pushable; }

    /**
     * Restituisce la proprietà {@code useable} dell'oggetto.
     * Questa stringa può essere "true" o un messaggio di errore se l'oggetto non è utilizzabile.
     *
     * @return La {@link java.lang.String} che indica se l'oggetto è utilizzabile.
     */
    public String getUseable() { return useable; }

    /**
     * Imposta la proprietà {@code useable} dell'oggetto.
     *
     * @param useable La {@link java.lang.String} per la proprietà useable.
     */
    public void setUseable(String useable) { this.useable = useable; }

    /**
     * Verifica se l'oggetto è attualmente aperto.
     *
     * @return {@code true} se l'oggetto è aperto, {@code false} altrimenti.
     */
    public boolean isOpen() { return open; }

    /**
     * Imposta lo stato di apertura dell'oggetto.
     *
     * @param open {@code true} per aprire l'oggetto, {@code false} per chiuderlo.
     */
    public void setOpen(boolean open) { this.open = open; }

    /**
     * Restituisce l'ID numerico unico dell'oggetto.
     *
     * @return L'ID dell'oggetto.
     */
    public int getId() { return id; }

    /**
     * Restituisce il nome dell'oggetto.
     *
     * @return Il nome dell'oggetto.
     */
    public String getName() { return name; }

    /**
     * Imposta il nome dell'oggetto.
     *
     * @param name Il nuovo nome dell'oggetto.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Restituisce la descrizione dell'oggetto.
     *
     * @return La descrizione dell'oggetto.
     */
    public String getDescription() { return description; }

    /**
     * Imposta la descrizione dell'oggetto.
     *
     * @param description La nuova descrizione dell'oggetto.
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Restituisce il set di alias per l'oggetto.
     *
     * @return Un {@link java.util.Set} di {@link java.lang.String} contenente gli alias.
     */
    public Set<String> getAlias() { return alias; }

    /**
     * Imposta il set di alias per l'oggetto.
     *
     * @param alias Il {@link java.util.Set} di {@link java.lang.String} da impostare come alias.
     */
    public void setAlias(Set<String> alias){ this.alias = alias;}

    /**
     * Imposta gli alias per l'oggetto da un array di stringhe.
     *
     * @param alias Un array di {@link java.lang.String} da convertire in un set di alias.
     */
    public void setAlias(String[] alias) { this.alias = new HashSet<>(Arrays.asList(alias)); }

    /**
     * Restituisce il percorso dell'immagine associata all'oggetto.
     *
     * @return Il percorso {@link java.lang.String} dell'immagine.
     */
    public String getImage() { return image;}

    /**
     * Imposta il percorso dell'immagine associata all'oggetto.
     *
     * @param image Il percorso {@link java.lang.String} dell'immagine.
     */
    public void setImage(String image) {this.image = image;}

    /**
     * Restituisce lo stato di blocco dell'oggetto.
     * Questa stringa può essere "false" se sbloccato, "true" se bloccato
     * o un messaggio personalizzato se bloccato in un modo specifico.
     *
     * @return La {@link java.lang.String} che indica lo stato di blocco.
     */
    public String getLocked() {return this.locked;}

    /**
     * Imposta lo stato di blocco dell'oggetto.
     *
     * @param locked La {@link java.lang.String} per lo stato di blocco.
     */
    public void setLocked(String locked) {this.locked = locked;}
    
    /**
     * Calcola il codice hash per questo oggetto, basato sul suo ID.
     *
     * @return Il codice hash dell'oggetto.
     */
    @Override
    public int hashCode() { return id; }

    /**
     * Compara questo oggetto con l'oggetto specificato per l'uguaglianza.
     * Due AdvObject sono considerati uguali se hanno lo stesso ID.
     *
     * @param obj L'oggetto da confrontare.
     * @return {@code true} se gli oggetti sono uguali, {@code false} altrimenti.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return this.id == ((AdvObject) obj).id;
    }

    /**
     * Verifica se l'oggetto è attualmente bloccato.
     * Un oggetto è considerato bloccato se la sua proprietà {@code locked} non è null e non è "false" (case-insensitive).
     *
     * @return {@code true} se l'oggetto è bloccato, {@code false} altrimenti.
     */
    public boolean isCurrentlyLocked() {
        return this.locked != null && !this.locked.equalsIgnoreCase("false");
    }
    
    /**
     * Imposta l'azione personalizzata da eseguire quando l'oggetto viene aperto.
     *
     * @param action La {@link java.util.function.Consumer} che definisce l'azione,
     * la quale accetta un'istanza di {@link adventure.GameDescription}.
     */
    public void setOnOpenAction(Consumer<GameDescription> action) { this.onOpenAction = action; }

    /**
     * Imposta l'azione personalizzata da eseguire quando l'oggetto viene spinto.
     *
     * @param action La {@link java.util.function.Consumer} che definisce l'azione,
     * la quale accetta un'istanza di {@link adventure.GameDescription}.
     */
    public void setOnPushAction(Consumer<GameDescription> action) { this.onPushAction = action; }

    /**
     * Imposta l'azione personalizzata da eseguire quando l'oggetto viene osservato.
     * L'azione può restituire un messaggio {@link java.lang.String}.
     *
     * @param action La {@link java.util.function.Function} che definisce l'azione,
     * la quale accetta un'istanza di {@link adventure.GameDescription} e restituisce una {@link java.lang.String}.
     */
    public void setOnLookAction(Function<GameDescription, String> action) { this.onLookAction = action; }

    /**
     * Imposta l'azione personalizzata da eseguire quando si "parla" con l'oggetto.
     * L'azione può restituire un messaggio {@link java.lang.String}.
     *
     * @param action La {@link java.util.function.Function} che definisce l'azione,
     * la quale accetta un'istanza di {@link adventure.GameDescription} e restituisce una {@link java.lang.String}.
     */
    public void setOnTalkAction(Function<GameDescription, String> action) { this.onTalkAction = action; }

    /**
     * Imposta l'azione personalizzata da eseguire quando l'oggetto viene usato con un input testuale.
     *
     * @param action La {@link java.util.function.BiConsumer} che definisce l'azione,
     * la quale accetta un'istanza di {@link adventure.GameDescription} e una {@link java.lang.String} (il testo extra).
     */
    public void setOnUseWithTextAction(BiConsumer<GameDescription, String> action) { this.onUseWithTextAction = action; }

    /**
     * Definisce un'azione personalizzata per l'uso di questo oggetto con un altro oggetto specifico.
     * Le azioni vengono memorizzate in una mappa associata all'ID dell'oggetto target.
     *
     * @param targetObjectId L'ID numerico dell'oggetto con cui questo oggetto viene usato.
     * @param action La {@link java.util.function.Consumer} che definisce l'azione,
     * la quale accetta un'istanza di {@link adventure.GameDescription}.
     */
    public void onUseWith(int targetObjectId, Consumer<GameDescription> action) { this.useWithObjectActions.put(targetObjectId, action); }

    /**
     * Restituisce l'azione personalizzata da eseguire all'apertura dell'oggetto.
     *
     * @return La {@link java.util.function.Consumer} per l'azione di apertura, o {@code null} se non definita.
     */
    public Consumer<GameDescription> getOnOpenAction() { return onOpenAction; }

    /**
     * Restituisce l'azione personalizzata da eseguire alla spinta dell'oggetto.
     *
     * @return La {@link java.util.function.Consumer} per l'azione di spinta, o {@code null} se non definita.
     */
    public Consumer<GameDescription> getOnPushAction() { return onPushAction; }

    /**
     * Restituisce l'azione personalizzata da eseguire all'osservazione dell'oggetto.
     *
     * @return La {@link java.util.function.Function} per l'azione di osservazione, o {@code null} se non definita.
     */
    public Function<GameDescription, String> getOnLookAction() { return onLookAction; }

    /**
     * Restituisce l'azione personalizzata da eseguire quando si "parla" con l'oggetto.
     *
     * @return La {@link java.util.function.Function} per l'azione di dialogo, o {@code null} se non definita.
     */
    public Function<GameDescription, String> getOnTalkAction() { return onTalkAction; }

    /**
     * Restituisce l'azione personalizzata da eseguire quando l'oggetto viene usato con un input testuale.
     *
     * @return La {@link java.util.function.BiConsumer} per l'azione con testo, o {@code null} se non definita.
     */
    public BiConsumer<GameDescription, String> getOnUseWithTextAction() { return onUseWithTextAction; }

    /**
     * Restituisce l'azione personalizzata da eseguire quando questo oggetto viene usato con un altro oggetto specifico.
     *
     * @param targetObjectId L'ID numerico dell'oggetto con cui questo oggetto viene usato.
     * @return La {@link java.util.function.Consumer} per l'azione specifica, o {@code null} se non definita per quell'ID target.
     */

    public Consumer<GameDescription> getUseWithObjectAction(int targetObjectId) { return useWithObjectActions.get(targetObjectId); }
}