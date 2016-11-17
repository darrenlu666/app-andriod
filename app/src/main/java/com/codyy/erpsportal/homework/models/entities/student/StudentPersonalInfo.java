package com.codyy.erpsportal.homework.models.entities.student;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生个人信息实体类
 * Created by ldh on 2016/1/26.
 */
public class StudentPersonalInfo implements Parcelable {

    /**
     * headPicStr : /headPic/pic.png
     * studentId : **************
     * studentName : 张三
     */

    private String headPicStr;
    private String studentId;
    private String studentName;
    private String examResultId;

    public String getExamResultId() {
        return examResultId;
    }

    public void setExamResultId(String examResultId) {
        this.examResultId = examResultId;
    }

    public void setHeadPicStr(String headPicStr) {
        this.headPicStr = headPicStr;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getHeadPicStr() {
        return headPicStr;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public static List<StudentPersonalInfo> parseStudentResponse(JSONObject response) {
        List<StudentPersonalInfo> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                StudentPersonalInfo studentPersonalInfo = new StudentPersonalInfo();
                studentPersonalInfo.setStudentId(jsonObject.isNull("studentId") ? "" : jsonObject.optString("studentId"));
                studentPersonalInfo.setStudentName(jsonObject.isNull("studentName") ? "" : jsonObject.optString("studentName"));
                studentPersonalInfo.setHeadPicStr(jsonObject.isNull("headPicStr") ? "" : jsonObject.optString("headPicStr"));
                list.add(studentPersonalInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;
    }

    public static List<StudentPersonalInfo> parseExamStudentResponse(JSONObject response) {
        List<StudentPersonalInfo> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                StudentPersonalInfo studentPersonalInfo = new StudentPersonalInfo();
                studentPersonalInfo.setStudentId(jsonObject.isNull("baseUserId") ? "" : jsonObject.optString("baseUserId"));
                studentPersonalInfo.setExamResultId(jsonObject.isNull("examResultId") ? "" : jsonObject.optString("examResultId"));
                studentPersonalInfo.setStudentName(jsonObject.isNull("realName") ? "" : jsonObject.optString("realName"));
                studentPersonalInfo.setHeadPicStr(jsonObject.isNull("headPic") ? "" : jsonObject.optString("headPic"));
                list.add(studentPersonalInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.headPicStr);
        dest.writeString(this.studentId);
        dest.writeString(this.studentName);
        dest.writeString(this.examResultId);
    }

    public StudentPersonalInfo() {
    }

    protected StudentPersonalInfo(Parcel in) {
        this.headPicStr = in.readString();
        this.studentId = in.readString();
        this.studentName = in.readString();
        this.examResultId = in.readString();
    }

    public static final Parcelable.Creator<StudentPersonalInfo> CREATOR = new Parcelable.Creator<StudentPersonalInfo>() {
        public StudentPersonalInfo createFromParcel(Parcel source) {
            return new StudentPersonalInfo(source);
        }

        public StudentPersonalInfo[] newArray(int size) {
            return new StudentPersonalInfo[size];
        }
    };
}
