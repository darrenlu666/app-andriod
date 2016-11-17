package com.codyy.erpsportal.perlcourseprep.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.models.parsers.JsonParsable;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 教案详情
 * Created by gujiajia on 2016/1/29.
 */
public class LessonPlanDetails implements Parcelable {

    /**
     * schoolName : null
     * operateTime : 1453098544420
     * sectionName : null
     * contentType : RICH_TEXT
     * serverResourceId : null
     * volumnName : 第一册
     * teacherName : 进来撒接欧文UR的萨芬就访客
     * areaId : null
     * subjectName : 语文
     * classlevelName : 一年级
     * canCompose : true
     * baseUserId : d64464a20ed241c0a47aae0e8155c257
     * richContent : <p>姐姐诶已未考虑加额外确<strong>认请额外</strong>负担<span style="text-decoration: underline;">是转发去玩儿去玩</span>儿完钱二分区发却无法去玩儿法</p><p>日期无法大方的说法武清千万去去玩儿fwewerrq<br/></p>
     * lessonPlanPath : null
     * serverAddress : null
     * lessonPlanName : 资源存储测试rihtext
     * chapterName : null
     * versionName : 人教版
     * content : {"lessonPlanName":"资源存储测试rihtext","coursewareList":[],"richContent":"<p>姐姐诶已未考虑加额外确<strong>认请额外<\/strong>负担<span style=\"text-decoration: underline;\">是转发去玩儿去玩<\/span>儿完钱二分区发却无法去玩儿法<\/p><p>日期无法大方的说法武清千万去去玩儿fwewerrq<br/><\/p>"}
     * isRecomend : null
     * avgScore : 0
     * lessonPlanId : 6613d87a059c4c8d89992e1673c98f10
     * schoolId : null
     * subjectPic : headPicDefault.jpg
     * rethink : null
     * viewCount : 1
     */

    public final static String TYPE_RICH_TEXT = "RICH_TEXT";

    public final static String TYPE_OFFICE = "OFFICE";

    public final static String EXTRA_LESSON_PLAN_DETAILS = "com.codyy.erpsportal.lesson";

    private String schoolName;
    private long operateTime;
    private String sectionName;
    private String contentType;
    private String serverResourceId;
    private String volumnName;
    private String teacherName;
    private String areaId;
    private String subjectName;
    private String classlevelName;
    private boolean canCompose;
    private String baseUserId;
    private String richContent;
    private String lessonPlanPath;
    private String serverAddress;
    private String lessonPlanName;
    private String chapterName;
    private String versionName;
    private String isRecomend;
    private String avgScore;
    private String lessonPlanId;
    private String schoolId;
    private String subjectPic;
    private String rethink;
    private String viewCount;

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setOperateTime(long operateTime) {
        this.operateTime = operateTime;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setServerResourceId(String serverResourceId) {
        this.serverResourceId = serverResourceId;
    }

    public void setVolumnName(String volumnName) {
        this.volumnName = volumnName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public void setCanCompose(boolean canCompose) {
        this.canCompose = canCompose;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public void setRichContent(String richContent) {
        this.richContent = richContent;
    }

    public void setLessonPlanPath(String lessonPlanPath) {
        this.lessonPlanPath = lessonPlanPath;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setLessonPlanName(String lessonPlanName) {
        this.lessonPlanName = lessonPlanName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setIsRecomend(String isRecomend) {
        this.isRecomend = isRecomend;
    }

    public void setAvgScore(String avgScore) {
        this.avgScore = avgScore;
    }

    public void setLessonPlanId(String lessonPlanId) {
        this.lessonPlanId = lessonPlanId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public void setRethink(String rethink) {
        this.rethink = rethink;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public long getOperateTime() {
        return operateTime;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getContentType() {
        return contentType;
    }

    public String getServerResourceId() {
        return serverResourceId;
    }

    public String getVolumnName() {
        return volumnName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public boolean isCanCompose() {
        return canCompose;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public String getRichContent() {
        return richContent;
    }

    public String getLessonPlanPath() {
        return lessonPlanPath;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getLessonPlanName() {
        return lessonPlanName;
    }

    public String getChapterName() {
        return chapterName;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getIsRecomend() {
        return isRecomend;
    }

    public String getAvgScore() {
        return avgScore;
    }

    public String getLessonPlanId() {
        return lessonPlanId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public String getRethink() {
        return rethink;
    }

    public String getViewCount() {
        return viewCount;
    }

    public boolean isOfficeType() {
        return TYPE_OFFICE.equals(getContentType());
    }

    public static final JsonParsable<LessonPlanDetails> JSON_PARSER = new JsonParser<LessonPlanDetails>() {

        @Override
        public LessonPlanDetails parse(JSONObject jsonObject) {
            LessonPlanDetails lessonPlanDetails = new LessonPlanDetails();
            lessonPlanDetails.setLessonPlanId(jsonObject.optString("lessonPlanId"));
            lessonPlanDetails.setLessonPlanName(jsonObject.optString("lessonPlanName"));
//            lessonPlanDetails.setBaseUserId(jsonObject.optString("baseUserId"));
            lessonPlanDetails.setBaseUserId(jsonObject.optString("teacherId"));
            lessonPlanDetails.setTeacherName(jsonObject.optString("teacherName"));
            lessonPlanDetails.setClasslevelName(optStrOrNull(jsonObject, "classlevelName"));
            lessonPlanDetails.setSubjectName(optStrOrNull(jsonObject, "subjectName"));
            lessonPlanDetails.setVolumnName(optStrOrNull(jsonObject, "volumnName"));
            lessonPlanDetails.setVersionName(optStrOrNull(jsonObject, "versionName"));
            lessonPlanDetails.setChapterName(optStrOrNull(jsonObject, "chapterName"));
            lessonPlanDetails.setSectionName(optStrOrNull(jsonObject, "sectionName"));
            lessonPlanDetails.setContentType(jsonObject.optString("contentType"));
            lessonPlanDetails.setOperateTime(jsonObject.optLong("operateTime"));
//            if (!jsonObject.isNull("operateTime") && !TextUtils.isEmpty(jsonObject.optString("operateTime"))) {
//                lessonPlanDetails.setOperateTime(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")
//                        .parseDateTime(jsonObject.optString("operateTime"))
//                        .getMillis());
//            }
            lessonPlanDetails.setAvgScore(jsonObject.optString("avgScore"));
            lessonPlanDetails.setViewCount(jsonObject.optString("viewCount"));
            lessonPlanDetails.setRichContent(optStrOrNull(jsonObject, "richContent"));
            lessonPlanDetails.setRethink(optStrOrBlank(jsonObject, "rethink"));
            lessonPlanDetails.setServerAddress(optStrOrNull(jsonObject, "serverAddress"));
            lessonPlanDetails.setLessonPlanPath(optStrOrNull(jsonObject, "lessonPlanPath"));
            return lessonPlanDetails;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.schoolName);
        dest.writeLong(this.operateTime);
        dest.writeString(this.sectionName);
        dest.writeString(this.contentType);
        dest.writeString(this.serverResourceId);
        dest.writeString(this.volumnName);
        dest.writeString(this.teacherName);
        dest.writeString(this.areaId);
        dest.writeString(this.subjectName);
        dest.writeString(this.classlevelName);
        dest.writeByte(canCompose ? (byte) 1 : (byte) 0);
        dest.writeString(this.baseUserId);
        dest.writeString(this.richContent);
        dest.writeString(this.lessonPlanPath);
        dest.writeString(this.serverAddress);
        dest.writeString(this.lessonPlanName);
        dest.writeString(this.chapterName);
        dest.writeString(this.versionName);
        dest.writeString(this.isRecomend);
        dest.writeString(this.avgScore);
        dest.writeString(this.lessonPlanId);
        dest.writeString(this.schoolId);
        dest.writeString(this.subjectPic);
        dest.writeString(this.rethink);
        dest.writeString(this.viewCount);
    }

    public LessonPlanDetails() {
    }

    protected LessonPlanDetails(Parcel in) {
        this.schoolName = in.readString();
        this.operateTime = in.readLong();
        this.sectionName = in.readString();
        this.contentType = in.readString();
        this.serverResourceId = in.readString();
        this.volumnName = in.readString();
        this.teacherName = in.readString();
        this.areaId = in.readString();
        this.subjectName = in.readString();
        this.classlevelName = in.readString();
        this.canCompose = in.readByte() != 0;
        this.baseUserId = in.readString();
        this.richContent = in.readString();
        this.lessonPlanPath = in.readString();
        this.serverAddress = in.readString();
        this.lessonPlanName = in.readString();
        this.chapterName = in.readString();
        this.versionName = in.readString();
        this.isRecomend = in.readString();
        this.avgScore = in.readString();
        this.lessonPlanId = in.readString();
        this.schoolId = in.readString();
        this.subjectPic = in.readString();
        this.rethink = in.readString();
        this.viewCount = in.readString();
    }

    public static final Creator<LessonPlanDetails> CREATOR = new Creator<LessonPlanDetails>() {
        public LessonPlanDetails createFromParcel(Parcel source) {
            return new LessonPlanDetails(source);
        }

        public LessonPlanDetails[] newArray(int size) {
            return new LessonPlanDetails[size];
        }
    };

    public float obtainRate() {
        float rate;
        try{
            rate = Float.parseFloat(avgScore);
        } catch (Exception e) {
            e.printStackTrace();
            rate = 0f;
        }
        return rate;
    }
}
