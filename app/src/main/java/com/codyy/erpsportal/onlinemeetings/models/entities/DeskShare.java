package com.codyy.erpsportal.onlinemeetings.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;

import org.json.JSONObject;

/**
 * 桌面共享
 * {@link UiOnlineMeetingUtils}.getDeskStream
 * Created by poe on 15-9-24.
 */
public class DeskShare implements Parcelable {
    /**
     * 0:关闭
     * 1:开启桌面共享
     */
    private int deskSwitch;
    /**
     * 共享桌面的id.
     * 用于拼接视频地址
     */
    private String deskID;


    public String getDeskID() {
        return deskID;
    }

    public void setDeskID(String deskID) {
        this.deskID = deskID;
    }

    public int getDeskSwitch() {
        return deskSwitch;
    }

    public void setDeskSwitch(int deskSwitch) {
        this.deskSwitch = deskSwitch;
    }

    public DeskShare() {
    }

    protected DeskShare(Parcel in) {
        deskSwitch = in.readInt();
        deskID = in.readString();
    }

    public static final Creator<DeskShare> CREATOR = new Creator<DeskShare>() {
        @Override
        public DeskShare createFromParcel(Parcel in) {
            return new DeskShare(in);
        }

        @Override
        public DeskShare[] newArray(int size) {
            return new DeskShare[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(deskSwitch);
        parcel.writeString(deskID);
    }

    public static DeskShare parseOneData(JSONObject dsJsonObject){
        if(null == dsJsonObject ) return  null;

        DeskShare ds = new DeskShare();

        ds.setDeskSwitch(dsJsonObject.optInt("desk_share"));
        ds.setDeskID(dsJsonObject.optString("desk_id"));

        return  ds;
    }
}
