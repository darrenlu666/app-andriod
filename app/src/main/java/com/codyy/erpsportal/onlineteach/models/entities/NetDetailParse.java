package com.codyy.erpsportal.onlineteach.models.entities;

import java.util.List;

/**
 * 网络授课－详情解析类 .　
 * Created by poe on 16-8-15.
 */
public class NetDetailParse {
    private String result;
    private String userType;
    private boolean hasVideo;//结束后判断是否拥有视频文件.
    private NetTeachDetail data;
    private NetPermission permission;
    private NetMasterSchool masterSchool;
    private List<NetParticipator> participator;
    private List<NetDocument> interrelatedDoc;

    public boolean isHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public List<NetParticipator> getParticipator() {
        return participator;
    }

    public void setParticipator(List<NetParticipator> participator) {
        this.participator = participator;
    }

    public List<NetDocument> getInterrelatedDoc() {
        return interrelatedDoc;
    }

    public void setInterrelatedDoc(List<NetDocument> interrelatedDoc) {
        this.interrelatedDoc = interrelatedDoc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public NetTeachDetail getData() {
        return data;
    }

    public void setData(NetTeachDetail data) {
        this.data = data;
    }

    public NetPermission getPermission() {
        return permission;
    }

    public void setPermission(NetPermission permission) {
        this.permission = permission;
    }

    public NetMasterSchool getMasterSchool() {
        return masterSchool;
    }

    public void setMasterSchool(NetMasterSchool masterSchool) {
        this.masterSchool = masterSchool;
    }
}
