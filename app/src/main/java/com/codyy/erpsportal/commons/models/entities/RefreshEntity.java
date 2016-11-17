package com.codyy.erpsportal.commons.models.entities;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by kmdai on 15-12-11.
 */
public class RefreshEntity implements Parcelable {
    /**
     * 最后一个view的类型，集成的不要与此重复，避免错误
     */
    public final static int REFRESH_TYPE_LASTVIEW = 0xa01;
    /**
     *
     */
    private String mID;
    /**
     * holder类型
     */
    private int mHolderType;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public int getmHolderType() {
        return mHolderType;
    }

    public void setmHolderType(int mHolderType) {
        this.mHolderType = mHolderType;
    }


    public RefreshEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mID);
        dest.writeInt(this.mHolderType);
        dest.writeInt(this.position);
    }

    protected RefreshEntity(Parcel in) {
        this.mID = in.readString();
        this.mHolderType = in.readInt();
        this.position = in.readInt();
    }

}
