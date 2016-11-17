package com.codyy.erpsportal.groups.models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 圈组空间-集体备课
 * Created by poe on 16-2-1.
 */
public class ModulePrepareLesson extends ModuleBase  implements Serializable {

    @SerializedName("list")
    private List<GroupPrepareLesson> data;

    public List<GroupPrepareLesson> getData() {
        return data;
    }

    public void setData(List<GroupPrepareLesson> data) {
        this.data = data;
    }
}
