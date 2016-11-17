package com.codyy.erpsportal.commons.models.entities.blog;

import java.util.List;

/**
 *  首页博文分类集合
 * Created by poe on 16-1-8.
 */
public class BlogPostList {

    private String result;
    private String message;
    private int total;
    /**
     * 推荐博文
     */
    private List<BlogPost> shareBlogList;
    /**
     * 热门博文
     */
    private List<BlogPost> hotBlogList;
     /**
     * 全部博文
     */
    private List<BlogPost> allBlogList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

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

    public List<BlogPost> getShareBlogList() {
        return shareBlogList;
    }

    public void setShareBlogList(List<BlogPost> shareBlogList) {
        this.shareBlogList = shareBlogList;
    }

    public List<BlogPost> getHotBlogList() {
        return hotBlogList;
    }

    public void setHotBlogList(List<BlogPost> hotBlogList) {
        this.hotBlogList = hotBlogList;
    }

    public List<BlogPost> getAllBlogList() {
        return allBlogList;
    }

    public void setAllBlogList(List<BlogPost> allBlogList) {
        this.allBlogList = allBlogList;
    }
}
