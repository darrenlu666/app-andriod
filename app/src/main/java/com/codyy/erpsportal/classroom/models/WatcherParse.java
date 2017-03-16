package com.codyy.erpsportal.classroom.models;

import java.util.List;

/**
 * Created by poe on 17-3-14.
 */
public class WatcherParse {
    private String errorCode;
    private String result;
    private int total =0;//default : 0
    private List<Watcher> data;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
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

    public List<Watcher> getData() {
        return data;
    }

    public void setData(List<Watcher> data) {
        this.data = data;
    }
}
