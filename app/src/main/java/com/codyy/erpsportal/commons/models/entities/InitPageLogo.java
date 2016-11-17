package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by eachann on 2015/7/3.
 * coco接收到的台标信息
 */
public class InitPageLogo implements Parcelable {
    /**
     * 存储选择项目信息
     */
    private List<MapInfo> mapInfo;
    /**
     * 当前启用的索引，为-1 表示不启用
     */
    private int logoUseIndex;
    /**
     * 位置，不开发
     */
    private String logoPos;
    /**
     * 位置，不开发
     */
    private String logoPosX;
    /**
     * 位置，不开发
     */
    private int logoPosY;

    public List<MapInfo> getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(List<MapInfo> mapInfo) {
        this.mapInfo = mapInfo;
    }

    public int getLogoUseIndex() {
        return logoUseIndex;
    }

    public void setLogoUseIndex(int logoUseIndex) {
        this.logoUseIndex = logoUseIndex;
    }

    public String getLogoPos() {
        return logoPos;
    }

    public void setLogoPos(String logoPos) {
        this.logoPos = logoPos;
    }

    public String getLogoPosX() {
        return logoPosX;
    }

    public void setLogoPosX(String logoPosX) {
        this.logoPosX = logoPosX;
    }

    public int getLogoPosY() {
        return logoPosY;
    }

    public void setLogoPosY(int logoPosY) {
        this.logoPosY = logoPosY;
    }

    public static Creator<InitPageLogo> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mapInfo);
        dest.writeInt(this.logoUseIndex);
        dest.writeString(this.logoPos);
        dest.writeString(this.logoPosX);
        dest.writeInt(this.logoPosY);
    }

    public InitPageLogo() {
    }

    protected InitPageLogo(Parcel in) {
        this.mapInfo = in.createTypedArrayList(MapInfo.CREATOR);
        this.logoUseIndex = in.readInt();
        this.logoPos = in.readString();
        this.logoPosX = in.readString();
        this.logoPosY = in.readInt();
    }

    public static final Creator<InitPageLogo> CREATOR = new Creator<InitPageLogo>() {
        public InitPageLogo createFromParcel(Parcel source) {
            return new InitPageLogo(source);
        }

        public InitPageLogo[] newArray(int size) {
            return new InitPageLogo[size];
        }
    };

    @Override
    public String toString() {
        return "InitPageLogo{" +
                "mapInfo=" + mapInfo +
                ", logoUseIndex=" + logoUseIndex +
                ", logoPos='" + logoPos + '\'' +
                ", logoPosX='" + logoPosX + '\'' +
                ", logoPosY=" + logoPosY +
                '}';
    }
}
