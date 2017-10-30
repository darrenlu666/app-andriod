package com.codyy.erpsportal.onlinemeetings.models.entities;

/**
 * Created by yangxinwu on 2015/8/30.
 */
public class DocControl {
    private String actionType;
    private String from;
    private String to;
    private String from_null;
    private String current;
    private String url;
    private String id;
    private String p2p;
    private String owner;
    private String filename;
    private String index;
    private String key;
    private String tabId;

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom_null() {
        return from_null;
    }

    public void setFrom_null(String from_null) {
        this.from_null = from_null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getP2p() {
        return p2p;
    }

    public void setP2p(String p2p) {
        this.p2p = p2p;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
