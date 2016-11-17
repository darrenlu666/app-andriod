package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/8/29.
 */
public class SearchVideo extends SearchBase {

    /**
     * resourceId : ed019ddad65045d8b29aa7dbc8f12d19
     * resourceBrief : sssssssssssssssss
     * viewCnt : 6
     * createUserName : 习大大
     * ratingAvg : 7
     * createTimeStr : 20天前
     */
    private String resourceId;
    private String resourceBrief;
    private String viewCnt;
    private String createUserName;
    private String ratingAvg;
    private String createTimeStr;
    private String thumbPath;
    private String resourceName;

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setResourceBrief(String resourceBrief) {
        this.resourceBrief = resourceBrief;
    }

    public void setViewCnt(String viewCnt) {
        this.viewCnt = viewCnt;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public void setRatingAvg(String ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceBrief() {
        return resourceBrief;
    }

    public String getViewCnt() {
        return viewCnt;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public String getRatingAvg() {
        return ratingAvg;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     *
     */
    public static void getSearchVideo(JSONObject object, ArrayList<SearchBase> videos, int type, boolean flag) {
        JSONArray data = object.optJSONArray("data");
        if (flag) {
            SearchBase title = new SearchBase();
            title.setmType(RESOURCE_TITLE);
            title.setTotal(object.optInt("total"));
            videos.add(title);
        }
        for (int i = 0; i < data.length(); i++) {
            SearchVideo searchVideo = new SearchVideo();
            JSONObject video = data.optJSONObject(i);
            searchVideo.setmType(type);
            searchVideo.setResourceId(video.optString("resourceId"));
            searchVideo.setResourceBrief(video.optString("resourceBrief"));
            searchVideo.setCreateUserName(video.optString("createUserName"));
            searchVideo.setCreateTimeStr(video.optString("createTimeStr"));
            searchVideo.setViewCnt(video.optString("viewCnt"));
            searchVideo.setRatingAvg(video.optString("ratingAvg"));
            searchVideo.setThumbPath(video.optString("thumbPathSrc"));
            searchVideo.setResourceName(video.optString("resourceName"));
            videos.add(searchVideo);
        }
    }
}
