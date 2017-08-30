package com.codyy.erpsportal.commons.models.personal;

import java.util.List;

/**
 * 二维码分享解析.
 * Created by poe on 16/05/17.
 */

public class ShareParse {
    private String result ;
    private List<ShareApp> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<ShareApp> getData() {
        return data;
    }

    public void setData(List<ShareApp> data) {
        this.data = data;
    }
}
