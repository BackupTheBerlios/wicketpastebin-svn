package com.mysticcoders.pastebin.web.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysticcoders.pastebin.search.IndexService;
import com.mysticcoders.pastebin.web.PastebinApplication;
import com.mysticcoders.pastebin.web.panels.RecentPostingPanel;
import wicket.markup.html.basic.Label;

/**
 * RebuildSearchIndexPage
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class RebuildSearchIndexPage extends BasePage {

    static Logger log = LoggerFactory.getLogger(RebuildSearchIndexPage.class);

    public RebuildSearchIndexPage() {
        super();

        IndexService indexService = (IndexService) PastebinApplication.getInstance().getBean("indexService");

        String status = "Rebuild successful.";

        try {
            indexService.rebuildIndex();
        } catch (Exception e) {
            log.error("Unable to rebuild index", e);
            status = "Rebuild not successful, please check logs";
        }

        new RecentPostingPanel(this, "recentPosts").setRenderBodyOnly(true);

        new Label(this, "status", status);
    }
}
