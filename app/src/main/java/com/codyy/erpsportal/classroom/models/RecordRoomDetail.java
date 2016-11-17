package com.codyy.erpsportal.classroom.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 专递课堂 往期录播详情实体类
 * Created by ldh on 2016/7/6.
 */
public class RecordRoomDetail extends BaseClassRoomDetail {
    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public String getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(String timeLength) {
        this.timeLength = timeLength;
    }

    public List<VideoListInfo> getVideoInfoList() {
        return videoInfoList;
    }

    public void setVideoInfoList(List<VideoListInfo> videoInfoList) {
        this.videoInfoList = videoInfoList;
    }

    private int playCount;
    private String timeLength;
    private List<VideoListInfo> videoInfoList;

    public static class VideoListInfo {
        private String videoId;
        private String videoIndex;
        private String videoUrl;

        public VideoListInfo(){

        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getVideoIndex() {
            return videoIndex;
        }

        public void setVideoIndex(String videoIndex) {
            this.videoIndex = videoIndex;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
    }

    /**
     * 请求成功返回实体类的内容
     *
     * @param response
     * @return
     */
    public static RecordRoomDetail parseResult(JSONObject response) {
        RecordRoomDetail recordRoomDetail = new RecordRoomDetail();
        List<ReceiveInfoEntity> receiveInfoEntityList = new ArrayList<>();
        JSONArray jsonArray = response.optJSONArray("receiveInfoList");
        if(null != jsonArray){
            for (int i = 0; i < jsonArray.length(); i++) {
                ReceiveInfoEntity receiveInfoEntity = new ReceiveInfoEntity();
                JSONObject object = jsonArray.optJSONObject(i);
                receiveInfoEntity.setReceiveName(object.isNull("receiveName") ? "" : object.optString("receiveName"));
                receiveInfoEntityList.add(receiveInfoEntity);
            }
        }
        recordRoomDetail.setReceiveInfoList(receiveInfoEntityList);
        JSONArray array = response.optJSONArray("videoList");
        List<VideoListInfo> videoListInfoList = new ArrayList<>();
        if(null != array){
            for(int i=0;i<array.length();i++){
                VideoListInfo videoListInfo = new VideoListInfo();
                JSONObject object = array.optJSONObject(i);
                videoListInfo.setVideoId(object.isNull("videoId")?"":object.optString("videoId"));
                videoListInfo.setVideoIndex(object.isNull("videoIndex")?"":object.optString("videoIndex"));
                videoListInfo.setVideoUrl(object.isNull("videoUrl")?"":object.optString("videoUrl"));
                videoListInfoList.add(videoListInfo);
            }
        }
        recordRoomDetail.setVideoInfoList(videoListInfoList);
        recordRoomDetail.setArea(response.isNull("area") ? "" : response.optString("area"));
        recordRoomDetail.setClassPeriod(response.isNull("classPeriod") ? "" : response.optString("classPeriod"));
        recordRoomDetail.setClassTime(response.isNull("classTime") ? "" : response.optString("classTime"));
        recordRoomDetail.setGrade(response.isNull("grade") ? "" : response.optString("grade"));
        recordRoomDetail.setSchoolName(response.isNull("schoolName") ? "" : response.optString("schoolName"));
        recordRoomDetail.setTeacher(response.isNull("teacher") ? "" : response.optString("teacher"));
        recordRoomDetail.setSubject(response.isNull("subject") ? "" : response.optString("subject"));
        recordRoomDetail.setTimeLength(response.isNull("timeLength")?"":response.optString("timeLength"));
        recordRoomDetail.setPlayCount(response.isNull("playCount")?0:response.optInt("playCount"));
        return recordRoomDetail;
    }
}
