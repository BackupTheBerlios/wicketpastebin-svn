package com.mysticcoders.pastebin.web.pages;

import wicket.markup.html.basic.Label;
import wicket.protocol.http.WebRequestCycle;
import wicket.util.time.Duration;
import wicket.Application;
import com.mysticcoders.pastebin.web.pages.BasePage;
import com.mysticcoders.pastebin.web.panels.RecentPostingPanel;
import com.mysticcoders.pastebin.web.panels.PastebinPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

        add(new RecentPostingPanel("recentPosts").setRenderBodyOnly(true));

        add(new Label("status", getLocalizer().getString("label.newPosting", this)));

        add(new PastebinPanel("pastebinPanel") {

            protected String getPageUrl() {
                StringBuffer url = humanReadableUrl((WebRequestCycle)super.getRequestCycle(), true);
                if(url!=null) return url.toString();
                return "";
            }
        }.setRenderBodyOnly(true));

    }

}