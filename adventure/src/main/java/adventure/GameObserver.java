package adventure;

import exceptions.ActionException;
import parser.ParserOutput;


/**
 * L'interfaccia GameObserver definisce il contratto per gli oggetti che desiderano
 * essere notificati dei cambiamenti di stato da un oggetto osservabile ({@link GameObservable}).
 * Fa parte del pattern Observer.
 * @author simona
 */
public interface GameObserver {

    /**
     * Questo metodo viene richiamato dall'oggetto osservabile per notificare un cambiamento di stato.
     * Le implementazioni di questo metodo dovrebbero gestire la logica di aggiornamento
     * in risposta allo stato attuale del gioco e all'output del parser.
     *
     * @param description L'oggetto {@link GameDescription} che rappresenta lo stato corrente del gioco.
     * @param parserOutput L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e i suoi elementi.
     * @return Una {@link java.lang.String} che rappresenta un messaggio di feedback o un risultato dell'aggiornamento.
     * @throws exceptions.ActionException Se si verifica un errore specifico durante l'elaborazione dell'aggiornamento.
     */
    public String update(GameDescription description, ParserOutput parserOutput) throws ActionException;

}
