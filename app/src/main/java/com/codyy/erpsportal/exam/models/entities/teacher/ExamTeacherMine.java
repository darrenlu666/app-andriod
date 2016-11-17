package com.codyy.erpsportal.exam.models.entities.teacher;

import com.codyy.erpsportal.exam.models.entities.ExamGrade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的试卷
 * Created by eachann on 2015/12/25.
 */
public class ExamTeacherMine extends ExamGrade {
    private String examUpdateTime;
    private String totalCount;//题量
    private String subject;//学科

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getExamUpdateTime() {
        return examUpdateTime;
    }

    public void setExamUpdateTime(String examUpdateTime) {
        this.examUpdateTime = examUpdateTime;
    }

    public static List<ExamTeacherMine> getExamTeacherMine(JSONObject response) {
        List<ExamTeacherMine> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ExamTeacherMine entity = new ExamTeacherMine();
                entity.setExamGrade((jsonObject.isNull("classlevelName") ? "" : jsonObject.getString("classlevelName")));
                entity.setExamId(jsonObject.isNull("examId") ? "" : jsonObject.getString("examId"));
                entity.setExamName(jsonObject.isNull("examName") ? "" : jsonObject.getString("examName"));
                entity.setExamUpdateTime((jsonObject.isNull("examUpdateTime") ? "" : jsonObject.getString("examUpdateTime")));
                entity.setExamType((jsonObject.isNull("examType") ? "" : jsonObject.getString("examType")));
                entity.setTotalCount(jsonObject.isNull("totalCount")?"":jsonObject.getString("totalCount"));
                entity.setSubject(jsonObject.isNull("subject")?"":jsonObject.getString("subject"));
                list.add(entity);
            }
        } catch (JSONException e) {
            list = new ArrayList<>();
        }
        return list;
    }
}
