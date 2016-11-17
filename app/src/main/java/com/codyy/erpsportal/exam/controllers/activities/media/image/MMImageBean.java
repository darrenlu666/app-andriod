package com.codyy.erpsportal.exam.controllers.activities.media.image;

/**
 * @author lijian
 */

import android.os.Parcel;
import android.os.Parcelable;

public class MMImageBean implements Parcelable {

    private String path = null;
    private boolean isSeleted = false;
    private String thumbnails = null;

    public MMImageBean(String path, boolean selected, String thumbnails) {
        this.path = path;
        this.isSeleted = selected;
        this.thumbnails = thumbnails;
    }

    public MMImageBean() {
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the isSeleted
     */
    public boolean isSeleted() {
        return isSeleted;
    }

    /**
     * @param isSeleted the isSeleted to set
     */
    public void setSeleted(boolean isSeleted) {
        this.isSeleted = isSeleted;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeByte(isSeleted ? (byte) 1 : (byte) 0);
        dest.writeString(this.thumbnails);
    }

    protected MMImageBean(Parcel in) {
        this.path = in.readString();
        this.isSeleted = in.readByte() != 0;
        this.thumbnails = in.readString();
    }

    public static final Creator<MMImageBean> CREATOR = new Creator<MMImageBean>() {
        public MMImageBean createFromParcel(Parcel source) {
            return new MMImageBean(source);
        }

        public MMImageBean[] newArray(int size) {
            return new MMImageBean[size];
        }
    };

    @Override
    public String toString() {
        return "MMImageBean{" +
                "isSeleted=" + isSeleted +
                ", path='" + path + '\'' +
                ", thumbnails='" + thumbnails + '\'' +
                '}';
    }
}
