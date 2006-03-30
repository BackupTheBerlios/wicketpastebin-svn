package com.mysticcoders.pastebin.core;

import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.dao.PasteEntryDAO;

/**
 * PasteServiceImpl
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class PasteServiceImpl implements PasteService {

    public void save(PasteEntry pasteEntry) {
        pasteEntryDAO.save(pasteEntry);
    }

    /* Spring Injected */
    private PasteEntryDAO pasteEntryDAO;

    public void setPasteEntryDAO(PasteEntryDAO pasteEntryDAO) {
        this.pasteEntryDAO = pasteEntryDAO;
    }
}
