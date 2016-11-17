package com.codyy.erpsportal.exam.models.entities.student;

import com.codyy.erpsportal.exam.models.entities.ExamGrade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生-自主测试
 * Created by eachann on 2015/12/28.
 */
public class ExamStudentSelfTask extends ExamGrade {
    private String examTime;
    private String examSubject;
    private String examState;
    private String examTaskId;
    private String examResultId;
    private String totalCount;
    private String duration;

    public String getTotalCount() {

        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public String getExamState() {
        return examState;
    }

    public void setExamState(String examState) {
        this.examState = examState;
    }

    public String getExamSubject() {
        return examSubject;
    }

    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public static List<ExamStudentSelfTask> getExamStudentSelfTask(JSONObject response) {
        List<ExamStudentSelfTask> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ExamStudentSelfTask entity = new ExamStudentSelfTask();
                entity.setExamSubject(jsonObject.isNull("subjectName") ? "" : jsonObject.getString("subjectName"));
                entity.setExamId(jsonObject.isNull("examId") ? "" : jsonObject.getString("examId"));
                entity.setExamName(jsonObject.isNull("examName") ? "" : jsonObject.getString("examName"));
                entity.setExamTime((jsonObject.isNull("startTime") ? "-1" : jsonObject.getString("startTime")));
                entity.setExamState(jsonObject.isNull("examState") ? "" : jsonObject.getString("examState"));
                entity.setExamTaskId(jsonObject.isNull("examTaskId") ? "" : jsonObject.getString("examTaskId"));
                entity.setExamResultId(jsonObject.isNull("examResultId") ? "" : jsonObject.getString("examResultId"));
                entity.setTotalCount(jsonObject.isNull("totalCount") ? "" : jsonObject.getString("totalCount"));
                entity.setDuration(jsonObject.isNull("examTime") ? "" : jsonObject.getString("examTime"));
                list.add(entity);
            }
        } catch (JSONException e) {
            list = new ArrayList<>();
        }
        return list;
    }
}
