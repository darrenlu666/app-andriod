package com.codyy.url;

import com.codyy.erpsportal.BuildConfig;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.urlbuilder.UrlBuilder;
import com.codyy.erpsportal.urlbuilder.annotations.UrlSuffix;

/**
 * Created by kmdai on 2015/4/13.
 * <p/>
 * 转换用的正则
 * 查找public\sstatic\sString\s(\S+?)\s??=\s??BASE\s??\+\s??\"(\S+?)\";
 * 替换@UrlSuffix\("$2"\)\n    public static String $1 = BASE + \"$2\";
 * 会把
 * public static String IMAGE_URL = BASE + "/ImageServlet/";
 * 转为
 *
 * @UrlSuffix("/ImageServlet/") public static String IMAGE_URL = BASE + "/ImageServlet/";
 * 用于添加UrlSuffix注解
 */
public class URLConfig {

    private final static String TAG = "URLConfig";

    public static String BASE = BuildConfig.API_HOST;

    @UrlSuffix("images/")
    public static String IMAGE_URL;

    /**
     * 获取应用模块
     */
    @UrlSuffix("mobile/workspace/getUseList.do")
    public static String URL_GET_APPS;

    /**
     * 获取应用-家长-孩子模块
     * studentId
     * uuid
     */
    @UrlSuffix("myHome/mobile/getAppOfChildren.do")
    public static String URL_GET_CHILD_APPS;
    /**
     * 上传图片
     */
    @UrlSuffix("mobile/changeHeadPic.do")
    public static String UPLOAD_IMAGE;

    /**
     * 获取默认地区码
     */
    @UrlSuffix("map/getDefaultAreaCode.do")
    public static String URL_DEFAULT_AREA_CODE;

    @UrlSuffix("index/getHomePageInitInfo.do")
    public static String CONFIG;

    /**
     * 获取地区数据
     */
    @UrlSuffix("index/getHomePageCount.do")
    public static String URL_HOME_PAGE_COUNT;

    /**
     * 天津首页地图
     */
    @UrlSuffix("map/mapTJPage.html")
    public static String URL_MAP;

    /**
     * 天津数据
     */
    @UrlSuffix("map/getDataByAreaId.do")
    public static String PANEL_DATA;

    /**
     * 费县数据
     */
    @UrlSuffix("map/getFXData.do")
    public static String GET_FX_DATA;

    /**
     * 登录
     */
    @UrlSuffix("newLogin.do")
    public static String LOGIN;

    @UrlSuffix("loginWithToken.do")
    public static String LOGIN_WITH_TOKEN;

    /**
     * 获取登录token
     */
    @UrlSuffix("generateLoginToken.do")
    public static String LOGIN_TOKEN;

    /**
     * 获取验证码图片
     */
    @UrlSuffix("getVerifyCodeImage.do")
    public static String VERIFY_CODE_IMAGE;

    @UrlSuffix("mobile/complete/completeUserInfo.do")
    public static String COMPLETE_USER_INFO;

    /**
     * 获得课表
     */
    @UrlSuffix("mobile/schedule/getSchoolInfoPageList.do")
    public static String TIMETABLE_URL;
    /**
     * 获取基本信息
     */
    @UrlSuffix("mobile/schedule/getBaseSchoolScheduleInfo.do")
    public static String GET_BASESCHEDULEINFO;
    /**
     * 专递课堂-课程表-获取主讲教室
     */
    @UrlSuffix("mobile/schedule/getMasterClassroomScheduleDetail.do")
    public static String GET_MASTERCLASSROOM_DETAIL;
    /**
     * 专递课堂-课程表-获取接收教室
     */
    @UrlSuffix("mobile/schedule/getReceiveClassroomScheduleDetail.do")
    public static String GET_RECEIVECLASSROOM_DETAIL;
    /**
     * 专递课堂-课程表-获取老师课表详情
     */
    @UrlSuffix("mobile/schedule/getTeacherScheduleDetail.do")
    public static String GET_TEACHER_SCHEDULE;

    /**
     * 课表详情
     */
    @UrlSuffix("mobile/schedule/getScheduleDetail.do")
    public static String TIMETABLE_DETAILS;

    /**
     * 获得地区
     * areaId
     */
    @UrlSuffix("bArea/getNextAreasAndLevelByParentId.do")
    public static String GET_AREA;
    /**
     * 获取学校
     * areaId	String	筛选时选择的区域id
     * semesterId	String	筛选时选择的学段id(可以为空)
     */
    @UrlSuffix("bSchool/getSchoolByAreaId.do")
    public static String GET_DIRECT_SCHOOL;

    /**
     * poe add
     * 获取学段
     * areaId 区域ID和学校ID有且只有一个有值
     * schoolId 区域ID和学校ID有且只有一个有值
     */
    @UrlSuffix("bSemester/getSemestersById.do")
    public static String GET_SEMESTER_LIST;
    /**
     * 获取接收教室
     */
    @UrlSuffix("mobile/schedule/getScheduleDetailInfo.do")
    public static String GET_MASTER_CLASS;

    /**
     * 获取评课详情
     */
    @UrlSuffix("mobile/eva/getEvaluationDetail.do")
    public static String GET_EVALUATIONDETAIL;

    /**
     * 获得发起评课
     */
    @UrlSuffix("mobile/eva/getSponsorEvaluations.do")
    public static String GET_SPONSOR_EVALUATION;
    /**
     * 获取管辖区的评课
     */
    @UrlSuffix("mobile/eva/getAreaEvaluations.do")
    public static String GET_AREA_EVALUALUATIONS;

    /**
     * 获取受邀的评课
     */
    @UrlSuffix("mobile/eva/getInvitedEvaluations.do")
    public static String GET_INVITED_EVALUALUATIONS;
    /**
     * 获取我的评课议【我发起的 我受邀的 教师发起的 本校主讲的我发起的 我参与的 我主讲的】
     */
    @UrlSuffix("mobile/eva/getInvitedEvaluations.do")
    public static String GET_ALL_EVALUATIONS;// = "http://10.5.52.14:8080/mockjs/5/mobile/eva/getSponsorEvaluations.do?";

    /**
     * 本校教师的评课
     */
    @UrlSuffix("mobile/eva/getSchoolTeacherEvas.do")
    public static String GET_SCHOOLTEACHER_EVALUALUATIONS;

    /**
     * 本校主讲的
     */
    @UrlSuffix("mobile/eva/getSchoolTeacherMasterEvas.do")
    public static String GET_SCHOOL_MASTER_EVALUALUATION;

    /**
     * 我主讲的评课
     */
    @UrlSuffix("mobile/eva/getMasterEvas.do")
    public static String GET_MASTER_EVALUALUATIONS;

    /**
     * 参与的评课
     */
    @UrlSuffix("mobile/eva/getAttendEvas.do")
    public static String GET_ATTEND_EVALUALUATIONS;

    /**
     * 获取评论
     */
    @UrlSuffix("mobile/eva/getEvaComments.do")
    public static String GET_COMMENT;

    /**
     * 打分
     */
    @UrlSuffix("mobile/eva/getEvaStandardDetails.do")
    public static String GET_EVASTANDARDDeETAILS;

    /**
     * 发表评论
     */
    @UrlSuffix("mobile/eva/sendEvaComment.do")
    public static String SEND_COMMENT;
    /**
     * 拒绝评课
     */
    @UrlSuffix("mobile/eva/rejectEvaluation.do")
    public static String REJECT_EVLUATION;

    /**
     * 星星打分
     */
    @UrlSuffix("mobile/eva/updateStarScore.do")
    public static String UPDATE_START;

    /**
     * 提交打分
     */
    @UrlSuffix("mobile/eva/updateStandardScore.do")
    public static String UPDATE_STANDARD_SCORE;

    /**
     * 获取下级推荐的文档
     */
    @UrlSuffix("mobile/res/doc/docRecommendList.do")
    public static String DOC_SUBORDINATE;

    /**
     * 获取我的文档
     */
    @UrlSuffix("mobile/res/doc/docResList.do")
    public static String DOC;

    /**
     * 课堂统计-开课概况统计（区域及学校数据）
     */
    @UrlSuffix("mobile/statisticsAnaly/statisticsData.do")
    public static String COURSES_PROFILE_STAT;

    /**
     * 课堂统计-筛选条件-区域、学校下学期获取
     */
    @UrlSuffix("mobile/statisticsAnaly/getTrimesters.do")
    public static String GET_TERMS;

    /**
     * 课堂统计-筛选条件-区域、学校下学科获取
     */
    @UrlSuffix("mobile/statisticsAnaly/getSubject.do")
    public static String GET_SUBJECTS;

    /**
     * 开课比统计获取基础数据
     */
    @UrlSuffix("mobile/statisticsAnaly/getClassRatioBaseInfo.do")
    public static String COURSES_PROPORTION_STAT_BASE;

    /**
     * 开课比按月统计
     */
    @UrlSuffix("mobile/statisticsAnaly/getMonthStatisticData.do")
    public static String COURSES_PROPORTION_STAT_MONTH;

    /**
     * 开课比按学期统计
     */
    @UrlSuffix("mobile/statisticsAnaly/getTrimesterStatisticData.do")
    public static String COURSES_PROPORTION_STAT_TERM;

    @UrlSuffix("mobile/statisticsAnaly/subject/org.do")
    public static String SUBJECT_STAT_AREA;

    @UrlSuffix("mobile/statisticsAnaly/subject/school.do")
    public static String SUBJECT_STAT_SCHOOL;

    /**
     * 资源统计 学校
     */
    //?uuid=2b58483a0ad34d699467eee6731f14d4&schoolId=1
    @UrlSuffix("mobile/statistic/resource/school.do")
    public static String STATISTIC_RESOURCE;

    /**
     * 资源统计 机构
     */
    //?uuid=2b58483a0ad34d699467eee6731f14d4&baseAreaId=1
    @UrlSuffix("mobile/statistic/resource/org.do")
    public static String STATISTIC_RESOURCE_ORG;

    /**
     * 活动统计 学校
     */
    //?uuid=2b58483a0ad34d699467eee6731f14d4&schoolId=2525234
    @UrlSuffix("mobile/statistic/evaluation/school.do")
    public static String STATISTIC_ACTIVITY;

    /**
     * 活动统计 机构
     */
    //?uuid=2b58483a0ad34d699467eee6731f14d4&baseAreaId=2525234
    @UrlSuffix("mobile/statistic/evaluation/org.do")
    public static String STATISTIC_ACTIVITY_ORG;

    /**
     * 资源统计 学校
     */
    //?uuid=2b58483a0ad34d699467eee6731f14d4&schoolId=dvgsdbvsd
    @UrlSuffix("mobile/statistic/schedule/school.do")
    public static String STATISTIC_CLASS;

    /**
     * 资源统计 机构
     */
    //?uuid=2b58483a0ad34d699467eee6731f14d4&statType=252525&baseAreaId=25252
    @UrlSuffix("mobile/statistic/schedule/org.do")
    public static String STATISTIC_CLASS_ORG;

    /**
     * 资源详情
     */
    @UrlSuffix("mobile/resource/getResDetailById.do")
    public static String RESOURCE_DETAILS;

    /**
     * 音频与视频播放数量自增
     */
    @UrlSuffix("mobile/resource/addResViewCnt.do")
    public static String INCREASE_VIEW_COUNT;

    /**
     * 下载数量自增
     */
    @UrlSuffix("mobile/resource/addResDownloadCnt.do")
    public static String INCREASE_DOWNLOAD_COUNT;

    /**
     * 资源列表
     */
    @UrlSuffix("mobile/res/video/getResources.do")
    public static String RESOURCE_LIST;

    /**
     * 新530资源列表
     */
    @UrlSuffix("mobile/resource/getResourcePageList.do")
    public static String RESOURCE_NEW_LIST;

    /**
     * 更多资源
     */
    @UrlSuffix("home/resource/getHomeResourcePageList.do")
    public static String MORE_RESOURCE;

    @UrlSuffix("mobile/resource/getParenrSpaceResourcePageList.do")
    public static String RESOURCE_LIST_PARENT;

    @UrlSuffix("mobile/resource/getClsSpaceResourcePageList.do")
    public static String RESOURCE_CLASS;

    /**
     * 获取资源评论
     */
    @UrlSuffix("mobile/resource/getResCommentPageList.do")
    public static String RESOURCE_COMMENTS;

    /**
     * 获取资源二级评论
     */
    @UrlSuffix("mobile/resource/getbResSuCommentPageList.do")
    public static String RESOURCE_RELIES;

    /**
     * 评论资源
     */
    //?uuid=MOBILE:5a02c6223ba041e7a2876d08a157e0e8&resourceId=1&comment=mobiletest
    @UrlSuffix("mobile/resource/addResComment.do")
    public static String COMMENT_RESOURCE;

    /**
     * 删除资源评论
     */
    @UrlSuffix("mobile/resource/deleteResComment.do")
    public static String DELETE_RES_COMMENT;

    /**
     * 专递课堂
     * 课堂巡视
     */
    @UrlSuffix("mobile/monitor/classroom/getClassWatch.do")
    public static String SPECIAL_MONITOR_CLASSROOM;

    /**
     * 专递课堂 往期录播
     */
    @UrlSuffix("scheduleDetailVideoView.html")
    public static String HISTORY_VIDEO_ONLINE_CLASS;

    /**
     * 专递课堂 直播 获取实时总在线人数
     * clsScheduleDetailId	课堂id	string
     * uuid	uuid	string
     */
    @UrlSuffix("onlineclass/getJoinUserRecord.do")
    public static String GET_CUSTOMER_LIVING_WATCH_COUNT;

    /**
     * 专递课堂 直播 获取实时总在线人数列表
     * lsScheduleDetailId	课堂id	string
     * count	每页显示条数	number
     * studentId	如果是家长需传入对应孩子的Id	string
     * updateTime	前一次加载的最后一条数据的时间	string
     * uuid	uuid	string
     */
    @UrlSuffix("onlineclass/selectJoinUserPageList.do")
    public static String GET_CUSTOMER_LIVING_WATCHER_LIST;

    /**
     * 专递课堂 区域用户往期录播 new
     */
    @UrlSuffix("onlineclass/getAreaReplyList.do")
    public static String NEW_AREA_RECORD_LIST;

    /**
     * 专递课堂 个人用户往期录播 new
     */
    @UrlSuffix("onlineclass/getSchoolReplyList.do")
    public static String NEW_PERSON_LIVE_RECORD_LIST;

    /**
     * 直录播课堂 个人用户往期录播列表 new
     */
    @UrlSuffix("mobile/schoolNet/getLiveHistoryListByschool.do")
    public static String NEW_PERSON_SCHOOL_RECORD_LIST;

    /**
     * 专递课堂 实时直播详情 new
     */
    @UrlSuffix("mobile/home/onlineclass/getScheduleLiveInfoById.do")
    public static String NEW_LIVE_ONLINE_DETAIL;

    /**
     * 专递课堂 往期录播详情 new
     */
    @UrlSuffix("mobile/home/onlineclass/getRecordVideoDetailById.do")
    public static String NEW_LIVE_RECORD_DETAIL;

    /**
     * 直录播（名校网络课堂）－ 实时直播详情 new
     * scheduleDetailId	正在直播课程id	string
     * uuid	用户唯一识别码	string
     */
    @UrlSuffix("mobile/home/live/getLiveAppointmentLiveInfoById.do")
    public static String NEW_RECORD_DETAIL＿LIVE;

    /**
     * 直录播（名校网络课堂） 往期录播详情 new
     * scheduleDetailId	正在直播课程id	string
     * uuid	用户唯一识别码	string
     */
    @UrlSuffix("mobile/home/live/getRecommendLiveAppointmentById.do")
    public static String NEW_RECORD_DETAIL_RECORD;

    /**
     * 专递课堂 往期录播详情
     */
    @UrlSuffix("home/onlineclass/getRecordVideoDetailById.do")
    public static String NEW_RECORD_ONLINE_DETAIL;


    //---------------------------专递课堂中的评论-----------------------------------------------------//
    /**
     * 专递课堂 最新评论列表
     */
    @UrlSuffix("mobile/home/onlineclass/getAllComment.do")
    public static String NEW_GET_ALL_ONLINE_COMMNET;

    /**
     * 专递课堂 发表一级评论接口
     */
    @UrlSuffix("mobile/home/onlineclass/addFirstComment.do")
    public static String NEW_ADD_FIRST_ONLINE_COMMENT;

    /**
     * 专递课堂 回复评论
     */
    @UrlSuffix("mobile/home/onlineclass/addSecondComment.do")
    public static String NEW_REPLY_ONLINE_COMMENT;

    /**
     * 专递课堂 删除评论
     */
    @UrlSuffix("mobile/home/onlineclass/delOneComment.do")
    public static String NEW_DELETE_ONLINE_COMMENT;

    /**
     * 专递课堂 获取更多二级评论
     */
    @UrlSuffix("mobile/home/onlineclass/getSecondComment.do")
    public static String NEW_GET_MORE_SEC_ONLINE_COMMENT;

    //---------------------------直录播课堂中的评论-----------------------------------------------------//
    /**
     * 直录播课堂 最新评论列表
     */
    @UrlSuffix("mobile/home/live/getAllComment.do")
    public static String NEW_GET_ALL_SCHOOL_COMMNET;

    /**
     * 直录播课堂 发表一级评论接口
     */
    @UrlSuffix("mobile/home/live/addFirstComment.do")
    public static String NEW_ADD_FIRST_SCHOOL_COMMENT;

    /**
     * 直录播课堂 回复评论
     */
    @UrlSuffix("mobile/home/live/addSecondComment.do")
    public static String NEW_REPLY_SCHOOL_COMMENT;

    /**
     * 直录播课堂 删除评论
     */
    @UrlSuffix("mobile/home/live/delOneComment.do")
    public static String NEW_DELETE_SCHOOL_COMMENT;

    /**
     * 直录播课堂 获取更多二级评论
     */
    @UrlSuffix("mobile/home/live/getSecondComment.do")
    public static String NEW_GET_MORE_SEC_SCHOOL_COMMENT;

    /**
     * 课堂作业的课堂列表
     */
    @UrlSuffix("classWork/getClassList.do")
    public static String NEW_GET_HOMEWORK_CLASS_LIST;

    /**
     * 课堂的课堂作业列表
     */
    @UrlSuffix("classWork/classWorkList.do")
    public static String NEW_GET_HOMEWORK_LIST;

    /**
     * 名校网络课堂 往期录播
     */
    @UrlSuffix("liveAppointmentVideoView.html")
    public static String HISTORY_VIDEO_ONLINE_VIDEO;

    /**
     * 名校网络课堂 区域往期录播列表 new
     */
    @UrlSuffix("mobile/schoolNet/getLiveHistoryRecordByarea.do")
    public static String GET_HISTORY_SCHOOL_RECORD_AREA_LIST;

    /**
     * 直录播课堂（名校网络课堂）
     * 课堂巡视
     */
    @UrlSuffix("mobile/schoolNet/appointmentPagelist/tour.do")
    public static String NET_MONITOR_CLASSROOM;

    /**
     * 名校网络课堂 往期录播课程回放
     */
    @UrlSuffix("mobile/schoolNet/playBackVideo.do")
    public static String SCHOOLNET_VIDEO_BACK;

    /**
     * 评课点播
     * http://172.17.53.53:8080/mobile/eva/getEvaVideoByVideoId.do?uuid=MOBILE:0ec4e74b21a249c4a147580e7e42650a&videoId=1
     */
    @UrlSuffix("mobile/eva/getEvaVideoByVideoId.do")
    public static String GET_EVAVIDEO_BYVIDEO_BYID;
    /**
     * 首页网络教研视频
     * meetingVideoView.do
     */
    @UrlSuffix("meetingVideoView.do")
    public static String GET_MEETING_VIDEO_VIEW;
    /**
     * 播放专递课堂的视频
     * scheduleDetailVideoView.html?
     */
    @UrlSuffix("scheduleDetailVideoView.html")
    public static String GET_SCHEDULEDETAIL_VIDEO;
    /**
     * 播放名校网络课堂的视频
     * liveAppointmentVideoView.html
     */
    @UrlSuffix("liveAppointmentVideoView.html")
    public static String GET_LIVE_APPOITMENT_VIDEO;

    @UrlSuffix("meetingVideoView.do")
    public static String GET_VIDEO;
    /**
     * 获取受邀评课学科、老师
     */
    @UrlSuffix("mobile/eva/getEvaSubjTea.do")
    public static String GET_EVASUBJTEA;

    /**
     * 获得老师列表
     */
    @UrlSuffix("mobile/eva/getSchoolTeachers.do")
    public static String GET_SCHOOL_TEACHER;

    /**
     * 设置参与老师
     */
    @UrlSuffix("mobile/eva/addEvaluationTeachers.do")
    public static String ADD_EVALUATION_TEACHER;

    /**
     * 直播课堂
     */
    @UrlSuffix("mobile/monitor/classroom/listLiveBroadcast.do")
    public static String URL_LIVE_LESSON;//getLiveClass

    /**
     * 直播页面
     */
    @UrlSuffix("mobile/schoolNet/appointment/live/detail.do")
    public static String URL_SCHCOOL_NET_LESSON;
    /**
     * 历史资源（往期录播）
     */
    @UrlSuffix("mobile/monitor/classroom/listHistoryBroadcast.do")
    public static String URL_HISTORY_LESSON;//getHistoryClass
    /**
     * 往期录播分段视频获取
     */
    //public static String URL_HISTORY_VIDEO_LIST = BASE + "/mobile/monitor/schedule/videoList.do";
    @UrlSuffix("mobile/monitor/classroom/playBackVideo.do")
    public static String URL_HISTORY_VIDEO_LIST;

    /**
     *
     */
    @UrlSuffix("mobile/eva/getEvaVideoByVideoId.do")
    public static String URL_PLAY_VIDEO_HTTP;

    /**
     * 资源搜索 （视频/文档）
     */
    @UrlSuffix("mobile/res/searchResList.do")
    public static String URL_SEARCH_RESOURCE;

    /**
     * 修改密码
     */
    @UrlSuffix("mobile/modifyPassword.do")
    public static String URL_CHANGE_PASSWORD;

    /**
     * 下载资源视频的基本路径 参数：uuid$resourceId$defineType=normalDefine
     */
    @UrlSuffix("mobile/res/downloadVideo.do")
    public static String URL_DOWNLOAD_RESOURCE;

    /**
     * 专递课堂巡视多个url获取
     */
    @UrlSuffix("mobile/monitor/classroom/getVideoList.do")
    public static String SPECIAL_DELIVERY_CLASSROOM_VIDEOS;

    @UrlSuffix("mobile/monitor/classroom/getClassDetail.do")
    public static String SPECIAL_DELIVERY_CLASSROOM_CLASS_DETAIL;

    /**
     * 直录播课堂巡视多个url获取
     */
    @UrlSuffix("mobile/schoolNet/getClassWatchAll.do")
    public static String SCHOOL_NET_CLASSROOM_VIDEOS;
    @UrlSuffix("mobile/schoolNet/getLiveAppointmentInfo.do")
    public static String SCHOOL_NET_CLASSROOM_APPOINTMENT_INFO;

    /**
     * 筛选：视频分类
     */
    @UrlSuffix("mobile/commons/getVideosCategory.do")
    public static String VIDEO_CATEGORY;

    /**
     * 筛选：文档分类
     */
    @UrlSuffix("mobile/commons/getDocsCategory.do")
    public static String DOCUMENT_CATEGORY;

    /**
     * 筛选学段
     */
    @Deprecated
    @UrlSuffix("commons/data/getAllSemester.do")
    public static String SEMESTERS;

    /**
     * 筛选：年级
     */
    @UrlSuffix("bClasslevel/getClasslevelByPsemesterId.do")
    public static String CLASS_LEVELS;

    /**
     * 筛选：科目
     */
    @UrlSuffix("bSubject/getSubjectsByPclasslevelId.do")
    public static String SUBJECTS;

    /**
     * 筛选：版本
     */
    @UrlSuffix("bVersion/getVersionByPclasslevelAndPsubject.do")
    public static String VERSIONS;

    /**
     * 筛选：分册
     */
    @UrlSuffix("bVolume/getVolumnByClasslevelSubjectVersion.do")
    public static String VOLUMES;

    /**
     * 筛选：章
     */
    @UrlSuffix("bChapter/getChaptersByVolumeId.do")
    public static String CHAPTERS;

    /**
     * 筛选：节
     */
    @UrlSuffix("bSection/getSectionByChapterId.do")
    public static String SECTIONS;

    /**
     * 获取一级分类
     */
    @UrlSuffix("bResCategory/getAllFirstCategory.do")
    public static String CATEGORIES;

    /**
     * 获取二级分类
     */
    @UrlSuffix("bResCategory/getCategoryByParentId.do")
    public static String SON_CATEGORIES;

    /**
     * 根据学校id查询年级信息
     * schoolId
     */
    @UrlSuffix("bClasslevel/getClasslevelsBySchoolId.do")
    public static String ALL_CLASS_LEVEL_BY_SCHOOL_ID;

    /**
     * 根据学校id 年级id 学科id 查询老师
     */
    @UrlSuffix("personinfo/getTeacherByClass.do")
    public static String GET_TEACHER_BY_CLASS;

    @UrlSuffix("personinfo/getUserTypeByBaseUserId.do")
    public static String GET_USER_TYPE;

    /**
     * 根据学校id查询年级信息
     * areaId 区域ID和学校ID有且只有一个有值
     * schoolId 区域ID和学校ID有且只有一个有值
     */
    @UrlSuffix("bClasslevel/getClasslevels.do")
    public static String ALL_CLASS_LEVEL;
    /**
     * 根据年级id查询学科信息
     * pClasslevelId    上一级(年级)返回的id	string
     */
    @UrlSuffix("bSubject/getSubjectsByPclasslevelId.do")
    public static String ALL_SUBJECTS_BY_CLASS_ID;

    /**
     * 根据年级id查询班级信息
     * ///bClassRoom/getClassRoomByClassLevelId.do
     */
    @UrlSuffix("mobile/bClassRoom/getSchoolClassRoomByClassLevelId.do")
    public static String ALL_CLASS_BY_CLASSLEVEL_ID;

    /**
     * 获取所有学科,列表形式
     */
    @UrlSuffix("bSubject/getAllSubjects.do")
    public static String ALL_SUBJECTS_LIST;

    /**
     * get root knowledge by semester id and subject id.
     */
    @UrlSuffix("bKnowledge/getFirstKnowledgeBySemesterSubject.do")
    public static String ROOT_KNOWLEDGE;

    /**
     * get child knowledge by parent knowledge id.
     */
    @UrlSuffix("bKnowledge/getKnowledgeByParentId.do")
    public static String KNOWLEDGE;

    /**
     * 获取最新版本
     */
    @UrlSuffix("app/version/getAndroidPhoneCurrentVersionInfo.do")
    public static String VERSION;

    /**
     * 图片映射路径
     */
    @UrlSuffix("images/")
    public static String CLASS_IMAGES;

    //    public static String CLASS_IMAGES = TEMP_SERVER4 + "images/";
    /**
     * 地区选择
     */

    @UrlSuffix("commons/data/getAreasAndLevelByParentId.do")
    public static String LOCATION;

    @UrlSuffix("commons/data/getSchoolByAreaId.do")
    public static String LOCATION_SCHOOL;

    /**
     * 资讯删除
     */
    @UrlSuffix("mobile/information/delete.do")
    public static String INFO_DELETE;

    /**
     * 得到家长的孩子信息
     * uuid
     * userId
     */
    @UrlSuffix("mobile/workspace/getParentChildren.do")
    public static String PARENT_CHILDREN;

    /**
     * 得到家长的孩子信息
     * uuid
     * userId
     */
    @UrlSuffix("myHome/getParentChildren.do")
    public static String GET_PARENT_CHILDREN;

    /**
     * 得到家长的孩子信息(访客模式　：　公共开放ＡＰＩ)
     * uuid
     * userId
     */
    @UrlSuffix("getParentChildren.do")
    public static String GET_PUBLIC_PARENT_CHILDREN;

    /**
     * 名校网络课堂接口(学校)
     */
    //    public static String FAMOUS_SCHOOL_CLASS="http://10.1.210.103:8080/classWork/getClassList.do?uuid=MOBILE:1dde6218a68947f9b85b69830c670e10&baseUserId=bb8a193dd585452d98c927faf7ad16b5&start=0&end=29&type=LIVE&userType=SCHOOL";
    @UrlSuffix("classWork/getClassList.do")
    public static String FAMOUS_SCHOOL_CLASS;

    /**
     * 作业列表（名校网络）
     */
    @UrlSuffix("classWork/classWorkList.do")
    public static String HOMEWORK_LIST_FAMOUS;

    /**
     * 上传头像
     */
    @UrlSuffix("ImageUploadServlet")
    public static String UPLOAD_HEADER_PIC;

    /**
     * 资讯
     */
    @UrlSuffix("mobile/information/list.do")
    public static String APP_INFO;
    /**
     * 修改密码
     */
    @UrlSuffix("mobile/modifyPassword.do")
    public static String CHANGE_PASS;
    /**
     * 退出登录
     */
    @UrlSuffix("logout.do")
    public static String LOGOUT;

    /**
     * 获取用户详情
     */
    @UrlSuffix("getUserInfoByUserId.do")
    public static String URL_USER_DETAIL;

    /**
     * 作业上传接口
     * http://172.17.53.220:8080/classWork/classWorkUpload.do?clsClassroomId=&classId=
     */
    @UrlSuffix("classWork/classWorkUpload.do")
    public static String HOMEWORK_UPLOAD;

    /**
     * 首页资讯轮播图接口
     */
    @UrlSuffix("index/getIndexNewsWithThumb.do")
    public static String HOME_NEWS_SLIDE;
    /**
     * 首页资讯新闻接口
     */
    @UrlSuffix("index/getIndexNewsExceptPartThumbNews.do")
    public static String HOME_NEWS;

    /**
     * 首页资讯通知接口
     */
    @UrlSuffix("index/getIndexInformation.do")
    public static String HOME_NOTIFICATION;

    /**
     * 首页资讯公告接口
     */
    @UrlSuffix("index/getIndexInformation.do")
    public static String HOME_ANNOUNCEMENT;

    /**
     * 获取资讯详情
     */
    @UrlSuffix("index/getInformationDetail.do")
    public static String INFORMATION_DETAIL;

    /**
     * 获取首页新闻图片
     * ?size=3&baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=
     */
    @UrlSuffix("index/getIndexNewsWithThumb.do")
    public static String GET_INDEX_NEWS_WHITHTHUMB;
    /**
     * /index/getIndexAllNews.do
     */
    @UrlSuffix("index/getIndexAllNews.do")
    public static String GET_INDEX_ALL_NEWS;

    /**
     * 获取资讯详情
     */
    @UrlSuffix("index/informationView/")
    public static String GET_NEW_DELTAIL;

    @UrlSuffix("index/getInformationList.do")
    public static String MORE_INFORMATION;

    /**
     * 获取优课资源
     * /index/getIndexRecommendResource.do
     */
    @UrlSuffix("index/getIndexRecommendResource.do")
    public static String GET_RECOMMEND_RESOURCE;

    /**
     * 首页直播课堂
     * /index/getIndexScheduleLive.do?size=3&baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=
     */
    @UrlSuffix("index/getIndexScheduleLive.do")
    public static String GET_INDEX_SCHEDULE_LIVE;

    /**
     * 新闻、通告
     * http://172.17.53.6:8080/index/getIndexMixInformation.do?eachSize=1&thumbCount=4&baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId
     */
    @UrlSuffix("index/getIndexMixInformation.do")
    public static String GET_MIXINFORMATION;

    /**
     * 获取名师推荐
     */
    @UrlSuffix("index/getIndexTopUser.do")
    public static String GET_FAMOUS_TEACHER;

    /**
     * 专递课堂正在直播
     */
    @UrlSuffix("mobile/home/onlineclass/getIndexScheduleLiveList.do")
    public static String GET_SCHEDULE_LIVE;

    /**
     * 专递课堂-实时直播列表
     */
    @UrlSuffix("onlineclass/listAreaSchoolBroadcast.do")
    public static String GET_ONLINE_LIVE_CLASS_LIST;

    /**
     * 直录播课堂-实时直播列表
     */
    @UrlSuffix("mobile/schoolNet/getDirectSeedingList.do")
    public static String GET_SCHOOL_LIVE_CLASS_LIST;
    /**
     * 专递课堂课堂回放
     * http://172.17.53.6:8080/index/getRecommendSchedule.do?size=7&baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=
     */
    @UrlSuffix("mobile/home/onlineclass/getIndexRecordScheduleList.do")
    public static String GET_RECOMMEND_SCHEDULE;

    /**
     * 首页网络调研
     */
    @UrlSuffix("mobile/netTeach/index.do")
    public static String GET_TEACHERING_RESEARCH;

    /**
     * 首页网络调研-集体备课
     * /mobile/netTeach/viewMore/prepareLesson.do?uuid={uuid}&start={start}&end={end}&userType={userType}&schoolId={schoolId}&areaId={areaId}&sortType={sortType}&orderType={orderType}
     */

    @UrlSuffix("mobile/netTeach/viewMore/prepareLesson.do")
    public static String GET_PREPARE_LESSON;


    /**
     * 首页网络调研-互动听课
     * /mobile/netTeach/viewMore/interactLesson.do?uuid={uuid}&start={start}&end={end}&userType={userType}&schoolId={schoolId}&areaId={areaId}
     */
    @UrlSuffix("mobile/netTeach/viewMore/interactLesson.do")
    public static String GET_INTERAC_LESSON;
    /**
     * 首页网络调研-互动听课详情
     * netTeach/viewDetail/interactLesson.do
     */
    @UrlSuffix("mobile/Lecture/getLectureDetail.do")
    public static String GET_INTERAC_DETAIL;
    /**
     * 首页网络调研-评课议课
     * /mobile/netTeach/viewMore/evaluation.do?uuid={uuid}&start={start}&end={end}&userType={userType}&schoolId={schoolId}&areaId={areaId}
     */
    @UrlSuffix("mobile/netTeach/viewMore/evaluation.do")
    public static String GET_EVALUATION_LESSON;

    /**
     * 评课议课详情
     * netTeach/viewDetail/evaluation.do
     */
    @UrlSuffix("mobile/eva/getEvaluationDetail.do")
    public static String GET_EVALUATION_DETAIL;

    /**
     * 频道页资源页
     */
    @UrlSuffix("home/resource/getLatestSemesterResourceList.do")
    public static String RESOURCE_INTRO;

    /**
     * 发起的备课
     * http://10.1.200.64:8080/mobile/Preparation/getSponsorPreparation.do?uuid=MOBILE:36f34b7682d1409c9dd6a73d9616f1dc&status=INIT&start=0&end=10
     */
    @UrlSuffix("mobile/Preparation/getSponsorPreparation.do")
    public static String GET_SPONSOR_PREPARATION;

    /**
     * 参与的备课
     * http://192.168.100.110:8080/mobile/Preparation/getParticipantPreparation.do?uuid=MOBILE:36f34b7682d1409c9dd6a73d9616f1dc&status=INIT&start=0&end=10
     */
    @UrlSuffix("mobile/Preparation/getParticipantPreparation.do")
    public static String GET_PARTICIPANT_PREPARATION;

    /**
     * 管辖区内的备课
     * http://192.168.100.110:8080/mobile/Preparation/getAreaPreparation.do?uuid=MOBILE:36f34b7682d1409c9dd6a73d9616f1dc&status=INIT&start=0&end=10
     */
    @UrlSuffix("mobile/Preparation/getAreaPreparation.do")
    public static String GET_AREA_PREPARATION;

    /**
     * 发起的听课
     * http://10.1.200.64:8080/networkClassroom/getSponsorLecture.do?uuid=MOBILE:36f34b7682d1409c9dd6a73d9616f1dc&status=INIT&start=0&end=1
     */
    @UrlSuffix("mobile/Lecture/getSponsorLecture.do")
    public static String GET_SPONSOR_LECTURE;

    /**
     * 参与的听课
     * http://192.168.100.110:8080/mobile/Preparation/getParticipantPreparation.do?uuid=MOBILE:36f34b7682d1409c9dd6a73d9616f1dc&status=INIT&start=0&end=10
     */
    @UrlSuffix("mobile/Lecture/getParticipantLecture.do")
    public static String GET_PARTICIPANT_LECTURE;

    /**
     * 管辖区内的听课
     * http://192.168.100.110:8080/mobile/Preparation/getAreaPreparation.do?uuid=MOBILE:36f34b7682d1409c9dd6a73d9616f1dc&status=INIT&start=0&end=10
     */
    @UrlSuffix("mobile/Lecture/getAreaLecture.do")
    public static String GET_AREA_LECTURE;

    /**
     * 集体备课视频列表
     */
    @UrlSuffix("mobile/Preparation/getPreparationVideos.do")
    public static String GET_PREPARATION_VIDEO_LIST;
    /**
     * 互动听课视频列表
     */
    @UrlSuffix("mobile/Lecture/getLectureVideos.do")
    public static String GET_LECTURE_VIDEO_LIST;
    /**
     * 集体备课评论
     */
    @UrlSuffix("mobile/netTeach/getComment.do")
    public static String VIDEO_PREPARATION_COMMENTS;

    /**
     * 首页-直录播(名校网络课堂)-正在直播
     * baseAreaId
     * schoolId
     * size
     * uuid
     */
    @UrlSuffix("mobile/home/live/getIndexLiveAppointmentList.do")
    public static String GET_INDEX_LIVE_APPOINTMENT;

    /**
     * 首页-直录播(名校网络课堂)-推荐课程
     * baseAreaId
     * schoolId
     * size
     * uuid
     */
    @UrlSuffix("mobile/home/live/getIndexRecommendLiveAppointmentList.do")
    public static String GET_INDEX_LIVE_APPOINTMENT_RECOMMEND;


    @UrlSuffix("bClasslevel/getClasslevels.do")
    public static String GET_GRADE_UNLOGIN;
    /**
     * 获取互动听课视频评论
     */
    @UrlSuffix("mobile/Lecture/getLectureComments.do")
    public static String VIDEO_LECTURE_COMMENTS;
    /**
     * 获取视频会议视频评论
     */
    @UrlSuffix("mobile/remote/getRemoteComments.do")
    public static String GET_ONLINE_VIDEO_LECTURE_COMMENTS;
    /**
     * 获得所有学科，不需要登陆
     * commons/data/getAllSubjects.do
     * 发送集体备课视频评论
     */
    @UrlSuffix("bSubject/getAllSubjects.do")
    public static String GET_SUBJECT_UNLOGIN;

    /**
     * 发送集体备课评论
     */
    @UrlSuffix("mobile/Preparation/addComment.do")
    public static String POST_PREPARATION_VIDEO_COMMENTS;
    /**
     * 圈组集体备课
     * mobile/group/groupMeetList.do
     */
    @UrlSuffix("mobile/group/groupMeetList.do")
    public static String GROUP_MEET_LIST;
    /**
     * 发送互动听课评论
     */
    @UrlSuffix("mobile/Lecture/addComment.do")
    public static String POST_LECTURE_VIDEO_COMMENTS;
    /**
     * 发送视频会议评论
     */
    @UrlSuffix("mobile/remote/addComment.do?")
    public static String POST_ONLINE_VIDEO_COMMENTS;

    /**
     * 获取视频会议列表
     */
    @UrlSuffix("mobile/remote/getVideoMeets.do")
    public static String GET_VIDEOMEETING;
    /**
     * 获取视频会议详情
     */
    @UrlSuffix("mobile/remote/getVideoMeetDetail.do")
    public static String GET_VIDEOMEETING_DETAIL;

    /**
     * 获取视频会议已经完成的视频列表 .
     */
    @UrlSuffix("mobile/remote/getRemoteVideos.do")
    public static String GET_ONLINE_MEETING_VIDEOS;

    /**
     * 获取文档管理的列表
     */
    @UrlSuffix("mobile/remote/getVideoMeetDocuments.do")
    public static String GET_VIDEODOCUMENT_DETAIL;

    /**
     * 主动打开某个文档
     */
    @UrlSuffix("mobile/remote/getVideoMeetDocumentInfo.do")
    public static String POST_OPEN_ONLINE_MEETING_DOCUMENT;

    /**
     * 获取某个文档内容
     */
    @UrlSuffix("mobile/remote/getVideoMeetDocumentsContent.do")
    public static String GET_VIDEOMEETING_DOCUMENT_CONTENT;

    /**
     * get the online meeting resource in interact pager
     * 获取在线视频会议的基本信息
     * 发送互动听课视频评论
     */
    @UrlSuffix("mobile/remote/getOnlineVideoMeetResource.do")
    public static String GET_ONLINE_MEETING_RESOURCE;

    /**
     * 获取共享桌面信息 / 视频共享/桌面共享
     */
    @UrlSuffix("mobile/remote/getMeetingInfo.do")
    public static String GET_ONLINE_MEETING_SHARE_INFO;
    /**
     * 获取共享视频的基本信息
     */
    @UrlSuffix("mobile/remote/getVideoShare.do")
    public static String GET_ONLINE_MEETING_SHARE_VIDEO;
    /**
     * 获取共享桌面的基本信息
     */
    @UrlSuffix("mobile/remote/getRemoteState.do")
    public static String GET_ONLINE_MEETING_SHARE_DESKTOP;
    /**
     * 获取演示详情 .
     */
    @UrlSuffix("mobile/remote/getOnlineVideoMeetShowDetail.do")
    public static String GET_ONLINE_MEETING_SHOW_DETAIL;
    /**
     * 设置演示文档进度页码
     */
    @UrlSuffix("mobile/remote/setOnlineVideoMeetShowIndex.do")
    public static String SET_ONLINE_MEETING_SHOW_INDEX;
    /**
     * 视频会议-取消发言人
     */
    @UrlSuffix("mobile/remote/cancelSpeaker.do")
    public static String SET_ONLINE_MEETING_SPEAKER_DISABLE;
    /**
     * 切换视频/演示 mode : 0:视频 1:演示
     *
     * @deprecated v5.3.0开始只有主讲人才有权限对演示模式进行切换 .
     */
    @UrlSuffix("mobile/remote/setOnlineVideoMeetModel.do")
    public static String SET_ONLINE_MEETING_MODEL;
    /**
     * 切换:演示文档标签页切换
     */
    @UrlSuffix("mobile/remote/setOnlineVideoMeetShowDocument.do")
    public static String SET_ONLINE_MEETING_SHOW_DOCUMENT;
    /**
     * 删除 :演示文档-标签页面删除
     */
    @UrlSuffix("mobile/remote/deleteOnlineVideoMeetShowDocument.do")
    public static String DELETE_ONLINE_MEETING_SHOW_DOCUMENT;

    /**
     * 获取视频会议的用户列表
     */
    @UrlSuffix("mobile/remote/getVideoMeetPartners.do")
    public static String GET_ONLINE_MEETING_USER_LIST;
    /**
     * 集体备课详情
     */
    @UrlSuffix("mobile/Preparation/getPreparationDetail.do")
    public static String GET_PREPARE_DETAILS;

    /**
     * 集体备课详情
     * http://172.17.53.129:8080/mobile/Preparation/getPreparationDetail.do?uuid=MOBILE:06e85b10b1ea44d6bbf11e73d61c4e8d&preparationId=41d9cfecc98b46788bf59ea2a76cb9fa
     */
    @UrlSuffix("mobile/Preparation/getPreparationDetail.do")
    public static String GET_PREPARATION_DETAIL;

    /**
     * 互动听课详情
     * http://172.17.53.129:8080/mobile/Preparation/getPreparationDetail.do?uuid=MOBILE:06e85b10b1ea44d6bbf11e73d61c4e8d&preparationId=41d9cfecc98b46788bf59ea2a76cb9fa
     */
    @UrlSuffix("mobile/Lecture/getLectureDetail.do")
    public static String GET_LECTURE_DETAIL;
    /**
     * 获取约课列表
     */
    @UrlSuffix("mobile/schoolNet/getLiveSchoolInfoList.do")
    public static String GET_RESERVATION_CLASS;

    /**
     * 获取约课详情
     */
    @UrlSuffix("mobile/schoolNet/getLiveSchoolInfoDetail.do")
    public static String GET_LIVE_SCHOOL_INFODETAIL;
    /**
     * 获取约课课表详情
     */
    @UrlSuffix("mobile/schoolNet/getLiveInfo.do")
    public static String GET_LIVE_INFO;
    /**
     * /MobileInterface/mobile/schoolNet/appointmentDetail.do?uuid={uuid}&mid={mid}
     */
    @UrlSuffix("mobile/schoolNet/appointmentDetail.do")
    public static String GET_APPOINTMENTDETAIL;

    /**
     * 获取远程导播列表
     * http://172.17.53.234:8080//MobileInterface/mobile/schoolNet/appointmentPageList/live_record.do
     */
    @UrlSuffix("mobile/schoolNet/appointmentPageList/live_record.do")
    public static String GET_DIRECTOR_LIST;

    /**
     * 获取专递课堂-远程导播列表 new
     */
    @UrlSuffix("onlineclass/director.do")
    public static String GET_NEW_ONLINE_DIRECTOR_LIST;

    /**
     * 获取直录播课堂-远程导播列表 new
     */
    @UrlSuffix("mobile/schoolNet/getLiveRecordView.do")
    public static String GET_NEW_SCHOOL_DIRECTOR_LIST;

    /**
     * 获取远程导播配置
     * http://172.17.55.86:8080/MobileInterface/mobile/remote/getRemoteDirectorConfig.do?mid=687872139a774f23a8e6411936832d00&meetType=LIVE
     */
    @UrlSuffix("mobile/remote/getRemoteDirectorConfig.do")
    public static String REMOTE_DIRECTOR_CONFIG;
    /**
     * 资讯搜索
     * http://10.1.210.102:8080/MobileInterface/search/infoSearchAction.do?baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=&start=0&end=9&key=
     */
    @UrlSuffix("search/infoSearchAction.do")
    public static String INFO_SEARCH_ACTION;
    /**
     * 视频搜索
     * http://10.1.210.102:8080/MobileInterface/search/videoSearchAction.do?baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=&start=0&end=9&key=
     */
    @UrlSuffix("search/videoSearchAction.do")
    public static String VIDEO_SEARCH_ACTION;
    /**
     * 圈组搜索
     * search/groupSearchAction.do
     */
    @UrlSuffix("search/groupSearchAction.do")
    public static String GROUP_SEARCH_ACTION;
    /**
     * 文档搜索
     * http://10.1.210.102:8080/MobileInterface/search/docSearchAction.do?baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=&start=0&end=9&key=
     */
    @UrlSuffix("search/docSearchAction.do")
    public static String DOC_SEARCH_ACTION;

    /**
     * 获得班级列表
     * baseUserId
     */
    @UrlSuffix("myHome/getTeacherClassList.do")
    public static String GET_TEACHER_CLASS_LIST;

    /**
     * 获得课表
     * mobile/myHome/getSelfOrClassScheduleDetail.do
     */
    @UrlSuffix("myHome/getSelfOrClassScheduleDetail.do")
    public static String GET_TEACHER_TIMETABLE;
    /**
     * 获取班级老师
     * mobile/myHome/getTeacherClassMemberDetail.do
     */
    @UrlSuffix("myHome/getTeacherClassMemberDetail.do")
    public static String GET_TEACHER_CLASS_MEMBERDETAIL;
    /**
     * 获取学生详情
     * mobile/myHome/getStudentDetail.do
     */
    @UrlSuffix("myHome/getStudentDetail.do")
    public static String GET_STUDENT_DETAIL;
    /**
     * 获取约课老师列表
     * mobile/schoolNet/teacherList.do?uuid=123&classroomId=4dd6757e4878419496f57dbc7b0e6087
     */
    @UrlSuffix("mobile/schoolNet/teacherList.do")
    public static String GET_TEACHER_LIST;
    /**
     * 获取所有学科，需登录
     */
    @UrlSuffix("mobile/commons/getAllSubjects.do")
    public static String GET_ALL_SUBJECT;
    /**
     * 获取所有学科，需要登录
     */
    @UrlSuffix("mobile/commons/getAllClasslevels.do")
    public static String GET_ALL_GRADE;

    @UrlSuffix("mobile/monitor/schedule/directorDetailListById.do")
    public static String GET_DIRECTOR_FOR_CLASS;


    /**
     * 获取网络教研-评课议课评论
     */
    @UrlSuffix("mobile/eva/getEvaComments.do")
    public static String GET_COMMENT_LSIT;
    @UrlSuffix("mobile/netTeach/getComment.do")
    public static String GET_COMMENT_NETTEACH;
    /**
     * 集体备课发表评论
     */
    @UrlSuffix("mobile/netTeach/addComment.do")
    public static String ADD_COMMENT_PREPARATION;
    /**
     * 互动听课发表评论
     */
    @UrlSuffix("mobile/netTeach/addComment.do")
    public static String ADD_COMMENT_LECTURE;
    /**
     * 互动听课删除评论
     */
    @UrlSuffix("mobile/netTeach/deleteComment.do")
    public static String DELETE_COMMENT_LECTURE;
    /**
     * 评课议课发表评论
     */
    @UrlSuffix("mobile/eva/sendEvaComment.do")
    public static String ADD_COMMENT_EVA;
    /**
     * 首页课程回放详情
     * index/getScheduleViewDetail.do
     */
    @UrlSuffix("index/getScheduleViewDetail.do")
    public static String GET_SCHEDULE_DETAIL;
    /**
     * 获取名校网络课堂详情
     * index/getLiveAppointmentDetail.do
     */
    @UrlSuffix("index/getLiveAppointmentDetail.do")
    public static String GET_LIVE_APPOINTMENT_DETAIL;

    /**
     * 专递课堂评论
     * http://172.17.53.6:8080/MobileInterface/index/getScheduleCommentList.do?scheduleDetailId=e7ab6f39a4ca433fba4ef6feb968f690&start=0&end=9
     */
    @UrlSuffix("index/getScheduleCommentList.do")
    public static String GET_SCHEDULE_COMMENTLIST;
    /**
     * 专递课堂 课程回放评论
     * mobile/addScheduleDetailComment.do
     */
    @UrlSuffix("mobile/addScheduleDetailComment.do")
    public static String ADD_SCHEDULE_COMMENT;
    /**
     * index/getLiveAppointmentComment.do
     * 获取名校网络课堂评论
     */
    @UrlSuffix("index/getLiveAppointmentComment.do")
    public static String GET_LIVE_APPOINTMENT_COMMENT;
    /**
     * mobile/addLiveAppointmentComment.do
     * 添加名校网络课堂评论
     */
    @UrlSuffix("mobile/addLiveAppointmentComment.do")
    public static String ADD_LIVE_APPOINTMENT_COMMENT;

    /**
     * 首页（资源）的资源幻灯片
     * ?size=4&baseAreaId=3268c4982fbd47b996de356d00d8adcc
     */
    @UrlSuffix("index/getIndexRecommendResource.do")
    public static String SLIDE_RESOURCES;

    /**
     * 首页（资源）要显示的资源
     * ?categorySize=3&resourceSize=2&baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=
     */
    @UrlSuffix("index/getIndeResourceWithCategory.do")
    public static String MAIN_RESOURCES;

    /**
     * 首页（资源）直播课堂
     * ?size=3&baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=
     */
    @UrlSuffix("index/getIndexScheduleLive.do")
    public static String MAIN_LIVE_CLASSROOM;

    /**
     * 首页获取直播权限
     */
    @UrlSuffix("index/getLiveVisitPermission.do")
    public static String MAIN_LIVE_PERMISSION;

    /**
     * 首页（资源无直播）圈组
     */
    @UrlSuffix("index/findHotGroup.do")
    public static String MAIN_GROUPS;

    @UrlSuffix("index/getHomeBlog.do")
    public static String MAIN_BLOG;

    /**
     * 首页（资源）名师推荐
     * ?size=4&baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=
     */
    @UrlSuffix("index/getIndexTopUser.do")
    public static String MAIN_TEACHER_RECOMMENDED;
    /**
     * 课堂作业获取老师列表
     */
    @UrlSuffix("classWork/getAllTeacherList.do")
    public static String GET_HOMEWORK_TEACHERLIST;

    /**
     * index/canVisitScheduleLive.do
     */
    @UrlSuffix("index/canVisitScheduleLive.do")
    public static String CAN_VISITSCHEDULE_LIVE;

    @UrlSuffix("index/new/canVisitScheduleLive.do")
    public static String CHECK_PERMISSION;

    /**
     * 老师登录，获取课表详情
     * http://172.17.55.72:8080/MobileInterface/mobile/schedule/getTeacherScheduleList.do?uuid=MOBILE:5b569e28e0b743c8a57f65575a8e62e3&weekSeq=0
     */
    @UrlSuffix("mobile/schedule/getTeacherScheduleList.do")
    public static String GET_TEACHER_SCHEDULELITE;

    /**
     * 个人空间微博接口
     */
    @UrlSuffix("mobile/micblog/dynamic/selectDynamicListByUserId.do")
    public static String SELECT_DYNAMIC_LIST;

    /**
     * 更多个人备课
     */
    @UrlSuffix("home/prepare/lessonPlan/lessonPlanList.do")
    public static String MORE_PERSONAL_LES_PREP;

    @UrlSuffix("mobile/prepare/lessonPlan/prepareLessonOfApplication.do")
    public static String PERSONAL_LESSON_PLANS;

    /**
     * 圈组个人备课
     */
    @UrlSuffix("group/preparLesson/getGroupPersonalPreparationUnPaged.do")
    public static String GROUP_PERSONAL_LESSON_PLANS;

    /**
     * 圈组个人备课全部
     * /getGroupPersonalPreparationPaged.do?uuid={uuid}&groupId={groupId}&start={start}&end={end}
     */
    @UrlSuffix("group/preparLesson/getGroupPersonalPreparationPaged.do")
    public static String GROUP_PERSONAL_LESSON_PLANS_ALL;

    /**
     * 教案详情
     */
    @UrlSuffix("prepare/lessonPlan/prepareLessonPlanDetail.do")
    public static String LESSON_PLAN_DETAILS;

    /**
     * 修改个人备课反思
     */
    @UrlSuffix("mobile/prepare/lessonPlan/saveRethink.do")
    public static String UPDATE_LESSON_PLAN_RETHINK;

    /**
     * 教案评论列表
     */
//    @UrlSuffix("mobile/prepare/lessonPlan/getComments.do")
    @UrlSuffix("prepare/lessonPlan/commentList.do")
    public static String LESSON_PLAN_COMMENTS;

    /**
     * 添加教案评论
     */
//    @UrlSuffix("mobile/prepare/lessonPlan/insertComment.do")
    @UrlSuffix("mobile/prepare/lessonPlan/addComment.do")
    public static String LESSON_PLAN_ADD_COMMENT;

    @UrlSuffix("mobile/netTeach/addComment.do")
    public static String LESSON_NET_TEACH_ADD_COMMENT;
    /**
     * 删除教案评论
     */
    @UrlSuffix("mobile/prepare/lessonPlan/deleteComment.do")
    public static String LESSON_PLAN_DELETE_COMMENT;
    /**
     * 删除集体备课评论
     */
    @UrlSuffix("mobile/netTeach/deleteComment.do")
    public static String LESSON_NET_TEACH_DELETE_COMMENT;
    /**
     * 获取更多教案评论
     */
    @UrlSuffix("prepare/lessonPlan/replyList.do")
    public static String LESSON_PLAN_MORE_COMMENT;
    /**
     * 获取集体备课更多二级评论
     */
    @UrlSuffix("mobile/netTeach/getChildComment.do")
    public static String LESSON_NET_TEACH_MORE_COMMENT;

    /**
     * 获取学科素材
     */
    @UrlSuffix("mobile/prepareImage/getSubjectMaterials.do")
    public static String SUBJECT_MATERIAL;

    /**
     * 找人接口
     * /mobile/micblog/fans/getSearchPeopleList.do?uuid={uuid}&key={key}&start={start}&end={end}
     */
    @UrlSuffix("mobile/micblog/fans/getSearchPeopleList.do")
    public static String GET_SEARCH_PEOPLE;

    /**
     * 个人空间微博 我的关注
     * /mobile/micblog/fans/getMyFriendList.do?uuid={uuid}&key={key}&start={start}&end={end}
     */
    @UrlSuffix("mobile/micblog/fans/getMyFriendList.do")
    public static String GET_MY_FRIENDLIST;

    @UrlSuffix("mobile/micblog/fans/getMyFriendList2.do")
    public static String GET_MY_FRIENDLIST2;

    /**
     * 个人空间微博点赞
     * /mobile/micblog/dynamic/insertMiblogAgreeUser.do?uuid={uuid}&weiBoId={weiBoId}
     */
    @UrlSuffix("mobile/micblog/dynamic/insertMiblogAgreeUser.do")
    public static String INSERT_MIBLOG_AGREE;
    /**
     * 圈组微博点赞
     * mobile/group/micblog/insertGroupMiblogAgreeUser.do
     */
    @UrlSuffix("mobile/group/micblog/insertGroupMiblogAgreeUser.do")
    public static String INSERT_MIBLOG_AGREE_GROUP;
    /**
     * 个人空间微博取消赞
     * /mobile/micblog/dynamic/cancelMiblogAgree.do?uuid={uuid}&weiBoId={weiBoId}
     */
    @UrlSuffix("mobile/micblog/dynamic/cancelMiblogAgree.do")
    public static String CANCEL_MIBLOG_AGREE;
    /**
     * 圈组微博取消赞
     * mobile/group/micblog/cancelGroupMiblogAgree.do
     */
    @UrlSuffix("mobile/group/micblog/cancelGroupMiblogAgree.do")
    public static String CANCEL_MIBLOG_AGREE_GROUP;
    /**
     * 关注某个人
     * /mobile/micblog/fans/addFriend.do?uuid={uuid}&followId={followId}
     */
    @UrlSuffix("mobile/micblog/fans/addFriend.do")
    public static String ADD_FRIEND;
    /**
     * 取消关注
     * mobile/micblog/fans/deleteFriend.do?uuid={uuid}&unfollowId={unfollowId}
     */
    @UrlSuffix("mobile/micblog/fans/deleteFriend.do")
    public static String DELETE_FRIEND;
    /**
     * 粉丝列表
     * mobile/micblog/fans/getMyFansList.do
     */
    @UrlSuffix("mobile/micblog/fans/getMyFansList.do")
    public static String GET_MY_FANS_LIST;
    /**
     * 移除粉丝
     * mobile/micblog/fans/deleteFans.do
     */
    @UrlSuffix("mobile/micblog/fans/deleteFans.do")
    public static String DELETE_FANS;
    /**
     * 私信列表
     * micblog/message/getMiBlogMessagePeopleList.do
     */
    @UrlSuffix("mobile/micblog/message/getMiBlogMessagePeopleList.do")
    public static String GET_MIBLOG_MESSAGE_LIST;
    /**
     * 屏蔽私信
     * mobile/micblog/fans/addMiBlogBlack.do
     */
    @UrlSuffix("mobile/micblog/fans/addMiBlogBlack.do")
    public static String ADD_MI_BLOG_BLACK;
    /**
     * 取消屏蔽私信
     * mobile/micblog/fans/deleteMiBlogBlackBatch.do
     */
    @UrlSuffix("mobile/micblog/fans/deleteMiBlogBlackBatch.do")
    public static String DELETE_MI_BLOG_BLACK;
    /**
     * 删除私信
     * micblog/message/deleteMiBlogMessageBatch.do
     */
    @UrlSuffix("mobile/micblog/message/deleteMiBlogMessageBatch.do")
    public static String DELETE_MI_MIBLOG_MESSAGE;
    /**
     * 私信详情
     * mobile/micblog/message/getMiBlogMessageList.do
     */
    @UrlSuffix("mobile/micblog/message/getMiBlogMessageList.do")
    public static String GET_MIBLOG_MESSAGELIST;
    /**
     * 发私信
     * /mobile/micblog/message/sendMessage.do
     */
    @UrlSuffix("mobile/micblog/message/sendMessage.do")
    public static String SEND_MESSAGE;
    /**
     * 获取微博评论列表
     * /mobile/micblog/comment/getCommentList.do
     */
    @UrlSuffix("mobile/micblog/comment/getCommentList.do")
    public static String WEIBO_GET_COMMENTLIST;
    /**
     * 圈组获取微博评论列表
     * mobile/group/micblog/comment/getCommentList.do
     */
    @UrlSuffix("mobile/group/micblog/comment/getCommentList.do")
    public static String WEIBO_GET_COMMENTLIST_GROUP;
    /**
     * 删除指定微博
     * /mobile/micblog/dynamic/deleteDynamicByMiblogId.do?uuid={uuid}&weiBoId={weiBoId}
     */
    @UrlSuffix("mobile/micblog/dynamic/deleteDynamicByMiblogId.do")
    public static String DELETE_DYNAMIC_BLOG;
    /**
     * 删除指定圈组微博
     * mobile/group/micblog/deleteDynamicByGroupMiblogId.do
     */
    @UrlSuffix("mobile/group/micblog/deleteDynamicByGroupMiblogId.do")
    public static String DELETE_DYNAMIC_BLOG_GROUP;
    /**
     * 微博获得更多子评论
     * /mobile/micblog/comment/getChildrenCommentList.do?uuid={uuid}&commentId={commentId}&start={start}&end={end}
     */
    @UrlSuffix("mobile/micblog/comment/getChildrenCommentList.do")
    public static String WEIBO_GET_CHILD_COMMENT;
    /**
     * 圈组微博获得更多子评论
     * mobile/group/micblog/comment/getChildrenCommentList.do
     */
    @UrlSuffix("mobile/group/micblog/comment/getChildrenCommentList.do")
    public static String WEIBO_GET_CHILD_COMMENT_GROUP;
    /**
     * 微博删除一条评论
     * /mobile/micblog/comment/deleteComment.do?uuid={uuid}&commentId={commentId}
     */
    @UrlSuffix("mobile/micblog/comment/deleteComment.do")
    public static String WEIBO_DELETE_COMMENT;
    /**
     * 圈组微博删除一条评论
     * mobile/group/micblog/comment/deleteComment.do
     */
    @UrlSuffix("mobile/group/micblog/comment/deleteComment.do")
    public static String WEIBO_DELETE_COMMENT_GROUP;
    /**
     * 评论微博
     * /mobile/micblog/comment/addComment.do?uuid={uuid}&weiBoId={weiBoId}&comment={comment}&commentId={commentId}&replyToUserId=
     */
    @UrlSuffix("mobile/micblog/comment/addComment.do")
    public static String WEIBO_ADD_COMMENT;
    /**
     * 评论圈组微博
     * mobile/group/micblog/comment/addComment.do
     */
    @UrlSuffix("mobile/group/micblog/comment/addComment.do")
    public static String WEIBO_ADD_COMMENT_GROUP;
    /**
     * 发微博
     * mobile/micblog/dynamic/sendDynamic.do
     */
    @UrlSuffix("mobile/micblog/dynamic/sendDynamic.do")
    public static String WEIBO_SEND_DYNAMIC;
    /**
     * mobile/group/micblog/sendDynamic.do
     */
    @UrlSuffix("mobile/group/micblog/sendDynamic.do")
    public static String WEIBO_SEND_DYNAMIC_GROUP;
    /**
     * /mobile/group/micblog/findGroupByUserId.do?uuid={uuid}
     */
    @UrlSuffix("mobile/group/micblog/findGroupByUserId.do")
    public static String WEIBO_FIND_GROUP;
    /**
     * mobile/group/micblog/selectDynamicListByGroupId.do
     */
    @UrlSuffix("mobile/group/micblog/selectDynamicListByGroupId.do")
    public static String WEIBO_LIST_BYGROUPID;
    /**
     * mobile/micblog/message/getUnReadMiBlogCount.do
     */
    @UrlSuffix("mobile/micblog/message/getUnReadMiBlogCount.do")
    public static String GET_MIBLOG_COUNT;
    /**
     * poe add
     * 首页-圈组页面集合
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * schoolId	string	学校用户
     * baseAreaId	string	地区用户
     */
    @UrlSuffix("mobile/group/index.do")
    public static String GET_HOME_GROUP;
    /**
     * poe add
     * 首页-教研组-更多列表
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * grade	String	年级(默认全部)
     * subject	String	学科(默认全部)
     * semester	String	学段(默认全部)
     * start	int	开始位置
     * end	int	结束位置
     * baseAreaId	string	地区
     * schoolId	string	学校
     * type	String	直属校（默认nodirectly，非直属校）
     */
    @UrlSuffix("mobile/group/getTeachList.do")
    public static String GET_HOME_GROUP_RESEARCH_LIST;
    /**
     * poe add
     * 首页-兴趣组-更多列表
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * category	String	分类(默认全部)
     * semester	String	学段(默认全部)
     * start	int	开始位置
     * end	int	结束位置
     * baseAreaId	string	地区
     * schoolId	string	学校
     * type	String	直属校（默认nodirectly，非直属校）
     */
    @UrlSuffix("mobile/group/getInterestList.do")
    public static String GET_HOME_GROUP_INTEREST_LIST;
    /**
     * 获取圈组兴趣组筛选的分类信息
     * uuid String : 32位uuid字符串	用户唯一识别码
     */
    @UrlSuffix("mobile/group/getAllGroupCategory.do")
    public static String GET_GROUP_CATEGORY_LIST;
    /**
     * poe add
     * 首页-圈组博文-博文详情集合
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * groupId	String	圈组id
     */
    @UrlSuffix("mobile/group/blog/getGroupBlogList.do")
    public static String GET_HOME_GROUP_BLOG_POST;
    /**
     * poe add
     * 首页-圈组博文-分类信息
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * groupId	String	圈组id
     */
    @UrlSuffix("mobile/group/blog/getGroupBlogCategory.do")
    public static String GET_HOME_GROUP_BLOG_POST_CATEGORY_INFO;

    //getBlogHomeCategoryList
    /**
     * poe add
     * 首页-博文-更多博文－分类信息
     * 参数名称	类型	说明
     * baseAreaId	区域Id	string
     * schoolId	学校Id	string
     * uuid	用户uuid	string
     */
    @UrlSuffix("mobile/blog/getBlogHomeCategoryList.do")
    public static String GET_HOME_BLOG_POST_CATEGORY_INFO;

    /**
     * poe add
     * 首页-博文-按分获取博文
     * 参数名称	类型	说明
     * groupId	String	圈组id
     * categoryId	String	圈组博文分类id(为空时表示全部，为1时表示未分类)
     * start	int	开始位置
     * end	int	结束位置
     */
    @UrlSuffix("mobile/group/blog/getAllGroupBlog.do")
    public static String GET_HOME_BLOG_POST_CATEGORY_LIST;
    /**
     * poe add
     * 圈组-推荐/取消推荐
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * groupId	String	要推荐的圈组的ID
     * closedFlag	String	圈组关闭标志
     */
    @UrlSuffix("mobile/group/toRecommend.do")
    public static String UPDATE_GROUP_RECOMMEND;
    /**
     * poe add
     * 圈组-关闭
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * groupId	String	要推荐的圈组的ID
     * close
     */
    @UrlSuffix("mobile/group/closeGroup.do")
    public static String UPDATE_GROUP_CLOSE;
    /**
     * poe add
     * 圈组-应用-管理列表
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * areaId	String	区域ID
     * schoolId	String	学校ID
     * semesterId	String	学段（默认全部）
     * groupType	String	组别(默认全部)（TEACH/INTEREST)
     * gradeId	String	年级ID
     * subjectId	String	学科ID
     * categoryId	String	兴趣组的分类ID
     * state	String	状态(默认全部)
     * (CLOSED/WAIT/APPROVED/REJECT)
     * type	String	类型（辖区内AREA_MANAGE/ 校内SCHOOL_MANAGE/我的MY_LIST）
     * directFlag	boolean	直属校（默认false，非直属)
     * start	int	开始位置
     * end	int	结束位置
     */
    @UrlSuffix("mobile/group/getGroupList.do")
    public static String GET_GROUP_MANAGER_LIST;
    /**
     * poe add
     * 我的-圈组空间详情
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * groupId	String	圈组id
     */
    @UrlSuffix("mobile/group/groupDetail.do")
    public static String GET_GROUP_SPACE_DETAIL;
    /**
     * poe add
     * 我的-圈组空间成员
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * groupId	String	圈组的id
     * type	String	(MANAGER/MEMBER)查询管理员/普通成员
     * start	int
     * end	int
     */
    @UrlSuffix("group/member/getGroupMember.do")
    public static String GET_GROUP_SPACE_MEMBER;
    /**
     * poe add
     * 圈组空间-申请加入
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * groupId	String	要加入的圈组的ID
     * joinReason	String	加入原因
     */
    @UrlSuffix("mobile/group/joinGroup.do")
    public static String UPDATE_GROUP_PROMPT_ENTER;
    /**
     * poe add
     * 圈组空间-申请退出
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * groupId	String	要加入的圈组的ID
     */
    @UrlSuffix("mobile/group/quitGroup.do")
    public static String UPDATE_GROUP_PROMPT_OUT;
    /**
     * poe add
     * 圈组空间-审核
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * groupId	String	要审核的圈组的ID
     * approveStatus	String	类型（通过APPROVED／拒绝REJECT）
     * rejectReason--	String	拒绝是输入原因（移动端是否需要？）
     */
    @UrlSuffix("mobile/group/checkGroup.do")
    public static String UPDATE_GROUP_CHECK;
    /**
     * poe add
     * 首页-博文
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * schoolId	string	学校用户
     * baseAreaId	string	地区用户
     */
    @UrlSuffix("mobile/blog/getBlogList.do")
    public static String GET_HOME_BLOG_POST;
    /**
     * poe add
     * 首页-博文-博文详情
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * blogId	string	博文id
     * start 0 (默认显示的评论数)
     * end  3
     */
    @UrlSuffix("mobile/blog/getBlogDetail.do")
    public static String GET_HOME_BLOG_POST_DETAIL;
    /**
     * poe add
     * 博文评论-一级评论列表及更多
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * blogId	string	博文id
     * start 0 (默认显示的评论数)
     * end  3
     */
    @UrlSuffix("mobile/blog/selectCommentPageList.do")
    public static String GET_BLOG_COMMENT_LIST;
    /**
     * poe add
     * 博文评论-二级评论列表及更多
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * commentId	string	评论id
     * start 0 (默认显示的评论数)
     * end  3
     */
    @UrlSuffix("mobile/blog/getScecondCommentPageList.do")
    public static String GET_BLOG_SECOND_COMMENT_LIST;
    /**
     * poe add
     * 博文-发表评论
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * blogId	string	博文id
     * commentContent 评论的内容
     */
    @UrlSuffix("mobile/blog/saveBlogComment.do")
    public static String POST_BLOG_COMMENT;
    /**
     * poe add
     * 博文-回复评论
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * blogId	string	博文id
     * parentCommentId	String	需要回复的评论ID
     * commentContent 评论的内容
     */
    @UrlSuffix("mobile/blog/saveSecondComment.do")
    public static String POST_BLOG_COMMENT_REPLY;

    /**
     * poe add
     * 博文-删除评论
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * blogId	string	博文id
     */
    @UrlSuffix("mobile/blog/deleteComment.do")
    public static String DELETE_BLOG_COMMENT;

    /**
     * poe add
     * 首页-博文-更多-全部博文（分类）
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * schoolId	string	学校用户
     * baseAreaId	string	地区用户
     */
    @UrlSuffix("mobile/blog/getAllBlog.do")
    public static String GET_ALL_BLOG_POST_LIST;

    /**
     * poe add
     * 圈组博文-更多（分类）
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * groupId	String	圈组id
     * categoryId	String	圈组博文分类id(为空时表示全部，为1时表示未分类)
     * start	int	开始位置
     * end	int	结束位置
     */
    @UrlSuffix("mobile/group/blog/getAllGroupBlog.do")
    public static String GET_GROUP_BLOG_BY_CATEGORY;

    /**
     * poe add
     * 个人博文-更多-分类筛选
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * schoolId	string	学校用户
     * baseAreaId	string	地区用户
     */
    @UrlSuffix("mobile/blog/getPersonCategoryList.do")
    public static String GET_BLOG_CATEGORY_LIST;
    /**
     * poe add
     * 我的-班级空间
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     * classId	String	班级ID
     */
    @UrlSuffix("mobile/classSpace/classInfo.do")
    public static String GET_MY_CLASSROOM_INFO;
    /**
     * poe add
     * 班级博文列表
     * 参数名称	类型	说明
     * classId	String	班级ID
     * int start
     * int end
     */
    @UrlSuffix("mobile/blog/getClassBlog.do")
    public static String GET_CLASS_BLOG_LIST;
    /**
     * poe add
     * 更多班级博文列表
     * 参数名称	类型	说明
     * uuid Stirng (非必须)
     * classId	String	班级ID
     * int start
     * int end
     */
    @UrlSuffix("mobile/blog/getAllClassBlog.do")
    public static String GET_CLASS_BLOG_LIST_MORE;
    /**
     * poe add
     * 我的博文（个人博文-登录）列表
     * 参数名称	类型	说明
     * uuid	String	班级ID
     * visitedUserId
     * start
     * end
     * blogCategoryId
     * schoolId
     */
    @UrlSuffix("mobile/blog/getPersonBlog.do")
    public static String GET_PERSONAL_BLOG_LIST;
    /**
     * poe add
     * 我的博文（个人博文-访客）列表
     * 参数名称	类型	说明
     * uuid	String	班级ID
     * visitedUserId	String	访客baseUserId
     */
    @UrlSuffix("mobile/blog/getPublicPersonBlog.do")
    public static String GET_PUBLIC_PERSONAL_BLOG_LIST;

    /**
     * poe add
     * 个人博文分类列表
     * 参数名称	类型	说明
     * uuid	String : 32位uuid字符串	用户唯一识别码
     *
     * @deprecated
     */
    @UrlSuffix("mobile/blog/getPersonCategory.do")
    public static String GET_PERSONAL_BLOG_CATEGORY_LIST;
    /**
     * poe add
     * 个人博文按分类查询列表
     * 参数名称	类型	说明
     * uuid 	String	用户uuid（必须）
     * visitedUserId	String	访客baseUserId
     * blogCategoryId	string	分类id
     * start	int
     * end	int
     * blogCategoryId	string	分类id 1:表示全部，0：表示为分类
     */
    @UrlSuffix("mobile/blog/getBlogByCategory.do")
    public static String GET_PERSONAL_BLOG_BY_CATEGORY;
    /**
     * poe add
     * 应用-圈组-圈组管理详情
     * 参数名称	类型	说明
     * uuid 	String	用户uuid（必须）
     * groupId	string	圈组id
     */
    @UrlSuffix("mobile/group/applicationGroupDetail.do")
    public static String GET_APPLICATION_GROUP_DETAIL;


    /**
     * poe add
     * 获取别人的个人信息
     * 参数名称	类型	说明
     * baseUserId String (非必须)//?baseUserId=666fdfdfddffdfd666
     */
    @UrlSuffix("personInfo.do")
    public static String GET_PERSON_INFO;

    /**
     * 移动端的二维码信息 .
     */
    @UrlSuffix("app/version/getShareVersionList.do")
    public static String GET_MOBILE_SHARE_BAR_CODE ;//= "http://10.1.80.22:8080/mobile/app/version/getShareVersionList.do";

    /**
     * 查看账号状态 .
     */
    @UrlSuffix("personinfo/checkForbidden.do")
    public static String CHECK_USER_FORBIDDEN ;//= "http://10.1.80.22:8080/mobile/app/version/getShareVersionList.do";

    /**
     * poe add
     * 网络授课-我创建的
     */
    @UrlSuffix("mobile/netteach/getMyCreateList.do")
    public static String GET_MY_CREATE_TEACH_LIST;
    /**
     * poe add
     * 网络授课-详细
     * mid	视频会议id	string
     * uuid	用户唯一识别码	string
     */
    @UrlSuffix("mobile/netteach/getTeachDetail.do")
    public static String GET_NET_TEACH_DETAIL;

    /**
     * poe add
     * 网络授课-一级评论列表
     * end	结束	number
     * meetingId	网络授课id	string
     * start	起始	number
     * uuid	uuid	string
     */
    @UrlSuffix("mobile/netTeach/getComment.do")
    public static String GET_NET_TEACH_COMMENT_LIST;

    /**
     * poe add
     * 网络授课-二级评论列表
     * end	结束	number
     * ＊parentCommentId	父级评论id	string	二级评论时需带此参数
     * ＊start	起始	number
     * ＊uuid	uuid	string
     */
    @UrlSuffix("mobile/netTeach/getChildComment.do")
    public static String GET_NET_TEACH_SECOND_COMMENT_LIST;

    /**
     * poe add
     * 网络授课-发表一级评论
     * comment	评论内容	string
     * meetingId	网络授课id	string
     * uuid	uuid	string
     */
    @UrlSuffix("mobile/netTeach/addComment.do")
    public static String SEND_NET_TEACH_COMMENT;

    /**
     * poe add
     * 网络授课-发表二级评论
     * comment	评论内容	string
     * meetingId	网络授课id	string
     * parentCommentId	父类评论id	string
     * replyToId	回复对象id	string
     * uuid	uuid	string
     */
    @UrlSuffix("mobile/netTeach/addComment.do")
    public static String SEND_NET_TEACH_SECOND_COMMENT;

    /**
     * poe add
     * 网络授课-删除评论
     * commentId	评论id	string
     * uuid	uuid	string
     */
    @UrlSuffix("mobile/netTeach/deleteComment.do")
    public static String DELETE_NET_TEACH_COMMENT;

    /**
     * poe add
     * 网络授课-详细-观看视频详情
     * mid	视频会议id	string
     * uuid	用户唯一识别码	string
     */
    @UrlSuffix("mobile/netteach/getVideoDetail.do")
    public static String GET_NET_TEACH_VIDEO_DETAIL;

    /**
     * added by eachann
     * 测试-学校-年级统考列表
     */
    @UrlSuffix("mobile/school/exam/classlevelExam.do")
    public static String CLASS_LEVEL_EXAM;
    /**
     * added by eachann
     * 测试-学校-年级统考列表详情
     */
    @UrlSuffix("mobile/school/exam/classlevelExamDetail.do")
    public static String CLASS_LEVEL_EXAM_DETAIL;
    /**
     * added by eachann
     * 测试-学校-年级统考列表-布置试卷-选择人员列表
     */
    @UrlSuffix("mobile/school/exam/selectClassBySchool.do")
    public static String GET_RECEIVE_PERSONS;
    /**
     * added by eachann
     * 测试-学校-年级统考列表-布置试卷
     */
    @UrlSuffix("mobile/school/exam/finishCreateClassTask.do")
    public static String POST_CREATE_CLASS_TASK;
    /**
     * added by eachann
     * 测试-学校-班级测试列表
     */
    @UrlSuffix("mobile/school/exam/classTestExam.do")
    public static String CLASS_TEST_EXAM;
    /**
     * added by eachann
     * 测试-学校-班级测试列表详情
     */
    @UrlSuffix("mobile/school/exam/classTestExamDetail.do")
    public static String CLASS_TEST_EXAM_DETAIL;
    /**
     * added by eachann
     * 测试-学校-班级测试-总体统计
     */
    @UrlSuffix("mobile/school/exam/globleStatistic.do")
    public static String CLASS_GLOBLE_STATISTIC;
    /**
     * added by eachann
     * 测试-学校-班级测试-题目总计
     */
    @UrlSuffix("mobile/school/exam/questionStatistic.do")
    public static String CLASS_QUESTIOIN_STATISTIC;
    /**
     * added by eachann
     * 测试-学校-班级测试-学生统计
     */
    @UrlSuffix("mobile/school/exam/studentStatistic.do")
    public static String GET_STUDENT_STATISTIC;
    /**
     * added by eachann
     * 测试-学校-班级测试-学生统计-答题批阅详情
     */
    @UrlSuffix("mobile/school/exam/classTestCommentDetail/{examTaskId}/{baseUserId}.do")
    public static String GET_STUDENT_STATISTIC_DETAIL;
    /**
     * added by eachann
     * 测试-教师-按题批阅列表及学生列表
     */
    @UrlSuffix("mobile/teacher/exam/readByQuestionPre.do")
    public static String GET_READ_BY_QUESTIONPRE;
    /**
     * added by eachann
     * 测试-教师-按题批阅列表及学生列表-学生答案
     */
    @UrlSuffix("mobile/teacher/exam/getQuestionAnswer.do")
    public static String GET_QUESTION_ANSWER;
    /**
     * added by eachann
     * 测试-教师-按题批阅列表及学生列表-提交批阅答案
     */
    @UrlSuffix("mobile/teacher/exam/saveReadByQuestionComment.do")
    public static String SAVE_READ_BY_QUESTION_COMMENT;
    /**
     * added by eachann
     * 测试-学生-巩固测试-提交答案
     */
    @UrlSuffix("mobile/student/exam/submitPracticeExam.do")
    public static String SUBMIT_PRACTICE_EXAM;
    /**
     * added by eachann
     * 测试-学校-班级测试-班级列表
     */
    @UrlSuffix("mobile/school/exam/getClassList.do")
    public static String GET_CLASS_LIST;
    /**
     * added by eachann
     * 测试-学校-班级测试-班级列表
     */
    @UrlSuffix("mobile/commons/getClassByClasslevelId.do")
    public static String GET_CLASS_LEVEL_INFO_BY_SCHOOL_ID;
    /**
     * added by eachann
     * 测试-教师-我的试卷列表
     */
    @UrlSuffix("mobile/teacher/exam/list.do")
    public static String MINE_TEST_EXAM;
    /**
     * added by eachann
     * 测试-教师-我的试卷列表详情
     */
    @UrlSuffix("mobile/teacher/exam/examDetail.do")
    public static String MINE_TEST_EXAM_DETAIL;
    /**
     * added by eachann
     * 测试-教师-布置试卷-年级列表
     */
    @UrlSuffix("mobile/teacher/exam/getSelectStudentByTeacher.do")
    public static String GET_SELECT_STU_BY_TEACHER;
    /**
     * added by eachann
     * 测试-教师-布置试卷
     */
    @UrlSuffix("mobile/teacher/exam/finishCreateTask.do")
    public static String POST_FINISH_CREATE_TASK;
    /**
     * added by eachann
     * 测试-教师-真题列表
     */
    @UrlSuffix("mobile/teacher/exam/searchRealExamList.do")
    public static String GET_REAL_EXAM_LIST;
    /**
     * added by eachann
     * 测试-教师-测试任务列表
     */
    @UrlSuffix("mobile/teacher/exam/getClassExamList.do")
    public static String GET_CLASS_EXAM_LIST;
    /**
     * added by eachann
     * 测试-教师-测试任务-班级列表
     */
    @UrlSuffix("mobile/teacher/exam/selecClassByTaskExamId.do")
    public static String GET_EXAM_CLASS_LIST;
    /**
     * added by eachann
     * 测试-教师-测试任务-学生列表
     */
    @UrlSuffix("mobile/teacher/exam/showStuStatuPageList.do")
    public static String GET_EXAM_SHOW_STU_LIST;
    /**
     * added by eachann
     * 测试-教师-测试任务--按学生批阅
     */
    @UrlSuffix("mobile/teacher/exam/readByStudentPre.do")
    public static String GET_EXAM_READ_BY_STUDENT_PRE;
    /**
     * added by eachann
     * 测试-学生-测试任务列表
     */
    @UrlSuffix("mobile/student/exam/listExamTask.do")
    public static String LIST_TASK_EXAM;
    /**
     * added by eachann
     * 测试-学生-测试任务列表-答题
     */
    @UrlSuffix("mobile/student/exam/studentAnswerExam/{examTaskId}.do")
    public static String STUDENT_ANSWER_EXAM;
    /**
     * added by eachann
     * 测试-学生-测试任务列表-提交答案
     */
    @UrlSuffix("mobile/student/exam/studentSubmitExam.do")
    public static String STUDENT_SUBMIT_EXAM;
    /**
     * added by eachann
     * 测试-学生-测试任务列表-按学生批阅
     */
    @UrlSuffix("mobile/teacher/exam/updatestucoment.do")
    public static String POST_UPDATE_STU_COMENT;
    /**
     * added by eachann
     * 测试-学生-测试任务列表-答题-查看批阅
     */
    @UrlSuffix("mobile/student/exam/viewCheckedExam.do")
    public static String STUDENT_VIEW_CHECKED;
    /**
     * added by eachann
     * 测试-学生-巩固测试
     */
    @UrlSuffix("mobile/student/exam/studentPracticeExam.do")
    public static String STUDENT_PRACTICE_EXAM;
    /**
     * added by eachann
     * 测试-学生-巩固结果
     */
    @UrlSuffix("mobile/student/exam/practiceExamResult.do")
    public static String STUDENT_PRACTICE_EXAM_RESULT;
    /**
     * added by eachann
     * 测试-学生-自主测试任务列表
     */
    @UrlSuffix("mobile/student/exam/getStudentSelfTaskList.do")
    public static String LIST_SELF_TASK_EXAM;
    /**
     * added by eachann
     * 测试-家长-测试情况列表
     */
    @UrlSuffix("mobile/parent/exam/getTeacherAssignList.do")
    public static String GET_TEACHER_ASSIGN_LIST;
    /**
     * added by eachann
     * 测试-家长-测试情况-已完成
     */
    @UrlSuffix("mobile/parent/exam/viewCheckedExam.do")
    public static String PARENT_VIEW_CHECKED;
    /**
     * added by eachann
     * 测试-家长-测试情况-待批阅
     */
    @UrlSuffix("mobile/parent/exam/viewSubmitExam.do")
    public static String PARENT_VIEW_SUBMIT;
    /**
     * added by eachann
     * 测试-班级空间-列表
     */
    @UrlSuffix("mobile/class/exam/classExamList.do")
    public static String CLASS_EXAM_LIST;
    /**
     * added by eachann
     * 测试-获取成绩分析-学科分析
     */
    @UrlSuffix("mobile/parent/exam/socreAnalyze.do")
    public static String SOCRE_ANALYZE;
    /**
     * added by eachann
     * 测试-获取成绩分析-学科分析-考试信息
     */
    @UrlSuffix("mobile/parent/exam/examInfo.do")
    public static String GET_PARENT_INFO;
    /**
     * added by eachann
     * 测试-班级空间-总体统计
     */
    @UrlSuffix("mobile/class/exam/globleStatistic.do")
    public static String SPACE_CLASS_GLOBLE_STATISTIC;
    /**
     * added by eachann
     * 测试-班级空间-题目统计
     */
    @UrlSuffix("mobile/class/exam/questionStatistic.do")
    public static String SPACE_CLASS_QUESTION_STATISTIC;
    /**
     * ldh add
     * 老师作业列表接口/班级空间
     */
    @UrlSuffix("mobile/homework/getHomework.do")
    public static String GET_HOMEWORK_LIST;

    /**
     * ldh add
     * 学生我的作业列表
     */
    @UrlSuffix("mobile/homework/getHomeworkForStudentByPageList.do")
    public static String GET_STU_HOMEWORK_LIST;

    /**
     * ldh add
     * 学生我的批阅列表
     */
    @UrlSuffix("mobile/homework/getStudentReadByPageList.do")
    public static String GET_STU_READ_LIST;

    /**
     * ldh add
     * 家长 作业中心
     */
    @UrlSuffix("mobile/homework/getHomeworkForParent.do")
    public static String GET_PAR_HOMEWORK_LIST;//

    /**
     * ldh add
     * 做该套作业的班级列表
     */
    @UrlSuffix("mobile/homework/getHomeworkClass.do")
    public static String GET_HOMEWORK_CLASSES;//mobile/

    /**
     * ldh add
     * 作业统计完成情况列表
     */
    @UrlSuffix("mobile/homework/getHomeworkFinishedInfo.do")
    public static String GET_FINISH_INFO_LIST;//mobile/

    /**
     * ldh add
     * 学生我的批阅 批阅/查看批阅列表
     */
    @UrlSuffix("mobile/homework/getReadFinish.do")
    public static String GET_STU_READ_INF0_LIST;

    /**
     * ldh add
     * 做该套作业的该班级的学生
     */
    @UrlSuffix("mobile/homework/getHomeworkStudent.do")
    public static String GET_HOMEWORK_STUDENT_LIST;

    /**
     * LDH ADD
     * 按题批阅 获取学生列表及其对应的答案
     */
    @UrlSuffix("mobile/homework/getHomeworkQuestionAnswer.do")
    public static String GET_HOMEWORK_STU_ANSWER_BY_ITEM;
    /**
     * ldh add
     * 该班该套作业的统计情况
     */
    @UrlSuffix("mobile/homework/getHomeworkStatistics.do")
    public static String GET_HOMEWORK_STATISTICS_INFO;

    /**
     * ldh add
     * 试题内容
     */
    @UrlSuffix("mobile/homework/getHomeWorkQuestion.do")
    public static String GET_HOMEWORK_QUESTION;

    @UrlSuffix("mobile/homework/getHomeWorkQuestionByQuesReadOver.do")
    public static String GET_HOMEWORK_QUESTION_BY_OVER;

    /**
     * ldh add
     * 学生答案
     */
    @UrlSuffix("mobile/homework/getStudentAnswer.do")
    public static String GET_STUDENT_ANSWER;

    /**
     * ldh add
     * 提交学生答案
     */
    @UrlSuffix("mobile/homework/submitAnswer.do")
    public static String SUBMIT_STUDENT_ANSWER;//"http://10.5.225.18:8080/MobileInterface/"

    /**
     * ldh add
     * 老师/学生提交批阅
     */
    @UrlSuffix("mobile/homework/submitReadInfos.do")
    public static String SUBMIT_READ_INFO;//

    /**
     * ldh add
     * 批量提交批阅
     */
    @UrlSuffix("mobile/homework/readOverBatch.do")
    public static String SUBMIT_READ_INFOS;//BASE

    /**
     * ldh add
     * 布置作业
     */
    @UrlSuffix("mobile/homework/arrangeWork.do")
    public static String ARRANGE_WORK;

    /**
     * ldh add
     * 获取评语
     */
    @UrlSuffix("mobile/homework/getCommentTemp.do")
    public static String GET_COMMENT_TEMP;

    /**
     * ldh add
     * 提交总体评价
     */
    @UrlSuffix("mobile/homework/submitOverallComment.do")
    public static String SUBMIT_OVERALL_COMMENT;

    /**
     * 获取教学反思列表
     */
    @UrlSuffix("mobile/rethink/getRethinkList.do")
    public static String RETHINK_LIST;

    /**
     * 获取教学反思详情
     */
    @UrlSuffix("mobile/rethink/getRethinkDetail.do")
    public static String RETHINK_DETAILS;

    /**
     * 获取教学反思评论列表
     */
    @UrlSuffix("mobile/rethink/getRethinkComments.do")
    public static String RETHINK_COMMENTS;

    /**
     * 获取教学反思更多评论回复
     */
    @UrlSuffix("mobile/rethink/getReplyComment.do")
    public static String RETHINK_MORE_REPLIES;

    /**
     * 推荐教学反思
     */
    @UrlSuffix("mobile/rethink/recommend.do")
    public static String RECOMMEND_RETHINK;

    /**
     * 添加评论教学反思评论
     */
    @UrlSuffix("mobile/rethink/addComments.do")
    public static String ADD_RETHINK_COMMENT;

    @UrlSuffix("mobile/rethink/delComment.do")
    public static String DELETE_RETHINK_COMMENT;

    /**
     *
     */
    @UrlSuffix("mobile/onlineTutor/tutorialList.do")
    public static String GET_TUTORSHIP_LIST;

    @UrlSuffix("mobile/onlineTutor/tutorialDetail.do")
    public static String GET_TUTORSHIP_DETAILS;

    @UrlSuffix("mobile/meetexam/tutorialTestList.do")
    public static String GET_TUTORIAL_TEST_LIST;

    @UrlSuffix("mobile/meetexam/getTutorialTest.do")
    public static String GET_TUTORIAL_TEST;
    /**
     * 区县总表-计划开课
     */
    @UrlSuffix("mobile/district/getPlanData.do")
    public static String CONTY_GET_PLANDATA;
    /**
     * 区县总表-自主开课
     */
    @UrlSuffix("mobile/district/getSelfDefineData.do")
    public static String CONTY_GET_LIBERTYDATA;
    /**
     * 区县总表-计划开课-详情
     */
    @UrlSuffix("mobile/district/getPlanDetail.do")
    public static String CONTY_GET_PLAN_DETAIL;
    /**
     * 区县总表-自主开课-详情
     */
    @UrlSuffix("mobile/district/getSelfDefineDetail.do")
    public static String CONTY_GET_SELF_DETAIL;

    /**
     * 区县总表-主讲教师详情
     */
    @UrlSuffix("mobile/district/getMainTeacherSchedule.do")
    public static String CONTY_GET_MAINTEACHER_SCHEDULE;
    @UrlSuffix("mobile/district/getReceiveClassroomSchedule.do")
    public static String CONTY_GET_RECEIVECLASSROOM;
    @UrlSuffix("mobile/district/getMainClassroomSchedule.do")
    public static String CONTY_GET_MAINCLASSROOM;
    @UrlSuffix("mobile/district/getFilter.do")
    public static String CONTY_GET_FILTER;
    @UrlSuffix("mobile/district/getScheduleDetail.do")
    public static String CONTY_GET_SCHEDULE_DETAIL;
    /**
     * 县区总表-主讲教室开课详情
     */
    @UrlSuffix("mobile/district/getClsClassroomWeekScheduleDetail.do")
    public static String GET_CLASS_WEEKSCHEDULE_DETAIL;
    /**
     * 县区总表-主讲教室开课统计
     * mobile/district/getMainClassesCount.do
     */
    @UrlSuffix("mobile/district/getMainClassesCount.do")
    public static String GET_MAIN_CLASS_COUNT;
    /**
     * 县区总表-主讲教师开课详情
     * mobile/district/getTeacherScheduleDetailList.do
     */
    @UrlSuffix("mobile/district/getTeacherScheduleDetailList.do")
    public static String GET_TEACHER_WEEKSCHEDULE_DETAIL;
    /**
     * 县区总表-主讲教师开课统计
     * mobile/district/getMainTeacherCount.do
     */
    @UrlSuffix("mobile/district/getMainTeacherCount.do")
    public static String GET_MAIN_TEACHER_COUNT;
    /**
     * mobile/district/getReceiveScheduleDetailList.do
     */
    @UrlSuffix("mobile/district/getReceiveScheduleDetailList.do")
    public static String GET_RECEIVE_WEEKSCHEDULE_DETAIL;

    /**
     * 县区总表-接收教室听课统计
     * mobile/district/getReceiveCount.do
     */
    @UrlSuffix("mobile/district/getReceiveCount.do")
    public static String GET_RECEIVE_COUNT_DETAIL;

    /**
     * 校园电视台-节目单
     * liveDate	查询日期	string	2017-02-03
     * schoolId	学校id	string	必填
     * uuid	uuid	string
     */
    @UrlSuffix("mobile/tvprogram/liveList.do")
    public static String GET_SCHOOL_TV_PROGRAM_LIST;

    /**
     * 校园电视台-节目单
     * schoolId	学校id	string
     * tvProgramDetailId	节目id	string
     * uuid		string
     */
    @UrlSuffix("mobile/tvprogram/detail.do")
    public static String GET_SCHOOL_TV_PROGRAM_DETAIL;

    /**
     * 校园电视台-往期视频
     * end		number
     * orderBy	按什么排序	string	programName 节目名称；viewCnt 点击量；time 按时间
     * orderType	排序规则	string	asc 正序； desc 倒序
     * schoolId	学校id	number
     * start		number
     * uuid		string
     */
    @UrlSuffix("mobile/tvProgram/getPastProgramList.do")
    public static String GET_SCHOOL_TV_HISTORY_LIST;

    @UrlSuffix("mobile/malfunction/getMalFunctionDetailPageList.do")
    public static String GET_REPAIRS_SCHOOLS;

    @UrlSuffix("mobile/malfunction/getSchoolMalDetailPageList.do")
    public static String GET_REPAIR_RECORDS;

    @UrlSuffix("mobile/malfunction/getSchoolClassRoomList.do")
    public static String GET_CLASSROOMS;

    @UrlSuffix("mobile/malguidefunction/getSearchMalPageList.do")
    public static String SEARCH_MALFUNC;

    @UrlSuffix("mobile/malfunction/getMalDetail.do")
    public static String GET_REPAIR_DETAILS;

    @UrlSuffix("mobile/malfunction/getAppendList.do")
    public static String GET_REPAIR_TRACKING;

    /**
     * 故障类型分类
     */
    @UrlSuffix("mobile/malfunction/getSubCatalogListById.do")
    public static String GET_MALFUNC_CATEGORIES;

    /**
     * 获取常见问题分类
     */
    @UrlSuffix("mobile/malguidefunction/getCatalogList.do")
    public static String GET_MAL_GUIDE_CATALOGS;

    /**
     * 根据类型获取常见问题
     */
    @UrlSuffix("mobile/malguidefunction/getMalByCatalogPageList.do")
    public static String GET_MALFUNCTIONS_BY_CATALOG;

    /**
     * 获取最热常见问题
     */
    @UrlSuffix("mobile/malguidefunction/getHotMalList.do")
    public static String GET_HOT_MALFUNCTIONS;

    /**
     * 获取最新常见问题
     */
    @UrlSuffix("mobile/malguidefunction/getNewestMalList.do")
    public static String GET_LATEST_MALFUNCTIONS;

    /**
     * 获取常见问题详情
     */
    @UrlSuffix("mobile/malguidefunction/getMalDetailById.do")
    public static String GET_MALFUNCTION_DETAILS;

    /**
     * 报修
     */
    @UrlSuffix("mobile/malfunction/addMalDetail.do")
    public static String REPORT_MAL;

    /**
     * 追问
     */
    @UrlSuffix("mobile/malfunction/addAppend.do")
    public static String MAKE_DETAILED_INQUIRY;



    static {
        UrlBuilder.updateUrls();
    }

    public static void updateUrls(String newBase) {
        BASE = newBase;
        long start = System.currentTimeMillis();
        UrlBuilder.updateUrls();
        Cog.d(TAG, "updateUrls spend=", System.currentTimeMillis() - start);
    }
}
