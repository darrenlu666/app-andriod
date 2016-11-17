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
public class ExamTeacherReal extends ExamGrade {
    private String examArea;
    private String examSubject;
    private String examYear;
    private String totalCount;

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getExamArea() {
        return examArea;
    }

    public void setExamArea(String examArea) {
        this.examArea = examArea;
    }

    public String getExamSubject() {
        return examSubject;
    }

    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }

    public String getExamYear() {
        return examYear;
    }

    public void setExamYear(String examYear) {
        this.examYear = examYear;
    }

    public static List<ExamTeacherReal> getExamTeacherReal(JSONObject response) {
        List<ExamTeacherReal> list = new ArrayList<>();
        if ("error".equals(response.optString("result"))) {
            return list;
        }
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ExamTeacherReal entity = new ExamTeacherReal();
                entity.setExamSubject((jsonObject.isNull("subjectName") ? "" : jsonObject.getString("subjectName")));
                entity.setExamArea((jsonObject.isNull("areaName") ? "" : jsonObject.getString("areaName")));
                entity.setExamYear((jsonObject.isNull("year") ? "" : jsonObject.getString("year")));
                entity.setExamGrade((jsonObject.isNull("classlevelName") ? "" : jsonObject.getString("classlevelName")));
                entity.setExamId(jsonObject.isNull("examId") ? "" : jsonObject.getString("examId"));
                entity.setExamName(jsonObject.isNull("examName") ? "" : jsonObject.getString("examName"));
                entity.setTotalCount(jsonObject.isNull("totalCount")?"":jsonObject.getString("totalCount"));
                list.add(entity);
            }
        } catch (JSONException e) {
            list = new ArrayList<>();
        }
        return list;
    }
}
