package com.mysticcoders.pastebin.web.model;

import wicket.model.IModel;
import wicket.model.AbstractReadOnlyModel;
import wicket.Component;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.dao.PasteEntryDAO;
import com.mysticcoders.pastebin.web.PastebinApplication;

import java.util.List;

/**
 * PasteEntryModel
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
/**
 * A model that provides a list of the latest entries.  The number of entries
 * return is adjustable at the time of instance creation.
 *
 * @author pchapman
 */
public class PasteEntryModel
	extends AbstractReadOnlyModel {
	// CONSTANTS

	private static final long serialVersionUID = 1L;

	// CONSTRUCTORS

    /**
     * Creates an instance that looks up the given id
     * @param id the id of the record to grab
     */
    public PasteEntryModel(long id)
    {
        this(id, null);
    }

    public PasteEntryModel(long id, String privatePastebin) {
        super();
        this.id = id;
        this.privatePastebin = privatePastebin;
        onAttach();
    }

    // MEMBERS

    private PasteEntry pasteEntry;

    private long id;
    private String privatePastebin;

    public IModel getNestedModel()
    {
        return null;
    }

    // METHODS

    public void onAttach()
    {
        PasteEntryDAO dao =
        	(PasteEntryDAO) PastebinApplication.getInstance().getBean(
        			"pasteEntryDAO"
        		);

        pasteEntry = dao.lookupPastebinEntry( id, privatePastebin);
    }

    public void onDetach()
    {
        pasteEntry = null;
    }

    /**
     * Called when getObject is called in order to retrieve the detachable
     * object. Before this method is called, getObject() always calls attach()
     * to ensure that the object is attached.
     *
     * @return The object
     */
    protected Object onGetObject() {
        return pasteEntry;
    }

    public Object getObject() {
        return pasteEntry;
    }
}