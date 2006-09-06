package com.mysticcoders.pastebin.search;

import com.mysticcoders.pastebin.model.PasteEntry;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;

/**
 * IndexService
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public interface IndexService {

    public void rebuildIndex() throws Exception;

    public void addToIndex(PasteEntry pasteEntry) throws Exception;

    public void optimizeIndex() throws Exception;
}
