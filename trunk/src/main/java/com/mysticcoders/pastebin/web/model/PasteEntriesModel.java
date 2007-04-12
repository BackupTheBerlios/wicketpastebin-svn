package com.mysticcoders.pastebin.web.model;

import com.mysticcoders.pastebin.dao.PasteEntryDAO;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.web.PastebinApplication;
import wicket.model.IModel;
import wicket.model.AbstractReadOnlyModel;

import java.util.List;

/**
 * A model that provides a list of the latest entries.  The number of entries
 * return is adjustable at the time of instance creation.
 *
 * @author pchapman
 */
public class PasteEntriesModel
        extends AbstractReadOnlyModel {
    // CONSTANTS

    private static final long serialVersionUID = 1L;

    // CONSTRUCTORS

    /**
     * Creates an instance that lists the latest 10 entries.
     */
    public PasteEntriesModel(String privatePastebin) {
        this(10, privatePastebin);
    }

    /**
     * Creates an instance that lists the latest entries.
     *
     * @param rowCount The number of entries to return.
     */
    public PasteEntriesModel(int rowCount, String privatePastebin) {
        super();
        this.rowCount = rowCount;
        this.privatePastebin = privatePastebin;
        onAttach();
    }

    // MEMBERS

    private List<PasteEntry> list;

    private int rowCount;
    private String privatePastebin;

    public IModel getNestedModel() {
        return null;
    }

    // METHODS

    public void onAttach() {
        PasteEntryDAO dao =
                (PasteEntryDAO) PastebinApplication.getInstance().getBean(
                        "pasteEntryDAO"
                );
        list = dao.getPreviousEntriesList(rowCount, privatePastebin);
    }

    public void onDetach() {
        list = null;
    }

    public Object onGetObject() {
        return list;
    }

    public Object getObject() {
        return list;
    }
}