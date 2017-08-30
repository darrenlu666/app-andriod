package com.codyy.erpsportal.commons.models.entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.models.dao.UserInfoDao;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.groups.models.entities.Group;
import com.codyy.erpsportal.commons.models.personal.Student;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by gujiajia on 2015/4/10.
 */
public class UserInfo implements Parcelable {

    public static final String USER_TYPE_AREA_USER = "AREA_USR";// 行政机构用户

    public static final String USER_TYPE_SCHOOL_USER = "SCHOOL_USR";// 学校用户

    public static final String USER_TYPE_TEACHER = "TEACHER";// 教师

    public static final String USER_TYPE_STUDENT = "STUDENT";// 学生

    public static final String USER_TYPE_PARENT = "PARENT";// 家长

    public static final String TEAM_TYPE_TEACH = "TEACH" ;//教研组

    public static final String TEAM_TYPE_INTEREST = "INTEREST";//兴趣组
    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户id ::用于coco聊天和视频会议
     */
    private String baseUserId;

    /**
     * uuid Mobile:xxxsaf888f88f8jk . 基本http数据请求.
     */
    private String uuid;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 职位名称
     */
    private String position;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 用户类型
     */
    private String remark;

    /**
     * 所在地区id
     */
    private String baseAreaId;

    private String areaName;

    private String areaCode;

    private String levelName;

    /**
     * 是否是超级管理员
     */
    @SerializedName("adminFlag")
    private boolean isAdmin;

    /**
     * 用户真实名
     */
    private String realName;

    /**
     * 用户学校id
     */
    private String schoolId;

    private String schoolName;

    /**
     * 用户头像url
     */
    private String headPic;

    private String subjectNames;

    private String classLevelNames;

    /**
     * 授予权限
     */
    private String permGrant;

    /**
     * 年级
     */
    private String classlevelName;
    /**
     * 班级id
     */
    private String baseClassId;
    /**
     * 班级名称
     */
    private String baseClassName;
    /**
     * 是否能发起评课
     */
    private String evaFlag;
    /**
     * 是否有发起互动听课功能，Y:有，其他:无
     */
    private String interactiveListenFlag;
    /**
     * 是否有发起集体备课功能，Y:有，其他:无
     */
    private String groupPreparationFlag;
    /**
     * 是否有发起视频会议功能，Y:有，其他:无
     */
    private String videoConferenceFlag;

    /**
     * 是否有权限创建网络授课，Y:有，其他:无
     */
    private String netTeachingFlag;

    private String validateCode;
    private String serverAddress;
    private String areaPath;

    /**
     * 上级区域id，不是家长id哦
     */
    private String parentId;

    /**
     * 加入的组别 【INTEREST/TEACH】
     */
    private String groupCategoryConfig;//加入的组别 【INTEREST/TEACH】

    /**
     * 选中的孩子，（仅家长账号下可用）
     */
    private Student selectedChild ;

    private List<Student> childList ;

    public String getGroupCategoryConfig() {
        return groupCategoryConfig;
    }

    public void setGroupCategoryConfig(String groupCategoryConfig) {
        this.groupCategoryConfig = groupCategoryConfig;
    }

    /**
     * 老师账号下-任课的班级列表
     */
    private List<ClassCont> classList ;

    public List<ClassCont> getClassList() {
        return classList;
    }

    public void setClassList(List<ClassCont> classList) {
        this.classList = classList;
    }

    public List<Student> getChildList() {
        return childList;
    }

    public void setChildList(List<Student> childList) {
        this.childList = childList;
    }

    public Student getSelectedChild() {
        return selectedChild;
    }

    public void setSelectedChild(Student selectedChild) {
        this.selectedChild = selectedChild;
    }

    public UserInfo() {
    }

    /**
     * 判断是否拥有对应类型的圈组 .
     * @param teamType
     * @return
     */
    public boolean isHasTeam(String teamType) {
        boolean hasTeam = false;
        if(Group.TYPE_INTEREST.equals(teamType)){
            teamType = "hasInterestGroupFlag";
        }else if(Group.TYPE_TEACHING.equals(teamType)){
            teamType = "hasTeacheringGroupFlag";
        }
        if(null != groupCategoryConfig && groupCategoryConfig.length() >0){
                if(groupCategoryConfig.contains(teamType)){
                    hasTeam = true;
                }
        }
        return hasTeam;
    }

    public String getAreaPath() {
        return areaPath;
    }

    public void setAreaPath(String areaPath) {
        this.areaPath = areaPath;
    }

    public String getBaseClassName() {
        return baseClassName;
    }

    public void setBaseClassName(String baseClassName) {
        this.baseClassName = baseClassName;
    }

    public String getBaseClassId() {
        return baseClassId;
    }

    public void setBaseClassId(String baseClassId) {
        this.baseClassId = baseClassId;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public String getEvaFlag() {
        return evaFlag;
    }

    public void setEvaFlag(String evaFlag) {
        this.evaFlag = evaFlag;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBaseAreaId() {
        return baseAreaId;
    }

    public void setBaseAreaId(String baseAreaId) {
        this.baseAreaId = baseAreaId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSubjectNames() {
        return subjectNames;
    }

    public void setSubjectNames(String subjectNames) {
        this.subjectNames = subjectNames;
    }

    public String getClassLevelNames() {
        return classLevelNames;
    }

    public void setClassLevelNames(String classLevelNames) {
        this.classLevelNames = classLevelNames;
    }

    public String getPermGrant() {
        return permGrant;
    }

    public void setPermGrant(String permGrant) {
        this.permGrant = permGrant;
    }

    public String getInteractiveListenFlag() {
        return interactiveListenFlag;
    }

    public void setInteractiveListenFlag(String interactiveListenFlag) {
        this.interactiveListenFlag = interactiveListenFlag;
    }

    public String getGroupPreparationFlag() {
        return groupPreparationFlag;
    }

    public void setGroupPreparationFlag(String groupPreparationFlag) {
        this.groupPreparationFlag = groupPreparationFlag;
    }

    public String getVideoConferenceFlag() {
        return videoConferenceFlag;
    }

    public void setVideoConferenceFlag(String videoConferenceFlag) {
        this.videoConferenceFlag = videoConferenceFlag;
    }

    public String getNetTeachingFlag() {
        return netTeachingFlag;
    }

    public void setNetTeachingFlag(String netTeachingFlag) {
        this.netTeachingFlag = netTeachingFlag;
    }

    public String getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isSchool() {
        return USER_TYPE_SCHOOL_USER.equals(userType);
    }

    public boolean isArea() {
        return USER_TYPE_AREA_USER.equals(userType);
    }

    public boolean isNation() {
        return isArea() && TextUtils.isEmpty(parentId);
    }

    public boolean isTeacher() {
        return USER_TYPE_TEACHER.equals(userType);
    }

    public boolean isStudent() {
        return USER_TYPE_STUDENT.equals(userType);
    }

    public boolean isParent() {
        return USER_TYPE_PARENT.equals(userType);
    }

    /**
     * 地区或学校管理员
     * @return
     */
    public boolean isManager() {
        return isSchool() || isArea();
    }

    public static boolean checkIsManager(String userType) {
        return USER_TYPE_AREA_USER.equals(userType) || USER_TYPE_SCHOOL_USER.equals(userType);
    }

    /**
     * 是否有课程表权限
     *
     * @return
     */
    public boolean hasTimeTablePermission() {
        return getPermGrant() != null && getPermGrant().contains(",1,");
    }

    /**
     * 是否有直播课堂权限
     *
     * @return
     */
    public boolean hasLiveClassPermission() {
        return getPermGrant() != null && getPermGrant().contains(",2,");
    }

    /**
     * 是否有课堂巡视权限
     *
     * @return
     */
    public boolean hasClassTourPermission() {
        return getPermGrant() != null && getPermGrant().contains(",3,");
    }

    /**
     * 是否有评课议课权限
     */
    public boolean hasEvaluationPermission() {
        return getPermGrant() != null && getPermGrant().contains(",5,");
    }

    /**
     * 是否有教学资源权限
     *
     * @return
     */
    public boolean hasResourcePermission() {
        return getPermGrant() != null && getPermGrant().contains(",4,");
    }

    /**
     * 是否有统计分析权限
     *
     * @return
     */
    public boolean hasStatisticPermission() {
        return getPermGrant() != null && getPermGrant().contains(",6,");
    }

    @Override
    public String toString() {
        return userName + "," + userType + "," + realName;
    }

    public static UserInfo parseJson(JSONObject jsonObject) {
        return PARSER.parse(jsonObject);
    }

    private final static JsonParser<UserInfo> PARSER = new JsonParser<UserInfo>() {
        @Override
        public UserInfo parse(JSONObject jsonObject) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUuid( jsonObject.optString("uuid"));
            userInfo.setUserName( optNoHtmlOrNull(jsonObject, "userName"));
            userInfo.setBaseUserId( jsonObject.optString("baseUserId"));
            userInfo.setPosition( optStrOrNull(jsonObject, "position"));
            userInfo.setContactPhone( optStrOrNull(jsonObject, "contactPhone"));
            userInfo.setRemark( optStrOrNull(jsonObject, "remark"));
            userInfo.setBaseAreaId( optStrOrNull(jsonObject, "baseAreaId"));
            userInfo.setAreaName( optNoHtmlOrNull( jsonObject, "areaName"));
            userInfo.setAreaCode( optStrOrNull( jsonObject, "areaCode"));
            userInfo.setLevelName( optNoHtmlOrNull(jsonObject,"levelName"));
            userInfo.setIsAdmin("Y".equals(jsonObject.optString("adminFlag")));
            userInfo.setRealName( optNoHtmlOrNull(jsonObject, "realName"));
            userInfo.setSchoolId( optStrOrNull(jsonObject, "schoolId"));
            userInfo.setSchoolName( optNoHtmlOrNull(jsonObject, "schoolName"));
            userInfo.setHeadPic( optStrOrNull(jsonObject, "headPic"));
            userInfo.setUserType( optStrOrNull(jsonObject, "userType"));
            userInfo.setSubjectNames( optNoHtmlOrNull(jsonObject, "subjectNames"));
            userInfo.setClassLevelNames( optNoHtmlOrNull(jsonObject, "classLevelNames"));
            userInfo.setEvaFlag( jsonObject.optString("evaFlag"));
            userInfo.setPermGrant( jsonObject.optString("permGrant"));
            userInfo.setInteractiveListenFlag( jsonObject.optString("interactiveListenFlag"));
            userInfo.setGroupPreparationFlag( jsonObject.optString("groupPreparationFlag"));
            userInfo.setVideoConferenceFlag( jsonObject.optString("videoConferenceFlag"));
            userInfo.setNetTeachingFlag( jsonObject.optString("netTeachFlag"));
            userInfo.setClasslevelName( optNoHtmlOrNull(jsonObject,"classlevelName"));
            userInfo.setBaseClassId( optStrOrNull(jsonObject, "baseClassId"));
            userInfo.setBaseClassName( optNoHtmlOrNull(jsonObject, "baseClassName"));
            userInfo.setValidateCode( jsonObject.optString("validateCode"));
            userInfo.setServerAddress( jsonObject.optString("serverAddress"));
            userInfo.setAreaPath( optStrOrNull(jsonObject, "areaPath"));
            userInfo.setParentId( optStrOrNull(jsonObject, "parentId"));
//            userInfo.setTeams();
            JSONArray array = jsonObject.optJSONArray("groupCategoryConfig");
            if(array != null && array.length() >0){
                StringBuffer sb = new StringBuffer();
                for(int i = 0 ;i < array.length();i++){
                    sb.append(array.optString(i));
                }
                userInfo.setGroupCategoryConfig(sb.toString());
            }

            return userInfo;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public UserInfo(Parcel src) {
        this.uuid = src.readString();
        this.baseUserId = src.readString();
        this.userName = src.readString();
        this.position = src.readString();
        this.contactPhone = src.readString();
        this.remark = src.readString();
        this.baseAreaId = src.readString();
        this.areaName = src.readString();
        this.areaCode = src.readString();
        this.levelName = src.readString();
        this.isAdmin = src.readInt() == 1;
        this.realName = src.readString();
        this.headPic = src.readString();
        this.userType = src.readString();
        this.permGrant = src.readString();
        if (!isArea()) {
            this.schoolId = src.readString();
            this.schoolName = src.readString();
        }
        if (isTeacher()) {
            this.subjectNames = src.readString();
            this.classLevelNames = src.readString();
            this.evaFlag = src.readString();
            this.interactiveListenFlag = src.readString();
            this.groupPreparationFlag = src.readString();
            this.videoConferenceFlag = src.readString();
            this.netTeachingFlag =src.readString();
        }
        this.classlevelName = src.readString();
        this.baseClassId = src.readString();
        this.baseClassName = src.readString();
        this.validateCode = src.readString();
        this.serverAddress = src.readString();
        this.areaPath = src.readString();
        this.parentId = src.readString();
        this.groupCategoryConfig    =   src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getUuid());
        dest.writeString(getBaseUserId());
        dest.writeString(getUserName());
        dest.writeString(getPosition());
        dest.writeString(getContactPhone());
        dest.writeString(getRemark());
        dest.writeString(getBaseAreaId());
        dest.writeString(getAreaName());
        dest.writeString(getAreaCode());
        dest.writeString(getLevelName());
        dest.writeInt(isAdmin() ? 1 : 0);
        dest.writeString(getRealName());
        dest.writeString(getHeadPic());
        dest.writeString(getUserType());
        dest.writeString(getPermGrant());

        if (!isArea()) {
            dest.writeString(getSchoolId());
            dest.writeString(getSchoolName());
        }

        if (isTeacher()) {
            dest.writeString(getSubjectNames());
            dest.writeString(getClassLevelNames());
            dest.writeString(this.evaFlag);
            dest.writeString(this.interactiveListenFlag);
            dest.writeString(this.groupPreparationFlag);
            dest.writeString(this.videoConferenceFlag);
            dest.writeString(this.netTeachingFlag);
        }
        dest.writeString(this.classlevelName);
        dest.writeString(this.baseClassId);
        dest.writeString(this.baseClassName);
        dest.writeString(this.validateCode);
        dest.writeString(this.serverAddress);
        dest.writeString(this.areaPath);
        dest.writeString(this.parentId);
        dest.writeString(this.groupCategoryConfig);
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public void save(Context context) {
        UserInfoDao.save(context, this);
    }
}
