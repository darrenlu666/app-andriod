package com.codyy.erpsportal.exam.models.entities.student;

import com.codyy.erpsportal.exam.models.entities.school.ExamSchoolGrade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生-测试任务
 * Created by eachann on 2015/12/24.
 */
public class ExamStudentTask extends ExamSchoolGrade {

    private String examStartTime;
    private String examState;
    private boolean isUnified;
    private String practiceStatus;
    private String examPracticeId;
    private String examTaskId;
    private String examResultId;

    //add attribute by ldh
    private String totolCount;//题量

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTotolCount() {
        return totolCount;
    }

    public void setTotolCount(String totolCount) {
        this.totolCount = totolCount;
    }

    private String duration;//时长

    public static List<ExamStudentTask> getExamStudentTask(JSONObject response) {
        List<ExamStudentTask> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ExamStudentTask entity = new ExamStudentTask();
                entity.setExamSubject((jsonObject.isNull("subjectName") ? "" : jsonObject.optString("subjectName")));
                entity.setExamId(jsonObject.isNull("examId") ? "" : jsonObject.optString("examId"));
                entity.setExamTaskId(jsonObject.isNull("examTaskId") ? "" : jsonObject.optString("examTaskId"));
                entity.setExamName(jsonObject.isNull("examName") ? "" : jsonObject.optString("examName"));
                entity.setExamStartTime((jsonObject.isNull("examStartTime") ? "" : jsonObject.optString("examStartTime")));
                entity.setExamType((jsonObject.isNull("examTypeName") ? "" : jsonObject.optString("examTypeName")));
                entity.setUnified(jsonObject.optBoolean("isUnified"));
                entity.setExamState(jsonObject.isNull("examState") ? "" : jsonObject.optString("examState"));
                entity.setPracticeStatus(jsonObject.isNull("practiceStatus") ? "" : jsonObject.optString("practiceStatus"));
                entity.setExamPracticeId(jsonObject.isNull("examPracticeId") ? "" : jsonObject.optString("examPracticeId"));
                entity.setExamResultId(jsonObject.isNull("examResultId") ? "" : jsonObject.optString("examResultId"));
                entity.setDuration(jsonObject.isNull("examDuration") ? "" : "/" + jsonObject.optString("examDuration"));
                list.add(entity);
            }
        } catch (JSONException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static List<ExamStudentTask> getExamParentTask(JSONObject response) {
        List<ExamStudentTask> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ExamStudentTask entity = new ExamStudentTask();
                entity.setExamSubject((jsonObject.isNull("examSubjectName") ? "" : jsonObject.getString("examSubjectName")));
                entity.setExamId(jsonObject.isNull("examId") ? "" : jsonObject.getString("examId"));
                entity.setExamTaskId(jsonObject.isNull("examTaskId") ? "" : jsonObject.getString("examTaskId"));
                entity.setExamName(jsonObject.isNull("examName") ? "" : jsonObject.getString("examName"));
                entity.setExamStartTime((jsonObject.isNull("examStartTime") ? "" : jsonObject.getString("examStartTime")));
                entity.setExamType((jsonObject.isNull("examType") ? "" : jsonObject.getString("examType")));
                entity.setUnified(jsonObject.optBoolean("isUnified"));
                entity.setExamState(jsonObject.isNull("examState") ? "" : jsonObject.getString("examState"));
                entity.setPracticeStatus(jsonObject.isNull("practiceStatus") ? "" : jsonObject.getString("practiceStatus"));
                entity.setExamPracticeId(jsonObject.isNull("examPracticeId") ? "" : jsonObject.getString("examPracticeId"));
                entity.setExamResultId(jsonObject.isNull("examResultId") ? "" : jsonObject.getString("examResultId"));
                entity.setDuration(jsonObject.isNull("examDuration") ? "" : "/" + jsonObject.getString("examDuration"));
                entity.setTotolCount(jsonObject.isNull("totalCount") ? "" : jsonObject.getString("totalCount"));
                list.add(entity);
            }
        } catch (JSONException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    public String getExamResultId() {
        return examResultId;
    }

    public void setExamResultId(String examResultId) {
        this.examResultId = examResultId;
    }

    public String getExamTaskId() {
        return examTaskId;
    }

    public void setExamTaskId(String examTaskId) {
        this.examTaskId = examTaskId;
    }

    public String getExamPracticeId() {
        return examPracticeId;
    }

    public void setExamPracticeId(String examPracticeId) {
        this.examPracticeId = examPracticeId;
    }

    public String getPracticeStatus() {
        return practiceStatus;
    }

    public void setPracticeStatus(String practiceStatus) {
        this.practiceStatus = practiceStatus;
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
}
