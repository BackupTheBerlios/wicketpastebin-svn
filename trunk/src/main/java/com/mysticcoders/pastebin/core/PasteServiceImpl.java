package com.mysticcoders.pastebin.core;

import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.model.PrivatePastebin;
import com.mysticcoders.pastebin.dao.PasteEntryDAO;
import com.mysticcoders.pastebin.dao.PrivatePastebinDAO;

import java.util.List;

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

    public void savePrivatePastebin(PrivatePastebin privatePastebin) {
        privatePastebinDAO.save(privatePastebin);
    }

    public List<PasteEntry> listAll() {
//        return pasteEntryDAO.getPreviousEntriesList();
        return null;                // TODO due to private pastebin's we'll have to think up how to store this for searching.
    }

    /* Spring Injected */
    private PasteEntryDAO pasteEntryDAO;
    private PrivatePastebinDAO privatePastebinDAO;

    public void setPasteEntryDAO(PasteEntryDAO pasteEntryDAO) {
        this.pasteEntryDAO = pasteEntryDAO;
    }

    public void setPrivatePastebinDAO(PrivatePastebinDAO privatePastebinDAO) {
        this.privatePastebinDAO = privatePastebinDAO;
    }
}
