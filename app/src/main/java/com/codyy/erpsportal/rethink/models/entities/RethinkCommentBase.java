package com.codyy.erpsportal.rethink.models.entities;

/**
 * 教学反思评论
 * Created by gujiajia on 2016/1/6.
 */
public class RethinkCommentBase{

    private String id;

    private String userIcon;

    private String userRealName;

    private String userId;

    private String content;

    private String createTime;

    /**
     * 回复是否是我的
     */
    private boolean isMine;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public RethinkCommentBase() {
    }

}
