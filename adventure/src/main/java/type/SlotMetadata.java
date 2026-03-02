package type;

import java.sql.Timestamp;

/**
 * Contiene i metadati di un singolo slot di salvataggio,
 * usati per visualizzare il menu di scelta.
 * @author simona
 */
public class SlotMetadata {
    private final int slotId;
    private final String slotName;
    private final Timestamp lastSaved;
    private boolean completed;

    /**
     * Costruisce una nuova istanza di SlotMetadata.
     *
     * @param slotId L'ID numerico dello slot.
     * @param slotName Il nome dello slot.
     * @param lastSaved Il {@link java.sql.Timestamp} dell'ultima data di salvataggio.
     * @param completed {@code true} se la partita è completata, {@code false} altrimenti.
     */
    public SlotMetadata(int slotId, String slotName, Timestamp lastSaved, boolean completed) {
        this.slotId = slotId;
        this.slotName = slotName;
        this.lastSaved = lastSaved;
        this.completed = completed;
    }

    /**
     * Restituisce l'ID dello slot di salvataggio.
     *
     * @return L'ID dello slot.
     */
    public int getSlotId() { return slotId; }

    /**
     * Restituisce il nome dello slot di salvataggio.
     *
     * @return Il nome dello slot.
     */
    public String getSlotName() { return slotName; }

    /**
     * Restituisce il timestamp dell'ultimo salvataggio.
     *
     * @return Il {@link java.sql.Timestamp} dell'ultimo salvataggio, o {@code null} se mai salvato.
     */
    public Timestamp getLastSaved() { return lastSaved; }

    /**
     * Verifica se la partita in questo slot è stata completata.
     *
     * @return {@code true} se la partita è completata, {@code false} altrimenti.
     */
    public boolean isCompleted(){ return completed; }

    /**
     * Imposta lo stato di completamento della partita per questo slot.
     *
     * @param completed {@code true} se la partita deve essere marcata come completata, {@code false} altrimenti.
     */
    public void setCompleted(boolean completed){ this.completed = completed;}
}