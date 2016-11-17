package com.codyy.erpsportal.commons.models.entities.customized;

import java.util.List;

/**
 * 首页-直录播(名校网络课堂)-Gson解析模型
 * Created by poe on 16-6-1.
 */
public class LivingRecordParse {
    private String result ;
    private String message;
    private String errorCode;
    private List<LivingRecordLesson> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public List<LivingRecordLesson> getData() {
        return data;
    }

    public void setData(List<LivingRecordLesson> data) {
        this.data = data;
    }
}
