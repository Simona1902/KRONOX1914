/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adventure;

import parser.ParserOutput;
import type.AdvObject;
import type.Command;
import type.Room;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import exceptions.GameException;

/**
 * La classe astratta GameDescription definisce la struttura di base di un gioco di avventura testuale.
 * Fornisce i metodi fondamentali per la gestione delle stanze, dei comandi,
 * dell'inventario e dello stato del gioco, lasciando l'implementazione specifica
 * alle sottoclassi.
 * @author simona
 */
public abstract class GameDescription {

    private List<Room> rooms = new ArrayList<>();

    private final List<Command> commands = new ArrayList<>();

    private final List<AdvObject> inventory = new ArrayList<>();

    private Room currentRoom;
    
    private type.GameEpoch currentEpoch;

    /**
     * Restituisce la lista di tutte le stanze presenti nel gioco.
     * @return Una {@link java.util.List} di oggetti {@link type.Room} che rappresentano le stanze.
     *
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * Restituisce la lista dei comandi disponibili nel gioco.
     * @return Una {@link java.util.List} di oggetti {@link type.Command}.
     */
    public List<Command> getCommands() {
        return commands;
    }

    /**
     * Restituisce la stanza in cui si trova attualmente il giocatore.
     * @return L'oggetto {@link type.Room} che rappresenta la stanza corrente.
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Imposta la stanza corrente in cui si trova il giocatore.
     * @param currentRoom L'oggetto {@link type.Room} da impostare come stanza corrente.
     *
     */
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    /**
     * Imposta la lista di tutte le stanze disponibili nel gioco.
     * Questo metodo è tipicamente usato durante l'inizializzazione del gioco.
     * @param rooms Una {@link java.util.List} di oggetti {@link type.Room} da assegnare come stanze del gioco.
     */
    public void setRooms(List<Room> rooms){
        this.rooms = rooms;
    }
    
    /**
     * Restituisce la lista degli oggetti presenti nell'inventario del giocatore.
     * @return Una {@link java.util.List} di oggetti {@link type.AdvObject} nell'inventario.
     */
    public List<AdvObject> getInventory() {
        return inventory;
    }
    
    /**
     * Restituisce l'epoca temporale corrente del gioco.
     * @return Una {@link java.lang.String} che rappresenta l'epoca corrente.
     */
    public type.GameEpoch getCurrentEpoch(){
        return currentEpoch;
    }
    
    /**
     * Imposta l'epoca temporale corrente del gioco.
     * @param epoch La {@link java.lang.String} che rappresenta la nuova epoca corrente.
     */
    public void setCurrentEpoch(type.GameEpoch epoch){
        this.currentEpoch = epoch;
    }

    /**
     * Metodo astratto per inizializzare lo stato del gioco.
     * Deve essere implementato dalle sottoclassi per caricare il gioco da uno slot
     * o iniziarne uno nuovo.
     * @param slotId L'ID dello slot di salvataggio da caricare o in cui salvare.
     * @param isNewGame {@code true} se si tratta di una nuova partita, {@code false} altrimenti.
     * @throws exceptions.GameException Se si verifica un errore durante l'inizializzazione del gioco.
     */
    public abstract void init(int slotId, boolean isNewGame) throws GameException;

    /**
     * Metodo astratto che gestisce la prossima mossa del giocatore basandosi sul comando parsato.
     * Deve essere implementato dalle sottoclassi per definire la logica di gioco.
     *
     * @param p L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e eventuali oggetti correlati.
     * @param out Il {@link java.io.PrintWriter} per inviare output al client.
     */
    public abstract void nextMove(ParserOutput p, PrintWriter out);
    
    /**
     * Restituisce il messaggio di benvenuto del gioco.
     * @return Una {@link java.lang.String} contenente il messaggio di benvenuto.
     */
    public abstract String getWelcomeMsg();

    /**
     * Aggiorna il mondo di gioco allo stato corrispondente all'epoca corrente.
     * Questo metodo è chiamato dopo un viaggio nel tempo o al caricamento per assicurare
     * che le stanze e gli oggetti siano coerenti con l'epoca.
     * @throws exceptions.GameException Se si verifica un errore durante l'aggiornamento del mondo.
     */
    public abstract void updateWorldToCurrentEpoch() throws GameException;
    
    /**
     * Restituisce la stanza di partenza per una specifica epoca.
     * Utilizzato per posizionare il giocatore correttamente dopo un viaggio nel tempo.
     *
     * @param epoch La {@link java.lang.String} che identifica l'epoca desiderata.
     * @return L'oggetto {@link type.Room} che è la stanza di partenza per l'epoca specificata.
     *
     */
    public abstract Room getStartingRoomForEpoch(type.GameEpoch epoch);
    
    /**
     * Termina la sessione di gioco corrente e pulisce eventuali risorse.
     */
    public abstract void endGame();
    
    /**
     * Trova un oggetto di avventura tramite il suo ID numerico.
     * Utile per recuperare oggetti specifici indipendentemente dalla loro posizione nel mondo.
     *
     * @param id L'ID numerico dell'oggetto da cercare.
     * @return L'oggetto {@link type.AdvObject} trovato, o {@code null} se non trovato.
     */
    public abstract AdvObject findObjectById(int id);
    
    /**
     * Trova un oggetto di avventura tramite il suo ID testuale.
     * Utile per recuperare oggetti specifici indipendentemente dalla loro posizione nel mondo.
     *
     * @param id L'ID testuale dell'oggetto da cercare.
     * @return L'oggetto {@link type.AdvObject} trovato, o {@code null} se non trovato.
     */
    public abstract AdvObject findObjectByStringId(String id);

    /**
     * Gestisce il viaggio nel tempo, cambiando l'epoca corrente del gioco
     * e posizionando il giocatore in una stanza specifica della nuova epoca.
     *
     * @param newEpoch La {@link java.lang.String} che rappresenta la nuova epoca di destinazione.
     * @param startingRoomId La {@link java.lang.String} che rappresenta l'ID della stanza di partenza nella nuova epoca.
     * @throws GameException Se si verifica un errore durante il viaggio nel tempo.
     */
    public abstract void timeTravel(type.GameEpoch newEpoch, String startingRoomId) throws GameException;
    
    /**
     * Imposta lo stato di fine del gioco e il messaggio finale da mostrare.
     *
     * @param end Un valore booleano che indica se il gioco è terminato ({@code true}) o meno ({@code false}).
     * @param finalMessage La {@link java.lang.String} che contiene il messaggio da mostrare alla fine del gioco.
     */
    public abstract void setEnd(boolean end, String finalMessage);

    /**
     * Verifica se il gioco è terminato.
     *
     * @return {@code true} se il gioco è terminato, {@code false} altrimenti.
     */
    public abstract boolean isEnd();

    /**
     * Restituisce il messaggio finale del gioco, che viene mostrato alla sua conclusione.
     *
     * @return Una {@link java.lang.String} contenente il messaggio finale.
     */
    public abstract String getFinalMessage();
}
