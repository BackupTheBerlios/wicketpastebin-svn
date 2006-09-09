package com.mysticcoders.pastebin.dao;

import com.mysticcoders.pastebin.model.PrivatePastebin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * PrivatePastebinDAOHibernate
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class PrivatePastebinDAOHibernate implements PrivatePastebinDAO {
    private SessionFactory factory;

    public void setSessionFactory(SessionFactory factory) {
        this.factory = factory;
    }

    public Session getSession() {
        return factory.getCurrentSession();
    }

    public PrivatePastebin lookupPrivatePastebin(Long id) {
        return (PrivatePastebin) getSession().load(PrivatePastebin.class, id);
    }

    public PrivatePastebin lookupPrivatePastebin(String name) {
        String query = "from PrivatePastebin as pp where pp.name = :name";

        return (PrivatePastebin) getSession().createQuery(query)
                .setString("name", name)
                .setMaxResults(1)
                .uniqueResult();
    }

    public void save(PrivatePastebin privatePastebin) {
        getSession().saveOrUpdate( privatePastebin );
    }

    public boolean validate(String pastebinName, String password) {
        String query = "from PrivatePastebin as pp where pp.name = :name AND pp.password = :password";

        PrivatePastebin privatePastebin = (PrivatePastebin) getSession().createQuery(query)
                .setString("name", pastebinName)
                .setString("password", password)
                .setMaxResults(1)
                .uniqueResult();

        return privatePastebin != null;
    }


}
