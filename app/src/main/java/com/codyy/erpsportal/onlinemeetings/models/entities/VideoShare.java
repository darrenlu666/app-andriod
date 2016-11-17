package com.codyy.erpsportal.onlinemeetings.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * 视频会议-视频共享
 * Created by poe on 15-9-24.
 */
public class VideoShare implements Parcelable{

    private int videoSwitch;//0 :关闭 1：打开
    private String videoID;//资源id .
    private String videoTotal;//视频总时长
    private String videoStreamer;//视频共享的stream

    public VideoShare() {
    }

    protected VideoShare(Parcel in) {
        videoSwitch = in.readInt();
        videoID = in.readString();
        videoTotal = in.readString();
        videoStreamer = in.readString();
    }

    public static final Creator<VideoShare> CREATOR = new Creator<VideoShare>() {
        @Override
        public VideoShare createFromParcel(Parcel in) {
            return new VideoShare(in);
        }

        @Override
        public VideoShare[] newArray(int size) {
            return new VideoShare[size];
        }
    };

    public int getVideoSwitch() {
        return videoSwitch;
    }

    public void setVideoSwitch(int videoSwitch) {
        this.videoSwitch = videoSwitch;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getVideoTotal() {
        return videoTotal;
    }

    public void setVideoTotal(String videoTotal) {
        this.videoTotal = videoTotal;
    }

    public String getVideoStreamer() {
        return videoStreamer;
    }

    public void setVideoStreamer(String videoStreamer) {
        this.videoStreamer = videoStreamer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(videoSwitch);
        parcel.writeString(videoID);
        parcel.writeString(videoTotal);
        parcel.writeString(videoStreamer);
    }

    /**
     * parse one data  .
     * @param vsJsonObject
     * @return
     */
    public static VideoShare parseOneData(JSONObject vsJsonObject){
        if(null == vsJsonObject) return  null;

        VideoShare vs = new VideoShare();
        vs.setVideoSwitch(vsJsonObject.optInt("video_share"));
        vs.setVideoID(vsJsonObject.optString("video_id"));
        vs.setVideoTotal(vsJsonObject.optString("video_total"));
        vs.setVideoStreamer(vsJsonObject.optString("video_stream"));

        return  vs;
    }


}
