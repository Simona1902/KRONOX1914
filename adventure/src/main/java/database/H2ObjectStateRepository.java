package database;

import exceptions.GameException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import database.DBManager;
import type.AdvObject;
import type.ObjectData;
import type.ObjectState;

/**
 * Implementazione di {@link ObjectStateRepository} che gestisce il salvataggio e il caricamento
 * dello stato degli oggetti di gioco nel database H2.
 * @author simona
 */
public class H2ObjectStateRepository implements ObjectStateRepository {

    /**
     * Carica tutti gli stati degli oggetti associati a uno specifico slot di salvataggio.
     * Recupera i dati dalla tabella `object_state` del database.
     *
     * @param slotId L'ID dello slot di salvataggio da cui caricare gli stati degli oggetti.
     * @return Una {@link java.util.Map} dove la chiave è l'ID stringa dell'oggetto
     * e il valore è l'oggetto {@link type.ObjectState} che rappresenta il suo stato.
     * @throws GameException Se si verifica un errore SQL durante il caricamento.
     */
    @Override
    public Map<String, ObjectState> loadAllObjectStates(int slotId) throws GameException {
        Map<String, ObjectState> states = new HashMap<>();
        String sql = "SELECT * FROM object_state WHERE slot_id = ?";
        Connection conn = DBManager.getConnection();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, slotId);
            rs = pstm.executeQuery();
            while(rs.next()) {
                String objectId = rs.getString("object_id");
                states.put(objectId, new ObjectState(
                    objectId, rs.getString("location"), rs.getString("openable"), rs.getString("pushable"), rs.getString("is_locked")
                ));
            }
        } catch(SQLException e) {
            throw new GameException("Errore caricamento stato oggetti per slot " + slotId, e);
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { /* ignora */ }
            }
            if (pstm != null) {
                try { pstm.close(); } catch (SQLException e) { /* ignora */ }
            }
        }
        return states;
    }
    
    /**
     * Salva o aggiorna tutti gli stati degli oggetti correnti nel database per uno specifico slot.
     * Utilizza un'operazione MERGE (upsert) per inserire nuovi stati o aggiornare quelli esistenti.
     *
     * @param slotId L'ID dello slot di salvataggio a cui associare gli stati degli oggetti.
     * @param liveObjects Una {@link java.util.Map} di oggetti {@link type.AdvObject} correnti,
     * dove la chiave è l'ID stringa dell'oggetto.
     * @param objectLocations Una {@link java.util.Map} che mappa l'ID stringa di ogni oggetto
     * alla sua posizione attuale (stanza, inventario, ecc.).
     * @throws GameException Se si verifica un errore SQL durante il salvataggio.
     */
    @Override
    public void saveAllObjectStates(int slotId, Map<String, AdvObject> liveObjects, Map<String, String> objectLocations) throws GameException {
        String sql = "MERGE INTO object_state (slot_id, object_id, location, openable, pushable, is_locked) KEY(slot_id, object_id) VALUES(?, ?, ?, ?, ?, ?)";
        Connection conn = DBManager.getConnection();
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sql);
            for (Map.Entry<String, AdvObject> entry : liveObjects.entrySet()) {
                String objectId = entry.getKey();
                AdvObject obj = entry.getValue();
                pstm.setInt(1, slotId);
                pstm.setString(2, objectId);
                pstm.setString(3, objectLocations.get(objectId));
                pstm.setString(4, obj.getOpenable()); 
                pstm.setString(5, obj.getPushable());
                pstm.setString(6, obj.getLocked());
                pstm.addBatch();
            }
            pstm.executeBatch();
        } catch(SQLException e) {
            throw new GameException("Errore salvataggio stato oggetti per slot " + slotId, e);
        } finally {
            if (pstm != null) {
                try { pstm.close(); } catch (SQLException e) { /* ignora */ }
            }
        }
    }
    
    /**
     * Inizializza lo stato degli oggetti nel database per una nuova partita in uno specifico slot.
     * Prima elimina tutti gli stati degli oggetti esistenti per quello slot,
     * poi inserisce gli stati iniziali basati sui blueprint e le posizioni iniziali.
     *
     * @param slotId L'ID dello slot di salvataggio per cui inizializzare gli stati.
     * @param objectBlueprints Una {@link java.util.Map} dove la chiave è l'ID stringa dell'oggetto
     * e il valore è l'oggetto {@link type.ObjectData} che contiene i dati iniziali (blueprint).
     * @param initialObjectLocations Una {@link java.util.Map} che mappa l'ID stringa di ogni oggetto
     * alla sua posizione iniziale (stanza o inventario).
     * @throws GameException Se si verifica un errore SQL durante l'inizializzazione.
     */
    @Override
    public void initNewGameStates(int slotId, Map<String, ObjectData> objectBlueprints, Map<String, String> initialObjectLocations) throws GameException {
        Connection conn = DBManager.getConnection();
        PreparedStatement deletePstm = null;
        PreparedStatement insertPstm = null;
        
        try {
            // Prima operazione: pulizia
            String deleteSql = "DELETE FROM object_state WHERE slot_id = ?";
            deletePstm = conn.prepareStatement(deleteSql);
            deletePstm.setInt(1, slotId);
            deletePstm.executeUpdate();
            
            // Seconda operazione: inserimento
            String insertSql = "INSERT INTO object_state (slot_id, object_id, location, openable, pushable, is_locked) VALUES (?, ?, ?, ?, ?, ?)";
            insertPstm = conn.prepareStatement(insertSql);
            for (Map.Entry<String, ObjectData> entry : objectBlueprints.entrySet()) {
                String objectId = entry.getKey();
                ObjectData blueprint = entry.getValue();
                insertPstm.setInt(1, slotId);
                insertPstm.setString(2, objectId);
                insertPstm.setString(3, initialObjectLocations.get(objectId));
                insertPstm.setString(4, blueprint.openable);
                insertPstm.setString(5, blueprint.pushable);
                insertPstm.setString(6, blueprint.locked);
                insertPstm.addBatch();
            }
            insertPstm.executeBatch();
            
        } catch(SQLException e) {
            throw new GameException("Errore durante l'inizializzazione dello stato degli oggetti per lo slot " + slotId, e);
        } finally {
            if (deletePstm != null) {
                try { deletePstm.close(); } catch (SQLException e) { /* ignora */ }
            }
            if (insertPstm != null) {
                try { insertPstm.close(); } catch (SQLException e) { /* ignora */ }
            }
        }
    }
}