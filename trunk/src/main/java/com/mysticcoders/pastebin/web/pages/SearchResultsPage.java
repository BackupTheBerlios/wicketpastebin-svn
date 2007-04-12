package com.mysticcoders.pastebin.web.pages;

import com.mysticcoders.pastebin.search.SearchService;
import com.mysticcoders.pastebin.util.ModelIteratorAdapter;
import com.mysticcoders.pastebin.web.PastebinApplication;
import com.mysticcoders.pastebin.web.panels.RecentPostingPanel;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import wicket.PageParameters;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.repeater.RefreshingView;
import wicket.markup.repeater.Item;
import wicket.model.IModel;
import wicket.model.Model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

/**
 * SearchResultsPage
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class SearchResultsPage extends BasePage {

    public SearchResultsPage(PageParameters params) {
        String query = params.getString("0");

        if (query == null) {
            throw new RuntimeException("Entry not found");
        }

        new RecentPostingPanel(this, "recentPosts").setRenderBodyOnly(true);

        results(query);
    }


    private void results(final String query) {
        new RefreshingView(this, "searchResults") {

            @Override
            protected Iterator getItemModels() {
                SearchService searchService = (SearchService) PastebinApplication.getInstance().getBean("searchService");

                Hits hits = searchService.search(query);

                return new ModelIteratorAdapter(new HitsIterator(hits)) {

                    @Override
                    protected IModel model(Object object) {
                        return new Model((Serializable) object);
                    }
                };
            }

            @Override
            protected void populateItem(Item item) {
                final Document document = (Document) item.getModelObject();

                BookmarkablePageLink link = ViewPastebinPage.newLink(item, "pastebin", Long.valueOf(document.get("id")));

                new Label(link, "name", document.get("name"));
                new Label(item, "channel", document.get("channel"));

            }

        };

    }

    private class HitsIterator implements Iterator {

        private Hits hits;

        private int idx = 0;

        public HitsIterator(Hits hits) {
            this.hits = hits;
        }

        public boolean hasNext() {
            return (idx + 1) < hits.length();
        }

        public Object next() {
            if (!hasNext()) return null;

            Document doc = null;
            try {
                doc = hits.doc(idx);
            } catch (IOException e) {
                e.printStackTrace();
            }

            idx += 1;
            
            return doc;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
