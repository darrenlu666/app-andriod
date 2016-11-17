package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * Created by codyy on 2015/8/11.
 */
public class LocationBean implements Parcelable {

    /**
     * areaId : da062823ce4742a2a247fef0585c8161
     * areaName : aaaaa
     */
    private String id;
    private String name;
    private String fetchType;
    private boolean isFinal;
    private String isDirect;
    private boolean isSchool;

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public String getIsDirect() {
        return isDirect;
    }

    public void setIsDirect(String isDirect) {
        this.isDirect = isDirect;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSchool() {
        return isSchool;
    }

    public void setIsSchool(boolean isSchool) {
        this.isSchool = isSchool;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public LocationBean() { }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeByte(isSchool ? (byte) 1 : (byte) 0);
    }

    protected LocationBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.isSchool = in.readByte() != 0;
    }

    public static final Creator<LocationBean> CREATOR = new Creator<LocationBean>() {
        public LocationBean createFromParcel(Parcel source) {
            return new LocationBean(source);
        }

        public LocationBean[] newArray(int size) {
            return new LocationBean[size];
        }
    };
}
