package com.codyy.erpsportal.groups.models.entities;

import com.google.gson.annotations.SerializedName;

/**
 * 应用-圈组管理-详细信息
 * Created by poe on 16-4-1.
 */
public class GroupDetailParse {

    private String result ;
    @SerializedName("data")
    private GroupDetail groupDetail;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public GroupDetail getGroupDetail() {
        return groupDetail;
    }

    public void setGroupDetail(GroupDetail groupDetail) {
        this.groupDetail = groupDetail;
    }
}
