package com.mysticcoders.pastebin.web;

import com.mysticcoders.common.BaseApplication;
import com.mysticcoders.pastebin.web.pages.*;
import com.mysticcoders.pastebin.web.resource.ExportResource;
import com.mysticcoders.pastebin.web.resource.ImageResource;
import com.mysticcoders.pastebin.dao.PrivatePastebinDAO;
import com.mysticcoders.pastebin.model.PrivatePastebin;
import wicket.*;
import wicket.authorization.IUnauthorizedComponentInstantiationListener;
import wicket.authorization.UnauthorizedInstantiationException;
import wicket.authorization.IAuthorizationStrategy;
import wicket.authorization.Action;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.servlet.ServletWebRequest;
import wicket.request.target.coding.IndexedParamUrlCodingStrategy;

import javax.servlet.http.HttpServletRequest;
//import com.mysticcoders.pastebin.util.SimpleParamBookmarkablePageEncoder;

/**
 * PastebinApplication <p/> Created by: Andrew Lombardi Copyright 2004 Mystic
 * Coders, LLC
 */
public class PastebinApplication extends BaseApplication implements ISessionFactory {

    protected void init() {
        super.init();

        setSessionFactory(this);

        getApplicationSettings().setPageExpiredErrorPage(ErrorPage.class);
        getApplicationSettings().setInternalErrorPage(ErrorPage.class);

        getMarkupSettings().setStripWicketTags(true);

        mountBookmarkablePage("/home", PastebinPage.class);

        mount(new IndexedParamUrlCodingStrategy(
                "/view", ViewPastebinPage.class, null
        )
        );

        mount(new IndexedParamUrlCodingStrategy(
                "/search", SearchResultsPage.class, null
        ));

        mountBookmarkablePage("/pastelist", PasteListXmlPage.class);

        mountBookmarkablePage("/rebuild", RebuildSearchIndexPage.class);

        mountBookmarkablePage("/private/create", CreatePrivatePastebinPage.class);

        mountBookmarkablePage("/private/login", LoginPage.class);

        Application.get().getSharedResources().add("exportResource",
                new ExportResource());
        Application.get().getSharedResources().add("imageResource",
                new ImageResource());

        setSignInPage(LoginPage.class);
        getSecuritySettings().setAuthorizationStrategy(authorizationStrategy);
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(unauthorizedComponentInstantiationListener);


    }

    public static PastebinApplication getInstance() {
        return ((PastebinApplication) WebApplication.get());
    }


    public Class<PastebinPage> getHomePage() {
        return PastebinPage.class;
    }

    private Class signInPage;

    protected void setSignInPage(Class signInPage) {
        this.signInPage = signInPage;
    }

    /**
     * Creates a new session
     *
     * @param request The request that will create this session.
     * @return The session
     * @since 2.0
     */
    public Session newSession(Request request) {
        return new PastebinSession(PastebinApplication.this);
    }

    public String getPrivatePastebinName() {
        ServletWebRequest servletWebRequest = (ServletWebRequest) RequestCycle.get().getRequest();
        HttpServletRequest httpRequest = servletWebRequest.getHttpServletRequest();
        String serverName = httpRequest.getServerName();

        String[] splitServerName = serverName.split("\\.");
        int count = splitServerName.length;

        String privatePastebinName = null;

        if(count==3 && !serverName.startsWith("www")) {      // check for www, if not, we have a private pastebin
            privatePastebinName = splitServerName[0];
        }

        return privatePastebinName;
    }

    private IAuthorizationStrategy authorizationStrategy = new IAuthorizationStrategy() {

        public boolean isInstantiationAuthorized(Class componentClass) {
            String privateName = getPrivatePastebinName();

            if(privateName!=null && BasePage.class.isAssignableFrom(componentClass)) {
                PrivatePastebinDAO privatePastebinDAO = (PrivatePastebinDAO)getBean("privatePastebinDAO");

                PrivatePastebin privatePastebin = privatePastebinDAO.lookupPrivatePastebin( getPrivatePastebinName() );

                if(privatePastebin!=null) {

                    if(privatePastebin.getPassword()!=null) {
                        System.out.println("PROTECTED!");

                        PastebinSession pastebinSession = (PastebinSession)Session.get();
                        return pastebinSession.containsPrivatePastebin( privatePastebin );
                    }

                    return true;
                } else {
                    System.out.println("CREATE YOUR PRIVATE PASTEBIN NAOW!");
                    // redirect them to a page asking them to create the private pastebin
                    throw new RestartResponseException(CreatePrivatePastebinPage.class);
                }
            } else {
                return true;
            }
        }

        public boolean isActionAuthorized(Component component, Action action) {
            return true;
        }
    };

    private IUnauthorizedComponentInstantiationListener unauthorizedComponentInstantiationListener = new IUnauthorizedComponentInstantiationListener() {
        /**
         * Called when an unauthorized component instantiation is about to take
         * place (but before it happens).
         *
         * @param component The partially constructed component (only the id is
         *                  guaranteed to be valid).
         */
        public void onUnauthorizedInstantiation(final Component component) {
            // If there is a sign in page class declared, and the unauthorized
            // component is a page, but it's not the sign in page
            if (signInPage != null && component instanceof Page
                    && signInPage != component.getClass()) {

                    // Redirect to intercept page to let the user sign in
                    throw new RestartResponseAtInterceptPageException(signInPage);
            } else {
                // The component was not a page, so throw an exception
                throw new UnauthorizedInstantiationException(component.getClass());
            }
        }
    };
}
