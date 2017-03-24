package com.codyy.erpsportal.repairs.models.entities;

/**
 * 常见问题
 * Created by gujiajia on 2017/3/23.
 */

public class Malfunction {

    private String id;

    private String title;

    private int hits;

    public Malfunction(String id, String title, int hits) {
        this.id = id;
        this.title = title;
        this.hits = hits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }
}
