package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.commons.models.parsers.JsonParsable;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxinwu on 2015/8/17.
 */
public class CommentItem {
    /**
     * realName : 习大大
     * meetCommentId : ff10d44a7e3f427d9bd13250504c23cb
     * createTime : 1439521566893
     * strCreatetime : 2015-08-14 11:06
     * meetingId : eb3982d7069443309a2952188b2129c4
     * schoolName : null
     * areaPath : 中国
     * headPic : 3f12b617a4ba4cd7ab1446e47406aa50.png
     * content : my second comment
     * baseUserId : 02d13c173d7c4f2ab1e40c1fa60f39a0
     */
    private String realName;
    private String meetCommentId;
    private long createTime;
    private String strCreatetime;
    private String meetingId;
    private String schoolName;
    private String areaPath;
    private String headPic;
    private String content;
    private String userType;
    private String baseUserId;
    private String formattedTime;

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setMeetCommentId(String meetCommentId) {
        this.meetCommentId = meetCommentId;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setStrCreatetime(String strCreatetime) {
        this.strCreatetime = strCreatetime;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setAreaPath(String areaPath) {
        this.areaPath = areaPath;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getRealName() {
        return realName;
    }

    public String getMeetCommentId() {
        return meetCommentId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getStrCreatetime() {
        return strCreatetime;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getAreaPath() {
        return areaPath;
    }

    public String getHeadPic() {
        return headPic;
    }

    public String getContent() {
        return content;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public static CommentItem parseJson(JSONObject jsonObject) {
        return JSON_PARSER.parse(jsonObject);
    }

    public static List<CommentItem> parseJsonArray(JSONArray jsonArray) {
        return JSON_PARSER.parseArray(jsonArray);
    }

    public final static JsonParsable<CommentItem> JSON_PARSER = new JsonParser<CommentItem>() {
        @Override
        public CommentItem parse(JSONObject jsonObject) {
            CommentItem commentItem = new CommentItem();
            commentItem.setMeetCommentId(jsonObject.optString("meetCommentId"));
            commentItem.setMeetingId(jsonObject.optString("meetingId"));
            commentItem.setBaseUserId(jsonObject.optString("baseUserId"));
            commentItem.setContent(jsonObject.optString("content"));
            commentItem.setCreateTime(jsonObject.optLong("createTime"));
            commentItem.setRealName(jsonObject.optString("realName"));
            commentItem.setSchoolName(jsonObject.optString("schoolName"));
            commentItem.setHeadPic(jsonObject.optString("headPic"));
            commentItem.setAreaPath(jsonObject.optString("areaPath"));
            commentItem.setUserType(jsonObject.optString("userType"));
            commentItem.setStrCreatetime(jsonObject.optString("strCreatetime"));
            commentItem.setFormattedTime(jsonObject.optString("formattedTime"));
            return commentItem;
        }
    };

    /**
     * @param object
     * @param commentItems
     */
    public static void getComment(JSONObject object, ArrayList<CommentItem> commentItems) {
        if ("success".equals(object.optString("result"))) {
            JSONArray array = object.optJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.optJSONObject(i);
                CommentItem commentItem = new CommentItem();
                commentItem.setMeetingId(jsonObject.optString("id"));
                commentItem.setBaseUserId(jsonObject.optString("baseUserId"));
                commentItem.setRealName(jsonObject.optString("commentUser"));
                commentItem.setUserType(jsonObject.optString("userType"));
                commentItem.setHeadPic(jsonObject.optString("commentUserPic"));
                commentItem.setContent(jsonObject.optString("commentContent"));
                commentItem.setFormattedTime(jsonObject.optString("formattedTime"));
                commentItem.setStrCreatetime(jsonObject.optString("commentTime"));
                commentItems.add(commentItem);
            }
        }
    }

    /**
     * @param object
     * @param commentItems
     */
    public static void getScheduleComment(JSONObject object, ArrayList<CommentItem> commentItems) {
        if ("success".equals(object.optString("result"))) {
            JSONArray array = object.optJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.optJSONObject(i);
                CommentItem commentItem = new CommentItem();
                commentItem.setMeetingId(jsonObject.optString("scheduleDetailCommentId"));
                commentItem.setRealName(jsonObject.optString("realName"));
                commentItem.setHeadPic(jsonObject.optString("headPic"));
                commentItem.setUserType(jsonObject.optString("userType"));
                commentItem.setContent(jsonObject.optString("commentContent"));
                commentItem.setFormattedTime(jsonObject.optString("formattedTime"));
                commentItem.setStrCreatetime(jsonObject.optString("createTime"));
                commentItems.add(commentItem);
            }
        }
    }

}
