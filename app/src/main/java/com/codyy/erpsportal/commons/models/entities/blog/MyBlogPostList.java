package com.codyy.erpsportal.commons.models.entities.blog;

import java.util.List;

/**
 *  我的博文分类集合
 * Created by poe on 16-1-8.
 */
public class MyBlogPostList {

    private String result;
    private String message;
    private int total;
    /**
     * 博文集合
     */
    private List<BlogPost> blogList;

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

    public List<BlogPost> getBlogList() {
        return blogList;
    }

    public void setBlogList(List<BlogPost> blogList) {
        this.blogList = blogList;
    }
}
