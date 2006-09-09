/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mysticcoders.pastebin.web.pages;


import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.web.model.PasteEntriesModel;
import com.mysticcoders.pastebin.web.PastebinApplication;
import wicket.AttributeModifier;
import wicket.PageMap;
import wicket.PageParameters;
import wicket.Application;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A page that will create an XML document enumerating the latest 10 entries.
 *
 * @author pchapman
 */
public class PasteListXmlPage extends WebPage {
    // CONSTANTS

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
    private static final long serialVersionUID = 1L;

    // CONSTRUCTORS

    /**
     * Creates a new instance.  No parameters are necessary.
     */
    public PasteListXmlPage() {
        String privatePastebinName = ((PastebinApplication) Application.get()).getPrivatePastebinName();
        new ListView<PasteEntry>(this, "pastes", new PasteEntriesModel(privatePastebinName)) {
            private static final long serialVersionUID = 1L;

            public void populateItem(ListItem<PasteEntry> item) {
                // paste element
                PasteEntry entry = (PasteEntry) item.getModelObject();
                item.add(new AttributeModifier("creationtime", new Model(DATE_FORMAT.format(entry.getCreated()))));
                item.add(new AttributeModifier("imagecount", new Model(entry.getImages().size())));
                item.add(new AttributeModifier("pasteid", new Model(entry.getId())));

                // channel element
                StringBuilder sb = new StringBuilder("<![CDATA[");
                sb.append(entry.getChannel() == null ? "" : entry.getChannel());
                sb.append("]]>");
                Label label = new Label(item, "channel", new Model(sb.toString()));
                label.setEscapeModelStrings(false);
                label.setRenderBodyOnly(true);

                        // url element
                        PageParameters parms = new PageParameters();
                        parms.put("0", entry.getId());
                        String url =
                                getPage().urlFor(
                                        PageMap.forName(PageMap.DEFAULT_NAME),
                                        ViewPastebinPage.class, parms
                                ).toString();
                        label = new Label(item, "url", url);
                        label.setEscapeModelStrings(false);
                        label.setRenderBodyOnly(true);

                // user element
                sb = new StringBuilder("<![CDATA[");
                sb.append(entry.getName());
                sb.append("]]>");
                label = new Label(item, "user", sb.toString());
                label.setEscapeModelStrings(false);
                label.setRenderBodyOnly(true);
            }
        };
    }
        /**
         * @see wicket.MarkupContainer#getMarkupType()
         */
        public String getMarkupType()
        {
            return "xml";
        }
}
