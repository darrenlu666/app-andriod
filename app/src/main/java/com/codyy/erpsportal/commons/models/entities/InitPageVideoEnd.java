package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by eachann on 2015/7/3.
 * coco接收到的片尾信息
 */
public class InitPageVideoEnd implements Parcelable {
    /**
     * 存储选择项目信息
     */
    private List<MapInfo> mapInfo;
    /**
     * 当前启用的索引，为-1 表示不启用
     */
    private int logoUseIndex;

    public int getLogoUseIndex() {
        return logoUseIndex;
    }

    public void setLogoUseIndex(int logoUseIndex) {
        this.logoUseIndex = logoUseIndex;
    }

    public List<MapInfo> getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(List<MapInfo> mapInfo) {
        this.mapInfo = mapInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mapInfo);
        dest.writeInt(this.logoUseIndex);
    }

    public InitPageVideoEnd() {
    }

    protected InitPageVideoEnd(Parcel in) {
        this.mapInfo = in.createTypedArrayList(MapInfo.CREATOR);
        this.logoUseIndex = in.readInt();
    }

    public static final Creator<InitPageVideoEnd> CREATOR = new Creator<InitPageVideoEnd>() {
        public InitPageVideoEnd createFromParcel(Parcel source) {
            return new InitPageVideoEnd(source);
        }

        public InitPageVideoEnd[] newArray(int size) {
            return new InitPageVideoEnd[size];
        }
    };
}
