package com.mysticcoders.pastebin.dao;

import java.util.Iterator;
import java.util.List;

import com.mysticcoders.pastebin.model.PasteEntry;

/**
 * PasteEntryDAO
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public interface PasteEntryDAO {

    public PasteEntry lookupPastebinEntry( Long id );

    public void save( PasteEntry pasteEntry );

    public void delete( PasteEntry pasteEntry );

    public Iterator<PasteEntry> getPreviousEntries(int limit);

    public List<PasteEntry> getPreviousEntriesList(int limit);

    public List<PasteEntry> getPreviousEntriesList();
}
