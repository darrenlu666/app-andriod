package com.codyy.erpsportal.commons.models.entities.blog;

import java.util.List;

/**
 *  班级空间博文集合解析
 * Created by poe on 16-1-8.
 */
public class ClassBlogList extends BlogPostList{
    /**
     * 置顶博文
     */
    private List<BlogPost> topBlogList;

    public List<BlogPost> getTopBlogList() {
        return topBlogList;
    }

    public void setTopBlogList(List<BlogPost> topBlogList) {
        this.topBlogList = topBlogList;
    }
}
