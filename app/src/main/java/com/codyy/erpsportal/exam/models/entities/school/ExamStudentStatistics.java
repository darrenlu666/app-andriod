package com.codyy.erpsportal.exam.models.entities.school;

/**
 * 学生统计
 * Created by eachann on 2016/1/15.
 */
public class ExamStudentStatistics {

    /**
     * examTaskId : 11111111111
     * headUrl : http://****
     * name : 李
     * score : 87分
     * correctRate : 80.5%
     * answeredCounts : 16/16
     */

    private String examResultId;
    private String baseUserId;
    private String headUrl;
    private String baseUserName;
    private String score;
    private String rightRate;
    private String answerCount;
    private String totalCount;

    public String getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(String answerCount) {
        this.answerCount = answerCount;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getBaseUserName() {
        return baseUserName;
    }

    public void setBaseUserName(String baseUserName) {
        this.baseUserName = baseUserName;
    }

    public String getExamResultId() {
        return examResultId;
    }

    public void setExamResultId(String examResultId) {
        this.examResultId = examResultId;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getRightRate() {
        return rightRate;
    }

    public void setRightRate(String rightRate) {
        this.rightRate = rightRate;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }
}
