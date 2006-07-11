package com.mysticcoders.pastebin.web;

import com.mysticcoders.common.BaseApplication;
import com.mysticcoders.pastebin.web.pages.PasteListXmlPage;
import com.mysticcoders.pastebin.web.pages.PastebinPage;
import com.mysticcoders.pastebin.web.pages.ViewPastebinPage;
import com.mysticcoders.pastebin.web.resource.ExportResource;
import com.mysticcoders.pastebin.web.resource.ImageResource;
import wicket.Application;
import wicket.protocol.http.WebApplication;
import wicket.request.target.coding.IndexedParamUrlCodingStrategy;
//import com.mysticcoders.pastebin.util.SimpleParamBookmarkablePageEncoder;

/**
 * PastebinApplication <p/> Created by: Andrew Lombardi Copyright 2004 Mystic
 * Coders, LLC
 */
public class PastebinApplication extends BaseApplication {

    protected void init() {
        super.init();

        getMarkupSettings().setStripWicketTags(true);

        mountBookmarkablePage("/home", PastebinPage.class);

        mount(new IndexedParamUrlCodingStrategy(
                "/view", ViewPastebinPage.class, null
        )
        );

        mountBookmarkablePage("/pastelist", PasteListXmlPage.class);

        Application.get().getSharedResources().add("exportResource",
                new ExportResource());
        Application.get().getSharedResources().add("imageResource",
                new ImageResource());
    }

    public static PastebinApplication getInstance() {
        return ((PastebinApplication) WebApplication.get());
    }


    public Class<PastebinPage> getHomePage() {
        return PastebinPage.class;
    }
}
