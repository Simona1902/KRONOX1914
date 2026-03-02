package utils;

import implementazione.Kronox1914;
import java.io.PrintWriter;

/**
 * La classe AtmosphereThread è un thread separato che si occupa di gestire
 * l'atmosfera del gioco, ad esempio visualizzando messaggi periodici nella console
 * in base all'epoca corrente del gioco.
 * Implementa {@link java.lang.Runnable} per poter essere eseguita in un thread.
 *
 * @author simona
 */
public class AtmosphereThread implements Runnable{

    private final Kronox1914 game;
    private final PrintWriter clientOut;

    // Il costruttore riceve un riferimento al gioco principale
    // per poterne controllare lo stato (in questo caso, l'epoca).

    /**
     * Costruisce un nuovo AtmosphereThread.
     * Il costruttore riceve un riferimento all'istanza principale del gioco
     * per poterne controllare lo stato (in questo caso, l'epoca corrente).
     *
     * @param game L'istanza di {@link implementazione.Kronox1914} a cui questo thread è collegato.
     */
    public AtmosphereThread(Kronox1914 game, PrintWriter clientOut) {
        this.game = game;
        this.clientOut = clientOut;
    }

    /**
     * Metodo run eseguito quando il thread viene avviato.
     * Questo ciclo continua a eseguire messaggi di atmosfera finché il thread non viene interrotto.
     * Ogni 2 minuti, stampa un messaggio nella console basato sull'epoca corrente del gioco.
     */
    @Override
    public void run() {
        try {
            // Questo ciclo continua finché il thread non viene interrotto.
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(120000);
                type.GameEpoch currentEpoch = game.getCurrentEpoch();
                String msgContent = "";
                String msgTag = "ATMOS_MESSAGE:";
                if (type.GameEpoch.PRESENTE == currentEpoch) {
                    msgContent = "\n[Hai trovato Kronox?]";
                } else if (type.GameEpoch.PASSATO == currentEpoch) {
                    msgContent = "\n[Hai scoperto i segreti del Nunzio Apostolico?]";
                }
                clientOut.println(msgTag + msgContent);
                clientOut.flush();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
