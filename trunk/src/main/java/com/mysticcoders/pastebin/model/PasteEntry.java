package com.mysticcoders.pastebin.model;

import java.util.Date;
import java.util.Set;
import java.io.Serializable;

/**
 * PasteEntry
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public class PasteEntry implements Serializable {

    private Long id;
    private PasteEntry parent;
    private Set children;
    private Set images;

    private String channel;
    private String code;
    private Date created = new Date();
    private Date lastViewed;
    private String name;
    private int viewCount;

    private String highlight;

    public PasteEntry() {
    }

    public PasteEntry(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PasteEntry getParent() {
        return parent;
    }

    public void setParent(PasteEntry parent) {
        this.parent = parent;
    }

    public Set getChildren() {
        return children;
    }

    private void setChildren(Set children) {
        this.children = children;
    }

    public Set getImages() {
        return images;
    }

    private void setImages(Set images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getChannel() {
    	return channel;
    }
    public void setChannel(String channel) {
    	this.channel = channel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreated() {
        return created;
    }

    private void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastViewed() {
        return lastViewed;
    }

    private void setLastViewed(Date lastViewed) {
        this.lastViewed = lastViewed;
    }

    public int getViewCount() {
        return viewCount;
    }

    private void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void touch() {
        lastViewed = new Date();
        viewCount += 1;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

}
