package com.codyy.erpsportal.exam.models.entities.school;

import com.codyy.erpsportal.exam.models.entities.ExamGrade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 学校-年级统考
 * Created by eachann on 2015/12/24.
 */
public class ExamSchoolGrade extends ExamGrade {
    /**
     * examId : ************
     * examName : 英语模拟试卷
     * examSubject : 英语
     * examGrade : 一年级
     * examType : 周测试
     */

    private String examSubject;

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    private String totalCount;//题量


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    private String startTime;


    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }

    public String getExamSubject() {
        return examSubject;
    }

    public static List<ExamSchoolGrade> getExamSchoolGrade(JSONObject response) {
        List<ExamSchoolGrade> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ExamSchoolGrade entity = new ExamSchoolGrade();
                entity.setExamGrade((jsonObject.isNull("classlevelName") ? "" : jsonObject.getString("classlevelName")));
                entity.setExamId(jsonObject.isNull("examId") ? "" : jsonObject.getString("examId"));
                entity.setExamName(jsonObject.isNull("examName") ? "" : jsonObject.getString("examName"));
                entity.setExamSubject((jsonObject.isNull("subjectName") ? "" : jsonObject.getString("subjectName")));
                entity.setExamType((jsonObject.isNull("examType") ? "" : jsonObject.getString("examType")));
                entity.setTotalCount(jsonObject.isNull("totalCount")?"":jsonObject.getString("totalCount"));
                entity.setStartTime(jsonObject.isNull("startTime")?"":jsonObject.getString("startTime"));
                list.add(entity);
            }
        } catch (JSONException e) {
            list = new ArrayList<>();
        }
        return list;
    }

}
