package type;

/**
 * L'enumerazione CommandType definisce i tipi di comandi riconosciuti
 * all'interno del gioco di avventura. Ogni costante rappresenta un'azione
 * specifica che il giocatore può tentare di eseguire.
 * 
 * @author simona
 */
public enum CommandType {

    /**
     * Comando per terminare la sessione di gioco.
     */
    END,

    /**
     * Comando per visualizzare il contenuto dell'inventario del giocatore.
     */
    INVENTORY,

    /**
     * Comando di movimento: sposta il giocatore verso nord.
     */
    NORD,

    /** 
     * Comando di movimento: sposta il giocatore verso sud.
     */
    SOUTH,

    /** 
     * Comando di movimento: sposta il giocatore verso est.
     */
    EAST,

    /**
     * Comando di movimento: sposta il giocatore verso ovest.
     */
    WEST,

    /** 
     * Comando per aprire un oggetto.
     */
    OPEN,

    /**
     * Comando per chiudere un oggetto.
     */
    CLOSE,

    /**
     * Comando per spingere un oggetto.
     */
    PUSH,

    /**
     * Comando per tirare un oggetto.
     */
    PULL,

    /**
     * Comando per raccogliere un oggetto e aggiungerlo all'inventario.
     */
    PICK_UP,

    /**
     * Comando per parlare con un personaggio o un oggetto parlante.
     */
    TALK_TO,

    /**
     * Comando per dare un oggetto.
     */
    GIVE,

    /**
     * Comando per usare un oggetto (da solo o su un altro oggetto/testo).
     */
    USE,

    /**
     * Comando per osservare un oggetto o la stanza corrente.
     */
    LOOK_AT,

    /**
     * Comando per accendere qualcosa (es. una luce).
     */
    TURN_ON,

    /**
     * Comando di movimento: sposta il giocatore verso il basso (es. scendi una scala).
     */
    DOWN,

    /**
     * Comando di movimento: sposta il giocatore verso l'alto (es. sali una scala).
     */
    UP,
    
    /**
     * Comando per visualizzare un messaggio di aiuto o i comandi disponibili.
     */
    HELP
}
