package com.mysticcoders.pastebin.web.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * The page to be shown when spam detection has detected a possible spam posting.
 * 
 * @author pchapman
 */
public class SpamPage extends WebPage {

	public SpamPage() {
		super();
		add(new BookmarkablePageLink<Void>("homelink", PastebinPage.class));
	}
}
