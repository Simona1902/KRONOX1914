package adventure;

/**
 * L'interfaccia GameObservable definisce il contratto per gli oggetti che possono
 * notificare i propri osservatori ({@link GameObserver}) riguardo a cambiamenti di stato.
 * Fa parte del pattern Observer.
 * @author simona
 */
public interface GameObservable {
    
    /**
     * Aggiunge un osservatore alla lista degli osservatori di questo oggetto.
     * L'osservatore sarà notificato ogni volta che si verifica un cambiamento.
     *
     * @param o L'oggetto {@link GameObserver} da aggiungere.
     */
    public void attach(GameObserver o);
    
    /**
     * Rimuove un osservatore dalla lista degli osservatori di questo oggetto.
     * L'osservatore non riceverà più notifiche da questo oggetto.
     *
     * @param o L'oggetto {@link GameObserver} da rimuovere.
     */
    public void detach(GameObserver o);
    
    /**
     * Notifica tutti gli osservatori registrati riguardo a un cambiamento di stato.
     * Ogni osservatore riceverà un aggiornamento.
     */
    public void notifyObservers();
    
}
