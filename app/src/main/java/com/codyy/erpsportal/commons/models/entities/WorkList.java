package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 老师作业列表model
 * Created by ldh on 2015/12/24.
 */
public class WorkList extends RefreshEntity implements Parcelable {
    @Override
    public String getmID() {
        return super.getmID();
    }

    @Override
    public void setmID(String mID) {
        super.setmID(mID);
    }

    @Override
    public int getmHolderType() {
        return super.getmHolderType();
    }

    @Override
    public void setmHolderType(int mHolderType) {
        super.setmHolderType(mHolderType);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public WorkList() {
        super();
    }

    protected WorkList(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<WorkList> CREATOR = new Parcelable.Creator<WorkList>() {
        public WorkList createFromParcel(Parcel source) {
            return new WorkList(source);
        }

        public WorkList[] newArray(int size) {
            return new WorkList[size];
        }
    };
}
