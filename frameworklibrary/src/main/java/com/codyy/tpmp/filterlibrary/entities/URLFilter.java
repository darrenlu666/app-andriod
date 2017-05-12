package com.codyy.tpmp.filterlibrary.entities;

/**
 * Created by poe on 26/04/17.
 */

public class URLFilter {
    public static String BASE = "http://mobile.9itest.com:85";
    /**
     * 获得地区
     * areaId
     */
    public static String GET_AREA = BASE+"/bArea/getNextAreasAndLevelByParentId.do";

    /**
     * 获取学校
     * areaId	String	筛选时选择的区域id
     * semesterId	String	筛选时选择的学段id(可以为空)
     */
    public static String GET_DIRECT_SCHOOL = BASE+"/bSchool/getSchoolByAreaId.do";
    /**
     * 根据学校id查询年级信息
     * areaId 区域ID和学校ID有且只有一个有值
     * schoolId 区域ID和学校ID有且只有一个有值
     */
    public static String ALL_CLASS_LEVEL = BASE+"/bClasslevel/getClasslevels.do";
    /**
     * 根据年级id查询学科信息
     * pClasslevelId    上一级(年级)返回的id	string
     */
    public static String ALL_SUBJECTS_BY_CLASS_ID = BASE+"/bSubject/getSubjectsByPclasslevelId.do";

    /**
     * 根据年级id查询班级信息
     * ///bClassRoom/getClassRoomByClassLevelId.do
     */
    public static String ALL_CLASS_BY_CLASSLEVEL_ID = BASE+"/mobile/bClassRoom/getSchoolClassRoomByClassLevelId.do";

    /**
     * 获取所有学科,列表形式
     */
    public static String ALL_SUBJECTS_LIST = BASE+"/bSubject/getAllSubjects.do";

    /**
     * poe add
     * 获取学段
     * areaId 区域ID和学校ID有且只有一个有值
     * schoolId 区域ID和学校ID有且只有一个有值
     */
    public static String GET_SEMESTER_LIST = BASE+"/bSemester/getSemestersById.do";

    /**
     * 获取圈组兴趣组筛选的分类信息
     * uuid String : 32位uuid字符串	用户唯一识别码
     */
    public static String GET_GROUP_CATEGORY_LIST = BASE+"/mobile/group/getAllGroupCategory.do";

    /**
     * 根据LEVEL返回对应的 API .
     * @param level
     * @return
     */
    public static String getURL(int level) {
        String url = "" ;
        switch (level){
            case FilterConstants.LEVEL_AREA:
                url = URLFilter.GET_AREA;
                break;
            case FilterConstants.LEVEL_SCHOOL:
                url = URLFilter.GET_DIRECT_SCHOOL;
                break;
            case FilterConstants.LEVEL_CLASS_SEMESTER:
                url = URLFilter.GET_SEMESTER_LIST;
                break;
            case FilterConstants.LEVEL_CLASS_TEAM://分组动态获取(userInfo.getTeam()) .
                url = "";
                break;
            case FilterConstants.LEVEL_CLASS_LEVEL:
                url = URLFilter.ALL_CLASS_LEVEL;
                break;
            case FilterConstants.LEVEL_CLASS_SUBJECT:
                url = URLFilter.ALL_SUBJECTS_LIST ;
                break;
            case FilterConstants.LEVEL_CLASS_CATEGORY:
                url = URLFilter.GET_GROUP_CATEGORY_LIST;
                break;
            default:
                url = URLFilter.GET_AREA ;
                break;
        }
        return url;
    }

}
