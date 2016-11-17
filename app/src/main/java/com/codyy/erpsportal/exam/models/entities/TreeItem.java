package com.codyy.erpsportal.exam.models.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * 选择人员列表
 * Created by eachann on 2016/2/3.
 */
public class TreeItem implements Parcelable, Comparable<TreeItem> {
    private String classlevelId;
    private String classlevelName;
    private String parentName;
    private String childNames;
    private boolean isSelected;
    private int type;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getChildNames() {
        return childNames;
    }

    public void setChildNames(String childNames) {
        this.childNames = childNames;
    }

    public TreeItem(String classlevelId, String classlevelName, @Nullable String parentName, @Nullable String childNames, boolean isSelected, int type) {
        this.parentName = parentName;
        this.classlevelId = classlevelId;
        this.classlevelName = classlevelName;
        this.isSelected = isSelected;
        this.type = type;
        this.childNames = childNames;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.classlevelId);
        dest.writeString(this.classlevelName);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
        dest.writeString(this.parentName);
        dest.writeString(this.childNames);
    }

    protected TreeItem(Parcel in) {
        this.classlevelId = in.readString();
        this.classlevelName = in.readString();
        this.isSelected = in.readByte() != 0;
        this.type = in.readInt();
        this.parentName = in.readString();
        this.childNames = in.readString();
    }

    public static final Parcelable.Creator<TreeItem> CREATOR = new Parcelable.Creator<TreeItem>() {
        public TreeItem createFromParcel(Parcel source) {
            return new TreeItem(source);
        }

        public TreeItem[] newArray(int size) {
            return new TreeItem[size];
        }
    };

    @Override
    public String toString() {
        return "TreeItem{" +
                "classlevelId='" + classlevelId + '\'' +
                ", classlevelName='" + classlevelName + '\'' +
                ", isSelected=" + isSelected +
                ", type=" + type +
                '}';
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(@NonNull TreeItem another) {
//        if (this.classlevelId.equals(another.classlevelId) && this.classlevelName .equals( another.getClasslevelName()) && this.isSelected ==( another.isSelected )&& this.type == another.type)
        if (this.classlevelId.equals(another.classlevelId) && this.classlevelName.equals(another.getClasslevelName()) && this.type == another.type)
            return 0;
        else return -1;
    }
}
