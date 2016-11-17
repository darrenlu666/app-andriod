package com.codyy.erpsportal.groups.models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 圈组空间-资源
 * Created by poe on 16-2-1.
 */
public class ModuleResource extends ModuleBase  implements Serializable {

    @SerializedName("list")
    private List<GroupResource> data;

    public List<GroupResource> getData() {
        return data;
    }

    public void setData(List<GroupResource> data) {
        this.data = data;
    }
}
