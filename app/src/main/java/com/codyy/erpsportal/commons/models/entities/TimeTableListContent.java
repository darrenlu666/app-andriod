package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.view.View;

import com.codyy.erpsportal.timetable.activities.TimeTableDetailActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 2015/4/8.
 */
public class TimeTableListContent extends RefreshEntity {
    public final static int TYPE_ITEM = REFRESH_TYPE_LASTVIEW + 1;
    /**
     * clsSchoolId : 339a0f6f8a4d4d5280d1bedb223d25d0
     * headPic : http://10.1.70.15:8080/ResourceServer/images/headPicDefault.jpg
     * masterCount : 1
     * reciveCount : 0
     * schoolAreaPath : 中国
     * schoolName : lizhiwe
     */

    private String clsSchoolId;
    private String headPic;
    private int masterCount;
    private int reciveCount;
    private String schoolAreaPath;
    private String schoolName;

    /**
     * 获取课表列表
     *
     * @param jsonObject
     * @param contentList
     */
    public static List<TimeTableListContent> getTimeTableList(JSONObject jsonObject, List<TimeTableListContent> contentList) {
        if (contentList == null) {
            contentList = new ArrayList<>();
        }
        if (jsonObject.optBoolean("result", false)) {
            JSONArray jsonArray = jsonObject.optJSONArray("schooList");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                TimeTableListContent timeTableListContent = new TimeTableListContent();
                timeTableListContent.setClsSchoolId(jsonObject1.optString("clsSchoolId"));
                timeTableListContent.setHeadPic(jsonObject1.optString("headPic"));
                timeTableListContent.setSchoolName(jsonObject1.optString("schoolName"));
                timeTableListContent.setMasterCount(jsonObject1.optInt("masterCount", 0));
                timeTableListContent.setReciveCount(jsonObject1.optInt("reciveCount", 0));
                timeTableListContent.setSchoolAreaPath(jsonObject1.optString("schoolAreaPath"));
                timeTableListContent.setmHolderType(TYPE_ITEM);
                contentList.add(timeTableListContent);
            }
        }
        return contentList;
    }

    /**
     * 点击事件
     *
     * @param view
     * @param timeTableListContent
     */
    public void onClick(View view, TimeTableListContent timeTableListContent) {
        TimeTableDetailActivity.start(view.getContext(), timeTableListContent.getClsSchoolId(), timeTableListContent.getSchoolName());
    }

    public TimeTableListContent() {
    }

    public String getClsSchoolId() {
        return clsSchoolId;
    }

    public void setClsSchoolId(String clsSchoolId) {
        this.clsSchoolId = clsSchoolId;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public int getMasterCount() {
        return masterCount;
    }

    public void setMasterCount(int masterCount) {
        this.masterCount = masterCount;
    }

    public int getReciveCount() {
        return reciveCount;
    }

    public void setReciveCount(int reciveCount) {
        this.reciveCount = reciveCount;
    }

    public String getSchoolAreaPath() {
        return schoolAreaPath;
    }

    public void setSchoolAreaPath(String schoolAreaPath) {
        this.schoolAreaPath = schoolAreaPath;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.clsSchoolId);
        dest.writeString(this.headPic);
        dest.writeInt(this.masterCount);
        dest.writeInt(this.reciveCount);
        dest.writeString(this.schoolAreaPath);
        dest.writeString(this.schoolName);
    }

    protected TimeTableListContent(Parcel in) {
        super(in);
        this.clsSchoolId = in.readString();
        this.headPic = in.readString();
        this.masterCount = in.readInt();
        this.reciveCount = in.readInt();
        this.schoolAreaPath = in.readString();
        this.schoolName = in.readString();
    }

    public static final Creator<TimeTableListContent> CREATOR = new Creator<TimeTableListContent>() {
        @Override
        public TimeTableListContent createFromParcel(Parcel source) {
            return new TimeTableListContent(source);
        }

        @Override
        public TimeTableListContent[] newArray(int size) {
            return new TimeTableListContent[size];
        }
    };
}
