package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 16-3-9.
 */
public class GroupCollective extends RefreshEntity {

    /**
     * meetingId : 0c79d85bb4934a518b58c64406bc82cd
     * meetingTitle : null
     * realName : 国家学校
     * createUserId : d5e2b6b296db425f890e069fbb4403fd
     * headPic : 3ba2a4e4d6c24b46a26a935f51d77300_c9d064f724e04093a98b3f8b793f93f4.jpg
     * beginTime : null
     * duration : 0
     * publicFlag : Y
     * status : PROGRESS
     * serverAddress : http://10.5.225.19:8080/ResourceServer
     */

    private String meetingId;
    private String meetingTitle;
    private String realName;
    private String createUserId;
    private String headPic;
    private long beginTime;
    private long duration;
    private String publicFlag;
    private String status;
    private String serverAddress;
    private long createTime;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setPublicFlag(String publicFlag) {
        this.publicFlag = publicFlag;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public String getRealName() {
        return realName;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public String getHeadPic() {
        return headPic;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public long getDuration() {
        return duration;
    }

    public String getPublicFlag() {
        return publicFlag;
    }

    public String getStatus() {
        return status;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public GroupCollective() {
    }

    public static List<GroupCollective> getGroupCollective(JSONObject object) {
        ArrayList<GroupCollective> groupCollectives = new ArrayList<>();
        JSONObject jsonObject = object.optJSONObject("list");
        JSONArray array = jsonObject.optJSONArray("data");
        Gson gson = new Gson();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object1 = array.optJSONObject(i);
            GroupCollective groupCollective = gson.fromJson(object1.toString(), GroupCollective.class);
            groupCollectives.add(groupCollective);
        }
        return groupCollectives;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.meetingId);
        dest.writeString(this.meetingTitle);
        dest.writeString(this.realName);
        dest.writeString(this.createUserId);
        dest.writeString(this.headPic);
        dest.writeLong(this.beginTime);
        dest.writeLong(this.duration);
        dest.writeString(this.publicFlag);
        dest.writeString(this.status);
        dest.writeString(this.serverAddress);
        dest.writeLong(this.createTime);
    }

    protected GroupCollective(Parcel in) {
        super(in);
        this.meetingId = in.readString();
        this.meetingTitle = in.readString();
        this.realName = in.readString();
        this.createUserId = in.readString();
        this.headPic = in.readString();
        this.beginTime = in.readLong();
        this.duration = in.readLong();
        this.publicFlag = in.readString();
        this.status = in.readString();
        this.serverAddress = in.readString();
        this.createTime = in.readLong();
    }

    public static final Creator<GroupCollective> CREATOR = new Creator<GroupCollective>() {
        public GroupCollective createFromParcel(Parcel source) {
            return new GroupCollective(source);
        }

        public GroupCollective[] newArray(int size) {
            return new GroupCollective[size];
        }
    };
}
