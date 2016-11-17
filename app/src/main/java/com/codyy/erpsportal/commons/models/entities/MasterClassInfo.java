package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 教室详情
 * Created by kmdai on 2015/4/16.
 */
public class MasterClassInfo {
    private String classLevelName;
    private String subjectName;
    private String status;
    private String schoolName;
    private String realName;
    private String contactPhone;
    private List<ReceiveClassInfo> receiveClassInfos;
    private String[] students;

    public List<ReceiveClassInfo> getReceiveClassInfos() {
        return receiveClassInfos;
    }

    public void setReceiveClassInfos(List<ReceiveClassInfo> receiveClassInfos) {
        this.receiveClassInfos = receiveClassInfos;
    }

    public String getClassLevelName() {
        return classLevelName;
    }

    public void setClassLevelName(String classLevelName) {
        this.classLevelName = classLevelName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String[] getStudents() {
        return students;
    }

    public void setStudents(String[] students) {
        this.students = students;
    }

    /**
     * 获取主讲教室的信息
     *
     * @param object
     * @param masterClassInfo
     */
    public static void getMasterClass(JSONObject object, MasterClassInfo masterClassInfo) {
        if ("success".equals(object.optString("result"))) {
            JSONObject object1 = object.optJSONObject("masterClassroom");
            if (object1 != null) {
                masterClassInfo.setClassLevelName(object1.optString("classLevelName"));
                masterClassInfo.setSubjectName(object1.optString("subjectName"));
                masterClassInfo.setStatus(object1.optString("status"));
                masterClassInfo.setSchoolName(object1.optString("schoolName"));
                masterClassInfo.setRealName(object1.optString("realName"));
                masterClassInfo.setContactPhone(object1.optString("contactPhone"));
                JSONArray jsonArray = object.optJSONArray("receiveClassroom");
                List<ReceiveClassInfo> receiveClassInfos = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    ReceiveClassInfo receiveClassInfo = new ReceiveClassInfo();
                    receiveClassInfo.setSchoolName(jsonObject.optString("schoolName"));
                    receiveClassInfo.setStudentNum(jsonObject.optString("studentNum"));
                    receiveClassInfo.setRealName(jsonObject.optString("realName"));
                    receiveClassInfo.setContactPhone(jsonObject.optString("contactPhone"));
                    receiveClassInfos.add(receiveClassInfo);
                }
                masterClassInfo.setReceiveClassInfos(receiveClassInfos);
                JSONArray student = object.optJSONArray("selfSchoolStudent");
                if (student != null) {
                    String s[] = new String[student.length()];
                    for (int i = 0; i < s.length; i++) {
                        s[i] = student.optString(i);
                    }
                    masterClassInfo.setStudents(s);
                }
            }
        }
    }
}
