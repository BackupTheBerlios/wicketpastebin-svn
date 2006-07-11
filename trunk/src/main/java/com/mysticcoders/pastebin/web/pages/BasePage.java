package com.mysticcoders.pastebin.web.pages;

import wicket.markup.html.WebPage;
import wicket.protocol.http.WebRequestCycle;
import wicket.protocol.http.WebRequest;
import wicket.protocol.http.servlet.ServletWebRequest;
import com.mysticcoders.common.BaseApplication;
import com.mysticcoders.pastebin.util.BaseTag;
import com.mysticcoders.pastebin.web.util.CodeHighlighter;

import javax.servlet.http.HttpServletRequest;

/**
 * BasePage
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class BasePage extends WebPage {

    public BasePage() {
        super();
    }

    protected Object getBean(String beanName) {
        return ((BaseApplication) getApplication()).getBean(beanName);
    }

    protected CodeHighlighter getCodeHighlighter() {
        return (CodeHighlighter) ((BaseApplication) getApplication()).getBean("codeHighlighter");
    }
/*
    protected MediaManager getMediaManager() {
        return (MediaManager) ((BaseApplication) getApplication()).getBean("mediaManager");
    }

    protected NotifyManager getNotifyManager() {
        return (NotifyManager) ((BaseApplication) getApplication()).getBean("notifyManager");
    }
*/


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