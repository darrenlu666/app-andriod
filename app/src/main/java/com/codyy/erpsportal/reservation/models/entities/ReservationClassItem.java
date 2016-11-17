package com.codyy.erpsportal.reservation.models.entities;

import android.os.Parcel;
import android.view.View;

import com.codyy.erpsportal.commons.models.entities.RefreshEntity;
import com.codyy.erpsportal.reservation.controllers.activities.ReservationClassDetailActivity;

/**
 * Created by kmdai on 16-6-13.
 */
public class ReservationClassItem extends RefreshEntity {

    public final static int TYPE_CONTENT = REFRESH_TYPE_LASTVIEW + 1;
    /**
     * clsSchoolId : 3ba2a4e4d6c24b46a26a935f51d77300
     * headPic : 3ba2a4e4d6c24b46a26a935f51d77300_d12758338a1d406b9c1c84b3d9fe893e.jpg
     * masterCount : 6
     * reciveCount : null
     * schoolAreaPath : 中国
     * schoolName : 国家学校
     * serverAddress : http://10.1.70.15:8080/ResourceServer/images/3ba2a4e4d6c24b46a26a935f51d77300_d12758338a1d406b9c1c84b3d9fe893e.jpg
     */

    private String clsSchoolId;
    private String headPic;
    private int masterCount;
    private String reciveCount;
    private String schoolAreaPath;
    private String schoolName;
    private String serverAddress;


    /**
     * 点击事件
     *
     * @param view
     * @param item
     */
    public void onClick(View view, ReservationClassItem item) {
        ReservationClassDetailActivity.start(view.getContext(), item);
    }


    public String getClsSchoolId() {
        return clsSchoolId;
    }

    public void setClsSchoolId(String clsSchoolId) {
        this.clsSchoolId = clsSchoolId;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public int getMasterCount() {
        return masterCount;
    }

    public void setMasterCount(int masterCount) {
        this.masterCount = masterCount;
    }

    public String getReciveCount() {
        return reciveCount;
    }

    public void setReciveCount(String reciveCount) {
        this.reciveCount = reciveCount;
    }

    public String getSchoolAreaPath() {
        return schoolAreaPath;
    }

    public void setSchoolAreaPath(String schoolAreaPath) {
        this.schoolAreaPath = schoolAreaPath;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public ReservationClassItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.clsSchoolId);
        dest.writeString(this.headPic);
        dest.writeInt(this.masterCount);
        dest.writeString(this.reciveCount);
        dest.writeString(this.schoolAreaPath);
        dest.writeString(this.schoolName);
        dest.writeString(this.serverAddress);
    }

    protected ReservationClassItem(Parcel in) {
        super(in);
        this.clsSchoolId = in.readString();
        this.headPic = in.readString();
        this.masterCount = in.readInt();
        this.reciveCount = in.readString();
        this.schoolAreaPath = in.readString();
        this.schoolName = in.readString();
        this.serverAddress = in.readString();
    }

    public static final Creator<ReservationClassItem> CREATOR = new Creator<ReservationClassItem>() {
        @Override
        public ReservationClassItem createFromParcel(Parcel source) {
            return new ReservationClassItem(source);
        }

        @Override
        public ReservationClassItem[] newArray(int size) {
            return new ReservationClassItem[size];
        }
    };
}
