package com.codyy.tpmp.filterlibrary.entities;

/**
 * 过滤中的常用字段
 * Created by poe on 28/04/17.
 */

public class FilterConstants {

    public static final int LEVEL_AREA = 0x00;//地区id
    public static final int LEVEL_DIRECT = 0x01;//直属校
    public static final int LEVEL_CLASS_SEMESTER = 0x002;//固定的筛选条件-学段
    public static final int LEVEL_SCHOOL = 0x03;//固定-学校
    public static final int LEVEL_CLASS_TEAM = 0x004;//固定的筛选条件-组别
    public static final int LEVEL_CLASS_LEVEL = 0x005;//固定的筛选条件-年级
    public static final int LEVEL_CLASS_SUBJECT = 0x006;//固定的筛选条件-学科
    public static final int LEVEL_CLASS_CATEGORY = 0x007;//固定的筛选条件-分类-圈组
    public static final int LEVEL_CLASS_STATE = 0x008;//固定的筛选条件-状态[未开始/进行中/已结束]
    public static final int LEVEL_LESSON_CATEGORY = 0x009;//固定的筛选条件-类别
    public static final int LEVEL_MANAGER_STATE = 0x010;//固定的筛选条件-状态[待处理/通过/未通过/被关闭]
    public static final int LEVEL_CLASS_END = 0x111;//固定的筛选条件-结束位

    public static final String STR_PROVINCE = "省";
    public static final String STR_CITY = "市";
    public static final String STR_AREA = "县";
    public static final String STR_SEMESTER = "学段";
    public static final String STR_SCHOOL = "学校";
    public static final String STR_SCHOOL_DIRECT = "直属校";
    public static final String STR_CATEGORY = "分类";
    public static final String STR_TEAM = "组别";//INTEREST/TEACH
    public static final String STR_STATE = "状态";//(CLOSED/WAIT/APPROVED/REJECT) or (INIT/PROGRESS/CLOSED)
    public static final String STR_TEAM_TEACH = "教研组";
    public static final String STR_TEAM_INTEREST = "兴趣组";
    public static final String STR_LEVEL = "年级";
    public static final String STR_SUBJECT = "学科";
    public static final String STR_LESSON_CATEGORY = "类别";
    public static final String STR_ALL = "全部";

    //圈组-状态(CLOSED/WAIT/APPROVED/REJECT)
    public static final String STR_STATE_PASS = "通过";
    public static final String STR_STATE_REJECT = "未通过";
    public static final String STR_STATE_CLOSED = "被关闭";
    public static final String STR_STATE_PENDING = "待处理";

    public static final String VAL_STATE_PASS = "APPROVED";
    public static final String VAL_STATE_REJECT = "REJECT";
    public static final String VAL_STATE_CLOSED = "CLOSED";
    public static final String VAL_STATE_PENDING = "WAIT";

    //状态(INIT/PROGRESS/CLOSED)
    public static final String STR_STATE_INIT = "未开始";
    public static final String STR_STATE_PROGRESS = "进行中";
    public static final String STR_STATE_END = "已结束";

    public static final String VAL_STATE_INIT = "INIT";
    public static final String VAL_STATE_PROGRESS = "PROGRESS";
    public static final String VAL_STATE_END = "END";



    //类别
    public static final String STR_CATEGORY_LESSON_SINGLE = "单节课";
    public static final String STR_CATEGORY_LESSON_SERIES = "系列课";

    /**
     * 获取-筛选-右侧-Level
     * @param levelName
     * @return
     */
    public static int getLevel(String levelName) {
        int level =  LEVEL_AREA ;
        switch (levelName){
            case STR_PROVINCE:
            case STR_AREA:
            case STR_CITY:
                level = LEVEL_AREA ;
                break;
            case STR_SCHOOL:
                level = LEVEL_SCHOOL ;
                break;
            case STR_SEMESTER:
                level = LEVEL_CLASS_SEMESTER;
                break;
            case STR_LEVEL:
                level = LEVEL_CLASS_LEVEL ;
                break;
            case STR_SUBJECT:
                level = LEVEL_CLASS_SUBJECT;
                break;
            case STR_CATEGORY:
                level = LEVEL_CLASS_CATEGORY;
                break;
            case STR_TEAM:
                level = LEVEL_CLASS_TEAM;
                break;
            case STR_STATE:
                level = LEVEL_CLASS_STATE;
                break;
            case STR_LESSON_CATEGORY:
                level = LEVEL_LESSON_CATEGORY;
                break;
        }
        return level;
    }


    public static String getLevelName(int level){
        String levelName =  FilterConstants.STR_ALL;
        switch (level){
            case FilterConstants.LEVEL_AREA:
                levelName = FilterConstants.STR_PROVINCE;
                break;
            case FilterConstants.LEVEL_SCHOOL:
                levelName = FilterConstants.STR_SCHOOL;
                break;
            case FilterConstants.LEVEL_CLASS_LEVEL:
                levelName = FilterConstants.STR_LEVEL;
                break;
            case FilterConstants.LEVEL_CLASS_SUBJECT:
                levelName = FilterConstants.STR_SUBJECT;
                break;
            case FilterConstants.LEVEL_CLASS_CATEGORY:
                levelName = FilterConstants.STR_CATEGORY;
                break;
            case FilterConstants.LEVEL_CLASS_TEAM:
                levelName = FilterConstants.STR_TEAM;
                break;
            case FilterConstants.LEVEL_CLASS_STATE:
                levelName = FilterConstants.STR_STATE;
                break;
            case FilterConstants.LEVEL_CLASS_SEMESTER:
                levelName = FilterConstants.STR_SEMESTER;
                break;
            case FilterConstants.LEVEL_LESSON_CATEGORY:
                levelName = FilterConstants.STR_LESSON_CATEGORY;
                break;
            case FilterConstants.LEVEL_MANAGER_STATE:
                levelName = FilterConstants.STR_STATE;
                break;
        }
        return levelName;
    }

}
