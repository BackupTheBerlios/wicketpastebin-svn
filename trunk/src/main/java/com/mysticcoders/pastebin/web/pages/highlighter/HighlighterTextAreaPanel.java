package com.mysticcoders.pastebin.web.pages.highlighter;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.JavaScriptReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.*;

/**
 * HighlighterTextAreaPanel
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class HighlighterTextAreaPanel extends Panel {
	private static final long serialVersionUID = 1L;
    private static Map<String, String> languageMap = new LinkedHashMap<String, String>();

    static {
        languageMap.put("No", "shBrushNoHighlight.js");
        languageMap.put("C#", "shBrushCSharp.js");
        languageMap.put("CSS", "shBrushCss.js");
        languageMap.put("Delphi/Pascal", "shBrushDelphi.js");
        languageMap.put("Java", "shBrushJava.js");
        languageMap.put("Javascript", "shBrushJScript.js");
        languageMap.put("PHP", "shBrushPhp.js");
        languageMap.put("Python", "shBrushPython.js");
        languageMap.put("Ruby", "shBrushRuby.js");
        languageMap.put("SQL", "shBrushSql.js");
        languageMap.put("Visual Basic/VB.NET", "shBrushVb.js");
        languageMap.put("XML/XHTML", "shBrushXml.js");
    }

    private static Map<String, String> languageAliasMap = new HashMap<String, String>();

    static {
        languageAliasMap.put("No", "no-highlight");
        languageAliasMap.put("C#", "c#");
        languageAliasMap.put("CSS", "css");
        languageAliasMap.put("Delphi/Pascal", "pascal");
        languageAliasMap.put("Java", "java");
        languageAliasMap.put("Javascript", "javascript");
        languageAliasMap.put("PHP", "php");
        languageAliasMap.put("Python", "python");
        languageAliasMap.put("Ruby", "ruby");
        languageAliasMap.put("SQL", "sql");
        languageAliasMap.put("Visual Basic/VB.NET", "vb");
        languageAliasMap.put("XML/XHTML", "xml");
    }


    public static List<String> getLanguageKeys() {
        return new ArrayList<String>(
                languageMap.keySet()
        );
    }

    public HighlighterTextAreaPanel(String id, IModel model) {
        this(id, model, null);
    }

    public HighlighterTextAreaPanel(String id, IModel model, String language) {
        super(id);
        
        add(
        		HeaderContributor.forCss(
        				HighlighterTextAreaPanel.class, "SyntaxHighlighter.css"
        			)
        	);

        TextArea codeTextArea = new TextArea("code", model);
        add(codeTextArea);

        add(new JavaScriptReference("highlighterCore", HighlighterTextAreaPanel.class, "shCore.js"));

        if (language != null) {
            if (languageMap.get(language) != null) {
                add(new JavaScriptReference("highlighterLanguage", HighlighterTextAreaPanel.class,
                        languageMap.get(language)));
            }

            if (languageAliasMap.get(language) != null) {
                codeTextArea.add(new AttributeModifier("class", true, new Model(languageAliasMap.get(language))));
            }
        }

    }
}