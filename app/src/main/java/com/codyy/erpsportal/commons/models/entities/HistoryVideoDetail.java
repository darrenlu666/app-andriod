package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldh on 2015/9/14.
 */
public class HistoryVideoDetail {


    /**
     * total : 16
     * result : success
     * speakerUserName : guojiatea1
     * videoUrl : /Video/
     * data : [{"liveAppointmentVideoId":"1"},{"liveAppointmentVideoId":"ffffffffffffffffffffffffffffff15"},{"liveAppointmentVideoId":"fffffffffffffffffffffffffffffff2"},{"liveAppointmentVideoId":"fffffffffffffffffffffffffffffff3"},{"liveAppointmentVideoId":"fffffffffffffffffffffffffffffff4"},{"liveAppointmentVideoId":"fffffffffffffffffffffffffffffff5"},{"liveAppointmentVideoId":"fffffffffffffffffffffffffffffff6"},{"liveAppointmentVideoId":"fffffffffffffffffffffffffffffff7"},{"liveAppointmentVideoId":"fffffffffffffffffffffffffffffff8"},{"liveAppointmentVideoId":"fffffffffffffffffffffffffffffff9"},{"liveAppointmentVideoId":"ffffffffffffffffffffffffffffff10"},{"liveAppointmentVideoId":"ffffffffffffffffffffffffffffff11"},{"liveAppointmentVideoId":"ffffffffffffffffffffffffffffff12"},{"liveAppointmentVideoId":"ffffffffffffffffffffffffffffff13"},{"liveAppointmentVideoId":"ffffffffffffffffffffffffffffff14"},{"liveAppointmentVideoId":"fffffffffffffffffffffffffffffff1"}]
     * classroomName : 主讲教室1
     */
    private int total;
    private String result;
    private String speakerUserName;
    private String videoUrl;
    private List<DataEntity> data;
    private String classroomName;

    public void setTotal(int total) {
        this.total = total;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setSpeakerUserName(String speakerUserName) {
        this.speakerUserName = speakerUserName;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public int getTotal() {
        return total;
    }

    public String getResult() {
        return result;
    }

    public String getSpeakerUserName() {
        return speakerUserName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public List<DataEntity> getData() {
        return data;
    }

    public String getClassroomName() {
        return classroomName;
    }


    public static class DataEntity{
        /**
         * liveAppointmentVideoId : 1
         */
        private String liveAppointmentVideoId;

        public void setLiveAppointmentVideoId(String liveAppointmentVideoId) {
            this.liveAppointmentVideoId = liveAppointmentVideoId;
        }

        public String getLiveAppointmentVideoId() {
            return liveAppointmentVideoId;
        }

    }


    public static HistoryVideoDetail parseObject(JSONObject jsonObject){
        HistoryVideoDetail historyVideoDetail = new HistoryVideoDetail();

        historyVideoDetail.setVideoUrl(jsonObject.optString("videoUrl"));
        historyVideoDetail.setClassroomName(jsonObject.optString("classroomName"));
        historyVideoDetail.setSpeakerUserName(jsonObject.optString("speakerUserName"));

        List<HistoryVideoDetail.DataEntity> list = new ArrayList<>();
        JSONArray array = jsonObject.optJSONArray("data");

        for (int i = 0;i<array.length();i++){
            HistoryVideoDetail.DataEntity dataEntity = new HistoryVideoDetail.DataEntity();
            JSONObject object = array.optJSONObject(i);
            dataEntity.setLiveAppointmentVideoId(object.optString("liveAppointmentVideoId"));
            list.add(dataEntity);
        }
        historyVideoDetail.setData(list);

        return historyVideoDetail;
    }
}
