package com.mysticcoders.pastebin.web.pages;

import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.protocol.http.WebRequestCycle;
import wicket.protocol.http.WebRequest;
import wicket.protocol.http.servlet.ServletWebRequest;
import wicket.Application;
import wicket.model.Model;
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
	
    public BasePage() {
        super();

/*
        ServletWebRequest servletWebRequest = (ServletWebRequest) RequestCycle.get().getRequest();
        HttpServletRequest httpRequest = servletWebRequest.getHttpServletRequest();
        String serverName = httpRequest.getServerName();

        String[] splitServerName = serverName.split("\\.");
        int count = splitServerName.length;

        if(count==3 && !serverName.startsWith("www")) {      // check for www, if not, we have a private pastebin
            System.out.println("PRIVATE PASTEBIN:"+splitServerName[0]);
            privatePastebinName = splitServerName[0];
        }
*/

        String privatePastebinName = getPrivatePastebinName();
        System.out.println("PRIVATE PASTEBIN:"+privatePastebinName);

        new Label(this, "privateName", new Model<String>((privatePastebinName!=null?privatePastebinName+" ":"")));

        IncludedContentService contentService =
        	(IncludedContentService)PastebinApplication.getInstance()
        		.getBean("includedContentService");
        String s = contentService.getHeaderContent();
        new Label(
        		this, "headerIncludedContent", s
        	).setEscapeModelStrings(false).setVisible(s.length() > 0);
    }

    protected String getPrivatePastebinName() {
        return ((PastebinApplication)Application.get()).getPrivatePastebinName();
    }

    protected Object getBean(String beanName) {
        return ((BaseApplication) getApplication()).getBean(beanName);
    }

    protected StringBuffer humanReadableUrl(final WebRequestCycle cycle) {
        return humanReadableUrl(cycle, false);
    }

    protected StringBuffer humanReadableUrl(final WebRequestCycle cycle, boolean includeDomain) {
        return humanReadableUrl(cycle, includeDomain, false);
    }

    protected StringBuffer humanReadableUrl(final WebRequestCycle cycle, boolean includeDomain, boolean includeContext) {
        final StringBuffer buffer = new StringBuffer();
        final WebRequest request = cycle.getWebRequest();

        if(includeDomain) {
            buffer.append(httpServer(cycle));
        }

        if (request != null) {
            if(includeContext) {
                final String contextPath = request.getContextPath();
                buffer.append(contextPath);

                String path = request.getServletPath();
                if (path == null || "".equals(path)) {
                    path = "/";
                }
                buffer.append(path);
            }
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