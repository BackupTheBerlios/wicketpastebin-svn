package com.mysticcoders.pastebin.web.pages;

import com.mysticcoders.pastebin.web.panels.IncludedContentPanel;
import com.mysticcoders.pastebin.web.panels.PastebinPanel;
import com.mysticcoders.pastebin.web.panels.RecentPostingPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebRequestCycle;

/**
 * PastebinPage
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class PastebinPage extends BasePage {

    public PastebinPage() {
        super();

        add(new RecentPostingPanel("recentPosts").setRenderBodyOnly(true));

        add(new Label("status", getLocalizer().getString("label.newPosting", this)));
        
        replace(new IncludedContentPanel("headerIncludedContent"));

        add(new PastebinPanel("pastebinPanel") {

            protected String getPageUrl() {
                StringBuffer url = humanReadableUrl((WebRequestCycle) super.getRequestCycle(), true);
                if (url != null) return url.toString();
                return "";
            }
        }.setRenderBodyOnly(true));

    }

}