package exceptions;

/**
 * Eccezione lanciata quando un'azione del giocatore fallisce per una
 * ragione logica di gioco (es. porta chiusa, oggetto non utilizzabile).
 * @author simona
 */
public class ActionException extends GameException {
    
    /**
     * Costruttore che accetta un messaggio di errore specifico per l'azione fallita.
     *
     * @param message il messaggio che descrive il motivo del fallimento dell'azione.
     */
    public ActionException(String message) {
        super(message);
    }
    
    /**
     * Costruttore che incapsula un'altra eccezione, fornendo un messaggio
     * specifico per l'azione fallita e la causa originale.
     *
     * @param message il messaggio che descrive il motivo del fallimento dell'azione.
     * @param cause l'eccezione originale ({@link java.lang.Throwable}) che ha causato questo errore.
     */
    public ActionException(String message, Throwable cause){
        super(message, cause);
    }
}
