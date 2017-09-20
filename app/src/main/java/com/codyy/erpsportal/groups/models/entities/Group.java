package com.codyy.erpsportal.groups.models.entities;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 单个圈组信息
 */
public class Group extends BaseTitleItemBar implements Serializable{
    /**
     * 通过
     */
    public static final String TYPE_OPERATE_APPROVE = "APPROVED";
    /**
     * 待审核
     */
    public static final String TYPE_OPERATE_PENDING = "WAIT";
    /**
     * 拒绝
     */
    public static final String TYPE_OPERATE_REJECT = "REJECT";
    /**
     * 兴趣组
     */
    public final static String TYPE_INTEREST = "INTEREST";//兴趣组
    /**
     * 教研组
     */
    public final static String TYPE_TEACHING = "TEACH";//教研组

    private String groupId;
    private String createTime;
    private String groupType;//"INTEREST"
    private String groupName;
    private String groupDesc;
    private String memberCount;//当前人数
    private String limited;//最大人数
    private String pic;//封面图片路径
    private String approveStatus;//APPROVED/WAIT/REJECT
    private String closedFlag;
    private String serverAddress;
    private String categoryName;//分类
    private String semesterName;//学段
    @SerializedName("classlevelName")
    private String grade;//年级
    private String subjectName;//学科
    @SerializedName("realName")
    private String groupCreator;//圈组创建者
    private String userType;//CREATOR/MANAGER/MEMBER 圈主/组长/组员
    private String joinStatus;//APPROVED/WAIT/REJECT
//    private String[] managerUsers;//
//    private String[] groupModules;//模块，数据暂时无用，@use 区分 RECOMMEND/TEACHING/INTEREST
    private String groupRecommendId;//推荐到门户的id

    public Group() {
    }

    public Group(String title ,int viewHoldType){
        setBaseTitle(title);
        setBaseViewHoldType(viewHoldType);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public String getLimited() {
        return limited;
    }

    public void setLimited(String limited) {
        this.limited = limited;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getClosedFlag() {
        return closedFlag;
    }

    public void setClosedFlag(String closedFlag) {
        this.closedFlag = closedFlag;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getGroupCreator() {
        return groupCreator;
    }

    public void setGroupCreator(String groupCreator) {
        this.groupCreator = groupCreator;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getJoinStatus() {
        return joinStatus;
    }

    public void setJoinStatus(String joinStatus) {
        this.joinStatus = joinStatus;
    }

/*    public String[] getManagerUsers() {
        return managerUsers;
    }

    public void setManagerUsers(String[] managerUsers) {
        this.managerUsers = managerUsers;
    }

    public String[] getGroupModules() {
        return groupModules;
    }

    public void setGroupModules(String[] groupModules) {
        this.groupModules = groupModules;
    }*/

    public String getGroupRecommendId() {
        return groupRecommendId;
    }

    public void setGroupRecommendId(String groupRecommendId) {
        this.groupRecommendId = groupRecommendId;
    }
}
