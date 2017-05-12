package com.codyy.tpmp.filterlibrary.entities;

import com.google.gson.annotations.SerializedName;

/**
 *  远程数据解析.
 * 单条筛选返回的item
 * 如：地区->江苏
 * Created by poe on 28/04/17.
 */

public class Filter {

    @SerializedName(value = "id",alternate = {"groupCategoryId","areaId"})
    private String id;

    @SerializedName(value = "name",alternate = {"categoryName","areaName"})
    private String name;

    public Filter(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
