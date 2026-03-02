package database;

import exceptions.GameException;
import java.util.Optional;

/**
 * L'interfaccia Repository definisce un contratto generico per le operazioni di persistenza.
 * Permette di salvare e caricare dati di tipo {@code T} associati a uno specifico slot di gioco.
 *
 * @author simona
 * @param <T> Il tipo di dato che il repository gestirà (es. {@code GameState}).
 */
public interface Repository<T> {
    
    /**
     * Salva i dati forniti in uno specifico slot di gioco.
     *
     * @param slotId L'ID dello slot di salvataggio in cui salvare i dati.
     * @param data L'istanza di tipo {@code T} da salvare.
     * @throws GameException Se si verifica un errore durante l'operazione di salvataggio.
     */
    void save(int slotId,T data) throws GameException;
    
    /**
     * Carica i dati da uno specifico slot di gioco.
     *
     * @param slotId L'ID dello slot di salvataggio da cui caricare i dati.
     * @return Un {@link java.util.Optional} contenente l'istanza di tipo {@code T} se i dati sono presenti,
     * altrimenti un {@link java.util.Optional#empty()}.
     * @throws GameException Se si verifica un errore durante l'operazione di caricamento.
     */
    Optional<T> load(int slotId) throws GameException;
}
