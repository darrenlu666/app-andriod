package com.codyy.erpsportal.resource.models.entities;

/**
 * 更多回复项
 * Created by gujiajia on 2016/1/6.
 */
public class MoreRelies {

    private Comment mRethinkComment;

    public MoreRelies(Comment rethinkComment) {
        this.mRethinkComment = rethinkComment;
    }

    public boolean hasMore() {
        return mRethinkComment.hasMoreReplies();
    }

    public Comment getRethinkComment() {
        return mRethinkComment;
    }

    public void setRethinkComment(Comment rethinkComment) {
        mRethinkComment = rethinkComment;
    }
}
