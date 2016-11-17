package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yangxinwu on 2015/8/18.
 */
public class ThemeVideo {
    private String storeServer;
    private String filePath;
    private String id;
    private String videoPath;
    private String downloadUrl;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setStoreServer(String storeServer) {
        this.storeServer = storeServer;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreServer() {
        return storeServer;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getId() {
        return id;
    }

    public static ThemeVideo parseJson(JSONObject jsonObject) {

        ThemeVideo themeVideo = new ThemeVideo();
        themeVideo.setId(jsonObject.optString("id"));
        themeVideo.setFilePath(jsonObject.optString("filePath"));
        themeVideo.setStoreServer(jsonObject.optString("storeServer"));
        themeVideo.setVideoPath(jsonObject.optString("videoPath"));
        themeVideo.setDownloadUrl(jsonObject.optString("downloadUrl"));
        return themeVideo;

    }


    public static ArrayList<ThemeVideo> parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        ArrayList<ThemeVideo> videoList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            ThemeVideo video = parseJson(jsonObject);
            videoList.add(video);
        }
        return videoList;
    }

    /**
     * 专递课堂，课程回放
     *
     * @param jsonArray
     * @return
     */
    public static ArrayList<ThemeVideo> parseJsonArray1(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        ArrayList<ThemeVideo> videoList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            ThemeVideo themeVideo = new ThemeVideo();
            themeVideo.setId(jsonObject.optString("id"));
            themeVideo.setStoreServer(jsonObject.optString("sort"));
            themeVideo.setFilePath(jsonObject.optString("filePath"));
            themeVideo.setDownloadUrl(jsonObject.optString("downloadUrl"));
            videoList.add(themeVideo);
        }
        return videoList;
    }
}
