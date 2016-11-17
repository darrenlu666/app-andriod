package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONObject;

/**
 * 首页（综合）、首页（资源）、网络教研配置页内部显示模块配置
 * Created by gujiajia on 2015/8/11.
 */
public class MainPageConfig {

    //首页综合
    /**
     * 资讯
     */
    private boolean information;

    /**
     * 优课资源
     */
    private boolean resource;

    /**
     * 专递课堂
     */
    private boolean onlineClass;

    /**
     * 名校网络课堂
     */
    private boolean liveClass;

    /**
     * 圈组
     */
    private boolean group;

    /**
     * 博文
     */
    private boolean blog;

    /**
     * 统计，暂无用。
     */
    private boolean statistic;

    //+网络教研
    /**
     * 集体备课
     */
    private boolean prepareLesson;

    /**
     * 互动听课
     */
    private boolean interactLesson;

    /**
     * 评课议课
     */
    private boolean evaluationMeeting;

    //-网络教研

    /**
     * 视频会议，暂无用。
     */
    private boolean videoMeeting;

    /**
     * 基础设置，暂无用。
     */
    private boolean basicInfo;

    public boolean hasInformation() {
        return information;
    }

    public void setInformation(boolean information) {
        this.information = information;
    }

    public boolean hasResource() {
        return resource;
    }

    public void setResource(boolean resource) {
        this.resource = resource;
    }

    public boolean hasGroup(){
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean hasBlog() {
        return blog;
    }

    public void setBlog(boolean blog) {
        this.blog = blog;
    }

    public boolean hasOnlineClass() {
        return onlineClass;
    }

    public void setOnlineClass(boolean onlineClass) {
        this.onlineClass = onlineClass;
    }

    public boolean hasLiveClass() {
        return liveClass;
    }

    public void setLiveClass(boolean liveClass) {
        this.liveClass = liveClass;
    }

    public boolean hasStatistic() {
        return statistic;
    }

    public void setStatistic(boolean statistic) {
        this.statistic = statistic;
    }

    public boolean hasPrepareLesson() {
        return prepareLesson;
    }

    public void setPrepareLesson(boolean prepareLesson) {
        this.prepareLesson = prepareLesson;
    }

    public boolean hasInteractLesson() {
        return interactLesson;
    }

    public void setInteractLesson(boolean interactLesson) {
        this.interactLesson = interactLesson;
    }

    public boolean hasEvaluationMeeting() {
        return evaluationMeeting;
    }

    public void setEvaluationMeeting(boolean evaluationMeeting) {
        this.evaluationMeeting = evaluationMeeting;
    }

    public boolean hasVideoMeeting() {
        return videoMeeting;
    }

    public void setVideoMeeting(boolean videoMeeting) {
        this.videoMeeting = videoMeeting;
    }

    public boolean hasBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(boolean basicInfo) {
        this.basicInfo = basicInfo;
    }

    public static MainPageConfig parseJson(JSONObject jsonObject) {
        MainPageConfig config = new MainPageConfig();
        config.setInformation("Y".equals(jsonObject.optString("informationid")));
        config.setOnlineClass("Y".equals(jsonObject.optString("onlineclassid")));
        config.setPrepareLesson("Y".equals(jsonObject.optString("preparelessionid")));
        config.setLiveClass("Y".equals(jsonObject.optString("liveclassid")));
        config.setStatistic("Y".equals(jsonObject.optString("statisticid")));
        config.setInteractLesson("Y".equals(jsonObject.optString("interactlessionid")));
        config.setEvaluationMeeting("Y".equals(jsonObject.optString("evaluationmeetingid")));
        config.setVideoMeeting("Y".equals(jsonObject.optString("videomeetingid")));
        config.setResource("Y".equals(jsonObject.optString("resourceid")));
        config.setGroup("Y".equals(jsonObject.optString("groupid")));
        config.setBlog("Y".equals(jsonObject.optString("blogid")));
        config.setBasicInfo("Y".equals(jsonObject.optString("basicInfoid")));
        return config;
    }
}
