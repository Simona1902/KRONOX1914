package implementazione;

import constants.GameIDs;
import exceptions.GameException;
import type.AdvObject;
import type.IndizioCriptex;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Questa classe centralizza la definizione di tutte le azioni personalizzate del gioco.
 * Collega gli oggetti (AdvObject) a specifiche porzioni di codice (lambda)
 * che vengono eseguite quando il giocatore interagisce con essi.
 * Questo permette agli Observer di rimanere generici e semplici.
 * @author simona
 */
public class ActionBinder {

    /**
     * Collega le azioni personalizzate agli oggetti del gioco.
     * Questo metodo deve essere chiamato all'inizializzazione del gioco
     * per definire il comportamento degli oggetti in risposta ai comandi dell'utente.
     *
     * @param game L'istanza di {@link Kronox1914} a cui applicare i binding delle azioni.
     */
    public static void bindActions(Kronox1914 game) {

        // ==================================================================
        // BINDING PER PUSH OBSERVER
        // ==================================================================

        AdvObject scala = game.findObjectById(GameIDs.SCALA_LEGNO);
        scala.setOnPushAction(g -> {
            if (scala.getDescription().startsWith("Una vecchia scala a pioli")) {
                scala.setDescription("La vecchia scala a pioli è ora appoggiata al muro, liberando lo spazio sottostante.");
                game.addMessage("Con un po' di sforzo, sposti di lato la pesante scala. Ai tuoi piedi ora noti chiaramente una botola di metallo che prima era nascosta.");
            } else {
                game.addMessage("Hai già spostato la scala.");
            }
        });

        AdvObject quadro = game.findObjectById(GameIDs.QUADRO_SCHONBRUNN);
        quadro.setOnPushAction(g -> {
            Kronox1914 kg = (Kronox1914) g;
            if (!kg.isIndizio(IndizioCriptex.P)){
                kg.addIndizio(IndizioCriptex.P);
                quadro.setDescription("Un magnifico dipinto del castello di Schonbrunn. Ora è perfettamente dritto. Ti ricordi il rpimo indizio: 'P' come le parole che eccheggiano nella stanza qui di fronte. Che vorrà dire il secondo indizio?");
                game.addMessage("Sposti il quadro per raddrizzarlo. Ora è appeso correttamente. Noti una scritta sul muro: 'P' come le parole che eccheggiano nella stanza qui di fronte. Che vorrà dire il secondo indizio?");
            } else {
                game.addMessage("Il quadro è già dritto, non c'è motivo di muoverlo ancora.");
            }
        });

        AdvObject confessionale = game.findObjectById(GameIDs.CONFESSIONALE);
        confessionale.setOnPushAction(g -> {
            if ("Il pesante confessionale è stato spostato...".equals(confessionale.getDescription())) {
                game.addMessage("Hai già spostato il confessionale.");
            } else {
                confessionale.setDescription("Il pesante confessionale è stato spostato, rivelando una porta blindata che prima era nascosta.");
                game.addMessage("Con uno sforzo immenso, sposti il confessionale. La parete dietro di esso non è di pietra, ma di metallo: hai scoperto una porta blindata!");
            }
        });

        // ==================================================================
        // BINDING PER OPEN OBSERVER
        // ==================================================================
        
        AdvObject botola = game.findObjectById(GameIDs.BOTOLA);
        botola.setOnOpenAction(g -> {
            if (botola.isCurrentlyLocked()) {
                AdvObject s = game.findObjectById(GameIDs.SCALA_LEGNO);
                if (s != null && s.getDescription().startsWith("La vecchia scala a pioli è ora appoggiata")) {
                    botola.setLocked("false");
                    botola.setDescription("La pesante botola di metallo è aperta, rivelando un passaggio buio verso il basso.");
                    game.addMessage("Fai leva sulla maniglia e, con un po' di sforzo, sollevi la pesante botola. Ora il passaggio è accessibile.");
                } else {
                    game.addMessage("Non riesci ad aprirla, c'è una scala che la ostruisce.");
                }
            } else {
                game.addMessage("La botola è già aperta.");
            }
        });

        // ==================================================================
        // BINDING PER LOOK_AT OBSERVER
        // ==================================================================

        AdvObject leggio = game.findObjectById(GameIDs.LEGGIO_UDIENZE);
        if (leggio != null) {
            leggio.setOnLookAction(g -> {
                Kronox1914 kg = (Kronox1914) g;
                if (!kg.isIndizio(IndizioCriptex.A)) {
                    kg.addIndizio(IndizioCriptex.A);
                    leggio.setDescription("Un leggio in legno finemente intarsiato. Rileggi il secondo indizio: 'A' come l'ascolto richiesto in questa stanza... Adesso puoi sederti a capotavola. Che vorrà dire il terzo indizio? Dove puoi trovare un tavolo da pranzo?");
                    return "Ispezioni il leggio e noti un'incisione quasi invisibile lungo il bordo: 'A' come l'ascolto richiesto in questa stanza... Adesso puoi sederti a capotavola. Che vorrà dire il terzo indizio? Dove puoi trovare un tavolo da pranzo?";
                } else {
                    // Se l'indizio è già stato trovato, restituisci null per usare la descrizione di default.
                    return null;
                }
            });
        }

        AdvObject tavola = game.findObjectById(GameIDs.TAVOLA_PRANZO);
        if (tavola != null) {
            tavola.setOnLookAction(g -> {
                Kronox1914 kg = (Kronox1914) g;
                if (!kg.isIndizio(IndizioCriptex.X)) {
                    kg.addIndizio(IndizioCriptex.X);
                    tavola.setDescription("Una grande tavola di mogano scuro, apparecchiata con cura. Rivedi il terzo indizio: è una 'X' finemente incisa sul pavimento. Finalmente hai trovato l'ultimo indizio.");
                    return "Nel posto vuoto a capotavola, noti una 'X' finemente incisa sul pavimento di legno. Finalmente hai trovato anche l'ultimo indizio.";
                } else {
                    // Se l'indizio è già stato trovato, restituisci null.
                    return null;
                }
            });
        }
        
        // ==================================================================
        // BINDING PER TALK OBSERVER
        // ==================================================================

        AdvObject segretario = game.findObjectById(GameIDs.SEGRETARIO);
        if (segretario != null) {
            segretario.setOnTalkAction(g -> "Ti avvicini al segretario. Lui alza lo sguardo e ti sussurra: " +
                    "'Ciao, tu devi essere il mio nuovo collega, sali dal Nunzio Apostolico con queste scale. Vuole parlarti.'\n\n" +
                    "'Aspetta... Se vuoi quando finisci puoi passare nella Stanza del Tesoro." +
                    " È una grande fortuna che tu possa vederla dal vivo, finalmente è stata riaperta dopo tanti anni ma non lo sarà ancora per molto tempo.'.");
        }

        AdvObject nunzio = game.findObjectById(GameIDs.NUNZIO);
        if (nunzio != null) {
            nunzio.setOnTalkAction( g -> {
                Kronox1914 kg = (Kronox1914) g;
                if (!kg.isPastMidday()) {
                    return "Il Nunzio Apostolico ti squadra dall'alto in basso. 'Tu devi essere il nuovo segretario...Benvenuto in questa Santa Sede Diplomatica. " +
                           "Qui vige la disciplina. Un paio di avvertenze: la prima, l'accesso alle mie camera private è esclusivamente mio, nessun'altro può entrarci; " +
                           "la seconda, oggi pomeriggio, a partire dalle dodici, riceverò ospiti di massima importanza. Si assicuri di non essere né visto né sentito. " +
                           "Un'intrusione non sarà tollerata. Il suo lavoro può cominciare già da subito, può raggiungere il suo collega.'";
                } else {
                    return "Il NUnzio non è qui, è andato via con i suoi ospiti. Ma dov' è andato?";
                }
            });
        }

        // ==================================================================
        // BINDING PER USE OBSERVER
        // ==================================================================

        // --- USA OGGETTO SU OGGETTO ---
        AdvObject chiaveMagazzino = game.findObjectById(GameIDs.CHIAVE_MAGAZZINO);
        AdvObject portaMagazzino = game.findObjectById(GameIDs.PORTA_MAGAZZINO);
        chiaveMagazzino.onUseWith(portaMagazzino.getId(), g -> {
            if (portaMagazzino.isCurrentlyLocked()) {
                portaMagazzino.setLocked("false");
                g.getInventory().remove(chiaveMagazzino);
                game.addMessage("Usi la piccola chiave d'ottone sul lucchetto. Con un 'click' secco, la porta è ora sbloccata.");
            } else {
                game.addMessage("La porta è già sbloccata.");
            }
        });
        
        AdvObject pendrive = game.findObjectById(GameIDs.PENDRIVE_KRONOX);
        AdvObject terminale = game.findObjectById(GameIDs.TERMINALE_KRONOX);
        pendrive.onUseWith(terminale.getId(), g -> {
             if (!"PASSWORD_READY".equals(terminale.getPushable())) {
                terminale.setPushable("PASSWORD_READY");
                g.getInventory().remove(pendrive);
                game.addMessage("Inserisci la pendrive KRONOX. Lo schermo si illumina: [INSERIRE PASSWORD]");
            } else {
                game.addMessage("La pendrive è già inserita.");
            }
        });
        
        AdvObject chiaveStrana = game.findObjectById(GameIDs.CHIAVE_STRANA_CRIPTEX);
        AdvObject statua = game.findObjectById(GameIDs.STATUA_EQUESTRE);
        if (chiaveStrana != null && statua != null) {
            chiaveStrana.onUseWith(statua.getId(), g -> {
                AdvObject documenti = game.findObjectById(GameIDs.DOCUMENTI_CONFRATERNITA);
                AdvObject telegramma = game.findObjectById(GameIDs.BOZZA_TELEGRAMMA);
                if (documenti != null && g.getInventory().contains(documenti) && 
                    telegramma != null && g.getInventory().contains(telegramma)) {
                    if (statua.isCurrentlyLocked()) {
                        statua.setLocked("false");
                        g.getInventory().remove(chiaveStrana);
                        game.addMessage("Inserisci la chiave e improvvisamente la terra sotto i tuoi piedi trema. La statua si sposta rivelando un passaggio segreto sotto di essa. Sembra esserci una cripta lì sotto.");
                    } else {
                        game.addMessage("Hai già usato la chiave qui. Il passaggio è sbloccato.");
                    }
                } else {
                    game.addMessage("Mentre avvicini la chiave alla statua ti rendi conto che ti mancano delle prove che accuserebbero il Nunzio e i suoi complici. Devi prenderle e dopo potrai usare la chiave sulla statua.");
                }
            });
        }

        // Aggiungi anche questo blocco
        AdvObject anello = game.findObjectById(GameIDs.ANELLO_NUNZIO);
        AdvObject libro = game.findObjectById(GameIDs.LIBRO_SIGILLATO);
        if (anello != null && libro != null) {
            anello.onUseWith(libro.getId(), g -> {
                String finale = "Al suo interno, leggi la conferma definitiva, scritta con una calligrafia fredda e precisa: " +
                "'Il mandato per l’assassinio dell’Arciduca Francesco Ferdinando, da eseguirsi il giorno seguente — 28 giugno 1914 — è stato affidato al Cardinale Aurelio Columba.\n" +
                "L'esecutore apparente sarà l’attivista Gavrilo Princip, su cui ricadrà ogni colpa.\n\n" +
                "Hai appena scoperto la verità, quella che il tuo professore aveva intuito e che forse non era riuscito a completare.\n" +
                "Gavrillo Princip fu solo una copertura, la Guerra è stata scatenata per mano della confraternita e chissà cosa altro ha fatto. " +
                "Ma, nonostante ciò, non sono riusciti a ricostituire la loro sperata Monarchia. Che qualcuno li avesse scoperti e bloccati?\n\n" +
                "Mentre sei preso dai tuoi ragionamenti tutto si dissolve intorno a te ancora una volta, Kronox si è riattivata.\n\n" +
                "Sei tornato nello scantinato, accanto alla macchina Kronox ormai spenta. Provi a toccarla e scopri che si è surriscaldata. " +
                "Che fortuna non essere stato rimasto intrappolato in quell'epoca. " +
                "Adesso ti rimangono tanti dubbi.\n" +
                "Il tuo professore aveva scoperto tutto ?\n" +
                "Ha voluto di proposito portarti nel 1914 o è stata la macchina ad agire così?\n" +
                "La confraternita si è sciolta o continua ad agire con lo stesso scopo?\n" +
                "Chi li ha fermati?\n\n" + 
                "Preso da questi pensieri prendi una decisione: continuerai il lavoro segretamente come stava facendo il tuo professore, se è il caso " +
                "viaggierai ancora nel tempo cercando di riparare Kronox. Quando avrai scoperto tutto il necessario, deciderai se rivelare tutto o " +
                "se non sconvolgere l'umanità.\n\n" + 
                "The End";
                g.setEnd(true, finale);
                game.addMessage("Posizioni l'anello nell'incavo del libro come una chiave. Il sigillo scatta e il libro si apre.\n");
            });
        }

        // --- USA TESTO SU OGGETTO ---
        if (terminale != null) {
            terminale.setOnUseWithTextAction((g, text) -> {
                Pattern datePattern = Pattern.compile("\\d{2}[./-]\\d{2}[./-]\\d{4}");
                Matcher dateMatcher = datePattern.matcher(text);
                 if (!"PASSWORD_READY".equals(terminale.getPushable())) {
                    game.addMessage("Devi prima usare la pendrive sul terminale.");
                    return;
                }
                if (dateMatcher.find()) {
                    String password = dateMatcher.group(0).replace('.', '-').replace('/', '-');
                    if ("28-06-1914".equals(password)) {
                        try {
                            g.timeTravel(type.GameEpoch.PASSATO, "ingresso_ambasciata_passato");
                            g.getInventory().remove(game.findObjectById(GameIDs.FOGLIO_INDOVINELLO));
                            g.getInventory().add(game.findObjectById(GameIDs.MAPPA_AMBASCIATA));
                            
                            String narrativeText = "<html><body style='font-family: Segoe UI, sans-serif; font-size: 14pt; color: #FF9900; background-color: #000000; padding: 20px;'>" +
                                "Il mondo intorno a te si contorce...<br><br>" +
                                "Improvvisamente apri gli occhi: non sai come lo sai, ma lo sai, sei a Sarajevo, in Bosnia-Erzegovina, in particolare nell'Ambasciata Vaticana, ed è il 27 giugno 1914.<br>" +
                                "Sono le ore 9.00 del mattino e frugando nelle tasche dei tuoi pantaloni trovi una mappa dell'ambasciata stessa.<br>" +
                                "Come ci è finita lì? E perchè la macchina ti ha portato qui?.</body></html>";
                
                            game.addMessage("START_TIMETRAVEL_SEQUENCE\n" + narrativeText);

                        } catch (GameException e) {
                            game.addMessage("ERRORE CRITICO DEL KRONOX.");
                        }
                    } else {
                        game.addMessage("PASSWORD ERRATA.");
                    }
                } else {
                     game.addMessage("Formato password non valido. Usa GG-MM-AAAA.");
                }
            });
        }

        AdvObject portaBlindata = game.findObjectById(GameIDs.PORTA_BLINDATA_ARCHIVIO);
        portaBlindata.setOnUseWithTextAction((g, text) -> {
            if (text.contains("9537")) {
                if (portaBlindata.isCurrentlyLocked()) {
                    portaBlindata.setLocked("false");
                    portaBlindata.setDescription("La porta blindata è ora socchiusa.");
                    AdvObject pergamena = game.findObjectById(GameIDs.PERGAMENA_CODICE);
                    if (pergamena != null) g.getInventory().remove(pergamena);
                    game.addMessage("Inserisci il codice. Si sente un 'clack' metallico e la pesante porta si apre con un cigolio.");
                } else {
                    game.addMessage("La porta è già sbloccata.");
                }
            } else {
                game.addMessage("Codice errato.");
            }
        });
        
        AdvObject criptex = game.findObjectById(GameIDs.CRIPTEX);
        if (criptex != null) {
            criptex.setOnUseWithTextAction((g, text) -> {
                Kronox1914 kg = (Kronox1914) g;
                
                // Dividiamo l'input per gestire parole extra come "su"
                String[] parts = text.trim().split(" ");
                String passwordAttempt = (parts.length > 0) ? parts[0] : "";

                if (kg.getIndiziTrovati().size() == IndizioCriptex.values().length) {
                    // Controlliamo solo la prima parola dell'input
                    if ("PAX".equalsIgnoreCase(passwordAttempt)) {
                        if (criptex.isCurrentlyLocked()) {
                            criptex.setLocked("false");
                            kg.setPastMidday(true);
                            AdvObject chiaveCriptex = game.findObjectById(GameIDs.CHIAVE_STRANA_CRIPTEX);
                            if (chiaveCriptex != null) g.getCurrentRoom().getObjects().add(chiaveCriptex);
                            g.getInventory().remove(criptex);
                            game.addMessage("Allinei le lettere per formare la parola 'PAX'... il criptex si apre, rivelando una chiave strana che forse servirà in seguito.\n\nImprovvisamente senti le campane suonare: è mezzogiorno. Il Nunzio Apostolico sarà con i suoi ospiti adesso. Forse è il momento perfetto per dare un'occhiata ai suoi spazi privati.");
                        } else {
                            game.addMessage("Il criptex è già aperto.");
                        }
                    } else {
                        game.addMessage("Hai inserito la combinazione sbagliata. Le ghiere non si muovono.");
                    }
                } else {
                    game.addMessage("Le ghiere girano a vuoto. Non hai abbastanza informazioni per risolvere l'enigma.");
                }
            });
        }
    }
}