package com.codyy.erpsportal.commons.models.entities;

/**
 * Created by yangxinwu on 2015/8/29.
 */
public class CoCoAction {
    /**
     * COCO的动作类型 比如开启免打扰
     */
    private String actionType;
    /**
     * COCO的动作结果 比如设置某人禁言
     */
    private String actionResult;
    /**
     * COCO的操作的对象Id 比如设置某人禁言中的某人
     */
    private String byOperationObject;

    private String to;

    private String from;

    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getActionResult() {
        return actionResult;
    }

    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getByOperationObject() {
        return byOperationObject;
    }

    public void setByOperationObject(String byOperationObject) {
        this.byOperationObject = byOperationObject;
    }
}
