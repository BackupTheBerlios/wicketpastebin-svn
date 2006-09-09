package com.mysticcoders.pastebin.core;

import org.springframework.transaction.annotation.Transactional;

import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.model.PrivatePastebin;

import java.util.List;

/**
 * PasteService
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public interface PasteService {

    @Transactional
    public void save(PasteEntry pasteEntry);

    public List<PasteEntry> listAll();

    @Transactional
    public void savePrivatePastebin(PrivatePastebin privatePastebin);
}
