package com.mysticcoders.pastebin.web;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.Resource;
import org.apache.wicket.Request;

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

    public PastebinSession(Request request) {
        super(request);
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
