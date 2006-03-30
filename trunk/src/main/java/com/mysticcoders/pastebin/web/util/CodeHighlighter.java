package com.mysticcoders.pastebin.web.util;

import java.util.Map;

/**
 * CodeHighlighter
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class CodeHighlighter {


    public String highlight(String highlight, String code) {
        if(highlightEngines==null) {
            throw new IllegalArgumentException("No highlighter engines setup, please check configuration");
        }

        HighlighterEngine highlighter = highlightEngines.get( highlight );

        if(highlighter == null) return null;

        return highlighter.highlight(code, "UTF-8");
    }


    /* Spring Injected */
    private Map<String, HighlighterEngine> highlightEngines;

    public void setHighlightEngines(Map<String, HighlighterEngine> highlightEngines) {
        this.highlightEngines = highlightEngines;
    }
}
