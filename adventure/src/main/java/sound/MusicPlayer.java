package sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Gestisce la riproduzione di un file audio in un thread separato.
 * Usa un ciclo per mantenere il thread attivo e permettere la riproduzione in loop.
 * Implementa {@link java.lang.Runnable} per poter essere eseguita in un thread.
 * @author simona
 */
public class MusicPlayer implements Runnable {

    private final String soundFilePath;
    private Clip audioClip;
    private volatile boolean keepRunning = true;

    /**
     * Costruisce un nuovo MusicPlayer con il percorso del file audio specificato.
     *
     * @param soundFilePath Il percorso della {@link java.lang.String} al file audio (es. "sounds/colonna_sonora.wav").
     */
    public MusicPlayer(String soundFilePath) {
        this.soundFilePath = soundFilePath;
    }

    /**
     * Metodo run eseguito quando il thread viene avviato.
     * Carica il file audio, lo apre, avvia la riproduzione in loop continuo
     * e mantiene il thread vivo finché {@code keepRunning} non è {@code false}.
     * Gestisce le eccezioni legate al caricamento o alla riproduzione dell'audio.
     */
    @Override
    public void run() {
        try {
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream(soundFilePath);
            if (audioSrc == null) {
                System.err.println("File audio non trovato: " + soundFilePath);
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn)) {
                audioClip = AudioSystem.getClip();
                audioClip.open(audioInputStream);
                audioClip.loop(Clip.LOOP_CONTINUOUSLY); // Avvia la musica in loop

                //Ciclo per mantenere il thread vivo
                // Il thread si mette in pausa e controlla periodicamente se deve continuare a girare.
                while (keepRunning) {
                    Thread.sleep(100); // Dorme per 100ms per non consumare CPU inutilmente
                }
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            // Se c'è un errore, il thread terminerà naturalmente dopo aver stampato l'errore.
            System.err.println("Errore durante la riproduzione della musica:");
            e.printStackTrace();
        }
    }

    /**
     * Ferma la riproduzione della musica e termina il thread in modo pulito.
     * Imposta il flag {@code keepRunning} a {@code false} per interrompere il ciclo del thread,
     * e chiude la clip audio.
     */
    public void stopMusic() {
        this.keepRunning = false;
        
        if (audioClip != null) {
            audioClip.stop();
            audioClip.close();
        }
    }
}