package exceptions;

/**
 * La classe GameException è un'eccezione personalizzata che rappresenta un errore
 * generico o un'eccezione specifica all'interno della logica del gioco.
 * Estende {@link java.lang.Exception}.
 * 
 * @author simona
 */
public class GameException extends Exception{
    /**
     * Costruttore che accetta un messaggio di errore.
     *
     * @param message il messaggio nel dettaglio che descrive la causa dell'eccezione.
     */
    public GameException(String message) {
        super(message);
    }
    
    /**
     * Costruttore che incapsula un'altra eccezione (chaining di eccezioni).
     * Questo è utile per avvolgere eccezioni di livello inferiore con un contesto di gioco.
     *
     * @param message il messaggio nel dettaglio che descrive la causa dell'eccezione.
     * @param cause l'eccezione originale ({@link java.lang.Throwable}) che ha causato questo errore.
     */
    public GameException(String message, Throwable cause){
        super(message, cause);
    }
    
}
