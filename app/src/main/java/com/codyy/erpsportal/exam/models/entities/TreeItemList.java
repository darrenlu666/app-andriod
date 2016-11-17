package com.codyy.erpsportal.exam.models.entities;

import java.util.List;

/**
 * 选择人员列表
 * Created by eachann on 2016/2/3.
 */
public class TreeItemList {
    private String classlevelId;
    private String classlevelName;
    private boolean isSelected;
    private int type;
    private List<TreeItem> list;


    public String getClasslevelId() {
        return classlevelId;
    }

    public void setClasslevelId(String classlevelId) {
        this.classlevelId = classlevelId;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<TreeItem> getList() {
        return list;
    }

    public void setList(List<TreeItem> list) {
        this.list = list;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
