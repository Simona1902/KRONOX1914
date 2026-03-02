package type;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe AdvObjectContainer estende {@link type.AdvObject}
 * per rappresentare oggetti che possono contenere altri {@link type.AdvObject}.
 * Questo permette la gestione di inventari interni o contenitori apribili.
 *
 * @author simona
 */
public class AdvObjectContainer extends AdvObject {
    private final List<AdvObject> list = new ArrayList<>();

    /**
     * Costruisce una nuova istanza di AdvObjectContainer.
     *
     * @param id L'ID numerico unico dell'oggetto contenitore.
     * @param name Il nome dell'oggetto contenitore.
     * @param description La descrizione dell'oggetto contenitore.
     */
    public AdvObjectContainer(int id, String name, String description) {
        super(id, name, description);
    }

    /**
     * Restituisce la lista degli oggetti contenuti in questo contenitore.
     *
     * @return Una {@link java.util.List} di {@link type.AdvObject} contenuta.
     */
    public List<AdvObject> getList() { return list; }

    /**
     * Aggiunge un oggetto alla lista degli oggetti contenuti.
     *
     * @param o L'oggetto {@link type.AdvObject} da aggiungere al contenitore.
     */
    public void add(AdvObject o) { list.add(o); }

    /**
     * Rimuove un oggetto dalla lista degli oggetti contenuti.
     *
     * @param o L'oggetto {@link type.AdvObject} da rimuovere dal contenitore.
     */
    public void remove(AdvObject o) { list.remove(o); }
}