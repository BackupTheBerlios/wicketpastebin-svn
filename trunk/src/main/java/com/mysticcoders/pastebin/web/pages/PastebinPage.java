package com.mysticcoders.pastebin.web.pages;

import com.mysticcoders.pastebin.web.panels.IncludedContentPanel;
import com.mysticcoders.pastebin.web.panels.PastebinPanel;
import com.mysticcoders.pastebin.web.panels.RecentPostingPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wicket.markup.html.basic.Label;
import wicket.protocol.http.WebRequestCycle;

/**
 * PastebinPage
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class PastebinPage extends BasePage {

    static Log log = LogFactory.getLog(PastebinPage.class);

    public PastebinPage() {
        super();

        new RecentPostingPanel(this, "recentPosts").setRenderBodyOnly(true);

        new Label(this, "status", getLocalizer().getString("label.newPosting", this));
        
        new IncludedContentPanel(this, "headerIncludedContent");

        new PastebinPanel(this, "pastebinPanel") {

            protected String getPageUrl() {
                StringBuffer url = humanReadableUrl((WebRequestCycle) super.getRequestCycle(), true);
                if (url != null) return url.toString();
                return "";
            }
        }.setRenderBodyOnly(true);

    }

}