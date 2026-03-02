package parser;

import type.AdvObject;
import type.Command;

/**
 * La classe ParserOutput incapsula i risultati dell'analisi di una stringa di comando.
 * Contiene il comando riconosciuto, gli oggetti a cui si riferisce (nella stanza o nell'inventario)
 * ed eventuale testo aggiuntivo (extra).
 * 
 * @author simona
 */
public class ParserOutput {
    private final Command command;
    private final AdvObject object;
    private final AdvObject invObject;
    private final String extra;

    /**
     * Costruisce una nuova istanza di ParserOutput con i risultati del parsing.
     *
     * @param command L'oggetto {@link type.Command} riconosciuto. Può essere {@code null} se il comando non è valido.
     * @param object L'oggetto {@link type.AdvObject} presente nella stanza a cui si riferisce il comando. Può essere {@code null}.
     * @param invObject L'oggetto {@link type.AdvObject} presente nell'inventario a cui si riferisce il comando. Può essere {@code null}.
     * @param extra La {@link java.lang.String} di testo aggiuntivo. Può essere {@code null}.
     */
    public ParserOutput(Command command, AdvObject object, AdvObject invObject, String extra) {
        this.command = command;
        this.object = object;
        this.invObject = invObject;
        this.extra = extra;
    }

    /**
     * Restituisce il comando riconosciuto dall'input dell'utente.
     *
     * @return L'oggetto {@link type.Command}, o {@code null} se nessun comando è stato riconosciuto.
     */
    public Command getCommand() { return command; }

    /**
     * Restituisce l'oggetto trovato nella stanza a cui si riferisce il comando.
     *
     * @return L'oggetto {@link type.AdvObject} dalla stanza, o {@code null} se non rilevante o non trovato.
     */
    public AdvObject getObject() { return object; }

    /**
     * Restituisce l'oggetto trovato nell'inventario a cui si riferisce il comando.
     *
     * @return L'oggetto {@link type.AdvObject} dall'inventario, o {@code null} se non rilevante o non trovato.
     */
    public AdvObject getInvObject() { return invObject; }

    /**
     * Restituisce il testo extra associato al comando. Questo può essere usato per parametri
     * aggiuntivi, come una password o una descrizione dettagliata.
     *
     * @return La {@link java.lang.String} del testo extra, o {@code null} se non presente.
     */
    public String getExtra() { return extra; }
}