package com.mysticcoders.pastebin.model;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * PasteEntryDocument
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class PasteEntryDocument {

    public static Document Document(PasteEntry pasteEntry) {
        Document doc = new Document();

        doc.add(new Field("code", pasteEntry.getCode(), Field.Store.YES, Field.Index.TOKENIZED));

        if (pasteEntry.getName() != null)
            doc.add(new Field("name", pasteEntry.getName(), Field.Store.YES, Field.Index.TOKENIZED));
        else
            doc.add(new Field("code", "N/A", Field.Store.YES, Field.Index.TOKENIZED));

        if (pasteEntry.getChannel() != null)
            doc.add(new Field("channel", pasteEntry.getChannel(), Field.Store.YES, Field.Index.TOKENIZED));
        else
            doc.add(new Field("channel", "N/A", Field.Store.YES, Field.Index.TOKENIZED));

        StringBuilder content = new StringBuilder();
        content.append(pasteEntry.getName()).append(" ");
        content.append(pasteEntry.getCode()).append(" ");
        content.append(pasteEntry.getChannel());

        doc.add(new Field("content", content.toString(), Field.Store.YES, Field.Index.TOKENIZED));

        doc.add(new Field("id", String.valueOf(pasteEntry.getId()), Field.Store.YES, Field.Index.NO));

        return doc;
    }
}
