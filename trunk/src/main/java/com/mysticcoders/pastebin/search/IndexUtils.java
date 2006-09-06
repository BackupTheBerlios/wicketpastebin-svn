package com.mysticcoders.pastebin.search;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.util.Date;
import java.io.IOException;

/**
 * IndexUtils
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class IndexUtils {

    /**
     * Calculate the size of the Lucene index.
     *
     * @param directory a Directory object that is the Lucene index location
     * @return a String containing a human-readable representation of the size of
     *         the Lucene index.
     */
    public static final String getIndexSize(Directory directory) {

        long size = 0;

/*
        if(!(directory instanceof FSDirectory)) {
            throw new IllegalArgumentException( "'Given directory' is not a " +
                                                "Lucene index directory." );
        }
*/

        try {
            String[] indexDir = directory.list();

            for (int i = 0; i < indexDir.length; i++) {
                size += directory.fileLength(indexDir[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return IndexUtils.bytesToHumanReadable(size);
    }


    /**
     * Calculate the last modified date of the Lucene index
     *
     * @param directory a Directory object that is the Lucene index location
     * @return a Date object containing the last modified date of the Lucene index
     */
    public static final Date getIndexLastModified(Directory directory) {

        Date lastModified = null;


        try {
            String[] indexDir = directory.list();

            for (int i = 0; i < indexDir.length; i++) {
                if (lastModified == null) lastModified = new Date(directory.fileModified(indexDir[i]));

                else if (lastModified.before(new Date(directory.fileModified(indexDir[i])))) {
                    lastModified = new Date(directory.fileModified(indexDir[i]));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lastModified;
    }

    /**
     * Create a human readable String from a number of bytes as bytes, K, M, or
     * G.
     *
     * @param bytes the number of bytes
     * @return a human-readable string.
     */
    public static final String bytesToHumanReadable(long bytes) {

        if (bytes < 1024) {
            return bytes + " bytes";
        }

        long kilobytes = bytes / 1024;

        if (kilobytes < 1024) {
            return kilobytes + " K";
        }

        long megabytes = kilobytes / 1024;

        if (megabytes < 1024) {
            return megabytes + " M";
        }

        long gigabytes = megabytes / 1024;

        return gigabytes + " G";

    }
}
