package com.codyy.erpsportal.weibo.models.entities;

import android.os.Parcel;

import com.codyy.erpsportal.commons.models.entities.RefreshEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 16-2-19.
 */
public class WeiBoComment extends RefreshEntity {
    public static final int TYPE_PARENT = RefreshEntity.REFRESH_TYPE_LASTVIEW + 1;
    public static final int TYPE_CHILD = TYPE_PARENT + 1;
    public static final int TYPE_MORE = TYPE_CHILD + 1;
    /**
     * miblogCommentId : 2a7d24bb11384f6fa1fbdb9dee965981
     * miblogId : 3edac2eb389a427da85e6f788f88b8c2
     * baseUserId : f1d70ef8235047f896d8db49ae5c5455
     * realName : 测试
     * headPic : http://10.5.225.19:8080/ResourceServer/images/82d83045ee8a419e99a550d8e61396af.png
     * commentContent : 的期望道歉
     * createTime : 1455781048862
     * delFlag : N
     * total : 12
     * serverAddress : http://10.5.225.19:8080/ResourceServer
     * parentCommentId : 2a7d24bb11384f6fa1fbdb9dee965981
     * replyToUserId : f1d70ef8235047f896d8db49ae5c5455
     * replyToUserName :
     */
    private String groupMiblogCommentId;
    private String groupMiblogId;
    private String miblogCommentId;
    private String miblogId;
    private String baseUserId;
    private String realName;
    private String headPic;
    private String commentContent;
    private long createTime;
    private String delFlag;
    private int total;
    private String serverAddress;
    private String parentCommentId;
    private String replyToUserId;
    private String replyToUserName;
    private String userType;
    private int start;
    private int end;
    private int childrenSize;
    private int position;

    public String getGroupMiblogCommentId() {
        return groupMiblogCommentId;
    }

    public void setGroupMiblogCommentId(String groupMiblogCommentId) {
        this.groupMiblogCommentId = groupMiblogCommentId;
    }

    public String getGroupMiblogId() {
        return groupMiblogId;
    }

    public void setGroupMiblogId(String groupMiblogId) {
        this.groupMiblogId = groupMiblogId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getChildrenSize() {
        return childrenSize;
    }

    public void setChildrenSize(int childrenSize) {
        this.childrenSize = childrenSize;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setMiblogCommentId(String miblogCommentId) {
        this.miblogCommentId = miblogCommentId;
    }

    public void setMiblogId(String miblogId) {
        this.miblogId = miblogId;
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

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public void setReplyToUserId(String replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public void setReplyToUserName(String replyToUserName) {
        this.replyToUserName = replyToUserName;
    }

    public String getMiblogCommentId() {
        return miblogCommentId;
    }

    public String getMiblogId() {
        return miblogId;
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

    public String getCommentContent() {
        return commentContent;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public int getTotal() {
        return total;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public String getReplyToUserId() {
        return replyToUserId;
    }

    public String getReplyToUserName() {
        return replyToUserName;
    }

    public static ArrayList<WeiBoComment> getData(JSONObject object) {
        if ("success".equals(object.optString("result"))) {
            ArrayList<WeiBoComment> weiBoComments = new ArrayList<>();
            JSONArray array = object.optJSONArray("commentList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject comment = array.optJSONObject(i);
                WeiBoComment weiBoComment = getWeiBoComment(comment, TYPE_PARENT);
                JSONArray child = comment.optJSONArray("childrenCommontList");
                weiBoComment.setChildrenSize(child.length());
                weiBoComments.add(weiBoComment);
                for (int j = 0; j < child.length(); j++) {
                    JSONObject childComment = child.optJSONObject(j);
                    WeiBoComment weiBoCommentChild = getWeiBoComment(childComment, TYPE_CHILD);
                    weiBoCommentChild.setChildrenSize(child.length());
                    weiBoComments.add(weiBoCommentChild);
                }
                if (child.length() < weiBoComment.getTotal()) {
                    WeiBoComment refreshEntity = new WeiBoComment();
                    refreshEntity.setMiblogId(weiBoComment.getMiblogId());
                    refreshEntity.setMiblogCommentId(weiBoComment.getMiblogCommentId());
                    refreshEntity.setStart(child.length() + 1);
                    refreshEntity.setChildrenSize(child.length());
                    refreshEntity.setmHolderType(TYPE_MORE);
                    weiBoComments.add(refreshEntity);
                }
            }
            return weiBoComments;
        }
        return null;
    }

    public static WeiBoComment getWeiBoComment(JSONObject object, int type) {
//        Gson gson = new Gson();
//        WeiBoComment weiBoComment = gson.fromJson(object.toString(), WeiBoComment.class);
        WeiBoComment weiBoComment = new WeiBoComment();
        weiBoComment.setMiblogCommentId(object.optString("miblogCommentId"));
        weiBoComment.setMiblogId(object.optString("miblogId"));
        weiBoComment.setBaseUserId(object.optString("baseUserId"));
        weiBoComment.setRealName(object.optString("realName"));
        weiBoComment.setHeadPic(object.optString("headPic"));
        weiBoComment.setUserType(object.optString("userType"));
        weiBoComment.setCommentContent(object.optString("commentContent"));
        weiBoComment.setCreateTime(object.optLong("createTime"));
        weiBoComment.setParentCommentId(object.optString("parentCommentId"));
        weiBoComment.setReplyToUserId(object.optString("replyToUserId"));
        weiBoComment.setReplyToUserName(object.optString("replyToUserName"));
        weiBoComment.setDelFlag(object.optString("delFlag"));
        weiBoComment.setTotal(object.optInt("total"));
        weiBoComment.setGroupMiblogCommentId(object.optString("groupMiblogCommentId"));
        weiBoComment.setGroupMiblogId(object.optString("groupMiblogId"));
        weiBoComment.setmHolderType(type);
        return weiBoComment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.groupMiblogCommentId);
        dest.writeString(this.groupMiblogId);
        dest.writeString(this.miblogCommentId);
        dest.writeString(this.miblogId);
        dest.writeString(this.baseUserId);
        dest.writeString(this.realName);
        dest.writeString(this.headPic);
        dest.writeString(this.commentContent);
        dest.writeLong(this.createTime);
        dest.writeString(this.delFlag);
        dest.writeInt(this.total);
        dest.writeString(this.serverAddress);
        dest.writeString(this.parentCommentId);
        dest.writeString(this.replyToUserId);
        dest.writeString(this.replyToUserName);
        dest.writeString(this.userType);
        dest.writeInt(this.start);
        dest.writeInt(this.end);
        dest.writeInt(this.childrenSize);
        dest.writeInt(this.position);
    }

    public WeiBoComment() {
    }

    protected WeiBoComment(Parcel in) {
        super(in);
        this.groupMiblogCommentId = in.readString();
        this.groupMiblogId = in.readString();
        this.miblogCommentId = in.readString();
        this.miblogId = in.readString();
        this.baseUserId = in.readString();
        this.realName = in.readString();
        this.headPic = in.readString();
        this.commentContent = in.readString();
        this.createTime = in.readLong();
        this.delFlag = in.readString();
        this.total = in.readInt();
        this.serverAddress = in.readString();
        this.parentCommentId = in.readString();
        this.replyToUserId = in.readString();
        this.replyToUserName = in.readString();
        this.userType = in.readString();
        this.start = in.readInt();
        this.end = in.readInt();
        this.childrenSize = in.readInt();
        this.position = in.readInt();
    }

    public static final Creator<WeiBoComment> CREATOR = new Creator<WeiBoComment>() {
        @Override
        public WeiBoComment createFromParcel(Parcel source) {
            return new WeiBoComment(source);
        }

        @Override
        public WeiBoComment[] newArray(int size) {
            return new WeiBoComment[size];
        }
    };
}
