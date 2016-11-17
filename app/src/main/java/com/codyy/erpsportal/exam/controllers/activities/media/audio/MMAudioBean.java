package com.codyy.erpsportal.exam.controllers.activities.media.audio;

/**
 * @author lijian
 */

import android.os.Parcel;
import android.os.Parcelable;

public class MMAudioBean implements Parcelable {

    private String name = null;
    private String path = null;
    private String time = null;
    private String date = null;

    public MMAudioBean(String date, String name, String path, String time) {
        this.date = date;
        this.name = name;
        this.path = path;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.time);
        dest.writeString(this.date);
    }

    protected MMAudioBean(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.time = in.readString();
        this.date = in.readString();
    }

    public static final Creator<MMAudioBean> CREATOR = new Creator<MMAudioBean>() {
        public MMAudioBean createFromParcel(Parcel source) {
            return new MMAudioBean(source);
        }

        public MMAudioBean[] newArray(int size) {
            return new MMAudioBean[size];
        }
    };

    @Override
    public String toString() {
        return "MMAudioBean{" +
                "date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
