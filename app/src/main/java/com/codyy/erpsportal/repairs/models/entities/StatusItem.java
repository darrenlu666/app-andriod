package com.codyy.erpsportal.repairs.models.entities;

/**
 * 状态筛选项
 * Created by gujiajia on 2017/3/22.
 */

public class StatusItem implements RepairFilterItem {

    private String name;

    private int status;

    public StatusItem(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String content() {
        return name;
    }
}
