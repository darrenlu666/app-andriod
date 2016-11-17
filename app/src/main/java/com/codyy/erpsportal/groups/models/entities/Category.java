package com.codyy.erpsportal.groups.models.entities;

import com.google.gson.annotations.SerializedName;

/**
 * 圈组-筛选-分类
 * Created by poe on 16-1-21.
 */
public class Category {
    @SerializedName(value = "groupBlogCategoryId",alternate = {"blogCategoryId"})
    private String groupCategoryId;
    private String categoryName;

    public String getGroupCategoryId() {
        return groupCategoryId;
    }

    public void setGroupCategoryId(String groupCategoryId) {
        this.groupCategoryId = groupCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
