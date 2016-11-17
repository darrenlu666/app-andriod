package com.codyy.erpsportal.resource.models.entities;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 视频资源
 * Created by gujiajia on 2016/6/14.
 */
public class Video {

    private String id;

    private String name;

    private String thumbUrl;

    private int viewCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public static JsonParser<Video> sParser = new JsonParser<Video>() {
        @Override
        public Video parse(JSONObject jsonObject) {
            Video video = new Video();
            video.setId(jsonObject.optString("resourceId"));
            if (!jsonObject.isNull("thumbPathUrl")) {
                video.setThumbUrl(jsonObject.optString("thumbPathUrl").trim());
            }
            video.setName(jsonObject.optString("resourceName"));
            video.setViewCount(jsonObject.optInt("viewCnt"));
            return video;
        }
    };
}
