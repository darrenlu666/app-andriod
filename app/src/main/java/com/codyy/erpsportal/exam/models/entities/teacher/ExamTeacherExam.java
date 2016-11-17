package com.codyy.erpsportal.exam.models.entities.teacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 教师-测试任务
 * Created by eachann on 2015/12/28.
 */
public class ExamTeacherExam extends ExamTeacherMine {
    private boolean isUnified;
    private String startTime;
    private String examTaskId;
    private String classlevelId;
    private String examCategoryType;
    private String status;

    public static List<ExamTeacherExam> getExamTeacherExam(JSONObject response) {
        List<ExamTeacherExam> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ExamTeacherExam entity = new ExamTeacherExam();
                entity.setExamGrade((jsonObject.isNull("classlevelName") ? "" : jsonObject.getString("classlevelName")));
                entity.setExamId(jsonObject.isNull("examId") ? "" : jsonObject.getString("examId"));
                entity.setExamName(jsonObject.isNull("examName") ? "" : jsonObject.getString("examName"));
                entity.setStartTime((jsonObject.isNull("startTime") ? "" : jsonObject.getString("startTime")));
                entity.setExamType((jsonObject.isNull("examType") ? "" : jsonObject.getString("examType")));
                entity.setUnified(!jsonObject.isNull("examCategoryType") && jsonObject.getString("examCategoryType").equals("CLASSLEVEL"));
                entity.setStatus(jsonObject.isNull("status") ? "0" : jsonObject.getString("status"));
                entity.setExamTaskId(jsonObject.isNull("examTaskId") ? "" : jsonObject.getString("examTaskId"));
                entity.setClasslevelId(jsonObject.isNull("classlevelId") ? "" : jsonObject.getString("classlevelId"));
                list.add(entity);
            }
        } catch (JSONException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    public String getExamCategoryType() {
        return examCategoryType;
    }

    public void setExamCategoryType(String examCategoryType) {
        this.examCategoryType = examCategoryType;
    }

    public String getExamTaskId() {
        return examTaskId;
    }

    public void setExamTaskId(String examTaskId) {
        this.examTaskId = examTaskId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isUnified() {
        return isUnified;
    }

    public void setUnified(boolean unified) {
        isUnified = unified;
    }

    public String getClasslevelId() {
        return classlevelId;
    }

    public void setClasslevelId(String classlevelId) {
        this.classlevelId = classlevelId;
    }
}
