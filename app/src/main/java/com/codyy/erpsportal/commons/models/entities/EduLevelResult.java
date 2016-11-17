package com.codyy.erpsportal.commons.models.entities;

import java.util.List;

/**
 *
 * Created by gujiajia on 2016/8/8.
 */
public class EduLevelResult {
    private String result;

    private List<EduLevel> list;

    public List<EduLevel> getList() {
        return list;
    }

    public void setList(List<EduLevel> list) {
        this.list = list;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
