package com.mysticcoders.pastebin.dao;

import com.mysticcoders.pastebin.model.PasteEntry;

import java.util.Iterator;
import java.util.List;

/**
 * PasteEntryDAO
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public interface PasteEntryDAO {

    public PasteEntry lookupPastebinEntry(Long id);

    public PasteEntry lookupPastebinEntry(Long id, String privatePastebin);

    public void save(PasteEntry pasteEntry);

    public void delete(PasteEntry pasteEntry);

    public Iterator<PasteEntry> getPreviousEntries(int limit, String privatePastebin);

    public List<PasteEntry> getPreviousEntriesList(int limit, String privatePastebin);

    public List<PasteEntry> getPreviousEntriesList(String privatePastebin);

}
