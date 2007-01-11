package com.mysticcoders.pastebin.util;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.FieldPosition;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BotInterface
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class BotInterface {

    private MessageFormat botQuery;
    private MessageFormat messageToSend;

    private static final Logger log = LoggerFactory.getLogger(BotInterface.class);

    public void send(String from, String target, String postLocation) {
        // if this is a channel, then check to make sure its on the list of supported        
        if(target==null) return;

        if (channels != null && target.startsWith("#")) {
            boolean validChannel = false;
            for (int i = 0; i < channels.length; i++) {
                if (target.equals(channels[i])) {
                    validChannel = true;
                }
            }

            if (!validChannel) return;
        }

        //login={0}&password={1}&submission=sendMessage&bot={2}&target={3}&message={4}
        //drone_message=See {0}''s paste at:  {1}

        log.debug("postLocation:" + postLocation);

        log.debug("channels:" + channels);

        if (botQuery == null) return;

        StringBuffer finalMessageBuff = new StringBuffer();
        messageToSend.format(new Object[]{from, postLocation}, finalMessageBuff, new FieldPosition(0));

        String finalMessage = null;
        try {
            finalMessage = URLEncoder.encode(finalMessageBuff.toString(), "utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            return;
        }

        StringBuffer botUrl = new StringBuffer();
        botQuery.format(new Object[]{user, password, botName, target, finalMessage}, botUrl, new FieldPosition(0));


        log.debug("botUrl:" + botUrl);
        log.debug("finalMessage:" + finalMessage);

        sendToBot(botUrl.toString());
    }

    private void sendToBot(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            byte[] data = IOUtils.readStream(conn.getInputStream());
            log.debug("data:" + data);
        } catch (Exception e) {
            log.error("Error while trying to open URL connection", e);
        }
    }

    /* Spring Injected */
    private String url;
    private String[] channels;
    private String user;
    private String password;
    private String botName;
    private String message;

    public void setUrl(String url) {
        this.url = url;

        if (url != null) {
            botQuery = new MessageFormat(url);
        }
    }

    public void setMessage(String message) {
        this.message = message;

        if (message != null) {
            messageToSend = new MessageFormat(message);
        }
    }

    public void setChannels(String[] channels) {
        this.channels = channels;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }
}
