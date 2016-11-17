package com.codyy.erpsportal.commons.models.entities.blog;

import java.util.List;

/**
 * 更多-全部博文解析类
 * Created by poe on 16-2-17.
 */
public class AllBlogParse {
    private String message ;
    private String result;
    private int total;
    private List<BlogPost> allBlogList ;

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

    public List<BlogPost> getAllBlogList() {
        return allBlogList;
    }

    public void setAllBlogList(List<BlogPost> allBlogList) {
        this.allBlogList = allBlogList;
    }
}
