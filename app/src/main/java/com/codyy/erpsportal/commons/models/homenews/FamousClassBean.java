package com.codyy.erpsportal.commons.models.homenews;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codyy on 2015/8/7.
 */
public class FamousClassBean implements Parcelable {

    /**
     * date : 2015-08-06
     * classId : aee4675675824495b846e4a64700462a
     * teacherName : 王琦_/%琦2
     * subject : 数学
     * classroomId : dcd54064f866447faddc092a4ea29b47
     * schoolName : 园区%一中
     * order : 六
     */
    private String date;
    private String classId;
    private String teacherName;
    private String subject;
    private String classroomId;
    private String schoolName;
    private String order;
    private String subjectPic;
    private String baseResourceServerId;
    private String serverAddress;
    private String workCount;
    private String grade;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getWorkCount() {
        return workCount;
    }

    public void setWorkCount(String workCount) {
        this.workCount = workCount;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getBaseResourceServerId() {
        return baseResourceServerId;
    }

    public void setBaseResourceServerId(String baseResourceServerId) {
        this.baseResourceServerId = baseResourceServerId;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDate() {
        return date;
    }

    public String getClassId() {
        return classId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getSubject() {
        return subject;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getOrder() {
        return order;
    }

    public FamousClassBean() {
    }


    public static List<FamousClassBean> parseResponse(JSONObject response) {
        List<FamousClassBean> mFamousClassBeans = new ArrayList<>();
        if ("success".equals(response.optString("result"))) {
            JSONArray news = response.optJSONArray("list");
            int len = news.length();
            for (int i = 0; i < len; i++) {
                FamousClassBean bean = new FamousClassBean();
                JSONObject jsonObjcet = news.optJSONObject(i);
                bean.setClassId(jsonObjcet.optString("classId"));
                bean.setClassroomId(jsonObjcet.optString("clsClassroomId"));
                bean.setDate(jsonObjcet.optString("realBeginTime"));
                bean.setSubjectPic(jsonObjcet.optString("picUrl"));
                bean.setSchoolName(jsonObjcet.optString("schoolName"));
                bean.setSubject(jsonObjcet.optString("subjectName"));
                bean.setTeacherName(jsonObjcet.optString("speakerUserName"));
                bean.setBaseResourceServerId(jsonObjcet.optString("baseResourceServerId"));
                bean.setServerAddress(jsonObjcet.optString("serverAddress"));
                bean.setGrade(jsonObjcet.isNull("classlevelName") ? "" : jsonObjcet.optString("classlevelName"));
                bean.setWorkCount(jsonObjcet.isNull("classWorkCount") ? "" : jsonObjcet.optString("classWorkCount"));
                mFamousClassBeans.add(bean);
            }
        }
        return mFamousClassBeans;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.classId);
        dest.writeString(this.teacherName);
        dest.writeString(this.subject);
        dest.writeString(this.classroomId);
        dest.writeString(this.schoolName);
        dest.writeString(this.order);
        dest.writeString(this.subjectPic);
        dest.writeString(this.baseResourceServerId);
        dest.writeString(this.serverAddress);
        dest.writeString(this.grade);
        dest.writeString(this.workCount);
    }

    protected FamousClassBean(Parcel in) {
        this.date = in.readString();
        this.classId = in.readString();
        this.teacherName = in.readString();
        this.subject = in.readString();
        this.classroomId = in.readString();
        this.schoolName = in.readString();
        this.order = in.readString();
        this.subjectPic = in.readString();
        this.baseResourceServerId = in.readString();
        this.serverAddress = in.readString();
        this.grade = in.readString();
        this.workCount = in.readString();
    }

    public static final Creator<FamousClassBean> CREATOR = new Creator<FamousClassBean>() {
        public FamousClassBean createFromParcel(Parcel source) {
            return new FamousClassBean(source);
        }

        public FamousClassBean[] newArray(int size) {
            return new FamousClassBean[size];
        }
    };
}
