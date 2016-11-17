package com.codyy.erpsportal.commons.models.entities;

/**
 * Created by kmdai on 2015/9/1.
 */
public class UserClassBase {
    /**
     * 老师
     */
    public static final int TYPE_TEACHER = 0x001;
    /**
     * 学生
     */
    public static final int TYPE_STUDENT = 0x002;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
