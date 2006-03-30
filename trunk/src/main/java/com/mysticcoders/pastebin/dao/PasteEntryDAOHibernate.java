package com.mysticcoders.pastebin.dao;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mysticcoders.pastebin.model.PasteEntry;

/**
 * PasteEntryDAOHibernate
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class PasteEntryDAOHibernate implements PasteEntryDAO {
	private SessionFactory factory;
	
	public void setSessionFactory(SessionFactory factory) {
		this.factory=factory;
	}
    
	public Session getSession() {
		return factory.getCurrentSession();
	}
	public PasteEntry lookupPastebinEntry(Long id) {
        return (PasteEntry)getSession().load( PasteEntry.class, id );
    }

    public void save(PasteEntry pasteEntry) {
        getSession().saveOrUpdate( pasteEntry );
    }

    public void delete(PasteEntry pasteEntry) {
        getSession().delete( pasteEntry );
    }

    @SuppressWarnings("unchecked")
	public Iterator<PasteEntry> getPreviousEntries(int limit) {
        String query = "from PasteEntry as pasteEntry order by pasteEntry.created desc";
        return getSession().createQuery(query).setMaxResults(limit).iterate();
    }
    
    @SuppressWarnings("unchecked")
    public List<PasteEntry> getPreviousEntriesList(int limit)
    {
    	String query = "from PasteEntry as pasteEntry order by pasteEntry.created desc";
    	return getSession().createQuery(query).setMaxResults(limit).list();
    }
}
