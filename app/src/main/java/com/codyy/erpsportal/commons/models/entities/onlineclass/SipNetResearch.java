package com.codyy.erpsportal.commons.models.entities.onlineclass;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 网络教研（ｓｉｐ苏州园区首页）
 * Created by poe on 21/07/17.
 */

public class SipNetResearch extends BaseTitleItemBar {

    /**
     * averageScore : 0
     * classLevelName :
     * description : 初二数学备课
     * id : 6a2c7bde705b4d00aeb85118b3f2fd54
     * mainTeacher : 张老师
     * scoreType :
     * startTime : 1441614979624
     * subjectName :
     * subjectPic : 431d262d-0377-4d4e-8ae9-c72258784fd6.jpg
     * title : 初二数学备课
     * totalScore :
     * viewCount : 10
     */

    private float averageScore;
    private String classLevelName;
    private String description;
    private String id;
    private String mainTeacher;
    private String scoreType;
    private long startTime;
    private String subjectName;
    private String subjectPic;
    private String title;
    private String totalScore;
    private int viewCount;

    public float getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(float averageScore) {
        this.averageScore = averageScore;
    }

    public String getClassLevelName() {
        return classLevelName;
    }

    public void setClassLevelName(String classLevelName) {
        this.classLevelName = classLevelName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainTeacher() {
        return mainTeacher;
    }

    public void setMainTeacher(String mainTeacher) {
        this.mainTeacher = mainTeacher;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
