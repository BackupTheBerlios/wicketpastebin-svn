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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import javax.servlet.http.HttpServletRequest;
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
        add(new ListView("pastes", new PasteEntriesModel(privatePastebinName)) {
            private static final long serialVersionUID = 1L;

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
                item.add(label);
                label.setEscapeModelStrings(false);
                label.setRenderBodyOnly(true);

                // line1 element
                sb = new StringBuilder("<![CDATA[");
                sb.append(entry.getCode().split("\\n|\\r\\n")[0]);
                sb.append("]]>");
                label = new Label("line1", new Model(sb.toString()));
                item.add(label);
                label.setEscapeModelStrings(false);
                label.setRenderBodyOnly(true);

                // url element
                HttpServletRequest req = ((WebRequest)getRequest()).getHttpServletRequest();
                StringBuilder url = new StringBuilder();
//                StringBuilder url = new StringBuilder("http://");
//                url.append(req.getServerName());
//                if (req.getServerPort() != 80) {
//                    url.append(':').append(req.getServerPort());
//                }
                url.append(req.getContextPath());
                url.append('/');
                PageParameters parms = new PageParameters();
                parms.put("0", String.valueOf(entry.getId()));
                url.append(
                        getPage().urlFor(
                                PageMap.forName(PageMap.DEFAULT_NAME),
                                ViewPastebinPage.class, parms
                        )
                );
                label = new Label("url", url.toString());
                item.add(label);
                label.setEscapeModelStrings(false);
                label.setRenderBodyOnly(true);

                // user element
                sb = new StringBuilder("<![CDATA[");
                sb.append(entry.getName());
                sb.append("]]>");
                label = new Label("user", sb.toString());
                item.add(label);
                label.setEscapeModelStrings(false);
                label.setRenderBodyOnly(true);
            }
        });
    }
        /**
         * @see org.apache.wicket.MarkupContainer#getMarkupType()
         */
        public String getMarkupType()
        {
            return "xml";
        }
}
