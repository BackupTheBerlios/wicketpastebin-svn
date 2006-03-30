package com.mysticcoders.pastebin.web.util;

import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;

import java.io.IOException;

/**
 * JavaHighlighterEngine
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class JavaHighlighterEngine implements HighlighterEngine {


    // TODO should this be static?
    public String highlight(String code, String encoding) {

        try {
            return new JavaXhtmlRenderer() {
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
