package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;

/**
 * Created by yangxinwu on 2015/8/26.
 */
public class MeetingConfig implements Parcelable {
    private String cipher;
    private String  from;
    private String  group;
    private String  enterpriseId;
    private String  serverType;
    private String  ip;
    private int  port;
    private String  uName;
    private String gid;
    private String license;
    private String mid;//会议ｉｄ
    private String userId;//当前用户的id.

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public static MeetingConfig getSimulateConfig(){
        MeetingConfig meetingConfig = new MeetingConfig();
        meetingConfig.setCipher("iujOOt");
        meetingConfig.setFrom("d7e09baadbb34615ad8260a6754811ee");
        meetingConfig.setGroup("3afb7a1acdd54a398cc260ef7ff1295f");
        meetingConfig.setServerType("0");
        meetingConfig.setEnterpriseId("1");
        meetingConfig.setuName("语文老师001");
        meetingConfig.setIp("coco.ppmeeting.com");
        meetingConfig.setPort(1888);
        meetingConfig.setGid("3afb7a1acdd54a398cc260ef7ff1295f");
        meetingConfig.setLicense("4boVotgMTKBQEBYKVEFb3OSOhkZGeJxT4isazQuwJZPL4GA6WH7zdg9MKA936Gvr");
        return meetingConfig;
    }

    public static MeetingConfig getSimulateConfig(MeetingBase meetingBase, UserInfo userInfo){
        MeetingConfig meetingConfig = new MeetingConfig();
        meetingConfig.setCipher(meetingBase.getBaseCoco().getCocoCiper());
        if (meetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_3) {
            meetingConfig.setFrom("watcher_"+userInfo.getBaseUserId());
        }else {
            meetingConfig.setFrom(userInfo.getBaseUserId());
        }
        /** V530　ｇｒｏｕｐ规则：ｍｉｄ的前六位的十六进制数值**/
        meetingConfig.setGroup(Integer.valueOf(meetingBase.getBaseMeetID().substring(0,6),16)+"");
        meetingConfig.setMid(meetingBase.getBaseMeetID());
        meetingConfig.setServerType("0");
        meetingConfig.setEnterpriseId("1");
        meetingConfig.setuName(userInfo.getRealName());
        meetingConfig.setIp(meetingBase.getBaseCoco().getCocoIP());
        int port = 1888;

        if(!TextUtils.isEmpty(meetingBase.getBaseCoco().getCocoPort())){
            port    =   Integer.parseInt(meetingBase.getBaseCoco().getCocoPort());
        }

        meetingConfig.setPort(port);
        meetingConfig.setGid(Integer.valueOf(meetingBase.getBaseMeetID().substring(0,6),16)+"");
        meetingConfig.setLicense(meetingBase.getBaseCoco().getCocoLicense());
        meetingConfig.setUserId(userInfo.getBaseUserId());//add by poe v5.3.8 20170921.
        return meetingConfig;
    }

    public MeetingConfig() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cipher);
        dest.writeString(this.from);
        dest.writeString(this.group);
        dest.writeString(this.enterpriseId);
        dest.writeString(this.serverType);
        dest.writeString(this.ip);
        dest.writeInt(this.port);
        dest.writeString(this.uName);
        dest.writeString(this.gid);
        dest.writeString(this.license);
        dest.writeString(this.mid);
        dest.writeString(this.userId);
    }

    protected MeetingConfig(Parcel in) {
        this.cipher = in.readString();
        this.from = in.readString();
        this.group = in.readString();
        this.enterpriseId = in.readString();
        this.serverType = in.readString();
        this.ip = in.readString();
        this.port = in.readInt();
        this.uName = in.readString();
        this.gid = in.readString();
        this.license = in.readString();
        this.mid = in.readString();
        this.userId=in.readString();
    }

    public static final Creator<MeetingConfig> CREATOR = new Creator<MeetingConfig>() {
        public MeetingConfig createFromParcel(Parcel source) {
            return new MeetingConfig(source);
        }

        public MeetingConfig[] newArray(int size) {
            return new MeetingConfig[size];
        }
    };
}
