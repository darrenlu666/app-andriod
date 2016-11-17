package com.codyy.erpsportal.exam.models.entities;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by eachann on 2016/1/14.
 */
public class ExamItemInfo implements Parcelable {
    private int progress;
    private String type;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.progress);
        dest.writeString(this.type);
        dest.writeInt(this.index);
    }

    public ExamItemInfo() {
    }

    protected ExamItemInfo(Parcel in) {
        this.progress = in.readInt();
        this.type = in.readString();
        this.index = in.readInt();
    }

    public static final Parcelable.Creator<ExamItemInfo> CREATOR = new Parcelable.Creator<ExamItemInfo>() {
        public ExamItemInfo createFromParcel(Parcel source) {
            return new ExamItemInfo(source);
        }

        public ExamItemInfo[] newArray(int size) {
            return new ExamItemInfo[size];
        }
    };
}
