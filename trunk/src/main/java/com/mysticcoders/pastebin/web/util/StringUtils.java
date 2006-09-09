package com.mysticcoders.pastebin.web.util;

/**
 * StringUtils
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class StringUtils {


    public static String removeHighlightTag(String code) {
        String[] splitCode = code.split("\n");
        StringBuilder codeBuffer = new StringBuilder();
        for(String codeLine : splitCode) {
            if(codeLine.startsWith("@@")) {
                codeLine = codeLine.substring(2, codeLine.length());
            }
            codeBuffer.append(codeLine).append("\n");
        }

        return codeBuffer.toString();
    }
}
