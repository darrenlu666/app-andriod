/*
 *
 * 阔地教育科技有限公司版权所有(codyy.com/codyy.cn)
 * Copyright (c)  2017, Codyy and/or its affiliates. All rights reserved.
 *
 */

package com.codyy.erpsportal.commons.models.entities.blog;

import java.util.List;

/**
 *  应用博文分类集合
 * Created by poe on 17-09-26.
 */
public class AreaBlogPostList {

    private String result;
    private String message;
    private int total;
    /**
     * 博文集合
     */
    private List<BlogPost> data;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<BlogPost> getData() {
        return data;
    }

    public void setData(List<BlogPost> data) {
        this.data = data;
    }
}
