package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * 评论列表信息
 * Created by kmdai on 2015/4/15.
 */
public class Assessment extends RefreshEntity {
    /**
     * 未开始INIT
     */
    public final static String INIT = "INIT";
    /**
     * 进行中PROGRESS
     */
    public final static String PROGRESS = "PROGRESS";
    /**
     * 已结束END
     */
    public final static String END = "END";
    public final static int TYPE_ASSESSMENT = REFRESH_TYPE_LASTVIEW + 1;

    public final static String TYPE_LIVE = "LIVE";
    public final static String TYPE_VIDEO = "VIDEO";
    public final static String TYPE_RESOURCE = "RESOURCE";
    /**
     * 评课id
     */
    private String evaluationId;
    /**
     * 课程名称
     */
    private String title;
    /**
     * 讲课老师名
     */
    private String teacherName;
    /**
     * 教室名称
     */
    private String classroomName;
    /**
     * 学科名
     */
    private String subjectName;
    /**
     * 开课日期（YYYY-MM-dd）
     */
    private String scheduleDate;
    /**
     * 课时，第几节
     */
    private String classSeq;
    /**
     * 总分
     */
    private int totalScore;
    /**
     * 平均评分
     */
    private double averageScore;
    /**
     * 状态码：未开始INIT，进行中PROGRESS，已结束END
     */
    private String status;

    private String scoreType;

    /**LIVE/直播,VIDEO/录播，RESOURCE/优课资源**/
    private String evaType;//评课类型 RESOURCE/VIDEO/

    public String getEvaType() {
        return evaType;
    }

    public void setEvaType(String evaType) {
        this.evaType = evaType;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public String getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getClassSeq() {
        return classSeq;
    }

    public void setClassSeq(String classSeq) {
        this.classSeq = classSeq;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    /**
     * @param object
     * @param assessments
     */
    public static void getAssess(JSONObject object, List<Assessment> assessments) {
        if ("success".equals(object.optString("result"))) {
            JSONArray jsonArray = object.optJSONArray("evaluations");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                Assessment assessment = new Assessment();
                assessment.setTitle(jsonObject.optString("title"));
                assessment.setEvaluationId(jsonObject.optString("evaluationId"));
                assessment.setTeacherName(jsonObject.optString("teacherName"));
                assessment.setSubjectName(jsonObject.optString("subjectName"));
                assessment.setScheduleDate(jsonObject.optString("scheduleDate"));
                assessment.setClassSeq(jsonObject.optString("classSeq"));
                assessment.setTotalScore(jsonObject.optInt("totalScore"));
                assessment.setAverageScore(jsonObject.optDouble("averageScore", 0));
                assessment.setClassroomName(jsonObject.optString("classroomName"));
                assessment.setStatus(jsonObject.optString("status"));
                assessment.setScoreType(jsonObject.optString("scoreType"));
                assessment.setmHolderType(TYPE_ASSESSMENT);
                assessment.setEvaType(jsonObject.optString("eva_type"));
                assessments.add(assessment);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.evaluationId);
        dest.writeString(this.title);
        dest.writeString(this.teacherName);
        dest.writeString(this.classroomName);
        dest.writeString(this.subjectName);
        dest.writeString(this.scheduleDate);
        dest.writeString(this.classSeq);
        dest.writeInt(this.totalScore);
        dest.writeDouble(this.averageScore);
        dest.writeString(this.status);
        dest.writeString(this.scoreType);
    }

    public Assessment() {
    }

    protected Assessment(Parcel in) {
        super(in);
        this.evaluationId = in.readString();
        this.title = in.readString();
        this.teacherName = in.readString();
        this.classroomName = in.readString();
        this.subjectName = in.readString();
        this.scheduleDate = in.readString();
        this.classSeq = in.readString();
        this.totalScore = in.readInt();
        this.averageScore = in.readDouble();
        this.status = in.readString();
        this.scoreType = in.readString();
    }

    public static final Creator<Assessment> CREATOR = new Creator<Assessment>() {
        @Override
        public Assessment createFromParcel(Parcel source) {
            return new Assessment(source);
        }

        @Override
        public Assessment[] newArray(int size) {
            return new Assessment[size];
        }
    };
}
