package com.mysticcoders.pastebin.web.panels;

import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.util.ModelIteratorAdapter;
import com.mysticcoders.pastebin.web.model.PasteEntriesModel;
import com.mysticcoders.pastebin.web.pages.PastebinPage;
import com.mysticcoders.pastebin.web.pages.ViewPastebinPage;
import wicket.AttributeModifier;
import wicket.MarkupContainer;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.extensions.markup.html.repeater.refreshing.RefreshingView;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;
import wicket.model.Model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * RecentPostingPanel
 * <p/>
 * Created by: Andrew Lombardi
 * Modified by: Philip A. Chapman to include pasted image link
 * Copyright 2004 Mystic Coders, LLC
 */
public class RecentPostingPanel extends Panel {

    public RecentPostingPanel(MarkupContainer parent, String id) {
        this(parent, id, null);
    }

    public RecentPostingPanel(MarkupContainer parent, String id, final Long pasteEntryId) {
        super(parent, id);

        new BookmarkablePageLink(this, "newPost", PastebinPage.class);

        new RefreshingView(this, "recentPosts") {

            @Override
            protected Iterator getItemModels() {
                List<PasteEntry> pasteEntries = (List<PasteEntry>) new PasteEntriesModel().getObject();

                return new ModelIteratorAdapter(pasteEntries.iterator()) {

                    @Override
                    protected IModel model(Object object) {
                        return new Model((Serializable) object);
                    }
                };
            }

            @Override
            protected void populateItem(Item item) {
                final PasteEntry pasteEntry = (PasteEntry) item.getModelObject();

                if (pasteEntry.getId().equals(pasteEntryId)) {
                    item.add(new AttributeModifier("class", true, new Model("highlight")));
                }

                long timeInMillis = Calendar.getInstance().getTime().getTime() - pasteEntry.getCreated().getTime();

                String elapsedTime = calcElapsedTime((int) timeInMillis);

                BookmarkablePageLink link = ViewPastebinPage.newLink(item, "pastebin", pasteEntry.getId());
                new Label(link, "name", pasteEntry.getName());
                new Label(item, "elapsedTime", elapsedTime).setRenderBodyOnly(true);
            }

        };
    }


    private String calcElapsedTime(int timeInMillis) {
        int days, hours, minutes, seconds;

        int timeInSeconds = timeInMillis / 1000;

        days = timeInSeconds / 86400;
        timeInSeconds = timeInSeconds - (days * 86400);
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;

        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            return hours + " hr" + (hours > 1 ? "s" : "") + " ago";
        } else if (minutes > 0) {
            return minutes + " min" + (minutes > 1 ? "s" : "") + " ago";
        } else if (seconds > 0) {
            return seconds + " sec" + (seconds > 1 ? "s" : "") + " ago";
        }

        return "";
    }

}
