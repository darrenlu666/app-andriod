package com.codyy.erpsportal.commons.models.entities.customized;

import java.util.List;

/**
 * 同步课堂解析类
 * Created by poe on 24/07/17.
 */

public class SipLessonParse {

    /**
     * message : 成功
     * result : success
     * errorCode : 1
     * data:[]
     */
    private String message;
    private String result;
    private String errorCode;
    private List<SipSemesterLesson> data;

    public List<SipSemesterLesson> getData() {
        return data;
    }

    public void setData(List<SipSemesterLesson> data) {
        this.data = data;
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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
