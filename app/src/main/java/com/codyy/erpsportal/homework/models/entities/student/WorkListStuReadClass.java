package com.codyy.erpsportal.homework.models.entities.student;

import com.codyy.erpsportal.homework.models.entities.WorkListBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生作业列表-我的批阅类
 * Created by ldh on 2015/12/28.
 */
public class WorkListStuReadClass extends WorkListBase {
    private String workTotal;
    private String readFinishedCount;

    public String getWorkTotal() {
        return workTotal;
    }

    public void setWorkTotal(String workTotal) {
        this.workTotal = workTotal;
    }

    public String getReadFinishedCount() {
        return readFinishedCount;
    }

    public void setReadFinishedCount(String readFinishedCount) {
        this.readFinishedCount = readFinishedCount;
    }

    public static List<WorkListStuReadClass> parseResponse(JSONObject response){
        List<WorkListStuReadClass> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for(int i = 0; i< jsonArray.length();i++){
                WorkListStuReadClass workListStuReadClass = new WorkListStuReadClass();
                JSONObject object = jsonArray.getJSONObject(i);
                workListStuReadClass.setWorkId(object.isNull("workId") ? "":object.optString("workId"));
                workListStuReadClass.setWorkName(object.isNull("workName")? "" : object.optString("workName"));
                workListStuReadClass.setWorkAssignTime("布置时间 " +  (object.isNull("workStartTime") ? "" :object.optString("workStartTime")));
                workListStuReadClass.setWorkSubject(object.isNull("workSubject") ? "":object.optString("workSubject"));
                workListStuReadClass.setSubjectPic(object.isNull("subjectPic")?"":object.optString("subjectPic"));
                workListStuReadClass.setReadOverType(object.isNull("readOverType")?"":object.optString("readOverType"));
                workListStuReadClass.setWorkTotal("共" + (object.isNull("workTotal")? "":object.optString("workTotal")) + "题");
                workListStuReadClass.setReadFinishedCount(object.isNull("readFinishedCount") ? "" :object.optString("readFinishedCount"));
                workListStuReadClass.setWorkState(object.isNull("workState")? "" :object.optString("workState"));
                list.add(workListStuReadClass);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
