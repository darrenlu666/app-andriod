package com.codyy.erpsportal.commons.models.entities.mainpage;

import com.codyy.erpsportal.commons.models.entities.PrepareLessonsShortEntity;

import java.util.List;

/**
 * 集团校－网络教研(v5.3.7）
 * Created by poe on 17-8-22.
 */

public class MainNetTeachParse {

    private String result;
    private List<PrepareLessonsShortEntity> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<PrepareLessonsShortEntity> getData() {
        return data;
    }

    public void setData(List<PrepareLessonsShortEntity> data) {
        this.data = data;
    }
}
