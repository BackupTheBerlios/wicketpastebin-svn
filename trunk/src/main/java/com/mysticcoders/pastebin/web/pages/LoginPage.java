package com.mysticcoders.pastebin.web.pages;

import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.PasswordTextField;
import wicket.model.IModel;
import wicket.model.CompoundPropertyModel;
import wicket.MarkupContainer;
import wicket.Application;
import wicket.Session;
import com.mysticcoders.pastebin.model.PrivatePastebin;
import com.mysticcoders.pastebin.web.PastebinApplication;
import com.mysticcoders.pastebin.web.PastebinSession;
import com.mysticcoders.pastebin.dao.PrivatePastebinDAO;

/**
 * LoginPage
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class LoginPage extends WebPage {

    public LoginPage() {
        PrivatePastebin privatePastebin = new PrivatePastebin();
        privatePastebin.setName(((PastebinApplication) Application.get()).getPrivatePastebinName());

        new LoginPageForm(this, "loginForm", new CompoundPropertyModel(privatePastebin));
    }

    private class LoginPageForm extends Form {

        public LoginPageForm(MarkupContainer parent, String id, IModel model) {
            super(parent, id, model);

            new Label(this, "name");
            new FeedbackPanel(this, "feedback");
            new PasswordTextField(this, "password");

        }

        protected void onSubmit() {
            PrivatePastebin privatePastebin = (PrivatePastebin)getModelObject();

            PrivatePastebinDAO privatePastebinDAO = (PrivatePastebinDAO)((PastebinApplication)Application.get()).getBean("privatePastebinDAO");

            boolean valid = privatePastebinDAO.validate(privatePastebin.getName(), privatePastebin.getPassword());

            if(!valid) {
                error("Password entered was incorrect");
                return;
            }

            PastebinSession pastebinSession = (PastebinSession) Session.get();

            pastebinSession.addPrivatePastebin(privatePastebinDAO.lookupPrivatePastebin( privatePastebin.getName() ));

            setResponsePage(PastebinPage.class);
        }
    }
}
