package database;
import exceptions.GameException;
import java.util.Map;
import type.AdvObject;
import type.ObjectData;
import type.ObjectState;

/**
 * L'interfaccia ObjectStateRepository definisce il contratto per la gestione
 * della persistenza (salvataggio e caricamento) dello stato degli oggetti di gioco.
 * @author simona
 */
public interface ObjectStateRepository {
    
    /**
     * Carica tutti gli stati degli oggetti associati a uno specifico slot di salvataggio.
     *
     * @param slotId L'ID dello slot di salvataggio da cui caricare gli stati degli oggetti.
     * @return Una {@link java.util.Map} dove la chiave è l'ID stringa dell'oggetto
     * e il valore è l'oggetto {@link type.ObjectState} che rappresenta il suo stato.
     * @throws GameException Se si verifica un errore durante il caricamento degli stati degli oggetti.
     */
    Map<String, ObjectState> loadAllObjectStates(int slotId) throws GameException;
    
    /**
     * Salva o aggiorna tutti gli stati degli oggetti correnti nel repository per uno specifico slot.
     * Questo metodo dovrebbe gestire sia l'inserimento di nuovi stati che l'aggiornamento di quelli esistenti.
     *
     * @param slotId L'ID dello slot di salvataggio a cui associare gli stati degli oggetti.
     * @param liveObjects Una {@link java.util.Map} di oggetti {@link type.AdvObject} correnti,
     * dove la chiave è l'ID stringa dell'oggetto.
     * @param objectLocations Una {@link java.util.Map} che mappa l'ID stringa di ogni oggetto
     * alla sua posizione attuale (stanza, inventario, ecc.).
     * @throws GameException Se si verifica un errore durante il salvataggio degli stati degli oggetti.
     */
    void saveAllObjectStates(int slotId, Map<String, AdvObject> liveObjects, Map<String, String> objectLocations) throws GameException;
    
    /**
     * Inizializza lo stato degli oggetti nel repository per una nuova partita in uno specifico slot.
     * Questo metodo dovrebbe pulire qualsiasi stato precedente per lo slot e inserire gli stati iniziali
     * basati sui blueprint e le posizioni iniziali.
     *
     * @param slotId L'ID dello slot di salvataggio per cui inizializzare gli stati.
     * @param objectBlueprints Una {@link java.util.Map} dove la chiave è l'ID stringa dell'oggetto
     * e il valore è l'oggetto {@link type.ObjectData} che contiene i dati iniziali (blueprint).
     * @param initialObjectLocations Una {@link java.util.Map} che mappa l'ID stringa di ogni oggetto
     * alla sua posizione iniziale (stanza o inventario).
     * @throws GameException Se si verifica un errore durante l'inizializzazione dello stato degli oggetti.
     */
    void initNewGameStates(int slotId, Map<String, ObjectData> objectBlueprints, Map<String, String> initialObjectLocations) throws GameException;
}
