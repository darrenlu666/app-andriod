package com.codyy.erpsportal.homework.models.entities.parent;

import com.codyy.erpsportal.homework.models.entities.WorkListBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 家长作业列表类
 * Created by ldh on 2015/12/28.
 */
public class WorkListParentClass extends WorkListBase {
    private String workAccuracy;
    private String itemFinishedCount;

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

    public static List<WorkListParentClass> parseResponse(JSONObject response){
        List<WorkListParentClass> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for(int i = 0; i< jsonArray.length();i++){
                WorkListParentClass workListParentClass = new WorkListParentClass();
                JSONObject object = jsonArray.getJSONObject(i);
                workListParentClass.setWorkId(object.isNull("workId") ? "":object.optString("workId"));
                workListParentClass.setWorkName(object.isNull("workName")? "" : object.optString("workName"));
                workListParentClass.setWorkAssignTime("布置时间 " +  (object.isNull("workStartTime") ? "" :object.optString("workStartTime")));
                workListParentClass.setWorkSubject(object.isNull("workSubject") ? "":object.optString("workSubject"));
                workListParentClass.setSubjectPic(object.isNull("subjectPic")?"":object.optString("subjectPic"));
                workListParentClass.setReadOverType(object.isNull("readOverType")?"":object.optString("readOverType"));
                workListParentClass.setWorkAccuracy(object.isNull("workAccuracy")?"":object.optString("workAccuracy"));
                workListParentClass.setItemFinishedCount(object.isNull("itemFinishedCount")?"":object.optString("itemFinishedCount"));
                workListParentClass.setWorkState(object.isNull("workState")? "" :object.optString("workState"));
                list.add(workListParentClass);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
