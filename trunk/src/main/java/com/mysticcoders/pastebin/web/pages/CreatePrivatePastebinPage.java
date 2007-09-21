package com.mysticcoders.pastebin.web.pages;

import com.mysticcoders.pastebin.core.PasteService;
import com.mysticcoders.pastebin.dao.PrivatePastebinDAO;
import com.mysticcoders.pastebin.model.PrivatePastebin;
import com.mysticcoders.pastebin.web.PastebinApplication;
import com.mysticcoders.pastebin.web.PastebinSession;
import org.apache.wicket.Application;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * CreatePrivatePastebinPage
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class CreatePrivatePastebinPage extends WebPage {

    public CreatePrivatePastebinPage() {
        PrivatePastebin privatePastebin = new PrivatePastebin();
        privatePastebin.setName(((PastebinApplication) Application.get()).getPrivatePastebinName());

        add(new CreatePrivatePastebinForm("createPrivatePastebinForm", new CompoundPropertyModel(privatePastebin)));
    }


    private class CreatePrivatePastebinForm extends Form {

        public CreatePrivatePastebinForm(String id, IModel model) {
            super(id, model);

            add(new FeedbackPanel("feedback"));
            add(new TextField("name").setRequired(true));
            add(new TextField("email"));
            add(new PasswordTextField("password").setRequired(false));
        }

        protected void onSubmit() {
            PrivatePastebin privatePastebin = (PrivatePastebin) getModelObject();

            if (privatePastebin.getEmail() != null && privatePastebin.getPassword() == null) {
                error("Password is required if creating protected pastebin");
                return;         // short circuit
            } else if (privatePastebin.getPassword() != null && privatePastebin.getEmail() == null) {
                error("Email is required if creating protected pastebin");
                return;         // short circuit
            }

            PrivatePastebinDAO privatePastebinDAO = (PrivatePastebinDAO) ((PastebinApplication) Application.get()).getBean("privatePastebinDAO");

            if (privatePastebinDAO.lookupPrivatePastebin(privatePastebin.getName()) != null) {
                error("Pastebin with that name already exists");
                return;
            }

            if (privatePastebin.getName().equalsIgnoreCase("www")) {
                error("Pastebin with that name is reserved");
                return;
            }

            PasteService pasteService = (PasteService) ((PastebinApplication) Application.get()).getBean("pasteService");
            pasteService.savePrivatePastebin(privatePastebin);

            PastebinSession pastebinSession = (PastebinSession) Session.get();
            pastebinSession.addPrivatePastebin(privatePastebin);              // TODO debug this, why doesnt it autolog you in?

            String privateName = ((PastebinApplication) Application.get()).getPrivatePastebinName();

            if (privatePastebin.getName().equals(privateName)) {
                setResponsePage(PastebinPage.class);
            }

            ServletWebRequest servletWebRequest = (ServletWebRequest) RequestCycle.get().getRequest();
            HttpServletRequest httpRequest = servletWebRequest.getHttpServletRequest();
            String serverName = httpRequest.getServerName();

            StringBuilder finalRedirect = new StringBuilder();

            finalRedirect.append("http://");
            finalRedirect.append(privatePastebin.getName());
            finalRedirect.append(serverName.substring(serverName.indexOf("."), serverName.length()));
            if (httpRequest.getServerPort() != 80) {
                finalRedirect.append(":" + httpRequest.getServerPort());
            }
            finalRedirect.append(httpRequest.getRequestURI());

            setResponsePage(new RedirectPage(finalRedirect.toString()));        // TODO would be nice to be able to do a server redirect, rather than meta redirect
        }
    }

}

/*
public void onClick()
{

    RequestCycle.get().setRequestTarget(new IRequestTarget()
    {

        public void respond(RequestCycle requestCycle)
        {
            requestCycle.getResponse().redirect(yoururl);
        }

        public Object getLock(RequestCycle requestCycle)
        {
            return null;
        }

        public void detach(RequestCycle requestCycle)
        {
        }

    });
}
*/