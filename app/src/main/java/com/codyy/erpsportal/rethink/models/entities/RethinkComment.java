package com.codyy.erpsportal.rethink.models.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * 教学反思直接评论
 * Created by gujiajia on 2016/1/6.
 */
public class RethinkComment extends RethinkCommentBase {

    private int totalReplyCount;

    private String userType;

    private List<RethinkReply> replies;

    public int getCurrentCount() {
        return replies.size();
    }

    public RethinkComment() {
        replies = new ArrayList<>();
    }

    public int getTotalReplyCount() {
        return totalReplyCount;
    }

    public void setTotalReplyCount(int totalReplyCount) {
        this.totalReplyCount = totalReplyCount;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<RethinkReply> getReplies() {
        return replies;
    }

    public void setReplies(List<RethinkReply> replies) {
        this.replies = replies;
    }

    public boolean hasMoreReplies() {
        return totalReplyCount > replies.size();
    }

    public void addReply(RethinkReply rethinkReply) {
        replies.add(rethinkReply);
    }

    /**
     * 获取评论所需要的项的个数，
     * 如果有回复，结果为评论项+回复个数+更多回复
     * 没有的话只占1项
     *
     * @return
     */
    public int itemCount() {
        if (!hasRelies()) {
            return 1;
        } else {
            return 2 + replies.size();
        }
    }

    public boolean hasRelies() {
        return replies.size() > 0;
    }

    public void addReplies(List<RethinkReply> newReplies) {
        replies.addAll(newReplies);
    }

    public void remove(RethinkReply reply) {
        replies.remove(reply);
    }
}
