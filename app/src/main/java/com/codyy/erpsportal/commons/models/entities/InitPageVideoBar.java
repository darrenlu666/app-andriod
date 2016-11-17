package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by eachann on 2015/7/3.
 * 机位信息
 */
public class InitPageVideoBar implements Parcelable {
    private List<VideoBarMapInfo> mapInfos;
    private String directorMode;
    private int videoMain;

    @Override
    public String toString() {
        return "InitPageVideoBar{" +
                "mapInfos=" + mapInfos +
                ", directorMode='" + directorMode + '\'' +
                ", videoMain=" + videoMain +
                '}';
    }

    public List<VideoBarMapInfo> getMapInfos() {
        return mapInfos;
    }

    public void setMapInfos(List<VideoBarMapInfo> mapInfos) {
        this.mapInfos = mapInfos;
    }

    public String getDirectorMode() {
        return directorMode;
    }

    public void setDirectorMode(String directorMode) {
        this.directorMode = directorMode;
    }

    public int getVideoMain() {
        return videoMain;
    }

    public void setVideoMain(int videoMain) {
        this.videoMain = videoMain;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mapInfos);
        dest.writeString(this.directorMode);
        dest.writeInt(this.videoMain);
    }

    public InitPageVideoBar() {
    }

    protected InitPageVideoBar(Parcel in) {
        this.mapInfos = in.createTypedArrayList(VideoBarMapInfo.CREATOR);
        this.directorMode = in.readString();
        this.videoMain = in.readInt();
    }

    public static final Creator<InitPageVideoBar> CREATOR = new Creator<InitPageVideoBar>() {
        public InitPageVideoBar createFromParcel(Parcel source) {
            return new InitPageVideoBar(source);
        }

        public InitPageVideoBar[] newArray(int size) {
            return new InitPageVideoBar[size];
        }
    };
}
