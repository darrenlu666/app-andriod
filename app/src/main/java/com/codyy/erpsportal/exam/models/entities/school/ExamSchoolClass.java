package com.codyy.erpsportal.exam.models.entities.school;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 学校-班级测试
 * Created by eachann on 2015/12/24.
 */
public class ExamSchoolClass extends ExamSchoolGrade {
    private String examStartTime;
    private String examState;
    private boolean isUnified;
    private String examTaskId;
    private String classSpaceType;

    public String getClassSpaceType() {
        return classSpaceType;
    }

    public void setClassSpaceType(String classSpaceType) {
        this.classSpaceType = classSpaceType;
    }

    public String getExamTaskId() {
        return examTaskId;
    }

    public void setExamTaskId(String examTaskId) {
        this.examTaskId = examTaskId;
    }

    public boolean isUnified() {
        return isUnified;
    }

    public void setUnified(boolean unified) {
        isUnified = unified;
    }

    public String getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime(String examStartTime) {
        this.examStartTime = examStartTime;
    }

    public String getExamState() {
        return examState;
    }

    public void setExamState(String examState) {
        this.examState = examState;
    }

    public static List<ExamSchoolClass> getExamSchoolClass(JSONObject response) {
        List<ExamSchoolClass> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                ExamSchoolClass entity = new ExamSchoolClass();
                entity.setExamGrade((obj.isNull("classlevelName") ? "" : obj.getString("classlevelName")));
                entity.setExamId(obj.isNull("examId") ? "" : obj.getString("examId"));
                entity.setExamName(obj.isNull("examName") ? "" : obj.getString("examName"));
                entity.setExamTaskId(obj.isNull("examTaskId") ? "" : obj.getString("examTaskId"));
                entity.setExamSubject(obj.isNull("subjectName") ? "" : obj.getString("subjectName"));
                entity.setClassSpaceType(obj.isNull("examTypeName")?"":obj.getString("examTypeName"));
                switch (obj.isNull("examType") ? "" : obj.getString("examType")) {
                    case "CLASSLEVEL":
                        entity.setExamType("年级统考");
                        break;
                    case "CLASS":
                        entity.setExamType("班级测试");
                        break;
                    case "REAL":
                        entity.setExamType("真题试卷");
                        break;
                    case "CONSOLIDATION":
                        entity.setExamType("巩固测试");
                        break;
                    case "SELF":
                        entity.setExamType("自主测试");
                        break;
                }
                entity.setExamState(obj.isNull("examState") ? "" : obj.getString("examState"));
                entity.setExamStartTime(obj.isNull("examStartTime") ? "" : obj.getString("examStartTime"));
                if (!obj.isNull("isUnified")) {
                    entity.setUnified(obj.optBoolean("isUnified"));
                }
                list.add(entity);
            }
        } catch (JSONException e) {
            list = new ArrayList<>();
        }
        return list;
    }
}
