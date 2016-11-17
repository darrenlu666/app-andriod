package com.codyy.erpsportal.exam.models.entities;

/**
 * 试卷列表基类
 * Created by eachann on 2015/12/25.
 */
public class ExamGrade {
    /**
     * examId : ************
     * examName : 英语模拟试卷
     * examGrade : 一年级
     * examType : 周测试
     */

    private String examId;
    private String examName;
    private String examGrade;
    private String examType;

    public String getExamGrade() {
        return examGrade;
    }

    public void setExamGrade(String examGrade) {
        this.examGrade = examGrade;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }
}
