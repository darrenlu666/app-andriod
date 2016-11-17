package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.codyy.erpsportal.commons.utils.Cog;

import org.json.JSONObject;

import java.net.URLDecoder;

/**
 * 远程导播配置，进入导播时获取
 * Created by gujiajia on 2015/7/2.
 */
public class RemoteDirectorConfig implements Parcelable {

    private final static String TAG = "RemoteDirectorConfig";

    private String mid;

    private String uid;

    private String pmsRemoteUrl;

    private String mainSpeak;

    private String uName;

    private String mainSpeakName;

    /**
     * coco服务器ip
     */
    private String ip;

    /**
     * coco服务器端口
     */
    private int port;

    private String mid2;

    private String cipher;

    private String enterpriseId;

    private String serverType;

    private String license;

    public RemoteDirectorConfig() {
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPmsRemoteUrl() {
        return pmsRemoteUrl;
    }

    public void setPmsRemoteUrl(String pmsRemoteUrl) {
        this.pmsRemoteUrl = pmsRemoteUrl;
    }

    public String getMainSpeak() {
        return mainSpeak;
    }

    public void setMainSpeak(String mainSpeak) {
        this.mainSpeak = mainSpeak;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getMainSpeakName() {
        return mainSpeakName;
    }

    public void setMainSpeakName(String mainSpeakName) {
        this.mainSpeakName = mainSpeakName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMid2() {
        return mid2;
    }

    public void setMid2(String mid2) {
        this.mid2 = mid2;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public static RemoteDirectorConfig parse(@NonNull JSONObject jsonObject) {
        RemoteDirectorConfig config = new RemoteDirectorConfig();
        Cog.e("RemoteDirectorConfig", jsonObject.toString());
        config.mid= Integer.valueOf(jsonObject.optString("MID").substring(0,6),16).toString();
        config.mid2=jsonObject.optString("MID");
        Cog.e("RemoteDirectorConfig mid",config.mid);
        config.uid = jsonObject.optString("UID");
        if (!jsonObject.isNull("PMS_REMOTE_URL")) {
            config.pmsRemoteUrl = jsonObject.optString("PMS_REMOTE_URL");
        }
        Cog.d(TAG, "parse config.pmsRemoteUrl=" + config.getPmsRemoteUrl());
        config.mainSpeak = jsonObject.optString("MAIN_SPEAK");
        config.uName = jsonObject.optString("UNAME");
        config.mainSpeakName = jsonObject.optString("MAIN_SPEAK_NAME");
        config.ip = jsonObject.optString("IP");
        config.port = jsonObject.optInt("PORT");
        config.cipher = jsonObject.optString("cipher");
        config.enterpriseId = jsonObject.optString("enterpriseId");
        config.serverType = jsonObject.optString("serverType");
        config.license = jsonObject.optString("License");
        return config;
    }
    /**
     * 获取模拟配置
     * @return
     */
    public static RemoteDirectorConfig getSimulateConfig() {
        RemoteDirectorConfig config = new RemoteDirectorConfig();
        config.setMid("14975539");
        config.setUid("remote_admin_e48233198d684529a12ba333ee64359f");
        config.setPmsRemoteUrl("rtmp://172.17.53.18/pms");
        config.setMainSpeak("c5c1f6512c2340eabd8f5e85b8b15fe7");
        config.setuName("");
        config.setMainSpeakName("guwei");
        config.setIp("coco.ppmeeting.com");
        config.setPort(1888);
        config.setCipher("v7wPP4");
        config.setEnterpriseId("1");
        config.setServerType("0");
        config.setLicense("4boVotgMTKBQEBYKVEFb3OSOhkZGeJxT4isazQuwJZPL4GA6WH7zdg9MKA936Gvr");
        return config;
    }
    /**
     * 获取主的流地址
     *
     * @return
     */
    public String getMainStreamUrl() {
        StringBuilder sb = new StringBuilder(pmsRemoteUrl);
        sb.append('/').append("class_").append(mainSpeak)
                .append("_u_").append(mid2).append("_0__main");
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mid);
        dest.writeString(this.uid);
        dest.writeString(this.pmsRemoteUrl);
        dest.writeString(this.mainSpeak);
        dest.writeString(this.uName);
        dest.writeString(this.mainSpeakName);
        dest.writeString(this.ip);
        dest.writeInt(this.port);
        dest.writeString(this.mid2);
        dest.writeString(this.cipher);
        dest.writeString(this.enterpriseId);
        dest.writeString(this.serverType);
        dest.writeString(this.license);
    }

    protected RemoteDirectorConfig(Parcel in) {
        this.mid = in.readString();
        this.uid = in.readString();
        this.pmsRemoteUrl = in.readString();
        this.mainSpeak = in.readString();
        this.uName = in.readString();
        this.mainSpeakName = in.readString();
        this.ip = in.readString();
        this.port = in.readInt();
        this.mid2 = in.readString();
        this.cipher = in.readString();
        this.enterpriseId = in.readString();
        this.serverType = in.readString();
        this.license = in.readString();
    }

    public static final Creator<RemoteDirectorConfig> CREATOR = new Creator<RemoteDirectorConfig>() {
        public RemoteDirectorConfig createFromParcel(Parcel source) {
            return new RemoteDirectorConfig(source);
        }

        public RemoteDirectorConfig[] newArray(int size) {
            return new RemoteDirectorConfig[size];
        }
    };

    @Override
    public String toString() {
        return "RemoteDirectorConfig{" +
                "cipher='" + cipher + '\'' +
                ", mid='" + mid + '\'' +
                ", uid='" + uid + '\'' +
                ", pmsRemoteUrl='" + pmsRemoteUrl + '\'' +
                ", mainSpeak='" + mainSpeak + '\'' +
                ", uName='" + uName + '\'' +
                ", mainSpeakName='" + mainSpeakName + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", mid2='" + mid2 + '\'' +
                ", enterpriseId='" + enterpriseId + '\'' +
                ", serverType='" + serverType + '\'' +
                ", license='" + license + '\'' +
                '}';
    }

    public static void main(String[] args) {
        System.out.print(URLDecoder.decode("14eaa9b0f98941a19baf83c905f18c9f"));
    }
}
