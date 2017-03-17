package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.statistics.models.entities.BaseEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kmdai on 2015/4/21.
 */
public class Comment extends RefreshEntity implements Parcelable {
    /**
     * heardview
     */
    public final static int REFRESH_TYPE_TITLE_VIEW = REFRESH_TYPE_LASTVIEW + 1;
    /**
     * 列表
     */
    public final static int REFRESH_TYPE_ITEM_VIEW = REFRESH_TYPE_TITLE_VIEW + 1;
    private int total;
    private String commentId;
    private String baseUserId;
    private String realName;
    private String headPic;
    private String createTime;
    private String commentContent;
    private double showScore;
    private String formattedTime;

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public double getShowScore() {
        return showScore;
    }

    public void setShowScore(double showScore) {
        this.showScore = showScore;
    }

    /**
     * @param object
     * @param comments
     */
    public static void getComment(JSONObject object, List<Comment> comments) {
        if ("success".equals(object.optString("result"))) {
            JSONArray jsonArray = object.optJSONArray("comments");
            for (int i = 0; i < jsonArray.length(); i++) {
                Comment comment = new Comment();
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                comment.setCommentId(jsonObject.optString("commentId"));
                comment.setBaseUserId(jsonObject.optString("baseUserId"));
                comment.setRealName(jsonObject.optString("realName"));
                comment.setHeadPic(jsonObject.optString("headPic"));
                comment.setmHolderType(REFRESH_TYPE_ITEM_VIEW);
                comment.setCreateTime(jsonObject.optString("createTime"));
                comment.setCommentContent(jsonObject.optString("commentContent"));
                comment.setShowScore(jsonObject.optDouble("showScore", -1));
                comment.setFormattedTime(jsonObject.optString("formattedTime"));
                comments.add(comment);
            }
        }
    }

    public Comment() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.total);
        dest.writeString(this.commentId);
        dest.writeString(this.baseUserId);
        dest.writeString(this.realName);
        dest.writeString(this.headPic);
        dest.writeString(this.createTime);
        dest.writeString(this.commentContent);
        dest.writeDouble(this.showScore);
        dest.writeString(this.formattedTime);
    }

    protected Comment(Parcel in) {
        super(in);
        this.total = in.readInt();
        this.commentId = in.readString();
        this.baseUserId = in.readString();
        this.realName = in.readString();
        this.headPic = in.readString();
        this.createTime = in.readString();
        this.commentContent = in.readString();
        this.showScore = in.readDouble();
        this.formattedTime = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
