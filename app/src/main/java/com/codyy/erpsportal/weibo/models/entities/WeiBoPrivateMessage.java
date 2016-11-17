package com.codyy.erpsportal.weibo.models.entities;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 16-1-27.
 */
public class WeiBoPrivateMessage {
    /**
     * titleview
     */
    public final static int TITLE_VIEW = 0x001;
    /**
     * 信息-左
     */
    public static final int MESSAGE_LEFT = 0x002;
    /**
     * 信息-右
     */
    public static final int MESSAGE_RIGHT = 0x003;
    /**
     * 左-图片
     */
    public static final int MESSAGE_LEFT_IMAGE = 0x004;
    /**
     * 右-图片
     */
    public static final int MESSAVE_RIGHT_IMAGE = 0x005;
    /**
     * miBlogMessageId : 1786006b5d0b480d8225dcd3c26a3412
     * targetUserId : f56441ec2e07480c8c92ef1428f24193
     * speakerUserId : f1d70ef8235047f896d8db49ae5c5455
     * messageContent : [睡]hhhhh
     * createTime : 1453802334873
     * isSelf : Y
     * readFlag : null
     */

    private String miBlogMessageId;
    private String targetUserId;
    private String speakerUserId;
    private String messageContent;
    private long createTime;
    private String isSelf;
    private String readFlag;
    private boolean hasMoreMiBlogMessage;
    private String speakerUserHeadPic;
    private String imagePath;
    private int mType;

    public String getSpeakerUserHeadPic() {
        return speakerUserHeadPic;
    }

    public void setSpeakerUserHeadPic(String speakerUserHeadPic) {
        this.speakerUserHeadPic = speakerUserHeadPic;
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public boolean isHasMoreMiBlogMessage() {
        return hasMoreMiBlogMessage;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setHasMoreMiBlogMessage(boolean hasMoreMiBlogMessage) {
        this.hasMoreMiBlogMessage = hasMoreMiBlogMessage;
    }

    public void setMiBlogMessageId(String miBlogMessageId) {
        this.miBlogMessageId = miBlogMessageId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public void setSpeakerUserId(String speakerUserId) {
        this.speakerUserId = speakerUserId;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setIsSelf(String isSelf) {
        this.isSelf = isSelf;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public String getMiBlogMessageId() {
        return miBlogMessageId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public String getSpeakerUserId() {
        return speakerUserId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getIsSelf() {
        return isSelf;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public static ArrayList<WeiBoPrivateMessage> getMessageList(JSONObject object) {
        ArrayList<WeiBoPrivateMessage> messages;
        if ("success".equals(object.optString("result"))) {
            messages = new ArrayList<>();
            JSONArray array = object.optJSONArray("data");
            if (array != null) {
                Gson gson = new Gson();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.optJSONObject(i);
                    WeiBoPrivateMessage weiBoPrivateMessage = gson.fromJson(object1.toString(), WeiBoPrivateMessage.class);
                    weiBoPrivateMessage.setHasMoreMiBlogMessage(object.optBoolean("hasMoreMiBlogMessage"));
                    if ("Y".equals(weiBoPrivateMessage.getIsSelf())) {
                        if (!TextUtils.isEmpty(weiBoPrivateMessage.getImagePath())) {
                            weiBoPrivateMessage.setType(MESSAVE_RIGHT_IMAGE);
                        } else {
                            weiBoPrivateMessage.setType(MESSAGE_RIGHT);
                        }
                        weiBoPrivateMessage.setSpeakerUserHeadPic(object.optString("speakerUserHeadPic"));
                    } else {
                        if (!TextUtils.isEmpty(weiBoPrivateMessage.getImagePath())) {
                            weiBoPrivateMessage.setType(MESSAGE_LEFT_IMAGE);
                        } else {
                            weiBoPrivateMessage.setType(MESSAGE_LEFT);
                        }
                        weiBoPrivateMessage.setSpeakerUserHeadPic(object.optString("targetUserHeadPic"));
                    }
                    messages.add(weiBoPrivateMessage);
                }
                return messages;
            }
            return null;
        }
        return null;
    }

    public static ArrayList<WeiBoPrivateMessage> getSendMessage(JSONObject object, String speakerUserHeadPic, String targetUserHeadPic) {
        if ("success".equals(object.optString("result"))) {
            Gson gson = new Gson();
            JSONArray array = object.optJSONArray("data");
            if (array != null) {
                ArrayList<WeiBoPrivateMessage> messages = new ArrayList<>(array.length());
                for (int i = 0; i < array.length(); i++) {
                    WeiBoPrivateMessage weiBoPrivateMessage = gson.fromJson(array.optJSONObject(i).toString(), WeiBoPrivateMessage.class);
                    if (weiBoPrivateMessage != null) {
                        messages.add(weiBoPrivateMessage);
                        if ("Y".equals(weiBoPrivateMessage.getIsSelf())) {
                            if (!TextUtils.isEmpty(weiBoPrivateMessage.getImagePath())) {
                                weiBoPrivateMessage.setType(MESSAVE_RIGHT_IMAGE);
                            } else {
                                weiBoPrivateMessage.setType(MESSAGE_RIGHT);
                            }
                            weiBoPrivateMessage.setSpeakerUserHeadPic(speakerUserHeadPic);
                        } else {
                            if (!TextUtils.isEmpty(weiBoPrivateMessage.getImagePath())) {
                                weiBoPrivateMessage.setType(MESSAGE_LEFT_IMAGE);
                            } else {
                                weiBoPrivateMessage.setType(MESSAGE_LEFT);
                            }
                            weiBoPrivateMessage.setSpeakerUserHeadPic(targetUserHeadPic);
                        }
                    }
                }
                return messages;
            }
        }
        return null;
    }
}
