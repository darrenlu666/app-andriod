package com.codyy.erpsportal.groups.models.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 教研组/新区组
 * Created by poe on 16-1-19.
 */
public class GroupTeaching {

    private String result ;//
    private String total;//总条数
    @SerializedName("list")
    private List<Group> dataList;//数据集合

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Group> getDataList() {
        return dataList;
    }

    public void setDataList(List<Group> dataList) {
        this.dataList = dataList;
    }
}
