package com.codyy.erpsportal.homework.models.entities.student;

import com.codyy.erpsportal.homework.models.entities.WorkListBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生我的作业列表类
 * Created by ldh on 2015/12/28.
 */
public class WorkListStuWorkClass extends WorkListBase {
    private String workAccuracy;
    private String itemFinishedCount;
    private Boolean haveAttachment;

    public String getWorkAccuracy() {
        return workAccuracy;
    }

    public void setWorkAccuracy(String workAccuracy) {
        this.workAccuracy = workAccuracy;
    }

    public String getItemFinishedCount() {
        return itemFinishedCount;
    }

    public void setItemFinishedCount(String itemFinishedCount) {
        this.itemFinishedCount = itemFinishedCount;
    }
    public Boolean getHaveAttachment() {
        return haveAttachment;
    }

    public void setHaveAttachment(Boolean haveAttachment) {
        this.haveAttachment = haveAttachment;
    }

    public static List<WorkListStuWorkClass> parseResponse(JSONObject response){
        List<WorkListStuWorkClass> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for(int i = 0; i< jsonArray.length();i++){
                WorkListStuWorkClass workListStuWorkClass = new WorkListStuWorkClass();
                JSONObject object = jsonArray.getJSONObject(i);
                workListStuWorkClass.setWorkId(object.isNull("workId") ? "":object.optString("workId"));
                workListStuWorkClass.setWorkName(object.isNull("workName")? "" : object.optString("workName"));
                workListStuWorkClass.setWorkAssignTime("布置时间 " +  (object.isNull("workStartTime") ? "" :object.optString("workStartTime")));
                workListStuWorkClass.setWorkSubject(object.isNull("workSubject") ? "":object.optString("workSubject"));
                workListStuWorkClass.setSubjectPic(object.isNull("subjectPic")?"":object.optString("subjectPic"));
                workListStuWorkClass.setReadOverType(object.isNull("readOverType")?"":object.optString("readOverType"));
                workListStuWorkClass.setWorkAccuracy(object.isNull("workAccuracy")?"":object.optString("workAccuracy"));
                workListStuWorkClass.setItemFinishedCount(object.isNull("itemFinishedCount")?"":object.optString("itemFinishedCount"));
                workListStuWorkClass.setWorkState(object.isNull("workState")? "" :object.optString("workState"));
                workListStuWorkClass.setHaveAttachment(object.isNull("haveAttachment")? false :object.optBoolean("haveAttachment"));
                list.add(workListStuWorkClass);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
