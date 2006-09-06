package com.mysticcoders.pastebin.search;

import org.apache.lucene.search.Hits;

import java.util.Map;

/**
 * SearchService
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public interface SearchService {

    public Hits search(String queryText);


}
