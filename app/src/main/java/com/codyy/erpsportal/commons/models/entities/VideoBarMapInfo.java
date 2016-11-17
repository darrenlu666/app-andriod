package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eachann on 2015/7/3.
 * coco收到机位信息
 */
public class VideoBarMapInfo implements Parcelable, Comparable<VideoBarMapInfo> {
    /**
     * 索引值
     */
    private String index;
    /**
     * 机位名称
     */
    private String title;
    /**
     * 摄像机调控是否可用
     */
    private boolean pizEnable;
    /**
     * 是否开启录制，不开发
     */
    private boolean videoRecord;
    /**
     * 可用机位数量
     */
    private int presetNum;

    private boolean isReceiver;

    @Override
    public String toString() {
        return "VideoBarMapInfo{" +
                "index=" + index +
                ", title='" + title + '\'' +
                ", pizEnable=" + pizEnable +
                ", videoRecord=" + videoRecord +
                ", presetNum=" + presetNum +
                ", videoBitRate=" + videoBitRate +
                '}';
    }

    /**
     * 码率，不开发
     */
    private int videoBitRate;

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

    public boolean isPizEnable() {
        return pizEnable;
    }

    public void setPizEnable(boolean pizEnable) {
        this.pizEnable = pizEnable;
    }

    public boolean getPizEnable() {
        return this.pizEnable;
    }

    public boolean isVideoRecord() {
        return videoRecord;
    }

    public void setVideoRecord(boolean videoRecord) {
        this.videoRecord = videoRecord;
    }

    public int getPresetNum() {
        return presetNum;
    }

    public void setPresetNum(int presetNum) {
        this.presetNum = presetNum;
    }

    public int getVideoBitRate() {
        return videoBitRate;
    }

    public void setVideoBitRate(int videoBitRate) {
        this.videoBitRate = videoBitRate;
    }

    public boolean isReceiver() {
        return isReceiver;
    }

    public void setIsReceiver(boolean isReceiver) {
        this.isReceiver = isReceiver;
    }

    public VideoBarMapInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.index);
        dest.writeString(this.title);
        dest.writeByte(pizEnable ? (byte) 1 : (byte) 0);
        dest.writeByte(videoRecord ? (byte) 1 : (byte) 0);
        dest.writeInt(this.presetNum);
        dest.writeInt(this.videoBitRate);
        dest.writeByte(isReceiver ? (byte) 1 : (byte) 0);
    }

    protected VideoBarMapInfo(Parcel in) {
        this.index = in.readString();
        this.title = in.readString();
        this.pizEnable = in.readByte() != 0;
        this.videoRecord = in.readByte() != 0;
        this.presetNum = in.readInt();
        this.videoBitRate = in.readInt();
        this.isReceiver = in.readByte() != 0;
    }

    public static final Creator<VideoBarMapInfo> CREATOR = new Creator<VideoBarMapInfo>() {
        public VideoBarMapInfo createFromParcel(Parcel source) {
            return new VideoBarMapInfo(source);
        }

        public VideoBarMapInfo[] newArray(int size) {
            return new VideoBarMapInfo[size];
        }
    };

    @Override
    public int compareTo(VideoBarMapInfo another) {
        return this.getIndex().compareTo(another.getIndex());
    }
}
