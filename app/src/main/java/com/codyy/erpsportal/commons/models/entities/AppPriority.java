package com.codyy.erpsportal.commons.models.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 应用模块的权限
 * Created by poe on 15-8-11.
 */
public enum AppPriority {

    AREA(UserInfo.USER_TYPE_AREA_USER),
    SCHOOL(UserInfo.USER_TYPE_SCHOOL_USER),
    TEACHER(UserInfo.USER_TYPE_TEACHER),
    STUDENT(UserInfo.USER_TYPE_STUDENT),
    PARENT(UserInfo.USER_TYPE_PARENT)
    ;

    private  String role;

    AppPriority(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static AppPriority getByRole(String role){
        for(AppPriority prop : values()){
            if(prop.getRole().equals(role)){
                return prop;
            }
        }

        throw new IllegalArgumentException(role + " is not a valid PropName");
    }

    /**
     * 创建数据集合
     * @param priorities
     * @return
     */
    public  static List<AppPriority> createCollections(AppPriority... priorities){
        if(priorities == null) return  null;

        List<AppPriority> appPriorities = new ArrayList<>();
        Collections.addAll(appPriorities, priorities);

        return  appPriorities;
    }


}
