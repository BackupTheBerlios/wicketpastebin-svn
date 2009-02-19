package com.mysticcoders.pastebin.web.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.Application;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.model.Model;
import org.slf4j.LoggerFactory;

import com.mysticcoders.common.BaseApplication;
import com.mysticcoders.pastebin.core.IncludedContentService;
import com.mysticcoders.pastebin.web.PastebinApplication;

import javax.servlet.http.HttpServletRequest;

/**
 * BasePage
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 * Edited by: Philip Chapman
 */
public class BasePage extends WebPage {
	private static final long serialVersionUID = 1L;

    @SpringBean
    private IncludedContentService contentService;

    public BasePage() {
        super();

        String privatePastebinName = getPrivatePastebinName();
        LoggerFactory.getLogger(getClass()).debug("PRIVATE PASTEBIN: {}", privatePastebinName);

        add(new Label("privateName", new Model((privatePastebinName!=null?privatePastebinName+" ":""))));

        String s = contentService.getHeaderContent();
        add(new Label("headerIncludedContent", s
        	).setEscapeModelStrings(false).setVisible(s.length() > 0));
    }

    protected String getPrivatePastebinName() {
        return ((PastebinApplication)Application.get()).getPrivatePastebinName();
    }

    protected StringBuffer humanReadableUrl(final WebRequestCycle cycle) {
        return humanReadableUrl(cycle, false);
    }

    protected StringBuffer humanReadableUrl(final WebRequestCycle cycle, boolean includeDomain) {
        final StringBuffer buffer = new StringBuffer();

        if(includeDomain) {
            buffer.append(httpServer(cycle));
        }

        return buffer;
    }

    private final String httpServer(final WebRequestCycle cycle) {

        final HttpServletRequest servletRequest = ((ServletWebRequest) cycle.getWebRequest()).getHttpServletRequest();

        StringBuffer link = new StringBuffer();

        int httpsPort = 443;
        int httpPort = 80;

        String scheme = servletRequest.getScheme();

        link.append(scheme);
        link.append("://");
        link.append(servletRequest.getServerName());

        // do not append port for default ports
        int port = servletRequest.getServerPort();

        if (!(scheme.equals("http") && (port == httpPort)) && !(scheme.equals("https") && (port == httpsPort))) {
            link.append(":");
            link.append(port);
        }

        return link.toString();
    }
}
