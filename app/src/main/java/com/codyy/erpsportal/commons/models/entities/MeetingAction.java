package com.codyy.erpsportal.commons.models.entities;

/**
 * 用于在线视频会议传递消息
 * Created by poe on 15-9-17.
 */
public class MeetingAction {

    public static final String ACTION_TYPE_EXPAND = "meeting.action.expand";
    public static final String ACTION_TYPE_COLLAPSE = "meeting.action.collapse";
    public static final String ACTION_TYPE_REFRESH_USER_ONLINE = "meeting.action.refresh.online.users";

    /**
     * 消息类型
     */
    private String type;
    /**
     * 执行对象
     */
    private String action;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
