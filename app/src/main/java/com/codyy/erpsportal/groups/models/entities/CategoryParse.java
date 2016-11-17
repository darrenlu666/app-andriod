package com.codyy.erpsportal.groups.models.entities;

import java.util.List;

/**
 * 圈组-筛选-分类
 * Created by poe on 16-1-21.
 */
public class CategoryParse {
    private String result ;
    private String message;
    private List<Category> list ;

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

    public List<Category> getList() {
        return list;
    }

    public void setList(List<Category> list) {
        this.list = list;
    }
}
