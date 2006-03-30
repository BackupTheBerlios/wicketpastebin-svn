/*
 * $Id: PasteListXmlPage.java,v 1.1 2005/10/03 20:58:46 eelco12 Exp $
 * $Revision: 1.1 $
 * $Date: 2005/10/03 20:58:46 $
 *
 * ====================================================================
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


import com.mysticcoders.pastebin.dao.PasteEntryDAO;
import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.web.PastebinApplication;
import wicket.AttributeModifier;
import wicket.Component;
import wicket.PageMap;
import wicket.PageParameters;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.AbstractReadOnlyDetachableModel;
import wicket.model.IModel;
import wicket.model.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Look ma, you can use plain XML too with Wicket.
 *
 * @author Eelco Hillenius
 */
public class PasteListXmlPage extends WebPage {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");

    /**
     * Constructor
     */
    public PasteListXmlPage() {
        add(
                new ListView("pastes", new PasteEntriesModel()) {
                    public void populateItem(ListItem item) {
                        // paste element
                        PasteEntry entry = (PasteEntry) item.getModelObject();
                        item.add(new AttributeModifier("creationtime", new Model(DATE_FORMAT.format(entry.getCreated()))));
                        item.add(new AttributeModifier("imagecount", new Model(entry.getImages().size())));
                        item.add(new AttributeModifier("pasteid", new Model(entry.getId())));

                        // channel element
                        StringBuilder sb = new StringBuilder("<![CDATA[");
                        sb.append(entry.getChannel() == null ? "" : entry.getChannel());
                        sb.append("]]>");
                        Label label = new Label("channel", new Model(sb.toString()));
                        label.setEscapeModelStrings(false);
                        label.setRenderBodyOnly(true);
                        item.add(label);

                        // line1 element
                        sb = new StringBuilder("<![CDATA[");
                        sb.append(entry.getCode().split("\\n")[0]);
                        sb.append("]]>");
                        label = new Label("line1", new Model(sb.toString()));
                        label.setEscapeModelStrings(false);
                        label.setRenderBodyOnly(true);
                        item.add(label);

                        // url element
                        PageParameters parms = new PageParameters();
                        parms.put("0", entry.getId());
                        String url =
                                getPage().urlFor(
                                        PageMap.forName(PageMap.DEFAULT_NAME),
                                        ViewPastebinPage.class, parms
                                );
                        label = new Label("url", url);
                        label.setEscapeModelStrings(false);
                        label.setRenderBodyOnly(true);
                        item.add(label);

                        // user element
                        sb = new StringBuilder("<![CDATA[");
                        sb.append(entry.getName());
                        sb.append("]]>");
                        label = new Label("user", sb.toString());
                        label.setEscapeModelStrings(false);
                        label.setRenderBodyOnly(true);
                        item.add(label);
                    }
                }
        );
    }

    /**
     * @see wicket.MarkupContainer#getMarkupType()
     */
    public String getMarkupType() {
        return "xml";
    }

    class PasteEntriesModel extends AbstractReadOnlyDetachableModel {
        PasteEntriesModel() {
            super();
            onAttach();
        }

        public IModel getNestedModel() {
            return null;
        }

        public void onAttach() {
            PasteEntryDAO dao = (PasteEntryDAO) PastebinApplication.getInstance().getBean("pasteEntryDAO");
            list = dao.getPreviousEntriesList(10);
        }

        private List<PasteEntry> list;

        public void onDetach() {
            list = null;
        }

        public Object onGetObject(Component comp) {
            return list;
        }
    }
}
