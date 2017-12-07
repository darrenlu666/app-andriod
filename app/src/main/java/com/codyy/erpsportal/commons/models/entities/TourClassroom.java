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

    private String videoUrl;

    /**
     * 主课堂main还是接收课堂receive
     */
    private String type = "main";

    private String gradeName;

    private String subjectName;

    private String captureUrl;

    private String areaPath;//路径

    private String classroomName;//教室名

    private boolean showClassRoomName;//是否有多间教室

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public boolean isShowClassRoomName() {
        return showClassRoomName;
    }

    public void setShowClassRoomName(boolean showClassRoomName) {
        this.showClassRoomName = showClassRoomName;
    }

    public String getAreaPath() {
        return areaPath;
    }

    public void setAreaPath(String areaPath) {
        this.areaPath = areaPath;
    }

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
        dest.writeString(this.videoUrl);
        dest.writeString(this.type);
        dest.writeString(this.gradeName);
        dest.writeString(this.subjectName);
        dest.writeString(this.captureUrl);
    }

    protected TourClassroom(Parcel in) {
        this.id = in.readString();
        this.schoolName = in.readString();
        this.teacherName = in.readString();
        this.videoUrl = in.readString();
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
