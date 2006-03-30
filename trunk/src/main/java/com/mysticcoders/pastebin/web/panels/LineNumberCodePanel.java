package com.mysticcoders.pastebin.web.panels;

import com.mysticcoders.pastebin.model.PasteEntry;
import com.mysticcoders.pastebin.web.util.CodeHighlighter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wicket.AttributeModifier;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LineNumberCodePanel
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class LineNumberCodePanel extends Panel {

    static Log log = LogFactory.getLog(LineNumberCodePanel.class);

    private boolean[] userHighlight;        // TODO i know ... i know ... this is such a hack, fix it later :-)

    public LineNumberCodePanel(String id) {
        super(id);
    }

    public LineNumberCodePanel(String id, final PasteEntry pasteEntry, final CodeHighlighter codeHighlighter) {
        super(id);

        final String highlight = pasteEntry.getHighlight();
        String code = pasteEntry.getCode();

        String[] splitCode = null;

        String[] originalCode = code.split("\n");       // saved only for highlighting user shit

        List list = getSplitHighlightCode(code, highlight, codeHighlighter);

        List<String> lineNumbers = new ArrayList<String>();

        userHighlight = new boolean[list.size()];

        for (int i = 0; i < list.size(); i++) {
            lineNumbers.add("" + (i + 1));
            if (originalCode.length > i) {         // just in case the highlighter does something completely random
                if (originalCode[i].startsWith("@@")) {
                    userHighlight[i] = true;
                } else {
                    userHighlight[i] = false;
                }
            }
        }

/*
        add(createLabelListView("lineNumbers", "lineNumber", lineNumbers));
*/

        add(new ListView("codeView", list) {

            public void populateItem(final ListItem item) {
                final String codeLine = (String) item.getModelObject();

                String formattedCodeLine = null;

                if (highlight == null || highlight.equalsIgnoreCase("no")) {
                    formattedCodeLine = codeLine.replaceAll("\\t", "&#160;&#160;&#160;&#160;");     // replace tabs with 4 non-breaking spaces
                    formattedCodeLine = formattedCodeLine.replaceAll("\\s", "&#160;");              // replace space with non-breaking space
                } else {
                    formattedCodeLine = codeLine;
                }

                if (userHighlight.length > item.getIndex() && userHighlight[item.getIndex()]) {
                    item.add(new AttributeModifier("class", true, new Model("code-highlight")));
                    formattedCodeLine = formattedCodeLine.substring(2, formattedCodeLine.length());
                }

                Label codeLineLabel = new Label("codeLine", formattedCodeLine);

                if (highlight != null && !highlight.equalsIgnoreCase("no")) {
                    codeLineLabel.setEscapeModelStrings(false);
                }
//                codeLineLabel.setRenderBodyOnly(true);
                item.add(codeLineLabel);

            }
        });


    }


    private List<String> getSplitHighlightCode(String code, String highlight, CodeHighlighter codeHighlighter) {
        String[] splitCode = null;

        if (highlight != null && !highlight.equalsIgnoreCase("no")) {
            String highlightResult = codeHighlighter.highlight(highlight.toLowerCase(), code);

            log.debug("highlightResult:" + highlightResult);
            if (highlightResult != null) {
                splitCode = highlightResult.split("<br />");
            } else {
                StringEscapeUtils.escapeHtml(code);
                splitCode = code.split("\n");
            }
        } else {
            StringEscapeUtils.escapeHtml(code);
            splitCode = code.split("\n");
        }

        // this is bad, figure out a better way to dump the last line.
        List tempList = Arrays.asList(splitCode);
        List list = new ArrayList(tempList);

        if (highlight != null && !highlight.equalsIgnoreCase("no")) {      // dont remove this if not highlight
            list.remove(list.size() - 1);             // the highlighter adds an extra line to the file
        }

        return list;
    }

/*
    private ListView createLabelListView(String listViewId, final String itemId, List list) {
        ListView listView = new ListView(listViewId, list) {

            public void populateItem(final ListItem item) {
                final String labelItem = (String) item.getModelObject();

                if(userHighlight.length > item.getIndex() && userHighlight[item.getIndex()]) {
                    item.add(new AttributeModifier("class", true, new Model("code-highlight")));
                }

                item.add(new Label(itemId, labelItem).setRenderBodyOnly(true));
            }
        };

        listView.setRenderBodyOnly( true );
        return listView;
    }
*/
}
