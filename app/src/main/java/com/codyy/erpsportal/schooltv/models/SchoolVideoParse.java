package com.codyy.erpsportal.schooltv.models;

// FIXME generate failure  field _$PastProgramList6274

import java.util.List;

/**
 * Created by poe on 17-3-17.
 */

public class SchoolVideoParse {
    private String message;
    private String result;
    private int total;
    private List<SchoolVideo> pastProgramList;

    public List<SchoolVideo> getPastProgramList() {
        return pastProgramList;
    }

    public void setPastProgramList(List<SchoolVideo> pastProgramList) {
        this.pastProgramList = pastProgramList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
