package com.mysticcoders.pastebin.util;

import wicket.protocol.http.WebRequestCycle;
import wicket.protocol.http.servlet.ServletWebRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * CookieUtils
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class CookieUtils {

    private static int defaultMaxAge = 3600 * 24 * 30; // 30 days

    public static void setDefaultMaxAge(int maxAge) {
        CookieUtils.defaultMaxAge = maxAge;
    }

    public static String getCookie(String name, WebRequestCycle cycle) {
        HttpServletRequest request = ((ServletWebRequest) cycle.getWebRequest()).getHttpServletRequest();

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String getDecodedCookie(String name, WebRequestCycle cycle) {
        String encodedValue = getCookie(name, cycle);

        if(encodedValue==null) return null;

        sun.misc.BASE64Decoder base64dec = new sun.misc.BASE64Decoder();

        String decodedValue = null;

        try {
            decodedValue = new String(base64dec.decodeBuffer(encodedValue));
        } catch(IOException e) {
            e.printStackTrace();
        }

        return decodedValue;

    }

    public static void setCookie(String name, String value,
                                 WebRequestCycle cycle) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(defaultMaxAge);
        setCookie(cookie, cycle);
    }


    public static void setEncodedCookieValues(String name, String[] values, WebRequestCycle cycle) {
        sun.misc.BASE64Encoder base64enc = new sun.misc.BASE64Encoder();

        StringBuffer tValues = new StringBuffer();
        for(int i=0; i<values.length; i++) {
            tValues.append(base64enc.encode(new String(values[i] + (i<(values.length-1)?",":"")).getBytes()));
        }

        setCookie(name, tValues.toString(), cycle);
    }

    // This should not exceed 64-characters or problems will occur
    public static void setEncodedCookie(String name, String value, WebRequestCycle cycle) {
        sun.misc.BASE64Encoder base64enc = new sun.misc.BASE64Encoder();
        String encodedValue = base64enc.encode(new String(value).getBytes());

        System.out.println("encodedValue:"+encodedValue);
        setCookie(name, encodedValue, cycle);
    }

    public static void setCookie(Cookie cookie, WebRequestCycle cycle) {
        cycle.getWebResponse().getHttpServletResponse().addCookie(cookie);
    }

    public static void removeCookie(String name, WebRequestCycle cycle) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(-1);
        setCookie(cookie, cycle);
    }
}