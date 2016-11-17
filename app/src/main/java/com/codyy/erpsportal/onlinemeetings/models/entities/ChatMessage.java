package com.codyy.erpsportal.onlinemeetings.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

/**
 * Created by yangxinwu on 2015/8/19.
 */
public class ChatMessage extends BaseTitleItemBar implements Parcelable {
    public static int SINGLE_CHAT = 0x01;
    public static int GROUP_CHAT = 0x02;
    private String time;
    private String msg;
    private String from;//发消息人的ID
    private String to;//收消息人的ID
    private String name;
    private String headUrl;
    private int direct;
    private int chatType;
    private String id;
    private boolean hasUnReadMsg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public boolean isHasUnReadMsg() {
        return hasUnReadMsg;
    }

    public void setHasUnReadMsg(boolean hasUnReadMsg) {
        this.hasUnReadMsg = hasUnReadMsg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.time);
        dest.writeString(this.msg);
        dest.writeString(this.from);
        dest.writeString(this.to);
        dest.writeString(this.name);
        dest.writeString(this.headUrl);
        dest.writeInt(this.direct);
        dest.writeInt(this.chatType);
        dest.writeString(this.id);
        dest.writeByte(hasUnReadMsg ? (byte) 1 : (byte) 0);
    }

    public ChatMessage() {
    }

    protected ChatMessage(Parcel in) {
        this.time = in.readString();
        this.msg = in.readString();
        this.from = in.readString();
        this.to = in.readString();
        this.name = in.readString();
        this.headUrl = in.readString();
        this.direct = in.readInt();
        this.chatType = in.readInt();
        this.id = in.readString();
        this.hasUnReadMsg = in.readByte() != 0;
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };
}
