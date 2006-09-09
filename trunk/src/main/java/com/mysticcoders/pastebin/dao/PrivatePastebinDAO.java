package com.mysticcoders.pastebin.dao;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.model.PrivatePastebin;

/**
 * PrivatePastebinDAO
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public interface PrivatePastebinDAO {

    public PrivatePastebin lookupPrivatePastebin(Long id);

    public PrivatePastebin lookupPrivatePastebin(String name);

    public void save(PrivatePastebin privatePastebin);

    public boolean validate(String pastebinName, String password);
}
