package com.codyy.tpmp.filterlibrary.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 筛选相关的Gson解析工具类
 * Created by poe on 28/04/17.
 */

public class FilterParse {

    private String result;//结果
    private String levelName;
    private String hasDirect;

    @SerializedName(value = "list",alternate = {"areas","schools"})
    private List<Filter> list;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Filter> getList() {
        return list;
    }

    public void setList(List<Filter> list) {
        this.list = list;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getHasDirect() {
        return hasDirect;
    }

    public void setHasDirect(String hasDirect) {
        this.hasDirect = hasDirect;
    }
}
