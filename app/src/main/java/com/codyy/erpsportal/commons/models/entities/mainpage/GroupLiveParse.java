package com.codyy.erpsportal.commons.models.entities.mainpage;

import java.util.List;

/**
 * 集团校－直播类
 * Created by poe on 17-8-22.
 */

public class GroupLiveParse {
    private String result;
    private List<GroupLive> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<GroupLive> getData() {
        return data;
    }

    public void setData(List<GroupLive> data) {
        this.data = data;
    }
}
