package com.codyy.tpmp.filterlibrary.entities;

/**
 * 筛选中用到的用户信息.
 * Created by poe on 26/04/17.
 */

public class FilterUser {

    public static final String USER_TYPE_AREA_USER = "AREA_USR";// 行政机构用户

    public static final String USER_TYPE_SCHOOL_USER = "SCHOOL_USR";// 学校用户

    public static final String USER_TYPE_TEACHER = "TEACHER";// 教师

    public static final String USER_TYPE_STUDENT = "STUDENT";// 学生

    public static final String USER_TYPE_PARENT = "PARENT";// 家长

    public static final String TEAM_TYPE_TEACH = "TEACH";//教研组

    public static final String TEAM_TYPE_INTEREST = "INTEREST";//兴趣组

    public static final String EXTRA_UUID = "extra.uuid";//uuid
    public static final String EXTRA_USER_TYPE = "extra.userType";//userType
    public static final String EXTRA_BASE_AREA_ID = "extra.baseAreaId";//baseAreaId
    public static final String EXTRA_SCHOOL_ID = "extra.schoolId";//schoolId
    public static final String EXTRA_FILTER_DATA = "extra.filter.data";//自由组合插拔的筛选Level集合

    /**
     * 用户的uuid
     */
    private String uuid;

    /**
     * 用户类型
     */
    private String userType;
    /**
     * 所在地区id
     */
    private String baseAreaId;
    /**
     * 用户学校id
     */
    private String schoolId;

    public FilterUser() {
    }

    public FilterUser(String uuid, String userType, String baseAreaId, String schoolId) {
        this.uuid = uuid;
        this.userType = userType;
        this.baseAreaId = baseAreaId;
        this.schoolId = schoolId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getBaseAreaId() {
        return baseAreaId;
    }

    public void setBaseAreaId(String baseAreaId) {
        this.baseAreaId = baseAreaId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
