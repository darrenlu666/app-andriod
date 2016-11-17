package com.codyy.erpsportal.commons.models.entities.blog;

import java.util.List;

/**
 * Created by poe on 16-3-8.
 */
public class BlogCategoryParse {

    private String message ;
    private String result;
    private int total;
    private List<BlogPost> blogList ;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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
