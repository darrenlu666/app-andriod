package com.codyy.erpsportal.exam.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 试卷信息
 * Created by eachann on 2015/12/29.
 */
public class ExamInfo implements Parcelable {

    /**
     * examUpdateTime : 2015-07-28 13:30
     * examType : 周测试
     * examGrade : 一年级
     * examSubject : 英语
     * examDuartion : 120分钟
     * examTotalScore : 150分
     */
    /**
     * 更新时间: 2015-07-28 13:30
     */
    private String examUpdateTime;
    /**
     * 试卷类型:周测试
     */
    private String examType;
    /**
     * 年级
     */
    private String examGrade;
    /**
     * 学科
     */
    private String examSubject;
    /**
     * 时长
     */
    private String examDuartion;
    /**
     * 总分
     */
    private String examTotalScore;
    /**
     * 考试开始时间
     */
    private String examStartTime;
    /**
     * 考试结束时间
     */
    private String examCompleteTime;
    /**
     * 测试人数
     */
    private String examJoinNums;
    /**
     * 题数
     */
    private String examAmountOfQuestions;
    /**
     * 区域
     */
    private String examArea;
    /**
     * 年份
     */
    private String examYear;
    /**
     * 使用次数
     */
    private String examUsedCounts;

    public String getExamAmountOfQuestions() {
        return examAmountOfQuestions;
    }

    public void setExamAmountOfQuestions(String examAmountOfQuestions) {
        this.examAmountOfQuestions = examAmountOfQuestions;
    }

    public String getExamArea() {
        return examArea;
    }

    public void setExamArea(String examArea) {
        this.examArea = examArea;
    }

    public String getExamCompleteTime() {
        return examCompleteTime;
    }

    public void setExamCompleteTime(String examCompleteTime) {
        this.examCompleteTime = examCompleteTime;
    }

    public String getExamJoinNums() {
        return examJoinNums;
    }

    public void setExamJoinNums(String examJoinNums) {
        this.examJoinNums = examJoinNums;
    }

    public String getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime(String examStartTime) {
        this.examStartTime = examStartTime;
    }

    public String getExamUsedCounts() {
        return examUsedCounts;
    }

    public void setExamUsedCounts(String examUsedCounts) {
        this.examUsedCounts = examUsedCounts;
    }

    public String getExamYear() {
        return examYear;
    }

    public void setExamYear(String examYear) {
        this.examYear = examYear;
    }

    public void setExamUpdateTime(String examUpdateTime) {
        this.examUpdateTime = examUpdateTime;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public void setExamGrade(String examGrade) {
        this.examGrade = examGrade;
    }

    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }

    public void setExamDuartion(String examDuartion) {
        this.examDuartion = examDuartion;
    }

    public void setExamTotalScore(String examTotalScore) {
        this.examTotalScore = examTotalScore;
    }

    public String getExamUpdateTime() {
        return examUpdateTime;
    }

    public String getExamType() {
        return examType;
    }

    public String getExamGrade() {
        return examGrade;
    }

    public String getExamSubject() {
        return examSubject;
    }

    public String getExamDuartion() {
        return examDuartion;
    }

    public String getExamTotalScore() {
        return examTotalScore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.examUpdateTime);
        dest.writeString(this.examType);
        dest.writeString(this.examGrade);
        dest.writeString(this.examSubject);
        dest.writeString(this.examDuartion);
        dest.writeString(this.examTotalScore);
        dest.writeString(this.examStartTime);
        dest.writeString(this.examCompleteTime);
        dest.writeString(this.examJoinNums);
        dest.writeString(this.examAmountOfQuestions);
        dest.writeString(this.examArea);
        dest.writeString(this.examYear);
        dest.writeString(this.examUsedCounts);
    }

    public ExamInfo() {
    }

    protected ExamInfo(Parcel in) {
        this.examUpdateTime = in.readString();
        this.examType = in.readString();
        this.examGrade = in.readString();
        this.examSubject = in.readString();
        this.examDuartion = in.readString();
        this.examTotalScore = in.readString();
        this.examStartTime = in.readString();
        this.examCompleteTime = in.readString();
        this.examJoinNums = in.readString();
        this.examAmountOfQuestions = in.readString();
        this.examArea = in.readString();
        this.examYear = in.readString();
        this.examUsedCounts = in.readString();
    }

    public static final Parcelable.Creator<ExamInfo> CREATOR = new Parcelable.Creator<ExamInfo>() {
        public ExamInfo createFromParcel(Parcel source) {
            return new ExamInfo(source);
        }

        public ExamInfo[] newArray(int size) {
            return new ExamInfo[size];
        }
    };
}
