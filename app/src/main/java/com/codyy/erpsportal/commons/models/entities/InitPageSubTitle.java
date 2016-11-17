package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by eachann on 2015/7/3.
 * coco接收到的字幕信息
 */
public class InitPageSubTitle implements Parcelable {
    /**
     * 字颜色，不开发
     */
    private String subTitleFontColor;
    /**
     * 位置，不开发
     */
    private String subTitlePosY;
    /**
     * 滚动次数，不开发
     */
    private int subTitleScrollTimes;
    /**
     * 位置，不开发
     */
    private String subTitlePosX;
    /**
     * 字体大小，不开发
     */
    private int subTitleFontSize;
    /**
     * 滚动模式，不开发
     */
    private int subTitleScrollMode;
    /**
     * 存储选择项目信息
     */
    private List<MapInfo> mapInfos;
    /**
     * 位置，不开发
     */
    private String subTitlePos;
    /**
     * 字体类型，不开发
     */
    private int subTitleFontFamily;
    /**
     * 字形，不开发
     */
    private int subTitleFontStyle;
    /**
     * 背景色，不开发
     */
    private int subTitleBackgroundColor;
    /**
     * 当前启用的索引，为-1表示不启用
     */
    private int subTitleUseIndex;

    public String getSubTitleFontColor() {
        return subTitleFontColor;
    }

    public void setSubTitleFontColor(String subTitleFontColor) {
        this.subTitleFontColor = subTitleFontColor;
    }

    public String getSubTitlePosY() {
        return subTitlePosY;
    }

    public void setSubTitlePosY(String subTitlePosY) {
        this.subTitlePosY = subTitlePosY;
    }

    public int getSubTitleScrollTimes() {
        return subTitleScrollTimes;
    }

    public void setSubTitleScrollTimes(int subTitleScrollTimes) {
        this.subTitleScrollTimes = subTitleScrollTimes;
    }

    public String getSubTitlePosX() {
        return subTitlePosX;
    }

    public void setSubTitlePosX(String subTitlePosX) {
        this.subTitlePosX = subTitlePosX;
    }

    public int getSubTitleFontSize() {
        return subTitleFontSize;
    }

    public void setSubTitleFontSize(int subTitleFontSize) {
        this.subTitleFontSize = subTitleFontSize;
    }

    public int getSubTitleScrollMode() {
        return subTitleScrollMode;
    }

    public void setSubTitleScrollMode(int subTitleScrollMode) {
        this.subTitleScrollMode = subTitleScrollMode;
    }

    public List<MapInfo> getMapInfos() {
        return mapInfos;
    }

    public void setMapInfos(List<MapInfo> mapInfos) {
        this.mapInfos = mapInfos;
    }

    public String getSubTitlePos() {
        return subTitlePos;
    }

    public void setSubTitlePos(String subTitlePos) {
        this.subTitlePos = subTitlePos;
    }

    public int getSubTitleFontFamily() {
        return subTitleFontFamily;
    }

    public void setSubTitleFontFamily(int subTitleFontFamily) {
        this.subTitleFontFamily = subTitleFontFamily;
    }

    public int getSubTitleFontStyle() {
        return subTitleFontStyle;
    }

    public void setSubTitleFontStyle(int subTitleFontStyle) {
        this.subTitleFontStyle = subTitleFontStyle;
    }

    public int getSubTitleBackgroundColor() {
        return subTitleBackgroundColor;
    }

    public void setSubTitleBackgroundColor(int subTitleBackgroundColor) {
        this.subTitleBackgroundColor = subTitleBackgroundColor;
    }

    public int getSubTitleUseIndex() {
        return subTitleUseIndex;
    }

    public void setSubTitleUseIndex(int subTitleUseIndex) {
        this.subTitleUseIndex = subTitleUseIndex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subTitleFontColor);
        dest.writeString(this.subTitlePosY);
        dest.writeInt(this.subTitleScrollTimes);
        dest.writeString(this.subTitlePosX);
        dest.writeInt(this.subTitleFontSize);
        dest.writeInt(this.subTitleScrollMode);
        dest.writeTypedList(mapInfos);
        dest.writeString(this.subTitlePos);
        dest.writeInt(this.subTitleFontFamily);
        dest.writeInt(this.subTitleFontStyle);
        dest.writeInt(this.subTitleBackgroundColor);
        dest.writeInt(this.subTitleUseIndex);
    }

    public InitPageSubTitle() {
    }

    protected InitPageSubTitle(Parcel in) {
        this.subTitleFontColor = in.readString();
        this.subTitlePosY = in.readString();
        this.subTitleScrollTimes = in.readInt();
        this.subTitlePosX = in.readString();
        this.subTitleFontSize = in.readInt();
        this.subTitleScrollMode = in.readInt();
        this.mapInfos = in.createTypedArrayList(MapInfo.CREATOR);
        this.subTitlePos = in.readString();
        this.subTitleFontFamily = in.readInt();
        this.subTitleFontStyle = in.readInt();
        this.subTitleBackgroundColor = in.readInt();
        this.subTitleUseIndex = in.readInt();
    }

    public static final Creator<InitPageSubTitle> CREATOR = new Creator<InitPageSubTitle>() {
        public InitPageSubTitle createFromParcel(Parcel source) {
            return new InitPageSubTitle(source);
        }

        public InitPageSubTitle[] newArray(int size) {
            return new InitPageSubTitle[size];
        }
    };

    @Override
    public String toString() {
        return "InitPageSubTitle{" +
                "subTitleFontColor='" + subTitleFontColor + '\'' +
                ", subTitlePosY='" + subTitlePosY + '\'' +
                ", subTitleScrollTimes=" + subTitleScrollTimes +
                ", subTitlePosX='" + subTitlePosX + '\'' +
                ", subTitleFontSize=" + subTitleFontSize +
                ", subTitleScrollMode=" + subTitleScrollMode +
                ", mapInfos=" + mapInfos +
                ", subTitlePos='" + subTitlePos + '\'' +
                ", subTitleFontFamily=" + subTitleFontFamily +
                ", subTitleFontStyle=" + subTitleFontStyle +
                ", subTitleBackgroundColor=" + subTitleBackgroundColor +
                ", subTitleUseIndex=" + subTitleUseIndex +
                '}';
    }
}
