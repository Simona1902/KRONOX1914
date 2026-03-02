package database;

import exceptions.GameException;
import java.util.List;
import type.SlotMetadata;

/**
 * Interfaccia per un repository che gestisce i metadati degli slot di salvataggio.
 * @author simona
 */
public interface SaveSlotRepository {

    /**
     * Carica i metadati di tutti gli slot disponibili.
     * @return una lista di oggetti SlotMetadata.
     * @throws GameException se si verifica un errore.
     */
    List<SlotMetadata> loadAllSlotsMetadata() throws GameException;

    /**
     * Aggiorna il nome di uno slot specifico.
     * @param slotId L'ID dello slot da aggiornare.
     * @param newName Il nuovo nome per lo slot.
     * @throws GameException se si verifica un errore.
     */
    void updateSlotName(int slotId, String newName) throws GameException;

    /**
     * Marca uno slot di salvataggio come completato.
     * @param slotId L'ID dello slot da marcare.
     * @throws GameException se si verifica un errore.
     */
    void markAsCompleted(int slotId) throws GameException;
    
    /**
     * Resetta uno slot di salvataggio ai suoi valori iniziali,
     * eliminando i dati di gioco associati e marcandolo come non completato.
     *
     * @param slotId L'ID dello slot da resettare.
     * @throws GameException Se si verifica un errore durante il reset dello slot.
     */
    void resetSlot(int slotId) throws GameException;
}
