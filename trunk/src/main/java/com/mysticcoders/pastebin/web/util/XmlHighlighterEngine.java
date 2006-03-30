package com.mysticcoders.pastebin.web.util;

import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;

import java.io.IOException;

/**
 * XmlHighlighterEngine
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class XmlHighlighterEngine implements HighlighterEngine {

    public String highlight(String code, String encoding) {

        try {
            return new XmlXhtmlRenderer() {
                protected String getXhtmlHeaderFragment(String name) {
                    return "";
                }
            }.highlight(null, code.toString(), encoding, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
