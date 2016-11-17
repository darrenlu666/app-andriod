package com.codyy.erpsportal.homework.models.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作业统计实体类
 * Created by ldh on 2016/1/26.
 */
public class WorkStatisticInfo  {
    /**
     * workId : dhgjdgh
     * workName : 高三化学第五章练习作业
     * className : 初一（1）班
     * publishTime : yyyy-MM-dd hh:mm
     * finishPercent : 0/16
     * itemTotalCount : 16
     * objectiveItemCount : 12
     * subjectiveItemCount : 4
     * totalPercent : 80%
     * list : [{"id":"asdf","itemIndex":"1","itemAccuary":"25%"},{"id":"fdgsdfg","itemIndex":"1","itemAccuary":"25%"}]
     */

    private String workId;
    private String workName;
    private String className;
    private String publishTime;
    private String finishPercent;
    private String itemTotalCount;
    private String objectiveItemCount;
    private String subjectiveItemCount;
    private String totalPercent;
    /**
     * id : asdf
     * itemIndex : 1
     * itemAccuary : 25%
     */

    private List<ListEntity> list;

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public void setFinishPercent(String finishPercent) {
        this.finishPercent = finishPercent;
    }

    public void setItemTotalCount(String itemTotalCount) {
        this.itemTotalCount = itemTotalCount;
    }

    public void setObjectiveItemCount(String objectiveItemCount) {
        this.objectiveItemCount = objectiveItemCount;
    }

    public void setSubjectiveItemCount(String subjectiveItemCount) {
        this.subjectiveItemCount = subjectiveItemCount;
    }

    public void setTotalPercent(String totalPercent) {
        this.totalPercent = totalPercent;
    }

    public void setList(List<ListEntity> list) {
        this.list = list;
    }

    public String getWorkId() {
        return workId;
    }

    public String getWorkName() {
        return workName;
    }

    public String getClassName() {
        return className;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getFinishPercent() {
        return finishPercent;
    }

    public String getItemTotalCount() {
        return itemTotalCount;
    }

    public String getObjectiveItemCount() {
        return objectiveItemCount;
    }

    public String getSubjectiveItemCount() {
        return subjectiveItemCount;
    }

    public String getTotalPercent() {
        return totalPercent;
    }

    public List<ListEntity> getList() {
        return list;
    }

    public static class ListEntity {
        private String id;
        private String itemIndex;
        private String itemAccuary;

        public void setId(String id) {
            this.id = id;
        }

        public void setItemIndex(String itemIndex) {
            this.itemIndex = itemIndex;
        }

        public void setItemAccuary(String itemAccuary) {
            this.itemAccuary = itemAccuary;
        }

        public String getId() {
            return id;
        }

        public String getItemIndex() {
            return itemIndex;
        }

        public String getItemAccuary() {
            return itemAccuary;
        }
    }

    public static WorkStatisticInfo parseResponse(JSONObject response){
        WorkStatisticInfo workStatisticInfo = new WorkStatisticInfo();
        JSONObject jsonObject = response.optJSONObject("data");
        workStatisticInfo.setWorkId(jsonObject.isNull("workId")? "":jsonObject.optString("workId"));
        workStatisticInfo.setWorkName(jsonObject.isNull("workName")?"":jsonObject.optString("workName"));
        workStatisticInfo.setClassName(jsonObject.isNull("className")?"":jsonObject.optString("className"));
        workStatisticInfo.setPublishTime(jsonObject.isNull("pubishTime")?"":jsonObject.optString("pubishTime"));
        workStatisticInfo.setFinishPercent(jsonObject.isNull("finishPercent")?"":jsonObject.optString("finishPercent"));
        workStatisticInfo.setItemTotalCount("作业题量  " + (jsonObject.isNull("itemTotalCount")?"":jsonObject.optString("itemTotalCount")));
        workStatisticInfo.setObjectiveItemCount("客观题量  " + (jsonObject.isNull("objectiveItemCount")?"":jsonObject.optString("objectiveItemCount")));
        workStatisticInfo.setSubjectiveItemCount("主观题量  " + (jsonObject.isNull("subjectiveItemCount")?"":jsonObject.optString("subjectiveItemCount")));
        workStatisticInfo.setTotalPercent(jsonObject.isNull("totalPercent")?"":jsonObject.optString("totalPercent"));
        List<ListEntity> itemInfoList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.optJSONArray("list");
        for(int i = 0; i < jsonArray.length(); i ++){
            ListEntity listEntity = new ListEntity();
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                listEntity.setItemIndex(object.isNull("itemIndex")?"":String.valueOf(object.optInt("itemIndex")));
                listEntity.setItemAccuary(object.isNull("itemAccuary")?"":object.optString("itemAccuary"));
                itemInfoList.add(listEntity);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        workStatisticInfo.setList(itemInfoList);
        return workStatisticInfo;
    }
}
