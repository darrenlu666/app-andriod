package com.codyy.erpsportal.urlbuilder.processors;

/**
 * 链接属性
 * Created by gujiajia on 2016/4/13.
 */
public class UrlAttr {
    /**
     * 属性名称
     */
    private String attrName;
    /**
     * url相对位置
     */
    private String suffix;

    public UrlAttr(String attrName, String suffix) {
        this.attrName = attrName;
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }
}
