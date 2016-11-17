package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * 视频会议-轮巡
 * Created by poe on 15-9-24.
 */
public class LoopPatrol implements Parcelable{

    private int loopSwitch;//0 :正常 1：开启轮巡
    private String loopSpeak;//0 :保留发言人 1：关闭发言人

    public LoopPatrol() {
    }

    protected LoopPatrol(Parcel in) {
        loopSwitch = in.readInt();
        loopSpeak = in.readString();
    }

    public static final Creator<LoopPatrol> CREATOR = new Creator<LoopPatrol>() {
        @Override
        public LoopPatrol createFromParcel(Parcel in) {
            return new LoopPatrol(in);
        }

        @Override
        public LoopPatrol[] newArray(int size) {
            return new LoopPatrol[size];
        }
    };

    public int getLoopSwitch() {
        return loopSwitch;
    }

    public void setLoopSwitch(int loopSwitch) {
        this.loopSwitch = loopSwitch;
    }

    public String getLoopSpeak() {
        return loopSpeak;
    }

    public void setLoopSpeak(String loopSpeak) {
        this.loopSpeak = loopSpeak;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(loopSwitch);
        parcel.writeString(loopSpeak);
    }

    public static LoopPatrol parseOneData(JSONObject lpJsonObject){
        if(null == lpJsonObject ) return  null;

        LoopPatrol lp = new LoopPatrol();
        lp.setLoopSwitch(lpJsonObject.optInt("partrol_modle"));
        lp.setLoopSpeak(lpJsonObject.optString("speaker_author"));

        return  lp;
    }

}
