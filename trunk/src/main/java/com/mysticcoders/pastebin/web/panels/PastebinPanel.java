package com.mysticcoders.pastebin.web.panels;

import com.mysticcoders.pastebin.core.ImageService;
import com.mysticcoders.pastebin.core.PasteService;
import com.mysticcoders.pastebin.model.ImageEntry;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.model.PrivatePastebin;
import com.mysticcoders.pastebin.util.BotInterface;
import com.mysticcoders.pastebin.util.CookieUtils;
import com.mysticcoders.pastebin.web.PastebinApplication;
import com.mysticcoders.pastebin.web.pages.ViewPastebinPage;
import com.mysticcoders.pastebin.web.pages.highlighter.HighlighterTextAreaPanel;
import com.mysticcoders.pastebin.web.util.StringUtils;
import com.mysticcoders.pastebin.dao.PrivatePastebinDAO;
import wicket.Application;
import wicket.MarkupContainer;
import wicket.PageMap;
import wicket.markup.html.form.*;
import wicket.markup.html.form.upload.FileUpload;
import wicket.markup.html.form.upload.FileUploadField;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.markup.html.panel.Panel;
import wicket.model.CompoundPropertyModel;
import wicket.model.IModel;
import wicket.model.Model;
import wicket.protocol.http.WebRequestCycle;

import java.io.IOException;

/**
 * PastebinPanel
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class PastebinPanel extends Panel {

    public static final String REMEMBER_ME_COOKIE = "pastebin.rememberMe";
    public static final String REMEMBER_ME_SEP = "!||!||!";

    public PastebinPanel(MarkupContainer parent, String id) {
        this(parent, id, null);
    }

    public PastebinPanel(MarkupContainer parent, String id, PasteEntry existingEntry) {
        super(parent, id);

        PasteEntry pasteEntry = new PasteEntry();

        if (existingEntry != null) {
            pasteEntry.setCode(StringUtils.removeHighlightTag(existingEntry.getCode()));
            pasteEntry.setParent(existingEntry);
        }

        String rememberMe = CookieUtils.getDecodedCookie(REMEMBER_ME_COOKIE, (WebRequestCycle) getRequestCycle());

        if (rememberMe != null) {
            int index = rememberMe.indexOf(REMEMBER_ME_SEP);
            int start = index + REMEMBER_ME_SEP.length();
            if (index > -1) {
                if (rememberMe.length() > start) {
                    pasteEntry.setChannel(rememberMe.substring(start, rememberMe.length()));
                }
                pasteEntry.setName(rememberMe.substring(0, index));
            } else {
                if (rememberMe.length() > 0) {
                    pasteEntry.setName(rememberMe);
                }
            }
        }

        if (existingEntry != null) {
            if (existingEntry.getHighlight() == null) {
                pasteEntry.setHighlight("No");
            } else {
                pasteEntry.setHighlight(existingEntry.getHighlight());
            }
        } else {
            pasteEntry.setHighlight("No");
        }

        new PastebinForm(this, "pastebinForm", new CompoundPropertyModel(pasteEntry));
    }


    private class PastebinForm extends Form {

        public PastebinForm(MarkupContainer parent, String id, IModel model) {
            super(parent, id, model);

            setMultiPart(true);

            new FeedbackPanel(this, "feedback");
            new TextField(this, "name");
            new TextField(this, "channel");

            new CheckBox(this, "rememberMe", new Model<Boolean>(Boolean.FALSE));

            new DropDownChoice<String>(this, "highlight", HighlighterTextAreaPanel.getLanguageKeys());

            new TextArea(this, "code");

            new FileUploadField(this, "imageFile");
        }

        protected void onSubmit() {
            PasteEntry pasteEntry = (PasteEntry) getModelObject();

            if (pasteEntry.getName() == null || pasteEntry.getName().length() == 0) {
                pasteEntry.setName(getLocalizer().getString("label.AnonymousCoward", this));
            } else {
                CheckBox cb = (CheckBox) get("rememberMe");

                Boolean rememberMe = (Boolean) cb.getModelObject();

                if (rememberMe != null && rememberMe.booleanValue()) {
                    String channel = pasteEntry.getChannel();
                    if (channel == null) {
                        channel = "";
                    }
                    CookieUtils.setEncodedCookie(
                            REMEMBER_ME_COOKIE,
                            pasteEntry.getName() + REMEMBER_ME_SEP + channel,
                            (WebRequestCycle) getRequestCycle()
                    );
                }
            }

            if (pasteEntry.getCode() == null) {
                pasteEntry.setCode("");
            }

            ImageEntry imageEntry = null;
            FileUpload fupload = null;
            FileUploadField imageFile = (FileUploadField) get("imageFile");
            fupload = imageFile.getFileUpload();
            if (fupload == null) {
                // The FileUpload is null.
                if (pasteEntry.getCode().length() == 0) {
                    error("Please either paste code, provide an image, or both.");
                    return;
                }
            } else if (fupload.getSize() == 0) {
                error("The image you attempted to upload is empty.");
                return;
            } else if (! checkContentType(fupload.getContentType())) {
                error("Only images of types png, jpg, and gif are allowed.");
                return;
            } else {
                imageEntry = new ImageEntry();

                imageEntry.setContentType(fupload.getContentType());
                imageEntry.setName(fupload.getClientFileName());
                imageEntry.setParent(pasteEntry);
            }

            PrivatePastebinDAO privatePastebinDAO = (PrivatePastebinDAO) PastebinApplication.getInstance().getBean("privatePastebinDAO");
            String privatePastebinName = ((PastebinApplication) Application.get()).getPrivatePastebinName();
            PrivatePastebin privatePastebin = privatePastebinDAO.lookupPrivatePastebin( privatePastebinName );
            pasteEntry.setPrivatePastebin( privatePastebin );
            
            // Save the data
            PasteService pasteService = (PasteService) PastebinApplication.getInstance().getBean("pasteService");
            pasteService.save(pasteEntry);
            if (imageEntry != null) {
                ImageService imageService = (ImageService) PastebinApplication.getInstance().getBean("imageService");
                try {
                    imageService.save(imageEntry, fupload.getInputStream());
                } catch (IOException ioe) {
                    // It'd be nice to have a logger so that we could log the error.
                    error("Sorry, there was an error uploading the image.");
                }
            }

            BotInterface botInterface = (BotInterface) PastebinApplication.getInstance().getBean("botInterface");
            botInterface.send(pasteEntry.getName(), pasteEntry.getChannel(), getPageUrl() + getPage().urlFor(PageMap.forName(PageMap.DEFAULT_NAME), ViewPastebinPage.class, ViewPastebinPage.newPageParameters(pasteEntry.getId())));

/*
TODO add this back in when there is time.
                IndexService indexService = (IndexService) PastebinApplication.getInstance().getBean("indexService");
                try {
                    indexService.addToIndex(pasteEntry);
                } catch (Exception e) {
                    log.error("Unable to add paste entry to index", e);
                }
*/
            //setResponsePage(new RedirectPage("/pastebin/"+pasteEntry.getId()));
            setResponsePage(ViewPastebinPage.class, ViewPastebinPage.newPageParameters(pasteEntry.getId()));
        }
    }

    protected String getPageUrl() {
        return "";
    }

    private static final String[] VALID_CONTENT_TYPES = {
            "image/gif", "image/jpeg", "image/png"
    };

    private boolean checkContentType(String contentType) {
        for (int i = 0; i < VALID_CONTENT_TYPES.length; i++) {
            if (VALID_CONTENT_TYPES[i].equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }
}
