package com.codyy.erpsportal.statistics.models.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 学科统计结果
 * Created by gujiajia on 2016/8/11.
 */
public class SubjectsStatResult {

    private boolean result;

    private String title;

    @SerializedName("data")
    private List<SubjectsStatEntity> statEntities;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SubjectsStatEntity> getStatEntities() {
        return statEntities;
    }

    public void setStatEntities(List<SubjectsStatEntity> statEntities) {
        this.statEntities = statEntities;
    }
}
