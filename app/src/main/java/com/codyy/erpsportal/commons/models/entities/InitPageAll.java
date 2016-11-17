package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eachann on 2015/7/3.
 * coco收到的其他信息
 */
public class InitPageAll implements Parcelable {
    /**
     * 剩余可录制时间
     */
    int freeRecordTime;
    /**
     * 特效使用
     */
    int sceneUseIndex;

    int sceneSelectIndex;
    /**
     * 上课模式
     */
    int lessonMode;
    /**
     * 录制时间
     */
    int recordTime;
    /**
     * 录制模式：0电影模式 1资源模式 2电影模式+资源模式
     */
    int recordMode;
    /**
     * 剩余空间
     */
    int freeDiskSpace;
    /**
     * 录制状态:0开始 1暂停   2停止
     */
    int recordState;
    /**
     * "silent": 1，静音， 0 未静音
     */
    int silent;
    /**
     * "inClass": 1，上课后， 0： 未上课
     */
    int inClass;

    public int getFreeRecordTime() {
        return freeRecordTime;
    }

    public void setFreeRecordTime(int freeRecordTime) {
        this.freeRecordTime = freeRecordTime;
    }

    public int getSceneUseIndex() {
        return sceneUseIndex;
    }

    public void setSceneUseIndex(int sceneUseIndex) {
        this.sceneUseIndex = sceneUseIndex;
    }

    public int getSceneSelectIndex() {
        return sceneSelectIndex;
    }

    public void setSceneSelectIndex(int sceneSelectIndex) {
        this.sceneSelectIndex = sceneSelectIndex;
    }

    public int getLessonMode() {
        return lessonMode;
    }

    public void setLessonMode(int lessonMode) {
        this.lessonMode = lessonMode;
    }

    public int getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(int recordTime) {
        this.recordTime = recordTime;
    }

    public int getRecordMode() {
        return recordMode;
    }

    public void setRecordMode(int recordMode) {
        this.recordMode = recordMode;
    }

    public int getFreeDiskSpace() {
        return freeDiskSpace;
    }

    public void setFreeDiskSpace(int freeDiskSpace) {
        this.freeDiskSpace = freeDiskSpace;
    }

    public int getRecordState() {
        return recordState;
    }

    public void setRecordState(int recordState) {
        this.recordState = recordState;
    }

    public int getSilent() {
        return silent;
    }

    public void setSilent(int silent) {
        this.silent = silent;
    }

    public int getInClass() {
        return inClass;
    }

    public void setInClass(int inClass) {
        this.inClass = inClass;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.freeRecordTime);
        dest.writeInt(this.sceneUseIndex);
        dest.writeInt(this.sceneSelectIndex);
        dest.writeInt(this.lessonMode);
        dest.writeInt(this.recordTime);
        dest.writeInt(this.recordMode);
        dest.writeInt(this.freeDiskSpace);
        dest.writeInt(this.recordState);
        dest.writeInt(this.silent);
        dest.writeInt(this.inClass);
    }

    public InitPageAll() {
    }

    protected InitPageAll(Parcel in) {
        this.freeRecordTime = in.readInt();
        this.sceneUseIndex = in.readInt();
        this.sceneSelectIndex = in.readInt();
        this.lessonMode = in.readInt();
        this.recordTime = in.readInt();
        this.recordMode = in.readInt();
        this.freeDiskSpace = in.readInt();
        this.recordState = in.readInt();
        this.silent = in.readInt();
        this.inClass = in.readInt();
    }

    public static final Creator<InitPageAll> CREATOR = new Creator<InitPageAll>() {
        public InitPageAll createFromParcel(Parcel source) {
            return new InitPageAll(source);
        }

        public InitPageAll[] newArray(int size) {
            return new InitPageAll[size];
        }
    };
}
