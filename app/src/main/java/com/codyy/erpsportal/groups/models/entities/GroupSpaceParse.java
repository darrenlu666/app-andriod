package com.codyy.erpsportal.groups.models.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by poe on 16-2-1.
 */
public class GroupSpaceParse {
    private String result ;
    @SerializedName("data")
    private GroupSpace groupSpace;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public GroupSpace getGroupSpace() {
        return groupSpace;
    }

    public void setGroupSpace(GroupSpace groupSpace) {
        this.groupSpace = groupSpace;
    }
}
