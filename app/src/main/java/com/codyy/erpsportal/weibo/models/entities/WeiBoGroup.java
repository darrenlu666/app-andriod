package com.codyy.erpsportal.weibo.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.weibo.controllers.adapters.WeiBoMyFriendAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 16-3-3.
 */
public class WeiBoGroup extends WeiBoSearchPeople {

    /**
     * groupId : 20313ebc4f374c3a85370b1282eed694
     * groupName : 夜的第七章
     * groupPic : http://10.5.225.19:8080/ResourceServer/images/930f8743-422d-4a62-99a2-8c4210c9f0ce.jpg
     * serverAddress : http://10.5.225.19:8080/ResourceServer
     */

    private String groupId;
    private String groupName;
    private String groupPic;
    private String serverAddress;
    private boolean ischeck = false;

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupPic(String groupPic) {
        this.groupPic = groupPic;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupPic() {
        return groupPic;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public WeiBoGroup() {
    }

    public static List<WeiBoSearchPeople> getList(JSONObject object) {
        ArrayList<WeiBoSearchPeople> weiBoGroups = new ArrayList<>();
        if ("success".equals(object.optString("result"))) {
            JSONArray array = object.optJSONArray("list");
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object1 = array.optJSONObject(i);
                WeiBoGroup weiBoGroup = gson.fromJson(object1.toString(), WeiBoGroup.class);
                weiBoGroup.setmHolderType(WeiBoMyFriendAdapter.TYPE_MY_GROUP);
                weiBoGroups.add(weiBoGroup);
            }
        }
        return weiBoGroups;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.groupId);
        dest.writeString(this.groupName);
        dest.writeString(this.groupPic);
        dest.writeString(this.serverAddress);
        dest.writeByte(ischeck ? (byte) 1 : (byte) 0);
    }

    protected WeiBoGroup(Parcel in) {
        super(in);
        this.groupId = in.readString();
        this.groupName = in.readString();
        this.groupPic = in.readString();
        this.serverAddress = in.readString();
        this.ischeck = in.readByte() != 0;
    }

    public static final Parcelable.Creator<WeiBoGroup> CREATOR = new Parcelable.Creator<WeiBoGroup>() {
        public WeiBoGroup createFromParcel(Parcel source) {
            return new WeiBoGroup(source);
        }

        public WeiBoGroup[] newArray(int size) {
            return new WeiBoGroup[size];
        }
    };
}
