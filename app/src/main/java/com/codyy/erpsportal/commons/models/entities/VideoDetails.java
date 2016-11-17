package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxinwu on 2015/8/17.
 */
public class VideoDetails extends BaseTitleItemBar implements Parcelable {


    /**
     * strFileStatus : 已归档
     * filePath : N:/meet/95/d8/c6/af7f1f2a-9114-4883-8eb4-bff05c390ea0.trans.mp4
     * resolutionHeight : 1080
     * meetingId : eb3982d7069443309a2952188b2129c4
     * duration : 86235
     * uploadUserId : 02d13c173d7c4f2ab1e40c1fa60f39a0
     * fileStatus : FILED
     * createTime : 1438238662000
     * videoName : CodyyRec1.flv
     * storeServer : null
     * meetVideoId : 2222222222333
     * uploadUserName : 习大大
     * strCreateTime : 2015-07-30 14:44:22
     * thumbPath : 5f73030a-5bc1-4c42-92be-98b309724318.jpg
     * transFlag : TRANS_SUCCESS
     * resolutionWidth : 1920
     * localFilePath : null
     * strDuration : 23时57分15秒
     */
    private String strFileStatus;
    private String filePath;
    private int resolutionHeight;
    private String meetingId;
    private int duration;
    private String uploadUserId;
    private String fileStatus;
    private long createTime;
    private String videoName;
    private String storeServer;
    private String meetVideoId;
    private String uploadUserName;
    private String strCreateTime;
    private String thumbPath;
    private String transFlag;
    private int resolutionWidth;
    private String localFilePath;
    private String strDuration;

    public void setStrFileStatus(String strFileStatus) {
        this.strFileStatus = strFileStatus;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setUploadUserId(String uploadUserId) {
        this.uploadUserId = uploadUserId;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setStoreServer(String storeServer) {
        this.storeServer = storeServer;
    }

    public void setMeetVideoId(String meetVideoId) {
        this.meetVideoId = meetVideoId;
    }

    public void setUploadUserName(String uploadUserName) {
        this.uploadUserName = uploadUserName;
    }

    public void setStrCreateTime(String strCreateTime) {
        this.strCreateTime = strCreateTime;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public void setTransFlag(String transFlag) {
        this.transFlag = transFlag;
    }

    public void setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public void setStrDuration(String strDuration) {
        this.strDuration = strDuration;
    }

    public String getStrFileStatus() {
        return strFileStatus;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public int getDuration() {
        return duration;
    }

    public String getUploadUserId() {
        return uploadUserId;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getStoreServer() {
        return storeServer;
    }

    public String getMeetVideoId() {
        return meetVideoId;
    }

    public String getUploadUserName() {
        return uploadUserName;
    }

    public String getStrCreateTime() {
        return strCreateTime;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public String getTransFlag() {
        return transFlag;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public String getStrDuration() {
        return strDuration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.strFileStatus);
        dest.writeString(this.filePath);
        dest.writeInt(this.resolutionHeight);
        dest.writeString(this.meetingId);
        dest.writeInt(this.duration);
        dest.writeString(this.uploadUserId);
        dest.writeString(this.fileStatus);
        dest.writeLong(this.createTime);
        dest.writeString(this.videoName);
        dest.writeString(this.storeServer);
        dest.writeString(this.meetVideoId);
        dest.writeString(this.uploadUserName);
        dest.writeString(this.strCreateTime);
        dest.writeString(this.thumbPath);
        dest.writeString(this.transFlag);
        dest.writeInt(this.resolutionWidth);
        dest.writeString(this.localFilePath);
        dest.writeString(this.strDuration);
    }

    public VideoDetails() {
    }

    protected VideoDetails(Parcel in) {
        this.strFileStatus = in.readString();
        this.filePath = in.readString();
        this.resolutionHeight = in.readInt();
        this.meetingId = in.readString();
        this.duration = in.readInt();
        this.uploadUserId = in.readString();
        this.fileStatus = in.readString();
        this.createTime = in.readLong();
        this.videoName = in.readString();
        this.storeServer = in.readString();
        this.meetVideoId = in.readString();
        this.uploadUserName = in.readString();
        this.strCreateTime = in.readString();
        this.thumbPath = in.readString();
        this.transFlag = in.readString();
        this.resolutionWidth = in.readInt();
        this.localFilePath = in.readString();
        this.strDuration = in.readString();
    }

    public static final Parcelable.Creator<VideoDetails> CREATOR = new Parcelable.Creator<VideoDetails>() {
        public VideoDetails createFromParcel(Parcel source) {
            return new VideoDetails(source);
        }

        public VideoDetails[] newArray(int size) {
            return new VideoDetails[size];
        }
    };

    public static   VideoDetails parseJson(JSONObject jsonObject){

        VideoDetails videoDetails = new VideoDetails();
        videoDetails.setMeetVideoId(jsonObject.optString("meetVideoId"));
        videoDetails.setMeetingId(jsonObject.optString("meetingId"));
        videoDetails.setVideoName(jsonObject.optString("videoName"));
        videoDetails.setThumbPath(jsonObject.optString("thumbPath"));
        videoDetails.setFileStatus(jsonObject.optString("fileStatus"));
        videoDetails.setLocalFilePath(jsonObject.optString("localFilePath"));
        videoDetails.setFilePath(jsonObject.optString("filePath"));
        videoDetails.setResolutionHeight(jsonObject.optInt("resolutionHeight"));
        videoDetails.setResolutionWidth(jsonObject.optInt("resolutionWidth"));
        videoDetails.setDuration(jsonObject.optInt("duration"));
        videoDetails.setTransFlag(jsonObject.optString("transFlag"));
        videoDetails.setStoreServer(jsonObject.optString("storeServer"));
        videoDetails.setUploadUserId(jsonObject.optString("uploadUserId"));
        videoDetails.setCreateTime(jsonObject.optLong("createTime"));
        videoDetails.setUploadUserName(jsonObject.optString("uploadUserName"));
        videoDetails.setFileStatus(jsonObject.optString("strFileStatus"));
        videoDetails.setStrDuration(jsonObject.optString("strDuration"));
        videoDetails.setStrCreateTime(jsonObject.optString("strCreateTime"));
        return videoDetails;
    }


    public static List<VideoDetails> parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        List<VideoDetails> videoDetailList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            VideoDetails videoDetails = parseJson(jsonObject);
            videoDetailList.add(videoDetails);
        }
        return videoDetailList;
    }

}
