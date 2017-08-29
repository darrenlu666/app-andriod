package com.codyy.erpsportal.resource.models.entities;

import java.util.List;

/**
 * resource parse .
 * Created by poe on 17-8-8.
 */

public class ResourceParse {
    private String result;
    private List<Resource> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Resource> getData() {
        return data;
    }

    public void setData(List<Resource> data) {
        this.data = data;
    }
}
