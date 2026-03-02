# KRONOX 1914 - Avventura Testuale

Benvenuto nel progetto "KRONOX 1914"! Questo è un gioco di avventura testuale sviluppato con un'architettura Client-Server in Java, utilizzando Maven per la gestione del progetto e la creazione di JAR autonomi.

## Struttura del Progetto

* **`pom.xml`**: File di configurazione Maven che gestisce dipendenze e build.
* **`src/main/java/`**: Contiene il codice sorgente Java per il server e il client.
* **`src/main/resources/`**: Contiene risorse come il file del database H2 (`db/stato_gioco.mv.db`) e altre risorse del gioco.
* **`target/`**: Directory dove vengono generati i JAR eseguibili dopo la compilazione Maven.

## Requisiti

* **Java Development Kit (JDK):** Versione 8 o successiva (il codice è compilato per compatibilità con JDK 8).
* **Apache Maven:** Versione 3.x o successiva.

**Assicurarsi di avere nella sottocartella /target i due file: `KRONOX1914-server-standalone.jar` e `KRONOX1914-client-standalone.jar`prima di procedere.**
Nel caso in cui non dovessero esserci è necessario procedere con la compilazione del progetto, per esempio, con l'IDE NetBeans con il comando Clean and Bulid. A questo punto dovrebbe apparire la sottocartella target con all'interno i suddetti file.

## Come Avviare il Gioco

"KRONOX 1914" richiede l'avvio separato del server e del client da farsi così come descritto di seguito.

## 1. Avvio del Gioco

"KRONOX 1914" è un'applicazione basata su un'architettura Client-Server. Per poter giocare, è necessario avviare prima il Server e solo dopo il Client.

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

## Gioco e Comandi

Una volta avviato il client, segui le istruzioni a schermo per scegliere uno slot di salvataggio e iniziare la tua avventura.

Per i comandi di gioco e altre informazioni dettagliate, consulta il file [manuale_utente.md](manuale_utente.md).
Per la sequenza di mosse che portano alla soluzione del gioco, consulta il file [soluzione.md](soluzione.md).

---
Buon divertimento con "KRONOX 1914"!