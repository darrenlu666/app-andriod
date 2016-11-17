package com.codyy.erpsportal.resource.models.entities;


import com.codyy.erpsportal.commons.models.parsers.JsonParsable;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * 资源评论
 * Created by gujiajia on 2015/4/17.
 */
public class ResourceComment {

    private String id;

    private String baseUserId;

    private String userType;

    private String userName;

    private String photoUrl;

    private String time;

    private String content;

    private String formattedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public static ResourceComment parseJson(JSONObject jsonObject) {
        return JSON_PARSER.parse(jsonObject);
    }

    public static List<ResourceComment> parseJsonArray(JSONArray jsonArray) {
        return JSON_PARSER.parseArray(jsonArray);
    }

    public final static JsonParsable<ResourceComment> JSON_PARSER = new JsonParser<ResourceComment>() {
        @Override
        public ResourceComment parse(JSONObject jsonObject) {
            ResourceComment resourceComment = new ResourceComment();
            resourceComment.setId(jsonObject.optString("id"));
            resourceComment.setBaseUserId(optStrOrNull(jsonObject, "commentUserId"));
            resourceComment.setUserType(optStrOrNull(jsonObject, "userType"));
            resourceComment.setUserName(StringUtils.replaceHtml(jsonObject.optString("userName")));
            resourceComment.setContent(StringUtils.replaceHtml(jsonObject.optString("comments")));
            resourceComment.setPhotoUrl(jsonObject.optString("headPic"));
            resourceComment.setTime(jsonObject.optString("createTime"));
            if ( !jsonObject.isNull("formattedTime")) {
                resourceComment.setFormattedTime( jsonObject.optString("formattedTime"));
            }
            return resourceComment;
        }
    };
}
