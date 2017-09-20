package com.codyy.erpsportal.commons.models.entities.customized;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 学校排名－苏州首页－专递课堂(sip)
 * Created by poe on 24/07/17.
 */

public class SchoolRank extends BaseTitleItemBar{
    /**
     * scheduleActivityCount : 47
     * schoolId : 8598d1316d6747b8948100c5661c65b8
     * schoolName : 琦琦学校
     * teacherActivityCount : 7
     */
    private int rankPosition = 0;//排名
    private String schoolId;
    private String schoolName;
    private String scheduleActivityCount;//开课活动数
    private String teacherActivityCount;//教研活动数

    public int getRankPosition() {
        return rankPosition;
    }

    public void setRankPosition(int rankPosition) {
        this.rankPosition = rankPosition;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getScheduleActivityCount() {
        return scheduleActivityCount;
    }

    public void setScheduleActivityCount(String scheduleActivityCount) {
        this.scheduleActivityCount = scheduleActivityCount;
    }

    public String getTeacherActivityCount() {
        return teacherActivityCount;
    }

    public void setTeacherActivityCount(String teacherActivityCount) {
        this.teacherActivityCount = teacherActivityCount;
    }
}
