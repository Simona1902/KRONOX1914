package parser;

import utils.Utils;
import java.util.ArrayList;
import type.AdvObject;
import type.Command;
import java.util.List;
import java.util.Set;

/**
 * La classe Parser è responsabile di analizzare le stringhe di comando inserite dall'utente.
 * Riconosce comandi, oggetti nella stanza e nell'inventario, e testo extra,
 * restituendo un oggetto {@link parser.ParserOutput} che rappresenta il comando parsato.
 *
 * @author simona
 */
public class Parser {
    private final Set<String> stopwords;

    /**
     * Costruisce una nuova istanza di Parser.
     *
     * @param stopwords Un {@link java.util.Set} di {@link java.lang.String} contenente le parole da ignorare durante il parsing.
     */
    public Parser(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    private int checkForCommand(String token, List<Command> commands) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getName().equalsIgnoreCase(token) ||
               (commands.get(i).getAlias() != null && commands.get(i).getAlias().contains(token))) {
                return i;
            }
        }
        return -1;
    }

    private int checkForObject(String token, List<AdvObject> objects) {
        if (objects == null) return -1;
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).getName().equalsIgnoreCase(token) ||
               (objects.get(i).getAlias() != null && objects.get(i).getAlias().contains(token))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Parsifica una stringa di comando dell'utente, estraendo il comando,
     * gli oggetti di riferimento (nella stanza o nell'inventario) ed eventuale testo extra.
     *
     * @param commandString La {@link java.lang.String} del comando inserito dall'utente.
     * @param commands La {@link java.util.List} di {@link type.Command} disponibili nel gioco.
     * @param roomObjects La {@link java.util.List} di {@link type.AdvObject} presenti nella stanza corrente.
     * @param inventory La {@link java.util.List} di {@link type.AdvObject} presenti nell'inventario del giocatore.
     * @return Un oggetto {@link parser.ParserOutput} contenente il comando parsato, gli oggetti trovati
     * e il testo extra. Se il comando non è riconosciuto, il campo {@code command} sarà {@code null}
     * e il campo {@code extra} conterrà l'intera stringa originale.
     */
    public ParserOutput parse(String commandString, List<Command> commands, List<AdvObject> roomObjects, List<AdvObject> inventory) {
        List<String> tokens = Utils.parseString(commandString, stopwords);
        if (tokens.isEmpty()) {
            return new ParserOutput(null, null, null, null);
        }

        int commandIndex = checkForCommand(tokens.get(0), commands);
        if (commandIndex > -1) {
            Command foundCommand = commands.get(commandIndex);
            tokens.remove(0);
            AdvObject foundRoomObject = null;
            AdvObject foundInventoryObject = null;
            List<String> remainingTokens = new ArrayList<>(tokens);
            for (String token : tokens) {
                int invIndex = checkForObject(token, inventory);
                if (invIndex > -1) {
                    foundInventoryObject = inventory.get(invIndex);
                    remainingTokens.remove(token);
                }
                int roomIndex = checkForObject(token, roomObjects);
                if (roomIndex > -1) {
                    foundRoomObject = roomObjects.get(roomIndex);
                    remainingTokens.remove(token);
                }
            }
            String extraText = remainingTokens.isEmpty() ? null : String.join(" ", remainingTokens);
            return new ParserOutput(foundCommand, foundRoomObject, foundInventoryObject, extraText);
        }

        return new ParserOutput(null, null, null, commandString);
    }
}