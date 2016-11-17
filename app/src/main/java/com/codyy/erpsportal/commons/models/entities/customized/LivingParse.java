package com.codyy.erpsportal.commons.models.entities.customized;

import java.util.List;

/**
 * Created by poe on 16-6-1.
 */
public class LivingParse {
    private String result ;
    private String message ;
    private String errorCode ;
    private List<LivingClass> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<LivingClass> getData() {
        return data;
    }

    public void setData(List<LivingClass> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
