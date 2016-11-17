package com.codyy.erpsportal.commons.models.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 通用解析类
 * 只要json类似 {result:"" ,list:[...]}
 * Created by poe on 16-1-21.
 */
public class CommonParseObject<T extends Object> {

    private String result ;
    @SerializedName("list")
    private List<T> dataList ;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
