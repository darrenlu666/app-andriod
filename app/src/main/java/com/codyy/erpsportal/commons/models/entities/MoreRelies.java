package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.rethink.models.entities.RethinkComment;

/**
 * 更多回复项
 * Created by gujiajia on 2016/1/6.
 */
public class MoreRelies {

    private RethinkComment mRethinkComment;

    public MoreRelies(RethinkComment rethinkComment) {
        this.mRethinkComment = rethinkComment;
    }

    public boolean hasMore() {
        return mRethinkComment.hasMoreReplies();
    }

    public RethinkComment getRethinkComment() {
        return mRethinkComment;
    }

    public void setRethinkComment(RethinkComment rethinkComment) {
        mRethinkComment = rethinkComment;
    }
}
