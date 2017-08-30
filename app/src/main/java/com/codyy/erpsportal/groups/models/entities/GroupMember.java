package com.codyy.erpsportal.groups.models.entities;


import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 单个-圈组成员实体类
 * Created by poe on 16-3-7.
 */
public class GroupMember extends BaseTitleItemBar {
    /**
     * 创建者
     */
    public static final String ROLE_CREATOR = "CREATOR";
    /**
     * 管理者
     */
    public static final String ROLE_MANAGER = "MANAGER";
    /**
     * 普通成员
     */
    public static final String ROLE_MEMBER = "MEMBER";

    private String createTime;
    private String groupUserId;
    private String groupName;
    private String schoolName;
    private String areaPath;
    private String approveStatus;
    private String joinReason;
    private String realName;
    private String baseUserId;
    private String  headPic;
    private String userType;//CREATOR/MANAGER/MEMBER 圈主/组长/组员

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGroupUserId() {
        return groupUserId;
    }

    public void setGroupUserId(String groupUserId) {
        this.groupUserId = groupUserId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getAreaPath() {
        return areaPath;
    }

    public void setAreaPath(String areaPath) {
        this.areaPath = areaPath;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getJoinReason() {
        return joinReason;
    }

    public void setJoinReason(String joinReason) {
        this.joinReason = joinReason;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
