package com.codyy.erpsportal.commons.models.entities;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示模式-文档信息
 * Created by poe on 15-9-7.
 */
public class MeetingShow {
    /**
     * 演示文件标题
     */
    private String showTitle;
    /**
     * 演示资源id
     */
    private String showResID;
    /**
     * 当前是否处于演示状态 1：不是当前 2：正在演示
     */
    private String showModel;
    /**
     * 文档页码
     */
    private int showCount;
    /**
     * 当前正在演示的页码 if(showModel == 2)
     */
    private int showPageIndex;
    /**
     * 是否拥有删除的权限
     */
    private boolean showDelete;
    /**
     * 文档下载地址
     */
    private String showDocPath ;

    private String tabId;

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public MeetingShow() {
    }

    public MeetingShow(String showTitle, String showResID, String showModel, int showCount, int showPageIndex ,String showDocPath) {
        this.showTitle = showTitle;
        this.showResID = showResID;
        this.showModel = showModel;
        this.showCount = showCount;
        this.showPageIndex = showPageIndex;
        this.showDocPath    =   showDocPath ;
    }

    public boolean isShowDelete() {
        return showDelete;
    }

    public void setShowDelete(boolean showDelete) {
        this.showDelete = showDelete;
    }

    public String getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public String getShowResID() {
        return showResID;
    }

    public void setShowResID(String showResID) {
        this.showResID = showResID;
    }

    public String getShowModel() {
        return showModel;
    }

    public void setShowModel(String showModel) {
        this.showModel = showModel;
    }

    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }

    public int getShowPageIndex() {
        return showPageIndex;
    }

    public void setShowPageIndex(int showPageIndex) {
        this.showPageIndex = showPageIndex;
    }

    public String getShowDocPath() {
        return showDocPath;
    }

    public void setShowDocPath(String showDocPath) {
        this.showDocPath = showDocPath;
    }

    public static List<MeetingShow> parseList(JSONArray meetJsonArray){

        if(null == meetJsonArray) return  null;
        List<MeetingShow> meetingShowList = new ArrayList<>();

        if(meetJsonArray.length()>0){

            for(int i=0;i< meetJsonArray.length() ;i++){
                JSONObject meetJson = meetJsonArray.optJSONObject(i);

                meetingShowList.add(parseOneData(meetJson));
            }
        }

        return  meetingShowList;
    }

    private static MeetingShow parseOneData(JSONObject meetJson){

        if(null == meetJson) return  null;
        MeetingShow meetingShow =   new MeetingShow();

        meetingShow.setShowResID(meetJson.optString("res_id"));
        //bmp图片处理.
        String title = meetJson.optString("show_title").toLowerCase();
        /*if(!TextUtils.isEmpty(title)&&title.endsWith(".bmp")){
            title = title.substring(0,title.indexOf(".bmp"))+".jpg";
        }*/
        meetingShow.setShowTitle(title);
        meetingShow.setShowModel(meetJson.optString("is_show"));
        meetingShow.setShowCount(meetJson.optInt("count"));
        meetingShow.setShowPageIndex(meetJson.optInt("page_index"));
        meetingShow.setShowDocPath(meetJson.optString("filePath"));

        return  meetingShow;
    }
}
