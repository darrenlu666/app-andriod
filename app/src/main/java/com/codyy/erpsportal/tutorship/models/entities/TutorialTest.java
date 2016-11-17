package com.codyy.erpsportal.tutorship.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 辅导随堂测验
 * Created by gujiajia on 2015/12/24.
 */
public class TutorialTest implements Parcelable {

    private String id;
    private String title;
    private String subjectIcon;
    private String gradeName;
    private int duration;
    private String createTime;

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

    public String getSubjectIcon() {
        return subjectIcon;
    }

    public void setSubjectIcon(String subjectIcon) {
        this.subjectIcon = subjectIcon;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public static List<TutorialTest> parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) return null;
        List<TutorialTest> tutorialList = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            TutorialTest tutorialTest = new TutorialTest();
            tutorialTest.setId("ddd");
            tutorialTest.setSubjectIcon("");
            tutorialTest.setTitle("菜菜教你虐菜打菜之" + i);
            tutorialTest.setGradeName("一年级");
            tutorialTest.setDuration(6);
            tutorialTest.setCreateTime("2015-7-3 10:00");
            tutorialList.add(tutorialTest);
        }
        return tutorialList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.subjectIcon);
        dest.writeString(this.gradeName);
        dest.writeInt(this.duration);
        dest.writeString(this.createTime);
    }

    public TutorialTest() {
    }

    protected TutorialTest(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.subjectIcon = in.readString();
        this.gradeName = in.readString();
        this.duration = in.readInt();
        this.createTime = in.readString();
    }

    public static final Creator<TutorialTest> CREATOR = new Creator<TutorialTest>() {
        public TutorialTest createFromParcel(Parcel source) {
            return new TutorialTest(source);
        }

        public TutorialTest[] newArray(int size) {
            return new TutorialTest[size];
        }
    };
}
