package com.codyy.erpsportal.commons.models.entities.mainpage;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

import java.util.List;

/**
 * 集团校首页－公告
 * Created by poe on 17-8-7.
 */

public class AnnounceParse extends BaseTitleItemBar{
    private String result ;
    private List<Announce> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Announce> getData() {
        return data;
    }

    public void setData(List<Announce> data) {
        this.data = data;
    }
}
