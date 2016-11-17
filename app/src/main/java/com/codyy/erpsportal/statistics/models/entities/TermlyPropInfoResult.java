package com.codyy.erpsportal.statistics.models.entities;

import java.util.List;

/**
 * 开课比分析学期统计数据结果对象
 * Created by gujiajia on 2016/8/10.
 */
public class TermlyPropInfoResult {
    private boolean result;
    private String title;
    private List<TermlyPropItem> data;

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

    public List<TermlyPropItem> getData() {
        return data;
    }

    public void setData(List<TermlyPropItem> data) {
        this.data = data;
    }
}
