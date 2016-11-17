package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 集体备课实体类
 * Created by yangxinwu on 2015/8/3.
 */

public class PreparationEntity implements Parcelable {
    /**
     * teacherName : 123456
     * scoreType : star
     * preparationId : 8
     * title : 活动1标题
     * startDate : 2015-03-14
     * subjectName : 语文
     * averageScore : 1
     * status : INIT
     */
    private String teacherName;
    private String scoreType;
    private String preparationId;
    private String title;
    private String startDate;
    private String subjectName;
    private int averageScore;
    private String status;
    private String totalScore;
    private String fromType;//区分是集体备课　还是　互动听课　{@link Constants.TYPE_PREPARE_LESSON}

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setPreparationId(String preparationId) {
        this.preparationId = preparationId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setAverageScore(int averageScore) {
        this.averageScore = averageScore;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getPreparationId() {
        return preparationId;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getAverageScore() {
        return averageScore;
    }

    public String getStatus() {
        return status;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public static PreparationEntity parseJson(JSONObject jsonObject,String type) {
        PreparationEntity preparationEntity = new PreparationEntity();
        preparationEntity.setFromType(type);
        if (type.equals(Constants.TYPE_PREPARE_LESSON)) {
            preparationEntity.setPreparationId(jsonObject.optString("preparationId"));
        }else {
            preparationEntity.setPreparationId(jsonObject.optString("lecturesId"));
        }
        preparationEntity.setTitle(jsonObject.optString("title"));
        preparationEntity.setTeacherName(jsonObject.optString("teacherName"));
        preparationEntity.setSubjectName(jsonObject.optString("subjectName"));
        preparationEntity.setStartDate(jsonObject.optString("startDate"));
        preparationEntity.setAverageScore(jsonObject.optInt("averageScore"));
        preparationEntity.setStatus(jsonObject.optString("status"));
        preparationEntity.setTotalScore(jsonObject.optString("totalScore"));
        return preparationEntity;
    }

    public static List<PreparationEntity> parseJsonArray(JSONArray jsonArray,String type) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        List<PreparationEntity> preparationEntitys = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            PreparationEntity preparationEntity = parseJson(jsonObject,type);
            preparationEntitys.add(preparationEntity);
        }
        return preparationEntitys;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.teacherName);
        dest.writeString(this.preparationId);
        dest.writeString(this.title);
        dest.writeString(this.startDate);
        dest.writeString(this.subjectName);
        dest.writeInt(this.averageScore);
        dest.writeString(this.status);
        dest.writeString(this.totalScore);
    }

    public PreparationEntity() {
    }

    protected PreparationEntity(Parcel in) {
        this.teacherName = in.readString();
        this.preparationId = in.readString();
        this.title = in.readString();
        this.startDate = in.readString();
        this.subjectName = in.readString();
        this.averageScore = in.readInt();
        this.status = in.readString();
        this.totalScore = in.readString();
    }

    public static final Parcelable.Creator<PreparationEntity> CREATOR = new Parcelable.Creator<PreparationEntity>() {
        public PreparationEntity createFromParcel(Parcel source) {
            return new PreparationEntity(source);
        }

        public PreparationEntity[] newArray(int size) {
            return new PreparationEntity[size];
        }
    };
}
