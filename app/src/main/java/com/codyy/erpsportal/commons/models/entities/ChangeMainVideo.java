package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eachann on 2015/7/3.
 * 七．当前主画面，自动切换
 */
public class ChangeMainVideo implements Parcelable {
    String pos;

    @Override
    public String toString() {
        return "ChangeMainVideo{" +
                "pos='" + pos + '\'' +
                '}';
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pos);
    }

    public ChangeMainVideo() {
    }

    protected ChangeMainVideo(Parcel in) {
        this.pos = in.readString();
    }

    public static final Creator<ChangeMainVideo> CREATOR = new Creator<ChangeMainVideo>() {
        public ChangeMainVideo createFromParcel(Parcel source) {
            return new ChangeMainVideo(source);
        }

        public ChangeMainVideo[] newArray(int size) {
            return new ChangeMainVideo[size];
        }
    };
}
