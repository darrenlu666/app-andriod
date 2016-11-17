package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/9/1.
 */
public class UserClassTeacher extends UserClassBase {

    /**
     * teacherId : 68b0728028cf4fb6892a804a710836fd
     * teacherName : 国家老师
     * teacherHeadpic : headPicDefault.jpg
     * subjectName : 数学
     */

    private String teacherId;
    private String teacherName;
    private String teacherHeadpic;
    private String subjectName;

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setTeacherHeadpic(String teacherHeadpic) {
        this.teacherHeadpic = teacherHeadpic;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getTeacherHeadpic() {
        return teacherHeadpic;
    }

    public String getSubjectName() {
        return subjectName;
    }

    /**
     * @param array
     * @param teachers
     */
    public static void getClassTeacher(JSONArray array, ArrayList<UserClassBase> teachers) {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.optJSONObject(i);
            UserClassTeacher teacher = new UserClassTeacher();
            teacher.setType(UserClassBase.TYPE_TEACHER);
            teacher.setSubjectName(object.optString("subjectName"));
            teacher.setTeacherId(object.optString("teacherId"));
            teacher.setTeacherName(object.optString("teacherName"));
            teacher.setTeacherHeadpic(object.optString("teacherHeadpic"));
            teachers.add(teacher);
        }
    }
}
