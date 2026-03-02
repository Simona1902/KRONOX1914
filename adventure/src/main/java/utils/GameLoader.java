package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import type.ObjectData;
import type.RoomData;

/**
 * La classe GameLoader è responsabile del caricamento dei dati di gioco
 * (stanze e oggetti) da file JSON presenti nel classpath.
 * Utilizza la libreria Gson per la deserializzazione.
 *
 * @author simona
 */
public class GameLoader {
    private final Map<String, RoomData> roomDataMap;
    private final Map<String, ObjectData> objectDataMap;
    
    /**
     * Costruisce un nuovo GameLoader e carica i dati dalle due sorgenti JSON specificate.
     *
     * @param roomsFilePath Il percorso della {@link java.lang.String} al file JSON contenente i dati delle stanze, relativo al classpath.
     * @param objectsFilePath Il percorso della {@link java.lang.String} al file JSON contenente i dati degli oggetti, relativo al classpath.
     * @throws RuntimeException Se i file non vengono trovati o si verifica un errore durante il caricamento/parsing.
     */
    public GameLoader(String roomsFilePath, String objectsFilePath) {
        this.roomDataMap = loadData(roomsFilePath, new TypeToken<Map<String, RoomData>>(){}.getType());
        this.objectDataMap = loadData(objectsFilePath, new TypeToken<Map<String, ObjectData>>() {}.getType());
    }
    
    private <T> T loadData(String filePath, Type type) {
        Gson gson = new Gson();
        InputStream stream = null;
        InputStreamReader reader = null;
        try {
            stream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (stream == null) {
                throw new IllegalArgumentException("File non trovato nel classpath: " + filePath);
            }
            reader = new InputStreamReader(stream);
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile caricare i dati di gioco dal file: " + filePath, e);
        } finally {
            // Blocco per la chiusura manuale e sicura degli stream
            if (reader != null) {
                try {
                    reader.close();
                } catch (java.io.IOException e) {
                    // Ignora l'eccezione sulla chiusura
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (java.io.IOException e) {
                    // Ignora l'eccezione sulla chiusura
                }
            }
        }
    }
    
    /**
     * Restituisce la mappa dei dati delle stanze caricati.
     *
     * @return Una {@link java.util.Map} dove la chiave è l'ID stringa della stanza
     * e il valore è l'oggetto {@link type.RoomData}.
     */
    public Map<String, RoomData> getRoomDataMap(){
        return roomDataMap;
    }
    
    /**
     * Restituisce la mappa dei dati degli oggetti caricati.
     *
     * @return Una {@link java.util.Map} dove la chiave è l'ID stringa dell'oggetto
     * e il valore è l'oggetto {@link type.ObjectData}.
     */
    public Map<String, ObjectData> getObjectDataMap(){
        return objectDataMap;
    }
}
