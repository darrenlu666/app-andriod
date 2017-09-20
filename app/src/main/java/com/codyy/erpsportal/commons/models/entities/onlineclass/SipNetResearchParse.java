package com.codyy.erpsportal.commons.models.entities.onlineclass;

import java.util.List;

/**
 * Created by poe on 21/07/17.
 */

public class SipNetResearchParse {
    private int total;//数量
    private String dynamicLabel;//标题：集体备课
    private List<SipNetResearch> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getDynamicLabel() {
        return dynamicLabel;
    }

    public void setDynamicLabel(String dynamicLabel) {
        this.dynamicLabel = dynamicLabel;
    }

    public List<SipNetResearch> getList() {
        return list;
    }

    public void setList(List<SipNetResearch> list) {
        this.list = list;
    }
}
