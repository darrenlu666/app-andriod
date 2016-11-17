package com.codyy.erpsportal.groups.models.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poe on 16-3-7.
 */
public class GroupMemberParse {

    private int total;
    @SerializedName("data")
    private List<GroupMember> dataList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<GroupMember> getDataList() {
        return dataList;
    }

    public void setDataList(List<GroupMember> dataList) {
        this.dataList = dataList;
    }
}
