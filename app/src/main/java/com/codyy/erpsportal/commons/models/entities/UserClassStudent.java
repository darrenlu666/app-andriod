package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/9/1.
 */
public class UserClassStudent extends UserClassBase {

    /**
     * groupId : wfz
     * studentName : student1
     * studentId : 452c18628694493584194be73b91de03
     * studentHeadPic : headPicDefault.jpg
     */

    private String groupId;
    private String studentName;
    private String studentId;
    private String studentHeadPic;

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentHeadPic(String studentHeadPic) {
        this.studentHeadPic = studentHeadPic;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentHeadPic() {
        return studentHeadPic;
    }

    /**
     * @param array
     * @param students
     */
    public static void getClassStudent(JSONArray array, ArrayList<UserClassBase> students) {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.optJSONObject(i);
            UserClassStudent student = new UserClassStudent();
            student.setGroupId(object.optString("groupId"));
            student.setType(TYPE_STUDENT);
            student.setStudentHeadPic(object.optString("studentHeadPic"));
            student.setStudentName(object.optString("studentName"));
            student.setStudentId(object.optString("studentId"));
            students.add(student);
        }
    }
}
