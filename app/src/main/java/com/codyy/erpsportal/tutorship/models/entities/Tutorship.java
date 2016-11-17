package com.codyy.erpsportal.tutorship.models.entities;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 辅导实体类
 * Created by gujiajia on 2015/12/18.
 */
public class Tutorship {

    /**
     * 未开始状态
     */
    public final static String STATUS_INIT = "INIT";

    /**
     * 进行中状态
     */
    public final static String STATUS_PROGRESS = "PROGRESS";

    /**
     * 已结束状态
     */
    public final static String STATUS_END = "END";

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 主讲人名
     */
    private String speakerName;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 学科名称
     */
    private String subjectName;

    /**
     * 状态
     */
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public static List<Tutorship> parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) return null;
        List<Tutorship> tutorshipList = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            Tutorship tutorship = new Tutorship();
            tutorship.setId("ddd");
            tutorship.setSpeakerName("菜菜");
            tutorship.setTitle("菜菜教你虐菜打菜之" + i);
            tutorship.setStartTime("9835-6-31 98:99");
            tutorship.setStatus(STATUS_INIT);
            tutorshipList.add(tutorship);
        }
        return tutorshipList;
    }
}
