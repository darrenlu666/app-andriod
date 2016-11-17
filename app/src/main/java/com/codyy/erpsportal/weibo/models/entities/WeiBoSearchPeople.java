package com.codyy.erpsportal.weibo.models.entities;

import android.os.Parcel;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.models.entities.RefreshEntity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 16-1-20.
 */
public class WeiBoSearchPeople extends RefreshEntity {
    /**
     * baseUserId : null
     * fansUserId : null
     * realName : dasdasdasd
     * headPic : http://10.1.210.101/ResourceServer/images/headPicDefault.jpg
     * userType : STUDENT
     * followFlag : false
     * userId : 651446f93c354dd0bf410efc111f68d0
     * schoolName : 国家学校
     * baseClassName : 一年级ss
     */

    private String baseUserId;
    private String fansUserId;
    private String realName;
    private String headPic;
    private String userType;
    private boolean followFlag;
    private String userId;
    private String schoolName;
    private String baseClassName;

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public void setFansUserId(String fansUserId) {
        this.fansUserId = fansUserId;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setFollowFlag(boolean followFlag) {
        this.followFlag = followFlag;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setBaseClassName(String baseClassName) {
        this.baseClassName = baseClassName;
    }

    public String getBaseUserId() {
        return TextUtils.isEmpty(baseUserId) ? "" : baseUserId;
    }

    public String getFansUserId() {
        return fansUserId;
    }

    public String getRealName() {
        return TextUtils.isEmpty(realName) ? "" : realName;
    }

    public String getHeadPic() {
        return TextUtils.isEmpty(headPic) ? "" : headPic;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isFollowFlag() {
        return followFlag;
    }

    public String getUserId() {
        return TextUtils.isEmpty(userId) ? "" : userId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getBaseClassName() {
        return baseClassName;
    }

    public static List<WeiBoSearchPeople> getWeiBoSearchPeople(JSONObject object, int type) {
        ArrayList<WeiBoSearchPeople> weiBoSearchPeoples = null;
        if ("success".equals(object.optString("result"))) {
            weiBoSearchPeoples = new ArrayList<>();
            Gson gson = new Gson();
            JSONArray array = object.optJSONArray("list");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.optJSONObject(i);
//                System.out.println(object1.toString());
                    WeiBoSearchPeople weiBoSearchPeople = gson.fromJson(object1.toString(), WeiBoSearchPeople.class);
                    weiBoSearchPeople.setmHolderType(type);
                    weiBoSearchPeoples.add(weiBoSearchPeople);
                }
            }
        }
        return weiBoSearchPeoples;
    }

    public WeiBoSearchPeople() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.baseUserId);
        dest.writeString(this.fansUserId);
        dest.writeString(this.realName);
        dest.writeString(this.headPic);
        dest.writeString(this.userType);
        dest.writeByte(followFlag ? (byte) 1 : (byte) 0);
        dest.writeString(this.userId);
        dest.writeString(this.schoolName);
        dest.writeString(this.baseClassName);
    }

    protected WeiBoSearchPeople(Parcel in) {
        super(in);
        this.baseUserId = in.readString();
        this.fansUserId = in.readString();
        this.realName = in.readString();
        this.headPic = in.readString();
        this.userType = in.readString();
        this.followFlag = in.readByte() != 0;
        this.userId = in.readString();
        this.schoolName = in.readString();
        this.baseClassName = in.readString();
    }

    public static final Creator<WeiBoSearchPeople> CREATOR = new Creator<WeiBoSearchPeople>() {
        public WeiBoSearchPeople createFromParcel(Parcel source) {
            return new WeiBoSearchPeople(source);
        }

        public WeiBoSearchPeople[] newArray(int size) {
            return new WeiBoSearchPeople[size];
        }
    };
}
