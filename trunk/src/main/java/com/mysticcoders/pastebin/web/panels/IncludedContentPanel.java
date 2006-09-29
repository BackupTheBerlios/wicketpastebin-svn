package com.mysticcoders.pastebin.web.panels;

import com.mysticcoders.pastebin.core.IncludedContentService;
import com.mysticcoders.pastebin.web.PastebinApplication;

import wicket.MarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;

/**
 * A panel that loads content from the IncludedContent service and shows it as
 * provided.
 * 
 * @author pchapman
 */
public class IncludedContentPanel extends Panel {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance.
	 * @param parent The parent to add this Panel to.
	 * @param name The name of this panel.
	 */
	public IncludedContentPanel(MarkupContainer parent, String name) {
		super(parent, name);
	    
        IncludedContentService contentService =
        	(IncludedContentService)PastebinApplication.getInstance()
        		.getBean("includedContentService");
        String s = contentService.getHeaderContent();
        new Label(
        		this, "contentLabel", s
        	).setEscapeModelStrings(false).setVisible(s.length() > 0);
	}
}
