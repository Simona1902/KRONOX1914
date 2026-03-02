package database;

import exceptions.GameException;
import java.sql.*;
import java.io.File; 

/**
 * La classe DBManager gestisce la connessione al database H2 del gioco.
 * Si occupa di aprire, ottenere e chiudere la connessione,
 * oltre a inizializzare lo schema del database se necessario.
 * @author simona
 */
public class DBManager {
    private static String DB_URL;    
    private static final String USER= "user";
    private static final String PASSWORD = "kronox1914"; 
    private static Connection conn;
    private static final String DB_FILE_NAME_MV = "stato_gioco.mv.db";
    private static final String EXTERNAL_DB_DIR_NAME = "data";
    
    private static String getProjectRootPath() {
        String path = DBManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            System.err.println("Errore di decodifica del percorso: " + e.getMessage());
        }
        File projectRoot = null;
        File currentFile = new File(path);
        if (currentFile.isFile() && currentFile.getName().endsWith(".jar")) {
            currentFile = currentFile.getParentFile(); // target/
        }
        if (currentFile != null && currentFile.getName().equals("classes")) {
            currentFile = currentFile.getParentFile(); // Risale da target/classes a target/
        }
        if (currentFile != null && currentFile.getName().equals("target")) {
            projectRoot = currentFile.getParentFile(); // Questo dovrebbe essere map_project/adventure/
        } else {
            System.err.println("Attenzione: Impossibile determinare la radice del progetto in modo robusto. Usando user.dir.");
            projectRoot = new File(System.getProperty("user.dir"));
        }
        if (projectRoot == null) {
             throw new RuntimeException("Impossibile determinare la radice del progetto per il database.");
        }
        return projectRoot.getAbsolutePath();
    }

    static {
        try {
            initializeDatabasePath();
        } catch (GameException e) {
            System.err.println("ERRORE FATALE ALL'AVVIO: Impossibile inizializzare il percorso del database H2.");
            throw new RuntimeException("Inizializzazione del DB fallita.", e);
        }
    }
    
    /**
     * Inizializza il percorso del database estraendolo dal JAR se non esiste già.
     * Questo metodo deve essere chiamato prima di tentare qualsiasi connessione.
     */
    private static void initializeDatabasePath() throws GameException {
        try {
            File projectRoot = new File(getProjectRootPath());
            File externalDbDir = new File(projectRoot, EXTERNAL_DB_DIR_NAME);
            if (!externalDbDir.exists()) {
                externalDbDir.mkdirs(); // Crea la directory 'data' se non esiste
            }
            File externalDbFileMV = new File(externalDbDir, DB_FILE_NAME_MV);
            DB_URL = "jdbc:h2:" + externalDbFileMV.getAbsolutePath().replace(".mv.db", "");
            System.out.println("URL di connessione al database: " + DB_URL);
            System.out.println("Il database verrà creato/connesso in " + externalDbFileMV.getAbsolutePath());

        } catch (Exception e) {
            throw new GameException("ERRORE CRITICO: Impossibile preparare il percorso del database H2.", e);
        }
    }

    /**
     * Stabilisce una connessione al database se non è già attiva o è chiusa.
     * Se è la prima volta che il database viene usato, crea lo schema e inizializza gli slot di salvataggio.
     *
     * @throws GameException Se si verifica un errore SQL durante la connessione o l'inizializzazione del database.
     */
    public static void startConnection() throws GameException {
        if (DB_URL == null) {
            // Fallback di sicurezza.
            System.err.println("Avviso: DB_URL non inizializzato prima di startConnection. Tentativo di inizializzazione.");
            initializeDatabasePath();
        }
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                createSchemaAndInitSlots();
            }
        } catch (SQLException e) {
            throw new GameException("Impossibile connettersi al db.", e);
        }
    }
    
    /**
     * Restituisce l'istanza della connessione corrente al database.
     * Lancia un'eccezione se la connessione non è attiva.
     *
     * @return L'oggetto {@link java.sql.Connection} della connessione al database.
     * @throws GameException Se la connessione non è attiva o si verifica un errore nello stato.
     */
    public static Connection getConnection() throws GameException {
        try {
            if (conn == null || conn.isClosed()) {
                throw new GameException("Connessione non attiva al db.");
            }
            return conn;
        } catch (SQLException e) {
            throw new GameException("Errore nello stato di connessione al DB.", e);
        }
    }

    /**
     * Chiude la connessione al database se è aperta e non è già chiusa.
     * Eventuali errori durante la chiusura vengono stampati su `System.err`.
     */
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la chiusura al db" + e.getMessage());
        }
    }

    private static void createSchemaAndInitSlots() throws SQLException {
        Statement stm = null;
        ResultSet rs = null;
        PreparedStatement pstm = null;
    
        try {
            stm = conn.createStatement();
            rs = conn.getMetaData().getTables(null, null, "SAVE_SLOTS", null);
            boolean isFirstRun = !rs.next();

            // Rimuove le vecchie tabelle per sicurezza
            stm.executeUpdate("DROP TABLE IF EXISTS player_state");
            stm.executeUpdate("DROP TABLE IF EXISTS game_state");

            // Crea la tabella SAVE_SLOTS con TUTTE le colonne necessarie
            String saveSlotsSql = "CREATE TABLE IF NOT EXISTS save_slots (" +
                              "slot_id INT PRIMARY KEY, " +
                              "slot_name VARCHAR(255) NOT NULL, " +
                              "last_saved TIMESTAMP, " +
                              "player_location_id VARCHAR(255)," +
                              "current_epoch VARCHAR(50)," +
                              "is_basement_lit BOOLEAN DEFAULT FALSE," +
                              "found_p BOOLEAN DEFAULT FALSE," +
                              "found_a BOOLEAN DEFAULT FALSE," +
                              "found_x BOOLEAN DEFAULT FALSE," +
                              "is_past_midday BOOLEAN DEFAULT FALSE," +
                              "is_completed BOOLEAN DEFAULT FALSE" +
                              ")";
            stm.executeUpdate(saveSlotsSql);

            // Crea la tabella per lo stato degli oggetti
            String objectStateSql = "CREATE TABLE IF NOT EXISTS object_state (" +
                                "slot_id INT NOT NULL, " +
                                "object_id VARCHAR(255) NOT NULL, " +
                                "location VARCHAR(255), " +
                                "openable VARCHAR(255), " +
                                "pushable VARCHAR(255), " +
                                "is_locked VARCHAR(255), " +
                                "PRIMARY KEY (slot_id, object_id)" +
                                ")";
            stm.executeUpdate(objectStateSql);

            // Inizializza gli slot se è la prima esecuzione
            if (isFirstRun) {
                System.out.println("Prima esecuzione: inizializzazione degli slot di salvataggio...");
                pstm = conn.prepareStatement("INSERT INTO save_slots (slot_id, slot_name) VALUES (?, ?)");
                for (int i = 1; i <= 10; i++) {
                    pstm.setInt(1, i);
                    pstm.setString(2, "[Nuova Partita]");
                    pstm.addBatch();
                }
                pstm.executeBatch();
            }
        } finally {
            // Chiusura sicura di tutte le risorse del database
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { /* ignora */ }
            }
            if (pstm != null) {
                try { pstm.close(); } catch (SQLException e) { /* ignora */ }
            }
            if (stm != null) {
                try { stm.close(); } catch (SQLException e) { /* ignora */ }
            }
        }
    }
}