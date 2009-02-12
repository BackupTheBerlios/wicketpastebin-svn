package com.mysticcoders.pastebin.web.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
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

        add(new LoginPageForm("loginForm", new CompoundPropertyModel(privatePastebin)));
    }

    private class LoginPageForm extends Form {

        public LoginPageForm(String id, IModel model) {
            super(id, model);

            add(new Label("name"));
            add(new FeedbackPanel("feedback"));
            add(new PasswordTextField("password"));

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

            if(!continueToOriginalDestination()) {
            	setResponsePage(PastebinPage.class);
            }
        }
    }
}
