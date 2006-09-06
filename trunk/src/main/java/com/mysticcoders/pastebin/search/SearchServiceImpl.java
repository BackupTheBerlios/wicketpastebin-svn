package com.mysticcoders.pastebin.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * SearchServiceImpl
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class SearchServiceImpl implements SearchService {

    private Analyzer analyzer;
    private String indexPath;

    public SearchServiceImpl(String indexPath) {
        this(indexPath, new StandardAnalyzer());
    }

    public SearchServiceImpl(String indexPath, Analyzer analyzer) {
        this.indexPath = indexPath;
        this.analyzer = analyzer;
    }

    public Hits search(String queryText) {
        if (queryText == null || queryText.length() == 0) return null;

        try {
            IndexSearcher indexSearcher = new IndexSearcher(getIndex());

            StringBuffer queryBuffer = new StringBuffer();
            queryBuffer.append("+content:" + queryText);

            try {
                QueryParser parser = new QueryParser("content", analyzer);

                query = parser.parse(queryBuffer.toString());

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return indexSearcher.search(query);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private Directory getIndex() throws Exception {
        return FSDirectory.getDirectory(indexPath, false);
    }

    private Query query;

    public Query getQuery() {
        return query;
    }

}
