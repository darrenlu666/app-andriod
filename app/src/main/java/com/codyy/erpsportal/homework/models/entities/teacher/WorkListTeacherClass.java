package com.codyy.erpsportal.homework.models.entities.teacher;

import com.codyy.erpsportal.homework.models.entities.WorkListBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 老师作业列表
 * Created by ldh on 2015/12/28.
 */
public class WorkListTeacherClass extends WorkListBase {
    private String workTotal;
    private String workFinishedCount;
    private Boolean haveAttachment;

    public Boolean getHaveAttachment() {
        return haveAttachment;
    }

    public void setHaveAttachment(Boolean haveAttachment) {
        this.haveAttachment = haveAttachment;
    }

    public String getWorkFinishedCount() {
        return workFinishedCount;
    }

    public void setWorkFinishedCount(String workFinishedCount) {
        this.workFinishedCount = workFinishedCount;
    }

    public String getWorkTotal() {
        return workTotal;
    }

    public void setWorkTotal(String workTotal) {
        this.workTotal = workTotal;
    }

    public static List<WorkListTeacherClass> parseTeacherWorkList(JSONObject response){
        List<WorkListTeacherClass> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.optJSONArray("list");
            for(int i = 0; i< jsonArray.length();i++){
                WorkListTeacherClass workListTeacherClass = new WorkListTeacherClass();
                JSONObject object = jsonArray.getJSONObject(i);
                workListTeacherClass.setWorkId(object.isNull("workId") ? "":object.optString("workId"));
                workListTeacherClass.setWorkName(object.isNull("workName")? "" : object.optString("workName"));
                workListTeacherClass.setWorkAssignTime("布置时间 " +  (object.isNull("workStartTime") ? "" :object.optString("workStartTime")));
                workListTeacherClass.setWorkSubject(object.isNull("workSubject") ? "":object.optString("workSubject"));
                workListTeacherClass.setSubjectPic(object.isNull("subjectPic")?"":object.optString("subjectPic"));
                workListTeacherClass.setReadOverType(object.isNull("readOverType")?"":object.optString("readOverType"));
                workListTeacherClass.setWorkTotal("共" + (object.isNull("workTotal")? "":object.optString("workTotal")) + "题");
                workListTeacherClass.setWorkFinishedCount(object.isNull("workFinishedCount") ? "" :object.optString("workFinishedCount"));
                workListTeacherClass.setWorkState(object.isNull("workState")? "" :object.optString("workState"));
                workListTeacherClass.setHaveAttachment(object.isNull("haveAttachment")? false :object.optBoolean("haveAttachment"));
                list.add(workListTeacherClass);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
