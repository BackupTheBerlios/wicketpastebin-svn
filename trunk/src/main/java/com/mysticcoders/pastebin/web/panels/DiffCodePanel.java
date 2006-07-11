package com.mysticcoders.pastebin.web.panels;

import com.mysticcoders.pastebin.web.util.CodeHighlighter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;
import wicket.AttributeModifier;
import wicket.MarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DiffCodePanel
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class DiffCodePanel extends Panel {

    static Log log = LogFactory.getLog(DiffCodePanel.class);

    public DiffCodePanel(MarkupContainer parent, String id) {
        super(parent, id);
    }

    List<String> oldLineNumbers = new ArrayList<String>();
    List<String> newLineNumbers = new ArrayList<String>();
    List<String> modifiers = new ArrayList<String>();

    public DiffCodePanel(MarkupContainer parent, String id, String oldCode, String newCode, final String highlight, final CodeHighlighter codeHighlighter) {
        super(parent, id);

        String[] parentEntry = oldCode.split("\n");
        String[] currentEntry = newCode.split("\n");

        for (int i = 0; i < parentEntry.length; i++) {
            parentEntry[i] = parentEntry[i].substring(0, (parentEntry[i].lastIndexOf('\r') != -1 ? parentEntry[i].lastIndexOf('\r') : parentEntry[i].length()));
        }
        for (int i = 0; i < currentEntry.length; i++) {
            currentEntry[i] = currentEntry[i].substring(0, (currentEntry[i].lastIndexOf('\r') != -1 ? currentEntry[i].lastIndexOf('\r') : currentEntry[i].length()));
        }

        List<Difference> diffs = (new Diff(parentEntry, currentEntry)).diff();
        int parentLineNumber = 0;
        int currentLineNumber = 0;

        for (Difference diff : diffs) {
            int delStart = diff.getDeletedStart();
            int delEnd = diff.getDeletedEnd();
            int addStart = diff.getAddedStart();
            int addEnd = diff.getAddedEnd();

            if (delEnd != Difference.NONE) {

                for (; parentLineNumber < delStart; ++parentLineNumber, ++currentLineNumber) {
                    oldLineNumbers.add("" + (parentLineNumber + 1));
                    newLineNumbers.add("" + (currentLineNumber + 1));
                    modifiers.add("");
                }

                for (; parentLineNumber <= delEnd; ++parentLineNumber) {
                    oldLineNumbers.add("" + (parentLineNumber + 1));
                    newLineNumbers.add("");
                    modifiers.add("-");
                }

            }

            if (addEnd != Difference.NONE) {
                for (; currentLineNumber < addStart; ++currentLineNumber, ++parentLineNumber) {
                    oldLineNumbers.add("" + (parentLineNumber + 1));
                    newLineNumbers.add("" + (currentLineNumber + 1));
                    modifiers.add("");
                }
                for (; currentLineNumber <= addEnd; ++currentLineNumber) {
                    oldLineNumbers.add("");
                    newLineNumbers.add("" + (currentLineNumber + 1));
                    modifiers.add("+");
                }
            }
        }

        int lastLineNumber = (currentEntry.length > parentEntry.length ? currentEntry.length : parentEntry.length);

        for (; currentLineNumber < lastLineNumber; ++currentLineNumber, ++parentLineNumber) {
            oldLineNumbers.add("" + (parentLineNumber + 1));
            newLineNumbers.add("" + (currentLineNumber + 1));
            modifiers.add("");
        }

/*
        add(createLabelListView("oldLineNumbers", "lineNumber", oldLineNumbers));
        add(createLabelListView("newLineNumbers", "lineNumber", newLineNumbers));
        add(createLabelListView("modifierItems", "modifierItem", modifiers));
*/


        String[] splitCode = getSplitHighlightCode(newCode, highlight, codeHighlighter);
        String[] parentSplitCode = getSplitHighlightCode(oldCode, highlight, codeHighlighter);

        // this is bad, figure out a better way to dump the last line.
        List tempList = Arrays.asList(splitCode);
        List list = new ArrayList(tempList);

        if (highlight != null && !highlight.equalsIgnoreCase("no")) {      // dont remove this if not highlight
            list.remove(list.size() - 1);             // the highlighter adds an extra line to the file
        }

/*
        String[] splitCode = getSplitHighlightCode(pasteEntry.getCode(), highlight, codeHighlighter);
        String[] parentSplitCode = getSplitHighlightCode(pasteEntry.getParent().getCode(), highlight, codeHighlighter);

        List list = new ArrayList(Arrays.asList(splitCode));
*/

        for (int i = 0; i < parentSplitCode.length; i++) {
            if (modifiers.get(i).equals("-")) {
                list.add(i, parentSplitCode[i]);
            }
        }


        new ListView<String>(this, "codeView", list) {

            public void populateItem(final ListItem item) {
                final String codeLine = (String) item.getModelObject();

                String formattedCodeLine = null;

                if (highlight == null || highlight.equalsIgnoreCase("no")) {
                    formattedCodeLine = codeLine.replaceAll("\\t", "&#160;&#160;&#160;&#160;");
                    formattedCodeLine = formattedCodeLine.replaceAll("\\s", "&#160;");
                } else {
                    formattedCodeLine = codeLine;
                }
                if (formattedCodeLine.startsWith("@@")) {
                    item.add(new AttributeModifier("class", true, new Model("code-highlight")));
                    formattedCodeLine = formattedCodeLine.substring(2, formattedCodeLine.length());
                }

                if (modifiers.get(item.getIndex()).equals("+")) {
                    item.add(new AttributeModifier("class", true, new Model("diff_added")));
                } else if (modifiers.get(item.getIndex()).equals("-")) {
                    item.add(new AttributeModifier("class", true, new Model("diff_deleted")));
                } else {
                    item.add(new AttributeModifier("class", true, new Model("diff_regular")));
                }

                Label codeLineLabel = new Label(item, "codeLine", formattedCodeLine);

                if (highlight != null && !highlight.equalsIgnoreCase("no")) {
                    codeLineLabel.setEscapeModelStrings(false);
                }
                codeLineLabel.setRenderBodyOnly(true);

                if (oldLineNumbers.size() > item.getIndex()) {
                    String oldLineNumber = oldLineNumbers.get(item.getIndex());
                    new Label(item, "oldLineNumber", oldLineNumber);
                } else {
                    new Label(item, "oldLineNumber", "");
                }

                if (newLineNumbers.size() > item.getIndex()) {
                    String newLineNumber = newLineNumbers.get(item.getIndex());
                    new Label(item, "newLineNumber", newLineNumber);
                } else {
                    new Label(item, "newLineNumber", "");
                }
                if (modifiers.size() > item.getIndex()) {
                    String modifier = modifiers.get(item.getIndex());
                    new Label(item, "modifierItem", modifier);
                } else {
                    new Label(item, "modifierItem", "");
                }

            }

        };

    }


    private String[] getSplitHighlightCode(String code, String highlight, CodeHighlighter codeHighlighter) {
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

        return splitCode;
    }

/*
    private String[] getSplitHighlightCode(String code, String highlight, CodeHighlighter codeHighlighter) {
        String[] splitCode = null;

        if(highlight!=null && !highlight.equalsIgnoreCase("no")) {
            String highlightResult = codeHighlighter.highlight( highlight.toLowerCase(), code );

            System.out.println("highlightResult:"+highlightResult);
            if(highlightResult!=null) {
                splitCode = highlightResult.split("<br />");
            } else {
                splitCode = StringUtils.split(code, "\n");
//                        code.split("\n");
            }
        } else {
            splitCode = code.split("\n");
        }

        return splitCode;
    }
*/

}