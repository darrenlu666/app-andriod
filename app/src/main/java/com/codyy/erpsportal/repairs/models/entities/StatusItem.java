package com.codyy.erpsportal.repairs.models.entities;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 状态筛选项
 * Created by gujiajia on 2017/3/22.
 */

public class StatusItem implements RepairFilterItem {

    /**
     * 新建、待处理
     */
    public final static String STATUS_NEW = "NEW";

    /**
     * 处理中
     */
    public final static String STATUS_PROGRESS = "PROGRESS";

    /**
     * 处理完成
     */
    public final static String STATUS_DONE = "DONE";

    /**
     * 已验收
     */
    public final static String STATUS_VERIFIED = "VERIFIED";

    private String name;

    @RepairStatus
    private String status;

    public StatusItem(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String content() {
        return name;
    }

    @StringDef({STATUS_NEW, STATUS_PROGRESS, STATUS_DONE, STATUS_VERIFIED})
    @Retention(RetentionPolicy.SOURCE)
    @interface RepairStatus{}
}
