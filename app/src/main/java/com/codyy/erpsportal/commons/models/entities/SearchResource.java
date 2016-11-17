package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/8/28.
 */
public class SearchResource extends SearchBase {

    /**
     * publishTime : 2015-07-27 08:42
     * baseAreaId : 3268c4982fbd47b996de356d00d8adcc
     * createUserId : 02d13c173d7c4f2ab1e40c1fa60f39a0
     * infoType : ANNOUNCEMENT
     * attachment : bbddce7c-269d-446a-96c9-bf7780b990e1.jpg
     * thumb : null
     * informationId : bd67a45474f144c0af8daebfafa3efa5
     * createType : AREA
     * title : 杨永武，个
     * content : asdfa
     */
    private String publishTime;
    private String baseAreaId;
    private String createUserId;
    private String infoType;
    private String attachment;
    private String thumb;
    private String informationId;
    private String createType;
    private String title;
    private String content;

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public void setBaseAreaId(String baseAreaId) {
        this.baseAreaId = baseAreaId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getBaseAreaId() {
        return baseAreaId;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public String getInfoType() {
        return infoType;
    }

    public String getAttachment() {
        return attachment;
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

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    /**
     * @param object
     * @param resources
     */
    public static void getSearchResource(JSONObject object, ArrayList<SearchBase> resources, boolean hasHead) {
        JSONArray array = object.optJSONArray("data");
        if (hasHead) {
            SearchBase title = new SearchBase();
            title.setmType(SearchBase.RESOURCE_TITLE);
            title.setTotal(object.optInt("total"));
            resources.add(title);
        }
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.optJSONObject(i);
                SearchResource searchResource = new SearchResource();
                searchResource.setmType(SearchBase.RESOURCE_CONT);
                searchResource.setInformationId(jsonObject.optString("informationId"));
                searchResource.setTitle(jsonObject.optString("title"));
                searchResource.setInfoType(jsonObject.optString("infoType"));
                searchResource.setPublishTime(jsonObject.optString("publishTime"));
                searchResource.setThumb(jsonObject.optString("thumb"));
                searchResource.setCreateUserId(jsonObject.optString("createUserId"));
                searchResource.setBaseAreaId(jsonObject.optString("baseAreaId"));
                searchResource.setCreateType(jsonObject.optString("createType"));
                searchResource.setAttachment(jsonObject.optString("attachment"));
                searchResource.setContent(jsonObject.optString("content"));
                resources.add(searchResource);
            }
        }
    }
}
