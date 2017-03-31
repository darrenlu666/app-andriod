package com.codyy.erpsportal.commons.models.entities.configs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.activity.ClassRecordedFilterActivity;
import com.codyy.erpsportal.classroom.activity.ClassRecordedNoAreaActivity;
import com.codyy.erpsportal.classroom.activity.ClassRoomListActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.AssessmentClassActivity;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourNewActivity;
import com.codyy.erpsportal.commons.controllers.activities.CollectivePrepareLessonsActivity;
import com.codyy.erpsportal.commons.controllers.activities.HomeWorkNewActivity;
import com.codyy.erpsportal.commons.controllers.activities.RemoteDirectorNewActivity;
import com.codyy.erpsportal.commons.controllers.activities.UserTimeTableActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.ApplicationViewHold;
import com.codyy.erpsportal.commons.models.DirectJumper;
import com.codyy.erpsportal.commons.models.Jumpable;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.AppInfo;
import com.codyy.erpsportal.commons.models.entities.AppPriority;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.personal.Student;
import com.codyy.erpsportal.county.controllers.activities.CountyListActivity;
import com.codyy.erpsportal.exam.controllers.activities.ExamActivity;
import com.codyy.erpsportal.groups.controllers.activities.GroupManagerActivity;
import com.codyy.erpsportal.homework.controllers.activities.WorkListsActivity;
import com.codyy.erpsportal.info.controllers.activities.InfoDeleteActivity;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.VideoMeetingActivity;
import com.codyy.erpsportal.onlineteach.controllers.activities.OnlineTeachActivity;
import com.codyy.erpsportal.perlcourseprep.controllers.activities.PersonalLesPrepActivity;
import com.codyy.erpsportal.reservation.controllers.activities.ReservationClassActivity;
import com.codyy.erpsportal.reservation.controllers.activities.ReservationClassDetailActivity;
import com.codyy.erpsportal.resource.controllers.activities.ResourcesNewActivity;
import com.codyy.erpsportal.rethink.controllers.activities.RethinkListActivity;
import com.codyy.erpsportal.schooltv.controllers.activities.SchoolTvHistoryActivity;
import com.codyy.erpsportal.schooltv.controllers.activities.SchoolTvProgramListActivity;
import com.codyy.erpsportal.statistics.controllers.activities.CoursesStatisticsActivity;
import com.codyy.erpsportal.statistics.controllers.activities.StatisticalActivity;
import com.codyy.erpsportal.timetable.activities.TimeTableDetailActivity;
import com.codyy.erpsportal.timetable.activities.TimeTableListActivity;
import com.codyy.erpsportal.tutorship.controllers.activities.TutorshipActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * function config
 * Created by poe on 15-7-20.
 */
public class AppConfig {

//    private final static String TAG = "AppConfig";
    /**
     * single instance
     */
    private static AppConfig sAppInstance;
    /**
     * 完整的应用信息集合
     */
    private List<AppInfo> mAppData = new ArrayList();
    private static HashMap<String, List<AppInfo>> mChildItem = new HashMap<>();
    /**
     * icons for first level
     * size :9
     */
    private Integer[] PARENT_ICONS = {
            R.drawable.ic_fun_custom_lesson,//专递课堂 0
            R.drawable.ic_fun_live_record_lesson,//直录播课堂 1
            R.drawable.ic_fun_person_lesson,//个人备课 2
            R.drawable.ic_fun_collection_prepare,//集体备课 3
            R.drawable.ic_fun_interact_lesson,//互动听课 4
            R.drawable.ic_fun_evaluation,//评课议课 5
            R.drawable.ic_fun_online_meeting,//视频会议 6
            R.drawable.ic_fun_rethink,//教学反思 7
            R.drawable.ic_fun_schedule,//课程表 8
            R.drawable.ic_fun_test,//测试 9
            R.drawable.ic_fun_home_work,//作业 10
            R.drawable.ic_fun_ask_answer,//问答 ？ 11
            R.drawable.ic_fun_class_member,//班级成员  12
            R.drawable.ic_fun_infomation,//资讯 - >新闻  , 13
            R.drawable.ic_fun_tutorship,//辅导 14
            R.drawable.ic_fun_group,//圈组 15
            R.drawable.ic_fun_resource,//优课资源 16
            R.drawable.ic_fun_analysis,//统计 17
            R.drawable.ic_fun_net_teach,//网络授课 18
            R.drawable.ic_fun_school_tv,//校园电视台 19
    };
    /**
     * 二级菜单icon .
     */
    private static HashMap<String,Integer> MENU_ICON = new HashMap<>();

    private final static String[] MENUS = {
            "onlineclass.id",//专递课堂 0
            "liveclass.id",//直录播课堂 1
            "prepare.lesson.plan.id",//个人备课 2
            "prepare.lession.id",//集体备课 3
            "interact.lession.id",//互动听课 4
            "evaluation.meeting.id",//评课议课 5
            "video.meeting.id",//视频会议 6
            "rethink.id",//教学反思 7
            "schedule.id",//课程表 8
            "test.id",//测试 9
            "homework.id",//作业 10
            "ask.answer.id",//问答 ？ 11
            "class.member.id",//班级成员 12
            "information.id",//资讯 - >新闻 13
            "online.tutor.id",//辅导 14
            "group.id",//圈组 15
            "resource.id",//优课资源 16
            "statistic.id",//统计 17
            "netteach.id",//网络授课 18
            "tvProgram.id",//校园电视台 19
    };

    private HashMap<String, List<AppPriority>> mPriorityCollection = new HashMap<>();

    /**
     * 构造方法，初始化
     */
    private AppConfig() {
        //init the first level data .
        initGroupData();
        //init the appInfo
        initAppInfo();
    }

    public synchronized static AppConfig instance() {
        if (sAppInstance == null) {
            sAppInstance = new AppConfig();
        }
        return sAppInstance;
    }


    /**
     * 初始化数据配置(配置二级目录)
     */
    private void initGroupData() {
        initPriority();
        initChildIcon();
        mChildItem.clear();
        List<AppInfo> list1 = new ArrayList();//专递课堂
        List<AppInfo> list2 = new ArrayList();//名校网络课堂
        List<AppInfo> list3 = new ArrayList();//统计
        List<AppInfo> schoolBroadList = new ArrayList();//校园电视台


        //1.专递课堂 及 权限配置
        //1.0 区县总表
        list1.add(new AppInfo(R.drawable.ic_child_area_schedule, MENUS[0], Titles.sWorkspaceSpeclassAllTable,
                AppPriority.createCollections(AppPriority.AREA),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        Intent intent = new Intent(context, CountyListActivity.class);
                        context.startActivity(intent);
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));
        //1.1课程表 (先隐藏-去掉所有的权限)
        list1.add(new AppInfo(R.drawable.ic_child_schedule, MENUS[0], Titles.sWorkspaceSpeclassSchedule,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                        if (UserInfo.USER_TYPE_SCHOOL_USER.equals(userInfo.getUserType()) || UserInfo.USER_TYPE_TEACHER.equals(userInfo.getUserType())) {
                            Intent intent = new Intent(context, TimeTableDetailActivity.class);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, TimeTableListActivity.class);
                            context.startActivity(intent);
                        }
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));

        //1.2实时直播，标题没有配置项
        list1.add(new AppInfo(R.drawable.ic_child_live_reocrd, MENUS[0], Titles.sWorkspaceSpeclassLive,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        Intent intent = new Intent(context, ClassRoomListActivity.class);
                        intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, ClassRoomContants.TYPE_CUSTOM_LIVE);
                        intent.putExtra(Constants.USER_INFO , UserInfoKeeper.obtainUserInfo());
                        context.startActivity(intent);
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));
        //1.2实时直播-往期录播
        list1.add(new AppInfo(R.drawable.ic_child_record_history, MENUS[0], Titles.sWorkspaceSpeclassReplay,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                        if (userInfo.isArea()) {
                            Intent intent = new Intent(context, ClassRecordedFilterActivity.class);
                            intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, ClassRoomContants.TYPE_CUSTOM_RECORD);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, ClassRecordedNoAreaActivity.class);
                            intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, ClassRoomContants.TYPE_CUSTOM_RECORD);
                            context.startActivity(intent);
                        }
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));
        //1.3课堂巡视
        list1.add(new AppInfo(R.drawable.ic_child_lesson_watch, MENUS[0], Titles.sWorkspaceSpeclassRound,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL),
                new ClassTourNewActivity.ClassTourJumper(ClassTourNewActivity.TYPE_SPECIAL_DELIVERY_CLASSROOM),
                AppInfo.CATEGORY_SINGLE_MODEL));
        //1.4课堂作业
        list1.add(new AppInfo(R.drawable.ic_child_lesson_work, MENUS[0], Titles.sWorkspaceSpeclassTask,
                AppPriority.createCollections(AppPriority.SCHOOL, AppPriority.TEACHER),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        if ("SCHOOL_USR".endsWith(UserInfoKeeper.getInstance().getUserInfo().getUserType()) || "TEACHER".endsWith(UserInfoKeeper.getInstance().getUserInfo().getUserType())) {
                            Intent intent = new Intent(context, HomeWorkNewActivity.class);
                            intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, ClassRoomContants.FROM_ONLINE_CLASS);
                            context.startActivity(intent);
                        }
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));
        //1.5远程导播 ->今日导播(v5.3.0)
        list1.add(new AppInfo(R.drawable.ic_child_remote_director, MENUS[0], Titles.sWorkspaceSpeclassBroadcast,
                AppPriority.createCollections(AppPriority.TEACHER),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        Intent intent = new Intent(context, RemoteDirectorNewActivity.class);
                        intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, ClassRoomContants.FROM_WHERE_LINE);
                        context.startActivity(intent);
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));

        //2.名校网络课堂
        //2.1约课列表
        list2.add(new AppInfo(R.drawable.ic_child_lesson_about, MENUS[1], Titles.sWorkspaceNetClassClass,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        UserInfo userInfo = UserInfoKeeper.obtainUserInfo();
                        if (UserInfo.USER_TYPE_AREA_USER.equals(userInfo.getUserType())) {
                            Intent intent = new Intent(context, ReservationClassActivity.class);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, ReservationClassDetailActivity.class);
                            context.startActivity(intent);
                        }
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));
        //2.2 直播课堂，标题没有配置项。
        list2.add(new AppInfo(R.drawable.ic_child_live_lesson, MENUS[1], Titles.sWorkspaceNetClassLive,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        Intent intent = new Intent(context, ClassRoomListActivity.class);
                        intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, ClassRoomContants.TYPE_LIVE_LIVE);
                        intent.putExtra(Constants.USER_INFO , UserInfoKeeper.obtainUserInfo());
                        context.startActivity(intent);
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));
        //2.2 直录播-往期录播
        list2.add(new AppInfo(R.drawable.ic_child_record_history, MENUS[1], Titles.sWorkspaceNetClassReplay,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        UserInfo userInfo = UserInfoKeeper.obtainUserInfo();
                        if (userInfo.isArea()) {
                            Intent intent = new Intent(context, ClassRecordedFilterActivity.class);
                            intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, ClassRoomContants.TYPE_LIVE_RECORD);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, ClassRecordedNoAreaActivity.class);
                            intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, ClassRoomContants.TYPE_LIVE_RECORD);
                            context.startActivity(intent);
                        }
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));
        //2.3课堂巡视
        list2.add(new AppInfo(R.drawable.ic_child_lesson_watch, MENUS[1], Titles.sWorkspaceNetClassRound,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL),
                new ClassTourNewActivity.ClassTourJumper(ClassTourNewActivity.TYPE_SCHOOL_NET),
                AppInfo.CATEGORY_SINGLE_MODEL));
        //2.4课堂作业
        list2.add(new AppInfo(R.drawable.ic_child_lesson_work, MENUS[1], Titles.sWorkspaceNetClassTask,
                AppPriority.createCollections(AppPriority.SCHOOL, AppPriority.TEACHER),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        if ("SCHOOL_USR".endsWith(UserInfoKeeper.getInstance().getUserInfo().getUserType()) || "TEACHER".endsWith(UserInfoKeeper.getInstance().getUserInfo().getUserType())) {
                            Intent intent = new Intent(context, HomeWorkNewActivity.class);
                            intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, ClassRoomContants.FROM_LIVE);
                            context.startActivity(intent);
                        }
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));
        //2.5远程导播
        list2.add(new AppInfo(R.drawable.ic_child_remote_director, MENUS[1], Titles.sWorkspaceNetClassBroadcast,
                AppPriority.createCollections(AppPriority.TEACHER),

                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        Intent intent = new Intent(context, RemoteDirectorNewActivity.class);
                        intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, ClassRoomContants.FROM_WHERE_SCHOOL);
                        context.startActivity(intent);
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));

        //3.统计
        //3.1课堂统计
        list3.add(new AppInfo(R.drawable.ic_child_analysis_lesson, MENUS[17], Titles.sWorkspaceCountClass,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                        CoursesStatisticsActivity.start(context, userInfo);
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));
        //3.2评课统计
        list3.add(new AppInfo(R.drawable.ic_child_analysis_evaluation, MENUS[17], Titles.sWorkspaceCountDisucss,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                        StatisticalActivity.startActivityStat((Activity) context, userInfo);
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));
        //3.3资源统计
        list3.add(new AppInfo(R.drawable.ic_child_analysis_resource, MENUS[17], Titles.sWorkspaceCountResource,
                AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL),
                new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                        StatisticalActivity.startResourceStat((Activity) context, userInfo);
                    }
                },
                AppInfo.CATEGORY_SINGLE_MODEL));

        //4 校园电视台
        //4.1 节目表
        AppInfo app_program_tv = new AppInfo(R.drawable.ic_child_school_program_schedule, MENUS[19], Titles.sWorkspaceTvProgramProgramList,
                AppPriority.createCollections(AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT),
                /*new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        SchoolTvProgramListActivity.start((Activity) context,UserInfoKeeper.getInstance().getUserInfo());
                    }
                }*/null,
                AppInfo.CATEGORY_SINGLE_MODEL);
        app_program_tv.setBaseViewHoldType(ApplicationViewHold.ITEM_TYPE_CHILD);
        schoolBroadList.add(app_program_tv);
        //4.2 往期视频
        AppInfo app_program_history = new AppInfo(R.drawable.ic_child_school_history_video, MENUS[19], Titles.sWorkspaceTvProgramReplay,
                AppPriority.createCollections(AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT),
                /*new Jumpable() {
                    @Override
                    public void jump(Context context) {
                        SchoolTvHistoryActivity.start((Activity) context,UserInfoKeeper.getInstance().getUserInfo());
                    }
                }*/null,
                AppInfo.CATEGORY_SINGLE_MODEL);
        app_program_history.setBaseViewHoldType(ApplicationViewHold.ITEM_TYPE_CHILD);
        schoolBroadList.add(app_program_history);

        mChildItem.put(MENUS[0], list1);
        mChildItem.put(MENUS[1], list2);
        mChildItem.put(MENUS[17], list3);
        mChildItem.put(MENUS[19], schoolBroadList);
    }

    private void initChildIcon() {
        //统计-课堂统计
        MENU_ICON.put("front.workspace.count.class.area",R.drawable.ic_child_analysis_lesson);
        MENU_ICON.put("front.workspace.count.class.school",R.drawable.ic_child_analysis_lesson);
        //统计-资源统计
        MENU_ICON.put("front.workspace.count.resource.area",R.drawable.ic_child_analysis_resource);
        MENU_ICON.put("front.workspace.count.resource.school",R.drawable.ic_child_analysis_resource);
        //统计－活动统计
        MENU_ICON.put("front.workspace.count.disucss.area",R.drawable.ic_child_analysis_evaluation);
        MENU_ICON.put("front.workspace.count.disucss.school",R.drawable.ic_child_analysis_evaluation);
    }

    /**
     * 权限初始化
     */
    private void initPriority() {
        //专递课堂
        mPriorityCollection.put(MENUS[0], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT));
        //名校网络课堂
        mPriorityCollection.put(MENUS[1], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT));
        //个人备课
        mPriorityCollection.put(MENUS[2], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER));
        //集体备课
        mPriorityCollection.put(MENUS[3], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER));
        //互动听课
        mPriorityCollection.put(MENUS[4], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER));
        //评课议课
        mPriorityCollection.put(MENUS[5], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER));
        //视频会议
        mPriorityCollection.put(MENUS[6], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER));
        //教学反思teach thinking
        mPriorityCollection.put(MENUS[7], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER));
        //课程表
        mPriorityCollection.put(MENUS[8], AppPriority.createCollections(AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT));
        //测试test
        mPriorityCollection.put(MENUS[9], AppPriority.createCollections(AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT));
        //作业task
        mPriorityCollection.put(MENUS[10], AppPriority.createCollections(AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT));
        //问答ask question
//        mPriorityCollection.put(MENUS[10], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT));
        mPriorityCollection.put(MENUS[11], null);
        //班级成员classroom member
        mPriorityCollection.put(MENUS[12], AppPriority.createCollections(AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT));
        //资讯
        mPriorityCollection.put(MENUS[13], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        //辅导 tutorship
        mPriorityCollection.put(MENUS[14], AppPriority.createCollections(AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT));
        //圈组 ring group
        mPriorityCollection.put(MENUS[15], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT));
        //优课资源
        mPriorityCollection.put(MENUS[16], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT));
        //统计
        mPriorityCollection.put(MENUS[17], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        //网络授课
        mPriorityCollection.put(MENUS[18], AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT));
        //校园电视台
        mPriorityCollection.put(MENUS[19], AppPriority.createCollections(AppPriority.SCHOOL, AppPriority.TEACHER, AppPriority.STUDENT, AppPriority.PARENT));

        /** 二级应用列表-统计**/
        //统计－课堂统计/** 二级应用列表-统计**/
        //统计－课堂统计A
        mPriorityCollection.put("front.workspace.count.class.area",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        mPriorityCollection.put("front.workspace.count.class.school",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        //统计－资源统计
        mPriorityCollection.put("front.workspace.count.resource.area",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        mPriorityCollection.put("front.workspace.count.resource.school",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        //统计－活动统计
        mPriorityCollection.put("front.workspace.count.disucss.area",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        mPriorityCollection.put("front.workspace.count.disucss.school",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        mPriorityCollection.put("front.workspace.count.class.area",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        mPriorityCollection.put("front.workspace.count.class.school",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        //统计－资源统计
        mPriorityCollection.put("front.workspace.count.resource.area",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        mPriorityCollection.put("front.workspace.count.resource.school",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        //统计－活动统计
        mPriorityCollection.put("front.workspace.count.disucss.area",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
        mPriorityCollection.put("front.workspace.count.disucss.school",AppPriority.createCollections(AppPriority.AREA, AppPriority.SCHOOL));
    }

    /**
     * 配置初始化结束调用，更新应用的titles .
     */
    public void updateConfigTitles() {
        if (null == mChildItem || mChildItem.size() == 0) return;
        //1.专递课堂 及 权限配置
        mChildItem.get(MENUS[0]).get(0).setAppName(Titles.sWorkspaceSpeclassAllTable); //1.0区县总表
        mChildItem.get(MENUS[0]).get(1).setAppName(Titles.sWorkspaceSpeclassSchedule); //1.1课程表
        mChildItem.get(MENUS[0]).get(2).setAppName(Titles.sWorkspaceSpeclassLive);  //1.2实时直播
        mChildItem.get(MENUS[0]).get(3).setAppName(Titles.sWorkspaceSpeclassReplay);  //1.2往期录播
        mChildItem.get(MENUS[0]).get(4).setAppName(Titles.sWorkspaceSpeclassRound); //1.3课堂巡视
        mChildItem.get(MENUS[0]).get(5).setAppName(Titles.sWorkspaceSpeclassTask); //1.4课堂作业
//        mChildItem.get(MENUS[0]).get(6).setAppName(Titles.sWorkspaceSpeclassBroadcast);  //1.5远程导播

        //2.名校网络课堂
        mChildItem.get(MENUS[1]).get(0).setAppName(Titles.sWorkspaceNetClassClass); //2.1约课列表
        mChildItem.get(MENUS[1]).get(1).setAppName(Titles.sWorkspaceNetClassLive);  //2.2实时直播
        mChildItem.get(MENUS[1]).get(2).setAppName(Titles.sWorkspaceNetClassReplay);  //2.2往期录播
        mChildItem.get(MENUS[1]).get(3).setAppName(Titles.sWorkspaceNetClassRound); //2.3课堂巡视
        mChildItem.get(MENUS[1]).get(4).setAppName(Titles.sWorkspaceNetClassTask);   //2.4课堂作业
//        mChildItem.get(MENUS[1]).get(5).setAppName(Titles.sWorkspaceNetClassBroadcast);   //2.5远程导播

        //9.统计
        mChildItem.get(MENUS[17]).get(0).setAppName(Titles.sWorkspaceCountClass);//9.1课堂统计
        mChildItem.get(MENUS[17]).get(1).setAppName(Titles.sWorkspaceCountDisucss); //9.2评课统计
        mChildItem.get(MENUS[17]).get(2).setAppName(Titles.sWorkspaceCountResource);//9.3资源统计
    }

    /**
     * 获取过滤后的应用信息
     *
     * @param userType 用户类型
     * @return
     */
    public List<AppInfo> getFilterAppInfo(String userType) {
        AppPriority level = AppPriority.getByRole(userType.toUpperCase());
        List<AppInfo> results = filterLevel(mAppData, level);
        return results;
    }

    private List<AppInfo> filterLevel(List<AppInfo> datas, AppPriority level) {
        List<AppInfo> results = new ArrayList<>();
        for (AppInfo app : datas) {

            /**
             * 省级管理者可以看到需要实际管理权限的数据，学校管理看不到需要省级管理权限的数据
             */
            List<AppPriority> roles = app.getRole();
            if (roles != null && roles.contains(level)) {
                //level级别越大数值越小
                //children filter recurse
                if (null != app.getChildGroups() && app.getChildGroups().size() > 0) {
                    app.setChildGroups(filterLevel(app.getChildGroups(), level));
                }

                results.add(app);
            }
        }

        return results;
    }

    /**
     * 根据服务器端数据来初始化数据 .
     *
     * @param parentApps
     */
    public void updateData(List<AppInfo> parentApps) {
        mAppData.clear();
        if (null != parentApps && parentApps.size() > 0) {
            for (AppInfo app : parentApps) {
                initJump(app);
                mAppData.add(app);
            }
        }
    }


    /**
     * 获取一级菜单icon
     */
    public int getParentIcon(String menuID) {
        int pos = 0;
        for (int i = 0; i < MENUS.length; i++) {
            if (menuID.equals(MENUS[i])) {
                pos = i;
                break;
            }
        }
        return PARENT_ICONS[pos];
    }

    /**
     * 获取二级菜单的icon
     * @param menuId
     * @return
     */
    public static int getChildMenuIcon(String menuId){
        if(MENU_ICON.containsKey(menuId)){
            return  MENU_ICON.get(menuId);
        }
        return R.drawable.icon_default_video;
    }

    /**
     * 初始化整个app的应用信息
     */
    private void initAppInfo() {
        mAppData.clear();
        String[] titles = EApplication.instance().getResources().getStringArray(R.array.apps_function_array);
        for (int i = 0; i < titles.length; i++) {
            AppInfo ai = new AppInfo();
            ai.setAppName(titles[i]);
            ai.setMenuID(MENUS[i]);
            ai.setRole(getPriorities(MENUS[i]));
            //children /icon /
            initJump(ai);
            mAppData.add(ai);
        }
    }

    /**
     * 根据id获取权限
     *
     * @param menuID
     * @return
     */
    public List<AppPriority> getPriorities(String menuID) {
        List<AppPriority> role = mPriorityCollection.get(menuID);
        return role;
    }

    /**
     * 获取id的排序位置
     *
     * @param menuID
     * @return
     */
    private int getMenuPosition(String menuID) {
        int pos = 0;
        for (int i = 0; i < MENUS.length; i++) {
            if (menuID.equals(MENUS[i])) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    /**
     * 初始化一级跳转 并设置子items
     *
     * @param ai
     */
    public void initJump(AppInfo ai) {
        ai.setIcon(getParentIcon(ai.getMenuID()));
        String menuID = ai.getMenuID();

        if (mChildItem.get(menuID) != null) {
            ai.setCategory(AppInfo.CATEGORY_CHILD_MODEL);
            ai.setBaseViewHoldType(ApplicationViewHold.ITEM_TYPE_MULTI);
            //此处过滤掉可配置模块＂统计"
            if(null == ai.getChildGroups() || ai.getChildGroups().size() == 0){
                ai.setChildGroups(mChildItem.get(menuID));
            }
        } else {
            ai.setBaseViewHoldType(ApplicationViewHold.ITEM_TYPE_SINGLE);
            ai.setCategory(AppInfo.CATEGORY_SINGLE_MODEL);
            ai.setChildGroups(new ArrayList<AppInfo>());
            int pos = getMenuPosition(menuID);
            Jumpable jumpable = null;
            switch (pos) {
                case 0://专递课堂
                    jumpable = null;
                    break;
                case 1://直录播课堂
                    jumpable = null;
                    break;
                case 2://个人备课
                    jumpable = new DirectJumper(PersonalLesPrepActivity.class);
                    break;
                case 3://集体备课
                    jumpable = new Jumpable() {
                        @Override
                        public void jump(Context context) {
                            Intent intent = new Intent(context, CollectivePrepareLessonsActivity.class);
                            intent.putExtra(Constants.TYPE_LESSON, Constants.TYPE_PREPARE_LESSON);
                            intent.putExtra(Constants.USER_INFO, UserInfoKeeper.getInstance().getUserInfo());
                            context.startActivity(intent);
                        }
                    };
                    break;
                case 4://互动听课
                    jumpable = new Jumpable() {
                        @Override
                        public void jump(Context context) {
                            Intent intent = new Intent(context, CollectivePrepareLessonsActivity.class);
                            intent.putExtra(Constants.TYPE_LESSON, Constants.TYPE_INTERACT_LESSON);
                            intent.putExtra(Constants.USER_INFO, UserInfoKeeper.getInstance().getUserInfo());
                            context.startActivity(intent);
                        }
                    };
                    break;
                case 5://评课议课
                    jumpable = new DirectJumper(AssessmentClassActivity.class);
                    break;
                case 6://视频会议
//                    jumpable = new DirectJumper(VideoMeetingActivity.class);
                    jumpable = new Jumpable() {
                        @Override
                        public void jump(Context context) {
                            Intent intent = new Intent(context, VideoMeetingActivity.class);
                            intent.putExtra(Constants.USER_INFO, UserInfoKeeper.getInstance().getUserInfo());
                            context.startActivity(intent);
                        }
                    };
                    break;
                case 7://教学反思
                    jumpable = new DirectJumper(RethinkListActivity.class);
                    break;
                case 8://课程表
                    jumpable = new Jumpable() {
                        @Override
                        public void jump(Context context) {
                            UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                            Student student = userInfo.getSelectedChild();
                            if (UserInfo.USER_TYPE_PARENT.equals(userInfo.getUserType())) {
                                if (userInfo.getSelectedChild() != null) {
                                    UserTimeTableActivity.start(context, userInfo, userInfo.getSelectedChild().getClassId(), null, 0);
                                }
                            } else {
                                UserTimeTableActivity.start(context, userInfo, userInfo.getBaseClassId(), null, 0);
                            }
                        }
                    };
                    break;
                case 9://测试
                    jumpable = new DirectJumper(ExamActivity.class);
                    break;
                case 10://作业
                    jumpable = new DirectJumper(WorkListsActivity.class);
                    break;
                case 11://问答
                    jumpable = null;
                    break;
                case 12://班级成员
                    jumpable = null;
                    break;
                case 13://资讯
//                    jumpable = new DirectJumper(InfoActivity.class);
                    jumpable = new DirectJumper(InfoDeleteActivity.class);
                    break;
                case 14://辅导
                    jumpable = new DirectJumper(TutorshipActivity.class);
                    break;
                case 15://圈组
                    jumpable = new DirectJumper(GroupManagerActivity.class);
                    break;
                case 16://优课资源
                    jumpable = new DirectJumper(ResourcesNewActivity.class);
                    break;
                case 17://统计
                    jumpable = null;
                    break;
                case 18://网络授课
                    jumpable = new DirectJumper(OnlineTeachActivity.class);
                    break;
            }
            ai.setJumpable(jumpable);
        }
    }

    public static void jumpToActivity(Jumpable jumper, Context context) {
        if (null == context) {
            return;
        }
        if (jumper == null) {
//            ToastUtil.showToast(EApplication.instance(), String.format(EApplication.instance().getString(R.string.title_function_no_imp), "模块"));
        } else {
            jumper.jump(context);
        }
    }

    /**
     * @param id
     * @return
     */
    public static int getMenuIndex(String id) {
        int result = -1;
        if (!TextUtils.isEmpty(id)) {
            for (int i = 0; i < MENUS.length; i++) {
                if (MENUS[i].equals(id)) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 清空缓存
     */
    public void destroy(){
        mChildItem.clear();
        MENU_ICON.clear();
        sAppInstance = null;
    }
}
