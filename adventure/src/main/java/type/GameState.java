package type;

/**
 * La classe GameState incapsula lo stato persistente del gioco in un dato momento.
 * Contiene informazioni sulla posizione del giocatore, l'epoca corrente e vari flag
 * di stato legati ai puzzle e agli eventi del gioco.
 * @author simona
 */
public class GameState {
    private final String locationId;
    private final type.GameEpoch currentEpoch;
    private final boolean basementLit;
    // Stati 1914
    private final boolean foundP;
    private final boolean foundA;
    private final boolean foundX;
    private final boolean pastMidday;

    /**
     * Costruisce una nuova istanza di GameState con tutti i parametri specificati.
     * Questo costruttore è utilizzato per creare un oggetto che rappresenta
     * lo stato completo del gioco da salvare o caricare.
     *
     * @param locationId L'ID della stanza in cui si trova il giocatore.
     * @param currentEpoch L'epoca temporale corrente del gioco.
     * @param basementLit {@code true} se la luce nello scantinato è accesa, {@code false} altrimenti.
     * @param foundP {@code true} se l'indizio 'P' è stato trovato, {@code false} altrimenti.
     * @param foundA {@code true} se l'indizio 'A' è stato trovato, {@code false} altrimenti.
     * @param foundX {@code true} se l'indizio 'X' è stato trovato, {@code false} altrimenti.
     * @param isPastMidday {@code true} se nel gioco del 1914 è passato mezzogiorno, {@code false} altrimenti.
     */
    public GameState(String locationId, type.GameEpoch currentEpoch, boolean basementLit,
                     boolean foundP, boolean foundA, boolean foundX, boolean isPastMidday) {
        this.locationId = locationId;
        this.currentEpoch = currentEpoch;
        this.basementLit = basementLit;
        this.foundP = foundP;
        this.foundA = foundA;
        this.foundX = foundX;
        this.pastMidday = isPastMidday;
    }

    /**
     * Restituisce l'ID della stanza in cui si trova il giocatore.
     *
     * @return La {@link java.lang.String} dell'ID della posizione del giocatore.
     */
    public String getLocationId() { return locationId; }

    /**
     * Restituisce l'epoca temporale corrente del gioco.
     *
     * @return La {@link java.lang.String} che rappresenta l'epoca corrente.
     */
    public type.GameEpoch getCurrentEpoch() { return currentEpoch; }

    /**
     * Verifica se la luce nello scantinato è accesa.
     *
     * @return {@code true} se la luce nello scantinato è accesa, {@code false} altrimenti.
     */
    public boolean isBasementLit() { return basementLit; }

    /**
     * Verifica se l'indizio 'P' per il Criptex è stato trovato.
     *
     * @return {@code true} se l'indizio 'P' è stato trovato, {@code false} altrimenti.
     */
    public boolean isFoundP() { return foundP; }

    /**
     * Verifica se l'indizio 'A' per il Criptex è stato trovato.
     *
     * @return {@code true} se l'indizio 'A' è stato trovato, {@code false} altrimenti.
     */
    public boolean isFoundA() { return foundA; }

    /**
     * Verifica se l'indizio 'X' per il Criptex è stato trovato.
     *
     * @return {@code true} se l'indizio 'X' è stato trovato, {@code false} altrimenti.
     */
    public boolean isFoundX() { return foundX; }

    /**
     * Verifica se nel gioco del 1914 è passato mezzogiorno.
     *
     * @return {@code true} se è passato mezzogiorno, {@code false} altrimenti.
     */
    public boolean isPastMidday() { return pastMidday; }
}