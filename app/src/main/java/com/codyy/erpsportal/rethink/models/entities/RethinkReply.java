package com.codyy.erpsportal.rethink.models.entities;

/**
 * 教学反思评论回复
 * Created by gujiajia on 2016/1/6.
 */
public class RethinkReply extends RethinkCommentBase {

    private String replyToName;

    private String replyToUserId;

    private RethinkComment comment;

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

    public RethinkComment getComment() {
        return comment;
    }

    public void setComment(RethinkComment comment) {
        this.comment = comment;
    }
}
