package com.mysticcoders.pastebin.web.panels;

import com.mysticcoders.pastebin.core.IncludedContentService;
import com.mysticcoders.pastebin.web.PastebinApplication;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * A panel that loads content from the IncludedContent service and shows it as
 * provided.
 * 
 * @author pchapman
 */
public class IncludedContentPanel extends Panel {
	private static final long serialVersionUID = 1L;

    @SpringBean
    private IncludedContentService contentService;

    /**
	 * Creates a new instance.
	 * @param name The name of this panel.
	 */
	public IncludedContentPanel(String name) {
		super(name);
	    
        String s = contentService.getHeaderContent();
        add(new Label("contentLabel", s
        	).setEscapeModelStrings(false).setVisible(s.length() > 0));
	}
}
