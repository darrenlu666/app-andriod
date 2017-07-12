package com.codyy.erpsportal.commons.models.entities.comment;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 评论类的基础
 * Created by poe on 16-7-1.
 */
public class BaseComment extends BaseTitleItemBar {
    /**
     * 相关类id【如：评论id ，博文id】
     */
    @SerializedName(value = "blogId", alternate = {"evaluationId", "scheduleDetailId"})
    private String id;                          //当前评论博文的id
    /**
     * 评论id
     */
    @SerializedName(value = "blogCommentId", alternate = {"commentId", "scheduleDetailCommentId","meetCommentId"})
    private String commentId;                   //当前评论id
    /**
     * 我的基本用户id
     */
    private String baseUserId;                  //评论人userId
    /**
     * 当前用户类型
     */
    private String userType;                   //评论人的用户类型 userType
    /**
     * 评论人名字
     */
    private String realName;                    //评论人名字
    /**
     * 评论头像
     */
    private String headPic;                     //头像
    /**
     * 被回复人的名字
     */
    @SerializedName(value = "replyName",alternate = {"replyToRealName"})
    private String replyName;                   //被回复人的名字
    /**
     * 被回复人的而用户id
     */
    @SerializedName("replyToId")
    private String replyToUserId;               //回复的人UserId
    /**
     * 创建时间
     */
    private String createTime;                  //创建时间 str

    /**
     * 创建时间
     */
    @SerializedName("strCreatetime")
    private String strCreateTime;                //字符串型时间　
    /**
     * 服务器地址
     */
    private String serverAddress;               //服务地址
    /**
     * 父类评论id
     */
    private String parentCommentId;             //被回复的评论id
    /**
     * 评论内容
     */
    @SerializedName(value = "commentContent", alternate = {"content"})
    private String commentContent;              //评论内容

    private String formattedTime;               //时间的统一形式 刚刚/1分钟前/04-06

    /**
     * 二级回复列表
     */
    @SerializedName(value = "replyComment", alternate = {"childCommentList"})
    private List<BaseComment> replyList;     //二级回复列表


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(String replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public List<BaseComment> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<BaseComment> replyList) {
        this.replyList = replyList;
    }

    public String getStrCreateTime() {
        return strCreateTime;
    }

    public void setStrCreateTime(String strCreateTime) {
        this.strCreateTime = strCreateTime;
    }
}
