package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 发言人实体类
 * Created by poe on 15-8-31.
 */
public class SpeakerEntity implements Parcelable{

    private String speakID;
    private String speakIcon;

    public SpeakerEntity(String speakID, String speakIcon) {
        this.speakID = speakID;
        this.speakIcon = speakIcon;
    }

    protected SpeakerEntity(Parcel in) {
        speakID = in.readString();
        speakIcon = in.readString();
    }

    public String getSpeakID() {
        return speakID;
    }

    public void setSpeakID(String speakID) {
        this.speakID = speakID;
    }

    public String getSpeakIcon() {
        return speakIcon;
    }

    public void setSpeakIcon(String speakIcon) {
        this.speakIcon = speakIcon;
    }

    /**
     * 解析数据集合
     * @param jsonArray
     * @return
     */
    public static List<SpeakerEntity> parseList(JSONArray jsonArray){
        if(null == jsonArray) return null;

        List<SpeakerEntity> sList = new ArrayList<>();

        for(int i=0 ;i <jsonArray.length() ;i++){
            JSONObject speakerJson = jsonArray.optJSONObject(i);
            sList.add(parseOneData(speakerJson));
        }

        return  sList;

    }
    /**
     * 解析发言者json
     * @param speakerJson
     * @return
     */
    public static SpeakerEntity parseOneData(JSONObject speakerJson){
        if(null == speakerJson) return null;

        SpeakerEntity se = new SpeakerEntity(speakerJson.optString("speak_id"),speakerJson.optString("icon"));
        return  se;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(speakID);
        dest.writeString(speakIcon);
    }

    public static final Creator<SpeakerEntity> CREATOR = new Creator<SpeakerEntity>() {
        @Override
        public SpeakerEntity createFromParcel(Parcel in) {
            return new SpeakerEntity(in);
        }

        @Override
        public SpeakerEntity[] newArray(int size) {
            return new SpeakerEntity[size];
        }
    };
}
