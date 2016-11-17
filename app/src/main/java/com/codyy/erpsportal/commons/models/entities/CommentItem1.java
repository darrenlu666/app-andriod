package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 15-10-17.
 */
public class CommentItem1 {

    /**
     * scheduleDetailCommentId : c5b31369155040d288c6c7acca6b397c
     * scheduleDetailId : 0e2cc9970b824b2582a8e6293bf4573b
     * commentContent : .@~-#&+fg付BBC
     * createTime : 2015-10-16 13:57:49
     * baseUserId : a0b6d9989b0943bfbb0d82749b6f9592
     * realName : 国家管理员
     * headPic : 1a3b82216b064ee9977843236e53bef0.jpg
     * schoolId : null
     * schoolName : null
     * baseAreaId : 76ad8bf306264daa8b9a77fa2778d666
     * areaName : 中国
     * userType : AREA_USR
     */

    private String scheduleDetailCommentId;
    private String scheduleDetailId;
    private String commentContent;
    private String createTime;
    private String baseUserId;
    private String realName;
    private String headPic;
    private String schoolId;
    private String schoolName;
    private String baseAreaId;
    private String areaName;
    private String userType;
    private String formattedTime;
    private String commentUserId;

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public void setScheduleDetailCommentId(String scheduleDetailCommentId) {
        this.scheduleDetailCommentId = scheduleDetailCommentId;
    }

    public void setScheduleDetailId(String scheduleDetailId) {
        this.scheduleDetailId = scheduleDetailId;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setBaseAreaId(String baseAreaId) {
        this.baseAreaId = baseAreaId;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getScheduleDetailCommentId() {
        return scheduleDetailCommentId;
    }

    public String getScheduleDetailId() {
        return scheduleDetailId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public String getRealName() {
        return realName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getBaseAreaId() {
        return baseAreaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getUserType() {
        return userType;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    /**
     * scheduleDetailCommentId : c5b31369155040d288c6c7acca6b397c
     * scheduleDetailId : 0e2cc9970b824b2582a8e6293bf4573b
     * commentContent : .@~-#&+fg付BBC
     * createTime : 2015-10-16 13:57:49
     * baseUserId : a0b6d9989b0943bfbb0d82749b6f9592
     * realName : 国家管理员
     * headPic : 1a3b82216b064ee9977843236e53bef0.jpg
     * schoolId : null
     * schoolName : null
     * baseAreaId : 76ad8bf306264daa8b9a77fa2778d666
     * areaName : 中国
     * userType : AREA_USR
     */
    public static void getCommentItem(JSONObject object, ArrayList<CommentItem1> commentItem1s) {
        if ("success".equals(object.optString("result"))) {
            JSONArray array = object.optJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object1 = array.optJSONObject(i);
                CommentItem1 item1 = new CommentItem1();
                item1.setScheduleDetailCommentId(object1.optString("scheduleDetailCommentId"));
                item1.setScheduleDetailId(object1.optString("scheduleDetailId"));
                item1.setCommentContent(object1.optString("commentContent"));
                item1.setCreateTime(object1.optString("createTime"));
                item1.setBaseUserId(object1.optString("baseUserId"));
                item1.setRealName(object1.optString("realName"));
                item1.setHeadPic(object1.optString("headPic"));
                item1.setSchoolId(object1.optString("schoolId"));
                item1.setSchoolName(object1.optString("schoolName"));
                item1.setAreaName(object1.optString("areaName"));
                item1.setBaseAreaId(object1.optString("baseAreaId"));
                item1.setCommentUserId(object1.optString("commentUserId"));
                item1.setUserType(object1.optString("userType"));
                item1.setFormattedTime(object1.optString("formattedTime"));//setFormattedTime
                commentItem1s.add(item1);
            }
        }
    }
}
