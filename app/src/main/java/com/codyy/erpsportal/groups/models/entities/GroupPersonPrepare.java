package com.codyy.erpsportal.groups.models.entities;


import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

import java.io.Serializable;

/**
 * 圈组空间-个人备课
 * Created by poe on 16-2-2.
 *   {
 "groupLessonPlanId": "143edsarq34r23",
 "prepareLessonPlanId": "ab08b8c96d1e4a89a6c905b33ff10542",
 "title": "谁说文本大闹齐天大圣",
 "subjectName": "语文",
 "realName": "进来撒接欧文UR的萨芬就访客",
 "averageScore": 0,
 "headPic": "headPicDefault.jpg"
 }
 */
public class GroupPersonPrepare extends BaseTitleItemBar implements Serializable {

    private String groupLessonPlanId ;
    private String prepareLessonPlanId ;
    private String title;
    private String subjectName;//语文
    private String realName;
    private String averageScore;
    private String headPic;
    private String baseUserId;

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getGroupLessonPlanId() {
        return groupLessonPlanId;
    }

    public void setGroupLessonPlanId(String groupLessonPlanId) {
        this.groupLessonPlanId = groupLessonPlanId;
    }

    public String getPrepareLessonPlanId() {
        return prepareLessonPlanId;
    }

    public void setPrepareLessonPlanId(String prepareLessonPlanId) {
        this.prepareLessonPlanId = prepareLessonPlanId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(String averageScore) {
        this.averageScore = averageScore;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }
}
