package com.mysticcoders.pastebin.web.util;

import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;
import com.uwyn.jhighlight.renderer.GroovyXhtmlRenderer;

import java.io.IOException;

/**
 * GroovyHighlighterEngine
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class GroovyHighlighterEngine implements HighlighterEngine {


    // TODO should this be static?
    public String highlight(String code, String encoding) {
        try {
            return new GroovyXhtmlRenderer() {
                protected String getXhtmlHeaderFragment(String name) {
                    return "";
                }
            }.highlight(null, code, encoding, true);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
