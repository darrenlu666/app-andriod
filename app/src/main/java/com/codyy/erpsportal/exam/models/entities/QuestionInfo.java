package com.codyy.erpsportal.exam.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 习题信息
 * Created by eachann on 2015/12/29.
 */
public class QuestionInfo implements Parcelable {

    /**
     * questionType : SINGLE_CHOICE
     * questionContent : 题干
     * questionOptions : 选项
     * questionMediaUrl : ***.mp3/***.mp4
     * questionCorrectAnswer : 正确答案
     * questionResolve : 习题解析
     * questionKnowledgePoint : 知识点
     * questionAssembleCounts : 组卷次数
     * questionUpdateDate : yyyy-MM-dd
     * questionDifficultyFactor : LITTLE_EASY
     * questionComeFrom : 来源
     */

    private String questionType;
    private String questionContent;
    private String questionOptions;
    private String questionMediaUrl;
    private String questionCorrectAnswer;
    private String questionResolve;
    private String questionKnowledgePoint;
    private String questionAssembleCounts;
    private String questionUpdateDate;
    private String questionDifficultyFactor;
    private String questionComeFrom;
    private String questionScores;
    private int questionScore;
    private String questionId;
    private String questionResultId;
    private int questionBlankCounts;

    private String questionTeacherReviews;
    private String questionStudentAnswer;
    private String questionResolveVideo;
    private String fillInScoreType;
    private String fillInAnswerType;
    private String fillInAnswers;
    private String questionStudentAnswerMediaUrl;
    private String examTaskId;
    private String studentId;
    private String hint;//填空;主观题;

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getExamTaskId() {
        return examTaskId;
    }

    public void setExamTaskId(String examTaskId) {
        this.examTaskId = examTaskId;
    }

    public String getFillInAnswers() {
        return fillInAnswers;
    }

    public void setFillInAnswers(String fillInAnswers) {
        this.fillInAnswers = fillInAnswers;
    }

    public String getFillInAnswerType() {
        return fillInAnswerType;
    }

    public void setFillInAnswerType(String fillInAnswerType) {
        this.fillInAnswerType = fillInAnswerType;
    }

    public String getFillInScoreType() {
        return fillInScoreType;
    }

    public void setFillInScoreType(String fillInScoreType) {
        this.fillInScoreType = fillInScoreType;
    }

    public String getQuestionResolveVideo() {
        return questionResolveVideo;
    }

    public void setQuestionResolveVideo(String questionResolveVideo) {
        this.questionResolveVideo = questionResolveVideo;
    }

    public String getQuestionStudentAnswer() {
        return questionStudentAnswer;
    }

    public void setQuestionStudentAnswer(String questionStudentAnswer) {
        this.questionStudentAnswer = questionStudentAnswer;
    }

    public String getQuestionStudentAnswerMediaUrl() {
        return questionStudentAnswerMediaUrl;
    }

    public void setQuestionStudentAnswerMediaUrl(String questionStudentAnswerMediaUrl) {
        this.questionStudentAnswerMediaUrl = questionStudentAnswerMediaUrl;
    }

    public String getQuestionTeacherReviews() {
        return questionTeacherReviews;
    }

    public void setQuestionTeacherReviews(String questionTeacherReviews) {
        this.questionTeacherReviews = questionTeacherReviews;
    }

    public int getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(int questionScore) {
        this.questionScore = questionScore;
    }

    public int getQuestionBlankCounts() {
        return questionBlankCounts;
    }

    public void setQuestionBlankCounts(int questionBlankCounts) {
        this.questionBlankCounts = questionBlankCounts;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionResultId() {
        return questionResultId;
    }

    public void setQuestionResultId(String questionResultId) {
        this.questionResultId = questionResultId;
    }

    public String getQuestionScores() {
        return questionScores;
    }

    public void setQuestionScores(String questionScores) {
        this.questionScores = questionScores;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public void setQuestionOptions(String questionOptions) {
        this.questionOptions = questionOptions;
    }

    public void setQuestionMediaUrl(String questionMediaUrl) {
        this.questionMediaUrl = questionMediaUrl;
    }

    public void setQuestionCorrectAnswer(String questionCorrectAnswer) {
        this.questionCorrectAnswer = questionCorrectAnswer;
    }

    public void setQuestionResolve(String questionResolve) {
        this.questionResolve = questionResolve;
    }

    public void setQuestionKnowledgePoint(String questionKnowledgePoint) {
        this.questionKnowledgePoint = questionKnowledgePoint;
    }

    public void setQuestionAssembleCounts(String questionAssembleCounts) {
        this.questionAssembleCounts = questionAssembleCounts;
    }

    public void setQuestionUpdateDate(String questionUpdateDate) {
        this.questionUpdateDate = questionUpdateDate;
    }

    public void setQuestionDifficultyFactor(String questionDifficultyFactor) {
        this.questionDifficultyFactor = questionDifficultyFactor;
    }

    public void setQuestionComeFrom(String questionComeFrom) {
        this.questionComeFrom = questionComeFrom;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public String getQuestionOptions() {
        return questionOptions;
    }

    public String getQuestionMediaUrl() {
        return questionMediaUrl;
    }

    public String getQuestionCorrectAnswer() {
        return questionCorrectAnswer;
    }

    public String getQuestionResolve() {
        return questionResolve;
    }

    public String getQuestionKnowledgePoint() {
        return questionKnowledgePoint;
    }

    public String getQuestionAssembleCounts() {
        return questionAssembleCounts;
    }

    public String getQuestionUpdateDate() {
        return questionUpdateDate;
    }

    public String getQuestionDifficultyFactor() {
        return questionDifficultyFactor;
    }

    public String getQuestionComeFrom() {
        return questionComeFrom;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.questionType);
        dest.writeString(this.questionContent);
        dest.writeString(this.questionOptions);
        dest.writeString(this.questionMediaUrl);
        dest.writeString(this.questionCorrectAnswer);
        dest.writeString(this.questionResolve);
        dest.writeString(this.questionKnowledgePoint);
        dest.writeString(this.questionAssembleCounts);
        dest.writeString(this.questionUpdateDate);
        dest.writeString(this.questionDifficultyFactor);
        dest.writeString(this.questionComeFrom);
        dest.writeString(this.questionScores);
        dest.writeInt(this.questionScore);
        dest.writeString(this.questionId);
        dest.writeString(this.questionResultId);
        dest.writeInt(this.questionBlankCounts);
        dest.writeString(this.questionTeacherReviews);
        dest.writeString(this.questionStudentAnswer);
        dest.writeString(this.questionResolveVideo);
        dest.writeString(this.fillInScoreType);
        dest.writeString(this.fillInAnswerType);
        dest.writeString(this.fillInAnswers);
        dest.writeString(this.questionStudentAnswerMediaUrl);
        dest.writeString(this.examTaskId);
        dest.writeString(this.studentId);
        dest.writeString(this.hint);
    }

    public QuestionInfo() {
    }

    protected QuestionInfo(Parcel in) {
        this.questionType = in.readString();
        this.questionContent = in.readString();
        this.questionOptions = in.readString();
        this.questionMediaUrl = in.readString();
        this.questionCorrectAnswer = in.readString();
        this.questionResolve = in.readString();
        this.questionKnowledgePoint = in.readString();
        this.questionAssembleCounts = in.readString();
        this.questionUpdateDate = in.readString();
        this.questionDifficultyFactor = in.readString();
        this.questionComeFrom = in.readString();
        this.questionScores = in.readString();
        this.questionScore = in.readInt();
        this.questionId = in.readString();
        this.questionResultId = in.readString();
        this.questionBlankCounts = in.readInt();
        this.questionTeacherReviews = in.readString();
        this.questionStudentAnswer = in.readString();
        this.questionResolveVideo = in.readString();
        this.fillInScoreType = in.readString();
        this.fillInAnswerType = in.readString();
        this.fillInAnswers = in.readString();
        this.questionStudentAnswerMediaUrl = in.readString();
        this.examTaskId = in.readString();
        this.studentId = in.readString();
        this.hint = in.readString();
    }

    public static final Creator<QuestionInfo> CREATOR = new Creator<QuestionInfo>() {
        @Override
        public QuestionInfo createFromParcel(Parcel source) {
            return new QuestionInfo(source);
        }

        @Override
        public QuestionInfo[] newArray(int size) {
            return new QuestionInfo[size];
        }
    };
}
