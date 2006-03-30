package com.mysticcoders.pastebin.web.util;

import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;

/**
 * HighlighterEngine
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public interface HighlighterEngine {

    public String highlight(String code, String encoding);

}
