package com.codyy.erpsportal.commons.models.entities.customized;

import java.util.List;

/**
 * 同步课堂-更多: 解析类
 * Created by poe on 25/07/17.
 */

public class SipLessonMoreParse {

    /**
     * message : 成功
     * result : success
     * errorCode : 1
     * data:[]
     */
    private String message;
    private String result;
    private String errorCode;
    private int total;//总数据
    private List<SipLesson> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SipLesson> getData() {
        return data;
    }

    public void setData(List<SipLesson> data) {
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
