package com.mysticcoders.pastebin.web;

import wicket.protocol.http.WebSession;

import java.util.Set;
import java.util.HashSet;

import com.mysticcoders.pastebin.model.PrivatePastebin;

/**
 * PastebinSession
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class PastebinSession extends WebSession {

    public PastebinSession(PastebinApplication application) {
        super(application);
    }

    private Set<PrivatePastebin> activePrivatePastebins = new HashSet<PrivatePastebin>();

    public Set getActivePrivatePastebins() {
        return activePrivatePastebins;
    }

    public boolean containsPrivatePastebin(PrivatePastebin privatePastebin) {
        return activePrivatePastebins.contains( privatePastebin );
    }
    
    public void addPrivatePastebin(PrivatePastebin privatePastebin) {
        activePrivatePastebins.add(privatePastebin);
    }



}
