package com.codyy.erpsportal.commons.models.entities.blog;

import java.util.List;

/**
 *
 * 圈组博文-更多-博文列表
 * Created by poe on 16-1-8.
 */
public class BlogPostCollection {

    private String result;
    private String message;
    private int total = 0;
    private List<BlogPost> list;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<BlogPost> getList() {
        return list;
    }

    public void setList(List<BlogPost> list) {
        this.list = list;
    }
}
