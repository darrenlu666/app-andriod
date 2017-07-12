package com.codyy.erpsportal.groups.models.entities;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 圈组空间-基本信息
 * Created by poe on 16-2-1.
 */
public class GroupSpace extends BaseTitleItemBar implements Serializable {
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
     * 申请加入 ：未加入 或 被拒绝
     */
    public static final int GROUP_OPERATE_TYPE_PROMPT = 0x01;
    /**
     * 退出 : 通过后
     */
    public static final int GROUP_OPERATE_TYPE_EXIT = 0x02;
    /**
     * 审核中 : 已申请 正在审批中 ...
     */
    public static final int GROUP_OPERATE_TYPE_WAIT = 0x03;

    private String groupId;
    private String createTime;
    private String groupType;//"INTEREST"
    private String groupName;
    private String groupDesc;
    @SerializedName("realName")
    private String groupCreator;//圈组创建者
    private String areaPath;
    private String schoolName;
    private String publishNotice;//公告
    private String memberCount;//当前人数
    private String limited;//最大人数
    private String pic;//封面图片路径
    private String approveStatus;//APPROVED/WAIT/REJECT
    private String serverAddress;
    private String categoryName;//分类
    private String semesterName;//学段
    @SerializedName("classlevelName")
    private String grade;//年级
    private String subjectName;//学科
    private String userType;//CREATOR/MANAGER/MEMBER 圈主/组长/组员
    private String moduleList;//GROUP_BLOG_INTEREST
    @SerializedName("list")
    private ModuleParse moduleData ;
    /**
     * {@link #GROUP_OPERATE_TYPE_PROMPT}
     */
    private int operateType ;//add by poe 操作类型 .

    public String getAreaPath() {
        return areaPath;
    }

    public void setAreaPath(String areaPath) {
        this.areaPath = areaPath;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
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

    public String getGroupCreator() {
        return groupCreator;
    }

    public void setGroupCreator(String groupCreator) {
        this.groupCreator = groupCreator;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getPublishNotice() {
        return publishNotice;
    }

    public void setPublishNotice(String publishNotice) {
        this.publishNotice = publishNotice;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getModuleList() {
        return moduleList;
    }

    public void setModuleList(String moduleList) {
        this.moduleList = moduleList;
    }

    public ModuleParse getModuleData() {
        return moduleData;
    }

    public void setModuleData(ModuleParse moduleData) {
        this.moduleData = moduleData;
    }
}
