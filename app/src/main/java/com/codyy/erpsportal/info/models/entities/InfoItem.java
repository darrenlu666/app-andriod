package com.codyy.erpsportal.info.models.entities;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ningfeng on 2015/7/30.
 */
public class InfoItem {


    /**
     * publishTime : 2015-08-08 16:27
     * createUserId : null
     * clsSchoolId : null
     * thumb : 11bc385b-5d89-400e-8845-61bfa5f65ea6.jpg
     * informationId : f40d47b82163444497b3b4d47402e5a1
     * createType : null
     * richContent : <p>是对方付费芳芳芳芳芳芳芳芳翻方<br/></p>
     * title : 是对方是否
     * baseClassId : null
     * content : 是对方付费芳芳芳芳芳芳芳芳翻方
     * originalName : null
     * baseAreaId : null
     * infoType : NEWS
     * attachment : null
     * sendPublishName : 黑龙江小学
     * createTime : 1439022417631
     */
    private String publishTime;
    private String createUserId;
    private String clsSchoolId;
    private String thumb;
    private String informationId;
    private String createType;
    private String richContent;
    private String title;
    private String baseClassId;
    private String content;
    private String originalName;
    private String baseAreaId;
    private String infoType;
    private String attachment;
    private String sendPublishName;
    private String createTime;

    public static List<InfoItem> parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        List<InfoItem> resources = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            resources.add(parseJson(jsonObject));
        }
        return resources;
    }

    @NonNull
    public static InfoItem parseJson(JSONObject obj) {
        InfoItem infoItem = new InfoItem();
        infoItem.setInformationId(obj.optString("informationId"));
        infoItem.setTitle(obj.optString("title"));
        infoItem.setInfoType(obj.optString("infoType"));
        infoItem.setPublishTime(obj.optString("publishTime"));
        if (!obj.isNull("thumb")) {
            infoItem.setThumb(obj.optString("thumb"));
        }
        infoItem.setCreateUserId(obj.optString("createUserId"));
        infoItem.setBaseAreaId(obj.optString("baseAreaId"));
        infoItem.setClsSchoolId(obj.optString("clsSchoolId"));
        infoItem.setBaseClassId(obj.optString("baseClassId"));
        infoItem.setCreateType(obj.optString("createType"));
        infoItem.setCreateTime(obj.optString("createTime"));

        infoItem.setAttachment(obj.optString("attachment"));
        if (!obj.isNull("content")) {
            infoItem.setContent(obj.optString("content"));
        }
        infoItem.setRichContent(obj.optString("richContent"));
        infoItem.setOriginalName(obj.optString("originalName"));
        infoItem.setSendPublishName(obj.optString("sendPublishName"));
        return infoItem;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    private boolean isSelected;

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public void setClsSchoolId(String clsSchoolId) {
        this.clsSchoolId = clsSchoolId;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setInformationId(String informationId) {
        this.informationId = informationId;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

    public void setRichContent(String richContent) {
        this.richContent = richContent;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBaseClassId(String baseClassId) {
        this.baseClassId = baseClassId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setBaseAreaId(String baseAreaId) {
        this.baseAreaId = baseAreaId;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public void setSendPublishName(String sendPublishName) {
        this.sendPublishName = sendPublishName;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public String getClsSchoolId() {
        return clsSchoolId;
    }

    public String getThumb() {
        return thumb;
    }

    public String getInformationId() {
        return informationId;
    }

    public String getCreateType() {
        return createType;
    }

    public String getRichContent() {
        return richContent;
    }

    public String getTitle() {
        return title;
    }

    public String getBaseClassId() {
        return baseClassId;
    }

    public String getContent() {
        return content;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getBaseAreaId() {
        return baseAreaId;
    }

    public String getInfoType() {
        return infoType;
    }

    public String getAttachment() {
        return attachment;
    }

    public String getSendPublishName() {
        return sendPublishName;
    }

    public String getCreateTime() {
        return createTime;
    }
}
