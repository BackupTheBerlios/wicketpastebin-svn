package com.mysticcoders.pastebin.web.panels;

import com.mysticcoders.pastebin.core.ImageService;
import com.mysticcoders.pastebin.core.PasteService;
import com.mysticcoders.pastebin.dao.PrivatePastebinDAO;
import com.mysticcoders.pastebin.model.ImageEntry;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.model.PrivatePastebin;
import com.mysticcoders.pastebin.util.BotInterface;
import com.mysticcoders.pastebin.util.CookieUtils;
import com.mysticcoders.pastebin.web.PastebinApplication;
import com.mysticcoders.pastebin.web.pages.ViewPastebinPage;
import com.mysticcoders.pastebin.web.pages.highlighter.HighlighterTextAreaPanel;
import com.mysticcoders.pastebin.web.util.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.PageMap;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.IOException;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PastebinPanel
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class PastebinPanel extends Panel {

    public static final String REMEMBER_ME_COOKIE = "pastebin.rememberMe";
    public static final String REMEMBER_ME_SEP = "!||!||!";


    @SpringBean
    private PrivatePastebinDAO privatePastebinDAO;
    @SpringBean
    private PasteService pasteService;
    @SpringBean
    private ImageService imageService;
    @SpringBean
    private BotInterface botInterface;

    static Logger log = LoggerFactory.getLogger(PastebinPanel.class);
    
    public PastebinPanel(String id) {
        this(id, null);
    }

    public PastebinPanel(String id, PasteEntry existingEntry) {
        super(id);

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

        add(new PastebinForm("pastebinForm", new CompoundPropertyModel(pasteEntry)));
    }


    private class PastebinForm extends Form {
        private final ValueMap properties = new ValueMap();
        
        public PastebinForm(String id, IModel model) {
            super(id, model);
            
            setMultiPart(true);

            add(new FeedbackPanel("feedback"));
            add(new TextField("name"));
            add(new TextField("channel"));

            add(new CheckBox("rememberMe", new Model(Boolean.FALSE)));

            add(new DropDownChoice("highlight", HighlighterTextAreaPanel.getLanguageKeys()));

            add(new TextArea("code"));

            add(new FileUploadField("imageFile"));
            
            //spam bot detection, this field is hidden to humans (via css), if 
            //it is filled out, we know the submission was by a bot
            TextField botField = new TextField("cylon", new PropertyModel(properties, "cylon"));
            //To test this functionality, uncomment the following line
            //botField.setModelValue(new String[]{"Cylon!"});
            add(botField);
        }

        protected void onSubmit() {
            PasteEntry pasteEntry = (PasteEntry) getModelObject();
            //something was entred!  Must be a spam bot as the field is
            //not visible to users
            String cylon = properties.getString("cylon");
            if (cylon == null) cylon = "";
            boolean isSpamBot = !"".equals(cylon);
            if (isSpamBot) {
                String contextPath = ((WebRequestCycle)getRequestCycle())
                    .getWebRequest().getHttpServletRequest().getContextPath();
                setResponsePage(new RedirectPage(contextPath + "/NoToasters.html"));
                return;
            }
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
            } else if (!checkContentType(fupload.getContentType())) {
                error("Only images of types png, jpg, and gif are allowed.");
                return;
            } else {
                imageEntry = new ImageEntry();

                imageEntry.setContentType(fupload.getContentType());
                imageEntry.setName(fupload.getClientFileName());
                imageEntry.setParent(pasteEntry);
            }

            String privatePastebinName = ((PastebinApplication) Application.get()).getPrivatePastebinName();
            PrivatePastebin privatePastebin = privatePastebinDAO.lookupPrivatePastebin(privatePastebinName);
            pasteEntry.setPrivatePastebin(privatePastebin);

            // Save the data
            pasteService.save(pasteEntry);
            if (imageEntry != null) {
                try {
                    imageService.save(imageEntry, fupload.getInputStream());
                } catch (IOException ioe) {
                    // It'd be nice to have a logger so that we could log the error.
                    error("Sorry, there was an error uploading the image.");
                }
            }

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
