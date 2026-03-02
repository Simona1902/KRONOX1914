package adventure.ui;

import adventure.Client;
import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * La classe GameUI rappresenta l'interfaccia grafica principale del gioco.
 * Estende {@link javax.swing.JFrame} e gestisce la visualizzazione delle stanze,
 * l'output del testo, l'input dell'utente e gli elementi grafici.
 * @author simona
 */
public class GameUI extends javax.swing.JFrame {
    
    private final Client client;
    private final Color TEXT_COLOR = new Color(255, 153, 0);
    private javax.swing.Timer typewriterTimer;
    /**
     * Crea una nuova istanza della form GameUI.
     * Inizializza i componenti dell'interfaccia utente e configura i listener
     * per la chiusura della finestra e la barra di scorrimento.
     *
     * @param client L'istanza del {@link Client} a cui questa UI è collegata.
     */
    public GameUI(Client client) {
        this.client = client;
        initComponents();  //Chiama il codice generato dal designer
        outputArea.setFocusable(true);
        javax.swing.JScrollBar verticalScrollBar = outputScrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
            // Le righe seguenti nascondono le frecce su/giù per un look più pulito
            @Override
            protected javax.swing.JButton createDecreaseButton(int orientation) {
                javax.swing.JButton button = new javax.swing.JButton();
                button.setPreferredSize(new java.awt.Dimension(0, 0));
                return button;
            }
            @Override
            protected javax.swing.JButton createIncreaseButton(int orientation) {
                javax.swing.JButton button = new javax.swing.JButton();
                button.setPreferredSize(new java.awt.Dimension(0, 0));
                return button;
            }
        });
        this.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override public void windowClosing(java.awt.event.WindowEvent e){
                client.exitApplication();
            }
        });  
    }
    
    /**
     * Visualizza un menu HTML nell'area di output.
     * 
     * @param html La stringa HTML da visualizzare come contenuto del menu
     */
    public void displayMenu(String html) { 
        SwingUtilities.invokeLater(() -> { outputArea.setText(html); 
        outputArea.setCaretPosition(0); }); 
    }
    
    /**
     * Mostra la schermata introduttiva del gioco, inclusi il nome del gioco,
     * un messaggio di benvenuto e un'immagine di sfondo.
     */
    public final void showIntroScreen() { 
        SwingUtilities.invokeLater(() -> { roomNameLabel.setText("KRONOX 1914"); 
            displayMenu("<html><body style='font-family: Colonna MT, sans-serif; font-size:20pt; color:#660066; background-color:#000000; text-align: center;'><b>Benvenuto!<br>Scegli uno slot di salvataggio.</b></body></html>"); 
            setRoomImage("images/intro.png"); 
        }); 
    }
    
    /**
     * Aggiunge testo all'area di output, formattandolo con il colore specificato.
     * Il testo viene aggiunto come un nuovo paragrafo HTML.
     * 
     * @param text La stringa di testo da aggiungere.
     * @param color Il {@link java.awt.Color} del testo da aggiungere.
     */
    public void appendOutput(String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            try {
                HTMLEditorKit kit = (HTMLEditorKit) outputArea.getEditorKit();
                String colorHex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                String htmlToInsert = "<p style='font-family: Consolas; font-size:12pt; margin-top: 5px; border-top: 1px solid #555; padding-top: 5px; color:" + colorHex + ";'>" + text + "</p>";
                kit.insertHTML((javax.swing.text.html.HTMLDocument) outputArea.getDocument(), outputArea.getDocument().getLength(), htmlToInsert, 0, 0, null);
            } catch (javax.swing.text.BadLocationException | java.io.IOException ex) { 
                outputArea.setText(outputArea.getText() + "\n" + text); 
            }
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    /**
     * Cancella tutto il testo dall'area di output.
     */
    public void clearOutput() { 
        SwingUtilities.invokeLater(() -> outputArea.setText("")); 
    }
    
    /**
     * Aggiorna la visualizzazione della stanza corrente con un nuovo nome, descrizione e immagine.
     * Se la schermata di gioco non è visibile, la abilita.
     * La descrizione viene visualizzata con un effetto "macchina da scrivere".
     * 
     * @param name Il nome della stanza da visualizzare.
     * @param description La descrizione della stanza da visualizzare.
     * @param imagePath Il percorso dell'immagine della stanza (può essere {@code null} o vuoto).
     */
    public void updateRoom(String name, String description, String imagePath) {
        SwingUtilities.invokeLater(() -> {
            if (!isGameScreenVisible()){
                showGameScreen();
            }
            roomNameLabel.setText(name);
            typewriterAppendOutput(description.replace("\n", "<br>"), TEXT_COLOR);
            setRoomImage(imagePath);
        });
    }
    private void setRoomImage(String path) {
        if (path == null || path.isEmpty()) {
            imageLabel.setIcon(null);
            imageLabel.setText("Nessuna immagine per questa stanza.");
            imageLabel.setForeground(TEXT_COLOR);
            return;
        }

        InputStream imgStream = null;
        try {
            imgStream = getClass().getClassLoader().getResourceAsStream(path);
            if (imgStream == null) {
                throw new java.io.FileNotFoundException("Risorsa non trovata: " + path);
            }
            Image image = ImageIO.read(imgStream);
            int newWidth = 600;
            int newHeight = (int) (((double) newWidth / image.getWidth(null)) * image.getHeight(null));
            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setText("");
        } catch (java.io.IOException e) {
            imageLabel.setIcon(null);
            imageLabel.setText("Immagine non trovata: " + path);
            imageLabel.setForeground(TEXT_COLOR);
            System.err.println("Errore I/O caricamento immagine: " + e.getMessage());
        } finally {
            if (imgStream != null) {
                try {
                    imgStream.close();
                } catch (java.io.IOException e) {
                    // Ignora eccezione sulla chiusura
                }
            }
        }
    }
    
    /**
     * Verifica se la schermata di gioco standard (con immagine e area di output sotto) è visibile.
     * 
     * @return {@code true} se la schermata di gioco è visibile, {@code false} altrimenti.
     */
    public boolean isGameScreenVisible() {
        return imageLabel.getParent() == jPanelCentro;
}

    /**
     * Mostra un testo HTML a schermo intero nell'area di output, nascondendo l'immagine della stanza.
     * Utile per visualizzare scene narrative estese o messaggi importanti.
     * 
     * @param html La stringa HTML da visualizzare a schermo intero.
     */
    public void showFullScreenText(String html) {
        SwingUtilities.invokeLater(() -> {
            // Riconfigura il layout per lo schermo intero
            jPanelCentro.remove(imageLabel);
            jPanelCentro.remove(outputScrollPane);
            jPanelCentro.add(outputScrollPane, java.awt.BorderLayout.CENTER);

            // Imposta il testo istantaneamente
            outputArea.setText(html);
            outputArea.setCaretPosition(0); // Assicura che si veda dall'inizio

            // Aggiorna la UI per mostrare le modifiche
            jPanelCentro.revalidate();
            jPanelCentro.repaint();
        });
    }

    /**
     * Ripristina la visualizzazione standard della schermata di gioco,
     * con l'immagine della stanza in alto e l'area di output in basso.
     * Riabilita anche i campi di input e i pulsanti.
     */
    public void showGameScreen() {
        SwingUtilities.invokeLater(() -> {
            // Rimuovi l'area di testo dalla posizione centrale
            jPanelCentro.remove(outputScrollPane);

            // Rimetti i componenti nelle loro posizioni originali
            jPanelCentro.add(imageLabel, java.awt.BorderLayout.CENTER);
            // La costante per il posizionamento in basso è SOUTH
            jPanelCentro.add(outputScrollPane, java.awt.BorderLayout.SOUTH);

            // Abilita nuovamente l'input
            inputField.setEnabled(true);
            sendButton.setEnabled(true);
            inputField.requestFocusInWindow();
        
            // Aggiorna la UI per mostrare le modifiche
            jPanelCentro.revalidate();
            jPanelCentro.repaint();
        });
    }

    /**
     * Aggiunge testo nell'area di output con effetto "macchina da scrivere" (typewriter).
     * Durante l'effetto, i pulsanti di input sono disabilitati. L'utente può premere
     * un tasto o cliccare per saltare l'animazione e visualizzare il testo completo.
     * @param text Il testo da visualizzare con l'effetto.
     * @param color Il {@link java.awt.Color} del testo
     */
    public void typewriterAppendOutput(String text, Color color) {
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
        }

        final String fullText = text;
        final StringBuilder currentText = new StringBuilder();
        final int delay = 35;
        final String colorHex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        final String finalHtml = "<html><body style='font-family: Segoe UI, sans-serif; font-size: 15pt; margin: 5px; color:" + colorHex + ";'>"
                             + fullText + "</body></html>";

        sendButton.setEnabled(false);
        inputField.setEnabled(false);
        inventoryButton.setEnabled(false);
        Menu.setEnabled(false);
        exitButton.setEnabled(false); 
     
        final java.awt.event.KeyAdapter keySkipper = new java.awt.event.KeyAdapter(){
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                typewriterTimer.stop();
                outputArea.setText(finalHtml);
                sendButton.setEnabled(true);
                inputField.setEnabled(true);
                inventoryButton.setEnabled(true);
                Menu.setEnabled(true);
                exitButton.setEnabled(true);
                inputField.requestFocusInWindow();
                outputArea.removeKeyListener(this);
            }
        };
        final java.awt.event.MouseAdapter mouseSkipper = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                typewriterTimer.stop();
                outputArea.setText(finalHtml);
                sendButton.setEnabled(true);
                inputField.setEnabled(true);
                inventoryButton.setEnabled(true);
                Menu.setEnabled(true);
                exitButton.setEnabled(true);
                inputField.requestFocusInWindow();
                outputScrollPane.getViewport().removeMouseListener(this);
                outputArea.removeKeyListener(keySkipper);
            }
        };

        outputScrollPane.getViewport().addMouseListener(mouseSkipper);
        outputArea.addKeyListener(keySkipper);

        outputArea.requestFocusInWindow();

        java.awt.event.ActionListener taskPerformer = new java.awt.event.ActionListener() {
            private int charIndex = 0;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (charIndex < fullText.length()) {
                    currentText.append(fullText.charAt(charIndex));
                    String htmlToShow = "<html><body style='font-family: Segoe UI, sans-serif; font-size: 15pt; margin: 5px; color:" + colorHex + ";'>"
                                  + currentText.toString() + "</body></html>";
                    outputArea.setText(htmlToShow);
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                    charIndex++;
                } else {
                    typewriterTimer.stop();
                    sendButton.setEnabled(true);
                    inputField.setEnabled(true);
                    inventoryButton.setEnabled(true);
                    Menu.setEnabled(true);
                    exitButton.setEnabled(true);
                    inputField.requestFocusInWindow();
                    outputScrollPane.getViewport().removeMouseListener(mouseSkipper);
                    outputArea.removeKeyListener(keySkipper);
                }
            }
        };
    
        typewriterTimer = new javax.swing.Timer(delay, taskPerformer);
        typewriterTimer.setInitialDelay(0);
        typewriterTimer.start();
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNord = new javax.swing.JPanel();
        roomNameLabel = new javax.swing.JLabel();
        jPanelSud = new javax.swing.JPanel();
        inputField = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        sendButton = new javax.swing.JButton();
        inventoryButton = new javax.swing.JButton();
        Menu = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        jPanelCentro = new javax.swing.JPanel();
        imageLabel = new javax.swing.JLabel();
        outputScrollPane = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Kronox - Adventure");
        setBackground(new java.awt.Color(0, 0, 0));
        setResizable(false);

        jPanelNord.setOpaque(false);
        jPanelNord.setLayout(new java.awt.BorderLayout());

        roomNameLabel.setBackground(new java.awt.Color(0, 0, 0));
        roomNameLabel.setFont(new java.awt.Font("Colonna MT", 1, 28)); // NOI18N
        roomNameLabel.setForeground(new java.awt.Color(255, 133, 11));
        roomNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        roomNameLabel.setOpaque(true);
        jPanelNord.add(roomNameLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelNord, java.awt.BorderLayout.NORTH);

        jPanelSud.setBackground(new java.awt.Color(255, 255, 255));
        jPanelSud.setOpaque(false);
        jPanelSud.setLayout(new java.awt.BorderLayout());

        inputField.setBackground(new java.awt.Color(255, 255, 255));
        inputField.setFont(new java.awt.Font("Consolas", 0, 16)); // NOI18N
        inputField.setForeground(new java.awt.Color(0, 0, 0));
        inputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputFieldActionPerformed(evt);
            }
        });
        jPanelSud.add(inputField, java.awt.BorderLayout.CENTER);

        buttonPanel.setBackground(new java.awt.Color(51, 51, 51));
        buttonPanel.setForeground(new java.awt.Color(255, 255, 255));

        sendButton.setBackground(new java.awt.Color(204, 102, 0));
        sendButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        sendButton.setForeground(new java.awt.Color(255, 255, 255));
        sendButton.setText("Invia");
        sendButton.setToolTipText("");
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(sendButton);

        inventoryButton.setBackground(new java.awt.Color(51, 204, 0));
        inventoryButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        inventoryButton.setForeground(new java.awt.Color(255, 255, 255));
        inventoryButton.setText("Inventario");
        inventoryButton.setBorderPainted(false);
        inventoryButton.setFocusPainted(false);
        inventoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(inventoryButton);

        Menu.setBackground(new java.awt.Color(51, 153, 255));
        Menu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Menu.setForeground(new java.awt.Color(255, 255, 255));
        Menu.setText("Torna al Menu");
        Menu.setOpaque(true);
        Menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuActionPerformed(evt);
            }
        });
        buttonPanel.add(Menu);

        exitButton.setBackground(new java.awt.Color(204, 0, 0));
        exitButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        exitButton.setForeground(new java.awt.Color(255, 255, 255));
        exitButton.setText("Esci");
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(exitButton);

        jPanelSud.add(buttonPanel, java.awt.BorderLayout.LINE_END);

        getContentPane().add(jPanelSud, java.awt.BorderLayout.SOUTH);

        jPanelCentro.setBackground(new java.awt.Color(0, 0, 0));
        jPanelCentro.setOpaque(false);
        jPanelCentro.setLayout(new java.awt.BorderLayout());

        imageLabel.setBackground(new java.awt.Color(0, 0, 0));
        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setText("jLabel1");
        imageLabel.setOpaque(true);
        jPanelCentro.add(imageLabel, java.awt.BorderLayout.CENTER);

        outputScrollPane.setBackground(new java.awt.Color(0, 0, 0));
        outputScrollPane.setBorder(null);
        outputScrollPane.setOpaque(false);
        outputScrollPane.setPreferredSize(new java.awt.Dimension(800, 400));
        outputScrollPane.setRowHeaderView(null);
        outputScrollPane.setVerifyInputWhenFocusTarget(false);

        outputArea.setEditable(false);
        outputArea.setBackground(new java.awt.Color(0, 0, 0));
        outputArea.setContentType("text/html"); // NOI18N
        outputArea.setForeground(new java.awt.Color(255, 102, 0));
        outputArea.setFocusCycleRoot(false);
        outputScrollPane.setViewportView(outputArea);

        jPanelCentro.add(outputScrollPane, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanelCentro, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(849, 902));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        String command = inputField.getText();
        client.processUserInput(command);
        inputField.setText("");
        
    }//GEN-LAST:event_sendButtonActionPerformed

    private void inventoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryButtonActionPerformed
        client.processUserInput("inventario");
    }//GEN-LAST:event_inventoryButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        client.exitApplication();
    }//GEN-LAST:event_exitButtonActionPerformed

    private void inputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputFieldActionPerformed
        String command = inputField.getText();
        client.processUserInput(command);
        inputField.setText("");
    }//GEN-LAST:event_inputFieldActionPerformed

    private void MenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuActionPerformed
        client.returnToMenu();
    }//GEN-LAST:event_MenuActionPerformed

 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Menu;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JTextField inputField;
    private javax.swing.JButton inventoryButton;
    private javax.swing.JPanel jPanelCentro;
    private javax.swing.JPanel jPanelNord;
    private javax.swing.JPanel jPanelSud;
    private javax.swing.JEditorPane outputArea;
    private javax.swing.JScrollPane outputScrollPane;
    private javax.swing.JLabel roomNameLabel;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables
}
