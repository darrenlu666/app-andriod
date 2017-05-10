package com.codyy.erpsportal.repairs.models.entities;

import android.os.Parcel;

/**
 * 故障类型
 * Created by gujiajia on 2017/3/28.
 */

public class MalfuncCategory implements android.os.Parcelable {

    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    @Override
    public String toString() {
        return id + ":" + name;
    }

    public MalfuncCategory() {
    }

    protected MalfuncCategory(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Creator<MalfuncCategory> CREATOR = new Creator<MalfuncCategory>() {
        @Override
        public MalfuncCategory createFromParcel(Parcel source) {
            return new MalfuncCategory(source);
        }

        @Override
        public MalfuncCategory[] newArray(int size) {
            return new MalfuncCategory[size];
        }
    };
}
