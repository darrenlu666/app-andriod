package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;

/**
 * 评课议课评分
 * Created by gujiajia on 2016/9/23.
 */
public class EvaluationScore implements android.os.Parcelable {

    private String scoreType;

    private float avgScore;

    private float totalScore;

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public float getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(float avgScore) {
        this.avgScore = avgScore;
    }

    public float getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(float totalScore) {
        this.totalScore = totalScore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.scoreType);
        dest.writeFloat(this.avgScore);
        dest.writeFloat(this.totalScore);
    }

    public EvaluationScore() {
    }

    protected EvaluationScore(Parcel in) {
        this.scoreType = in.readString();
        this.avgScore = in.readFloat();
        this.totalScore = in.readFloat();
    }

    public static final Creator<EvaluationScore> CREATOR = new Creator<EvaluationScore>() {
        @Override
        public EvaluationScore createFromParcel(Parcel source) {
            return new EvaluationScore(source);
        }

        @Override
        public EvaluationScore[] newArray(int size) {
            return new EvaluationScore[size];
        }
    };
}
