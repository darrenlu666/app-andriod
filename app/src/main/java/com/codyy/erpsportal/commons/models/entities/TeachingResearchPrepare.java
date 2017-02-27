package com.codyy.erpsportal.commons.models.entities;

/**
 * Created by kmdai on 2015/8/10.
 */
public class TeachingResearchPrepare extends TeachingResearchBase {

    /**
     * mainTeacher : 张三
     * startTime : 2015-07-09
     * id : 9b5a97d0274b40b49ce72c016e1a14a8
     * viewCount : 3
     * title : ddddd
     * totalScore : null
     * averageScore : null
     */
    private String mainTeacher;
    private long startTime;
    private String id;
    private int viewCount;
    private String title;
    private String totalScore;
    private float averageScore;
    private String subjectPic;
    private String scoreType;

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public void setMainTeacher(String mainTeacher) {
        this.mainTeacher = mainTeacher;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public void setAverageScore(float averageScore) {
        this.averageScore = averageScore;
    }

    public String getMainTeacher() {
        return mainTeacher;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getId() {
        return id;
    }

    public int getViewCount() {
        return viewCount;
    }

    public String getTitle() {
        return title;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public float getAverageScore() {
        return averageScore;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }
}
