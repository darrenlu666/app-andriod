package com.codyy.erpsportal.onlineteach.models.entities;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

/**
 * 网络授课－详情－录制权限
 * Created by poe on 16-8-15.
 */
public class NetPermission extends BaseTitleItemBar {
    private String schoolName;
    private boolean hasPermission;
    private String className;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public boolean isHasPermission() {
        return hasPermission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
