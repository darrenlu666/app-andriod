package com.codyy.erpsportal.schooltv.models;

import java.util.List;

/**
 * Created by poe on 17-3-15.
 */

public class SchoolProgramParse {
    private String result;
    private List<SchoolProgram> list;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<SchoolProgram> getList() {
        return list;
    }

    public void setList(List<SchoolProgram> list) {
        this.list = list;
    }
}
