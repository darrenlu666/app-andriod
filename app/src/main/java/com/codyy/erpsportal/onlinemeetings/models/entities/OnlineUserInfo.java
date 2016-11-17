package com.codyy.erpsportal.onlinemeetings.models.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频会议-在线用户类
 * Created by poe on 15-8-10.
 */
public class OnlineUserInfo implements Comparable<OnlineUserInfo>  {

    private String id;//用户id
    /**
     * 用户头像
     */
    private String icon;
    /**
     * 用户名
     */
    private String name;
    /**
     * 角色 ：主讲人/发言人/参会者
     */
    private int role;
    private boolean isOnline;
    private String enterTime;//会议进入时间


    public OnlineUserInfo(String id, String icon, String name, @MeetingBase.MeetRole int role, boolean isOnline) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.role = role;
        this.isOnline = isOnline;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @MeetingBase.MeetRole
    public int getRole() {
        return role;
    }

    public void setRole(@MeetingBase.MeetRole int role) {
        this.role = role;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(String enterTime) {
        this.enterTime = enterTime;
    }


    /**
     * parse json to user list .
     * @param json
     * @return
     */
    public static List<OnlineUserInfo> parseList(JSONObject json){
        if(null == json) return  null;

        List<OnlineUserInfo> userList = new ArrayList<>();
        try{
            JSONArray jsonArray = json.getJSONArray("list");
            if(jsonArray!=null && jsonArray.length()>0){
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject userJson = (JSONObject)jsonArray.get(i);
                    userList.add(OnlineUserInfo.parseOneJson(userJson));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return  userList;
    }

    public static OnlineUserInfo parseOneJson(JSONObject userJson){
        if(null == userJson) return null;

        String userId   =   userJson.optString("id");
        String icon     =   userJson.optString("icon");
        String name     =   userJson.optString("name");

        int role = userJson.optInt("role",2);
        int baseRole;
        switch (role){
            case MeetingBase.BASE_MEET_ROLE_0:
                baseRole    =   MeetingBase.BASE_MEET_ROLE_0;
                break;
            case MeetingBase.BASE_MEET_ROLE_1:
                baseRole    =   MeetingBase.BASE_MEET_ROLE_1;
                break;
            case MeetingBase.BASE_MEET_ROLE_2:
                baseRole    =  MeetingBase.BASE_MEET_ROLE_2;
                break;
            case MeetingBase.BASE_MEET_ROLE_3://来宾也等同与参会者
//                baseRole    =   MeetingBase.BASE_MEET_ROLE_3;
                baseRole    =   MeetingBase.BASE_MEET_ROLE_2;
                break;
            case MeetingBase.BASE_MEET_ROLE_4:
                baseRole    =   MeetingBase.BASE_MEET_ROLE_4;
                break;
            default:
                baseRole = MeetingBase.BASE_MEET_ROLE_2;
                break;
        }

        OnlineUserInfo userInfo = new OnlineUserInfo(userId,icon,name,baseRole,false);

        return  userInfo;
    }


    private static int BIG = -1;
    private static int LITTLE = 1;

    @Override
    public int compareTo(OnlineUserInfo onlineUserInfo) {
        if(this.isOnline){//本人在线

            //如果比较对象不在线 return 1
            if(!onlineUserInfo.isOnline){
                return BIG;
                //如果都是在线的 并且 比较对象的权限高 return -1
            }else if(onlineUserInfo.getRole()<this.getRole()){
                return  LITTLE;
            //否则 return 1
            }else{
                return BIG;
            }
        }else if(onlineUserInfo.isOnline){//本人不在线

            //如果都不在线 正常顺序 return 1
            if(!onlineUserInfo.isOnline){
                return  BIG;
            }

            return LITTLE;
        }

        return 0;
    }
}
