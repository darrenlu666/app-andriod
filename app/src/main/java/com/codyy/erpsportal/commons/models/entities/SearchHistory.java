package com.codyy.erpsportal.commons.models.entities;

/**
 * Created by kmdai on 2015/8/28.
 */
public class SearchHistory extends SearchBase {
    private String cont;
    private String type;

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
