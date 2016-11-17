package com.codyy.erpsportal.homework.models.entities.student;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 学生答题上传的图片的信息
 * Created by ldh on 2016/5/31.
 */
public class ImageDetail implements Parcelable{
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }


    public String getLoadInfo() {
        return loadInfo;
    }

    public void setLoadInfo(String loadInfo) {
        this.loadInfo = loadInfo;
    }

    private String loadInfo;
    private String picUrl;
    public static final String TYPE_LOADING = "TYPE_LOADING";
    public static final String TYPE_LOADED = "TYPE_LOADED";
    public static final String TYPE_LOAD_FAILED = "TYPE_LOAD_FAILED";


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.loadInfo);
        dest.writeString(this.picUrl);
    }

    public ImageDetail(){

    }

    public ImageDetail(Parcel in){
        this.loadInfo = in.readString();
        this.picUrl = in.readString();
    }

    public static final Parcelable.Creator<ImageDetail> CREATOR = new Parcelable.Creator<ImageDetail>() {
        public ImageDetail createFromParcel(Parcel source) {
            return new ImageDetail(source);
        }

        public ImageDetail[] newArray(int size) {
            return new ImageDetail[size];
        }
    };
}
