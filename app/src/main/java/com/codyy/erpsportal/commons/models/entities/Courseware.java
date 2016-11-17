package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 教案
 * Created by gujiajia on 2016/1/19.
 */
public class Courseware {

    public final static String TYPE_DOCUMENT = "DOC";

    public final static String TYPE_VIDEO = "VIDEO";

    private String resourceId;

    private String serverAddress;

    private String serverResourceId;

    private String type;

    private String name;

    private String transFlag;

    //下载进度，-1为未开始，100为完成
    private int progress = -1;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerResourceId() {
        return serverResourceId;
    }

    public void setServerResourceId(String serverResourceId) {
        this.serverResourceId = serverResourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransFlag() {
        return transFlag;
    }

    public void setTransFlag(String transFlag) {
        this.transFlag = transFlag;
    }

    public boolean isTransformFailed() {
        return "TRANS_FAILED".equals(transFlag);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public final static JsonParser<Courseware> JSON_PARSER = new JsonParser<Courseware>() {
        @Override
        public Courseware parse(JSONObject jsonObject) {
            Courseware courseware = new Courseware();
            courseware.setResourceId(jsonObject.optString("resourceId"));
            courseware.setName(jsonObject.optString("name"));
            courseware.setServerAddress(jsonObject.optString("serverAddress"));
            courseware.setServerResourceId(jsonObject.optString("serverResourceId"));
            courseware.setType(jsonObject.optString("type"));
            return courseware;
        }
    };
}
