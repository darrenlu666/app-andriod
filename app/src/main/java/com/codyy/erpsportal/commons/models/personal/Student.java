package com.codyy.erpsportal.commons.models.personal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 家长的孩子信息
 * Created by ningfeng on 2015/8/10.
 */
public class Student {
    private String parentHeadpic;
    private String teacherName;
    private String teacherHeadpic;
    private String groupId;
    private String parentId;
    private String studentId;
    private String classId;
    private String className;
    private String parentName;
    private String teacherId;
    private String studentName;
    private String studentHeadPic;
    private String subjectName;
    /**
     * 用户学校id
     */
    private String schoolId;

    private String schoolName;
    private String classLevelName;// N年纪

    public String getClassLevelName() {
        return classLevelName;
    }

    public void setClassLevelName(String classLevelName) {
        this.classLevelName = classLevelName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setParentHeadpic(String parentHeadpic) {
        this.parentHeadpic = parentHeadpic;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setTeacherHeadpic(String teacherHeadpic) {
        this.teacherHeadpic = teacherHeadpic;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setStudentHeadPic(String studentHeadPic) {
        this.studentHeadPic = studentHeadPic;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getParentHeadpic() {
        return parentHeadpic;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getTeacherHeadpic() {
        return teacherHeadpic;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getClassName() {
        return className;
    }

    public String getParentId() {
        return parentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getClassId() {
        return classId;
    }

    public String getParentName() {
        return parentName;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentHeadPic() {
        return studentHeadPic;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public static List<Student> parseData(JSONObject response){
        List<Student> studentList = new ArrayList<>();

        JSONArray arry = response.optJSONArray("children");
        int len = arry.length();
        for (int i = 0; i < len; i++) {
            JSONObject studentJson = arry.optJSONObject(i);
            studentList.add(parseOneData(studentJson));
        }

        return  studentList;
    }

    public static Student parseOneData(JSONObject studentJson){
        if(null == studentJson) return  null;
        Student student = new Student();
        student.setTeacherId(studentJson.optString("teacherId"));
        student.setTeacherName(studentJson.optString("teacherName"));
        student.setTeacherHeadpic(studentJson.optString("teacherHeadpic"));
        student.setGroupId(studentJson.optString("groupId"));
        student.setStudentName(studentJson.optString("studentName"));
        student.setStudentId(studentJson.optString("studentId"));
        student.setStudentHeadPic(studentJson.optString("studentHeadPic"));
        student.setSubjectName(studentJson.optString("subjectName"));
        student.setClassId(studentJson.optString("classId"));
        student.setClassName(studentJson.optString("className"));
        student.setParentId(studentJson.optString("parentId"));
        student.setParentName(studentJson.optString("parentName"));
        student.setParentHeadpic(studentJson.optString("parentHeadpic"));
        student.setSchoolId(studentJson.optString("schoolId"));
        student.setSchoolName(studentJson.optString("schoolName"));
        student.setClassLevelName(studentJson.optString("classLevelName"));
        return  student;
    }

    @Override
    public String toString() {
        return studentId + "," + studentName + "," + schoolId;
    }
}
