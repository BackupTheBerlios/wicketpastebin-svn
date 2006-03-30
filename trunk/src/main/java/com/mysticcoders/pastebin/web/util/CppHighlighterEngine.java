package com.mysticcoders.pastebin.web.util;

import com.uwyn.jhighlight.renderer.CppXhtmlRenderer;
import java.io.IOException;

/**
 * CppHighlighterEngine
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class CppHighlighterEngine implements HighlighterEngine {
    public String highlight(String code, String encoding) {
        try {
            return new CppXhtmlRenderer() {
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
