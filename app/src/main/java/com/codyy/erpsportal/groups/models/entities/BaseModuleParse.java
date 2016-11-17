package com.codyy.erpsportal.groups.models.entities;

/**
 * Created by poe on 16-3-7.
 */
public class BaseModuleParse {
    private String result ;//圈组成员写错了 -reuslt .明天沟通协调下
    private String message ;
    private GroupMemberParse list ;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GroupMemberParse getList() {
        return list;
    }

    public void setList(GroupMemberParse list) {
        this.list = list;
    }
}
