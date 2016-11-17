package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kmdai on 2015/5/18.
 */
public class TeacherInfo {
    private String classLevelName;
    private String realName;
    private String baseUserId;
    private String subjectId;
    private String headPic;

    public String getClassLevelName() {
        return classLevelName;
    }

    public void setClassLevelName(String classLevelName) {
        this.classLevelName = classLevelName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public static void getTeacherInfo(JSONObject object, List<TeacherInfo> teacherInfos) {
        if ("success".equals(object.optString("result"))) {
            JSONArray jsonArray = object.optJSONArray("teachers");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                JSONArray jsonArray1 = jsonObject.optJSONArray("classLevelTeachers");
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject1 = jsonArray1.optJSONObject(j);
                    TeacherInfo teacherInfo = new TeacherInfo();
                    teacherInfo.setClassLevelName(jsonObject.optString("classLevelName"));
                    teacherInfo.setRealName(jsonObject1.optString("realName"));
                    teacherInfo.setHeadPic(jsonObject1.optString("headPic"));
                    teacherInfo.setBaseUserId(jsonObject1.optString("baseUserId"));
                    teacherInfo.setSubjectId(jsonObject1.optString("subjectId"));
                    teacherInfos.add(teacherInfo);
                }
            }
        }
    }

}
