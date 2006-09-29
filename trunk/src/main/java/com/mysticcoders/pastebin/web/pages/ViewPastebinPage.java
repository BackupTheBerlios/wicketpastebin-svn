package com.mysticcoders.pastebin.web.pages;

import com.mysticcoders.pastebin.core.ImageService;
import com.mysticcoders.pastebin.core.PasteService;
import com.mysticcoders.pastebin.model.ImageEntry;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.web.PastebinApplication;
import com.mysticcoders.pastebin.web.pages.highlighter.HighlighterTextAreaPanel;
import com.mysticcoders.pastebin.web.model.PasteEntryModel;
import com.mysticcoders.pastebin.web.panels.DiffCodePanel;
import com.mysticcoders.pastebin.web.panels.IncludedContentPanel;
import com.mysticcoders.pastebin.web.panels.LineNumberCodePanel;
import com.mysticcoders.pastebin.web.panels.PastebinPanel;
import com.mysticcoders.pastebin.web.panels.RecentPostingPanel;
import wicket.AttributeModifier;
import wicket.MarkupContainer;
import wicket.PageParameters;
import wicket.ResourceReference;
import wicket.behavior.HeaderContributor;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.PackageResourceReference;
import wicket.markup.html.resources.CompressedPackageResourceReference;
import wicket.markup.html.resources.JavaScriptReference;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.link.ExternalLink;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.Model;
import wicket.protocol.http.WebRequestCycle;
import wicket.util.string.StringValueConversionException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * ViewPastebinPage
 * <p/>
 * Created by: Andrew Lombardi
 * Modified by: Philip A. Chapman  Added the view of uploaded images.
 * Copyright 2004 Mystic Coders, LLC
 */
public class ViewPastebinPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public static PageParameters newPageParameters(long entryId) {
        PageParameters params = new PageParameters();
        params.put("0", String.valueOf(entryId));
        return params;
    }

    public static PageParameters newPageParametersWithDiff(long entryId) {
        PageParameters params = new PageParameters();
        params.put("0", String.valueOf(entryId));
        params.put("1", "diff");
        return params;
    }

    public static BookmarkablePageLink newLink(MarkupContainer parent, String id, long entryId) {
        return new BookmarkablePageLink(parent, id, ViewPastebinPage.class, newPageParameters(entryId));

    }

    public ViewPastebinPage() {
    }

    @SuppressWarnings("unchecked")
    public ViewPastebinPage(PageParameters params) {
        Long id = null;
        boolean diff = false;

        try {
            id = new Long(params.getLong("0"));
            if (params.getString("1") != null && params.getString("1").equalsIgnoreCase("diff")) diff = true;
        } catch (StringValueConversionException e) {
            e.printStackTrace();
            throw new RuntimeException("Entry not found");
        }

        if (id == null) {
            throw new RuntimeException("Entry not found");
        }


        PasteEntryModel pasteEntryModel = new PasteEntryModel(id, getPrivatePastebinName());
        final PasteEntry existingEntry = (PasteEntry) pasteEntryModel.getObject();

        // Add a diff link in here, only if it includes a parent
        new BookmarkablePageLink(this, "diff", ViewPastebinPage.class, newPageParametersWithDiff(id)) {
            public boolean isVisible() {
//                return false;
                return existingEntry.getParent() != null;
            }
        };

        // you be viewing it
        existingEntry.touch();

        PasteService pasteService = (PasteService) PastebinApplication.getInstance().getBean("pasteService");

        pasteService.save(existingEntry);

        // Images

        MarkupContainer uploadedImages = new WebMarkupContainer(this, "thumbnails") {
            public boolean isVisible() {
                return existingEntry.getImages().size() > 0;
            }
        };

        new ListView<ImageEntry>(uploadedImages, "thumbnail", new ArrayList(existingEntry.getImages())) {        // TODO this is a Set grabbed from hibernate, using a detachable model, will this get GC'd?


            public void populateItem(final ListItem item) {
                /*
                * Either imageLink and it's contained img element or the
                * thumbnnailImageNA img will be shown.  Which one depends
                * on whether the image is available.
                */

                final ImageEntry image = (ImageEntry) item.getModelObject();
                ImageService imageService = (ImageService) PastebinApplication.getInstance().getBean("imageService");
                boolean available = imageService.isImageAvailable(image);

                ResourceReference ref = new ResourceReference("imageResource");

                String url = getRequestCycle().urlFor(ref) + "?imageEntryId=" + image.getId();
                ExternalLink eLink = new ExternalLink(item, "imageLink", url);
                url = url + "&amp;thumbnail=true";
                AttributeModifier modifier = new AttributeModifier(
                        "src",
                        new Model(url)
                );
                Label label = new Label(eLink, "thumbnailImage", "");
                label.add(modifier);
                eLink.setVisible(available);

                label = new Label(item, "thumbnailImageNA", "");
                label.add(modifier);
                label.setVisible(!available);
            }
        };

        // Amendments

        WebMarkupContainer postedAmendments = new WebMarkupContainer(this, "postedAmendments") {

            public boolean isVisible() {
                return existingEntry.getChildren().size() > 0;
            }

        };

        new ListView<PasteEntry>(postedAmendments, "postedAmendment", new ArrayList(existingEntry.getChildren())) {

            public void populateItem(final ListItem item) {
                final PasteEntry pasteEntry = (PasteEntry) item.getModelObject();

                BookmarkablePageLink link = ViewPastebinPage.newLink(item, "amendmentLink", pasteEntry.getId());

                new Label(link, "name", pasteEntry.getName());

                SimpleDateFormat formatter = new SimpleDateFormat("EEE d'th' MMM HH:mm");
                String formattedDate = formatter.format(pasteEntry.getCreated());

                new Label(item, "amendmentCreated", formattedDate);

            }
        };

        SimpleDateFormat formatter = new SimpleDateFormat("EEE d'th' MMM HH:mm");
        // TODO should conver the 'th' to an appropriate modifier, 1st, 2nd, 3rd, 4th
        String formattedDate = formatter.format(existingEntry.getCreated());

        StringBuilder labelText = new StringBuilder("Posted by ");
        labelText.append(existingEntry.getName());
        if (
                existingEntry.getChannel() != null &&
                        existingEntry.getChannel().length() > 0
                ) {
            labelText.append(" for channel ");
            labelText.append(existingEntry.getChannel());
        }
        labelText.append(" on ");
        labelText.append(formattedDate);

        new Label(this, "status", labelText.toString()).setRenderBodyOnly(true);

        String postStatus = "Viewed: " + existingEntry.getViewCount() + " time" + (existingEntry.getViewCount() > 1 ? "s" : "") + " | Last viewed: " + formatter.format(existingEntry.getLastViewed());

        new Label(this, "postStatus", postStatus).setRenderBodyOnly(true);


        ResourceReference ref = new ResourceReference("exportResource");

        String url = getRequestCycle().urlFor(ref) + "?pasteEntryId=" + existingEntry.getId();

        new ExternalLink(this, "downloadPost", url);

        WebMarkupContainer parentStatus = new WebMarkupContainer(this, "parentStatus") {

            public boolean isVisible() {
                return existingEntry.getParent() != null;
            }
        };
        parentStatus.setRenderBodyOnly(true);

        if (existingEntry.getParent() != null) {
            BookmarkablePageLink parentPageLink = ViewPastebinPage.newLink(parentStatus, "parentPost", existingEntry.getParent().getId());
            parentPageLink.setRenderBodyOnly(true);

            new Label(parentPageLink, "parentName", existingEntry.getParent().getName());
        }
        
        new IncludedContentPanel(this, "headerIncludedContent");

        new HighlighterTextAreaPanel(this, "codePanel", new Model(existingEntry.getCode()),
                (existingEntry.getHighlight()!=null?existingEntry.getHighlight():"None"))
                .setRenderBodyOnly( true );

        new RecentPostingPanel(this, "recentPosts", (existingEntry != null ? existingEntry.getId() : null))
                .setRenderBodyOnly(true);

        new PastebinPanel(this, "pastebinPanel", existingEntry) {
            protected String getPageUrl() {
                StringBuffer url = humanReadableUrl((WebRequestCycle) super.getRequestCycle(), true);
                if (url != null) return url.toString();
                return "";
            }
        }.setRenderBodyOnly(true);


    }

}