package type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * La classe Command rappresenta un comando riconosciuto all'interno del gioco.
 * Ogni comando ha un tipo ({@link type.CommandType}), un nome principale e un set di alias.
 * 
 * @author simona
 */
public class Command {

    private final CommandType type;

    private final String name;

    private Set<String> alias = new HashSet<>();

    /**
     * Costruisce una nuova istanza di Command con il tipo e il nome specificati.
     *
     * @param type Il {@link type.CommandType} dell'istanza del comando.
     * @param name La {@link java.lang.String} che rappresenta il nome principale del comando.
     */
    public Command(CommandType type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Costruisce una nuova istanza di Command con il tipo, il nome e gli alias specificati.
     *
     * @param type Il {@link type.CommandType} dell'istanza del comando.
     * @param name La {@link java.lang.String} che rappresenta il nome principale del comando.
     * @param alias Un {@link java.util.Set} di {@link java.lang.String} contenente gli alias per il comando.
     */
    public Command(CommandType type, String name, Set<String> alias) {
        this.type = type;
        this.name = name;
        this.alias = alias;
    }

    /**
     * Restituisce il nome principale del comando.
     *
     * @return La {@link java.lang.String} che rappresenta il nome del comando.
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce il set di alias per il comando.
     *
     * @return Un {@link java.util.Set} di {@link java.lang.String} contenente gli alias del comando.
     */
    public Set<String> getAlias() {
        return alias;
    }

    /**
     * Imposta il set di alias per il comando.
     *
     * @param alias Il {@link java.util.Set} di {@link java.lang.String} da impostare come alias.
     */
    public void setAlias(Set<String> alias) {
        this.alias = alias;
    }

    /**
     * Imposta gli alias per il comando da un array di stringhe.
     *
     * @param alias Un array di {@link java.lang.String} da convertire in un set di alias.
     */
    public void setAlias(String[] alias) {
        this.alias = new HashSet<>(Arrays.asList(alias));
    }

    /**
     * Restituisce il tipo di comando.
     *
     * @return Il {@link type.CommandType} del comando.
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Calcola il codice hash per questo comando, basato sul suo tipo.
     *
     * @return Il codice hash dell'oggetto.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.type);
        return hash;
    }

    /**
     * Compara questo oggetto Command con l'oggetto specificato per l'uguaglianza.
     * Due comandi sono considerati uguali se hanno lo stesso {@link type.CommandType}.
     *
     * @param obj L'oggetto da confrontare.
     * @return {@code true} se gli oggetti sono uguali, {@code false} altrimenti.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Command other = (Command) obj;
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

}
