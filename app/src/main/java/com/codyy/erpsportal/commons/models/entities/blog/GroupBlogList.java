package com.codyy.erpsportal.commons.models.entities.blog;

import java.util.List;

/**
 * Created by poe on 16-3-8.
 */
public class GroupBlogList {

    private List<BlogPost> hotBloglist;
    private List<BlogPost> groupBloglist;
    private List<BlogPost> topBloglist;

    public List<BlogPost> getHotBloglist() {
        return hotBloglist;
    }

    public void setHotBloglist(List<BlogPost> hotBloglist) {
        this.hotBloglist = hotBloglist;
    }

    public List<BlogPost> getGroupBloglist() {
        return groupBloglist;
    }

    public void setGroupBloglist(List<BlogPost> groupBloglist) {
        this.groupBloglist = groupBloglist;
    }

    public List<BlogPost> getTopBlogList() {
        return topBloglist;
    }

    public void setTopBlogList(List<BlogPost> topBlogList) {
        this.topBloglist = topBlogList;
    }
}
