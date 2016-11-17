package com.codyy.erpsportal.homework.models.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作业完成情况列表实体类 （共用：学生我的批阅-》批阅/查看批阅列表 实体类）
 * Created by ldh on 2016/1/25.
 */
public class WorkFinishInfoList {
    /**
     * studentId : ************
     * workId : ************
     * studentName : 张三
     * donePercent : 9/12
     * objectiveRightPercent : 5%
     * submitTime : yyyy-MM-dd hh:mm
     */

    private String studentId;
    private String workId;
    private String studentName;
    private String donePercent;
    private String objectiveRightPercent;
    private String submitTime;

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setDonePercent(String donePercent) {
        this.donePercent = donePercent;
    }

    public void setObjectiveRightPercent(String objectiveRightPercent) {
        this.objectiveRightPercent = objectiveRightPercent;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getWorkId() {
        return workId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getDonePercent() {
        return donePercent;
    }

    public String getObjectiveRightPercent() {
        return objectiveRightPercent;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public static List<WorkFinishInfoList> parseFinishInfoList(JSONObject response){
        List<WorkFinishInfoList> list = new ArrayList<>();
        if(response.optString("result").equals("success")){
            JSONArray jsonArray = response.optJSONArray("list");
            if(jsonArray == null) return null;
            for(int i = 0;i< jsonArray.length();i++){
                try {
                    WorkFinishInfoList workFinishInfoList = new WorkFinishInfoList();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    workFinishInfoList.setWorkId(jsonObject.isNull("workId")?"":jsonObject.optString("workId"));
                    workFinishInfoList.setStudentId(jsonObject.isNull("studentId")?"":jsonObject.optString("studentId"));
                    workFinishInfoList.setStudentName(jsonObject.isNull("studentName")?"":jsonObject.optString("studentName"));
                    workFinishInfoList.setDonePercent(jsonObject.isNull("donePercent")?"":jsonObject.optString("donePercent"));
                    workFinishInfoList.setObjectiveRightPercent(jsonObject.isNull("objectiveRightPercent")?"":jsonObject.optString("objectiveRightPercent"));
                    workFinishInfoList.setSubmitTime(jsonObject.isNull("submitTime")?"":jsonObject.optString("submitTime"));
                    list.add(workFinishInfoList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
