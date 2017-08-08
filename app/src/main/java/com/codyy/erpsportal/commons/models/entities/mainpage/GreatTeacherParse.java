package com.codyy.erpsportal.commons.models.entities.mainpage;

import java.util.List;

/**
 * 名师推荐
 * Created by poe on 17-8-8.
 */

public class GreatTeacherParse {
    private String result;
    private List<GreatTeacher> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<GreatTeacher> getData() {
        return data;
    }

    public void setData(List<GreatTeacher> data) {
        this.data = data;
    }
}
