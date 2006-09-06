package com.mysticcoders.pastebin.search;

import com.mysticcoders.pastebin.core.PasteService;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.model.PasteEntryDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.List;

/**
 * IndexServiceImpl
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class IndexServiceImpl implements IndexService {

    static Log log = LogFactory.getLog(IndexServiceImpl.class);

    protected Analyzer analyzer;
//    private IndexWriter writer;
    private String indexPath;

    public IndexServiceImpl(String indexPath) {
        this(indexPath, new StandardAnalyzer());
    }

    public IndexServiceImpl(String indexPath, Analyzer analyzer) {
        this.analyzer = analyzer;
        this.indexPath = indexPath;
/*
        try {
            writer = getWriter();
        } catch (IOException e) {
            log.error("Writer for search not initialized correctly", e);
        }
*/
    }


    private IndexWriter getWriter() throws IOException {
        return getWriter(true);
    }

    private IndexWriter getWriter(boolean create) throws IOException {
        return new IndexWriter(indexPath, analyzer, create);
    }

    public void rebuildIndex() throws Exception {
        List<PasteEntry> entries = pasteService.listAll();

        IndexWriter writer = getWriter(true);

        for (PasteEntry entry : entries) {
            addToIndex(entry, writer);
        }

        writer.close();
    }

    public void addToIndex(PasteEntry pasteEntry, IndexWriter writer) throws Exception {
        if(pasteEntry == null || pasteEntry.getCode() == null) return;

        boolean controllingWriterObject = (writer == null);

        if(controllingWriterObject)
            writer = getWriter(false);

        Document doc = PasteEntryDocument.Document(pasteEntry);

        // add the document to the index
        try {
            writer.addDocument(doc);

            log.debug("Paste entry#" + pasteEntry.getId() + " added to index.");

        } catch (IOException e) {
            log.error("Error adding paste entry: ", e);

        }

        if(controllingWriterObject)
            writer.close();
    }

    public void addToIndex(PasteEntry pasteEntry) throws Exception {
        addToIndex(pasteEntry, null);
    }

    public void optimizeIndex() throws Exception {
        IndexWriter writer = getWriter(false);
        writer.optimize();
        writer.close();
    }

    private Directory getIndex() throws Exception {
        return FSDirectory.getDirectory(indexPath, false);
    }

    /* Spring Injected */
    private PasteService pasteService;

    public void setPasteService(PasteService pasteService) {
        this.pasteService = pasteService;
    }

}