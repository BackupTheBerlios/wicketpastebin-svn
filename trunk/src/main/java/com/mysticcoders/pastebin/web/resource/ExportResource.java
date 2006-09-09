package com.mysticcoders.pastebin.web.resource;

import com.mysticcoders.pastebin.dao.PasteEntryDAO;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.web.PastebinApplication;
import com.mysticcoders.pastebin.web.util.StringUtils;
import wicket.markup.html.DynamicWebResource;
import wicket.protocol.http.WebResponse;
import wicket.util.string.StringValueConversionException;
import wicket.util.value.ValueMap;

/**
 * ExportResource
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class ExportResource extends DynamicWebResource {
    private static final long serialVersionUID = 1L;

    private PasteEntry pasteEntry;

    @Override
    protected void setHeaders(WebResponse response) {
        String filename = "pastebin.txt";
        if (pasteEntry != null) {
            filename = pasteEntry.getName() + "_" + pasteEntry.getId() + ".txt";
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    }

    public ResourceState getResourceState() {
        ValueMap params = getParameters();

        if (params.get("pasteEntryId") != null) {
            PasteEntryDAO pasteEntryDAO = (PasteEntryDAO) PastebinApplication.getInstance().getBean("pasteEntryDAO");
            try {
                pasteEntry = (PasteEntry) pasteEntryDAO.lookupPastebinEntry(new Long(params.getLong("pasteEntryId")));

                String code = StringUtils.removeHighlightTag(pasteEntry.getCode());

                return new ExportResourceState(code.getBytes());
            } catch (StringValueConversionException e) {
                e.printStackTrace();
            }
        }
        return new ResourceState();
    }

    private class ExportResourceState extends ResourceState {
        ExportResourceState(byte[] data) {
            super();
            this.data = data;
        }

        @Override
        public String getContentType() {
            return "text/plain";
        }

        private byte[] data;

        @Override
        public byte[] getData() {
            return data;
        }

        @Override
        public int getLength() {
            return data.length;
        }
    }
}
