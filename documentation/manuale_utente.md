# Manuale Utente: Avventura Testuale "KRONOX 1914"

Benvenuto, viaggiatore del tempo! Preparati a immergerti in un'avventura investigativa unica, dove ogni tua scelta testuale plasmerà il tuo percorso attraverso epoche e misteri. Questo manuale ti guiderà nell'avvio del gioco e nell'utilizzo dei comandi disponibili.

---

## 1. Avvio del Gioco

"KRONOX 1914" è un'applicazione basata su un'architettura Client-Server. Per poter giocare, è necessario avviare prima il Server e poi il Client.

**Requisiti:**
* Java Development Kit (JDK) installato (versione 8 o successiva).
* I file eseguibili del Server (`KRONOX1914-server-standalone.jar`) e del Client (`KRONOX1914-client-standalone.jar`), che troverai nella directory `target/` dopo aver compilato il progetto.

**Passaggi per avviare il gioco:**

1.  **Avviare il Server:**
    * Apri un terminale o prompt dei comandi (per esempio Powershell).
    * Naviga nella directory `target/` del progetto (dove si trovano i file JAR generati).
    * Esegui il comando:
        ```java -jar KRONOX1914-server-standalone.jar```
    per eseguire il progetto con main class server.java.
    * Dovresti vedere un messaggio che indica che il "SERVER DI GIOCO AVVIATO - In attesa sulla porta 12345".
    * **Importante:** Mantieni questo terminale aperto. Per chiudere il server pulitamente in seguito, premi `Ctrl+C` in questa stessa finestra.

2.  **Avviare il Client:**
    * Apri un **nuovo** terminale o prompt dei comandi.
    * Naviga nella directory `target/` del tuo progetto.
    * Esegui il comando:
        ```java -jar KRONOX1914-client-standalone.jar```
    * Si aprirà la finestra grafica del gioco. Il client tenterà automaticamente di connettersi al server.
    * **Alternativa Windows:** È anche possibile fare click con il tasto destro sul file `KRONOX1914-client-standalone.jar` ed aprirlo con JAVA(TM) Platform SE Binary.

Ovviamente è anche possibile avviare entrambi i processi utilizzando l'IDE NetBeans.

Una volta connesso, ti verrà presentata la schermata di benvenuto con la scelta dello slot di salvataggio. Segui le istruzioni a schermo per iniziare o caricare una partita.

---

## 2. Comandi di Gioco

L'interazione con il mondo di "KRONOX 1914" avviene principalmente tramite comandi testuali che dovrai digitare nell'apposito campo input. Il gioco è progettato per comprendere un linguaggio quasi naturale, ignorando preposizioni e articoli (stopwords) e riconoscendo alias per molti comandi e oggetti.

Ecco un elenco dei comandi principali e delle loro funzioni:

### Comandi di Movimento

Questi comandi ti permettono di spostarti tra le diverse stanze del gioco.

* **Nord**: si muove nella stanza davanti a quella corrente, se esiste.
* **Sud**: si muove nella stanza dietro a quella corrente, se esiste.
* **Est**: si muove nella stanza a destra rispetto a quella corrente, se esiste.
* **Ovest**: si muove nella stanza a sinistra rispetto a quella corrente, se esiste.
* **Scendi**: si muove nella stanza al piano di sotto rispetto a quella corrente, se esiste.
* **Sali**: si muove nella stanza al piano di sopra rispetto a quella corrente, se esiste.

### Comandi d'Azione

Questi comandi ti consentono di interagire con l'ambiente e gli oggetti.

* **osserva** (oppure **osserva [stanza]**): descrive la stanza corrente.
* **osserva [oggetto]**: descrive un oggetto presente nella stanza corrente o nell'inventario.
* **prendi [oggetto]**: raccoglie un oggetto presente nella stanza corrente per metterlo nel tuo inventario.
* **spingi [oggetto]**: spinge un oggetto presente nella stanza corrente o nell'inventario.
* **parla con [persona]**: parla con una persona presente nella stanza.
* **accendi**: accende la luce nella stanza corrente.
* **usa [oggetto] su [oggetto]**: utilizza un oggetto dell’inventario su un altro oggetto, quest'ultimo, a seconda del caso potrebbe essere nell'inventario oppure nella stanza corrente.
* **usa [parola] su [oggetto]**: utilizza una parola (es. una password) su un oggetto. L'oggetto, a seconda del caso, potrebbe essere nell'inventario oppure nella stanza corrente.

### Pulsanti dell'Interfaccia Grafica

Nella parte inferiore dell'interfaccia grafica del Client, troverai alcuni pulsanti rapidi per azioni comuni.

* **Invia**: Invia il comando scritto nel campo input. Equivale a premere Invio sulla tastiera.
* **Inventario** (oppure puoi digitare `i` o `inv` nel campo comandi): Mostra gli oggetti che hai raccolto nel tuo inventario, in una finestra di dialogo dedicata.
* **Torna al menu**: Ti riporta alla schermata iniziale di selezione degli slot di salvataggio, con salvataggio automatico della partita in corso nello slot attivo.
* **Esci**: Termina il gioco e chiude l'applicazione.

### Altri Comandi

* **aiuto**: Mostra la finestra di aiuto con l'elenco dei comandi disponibili (quella da cui sono tratte queste informazioni).

---

Buon divertimento con "KRONOX 1914"! Che la tua avventura nel tempo sia illuminante!