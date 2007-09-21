package com.mysticcoders.pastebin.web.pages;

import com.mysticcoders.pastebin.core.ImageService;
import com.mysticcoders.pastebin.core.PasteService;
import com.mysticcoders.pastebin.model.ImageEntry;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.web.PastebinApplication;
import com.mysticcoders.pastebin.web.pages.highlighter.HighlighterTextAreaPanel;
import com.mysticcoders.pastebin.web.model.PasteEntryModel;
import com.mysticcoders.pastebin.web.panels.IncludedContentPanel;
import com.mysticcoders.pastebin.web.panels.PastebinPanel;
import com.mysticcoders.pastebin.web.panels.RecentPostingPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.util.string.StringValueConversionException;

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


        PasteEntryModel pasteEntryModel = new PasteEntryModel(id, getPrivatePastebinName());
        final PasteEntry existingEntry = (PasteEntry) pasteEntryModel.getObject();

        // Add a diff link in here, only if it includes a parent
        add(new BookmarkablePageLink("diff", ViewPastebinPage.class, newPageParametersWithDiff(id)) {
            public boolean isVisible() {
//                return false;
                return existingEntry.getParent() != null;
            }
        });

        // you be viewing it
        existingEntry.touch();

        PasteService pasteService = (PasteService) PastebinApplication.getInstance().getBean("pasteService");

        pasteService.save(existingEntry);

        // Images

        MarkupContainer uploadedImages = new WebMarkupContainer("thumbnails") {
            public boolean isVisible() {
                return existingEntry.getImages().size() > 0;
            }
        };
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

                String url = getRequestCycle().urlFor(ref) + "/imageEntryId/" + image.getId();
                ExternalLink eLink = new ExternalLink("imageLink", url);
                item.add(eLink);

                url = url + "/thumbnail/true";
                AttributeModifier modifier = new AttributeModifier(
                        "src",
                        new Model(url)
                );
                Label label = new Label("thumbnailImage", "");
                eLink.add(label);
                label.add(modifier);
                eLink.setVisible(available);

                label = new Label("thumbnailImageNA", "");
                item.add(label);
                label.add(modifier);
                label.setVisible(!available);
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
                item.add(link);

                link.add(new Label("name", pasteEntry.getName()));

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

        String url = getRequestCycle().urlFor(ref) + "/pasteEntryId/" + existingEntry.getId();

        add(new ExternalLink("downloadPost", url));

        WebMarkupContainer parentStatus = new WebMarkupContainer("parentStatus") {

            public boolean isVisible() {
                return existingEntry.getParent() != null;
            }
        };
        add(parentStatus);
        parentStatus.setRenderBodyOnly(true);

        if (existingEntry.getParent() != null) {
            BookmarkablePageLink parentPageLink = ViewPastebinPage.newLink("parentPost", existingEntry.getParent().getId());
            parentPageLink.setRenderBodyOnly(true);
            parentStatus.add(parentPageLink);
            
            parentPageLink.add(new Label("parentName", existingEntry.getParent().getName()));
        }
        
        replace(new IncludedContentPanel("headerIncludedContent"));

        add(new HighlighterTextAreaPanel("codePanel", new Model(existingEntry.getCode()),
                (existingEntry.getHighlight()!=null?existingEntry.getHighlight():"None"))
                .setRenderBodyOnly( true ));

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

}