package com.codyy.erpsportal.repairs.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gujiajia on 2017/4/13.
 */

public class CategoriesPageInfo implements Parcelable {

    public CategoriesPageInfo(MalfuncCategory[] categories) {
        this.categories = categories;
        this.selected = -1;
    }

    public CategoriesPageInfo(CategoriesPageInfo src) {
        this.categories = src.getCategories();
        this.selected = src.getSelected();
    }

    private MalfuncCategory[] categories;

    private int selected = -1;

    public MalfuncCategory[] getCategories() {
        return categories;
    }

    public void setCategories(MalfuncCategory[] categories) {
        this.categories = categories;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getSelectedName() {
        if (selected < 0 || selected >= categories.length) {
            return "未选择";
        }
        return categories[selected].getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.categories, flags);
        dest.writeInt(this.selected);
    }

    public CategoriesPageInfo() {
    }

    protected CategoriesPageInfo(Parcel in) {
        this.categories = in.createTypedArray(MalfuncCategory.CREATOR);
        this.selected = in.readInt();
    }

    public static final Creator<CategoriesPageInfo> CREATOR = new Creator<CategoriesPageInfo>() {
        @Override
        public CategoriesPageInfo createFromParcel(Parcel source) {
            return new CategoriesPageInfo(source);
        }

        @Override
        public CategoriesPageInfo[] newArray(int size) {
            return new CategoriesPageInfo[size];
        }
    };
}
