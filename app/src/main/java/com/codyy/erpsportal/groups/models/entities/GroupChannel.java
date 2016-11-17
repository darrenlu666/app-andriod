package com.codyy.erpsportal.groups.models.entities;

import com.google.gson.annotations.SerializedName;

/**
 * 频道-圈组数据集合
 * Created by poe on 16-1-15.
 */
public class GroupChannel {

    private String result;
    private String message;
    @SerializedName("list")
    private GroupList groupList;

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

    public GroupList getGroupList() {
        return groupList;
    }

    public void setGroupList(GroupList groupList) {
        this.groupList = groupList;
    }
}
