package com.mysticcoders.pastebin.dao;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;

import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.model.PrivatePastebin;

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

    public PasteEntry lookupPastebinEntry(Long id, String privatePastebin) {
        String query = null;

        if(privatePastebin==null) {
            query = "from PasteEntry as pasteEntry where pasteEntry.id = :pasteEntryId";
        } else {
            query = "from PasteEntry as pasteEntry where pasteEntry.id = :pasteEntryId AND pasteEntry.privatePastebin.name = :privatePastebinName";
        }

        Query q = getSession().createQuery( query )
                .setLong("pasteEntryId", id);

        if(privatePastebin!=null) {
            q.setString("privatePastebinName", privatePastebin);
        }

        return (PasteEntry)q.setMaxResults(1)
                .uniqueResult();
    }

    public void save(PasteEntry pasteEntry) {
        getSession().saveOrUpdate( pasteEntry );
    }

    public void delete(PasteEntry pasteEntry) {
        getSession().delete( pasteEntry );
    }

    @SuppressWarnings("unchecked")
    public Iterator<PasteEntry> getPreviousEntries(int limit, String privatePastebin) {
        return getPreviousEntriesQuery(limit, privatePastebin).iterate();
    }

    @SuppressWarnings("unchecked")
    public List<PasteEntry> getPreviousEntriesList(int limit, String privatePastebin) {
        return getPreviousEntriesQuery(limit, privatePastebin).list();
    }

    public List<PasteEntry> getPreviousEntriesList(String privatePastebin) {
        return getPreviousEntriesList(0, privatePastebin);
    }

    private Query getPreviousEntriesQuery(int limit, String privatePastebin) {
        String query = null;
        if(privatePastebin==null) {
            query = "from PasteEntry as pasteEntry order by pasteEntry.created desc";
        } else {
            query = "from PasteEntry as pasteEntry where pasteEntry.privatePastebin.name = :privatePastebinName";
        }

        Query q = getSession().createQuery( query );
        if(privatePastebin!=null) {
            q.setString("privatePastebinName", privatePastebin);
        }
        if(limit>0)
            q.setMaxResults( limit );

        return q;
    }

}
