package com.codyy.erpsportal.rethink.models.entities;

/**
 * 教学反思评论列表头
 * Created by gujiajia on 2016/1/6.
 */
public class CommentListHeader {

    /**
     * 教学反思标题
     */
    private String title;

    /**
     * 教学反思创建时间
     */
    private String time;

    /**
     * 教学反思的评论数量
     */
    private int count;

    public CommentListHeader() {
        this.title = "";
        this.time = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
