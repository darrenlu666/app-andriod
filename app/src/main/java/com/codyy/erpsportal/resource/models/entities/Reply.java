package com.codyy.erpsportal.resource.models.entities;

/**
 * 教学反思评论回复
 * Created by gujiajia on 2016/1/6.
 */
public class Reply extends CommentBase {

    private String replyToName;

    private String replyToUserId;

    private Comment comment;

    public String getReplyToName() {
        return replyToName;
    }

    public void setReplyToName(String replyToName) {
        this.replyToName = replyToName;
    }

    public String getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(String replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public String getParentId() {
        return comment.getId();
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
