package com.codyy.erpsportal.commons.services;

/**
 * Created by yangxinwu on 2015/8/19.
 */
public interface IMeeting {
    /**
     * 发送单聊聊天信息
     */
    void sendMsg(String msg);

    /**
     * 发送群组聊天信息
     */
    void sendSignalMsg(String msg,String id);

}
