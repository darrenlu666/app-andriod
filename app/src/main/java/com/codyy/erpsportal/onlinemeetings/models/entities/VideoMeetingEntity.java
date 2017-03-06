package com.codyy.erpsportal.onlinemeetings.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldh on 2015/8/14.
 */


public class VideoMeetingEntity extends BaseTitleItemBar implements  Parcelable {


    /**
     * id : 45149d7738ec4ce885606292358e6e63
     * title : 阿苏大发地方
     * meet_sate : INIT
     * begin_time : 2015-08-11 13:41
     * speaker : &lt;input/&gt;
     */
    private String id;
    private String title;
    private String meet_sate;
    private String begin_time;
    private String speaker;

    public VideoMeetingEntity(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMeet_sate(String meet_sate) {
        this.meet_sate = meet_sate;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMeet_sate() {
        return meet_sate;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public String getSpeaker() {
        return speaker;
    }


    public static VideoMeetingEntity parseObject(JSONObject jsonObject)
    {
        VideoMeetingEntity videoMeetingEntity = new VideoMeetingEntity();
        videoMeetingEntity.setId(jsonObject.optString("id"));
        videoMeetingEntity.setBegin_time(jsonObject.optString("begin_time").equals("null")?"无":jsonObject.optString("begin_time"));
        videoMeetingEntity.setTitle(jsonObject.optString("title"));
        videoMeetingEntity.setSpeaker(jsonObject.optString("speaker"));
        videoMeetingEntity.setMeet_sate(jsonObject.optString("meet_sate"));

        return videoMeetingEntity;
    }

    public static List<VideoMeetingEntity> parseJsonArray (JSONArray jsonArray){
        if(jsonArray == null || jsonArray.length() == 0){
            return null;
        }

        List<VideoMeetingEntity> videoMeetingEntities = new ArrayList<>();
        for(int i = 0;i< jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            VideoMeetingEntity videoMeetingEntity = parseObject(jsonObject);
            videoMeetingEntities.add(videoMeetingEntity);
        }

        return videoMeetingEntities;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.begin_time);
        dest.writeString(this.id);
        dest.writeString(this.meet_sate);
        dest.writeString(this.title);
        dest.writeString(this.speaker);
    }

    public static final Parcelable.Creator<VideoMeetingEntity> CREATOR = new Parcelable.Creator<VideoMeetingEntity>() {
        public VideoMeetingEntity createFromParcel(Parcel source) {
            return new VideoMeetingEntity(source);
        }

        public VideoMeetingEntity[] newArray(int size) {
            return new VideoMeetingEntity[size];
        }
    };

    protected VideoMeetingEntity(Parcel in) {
        this.begin_time = in.readString();
        this.id = in.readString();
        this.meet_sate = in.readString();
        this.title = in.readString();
        this.title = in.readString();
        this.speaker = in.readString();
    }
}




