package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * La classe Utils fornisce metodi di utilità statici per operazioni comuni
 * come il caricamento di file di testo e il parsing di stringhe.
 *
 * @author simona
 */
public class Utils {

    /**
     * Carica il contenuto di un file specificato dal file system e lo restituisce come un {@link java.util.Set} di stringhe.
     * Ogni riga del file viene convertita in minuscolo e privata degli spazi bianchi iniziali/finali prima di essere aggiunta al set.
     * Il metodo assicura la chiusura sicura delle risorse I/O.
     *
     * @param file L'oggetto {@link java.io.File} da caricare.
     * @return Un {@link java.util.Set} di {@link java.lang.String} contenente le righe del file.
     * @throws java.io.IOException Se si verifica un errore di input/output durante la lettura del file.
     */
    public static Set<String> loadFileListInSet(File file) throws IOException {
        Set<String> set = new HashSet<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                set.add(reader.readLine().trim().toLowerCase());
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return set;
    }

    /**
     * Carica il contenuto di un {@link java.io.InputStream} (tipicamente da una risorsa del classpath)
     * e lo restituisce come un {@link java.util.Set} di stringhe.
     * Ogni riga dello stream viene convertita in minuscolo e privata degli spazi bianchi iniziali/finali.
     * Le righe vuote vengono ignorate. Il metodo assicura la chiusura sicura delle risorse I/O.
     *
     * @param stream Lo {@link java.io.InputStream} da cui leggere.
     * @return Un {@link java.util.Set} di {@link java.lang.String} contenente le righe lette.
     * @throws java.io.IOException Se si verifica un errore di input/output durante la lettura dello stream.
     */
    public static Set<String> loadFileListInSet(InputStream stream) throws IOException {
        Set<String> set = new HashSet<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    set.add(line.trim().toLowerCase());
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return set;
    }

    /**
     * Suddivide una stringa di input in una lista di token, ignorando le "stopwords".
     * La stringa viene convertita in minuscolo e suddivisa in base agli spazi.
     * I token che sono presenti nel set delle stopwords non vengono inclusi nella lista risultante.
     *
     * @param string La {@link java.lang.String} di input da parsare.
     * @param stopwords Un {@link java.util.Set} di {@link java.lang.String} contenente le parole da ignorare.
     * @return Una {@link java.util.List} di {@link java.lang.String} che rappresenta i token parsati.
     */
    public static List<String> parseString(String string, Set<String> stopwords) {
        List<String> tokens = new ArrayList<>();
        String[] split = string.toLowerCase().split("\\s+");
        for (String t : split) {
            if (!stopwords.contains(t)) {
                tokens.add(t);
            }
        }
        return tokens;
    }
}