package com.codyy.erpsportal.weibo.models.entities;

import android.os.Parcel;

import com.codyy.erpsportal.commons.models.entities.RefreshEntity;
import com.codyy.erpsportal.weibo.controllers.fragments.WeiBoUniversalFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 16-1-26.
 */
public class WeiBoMessage extends RefreshEntity {

    /**
     * targetUserId : b42242a1f3184db0bb9c4c011b338879
     * targetRealName : demo
     * targetHeadPic : http://10.5.225.36:8080/ResourceServer/images/headPicDefault.jpg
     * blackUserId : null
     * messageContent : hahaha
     * readFlag : Y
     * createTime : 1453715920771
     * serverAddress : http://10.5.225.36:8080/ResourceServer
     */

    private String targetUserId;
    private String targetRealName;
    private String targetHeadPic;
    private String blackUserId;
    private String messageContent;
    private String readFlag;
    private long createTime;
    private String serverAddress;

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public void setTargetRealName(String targetRealName) {
        this.targetRealName = targetRealName;
    }

    public void setTargetHeadPic(String targetHeadPic) {
        this.targetHeadPic = targetHeadPic;
    }

    public void setBlackUserId(String blackUserId) {
        this.blackUserId = blackUserId;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public String getTargetRealName() {
        return targetRealName;
    }

    public String getTargetHeadPic() {
        return targetHeadPic;
    }

    public String getBlackUserId() {
        return blackUserId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * @param object
     * @return
     */
    public static ArrayList<WeiBoMessage> getWeiBoMessage(JSONObject object) {
        ArrayList<WeiBoMessage> weiBoMessages = new ArrayList<>();
        if ("success".equals(object.optString("result"))) {
            JSONArray array = object.optJSONArray("list");
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object1 = array.optJSONObject(i);
                WeiBoMessage weiBoMessage = gson.fromJson(object1.toString(), WeiBoMessage.class);
                weiBoMessage.setmHolderType(WeiBoUniversalFragment.TYPE_MY_MSG);
                weiBoMessages.add(weiBoMessage);
            }
            return weiBoMessages;
        }
        return weiBoMessages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.targetUserId);
        dest.writeString(this.targetRealName);
        dest.writeString(this.targetHeadPic);
        dest.writeString(this.blackUserId);
        dest.writeString(this.messageContent);
        dest.writeString(this.readFlag);
        dest.writeLong(this.createTime);
        dest.writeString(this.serverAddress);
    }

    public WeiBoMessage() {
    }

    protected WeiBoMessage(Parcel in) {
        super(in);
        this.targetUserId = in.readString();
        this.targetRealName = in.readString();
        this.targetHeadPic = in.readString();
        this.blackUserId = in.readString();
        this.messageContent = in.readString();
        this.readFlag = in.readString();
        this.createTime = in.readLong();
        this.serverAddress = in.readString();
    }

    public static final Creator<WeiBoMessage> CREATOR = new Creator<WeiBoMessage>() {
        @Override
        public WeiBoMessage createFromParcel(Parcel source) {
            return new WeiBoMessage(source);
        }

        @Override
        public WeiBoMessage[] newArray(int size) {
            return new WeiBoMessage[size];
        }
    };
}
