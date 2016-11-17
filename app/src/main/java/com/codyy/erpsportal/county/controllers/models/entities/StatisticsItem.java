package com.codyy.erpsportal.county.controllers.models.entities;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by kmdai on 16-10-21.
 */

public class StatisticsItem {

    /**
     * result : success
     * invalidCount : 0
     * unreasonableMissedCount : 17
     * reasonableMissedCount : 0
     * reasonList : [{"count":0,"reasonName":"在线课堂系统故障"},{"count":0,"reasonName":"班班通设备故障"},{"count":0,"reasonName":"电力故障"},{"count":0,"reasonName":"教师请假"},{"count":0,"reasonName":"网络故障"},{"count":0,"reasonName":"教室关闭"},{"count":0,"reasonName":"其它原因"},{"count":0,"reasonName":"主讲教室未成功开课"}]
     * validCount : 0
     * classroomName : 主讲教室2
     */

    private int type;
    private String result;
    private int invalidCount;
    private int unreasonableMissedCount;
    private int reasonableMissedCount;
    private int validCount;
    private String classroomName;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * count : 0
     * reasonName : 在线课堂系统故障
     */

    private List<ReasonListBean> reasonList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getInvalidCount() {
        return invalidCount;
    }

    public void setInvalidCount(int invalidCount) {
        this.invalidCount = invalidCount;
    }

    public int getUnreasonableMissedCount() {
        return unreasonableMissedCount;
    }

    public void setUnreasonableMissedCount(int unreasonableMissedCount) {
        this.unreasonableMissedCount = unreasonableMissedCount;
    }

    public int getReasonableMissedCount() {
        return reasonableMissedCount;
    }

    public void setReasonableMissedCount(int reasonableMissedCount) {
        this.reasonableMissedCount = reasonableMissedCount;
    }

    public int getValidCount() {
        return validCount;
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public List<ReasonListBean> getReasonList() {
        return reasonList;
    }

    public void setReasonList(List<ReasonListBean> reasonList) {
        this.reasonList = reasonList;
    }

    public static class ReasonListBean {
        private int count;
        private String reasonName;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getReasonName() {
            return reasonName;
        }

        public void setReasonName(String reasonName) {
            this.reasonName = reasonName;
        }
    }

    /**
     * @param object
     * @return
     */
    public static StatisticsItem getStatisticsItem(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), StatisticsItem.class);
    }
}
