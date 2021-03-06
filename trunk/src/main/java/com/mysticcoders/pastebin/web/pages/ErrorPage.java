package com.mysticcoders.pastebin.web.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * ErrorPage
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class ErrorPage extends WebPage {
	public ErrorPage() {
		super();
		add(new BookmarkablePageLink<Void>("homelink", PastebinPage.class));
	}
}
