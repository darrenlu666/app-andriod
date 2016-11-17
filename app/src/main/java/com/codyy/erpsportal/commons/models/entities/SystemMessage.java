package com.codyy.erpsportal.commons.models.entities;

/**
 * Created by yangxinwu on 2015/8/28.
 */
public class SystemMessage {

    private String id;
    private String nick ;//发送人昵称
    private String msg;
    private String time;
    private String headUrl;

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
