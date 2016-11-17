package com.codyy.erpsportal.groups.models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 圈组空间-个人备课
 * Created by poe on 16-2-1.
 */
public class ModulePersonPrepare extends ModuleBase  implements Serializable {

    @SerializedName("list")
    private List<GroupPersonPrepare> data;

    public List<GroupPersonPrepare> getData() {
        return data;
    }

    public void setData(List<GroupPersonPrepare> data) {
        this.data = data;
    }
}
