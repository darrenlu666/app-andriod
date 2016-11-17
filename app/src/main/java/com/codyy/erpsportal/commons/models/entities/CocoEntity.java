package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * cocoa connection info ...
 * Created by poe on 15-8-14.
 */
public class CocoEntity implements Parcelable{

    private String cocoIP;
    private String cocoPort;
    private String cocoCiper;
    private String cocoLicense;

    public CocoEntity() {
    }


    public CocoEntity(String cocoIP, String cocoPort, String cocoCiper, String cocoLicense) {
        this.cocoIP = cocoIP;
        this.cocoPort = cocoPort;
        this.cocoCiper = cocoCiper;
        this.cocoLicense = cocoLicense;
    }

    public String getCocoIP() {
        return cocoIP;
    }

    public void setCocoIP(String cocoIP) {
        this.cocoIP = cocoIP;
    }

    public String getCocoPort() {
        return cocoPort;
    }

    public void setCocoPort(String cocoPort) {
        this.cocoPort = cocoPort;
    }

    public String getCocoCiper() {
        return cocoCiper;
    }

    public void setCocoCiper(String cocoCiper) {
        this.cocoCiper = cocoCiper;
    }

    public String getCocoLicense() {
        return cocoLicense;
    }

    public void setCocoLicense(String cocoLicense) {
        this.cocoLicense = cocoLicense;
    }

    /**
     * 解析聊天服务地址信息 .
     * @param cocoJson
     * @return
     */
    public static CocoEntity parseJson(JSONObject cocoJson) {

        if (null == cocoJson) return null;

        CocoEntity ce = new CocoEntity();
        ce.setCocoIP(cocoJson.optString("coco_ip"));
        ce.setCocoPort(cocoJson.optString("coco_port"));
        ce.setCocoCiper(cocoJson.optString("coco_ciper"));
        ce.setCocoLicense(cocoJson.optString("coco_license"));

        return ce;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cocoIP);
        dest.writeString(cocoPort);
        dest.writeString(cocoCiper);
        dest.writeString(cocoLicense);
    }

    public static final Parcelable.Creator<CocoEntity> CREATOR = new Parcelable.Creator<CocoEntity>() {

        @Override
        public CocoEntity createFromParcel(Parcel in) {

            CocoEntity part = new CocoEntity();
            part.setCocoIP(in.readString());
            part.setCocoPort(in.readString());
            part.setCocoCiper(in.readString());
            part.setCocoLicense(in.readString());

            return part;
        }

        @Override
        public CocoEntity[] newArray(int size) {

            return new CocoEntity[size];
        }
    };

}
