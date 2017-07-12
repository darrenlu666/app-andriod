package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.utils.NumberUtils;
import com.codyy.erpsportal.commons.utils.UriUtils;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxinwu on 2015/8/11.
 */
public class PrepareLessonsShortEntity extends BaseTitleItemBar implements Parcelable {

    /**
     * subjectPic : 6ca4eff5-dba8-4283-b9a0-8986ce6d91ab.jpg
     * mainTeacher : 让胡路小学
     * startTime : 2015-08-06
     * id : 71c65a6c8f994410951abc9a9d03617b
     * viewCount : 4
     * title : 13
     * totalScore : null
     * averageScore : null
     */
    private String subjectPic;
    private String mainTeacher;
    private long startTime;
    private String id;
    private int viewCount;
    private String title;
    private String totalScore;
    private float averageScore;
    private String scoreType;

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public void setMainTeacher(String mainTeacher) {
        this.mainTeacher = mainTeacher;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public void setAverageScore(float averageScore) {
        this.averageScore = averageScore;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public String getMainTeacher() {
        return mainTeacher;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getId() {
        return id;
    }

    public int getViewCount() {
        return viewCount;
    }

    public String getTitle() {
        return title;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public float getAverageScore() {
        return averageScore;
    }


    public PrepareLessonsShortEntity() {
    }

    public static PrepareLessonsShortEntity parseJson(JSONObject jsonObject) {
        PrepareLessonsShortEntity prepareLessonsShortEntity = new PrepareLessonsShortEntity();
        prepareLessonsShortEntity.setId(jsonObject.optString("id"));
        prepareLessonsShortEntity.setTitle(jsonObject.optString("title"));
        prepareLessonsShortEntity.setMainTeacher(jsonObject.optString("mainTeacher"));
        prepareLessonsShortEntity.setStartTime(jsonObject.optLong("startTime"));
        prepareLessonsShortEntity.setTotalScore(jsonObject.optString("totalScore"));
        prepareLessonsShortEntity.setAverageScore(NumberUtils.floatOf(jsonObject.optString("averageScore")));
        prepareLessonsShortEntity.setViewCount(jsonObject.optInt("viewCount"));
        prepareLessonsShortEntity.setSubjectPic(UriUtils.getImageUrl(jsonObject.optString("subjectPic")));
        prepareLessonsShortEntity.setScoreType(jsonObject.optString("scoreType"));
        return prepareLessonsShortEntity;
    }

    public static List<PrepareLessonsShortEntity> parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        List<PrepareLessonsShortEntity> prepareLessonsShortEntitys = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            PrepareLessonsShortEntity prepareLessonsShortEntity = parseJson(jsonObject);
            prepareLessonsShortEntitys.add(prepareLessonsShortEntity);
        }
        return prepareLessonsShortEntitys;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subjectPic);
        dest.writeString(this.mainTeacher);
        dest.writeLong(this.startTime);
        dest.writeString(this.id);
        dest.writeInt(this.viewCount);
        dest.writeString(this.title);
        dest.writeString(this.totalScore);
        dest.writeFloat(this.averageScore);
        dest.writeString(this.scoreType);
    }

    protected PrepareLessonsShortEntity(Parcel in) {
        this.subjectPic = in.readString();
        this.mainTeacher = in.readString();
        this.startTime = in.readLong();
        this.id = in.readString();
        this.viewCount = in.readInt();
        this.title = in.readString();
        this.totalScore = in.readString();
        this.averageScore = in.readFloat();
        this.scoreType = in.readString();
    }

    public static final Creator<PrepareLessonsShortEntity> CREATOR = new Creator<PrepareLessonsShortEntity>() {
        public PrepareLessonsShortEntity createFromParcel(Parcel source) {
            return new PrepareLessonsShortEntity(source);
        }

        public PrepareLessonsShortEntity[] newArray(int size) {
            return new PrepareLessonsShortEntity[size];
        }
    };
}
