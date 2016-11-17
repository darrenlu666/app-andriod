package com.codyy.erpsportal.rethink.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.models.parsers.JsonParsable;

import org.json.JSONObject;

/**
 * 教学反思详情
 * Created by gujiajia on 2016/1/25.
 */
public class RethinkDetails implements Parcelable {

    private String id;

    private String title;

    private String teacherName;

    private String type;

    private int viewCount;

    private String content;

    private String createTime;

    private boolean recommended;

    private String semesterName;

    private String subjectName;

    private String classLevelName;

    private String versionName;

    private String volumeName;

    private String chapterName;

    private String sectionName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getClassLevelName() {
        return classLevelName;
    }

    public void setClassLevelName(String classLevelName) {
        this.classLevelName = classLevelName;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public final static JsonParsable<RethinkDetails> JSON_PARSER = new JsonParser<RethinkDetails>() {
        @Override
        public RethinkDetails parse(JSONObject jsonObject) {
            RethinkDetails rethinkDetails = new RethinkDetails();
            rethinkDetails.setId(jsonObject.optString("rethinkId"));
            rethinkDetails.setContent(jsonObject.optString("rethinkContent"));
            rethinkDetails.setCreateTime(jsonObject.optString("createTime"));
            rethinkDetails.setTeacherName(jsonObject.optString("createUser"));
            rethinkDetails.setTitle(jsonObject.optString("rethinkTitle"));
            rethinkDetails.setType(jsonObject.optString("rethinkType"));
            rethinkDetails.setRecommended(jsonObject.optBoolean("recommanded"));
            rethinkDetails.setViewCount(jsonObject.optInt("viewCount"));
            rethinkDetails.setSemesterName( optStrOrNull(jsonObject, "semesterName"));
            rethinkDetails.setSubjectName( optStrOrNull(jsonObject, "subjectName"));
            rethinkDetails.setClassLevelName( optStrOrNull(jsonObject, "classlevelname"));
            rethinkDetails.setVersionName(optStrOrNull(jsonObject, "versionName"));
            rethinkDetails.setVolumeName( optStrOrNull(jsonObject, "volumeName"));
            rethinkDetails.setChapterName( optStrOrNull(jsonObject, "chaptername"));
            rethinkDetails.setSectionName( optStrOrNull(jsonObject, "sectionName"));
            return rethinkDetails;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.teacherName);
        dest.writeString(this.type);
        dest.writeInt(this.viewCount);
        dest.writeString(this.content);
        dest.writeString(this.createTime);
        dest.writeByte(recommended ? (byte) 1 : (byte) 0);
        dest.writeString(this.semesterName);
        dest.writeString(this.subjectName);
        dest.writeString(this.classLevelName);
        dest.writeString(this.volumeName);
        dest.writeString(this.chapterName);
        dest.writeString(this.sectionName);
    }

    public RethinkDetails() {
    }

    protected RethinkDetails(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.teacherName = in.readString();
        this.type = in.readString();
        this.viewCount = in.readInt();
        this.content = in.readString();
        this.createTime = in.readString();
        this.recommended = in.readByte() != 0;
        this.semesterName = in.readString();
        this.subjectName = in.readString();
        this.classLevelName = in.readString();
        this.volumeName = in.readString();
        this.chapterName = in.readString();
        this.sectionName = in.readString();
    }

    public static final Creator<RethinkDetails> CREATOR = new Creator<RethinkDetails>() {
        public RethinkDetails createFromParcel(Parcel source) {
            return new RethinkDetails(source);
        }

        public RethinkDetails[] newArray(int size) {
            return new RethinkDetails[size];
        }
    };
}
