package com.codyy.erpsportal.commons.models.entities.customized;

import java.util.List;

/**
 * Created by poe on 16-6-1.
 */
public class HistoryClassParse {
    private String result ;
    private String message;
    private int errorCode ;
    private List<HistoryClass> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<HistoryClass> getData() {
        return data;
    }

    public void setData(List<HistoryClass> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
