package database;

import exceptions.GameException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import type.GameState;
import type.SlotMetadata;

/**
 * Implementazione di {@link Repository} e {@link SaveSlotRepository}
 * che gestisce il salvataggio, caricamento e la gestione dei metadati
 * degli slot di gioco nel database H2.
 * @author simona
 */
public class H2SaveSlotRepository implements Repository<GameState>, SaveSlotRepository {

    /**
     * Salva lo stato corrente del gioco in uno specifico slot di salvataggio.
     * Tutti i campi dello stato del gioco vengono aggiornati nella tabella `save_slots`.
     *
     * @param slotId L'ID dello slot di salvataggio in cui salvare i dati.
     * @param data L'oggetto {@link type.GameState} contenente lo stato del gioco da salvare.
     * @throws GameException Se si verifica un errore SQL durante l'operazione di salvataggio.
     */
    @Override
    public void save(int slotId, GameState data) throws GameException {
        // Query aggiornata per scrivere TUTTI i campi di stato
        String sql = "UPDATE save_slots SET player_location_id = ?, current_epoch = ?, is_basement_lit = ?, found_p = ?, found_a = ?, found_x = ?, is_past_midday = ?, last_saved = ? WHERE slot_id = ?";
        Connection conn = DBManager.getConnection();
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, data.getLocationId());
            pstm.setString(2, data.getCurrentEpoch().name());
            pstm.setBoolean(3, data.isBasementLit());
            pstm.setBoolean(4, data.isFoundP());
            pstm.setBoolean(5, data.isFoundA());
            pstm.setBoolean(6, data.isFoundX());
            pstm.setBoolean(7, data.isPastMidday());
            pstm.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            pstm.setInt(9, slotId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new GameException("Errore durante il salvataggio dello stato del giocatore per lo slot " + slotId, e);
        } finally {
            if (pstm != null){
                try {
                    pstm.close();
                }catch (SQLException e){
                    /*ignora*/
                }
            }
        }
    }

    /**
     * Carica lo stato del gioco da uno specifico slot di salvataggio.
     *
     * @param slotId L'ID dello slot di salvataggio da cui caricare i dati.
     * @return Un {@link java.util.Optional} contenente l'oggetto {@link type.GameState}
     * se i dati sono presenti e validi per lo slot, altrimenti un {@link java.util.Optional#empty()}.
     * @throws GameException Se si verifica un errore SQL durante l'operazione di caricamento.
     */
    @Override
    public Optional<GameState> load(int slotId) throws GameException {
        String sql = "SELECT * FROM save_slots WHERE slot_id = ?";
        Connection conn = DBManager.getConnection();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, slotId);
            rs = pstm.executeQuery();
                if (rs.next()) {
                    String locationId = rs.getString("player_location_id");
                    if (locationId == null) {
                        return Optional.empty();
                    }
                    String epochString = rs.getString("current_epoch");
                    boolean isBasementLit = rs.getBoolean("is_basement_lit");
                    boolean foundP = rs.getBoolean("found_p");
                    boolean foundA = rs.getBoolean("found_a");
                    boolean foundX = rs.getBoolean("found_x");
                    boolean isPastMidday = rs.getBoolean("is_past_midday");
                    
                    // Chiamata al costruttore di GameState con tutti i parametri
                    return Optional.of(new GameState(locationId, type.GameEpoch.valueOf(epochString), isBasementLit, foundP, foundA, foundX, isPastMidday));
                }
            }catch (SQLException e) {
            throw new GameException("Errore durante il caricamento dello stato del giocatore per lo slot " + slotId, e);
            } finally{
                if (rs !=null){
                    try{
                        rs.close();
                    }catch(SQLException e){
                        /*ignora*/
                    }
                }
                if (pstm != null){
                    try{
                        pstm.close();
                    } catch (SQLException e){
                        /*ignora*/
                    }
                }
            }   
        return Optional.empty();
    }
    /**
     * Carica i metadati di tutti gli slot di salvataggio disponibili.
     * Questo include l'ID dello slot, il nome, l'ultima data di salvataggio e se è completato.
     *
     * @return Una {@link java.util.List} di oggetti {@link type.SlotMetadata} che contengono
     * le informazioni essenziali per ogni slot di salvataggio, ordinati per ID.
     * @throws GameException Se si verifica un errore SQL durante il caricamento dei metadati.
     */
    @Override
    public List<SlotMetadata> loadAllSlotsMetadata() throws GameException {
        List<SlotMetadata> slots = new ArrayList<>();
        String sql = "SELECT slot_id, slot_name, last_saved, is_completed FROM save_slots ORDER BY slot_id ASC";
        Connection conn = DBManager.getConnection();
        Statement stm = null;
        ResultSet rs = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                slots.add(new SlotMetadata(
                        rs.getInt("slot_id"),
                        rs.getString("slot_name"),
                        rs.getTimestamp("last_saved"),
                        rs.getBoolean("is_completed")
                ));
            }
        } catch (SQLException e) {
            throw new GameException("Errore durante il caricamento dei metadati degli slot.", e);
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { /* ignora */ }
            }
            if (stm != null) {
                try { stm.close(); } catch (SQLException e) { /* ignora */ }
            }
        }
        return slots;
    }
    /**
     * Aggiorna il nome di uno specifico slot di salvataggio.
     *
     * @param slotId L'ID dello slot di cui aggiornare il nome.
     * @param newName La nuova {@link java.lang.String} che sarà il nome dello slot.
     * @throws GameException Se si verifica un errore SQL durante l'aggiornamento del nome.
     */
    @Override
    public void updateSlotName(int slotId, String newName) throws GameException {
        String sql = "UPDATE save_slots SET slot_name = ? WHERE slot_id = ?";
        Connection conn = DBManager.getConnection();
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, newName);
            pstm.setInt(2, slotId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new GameException("Errore durante l'aggiornamento del nome dello slot " + slotId, e);
        } finally {
            if (pstm != null) {
                try { pstm.close(); } catch (SQLException e) { /* ignora */ }
            }
        }
    }

    /**
     * Marca uno slot di salvataggio come "completato".
     * Questo viene tipicamente chiamato quando il giocatore finisce la partita associata a quello slot.
     *
     * @param slotId L'ID dello slot da marcare come completato.
     * @throws GameException Se si verifica un errore SQL durante l'aggiornamento dello stato di completamento.
     */
    @Override
    public void markAsCompleted(int slotId) throws GameException {
        String sql = "UPDATE save_slots SET is_completed = TRUE WHERE slot_id = ?";
        Connection conn = DBManager.getConnection();
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, slotId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new GameException("Errore nel segnare lo slot " + slotId + " come completato.", e);
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (SQLException e) {
                    // Ignora l'eccezione sulla chiusura
                }
            }
        }
    }

    /**
     * Resetta completamente uno slot di salvataggio, eliminando tutti i dati
     * di stato del gioco e degli oggetti ad esso associati, e ripristinando
     * i metadati dello slot ai valori predefiniti di una "Nuova Partita".
     * L'operazione è transazionale per garantire che tutti gli aggiornamenti
     * avvengano con successo o nessuno di essi.
     *
     * @param slotId L'ID dello slot di salvataggio da resettare.
     * @throws GameException Se si verifica un errore SQL durante il reset dello slot.
     */
    @Override
    public void resetSlot(int slotId) throws GameException {
        // Elimina i dati dello stato del gioco e degli oggetti per lo slot specificato
        // e resetta i metadati dello slot per renderlo come una "Nuova Partita" pulita.
        String sqlDeleteGameState = "DELETE FROM GAME_STATE WHERE SLOT_ID = ?";
        String sqlDeleteObjectState = "DELETE FROM OBJECT_STATE WHERE SLOT_ID = ?";
        String sqlResetSlotMetadata = "UPDATE SAVE_SLOTS SET PLAYER_LOCATION_ID = NULL, CURRENT_EPOCH = 'PRESENTE', " +
                                      "IS_BASEMENT_LIT = FALSE, FOUND_P = FALSE, FOUND_A = FALSE, FOUND_X = FALSE, " +
                                      "IS_PAST_MIDDAY = FALSE, LAST_SAVED = NULL, IS_COMPLETED = FALSE " +
                                      "WHERE SLOT_ID = ?";

        Connection conn = DBManager.getConnection();
        PreparedStatement pstmDeleteObjectState = null;
        PreparedStatement pstmResetMetadata = null;

        try {
            conn.setAutoCommit(false); // Inizia una transazione per assicurare l'atomicità

            pstmDeleteObjectState = conn.prepareStatement(sqlDeleteObjectState);
            pstmDeleteObjectState.setInt(1, slotId);
            pstmDeleteObjectState.executeUpdate();

            pstmResetMetadata = conn.prepareStatement(sqlResetSlotMetadata);
            pstmResetMetadata.setInt(1, slotId);
            pstmResetMetadata.executeUpdate();

            conn.commit(); // Conferma le modifiche
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Annulla le modifiche in caso di errore
                }
            } catch (SQLException ex) {
                System.err.println("Errore durante il rollback: " + ex.getMessage());
            }
            throw new GameException("Errore durante il reset dello slot di salvataggio " + slotId, e);
        } finally {
            try {
                if (pstmDeleteObjectState != null) pstmDeleteObjectState.close();
                if (pstmResetMetadata != null) pstmResetMetadata.close();
                if (conn != null) conn.setAutoCommit(true); // Ripristina auto-commit
            } catch (SQLException e) {
                System.err.println("Errore durante la chiusura delle risorse nel reset dello slot: " + e.getMessage());
            }
        }
    }
}