package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 课堂巡视的课堂
 * Created by GuJiajia on 2015/4/20.
 */
public class TourClassroom implements Parcelable {
    private final static String TAG = "TourClassroom";

    /**
     * 课id
     */
    private String id;

    /**
     * 学校名称
     */
    private String schoolName;

    private String teacherName;

    private String classRoomId;

    private String videoUrl;

    /**
     * 流类型DMC或PMS
     */
    private String streamingServerType;

    /**
     * 实际是pms服务器地址
     */
    private String pmsServerHost;

    /**
     * dmc地址，用于获取dms地址
     */
    private String dmsServerHost;

    /**
     * 主课堂main还是接收课堂receive
     */
    private String type = "main";

    private String gradeName;

    private String subjectName;

    private String captureUrl;

    public TourClassroom() {
    }

    public TourClassroom(String schoolName, String videoUrl) {
        this.schoolName = schoolName;
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCaptureUrl() {
        return captureUrl;
    }

    public void setCaptureUrl(String captureUrl) {
        this.captureUrl = captureUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(String classRoomId) {
        this.classRoomId = classRoomId;
    }

    public String getStreamingServerType() {
        return streamingServerType;
    }

    public void setStreamingServerType(String streamingServerType) {
        this.streamingServerType = streamingServerType;
    }

    public String getPmsServerHost() {
        return pmsServerHost;
    }

    public void setPmsServerHost(String pmsServerHost) {
        this.pmsServerHost = pmsServerHost;
    }

    public String getDmsServerHost() {
        return dmsServerHost;
    }

    public void setDmsServerHost(String dmsServerHost) {
        this.dmsServerHost = dmsServerHost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public boolean isMain() {
        return "main".equals(type);
    }

    public boolean isServerTypePms() {
        return "PMS".equals(streamingServerType);
    }

    @Override
    public String toString() {
        return super.toString();
    }


    public interface VideoUrlCallback {
        void onUrlFetched(String url);

        void onError();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.schoolName);
        dest.writeString(this.teacherName);
        dest.writeString(this.classRoomId);
        dest.writeString(this.videoUrl);
        dest.writeString(this.streamingServerType);
        dest.writeString(this.pmsServerHost);
        dest.writeString(this.dmsServerHost);
        dest.writeString(this.type);
        dest.writeString(this.gradeName);
        dest.writeString(this.subjectName);
        dest.writeString(this.captureUrl);
    }

    protected TourClassroom(Parcel in) {
        this.id = in.readString();
        this.schoolName = in.readString();
        this.teacherName = in.readString();
        this.classRoomId = in.readString();
        this.videoUrl = in.readString();
        this.streamingServerType = in.readString();
        this.pmsServerHost = in.readString();
        this.dmsServerHost = in.readString();
        this.type = in.readString();
        this.gradeName = in.readString();
        this.subjectName = in.readString();
        this.captureUrl = in.readString();
    }

    public static final Creator<TourClassroom> CREATOR = new Creator<TourClassroom>() {
        @Override
        public TourClassroom createFromParcel(Parcel source) {
            return new TourClassroom(source);
        }

        @Override
        public TourClassroom[] newArray(int size) {
            return new TourClassroom[size];
        }
    };
}
