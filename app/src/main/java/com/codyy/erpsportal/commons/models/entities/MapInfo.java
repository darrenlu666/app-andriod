package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eachann on 2015/7/3.
 */
public class MapInfo implements Parcelable {
    /**
     * 索引值，用于启用，添加，删除功能
     */
    String index;
    /**
     * 名称，选项列表
     */
    String title;

    @Override
    public int describeContents() {
        return 0;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static Creator<MapInfo> getCREATOR() {
        return CREATOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.index);
        dest.writeString(this.title);
    }

    public MapInfo() {
    }

    protected MapInfo(Parcel in) {
        this.index = in.readString();
        this.title = in.readString();
    }

    public static final Creator<MapInfo> CREATOR = new Creator<MapInfo>() {
        public MapInfo createFromParcel(Parcel source) {
            return new MapInfo(source);
        }

        public MapInfo[] newArray(int size) {
            return new MapInfo[size];
        }
    };

    @Override
    public String toString() {
        return "MapInfo{" +
                "index='" + index + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
