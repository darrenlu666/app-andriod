package com.codyy.erpsportal.groups.models.entities;

import com.codyy.erpsportal.commons.models.entities.blog.GroupBlogPost;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 圈组空间-博文模块
 * Created by poe on 16-2-1.
 */
public class ModuleBlog extends ModuleBase  implements Serializable {

    @SerializedName("list")
    private List<GroupBlogPost> data;

    public List<GroupBlogPost> getData() {
        return data;
    }

    public void setData(List<GroupBlogPost> data) {
        this.data = data;
    }
}
