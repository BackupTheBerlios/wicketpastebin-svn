package com.mysticcoders.pastebin.web.model;

import java.util.List;

import wicket.Component;
import wicket.model.AbstractReadOnlyDetachableModel;
import wicket.model.IModel;

import com.mysticcoders.pastebin.dao.PasteEntryDAO;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.web.PastebinApplication;

/**
 * A model that provides a list of the latest entries.  The number of entries
 * return is adjustable at the time of instance creation.
 * 
 * @author pchapman
 */
public class PasteEntriesModel
	extends AbstractReadOnlyDetachableModel
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;
	
	// CONSTRUCTORS
	
	/**
	 * Creates an instance that lists the latest 10 entries.
	 */
    public PasteEntriesModel()
    {
        this(10);
    }
    
    /**
     * Creates an instance that lists the latest entries.
     * @param rowCount The number of entries to return.
     */
    public PasteEntriesModel(int rowCount)
    {
    	super();
    	this.rowCount = rowCount;
    	onAttach();
    }
    
    // MEMBERS

    private List<PasteEntry> list;
    
    private int rowCount;

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
        list = dao.getPreviousEntriesList(rowCount);
    }

    public void onDetach()
    {
        list = null;
    }

    public Object onGetObject(Component comp)
    {
        return list;
    }
}