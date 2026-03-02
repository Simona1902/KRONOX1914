package adventure;

import adventure.ui.GameUI;
import adventure.ui.ImageDialogUI;
import adventure.ui.InventoryDialogUI;
import adventure.ui.ShowHelpCommandsDialogUI;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import sound.MusicPlayer;

/**
 * La classe Client gestisce la connessione con il server di gioco,
 * l'interfaccia utente e l'elaborazione dei comandi
 * @author simona
 */
public class Client {

    private final String serverAddress = "127.0.0.1";
    private final int serverPort = 12345;
    private GameUI gameUI;
    private Socket socket;
    private PrintWriter out;
    private enum GameState {MENU, INTRO, PLAYING, OUTRO, TIMETRAVEL_SEQUENCE}
    private GameState currentState = GameState.MENU;

    private volatile boolean intentionalDisconnect = false;
    
    /**
     * Metodo principale che avvia il client.
     * @param args Argomenti della riga di comando
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client().start());
    }

    private void start() {
        
        gameUI = new GameUI(this);
        gameUI.showIntroScreen();
        gameUI.setVisible(true);

        new Thread(new MusicPlayer("sounds/colonna_sonora.wav")).start();
        
        connectAndPlay();
    }

    private void connectAndPlay() {
        intentionalDisconnect = false; 
        
        new Thread(() -> {
            try {
                socket = new Socket(serverAddress, serverPort);
                out = new PrintWriter(socket.getOutputStream(), true);
                try (Scanner serverIn = new Scanner(socket.getInputStream())) {
                    listenToServer(serverIn);
                }
            } catch (IOException e) {
                if (!intentionalDisconnect) {
                   gameUI.appendOutput("ERRORE: Impossibile connettersi al server.", Color.RED);
                }
            }
        }).start();
    }
    
    private void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Errore durante la disconnessione: " + e.getMessage());
        }
    }
    
    /**
     * Gestisce il ritorno al menu principale di gioco.
     */
    public void returnToMenu() {
        intentionalDisconnect = true;
        disconnect();
        gameUI.showIntroScreen(); 
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        connectAndPlay();
    }

    /**
     * Termina l'applicazione client.
     */
    public void exitApplication() {
        if (out != null) {
            out.println("end");
        }
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        System.exit(0);
    }

    /**
     * Elabora l'input dell'utente basandosi sullo stato corrente del gioco.
     * Gestisce comandi speciali come 'inventario' e transizioni di stato.
     * @param command Il comando inserito dall'utente.
     */
    public void processUserInput(String command) {
        if (currentState == GameState.INTRO) {
            sendCommand("start_game");
            currentState = GameState.PLAYING;
            return;
        }

        if (currentState == GameState.OUTRO) {
            exitApplication(); 
            return;
        }
        
        if (currentState == GameState.TIMETRAVEL_SEQUENCE) {
            sendCommand("request_room_update");
            gameUI.showGameScreen();
            currentState = GameState.PLAYING;
            return;
        }

        String trimmedCmd = command.trim().toLowerCase();
        if ("inventario".equals(trimmedCmd) || "inv".equals(trimmedCmd) || "i".equals(trimmedCmd)) {
            sendCommand("inventario");
            return;
        }

        if (!"esci".equals(trimmedCmd)) {
             gameUI.appendOutput("--> " + command, Color.WHITE);
        }

        sendCommand(command);
    }
    
    /**
     * Invia un comando testuale al server.
     * Il comando viene inviato solo se la connessione di output (`out`) è attiva.
     * @param command Il comando da inviare al server.
     */
    public void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        }
    }
    
    private void listenToServer(Scanner serverIn) {
        while (serverIn.hasNextLine()) {
            String serverMessage = serverIn.nextLine();
            
            if (serverMessage.startsWith("SHOW_INTRO:")){
                currentState = GameState.INTRO;
                gameUI.showFullScreenText(serverMessage.substring("SHOW_INTRO:".length()));
                gameUI.appendOutput("\n\n[Premi Invio per iniziare]", Color.CYAN);
            } else if (serverMessage.startsWith("SHOW_OUTRO:")){
                currentState = GameState.OUTRO;
                gameUI.showFullScreenText(serverMessage.substring("SHOW_OUTRO:".length()));
                gameUI.appendOutput("\n\n[Premi Invio per terminare il gioco]", Color.CYAN);
            } else if (serverMessage.startsWith("SHOW_NARRATIVE_SCENE:")) {
                currentState = GameState.TIMETRAVEL_SEQUENCE; 
                gameUI.showFullScreenText(serverMessage.substring("SHOW_NARRATIVE_SCENE:".length()));
                gameUI.appendOutput("\n\n[Premi Invio per continuare]", Color.CYAN);
            } else if (serverMessage.equals("START_TIMETRAVEL_SEQUENCE")){
                String html = serverIn.nextLine();
                gameUI.appendOutput("PASSWORD ACCETTATA. PROTOCOLLO CONFERMATO.", new Color(255, 153, 0));
                javax.swing.Timer timer = new javax.swing.Timer(4000, (e) -> {
                    currentState = GameState.TIMETRAVEL_SEQUENCE;
                    gameUI.showFullScreenText(html);
                    gameUI.appendOutput("\n\n[Premi Invio per continuare]", Color.CYAN);
                });
                timer.setRepeats(false);
                timer.start();
            } else if (serverMessage.startsWith("INVENTORY_DATA:")) {
                String data = serverMessage.substring("INVENTORY_DATA:".length());
                List<String[]> items = new ArrayList<>();
                if (!data.isEmpty()) {
                    String[] itemPairs = data.split(",");
                    for (String pair : itemPairs) {
                        String[] itemData = pair.split("\\|", 2);
                        if (itemData.length == 2) {
                            items.add(itemData);
                        }
                    }
                }
                new InventoryDialogUI(gameUI, items).setVisible(true);
            } else if ("ROOM_UPDATE".equals(serverMessage)) {
                String name = serverIn.nextLine();
                String description = serverIn.nextLine();
                String imageLine = serverIn.nextLine();
                String imagePath = null;
                if (imageLine.startsWith("IMAGE:")) {
                    imagePath = imageLine.substring(6);
                }
                gameUI.updateRoom(name, description, imagePath);
            } else if ("SHOW_IMAGE".equals(serverMessage)) {
                String objectName = serverIn.nextLine();
                String imagePath = serverIn.nextLine();
                String description = serverIn.nextLine();
                gameUI.appendOutput(description, new Color(255, 153, 0));
                SwingUtilities.invokeLater(() -> {
                    new ImageDialogUI(gameUI, objectName, imagePath).setVisible(true);
                });
            } else if ("DELAYED_ROOM_UPDATE".equals(serverMessage)) {
                String name = serverIn.nextLine();
                String description = serverIn.nextLine();
                String imageLine = serverIn.nextLine();
                String imagePath = imageLine.startsWith("IMAGE:") ? imageLine.substring(6) : null;
                
                javax.swing.Timer timer = new javax.swing.Timer(4000, (e) -> {
                    gameUI.updateRoom(name, description, imagePath);
                });
                timer.setRepeats(false);
                timer.start();
            } else if ("SHOW_HELP_COMMANDS".equals(serverMessage)) {
                // Esegui sulla EDT (Event Dispatch Thread) per interazioni Swing
                SwingUtilities.invokeLater(() -> {
                    // gameUI è il JFrame principale, quindi lo passiamo come genitore
                    new ShowHelpCommandsDialogUI(gameUI, true).setVisible(true);
                });
            } else if (serverMessage.startsWith("ATMOS_MESSAGE:")){
                String content = serverMessage.substring("ATMOS_MESSAGE:".length());
                gameUI.appendOutput(content, Color.CYAN);
            
            } else if (!serverMessage.trim().equals("-->") && !serverMessage.trim().isEmpty()) {
                gameUI.appendOutput(serverMessage, new Color(255, 153, 0));
            }
        }
        
        if (!intentionalDisconnect) {
            gameUI.appendOutput("[Connessione con il server terminata.]", Color.GRAY);
        }
    }
}