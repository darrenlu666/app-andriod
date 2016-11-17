package com.codyy.erpsportal.homework.models.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 班级实体类
 * Created by ldh on 2015/12/29.
 */
public class ClassEntity {
    private String classRoomId;
    private String classRoomName;
    private String classlevelId;

    public String getClasslevelId() {
        return classlevelId;
    }

    public void setClasslevelId(String classlevelId) {
        this.classlevelId = classlevelId;
    }

    public String getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(String classRoomId) {
        this.classRoomId = classRoomId;
    }

    public String getClassRoomName() {
        return classRoomName;
    }

    public void setClassRoomName(String classRoomName) {
        this.classRoomName = classRoomName;
    }

    public static List<ClassEntity> parseClassList(JSONObject response) {
        List<ClassEntity> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ClassEntity classEntity = new ClassEntity();
                classEntity.setClassRoomId(jsonObject.isNull("classRoomId") ? "" : jsonObject.optString("classRoomId"));
                classEntity.setClassRoomName(jsonObject.isNull("classRoomName") ? "" : jsonObject.optString("classRoomName"));
                list.add(classEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<ClassEntity> parseExamClassList(JSONObject response) {
        List<ClassEntity> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ClassEntity classEntity = new ClassEntity();
                classEntity.setClassRoomId(jsonObject.isNull("classId") ? "" : jsonObject.optString("classId"));
                classEntity.setClasslevelId(jsonObject.isNull("classLevalId") ? "" : jsonObject.optString("classLevalId"));
                classEntity.setClassRoomName(jsonObject.isNull("className") ? "" : jsonObject.optString("className"));
                list.add(classEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
