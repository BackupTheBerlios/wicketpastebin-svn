package com.mysticcoders.pastebin.web.pages;

import com.mysticcoders.pastebin.core.ImageService;
import com.mysticcoders.pastebin.core.PasteService;
import com.mysticcoders.pastebin.dao.PasteEntryDAO;
import com.mysticcoders.pastebin.model.ImageEntry;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.web.PastebinApplication;
import com.mysticcoders.pastebin.web.model.PasteEntryModel;
import com.mysticcoders.pastebin.web.panels.DiffCodePanel;
import com.mysticcoders.pastebin.web.panels.LineNumberCodePanel;
import com.mysticcoders.pastebin.web.panels.PastebinPanel;
import com.mysticcoders.pastebin.web.panels.RecentPostingPanel;
import wicket.AttributeModifier;
import wicket.PageParameters;
import wicket.ResourceReference;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.link.ExternalLink;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;
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

    public static PageParameters newPageParameters(long entryId) {
        PageParameters params = new PageParameters();
        params.put("0", entryId);
        return params;
    }

    public static PageParameters newPageParametersWithDiff(long entryId) {
        PageParameters params = new PageParameters();
        params.put("0", entryId);
        params.put("1", "diff");
        return params;
    }

    public static BookmarkablePageLink newLink(String id, long entryId) {
        return new BookmarkablePageLink(id, ViewPastebinPage.class, newPageParameters(entryId));

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

        PasteEntryModel pasteEntryModel = new PasteEntryModel(id);

        final PasteEntry existingEntry = (PasteEntry)pasteEntryModel.getObject( null );

        // Add a diff link in here, only if it includes a parent
        BookmarkablePageLink diffLink = new BookmarkablePageLink("diff", ViewPastebinPage.class, newPageParametersWithDiff(id)) {
            public boolean isVisible() {
//                return false;
                return existingEntry.getParent() != null;
            }
        };

        add(diffLink);

        // you be viewing it
        existingEntry.touch();

        PasteService pasteService = (PasteService) PastebinApplication.getInstance().getBean("pasteService");

        pasteService.save(existingEntry);

        // Images

        WebMarkupContainer uploadedImages = new WebMarkupContainer("thumbnails") {

            public boolean isVisible() {
                return existingEntry.getImages().size() > 0;
            }

        };
        uploadedImages.setRenderBodyOnly(true);
        add(uploadedImages);

        uploadedImages.add(new ListView("thumbnail", new ArrayList(existingEntry.getImages())) {        // TODO this is a Set grabbed from hibernate, using a detachable model, will this get GC'd?


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

                String url = getRequestCycle().urlFor(ref)  + "?imageEntryId=" + image.getId();
                ExternalLink eLink = new ExternalLink("imageLink", url);
                url = url + "&amp;thumbnail=true";
                AttributeModifier modifier = new AttributeModifier(
                        "src",
                        new Model(url)
                );
                Label label = new Label("thumbnailImage", "");
                label.add(modifier);
                eLink.add(label);
                eLink.setVisible(available);
                item.add(eLink);
                label = new Label("thumbnailImageNA", "");
                label.add(modifier);
                label.setVisible(!available);
                item.add(label);
            }
        });

        // Amendments

        WebMarkupContainer postedAmendments = new WebMarkupContainer("postedAmendments") {

            public boolean isVisible() {
                return existingEntry.getChildren().size() > 0;
            }

        };

        add(postedAmendments);

        postedAmendments.add(new ListView("postedAmendment", new ArrayList(existingEntry.getChildren())) {


            public void populateItem(final ListItem item) {
                final PasteEntry pasteEntry = (PasteEntry) item.getModelObject();

                BookmarkablePageLink link = ViewPastebinPage.newLink("amendmentLink", pasteEntry.getId());
                link.add(new Label("name", pasteEntry.getName()));
                item.add(link);

                SimpleDateFormat formatter = new SimpleDateFormat("EEE d'th' MMM HH:mm");
                String formattedDate = formatter.format(pasteEntry.getCreated());

                item.add(new Label("amendmentCreated", formattedDate));

            }
        });

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

        add(new Label("status", labelText.toString()).setRenderBodyOnly(true));

        String postStatus = "Viewed: " + existingEntry.getViewCount() + " time" + (existingEntry.getViewCount() > 1 ? "s" : "") + " | Last viewed: " + formatter.format(existingEntry.getLastViewed());

        add(new Label("postStatus", postStatus).setRenderBodyOnly(true));


        ResourceReference ref = new ResourceReference("exportResource");

        String url = getRequestCycle().urlFor(ref) + "?pasteEntryId=" + existingEntry.getId();

        add(new ExternalLink("downloadPost", url));

        WebMarkupContainer parentStatus = new WebMarkupContainer("parentStatus") {

            public boolean isVisible() {
                return existingEntry.getParent() != null;
            }
        };
        parentStatus.setRenderBodyOnly(true);

        if (existingEntry.getParent() != null) {
            BookmarkablePageLink parentPageLink = ViewPastebinPage.newLink("parentPost", existingEntry.getParent().getId());
            parentPageLink.setRenderBodyOnly(true);

            Label parentNameLabel = new Label("parentName", existingEntry.getParent().getName());
            parentStatus.add(parentPageLink.add(parentNameLabel));
        }

        add(parentStatus);

        Panel codePanel = (diff && existingEntry.getParent() != null
                ? new DiffCodePanel("codePanel",
                existingEntry.getParent().getCode(),
                existingEntry.getCode(),
                existingEntry.getHighlight(),
                getCodeHighlighter()) :
                new LineNumberCodePanel("codePanel",
                        existingEntry.getCode(),
                        existingEntry.getHighlight(),
                        getCodeHighlighter()));

        add(codePanel);

        add(new RecentPostingPanel("recentPosts", (existingEntry != null ? existingEntry.getId() : null))
                .setRenderBodyOnly(true));

        add(new PastebinPanel("pastebinPanel", existingEntry) {
            protected String getPageUrl() {
                StringBuffer url = humanReadableUrl((WebRequestCycle) super.getRequestCycle(), true);
                if (url != null) return url.toString();
                return "";
            }
        }.setRenderBodyOnly(true));


    }

    /*
               Set images = pasteEntry.getImages();
               if (images == null || images.size() == 0) {
                   // bugus adds  They are set invisible at the end.
                   ResourceLink rLink = new ResourceLink("imageLink", (Resource)null);
                   rLink.add(new Image("thumbnailImage"));
                   rLink.setVisible(false);
                   item.add(rLink);
               } else {
                   // For now, we assume only one image.
                   final ImageEntry image = (ImageEntry)images.iterator().next();
                   ResourceReference ref = new ResourceReference( "imageResource");

                   String url = getPage().urlFor(ref.getPath() + "?imageEntryId="+ image.getId());
                   ExternalLink eLink = new ExternalLink("imageLink", url);
                   url = getPage().urlFor(ref.getPath() + "?imageEntryId="+ image.getId() + "&thumbnail");
                   AttributeModifier modifier = new AttributeModifier(
                           "src",
                           new Model(url)
                       );
                   Label label = new Label("thumbnailImage", "Thumbnail");
                   label.add(modifier);
                   eLink.add(label);
                   item.add(eLink);
               }

    */

}
