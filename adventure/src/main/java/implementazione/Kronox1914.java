package implementazione;

import database.Repository;
import database.ObjectStateRepository;
import database.H2SaveSlotRepository;
import database.H2ObjectStateRepository;
import exceptions.GameException;
import adventure.GameDescription;
import adventure.GameObservable;
import adventure.GameObserver;
import exceptions.ActionException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import parser.ParserOutput;
import utils.GameLoader;
import type.*;
import utils.*;
import database.SaveSlotRepository;

/**
 * La classe Kronox1914 implementa la logica specifica del gioco di avventura "Kronox 1914".
 * Estende {@link adventure.GameDescription} per fornire le funzionalità di base del gioco
 * e implementa {@link adventure.GameObservable} per notificare gli osservatori sui cambiamenti di stato.
 * Gestisce l'inizializzazione, il salvataggio/caricamento, le interazioni con gli oggetti e le transizioni temporali.
 *
 * @author simona
 */
public class Kronox1914 extends GameDescription implements GameObservable {

    private final List<GameObserver> observers = new ArrayList<>();
    private ParserOutput parserOutput;
    private final List<String> messages = new ArrayList<>();
    private Map<String, ObjectData> objectDataMap;
    private Map<String, AdvObject> liveObjects;
    private Repository<GameState> playerStateRepository;
    private ObjectStateRepository objectStateRepository;
    private SaveSlotRepository saveSlotRepository; // Aggiunto per l'accesso a resetSlot
    private int currentSlotId;
    private List<Room> allRoomsMasterList = new ArrayList<>();
    private boolean isNewGameSession = false;
    private boolean gameEnded = false;
    private String finalMessage = "";
    private Thread atmosphereThread;
    private PrintWriter clientOut;

    // Stati puzzle 2025
    private boolean isBasementLit = false;
    // Stati puzzle 1914
    private final Set<IndizioCriptex> indiziTrovati = EnumSet.noneOf(IndizioCriptex.class);
    private boolean isPastMidday = false;

    /**
     * Inizializza il gioco caricando la configurazione, gli oggetti e le stanze.
     * In base al parametro {@code isNewGame}, carica uno stato esistente dal database
     * o inizializza un nuovo gioco, resettando lo slot e popolando gli oggetti iniziali.
     * Questo metodo imposta anche i comandi disponibili e registra gli osservatori.
     *
     * @param slotId L'ID dello slot di salvataggio da caricare o inizializzare.
     * @param isNewGame {@code true} se si sta iniziando una nuova partita, {@code false} per caricare una partita esistente.
     * @throws GameException Se si verifica un errore durante il caricamento o l'inizializzazione delle risorse di gioco.
     */
    @Override
    public void init(int slotId, boolean isNewGame) throws GameException {
        this.currentSlotId = slotId;
        this.isNewGameSession = isNewGame;

        GameLoader loader = new GameLoader("gamestate/rooms.json", "gamestate/objects.json");
        this.objectDataMap = loader.getObjectDataMap();
        Map<String, RoomData> roomDataMap = loader.getRoomDataMap();
        
        this.liveObjects = new HashMap<>();
        for (Map.Entry<String, ObjectData> entry : objectDataMap.entrySet()) {
            ObjectData objectData = entry.getValue();
            AdvObject liveObject = (objectData.contains != null && !objectData.contains.isEmpty())
                    ? new AdvObjectContainer(objectData.id, objectData.name, objectData.description)
                    : new AdvObject(objectData.id, objectData.name, objectData.description);
            liveObject.setAlias(objectData.aliases);
            liveObject.setPickupable(objectData.pickupable);
            liveObject.setOpenable(objectData.openable);
            liveObject.setPushable(objectData.pushable);
            liveObject.setUseable(objectData.useable);
            liveObject.setImage(objectData.image);
            this.liveObjects.put(entry.getKey(), liveObject);
        }

        this.allRoomsMasterList = new ArrayList<>();
        for (Map.Entry<String, RoomData> entry : roomDataMap.entrySet()) {
            RoomData roomData = entry.getValue();
            Room liveRoom = new Room(roomData.id, entry.getKey(), roomData.name, roomData.description);
            liveRoom.setLook(roomData.look);
            liveRoom.setEpoch(type.GameEpoch.valueOf(roomData.epoch.toUpperCase()));
            liveRoom.setImage(roomData.image);
            liveRoom.setBlockedExits(roomData.blockedExits);
            this.allRoomsMasterList.add(liveRoom);
        }

        // Collega le uscite tra le stanze
        for (RoomData roomData : roomDataMap.values()) {
            Room liveRoom = findRoomById(roomData.id);
            if (liveRoom != null && roomData.exits != null) {
                for (Map.Entry<String, String> exitEntry : roomData.exits.entrySet()) {
                    Room neighbor = findRoomByStringId(exitEntry.getValue(), this.allRoomsMasterList);
                    if (neighbor != null) {
                        switch (exitEntry.getKey().toLowerCase()) {
                            case "nord": liveRoom.setNorth(neighbor); break;
                            case "sud": liveRoom.setSouth(neighbor); break;
                            case "est": liveRoom.setEast(neighbor); break;
                            case "ovest": liveRoom.setWest(neighbor); break;
                            case "giu": liveRoom.setDown(neighbor); break;
                            case "su": liveRoom.setUp(neighbor); break;
                        }
                    }
                }
            }
        }        
        this.saveSlotRepository = new H2SaveSlotRepository();
        this.playerStateRepository = (Repository<GameState>) this.saveSlotRepository;
        this.objectStateRepository = new H2ObjectStateRepository();

        if (this.isNewGameSession) {
            this.saveSlotRepository.resetSlot(this.currentSlotId);
            setCurrentEpoch(type.GameEpoch.PRESENTE);
            setCurrentRoom(getStartingRoomForEpoch(getCurrentEpoch()));
            
            // Inizializza e SALVA lo stato degli oggetti nel DB per la nuova partita
            Map<String, String> initialLocations = new HashMap<>();
            for (RoomData roomData : roomDataMap.values()) {
                if (roomData.objects != null) {
                    for (String objectId : roomData.objects) {
                        initialLocations.put(objectId, "room_" + roomData.id);
                    }
                }
            }
             for (Map.Entry<String, ObjectData> entry : this.objectDataMap.entrySet()) {
                ObjectData objectData = entry.getValue();
                if (objectData.contains != null && !objectData.contains.isEmpty()) {
                    for (String containedId : objectData.contains) {
                        initialLocations.put(containedId, "container_" + objectData.id);
                    }
                }
            }
            objectStateRepository.initNewGameStates(this.currentSlotId, this.objectDataMap, initialLocations);

            loadGameFromInitialState(this.currentSlotId);
            
            this.isBasementLit = false;
            this.indiziTrovati.clear();
            this.isPastMidday = false;
            getInventory().clear();
            
        } else {
            loadGame(this.currentSlotId);
        }
        
        updateWorldToCurrentEpoch();

        // Inizializza i comandi (questa parte rimane invariata)
        getCommands().clear();
        Command nord = new Command(CommandType.NORD, "nord"); nord.setAlias(new String[]{"n"}); getCommands().add(nord);
        Command sud = new Command(CommandType.SOUTH, "sud"); sud.setAlias(new String[]{"s"}); getCommands().add(sud);
        Command est = new Command(CommandType.EAST, "est"); est.setAlias(new String[]{"e"}); getCommands().add(est);
        Command ovest = new Command(CommandType.WEST, "ovest"); ovest.setAlias(new String[]{"o"}); getCommands().add(ovest);
        Command look = new Command(CommandType.LOOK_AT, "osserva"); look.setAlias(new String[]{"guarda", "leggi", "vedi"}); getCommands().add(look);
        Command pickup = new Command(CommandType.PICK_UP, "raccogli"); pickup.setAlias(new String[]{"prendi"}); getCommands().add(pickup);
        Command open = new Command(CommandType.OPEN, "apri"); getCommands().add(open);
        Command use = new Command(CommandType.USE, "usa"); use.setAlias(new String[]{"inserisci", "metti", "utilizza"}); getCommands().add(use);
        Command push = new Command(CommandType.PUSH, "premi"); push.setAlias(new String[]{"spingi", "muovi", "sposta", "addrizza"}); getCommands().add(push);
        Command turnOn = new Command(CommandType.TURN_ON, "accendi"); turnOn.setAlias(new String[]{"attiva", "illumina"}); getCommands().add(turnOn);
        Command goDown = new Command(CommandType.DOWN, "giu"); goDown.setAlias(new String[]{"scendi"}); getCommands().add(goDown);
        Command goUp = new Command(CommandType.UP, "su"); goUp.setAlias(new String[]{"sali"}); getCommands().add(goUp);
        Command inventory = new Command(CommandType.INVENTORY, "inventario"); inventory.setAlias(new String[]{"inv", "i"}); getCommands().add(inventory);
        Command talk = new Command(CommandType.TALK_TO, "parla"); talk.setAlias(new String[]{"chiedi"}); getCommands().add(talk);
        Command end = new Command(CommandType.END, "end"); end.setAlias(new String[]{"esci", "uscire"}); getCommands().add(end);
        Command help = new Command(CommandType.HELP, "aiuto"); help.setAlias(new String[]{"chiedi aiuto", "help"}); getCommands().add(help);
        
        observers.clear();
        attach(new MoveObserver());
        attach(new PickUpObserver());
        attach(new OpenObserver());
        attach(new UseObserver());
        attach(new LightObserver());
        attach(new PushObserver());
        attach(new InventoryObserver());
        attach(new TalkObserver());
        attach(new LookAtObserver());
        
        ActionBinder.bindActions(this);

        AtmosphereThread atmosphereTask = new AtmosphereThread(this, this.clientOut);
        this.atmosphereThread = new Thread(atmosphereTask);
        this.atmosphereThread.setDaemon(true);
        this.atmosphereThread.start();
    }

    /**
     * Sets the PrintWriter used to send messages to the client.
     * This is necessary for the AtmosphereThread to send messages.
     * @param clientOut The PrintWriter connected to the client.
     */
    public void setClientOut(PrintWriter clientOut) {
        this.clientOut = clientOut;
    }
    
    /**
     * Metodo per caricare lo stato iniziale degli oggetti dopo un reset/nuova partita.
     * Questo è simile a loadGame, ma si aspetta che lo stato nel DB sia già quello iniziale.
     * 
     * @param slotId L'ID dello slot di salvataggio da cui caricare lo stato iniziale degli oggetti.
     * @throws GameException Se si verifica un errore durante il caricamento dello stato degli oggetti.
     *
     */
    private void loadGameFromInitialState(int slotId) throws GameException {
        // Carica lo stato degli oggetti e delle loro posizioni dal DB (che dovrebbe essere appena stato resettato/inizializzato)
        Map<String, ObjectState> initialObjectStates = objectStateRepository.loadAllObjectStates(slotId);
        
        // Pulisce le stanze e l'inventario prima di popolarle
        for (Room room : this.allRoomsMasterList) {
            room.getObjects().clear();
        }
        getInventory().clear();

        for (ObjectState state : initialObjectStates.values()) {
            AdvObject liveObject = this.liveObjects.get(state.getObjectId());
            if (liveObject != null) {
                // Applica lo stato iniziale all'oggetto live
                liveObject.setOpenable(state.getOpenable());
                liveObject.setPushable(state.getPushable());
                liveObject.setLocked(state.getLocked());
                
                if (state.getLocation() != null) {
                    if ("inventory".equalsIgnoreCase(state.getLocation())) {
                        getInventory().add(liveObject);
                    } else if (state.getLocation().startsWith("room_")) {
                        int roomId = Integer.parseInt(state.getLocation().replace("room_", ""));
                        Room room = findRoomById(roomId);
                        if (room != null) room.getObjects().add(liveObject);
                    } else if (state.getLocation().startsWith("container_")) {
                        int containerId = Integer.parseInt(state.getLocation().replace("container_", ""));
                        AdvObject container = findObjectById(containerId);
                        if (container instanceof AdvObjectContainer) {
                            ((AdvObjectContainer) container).add(liveObject);
                        }
                    }
                }
            }
        }
        // Per una nuova partita, lo stato del giocatore è già stato impostato nel blocco init (setCurrentRoom, setCurrentEpoch)
    }

    /**
     * Carica lo stato completo del gioco da uno slot di salvataggio esistente.
     * Popola le stanze e l'inventario con gli oggetti nelle loro posizioni salvate
     * e ripristina lo stato del giocatore e i flag dei puzzle.
     *
     * @param slotId L'ID dello slot di salvataggio da cui caricare il gioco.
     * @throws GameException Se si verifica un errore durante il caricamento dello stato del gioco o degli oggetti.
     */
    private void loadGame(int slotId) throws GameException {
        // Carica lo stato degli oggetti e delle loro posizioni
        Map<String, ObjectState> loadedObjectStates = objectStateRepository.loadAllObjectStates(slotId);
        
        // Resetta lo stato degli oggetti e delle stanze prima di ricaricare
        // Questo è importante per evitare che oggetti di una partita precedente rimangano.
        for (Room room : this.allRoomsMasterList) {
            room.getObjects().clear(); // Svuota gli oggetti dalle stanze
        }
        getInventory().clear(); // Svuota l'inventario prima di caricare

        for (ObjectState state : loadedObjectStates.values()) {
            AdvObject liveObject = this.liveObjects.get(state.getObjectId());
            if (liveObject != null) {
                liveObject.setOpenable(state.getOpenable());
                liveObject.setPushable(state.getPushable());
                liveObject.setLocked(state.getLocked());
                if (state.getLocation() != null) {
                    if ("inventory".equalsIgnoreCase(state.getLocation())) {
                        getInventory().add(liveObject);
                    } else if (state.getLocation().startsWith("room_")) {
                        int roomId = Integer.parseInt(state.getLocation().replace("room_", ""));
                        Room room = findRoomById(roomId);
                        if (room != null) room.getObjects().add(liveObject);
                    } else if (state.getLocation().startsWith("container_")) {
                        int containerId = Integer.parseInt(state.getLocation().replace("container_", ""));
                        AdvObject container = findObjectById(containerId);
                        if (container instanceof AdvObjectContainer) {
                            ((AdvObjectContainer) container).add(liveObject);
                        }
                    }
                }
            }
        }

        // Carica lo stato del giocatore (posizione, epoch, flag di puzzle)
        Optional<GameState> loadedPlayerState = this.playerStateRepository.load(slotId);
        if (loadedPlayerState.isPresent()) {
            GameState gs = loadedPlayerState.get();
            setCurrentEpoch(gs.getCurrentEpoch());
            this.isBasementLit = gs.isBasementLit();
            this.indiziTrovati.clear();
            if (gs.isFoundP()){ this.indiziTrovati.add(IndizioCriptex.P); }
            if (gs.isFoundA()){ this.indiziTrovati.add(IndizioCriptex.A); }
            if (gs.isFoundX()){ this.indiziTrovati.add(IndizioCriptex.X); }
            this.isPastMidday = gs.isPastMidday();
            setCurrentRoom(findRoomByStringId(gs.getLocationId(), this.allRoomsMasterList));
        } else {
             // Se lo slot non ha dati salvati, inizia da una stanza predefinita
             setCurrentEpoch(type.GameEpoch.PRESENTE);
             setCurrentRoom(getStartingRoomForEpoch(getCurrentEpoch()));
        }
    }

    /**
     * Esegue la prossima mossa del gioco basandosi sull'output del parser.
     * Questo metodo notifica gli osservatori per gestire i comandi specifici
     * e si occupa del salvataggio dello stato del gioco dopo ogni mossa.
     *
     * @param p L'oggetto {@link parser.ParserOutput} che contiene il comando parsato e i suoi elementi.
     * @param out Il {@link java.io.PrintWriter} per inviare output al client.
     */
    @Override
    public void nextMove(ParserOutput p, PrintWriter out) {
        parserOutput = p;
        messages.clear();
        if (p.getCommand() == null) { out.println("Non ho capito cosa devo fare! Prova con un altro comando."); return; }
        if ("aiuto".equalsIgnoreCase(p.getCommand().getName())){
            out.println("SHOW_HELP_COMMANDS");
            return;
        }
        
        notifyObservers();

        if (getCurrentRoom() == null) {
            if (!messages.isEmpty()) messages.forEach(m -> { if (m != null && !m.isEmpty()) out.println(m); });
            return;
        }

        if (!messages.isEmpty()) {
            messages.forEach(m -> { if (m != null && !m.isEmpty()) out.println(m); });
        }

        try {
            GameState currentState = new GameState(
                getCurrentRoom().getStringId(), 
                getCurrentEpoch(), 
                this.isBasementLit, 
                this.isIndizio(IndizioCriptex.P),
                this.isIndizio(IndizioCriptex.A),
                this.isIndizio(IndizioCriptex.X),
                this.isPastMidday
            );
            playerStateRepository.save(this.currentSlotId, currentState);
            
            Map<String, String> objectLocations = new HashMap<>();
            for (Room room : this.allRoomsMasterList) {
                if (room.getObjects() != null) {
                    for (AdvObject obj : room.getObjects()) {
                        objectLocations.put(findIdByObject(obj), "room_" + room.getId());
                    }
                }
            }
            if (getInventory() != null) {
                for (AdvObject obj : getInventory()) {
                    objectLocations.put(findIdByObject(obj), "inventory");
                }
            }
             for (AdvObject obj : this.liveObjects.values()) {
                if (obj instanceof AdvObjectContainer) {
                    AdvObjectContainer container = (AdvObjectContainer) obj;
                    if (container.getList() != null) {
                        for (AdvObject contained : container.getList()) {
                            objectLocations.put(findIdByObject(contained), "container_" + obj.getId());
                        }
                    }
                }
            }
            objectStateRepository.saveAllObjectStates(this.currentSlotId, this.liveObjects, objectLocations);
        } catch (GameException e) { 
            System.err.println("Errore di salvataggio: " + e.getMessage());
        }
    }

    /**
     * Restituisce la stanza di partenza predefinita per una data epoca.
     *
     * @param epoch La {@link java.lang.String} che rappresenta l'epoca desiderata (es. "presente", "passato").
     * @return L'oggetto {@link type.Room} che è la stanza di partenza per l'epoca specificata.
     */
    @Override
    public Room getStartingRoomForEpoch(type.GameEpoch epoch) {
        String startingRoomId;
        switch(epoch) {
            case PASSATO: startingRoomId = "ingresso_ambasciata_passato"; break;
            case PRESENTE: startingRoomId = "ufficio_assistente_presente"; break;
            default: startingRoomId = "ufficio_assistente_presente";
        }
        return findRoomByStringId(startingRoomId, this.allRoomsMasterList);
    }
    
    /**
     * Aggiorna la lista delle stanze disponibili nel gioco in base all'epoca corrente.
     * Filtra la lista master di tutte le stanze per includere solo quelle
     * appartenenti all'{@code currentEpoch}.
     */
    @Override public void updateWorldToCurrentEpoch() { 
        setRooms(this.allRoomsMasterList.stream()
                .filter(r -> this.getCurrentEpoch().equals(r.getEpoch()))
                .collect(Collectors.toList())); 
    }

    /**
     * Trova un oggetto di avventura tramite il suo ID numerico tra tutti gli oggetti "live" del gioco.
     *
     * @param id L'ID numerico dell'oggetto da cercare.
     * @return L'oggetto {@link type.AdvObject} trovato con l'ID specificato, o {@code null} se non trovato.
     */
    @Override public AdvObject findObjectById(int id) { for (AdvObject obj : this.liveObjects.values()) { if (obj.getId() == id) return obj; } return null; }
    
    /**
     * Trova una stanza tramite il suo ID numerico tra tutte le stanze master del gioco.
     *
     * @param id L'ID numerico della stanza da cercare.
     * @return L'oggetto {@link type.Room} trovato con l'ID specificato, o {@code null} se non trovato.
     */
    public Room findRoomById(int id) { 
        for (Room room : this.allRoomsMasterList) {
            if (room.getId() == id) return room;
        }
        return null;
    }    
    
    private Room findRoomByStringId(String stringId, List<Room> roomList) { if (stringId == null) return null; for (Room room : roomList) if (stringId.equalsIgnoreCase(room.getStringId())) return room; return null; }
    private String findIdByObject(AdvObject obj) { if (obj == null) return null; for (Map.Entry<String, ObjectData> entry : this.objectDataMap.entrySet()) if (entry.getValue().id == obj.getId()) return entry.getKey(); return null; }

    /**
     * Aggiunge un osservatore alla lista degli osservatori di questo gioco.
     * L'osservatore sarà notificato ogni volta che si verifica una mossa del giocatore.
     *
     * @param o L'oggetto {@link adventure.GameObserver} da aggiungere.
     */
    @Override public void attach(GameObserver o) { if (!observers.contains(o)) observers.add(o); }

    /**
     * Rimuove un osservatore dalla lista degli osservatori di questo gioco.
     * L'osservatore non riceverà più notifiche.
     *
     * @param o L'oggetto {@link adventure.GameObserver} da rimuovere.
     */
    @Override public void detach(GameObserver o) { observers.remove(o); }

    /**
     * Notifica tutti gli osservatori registrati riguardo alla mossa corrente del giocatore.
     * Ogni osservatore ha l'opportunità di elaborare il comando parsato
     * e può aggiungere messaggi alla lista dei messaggi del gioco.
     * Se un osservatore lancia una {@link exceptions.ActionException}, il ciclo si interrompe
     * e il messaggio dell'eccezione viene aggiunto.
     */
    @Override public void notifyObservers() { 
        for (GameObserver o : observers) try {
            String result = o.update(this, parserOutput);
            if (result != null && !result.isEmpty()){
                messages.add(result);
            }
            } catch (ActionException e) {
                messages.add(e.getMessage());
                //Interrompe il ciclo e non processa gli altri observer
                break;
            } 
    }

    /**
     * Restituisce il messaggio di benvenuto del gioco.
     * Il messaggio varia a seconda che si tratti di una nuova partita o di un caricamento.
     *
     * @return Una {@link java.lang.String} contenente il messaggio di benvenuto.
     */
    @Override public String getWelcomeMsg() { 
        if (this.isNewGameSession){
            return "La pioggia batte leggera contro le finestre del Dipartimento di Informatica, un metronomo malinconico in una serata che segna la fine di un'era: la tua.\n\nÈ quasi ora di chiusura e i corridoi, un tempo affollati di studenti, sono ora vuoti e silenziosi, illuminati solo dalle luci di emergenza e dal ronzio spettrale dei server nelle stanze vicine.\n\nSei seduto alla tua vecchia scrivania, quella del giovane assistente di ricerca, e stai mettendo via le tue cose per l'ultima volta.\n\nIl Professor Elia Vellini non c'è più.\n\nSe n'è andato la settimana scorsa, in modo tanto improvviso quanto enigmatico era stato in vita. Un brillante informatico con un'insaziabile, quasi anacronistica, passione per la Storia. I suoi colleghi lo consideravano un eccentrico, un uomo che cercava di decifrare il passato con algoritmi, che riempiva il suo ufficio di mappe antiche e tomi polverosi accanto a cluster di calcolo all'avanguardia.\n\nMa tu eri diverso. Tu eri il suo assistente, il suo confidente, l'unico che intravedeva un barlume di genio nella sua ossessione per i punti di svolta della storia, per i momenti in cui il destino del mondo è stato deciso da un singolo evento. E ora, sembra che tu sia l'unico erede di quella follia.\n\nDavanti a te, sulla scrivania, c'è la prova. Una busta con il tuo nome scritto con la sua inconfondibile calligrafia elegante. Il suo ultimo messaggio, la sua ultima volontà. L'hai lasciata per ultima, quasi temendo cosa potesse contenere.\n\nÈ ora di scoprire la verità.\n\n";
        } else {
            return "Bentornato!";
        }
    }

    /**
     * Gestisce il viaggio nel tempo, cambiando l'epoca corrente del gioco
     * e posizionando il giocatore in una stanza specifica della nuova epoca.
     * Aggiorna anche il mondo di gioco per riflettere la nuova epoca.
     *
     * @param newEpoch La {@link java.lang.String} che rappresenta la nuova epoca di destinazione.
     * @param startingRoomId La {@link java.lang.String} che rappresenta l'ID della stanza di partenza nella nuova epoca.
     * @throws GameException Se si verifica un errore durante il viaggio nel tempo (es. stanza non trovata).
     */
    @Override public void timeTravel(type.GameEpoch newEpoch, String startingRoomId) throws GameException { setCurrentEpoch(newEpoch); updateWorldToCurrentEpoch(); setCurrentRoom(findRoomByStringId(startingRoomId, this.allRoomsMasterList)); }

    /**
     * Verifica se la luce dello scantinato è accesa.
     * @return {@code true} se la luce è accesa, {@code false} altrimenti.
     */
    public boolean isBasementLit() { return isBasementLit; }

    /**
     * Imposta lo stato della luce dello scantinato.
     * @param lit {@code true} per accendere la luce, {@code false} per spegnerla.
     */
    public void setBasementLit(boolean lit) { this.isBasementLit = lit; }

    /**
     * Verifica se il gioco nell'epoca 1914 è passato il mezzogiorno.
     * @return {@code true} se è passato mezzogiorno, {@code false} altrimenti.
     */
    public boolean isPastMidday() { return isPastMidday; }

    /**
     * Imposta lo stato del mezzogiorno nell'epoca 1914.
     * @param past {@code true} se è passato mezzogiorno, {@code false} altrimenti.
     */
    public void setPastMidday(boolean past) { this.isPastMidday = past; }

    /**
     * Imposta lo stato di fine del gioco e il messaggio finale da mostrare.
     *
     * @param end Un valore booleano che indica se il gioco è terminato ({@code true}) o meno ({@code false}).
     * @param finalMessage La {@link java.lang.String} che contiene il messaggio da mostrare alla fine del gioco.
     */
    @Override
    public void setEnd(boolean end, String finalMessage) {
        this.gameEnded = end;
        this.finalMessage = finalMessage;
    }

    /**
     * Verifica se il gioco è terminato.
     * @return {@code true} se il gioco è terminato, {@code false} altrimenti.
     */
    @Override public boolean isEnd() {return this.gameEnded;}

    /**
     * Restituisce il messaggio finale del gioco, che viene mostrato alla sua conclusione.
     * @return Una {@link java.lang.String} contenente il messaggio finale.
     */
    @Override public String getFinalMessage() {return this.finalMessage;}

    /**
     * Termina la sessione di gioco corrente.
     * Interrompe il thread dell'atmosfera e chiude la connessione al database.
     */
    @Override
    public void endGame() {
        try {
            if (this.atmosphereThread != null){
                this.atmosphereThread.interrupt();
            }
            database.DBManager.closeConnection();
            System.out.println("Connessione al DB chiusa. Gioco terminato.");
        } catch (Exception e) {
            System.err.println("Errore durante la chiusura del gioco: " + e.getMessage());
        }    
    }

    /**
     * Trova un oggetto di avventura tramite il suo ID stringa (chiave nella mappa {@code liveObjects}).
     *
     * @param id L'ID stringa dell'oggetto da cercare (es. "lettera_professore").
     * @return L'oggetto {@link type.AdvObject} trovato, o {@code null} se l'ID è nullo o l'oggetto non è trovato.
     */
    @Override
    public AdvObject findObjectByStringId(String id) {
        if (id == null){
            return null;
        }
        return this.liveObjects.get(id);
    }
    
    /**
     * Aggiunge un indizio {@link type.IndizioCriptex} alla collezione degli indizi trovati.
     * Questo è usato per tenere traccia dei progressi nei puzzle che richiedono indizi.
     *
     * @param indizio L'enumerazione {@link type.IndizioCriptex} dell'indizio da aggiungere.
     */
    public void addIndizio(IndizioCriptex indizio){
        this.indiziTrovati.add(indizio);
    }
    
    /**
     * Verifica se un determinato indizio {@link type.IndizioCriptex} è stato trovato.
     *
     * @param indizio L'enumerazione {@link type.IndizioCriptex} dell'indizio da verificare.
     * @return {@code true} se l'indizio è stato trovato, {@code false} altrimenti.
     */
    public boolean isIndizio(IndizioCriptex indizio){
        return this.indiziTrovati.contains(indizio);
    }
    
    /**
     * Restituisce il set di tutti gli indizi {@link type.IndizioCriptex} che sono stati trovati finora.
     *
     * @return Un {@link java.util.Set} di {@link type.IndizioCriptex}.
     */
    public Set<IndizioCriptex> getIndiziTrovati(){
        return this.indiziTrovati;
    }

    /**
     * Aggiunge un messaggio alla lista dei messaggi da inviare al client.
     * I messaggi vengono accumulati e poi inviati in blocco dopo l'elaborazione di una mossa.
     *
     * @param message La {@link java.lang.String} del messaggio da aggiungere.
     */
    public void addMessage(String message) {
        this.messages.add(message);
    }
}