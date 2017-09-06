package com.codyy.erpsportal.commons.models;

import android.text.TextUtils;

import com.codyy.erpsportal.commons.models.entities.UserInfo;

import org.json.JSONObject;

/**
 * 标题保持器
 * Created by gujiajia on 2015/7/24.
 */
public class Titles {

    public static String sHomepageBlog = "博文";
    public static String sHomepageBlogClassify = "分类目录";
    public static String sHomepageBlogHot = "最热博文";
    public static String sHomepageBlogRecommend = "推荐博文";
    public static String sHomepageGroup = "圈组";
    public static String sHomepageGroupInterest = "兴趣组";
    public static String sHomepageGroupRecommend = "推荐圈组";
    public static String sHomepageGroupTeaching = "教研组";
    public static String sHomepageIndex = "首页";
    public static String sHomepageInfo = "资讯";
    public static String sHomepageInteract = "互动课堂开设情况";
    public static String sHomepageName = "互动学习平台";
    public static String sHomepageNetclass = "直录播课堂";
    public static String sHomepageNetteach = "网络教研";
    public static String sHomepageSpeclass = "专递课堂";
    public static String sHomepageSysTitle = "互动学习平台";

    //门户综合首页
    public static String sPagetitleIndexCompositeNew = "新闻";
    public static String sPagetitleIndexCompositeNotice = "通知";
    public static String sPagetitleIndexCompositeAnnouncement = "公告";
    public static String sPagetitleIndexCompositeResource = "优课资源";
    public static String sPagetitleIndexCompositeHotresource = "热门资源";
    public static String sPagetitleIndexCompositeTearec = "名师推荐";
    public static String sPagetitleIndexCompositeOlclass = "直播课堂";
    public static String sPagetitleIndexCompositeSpeclass = "专递课堂";
    public static String sPagetitleIndexCompositeNetclass = "名校网络课堂";
    public static String sPagetitleIndexCompositeNetteach = "网络教研";
    public static String sPagetitleIndexCompositeInteract = "互动听课";
    public static String sPagetitleIndexCompositePreparelesson = "个人备课";
    public static String sPagetitleIndexCompositePrepare = "集体备课";
    public static String sPagetitleIndexCompositeDisucss = "评课议课";
    public static String sPagetitleIndexCompositeRethink = "教学反思";
    public static String sPagetitleIndexCompositeBlog = "博文";
    public static String sPagetitleIndexCompositeHotblog = "热门博文";

    //门户监管首页
    public static String sPagetitleIndexMonitorOlclass = "直播课堂";
    public static String sPagetitleIndexMonitorSpeclass = "专递课堂";
    public static String sPagetitleIndexMonitorNetclass = "名校网络课堂";
    public static String sPagetitleIndexMonitorNew = "新闻";
    public static String sPagetitleIndexMonitorNotice = "通知";
    public static String sPagetitleIndexMonitorAnnouncement = "公告";
    public static String sPagetitleIndexMonitorReplay = "课程回放";
    public static String sPagetitleIndexMonitorResource = "优课资源";
    public static String sPagetitleIndexMonitorHotresource = "热门资源";

    //门户纯监管首页
    public static String sPagetitleIndexPuremonitorSpeclass = "专递课堂直播";
    public static String sPagetitleIndexPuremonitorNetclass = "名校网络课堂";

    //门户资源首页
    public static String sPagetitleIndexResourceOlclass = "直播课堂";
    public static String sPagetitleIndexResourceSpeclass = "专递课堂";
    public static String sPagetitleIndexResourceNetclass = "名校网络课堂";
    public static String sPagetitleIndexResourceNew = "新闻";
    public static String sPagetitleIndexResourceNoticeannoun = "通知/公告";
    public static String sPagetitleIndexResourceTearec = "名师推荐";
    public static String sPagetitleIndexResourceHotresource = "热门资源";
    public static String sPagetitleIndexResourceGoodresource = "精选资源";

    //门户资源无直播首页
    public static String sPagetitleIndexNoLiveNew = "新闻";
    public static String sPagetitleIndexNoLiveNoticeannoun = "通知/公告";
    public static String sPagetitleIndexNoLiveTearec = "名师推荐";
    public static String sPagetitleIndexNoLiveHotresource = "热门资源";
    public static String sPagetitleIndexNoLiveGoodresource = "精选资源";
    public static String sPagetitleIndexNoLiveHotgroup = "热门圈组";

    //门户天津首页
    public static String sPagetitleIndexTianjinTianjin = "天津";
    public static String sPagetitleIndexTianjinClass = "课堂";
    public static String sPagetitleIndexTianjinSchedule = "市级课表";
    public static String sPagetitleIndexTianjinResource = "精品资源";

    //门户苏州园区首页
    public static String sPagetitleIndexSipRecentClass = "近期课程";

    //门户资讯页
    public static String sPagetitleIndexInfoNewcenter = "新闻中心";
    public static String sPagetitleIndexInfoNoticecenter = "通知中心";
    public static String sPagetitleIndexInfoAnnouncenter = "公告中心";
    public static String sPagetitleIndexInfoNew = "新闻";
    public static String sPagetitleIndexInfoNotice = "通知";
    public static String sPagetitleIndexInfoAnnouncement = "公告";

    //门户集团校首页
    public static String sPagetitleIndexClubSchoolTeachingActivity = "教研活动";
    public static String sPagetitleIndexClubSchoolInfoNew = "新闻/通知/公告";
    public static String sPagetitleIndexGroupSchoolNew = "新闻";
    public static String sPagetitleIndexGroupSchoolNotice = "通知";
    public static String sPagetitleIndexGroupSchoolAnnouncement = "公告";
    public static String sPagetitleIndexClubSchoolTeacherSuggest = "名师推荐";
    public static String sPagetitleIndexClubSchoolClassDesign = "课程建设";
    public static String sPagetitleIndexClubSchoolLiveClass = "直播课堂";
    public static String sPagetitleIndexClubSchoolSchoolResource = "校本资源";
    public static String sPagetitleIndexClubSchoolResource = "优课资源";
    public static String sPagetitleIndexClubSchool = "集团学校";

    //专递课堂
    public static String sPagetitleSpeclassLive = "正在直播";
    public static String sPagetitleSpeclassReplay = "课程回放";

    //名校网络课堂
    public static String sPagetitleNetclassLive = "正在直播";
    public static String sPagetitleNetclassRecomcourse = "推荐课程";

    //网络教研相关
    public static String sPagetitleNetteachDisucss = "评课议课";
    public static String sPagetitleNetteachDisucssintro = "评课议课介绍";
    public static String sPagetitleNetteachDisucssstandintro = "评估标准介绍";
    public static String sPagetitleNetteachRecomddisucss = "推荐评课";
    public static String sPagetitleNetteachHotdisucss = "热门评课";
    public static String sPagetitleNetteachTearecomd = "名师推荐";
    public static String sPagetitleNetteachInteract = "互动听课";
    public static String sPagetitleNetteachListen = "热门听课";

    public static String sPagetitleNetteachAllprepare = "集体备课";
    public static String sPagetitleNetteachHotprepare = "热门备课";

    public static String sPagetitleNetteachPrepare = "个人备课";
    public static String sPagetitleNetteachHotteaching = "热门教案";

    public static String sPagetitleNetteachRethink = "教学反思";
    public static String sPagetitleNetteachGoodrethink = "优质教学反思";
    public static String sPagetitleNetteachHotrethink = "热门教学反思";
    public static String sPagetitleNetteachRelatedrethink = "相关教学反思";

    //门户博文
    public static String sPagetitleBlogClassify = "分类目录";
    public static String sPagetitleBlogRecommend = "推荐博文";
    public static String sPagetitleBlogHot = "热门博文";

    //门户圈组
    public static String sPagetitleGroupRecommend = "推荐圈组";
    public static String sPagetitleGroupActive = "活跃圈组";

    //门户问答
    public static String sPagetitleAnsandansAllask = "大家都在问";
    public static String sPagetitleAnsandansUnsolve = "未解决问题";
    public static String sPagetitleAnsandansSolved = "已解决问题";
    public static String sPagetitleAnsandansKeysort = "关键字排行";
    public static String sPagetitleAnsandansHot = "热门问题";
    public static String sPagetitleAnsandansExplainsort = "解答排行榜";
    public static String sPagetitleAnsandansAcceptsort = "采纳排行榜";

    //门户赛课
    public static String sPagetitleMatchDetails = "赛课详情";
    public static String sPagetitleMatchCourse = "赛课课程";
    public static String sPagetitleMatchAllmatch = "他们都在赛";
    public static String sPagetitleMatchNearactive = "近期活动";
    public static String sPagetitleMatchActivecenter = "活动中心";
    public static String sPagetitleMatchMatchsort = "赛课排行";
    public static String sPagetitleMatchActivedate = "活动日期";

    //门户全局
    public static String sPagetitleGlobalNew = "新闻";
    public static String sPagetitleGlobalNotice = "通知";
    public static String sPagetitleGlobalAnnouncement = "公告";
    public static String sPagetitleGlobalHotresource = "热门资源";
    public static String sPagetitleGlobalRecommendgroup = "推荐圈组";
    public static String sPagetitleGlobalActivegroup = "活跃圈组";
    public static String sPagetitleGlobalSpaceMicroblog = "微博";

    /*
     * 应用页的标题们
     */

    //专递课堂
    public static String sWorkspaceSpeclass = "专递课堂";

    public static String sWorkspaceSpeclassAllTable = "县区总表";
    public static String sWorkspaceSpeclassSpacktable = "主讲教室课表";
    public static String sWorkspaceSpeclassReceivetable = "接收教室课表";
    public static String sWorkspaceSpeclassSpacktea = "主讲教师列课表";
    public static String sWorkspaceSpeclassSchedule = "课程表";
    public static String sWorkspaceSpeclassLive = "正在直播";
    public static String sWorkspaceSpeclassReplay = "往期录播";
    public static String sWorkspaceSpeclassTask = "课堂作业";
    public static String sWorkspaceSpeclassRound = "实时轮巡";
    public static String sWorkspaceSpeclassBroadcast = "远程导播";

    //名校网络课堂
    public static String sWorkspaceNetClass = "名校网络课堂";

    public static String sWorkspaceNetClassClass = "约课列表";
    public static String sWorkspaceNetClassLive = "正在直播";
    public static String sWorkspaceNetClassRound = "实时轮巡";
    public static String sWorkspaceNetClassReplay = "往期录播";
    public static String sWorkspaceNetClassTask = "课堂作业";
    public static String sWorkspaceNetClassBroadcast = "远程导播";

    //集体备课
    public static String sWorkspacePrepare = "集体备课";

    public static String sWorkspacePrepareLaunch = "我发起的";
    public static String sWorkspacePrepareAttend = "我参与的";
    public static String sWorkspacePreparePrepare = "区内备课管理";

    //互动听课
    public static String sWorkspaceListen = "互动听课";
    public static String sWorkspaceListenLaunch = "我发起的";
    public static String sWorkspaceListenAttend = "我参与的";
    public static String sWorkspaceListenManage = "区内听课管理";

    //评课议课
    public static String sWorkspaceDisucss = "评课议课";
    public static String sWorkspaceDisucssLaunch = "我发起的";
    public static String sWorkspaceDisucssInArea = "区内评课";
    public static String sWorkspaceDisucssEvaluStand = "我的评估标准";
    public static String sWorkspaceDisucssPublishStand = "公开的评估标准";
    public static String sWorkspaceDisucssInvited = "我受邀的";
    public static String sWorkspaceDisucssTeaLaunch = "教师发起的";
    public static String sWorkspaceDisucssInSchool = "本校主讲的";
    public static String sWorkspaceDisucssAttend = "我参与的";
    public static String sWorkspaceDisucssMySpack = "我主讲的";

    //视频会议
    public static String sWorkspaceVidmeet = "视频会议";
    public static String sWorkspaceVidmeetLaunch = "我发起的";
    public static String sWorkspaceVidmeetAttend = "我参与的";
    public static String sWorkspaceVidmeetManage = "本级会议管理";

    //新闻
    public static String sWorkspaceInfo = "新闻";
    public static String sWorkspaceInfoNew = "新闻";
    public static String sWorkspaceInfoDraft = "草稿箱";

    //通知公告
    public static String sWorkspaceNoticeAnnouncement = "通知公告";
    public static String sWorkspaceNoticeAnnouncementAnnouncement = "公告";
    public static String sWorkspaceNoticeAnnouncementNotice = "通告";
    public static String sWorkspaceNoticeAnnouncementDraft = "草稿箱";

    //优课资源
    public static String sWorkspaceResource = "优课资源";
    public static String sWorkspaceResourceUpload = "我上传的";
    public static String sWorkspaceResourceRecommend = "下级推荐";
    public static String sWorkspaceResourceCollect = "我的收藏";
    public static String sWorkspaceResourceSpecial = "专辑";

    //统计
    public static String sWorkspaceCount = "统计";
    public static String sWorkspaceCountClass = "课堂统计";
    public static String sWorkspaceCountTutiongeneral = "开课概况";
    public static String sWorkspaceCountTutiontate = "开课比分析";
    public static String sWorkspaceCountSubject = "学科统计";
    public static String sWorkspaceCountResource = "资源统计";
    public static String sWorkspaceCountBysubject = "按学科统计";
    public static String sWorkspaceCountByclasslevel = "按年级统计";
    public static String sWorkspaceCountBytea = "按教师统计";
    public static String sWorkspaceCountBysub = "按下级统计";
    public static String sWorkspaceCountDisucss = "评课统计";
    public static String sWorkspaceCountSubject1 = "学科统计";
    public static String sWorkspaceCountClasslevel = "年级统计";
    public static String sWorkspaceCountSublaunch = "下级发起统计";
    public static String sWorkspaceCountTeacher = "主讲教师统计";
    public static String sWorkspaceCountBy = "按教师统计";

    //基础设置
    public static String sWorkspaceBase = "基础设置";
    public static String sWorkspaceBaseAccountmanage = "帐号管理";
    public static String sWorkspaceBaseRolemanage = "角色管理";
    public static String sWorkspaceBaseUsermanage = "用户管理";
    public static String sWorkspaceBaseSubmanage = "下级管理";
    public static String sWorkspaceBaseSchoolmanage = "学校管理";
    public static String sWorkspaceBaseTeamanage = "教师管理";
    public static String sWorkspaceBaseStumanage = "学生管理";
    public static String sWorkspaceBaseParmanage = "家长管理";
    public static String sWorkspaceBaseClassmanage = "班级管理";
    public static String sWorkspaceBaseCurclass = "当前班级";
    public static String sWorkspaceBaseHisclass = "历史班级";
    public static String sWorkspaceBaseCreateclass = "创建班级";
    public static String sWorkspaceBaseClsroommanage = "教室管理";
    public static String sWorkspaceBaseSchedulemanage = "课表管理";

    //个人备课
    public static String sWorkspacePrepareLesson = "个人备课";
    public static String sWorkspacePrepareLessonPrepareLesson = "教案";
    public static String sWorkspacePrepareLessonPrepareImage = "学科素材";

    //在线编辑
    public static String sWorkspaceOnlineEdit = "在线编辑";
    public static String sWorkspaceOnlineEditSection = "视频切片";
    public static String sWorkspaceOnlineEditLabel = "看点标注";
    public static String sWorkspaceOnlineEditTest = "测验";

    //教学反思
    public static String sWorkspaceRethink = "教学反思";

    //圈组
    public static String sWorkspaceGroup = "圈组";
    public static String sWorkspaceGroupAreaGroup = "辖区内圈组";
    public static String sWorkspaceGroupMyGroup = "我的圈组";
    public static String sWorkspaceGroupSchoolGroup = "校内圈组";

    //课程表
    public static String sWorkspaceSchedule = "课程表";
    public static String sWorkspaceSchedulePersonal = "个人课表";
    public static String sWorkspaceScheduleClass = "班级课表";

    //班级成员
    public static String sWorkspaceMember = "班级成员";

    //多媒体教室管理
    public static String sWorkspaceMultiMedia = "多媒体教室管理";

    //题库
    public static String sWorkspaceQuestionLib = "题库";
    public static String sWorkspaceQuestionLibShare = "共享习题";
    public static String sWorkspaceQuestionLibMyLib = "我的习题";
    public static String sWorkspaceQuestionLibCollect = "收藏习题";

    //测试
    public static String sWorkspaceTest = "测试";
    public static String sWorkspaceTestGrade = "年级统考";
    public static String sWorkspaceTestClass = "班级测试";
    public static String sWorkspaceTestSelf = "我的试卷";
    public static String sWorkspaceTestSubject = "真题试卷";
    public static String sWorkspaceTestTask = "测试任务";
    public static String sWorkspaceTestAuto = "自主测试";

    //作业
    public static String sWorkspaceHomework = "作业";
    public static String sWorkspaceHomeworkList = "我的作业";
    public static String sWorkspaceHomeworkReadOver = "我的批阅";

    //博文
    public static String sWorkspaceBlog = "博文";

    //辅导
    public static String sWorkspaceTutor = "辅导";
    public static String sWorkspaceTutorTution = "本校开课";
    public static String sWorkspaceTutorListen = "跨校听课";
    public static String sWorkspaceTutorSysconfig = "系统设置";
    public static String sWorkspaceTutorManage = "调查管理";
    public static String sWorkspaceTutorWaterMark = "视频水印";
    public static String sWorkspaceTutorClsList = "课堂列表";
    public static String sWorkspaceTutorTutorRoom = "辅导室";
    public static String sWorkspaceTutorTest = "随堂测验";

    //报修信息跟踪
    public static String sWorkspaceRepairs = "报修信息跟踪";

    //问答
    public static String sWorkspaceAskAndAns = "问答";
    public static String sWorkspaceAskAndAnsStuAsk = "学生提问";
    public static String sWorkspaceAskAndAnsMyAsk = "我的提问";
    public static String sWorkspaceAskAndAnsMyAnswer = "我的回答";
    public static String sWorkspaceAskAndAnsWaitMyAnswer = "等我来答";
    public static String sWorkspaceAskAndAnsMateAsk = "同学提问";

    //赛课
    public static String sWorkspaceMatch = "赛课";
    public static String sWorkspaceMatchActiveCenter = "活动中心";
    public static String sWorkspaceMatchMyDisCourse = "我评的课";
    public static String sWorkspaceMatchMyActive = "我的活动";

    //随堂测验
    public static String sWorkspaceOnlineClassExam = "随堂测验";

    //调查问卷
    public static String sWorkspaceQuestionnaire = "调查问卷";
    public static String sWorkspaceQuestionnaireSameLevel = "本级调查";
    public static String sWorkspaceQuestionnaireTask = "调查任务";
    public static String sWorkspaceQuestionnaireMy = "我的调查";
    public static String sWorkspaceQuestionnaireStuAppraisal = "学生测评";
    public static String sWorkspaceQuestionnaireCls = "班级调查";

    //网络授课
    public static String sWorkspaceNetTeach = "网络授课";
    public static String sWorkspaceNetTeachMyCreate = "我创建的";
    public static String sWorkspaceNetTeachManage = "区内课程管理";
    public static String sWorkspaceNetTeachMyCourse = "我的课程";
    public static String sWorkspaceNetTeachSubCourse = "课程订阅";

    //校园电视台
    public static String sWorkspaceTvProgram = "校园电视台";
    public static String sWorkspaceTvProgramProgramList = "节目表";
    public static String sWorkspaceTvProgramLive = "直播";
    public static String sWorkspaceTvProgramReplay = "往期视频";

    /**
     * front.field.masterschool	主讲学校
     */
    public static String sMasterSchool = "主讲学校";

    /**
     * front.field.receiveschool 接收学校
     */
    public static String sReceiveSchool = "接收学校";

    /**
     * front.field.masterroom 主讲教室
     */
    public static String sMasterRoom = "主讲教室";

    /**
     * front.field.receiveroom 接收教室
     */
    public static String sReceiveRoom = "接收教室";

    /**
     * front.field.masterteacher 主讲教师
     */
    public static String sMasterTeacher = "主讲教师";

    /**
     * front.field.coachteacher	辅助教师
     */
    public static String sCoachTeacher = "辅助教师";

    /**
     * front.field.master 主讲
     */
    public static String sMaster = "主讲";

    /**
     * front.field.receive 接收
     */
    public static String sReceiver = "接收";

    /**
     * front.field.invited 受邀
     */
    public static String sInvited = "受邀";

    public static void parseTitles(JSONObject jsonObject) {
        UserInfo userInfo = UserInfoKeeper.obtainUserInfo();
        if (jsonObject == null) return;
        //解析首页标题
        sHomepageBlog = jsonObject.optString("front.homepage.blog", sHomepageBlog);//博文
        sHomepageBlogClassify = jsonObject.optString("front.homepage.blog.classify", sHomepageBlogClassify);//分类目录
        sHomepageBlogHot = jsonObject.optString("front.homepage.blog.hot", sHomepageBlogHot);//最热博文
        sHomepageBlogRecommend = jsonObject.optString("front.homepage.blog.recommend", sHomepageBlogRecommend);//推荐博文
        sHomepageGroup = jsonObject.optString("front.homepage.group", sHomepageGroup);//圈组
        sHomepageGroupInterest = jsonObject.optString("front.homepage.group.interest", sHomepageGroupInterest);//兴趣组
        sHomepageGroupRecommend = jsonObject.optString("front.homepage.group.recommend", sHomepageGroupRecommend);//推荐圈组
        sHomepageGroupTeaching = jsonObject.optString("front.homepage.group.teaching", sHomepageGroupTeaching);//教研组
        sHomepageIndex = jsonObject.optString("front.homepage.index", sHomepageIndex);//首页
        sHomepageInfo = jsonObject.optString("front.homepage.info", sHomepageInfo);//资讯
        sHomepageInteract = jsonObject.optString("front.homepage.interact", sHomepageInteract);//互动课堂开设情况
        sHomepageName = jsonObject.optString("front.homepage.name", sHomepageName);//互动学习平台
        sHomepageNetclass = jsonObject.optString("front.homepage.netclass", sHomepageNetclass);//直录播课堂
        sHomepageNetteach = jsonObject.optString("front.homepage.netteach", sHomepageNetteach);//网络教研
        sHomepageSpeclass = jsonObject.optString("front.homepage.speclass", sHomepageSpeclass);//专递课堂
        sHomepageSysTitle = jsonObject.optString("front.homepage.sys.title", sHomepageSysTitle);//互动学习平台

        parseMainTitles(jsonObject);
        parsePortalTitles(jsonObject);
        parseAppNames(jsonObject);
        if (userInfo.isArea()) {
            parseAreaTitles(jsonObject);
        } else if (userInfo.isSchool()) {
            parseSchoolTitles(jsonObject);
        } else if (userInfo.isTeacher()) {
            parseTeacherTitles(jsonObject);
        } else if (userInfo.isStudent()) {
            parseStudentTitles(jsonObject);
        } else {
            parseParentTitles(jsonObject);
        }
        parseLiveTitles(jsonObject);
    }

    /**
     * 解析首页标题
     *
     * @param jsonObject
     */
    private static void parseMainTitles(JSONObject jsonObject) {
        sPagetitleIndexCompositeNew = jsonObject.optString("front.pagetitle.index.composite.new", "新闻");
        sPagetitleIndexCompositeNotice = jsonObject.optString("front.pagetitle.index.composite.notice", "通知");
        sPagetitleIndexCompositeAnnouncement = jsonObject.optString("front.pagetitle.index.composite.announcement", "公告");
        sPagetitleIndexCompositeResource = jsonObject.optString("front.pagetitle.index.composite.resource", "优课资源");
        sPagetitleIndexCompositeHotresource = jsonObject.optString("front.pagetitle.index.composite.hotresource", "热门资源");
        sPagetitleIndexCompositeTearec = jsonObject.optString("front.pagetitle.index.composite.tearec", "名师推荐");
        sPagetitleIndexCompositeOlclass = jsonObject.optString("front.pagetitle.index.composite.olclass", "直播课堂");
        sPagetitleIndexCompositeSpeclass = jsonObject.optString("front.pagetitle.index.composite.speclass", "专递课堂");
        sPagetitleIndexCompositeNetclass = jsonObject.optString("front.pagetitle.index.composite.netclass", "名校网络课堂");
        sPagetitleIndexCompositeNetteach = jsonObject.optString("front.pagetitle.index.composite.netteach", "网络教研");
        sPagetitleIndexCompositeInteract = jsonObject.optString("front.pagetitle.index.composite.interact", "互动听课");
        sPagetitleIndexCompositePreparelesson = jsonObject.optString("front.pagetitle.index.composite.preparelesson", "个人备课");
        sPagetitleIndexCompositePrepare = jsonObject.optString("front.pagetitle.index.composite.prepare", "集体备课");
        sPagetitleIndexCompositeDisucss = jsonObject.optString("front.pagetitle.index.composite.disucss", "评课议课");
        sPagetitleIndexCompositeRethink = jsonObject.optString("front.pagetitle.index.composite.rethink", "教学反思");
        sPagetitleIndexCompositeBlog = jsonObject.optString("front.pagetitle.index.composite.blog", "博文");
        sPagetitleIndexCompositeHotblog = jsonObject.optString("front.pagetitle.index.composite.hotblog", "热门博文");

        sPagetitleIndexMonitorOlclass = jsonObject.optString("front.pagetitle.index.monitor.olclass", "直播课堂");
        sPagetitleIndexMonitorSpeclass = jsonObject.optString("front.pagetitle.index.monitor.speclass", "专递课堂");
        sPagetitleIndexMonitorNetclass = jsonObject.optString("front.pagetitle.index.monitor.netclass", "名校网络课堂");
        sPagetitleIndexMonitorNew = jsonObject.optString("front.pagetitle.index.monitor.new", "新闻");
        sPagetitleIndexMonitorNotice = jsonObject.optString("front.pagetitle.index.monitor.notice", "通知");
        sPagetitleIndexMonitorAnnouncement = jsonObject.optString("front.pagetitle.index.monitor.announcement", "公告");
        sPagetitleIndexMonitorReplay = jsonObject.optString("front.pagetitle.index.monitor.replay", "课程回放");
        sPagetitleIndexMonitorResource = jsonObject.optString("front.pagetitle.index.monitor.resource", "优课资源");
        sPagetitleIndexMonitorHotresource = jsonObject.optString("front.pagetitle.index.monitor.hotresource", "热门资源");

        sPagetitleIndexPuremonitorSpeclass = jsonObject.optString("front.pagetitle.index.puremonitor.speclass", "专递课堂直播");
        sPagetitleIndexPuremonitorNetclass = jsonObject.optString("front.pagetitle.index.puremonitor.netclass", "名校网络课堂");

        sPagetitleIndexResourceOlclass = jsonObject.optString("front.pagetitle.index.resource.olclass", "直播课堂");
        sPagetitleIndexResourceSpeclass = jsonObject.optString("front.pagetitle.index.resource.speclass", "专递课堂");
        sPagetitleIndexResourceNetclass = jsonObject.optString("front.pagetitle.index.resource.netclass", "名校网络课堂");
        sPagetitleIndexResourceNew = jsonObject.optString("front.pagetitle.index.resource.new", "新闻");
        sPagetitleIndexResourceNoticeannoun = jsonObject.optString("front.pagetitle.index.resource.noticeannoun", "通知/公告");
        sPagetitleIndexResourceTearec = jsonObject.optString("front.pagetitle.index.resource.tearec", "名师推荐");
        sPagetitleIndexResourceHotresource = jsonObject.optString("front.pagetitle.index.resource.hotresource", "热门资源");
        sPagetitleIndexResourceGoodresource = jsonObject.optString("front.pagetitle.index.resource.goodresource", "精选资源");

        sPagetitleIndexNoLiveNew = jsonObject.optString("front.pagetitle.index.noLive.new", "新闻");
        sPagetitleIndexNoLiveNoticeannoun = jsonObject.optString("front.pagetitle.index.noLive.noticeannoun", "通知/公告");
        sPagetitleIndexNoLiveTearec = jsonObject.optString("front.pagetitle.index.noLive.tearec", "名师推荐");
        sPagetitleIndexNoLiveHotresource = jsonObject.optString("front.pagetitle.index.noLive.hotresource", "热门资源");
        sPagetitleIndexNoLiveGoodresource = jsonObject.optString("front.pagetitle.index.noLive.goodresource", "精选资源");
        sPagetitleIndexNoLiveHotgroup = jsonObject.optString("front.pagetitle.index.noLive.hotgroup", "热门圈组");

        sPagetitleIndexTianjinTianjin = jsonObject.optString("front.pagetitle.index.tianjin.tianjin", "天津");
        sPagetitleIndexTianjinClass = jsonObject.optString("front.pagetitle.index.tianjin.class", "课堂");
        sPagetitleIndexTianjinSchedule = jsonObject.optString("front.pagetitle.index.tianjin.schedule", "市级课表");
        sPagetitleIndexTianjinResource = jsonObject.optString("front.pagetitle.index.tianjin.resource", "精品资源");

        sPagetitleIndexSipRecentClass = jsonObject.optString("front.pagetitle.index.suzhousip.nearclass", "近期课程");

        sPagetitleIndexInfoNewcenter = jsonObject.optString("front.pagetitle.index.info.newcenter", "新闻中心");
        sPagetitleIndexInfoNoticecenter = jsonObject.optString("front.pagetitle.index.info.noticecenter", "通知中心");
        sPagetitleIndexInfoAnnouncenter = jsonObject.optString("front.pagetitle.index.info.announcenter", "公告中心");
        sPagetitleIndexInfoNew = jsonObject.optString("front.pagetitle.index.info.new", "新闻");
        sPagetitleIndexInfoNotice = jsonObject.optString("front.pagetitle.index.info.notice", "通知");
        sPagetitleIndexInfoAnnouncement = jsonObject.optString("front.pagetitle.index.info.announcement", "公告");

        //集团校　add by poe 20170825
        sPagetitleIndexClubSchoolTeachingActivity = jsonObject.optString("front.pagetitle.index.clubschool.teachingactivity", "教研活动");
        sPagetitleIndexClubSchoolInfoNew = jsonObject.optString("front.pagetitle.index.clubschool.newsnoticeannoun", "新闻/通知/公告");
        //  17-8-25 分割为三个类型的tag .
        sPagetitleIndexGroupSchoolNew = jsonObject.optString("front.pagetitle.index.clubschool.news", "新闻");
        sPagetitleIndexGroupSchoolNotice = jsonObject.optString("front.pagetitle.index.clubschool.notice", "通知");
        sPagetitleIndexGroupSchoolAnnouncement = jsonObject.optString("front.pagetitle.index.clubschool.announcement", "公告");

        sPagetitleIndexClubSchoolTeacherSuggest = jsonObject.optString("front.pagetitle.index.clubschool.tearecomd", "名师推荐");
        sPagetitleIndexClubSchoolClassDesign = jsonObject.optString("front.pagetitle.index.clubschool.classdesign", "课程建设");
        sPagetitleIndexClubSchoolLiveClass = jsonObject.optString("front.pagetitle.index.clubschool.liveclass", "直播课堂");
        sPagetitleIndexClubSchoolSchoolResource = jsonObject.optString("front.pagetitle.index.clubschool.schoolres", "优课资源");
        sPagetitleIndexClubSchoolResource = jsonObject.optString("front.pagetitle.index.clubschool.resource", "优课资源");
        sPagetitleIndexClubSchool = jsonObject.optString("front.pagetitle.index.clubschool.clubschoo", "集团学校");
    }

    /**
     * 门户页各标签标题解析
     *
     * @param jsonObject
     */
    private static void parsePortalTitles(JSONObject jsonObject) {
        sPagetitleSpeclassLive = jsonObject.optString("front.pagetitle.speclass.live", "正在直播");
        sPagetitleSpeclassReplay = jsonObject.optString("front.pagetitle.speclass.replay", "课程回放");

        sPagetitleNetclassLive = jsonObject.optString("front.pagetitle.netclass.live", "正在直播");
        sPagetitleNetclassRecomcourse = jsonObject.optString("front.pagetitle.netclass.recomcourse", "推荐课程");

        sPagetitleNetteachDisucss = jsonObject.optString("front.pagetitle.netteach.disucss", "评课议课");
        sPagetitleNetteachDisucssintro = jsonObject.optString("front.pagetitle.netteach.disucssintro", "评课议课介绍");
        sPagetitleNetteachDisucssstandintro = jsonObject.optString("front.pagetitle.netteach.disucssstandintro", "评估标准介绍");
        sPagetitleNetteachRecomddisucss = jsonObject.optString("front.pagetitle.netteach.recomddisucss", "推荐评课");
        sPagetitleNetteachHotdisucss = jsonObject.optString("front.pagetitle.netteach.hotdisucss", "热门评课");
        sPagetitleNetteachTearecomd = jsonObject.optString("front.pagetitle.netteach.tearecomd", "名师推荐");
        sPagetitleNetteachInteract = jsonObject.optString("front.pagetitle.netteach.interact", "互动听课");
        sPagetitleNetteachListen = jsonObject.optString("front.pagetitle.netteach.listen", "热门听课");
        sPagetitleNetteachAllprepare = jsonObject.optString("front.pagetitle.netteach.allprepare", "集体备课");
        sPagetitleNetteachHotprepare = jsonObject.optString("front.pagetitle.netteach.hotprepare", "热门备课");
        sPagetitleNetteachPrepare = jsonObject.optString("front.pagetitle.netteach.prepare", "个人备课");
        sPagetitleNetteachHotteaching = jsonObject.optString("front.pagetitle.netteach.hotteaching", "热门教案");
        sPagetitleNetteachRethink = jsonObject.optString("front.pagetitle.netteach.rethink", "教学反思");
        sPagetitleNetteachGoodrethink = jsonObject.optString("front.pagetitle.netteach.goodrethink", "优质教学反思");
        sPagetitleNetteachHotrethink = jsonObject.optString("front.pagetitle.netteach.hotrethink", "热门教学反思");
        sPagetitleNetteachRelatedrethink = jsonObject.optString("front.pagetitle.netteach.relatedrethink", "相关教学反思");

        sPagetitleBlogClassify = jsonObject.optString("front.pagetitle.blog.classify", "分类目录");
        sPagetitleBlogRecommend = jsonObject.optString("front.pagetitle.blog.recommend", "推荐博文");
        sPagetitleBlogHot = jsonObject.optString("front.pagetitle.blog.hot", "热门博文");

        sPagetitleGroupRecommend = jsonObject.optString("front.pagetitle.group.recommend", "推荐圈组");
        sPagetitleGroupActive = jsonObject.optString("front.pagetitle.group.active", "活跃圈组");

        sPagetitleAnsandansAllask = jsonObject.optString("front.pagetitle.ansandans.allask", "大家都在问");
        sPagetitleAnsandansUnsolve = jsonObject.optString("front.pagetitle.ansandans.unsolve", "未解决问题");
        sPagetitleAnsandansSolved = jsonObject.optString("front.pagetitle.ansandans.solved", "已解决问题");
        sPagetitleAnsandansKeysort = jsonObject.optString("front.pagetitle.ansandans.keysort", "关键字排行");
        sPagetitleAnsandansHot = jsonObject.optString("front.pagetitle.ansandans.hot", "热门问题");
        sPagetitleAnsandansExplainsort = jsonObject.optString("front.pagetitle.ansandans.explainsort", "解答排行榜");
        sPagetitleAnsandansAcceptsort = jsonObject.optString("front.pagetitle.ansandans.acceptsort", "采纳排行榜");

        sPagetitleMatchDetails = jsonObject.optString("front.pagetitle.match.details", "赛课详情");
        sPagetitleMatchCourse = jsonObject.optString("front.pagetitle.match.course", "赛课课程");
        sPagetitleMatchAllmatch = jsonObject.optString("front.pagetitle.match.allmatch", "他们都在赛");
        sPagetitleMatchNearactive = jsonObject.optString("front.pagetitle.match.nearactive", "近期活动");
        sPagetitleMatchActivecenter = jsonObject.optString("front.pagetitle.match.activecenter", "活动中心");
        sPagetitleMatchMatchsort = jsonObject.optString("front.pagetitle.match.matchsort", "赛课排行");
        sPagetitleMatchActivedate = jsonObject.optString("front.pagetitle.match.activedate", "活动日期");

        sPagetitleGlobalNew = jsonObject.optString("front.pagetitle.global.new", "新闻");
        sPagetitleGlobalNotice = jsonObject.optString("front.pagetitle.global.notice", "通知");
        sPagetitleGlobalAnnouncement = jsonObject.optString("front.pagetitle.global.announcement", "公告");
        sPagetitleGlobalHotresource = jsonObject.optString("front.pagetitle.global.hotresource", "热门资源");
        sPagetitleGlobalRecommendgroup = jsonObject.optString("front.pagetitle.global.recommendgroup", "推荐圈组");
        sPagetitleGlobalActivegroup = jsonObject.optString("front.pagetitle.global.activegroup", "活跃圈组");
        sPagetitleGlobalSpaceMicroblog = jsonObject.optString("front.pagetitle.global.space.microblog", "微博");
    }

    private static void parseAppNames(JSONObject jsonObject) {
        sWorkspaceSpeclass = jsonObject.optString("front.workspace.speclass", "专递课堂");
        sWorkspaceNetClass = jsonObject.optString("front.workspace.netclass", "名校网络课堂");
        sWorkspacePrepare = jsonObject.optString("front.workspace.prepare", "集体备课");//集体备课
        sWorkspaceListen = jsonObject.optString("front.workspace.listen", "互动听课");//互动听课
        sWorkspaceDisucss = jsonObject.optString("front.workspace.disucss", "评课议课");//评课议课
        sWorkspaceVidmeet = jsonObject.optString("front.workspace.vidmeet", "视频会议");//视频会议
        sWorkspaceInfo = jsonObject.optString("front.workspace.info", "新闻");//新闻
        sWorkspaceNoticeAnnouncement = jsonObject.optString("front.workspace.onticeannouncement", "通知公告");//通知公告
        sWorkspaceResource = jsonObject.optString("front.workspace.resource", "优课资源");//优课资源
        sWorkspaceCount = jsonObject.optString("front.workspace.count", "统计");//统计
        sWorkspaceBase = jsonObject.optString("front.workspace.base", "基础设置");//基础设置
        sWorkspacePrepareLesson = jsonObject.optString("front.workspace.preparelesson", "个人备课");//个人备课
        sWorkspaceOnlineEdit = jsonObject.optString("front.workspace.onlineedit", "在线编辑");//在线编辑
        sWorkspaceRethink = jsonObject.optString("front.workspace.rethink", "教学反思");//教学反思
        sWorkspaceGroup = jsonObject.optString("front.workspace.group", "圈组");//圈组
        sWorkspaceSchedule = jsonObject.optString("front.workspace.schedule", "课程表");//课程表
        sWorkspaceMember = jsonObject.optString("front.workspace.member", "班级成员");//班级成员
        sWorkspaceMultiMedia = jsonObject.optString("front.workspace.multimedia", "多媒体教室管理");//多媒体教室管理
        sWorkspaceQuestionLib = jsonObject.optString("front.workspace.questionlib", "题库");//题库
        sWorkspaceTest = jsonObject.optString("front.workspace.test", "测试");//测试
        sWorkspaceHomework = jsonObject.optString("front.workspace.homework", "作业");//作业
        sWorkspaceBlog = jsonObject.optString("front.workspace.blog", "博文");//博文
        sWorkspaceTutor = jsonObject.optString("front.workspace.tutor", "辅导");//辅导
        sWorkspaceRepairs = jsonObject.optString("front.workspace.repairs", "报修信息跟踪");//报修信息跟踪
        sWorkspaceAskAndAns = jsonObject.optString("front.workspace.askandans", "问答");//问答
        sWorkspaceMatch = jsonObject.optString("front.workspace.match", "赛课");
        sWorkspaceOnlineClassExam = jsonObject.optString("front.workspace.onlineclassexam", "随堂测验");
        sWorkspaceQuestionnaire = jsonObject.optString("front.workspace.questionnaire", "调查问卷");
        sWorkspaceNetTeach = jsonObject.optString("front.workspace.netteach", "网络授课");
        sWorkspaceTvProgram = jsonObject.optString("front.workspace.tvProgram", "校园电视台");
    }

    private static void parseAreaTitles(JSONObject jsonObject) {
        //专递课堂
        sWorkspaceSpeclassAllTable = jsonObject.optString("front.workspace.speclass.alltable.area", sWorkspaceSpeclassAllTable);
        sWorkspaceSpeclassSpacktable = jsonObject.optString("front.workspace.speclass.spacktable.area", sWorkspaceSpeclassSpacktable);
        sWorkspaceSpeclassReceivetable = jsonObject.optString("front.workspace.speclass.receivetable.area", sWorkspaceSpeclassReceivetable);
        sWorkspaceSpeclassSpacktea = jsonObject.optString("front.workspace.speclass.spacktea.area", sWorkspaceSpeclassSpacktea);
        sWorkspaceSpeclassSchedule = jsonObject.optString("front.workspace.speclass.schedule.area", sWorkspaceSpeclassSchedule);
        sWorkspaceSpeclassLive = jsonObject.optString("front.workspace.speclass.live.area", sWorkspaceSpeclassLive);
        sWorkspaceSpeclassReplay = jsonObject.optString("front.workspace.speclass.replay.area", sWorkspaceSpeclassReplay);
        sWorkspaceSpeclassTask = jsonObject.optString("front.workspace.speclass.task.area", sWorkspaceSpeclassTask);
        sWorkspaceSpeclassRound = jsonObject.optString("front.workspace.speclass.round.area", sWorkspaceSpeclassRound);

        //名校网络课堂
        sWorkspaceNetClassClass = jsonObject.optString("front.workspace.netclass.class.area", sWorkspaceNetClassClass);//约课列表
        sWorkspaceNetClassLive = jsonObject.optString("front.workspace.netclass.live.area", sWorkspaceNetClassLive);//"正在直播"
        sWorkspaceNetClassRound = jsonObject.optString("front.workspace.netclass.round.area", sWorkspaceNetClassRound);//"实时轮巡";
        sWorkspaceNetClassReplay = jsonObject.optString("front.workspace.netclass.replay.area", sWorkspaceNetClassReplay);//"往期录播";
        sWorkspaceNetClassTask = jsonObject.optString("front.workspace.netclass.task.area", sWorkspaceNetClassTask);//"课堂作业";

        //集体备课
        sWorkspacePrepareLaunch = jsonObject.optString("front.workspace.prepare.launch.area", "我发起的");//"我发起的";
        sWorkspacePrepareAttend = jsonObject.optString("front.workspace.prepare.attend.area", "我参与的");//"我参与的";
        sWorkspacePreparePrepare = jsonObject.optString("front.workspace.prepare.areaprepare.area", "区内备课管理");//"区内备课管理";

        //互动听课
        sWorkspaceListenLaunch = jsonObject.optString("front.workspace.listen.launch.area", "我发起的");//我发起的
        sWorkspaceListenManage = jsonObject.optString("front.workspace.listen.manage.area", "区内听课管理");//区内听课管理

        //评课议课
        sWorkspaceDisucssLaunch = jsonObject.optString("front.workspace.disucss.launch.area", "我发起的");//我发起的
        sWorkspaceDisucssInArea = jsonObject.optString("front.workspace.disucss.inarea.area", "区内评课");//区内评课
        sWorkspaceDisucssEvaluStand = jsonObject.optString("front.workspace.disucss.evalustand.area", "我的评估标准");//我的评估标准
        sWorkspaceDisucssPublishStand = jsonObject.optString("front.workspace.disucss.publishstand.area", "公开的评估标准");//公开的评估标准

        //视频会议
        sWorkspaceVidmeetLaunch = jsonObject.optString("front.workspace.vidmeet.launch.area", "我发起的");
        sWorkspaceVidmeetAttend = jsonObject.optString("front.workspace.vidmeet.attend.area", "我参与的");
        sWorkspaceVidmeetManage = jsonObject.optString("front.workspace.vidmeet.manage.area", "本级会议管理");

        //新闻
        sWorkspaceInfoNew = jsonObject.optString("front.workspace.info.new.school", "新闻");
        sWorkspaceInfoDraft = jsonObject.optString("front.workspace.info.draft.school", "草稿箱");

        //通知公告
        sWorkspaceNoticeAnnouncementAnnouncement = jsonObject.optString("front.workspace.onticeannouncement.announcement.school", "公告");
        sWorkspaceNoticeAnnouncementNotice = jsonObject.optString("front.workspace.onticeannouncement.notice.school", "通告");
        sWorkspaceNoticeAnnouncementDraft = jsonObject.optString("front.workspace.onticeannouncement.draft.school", "草稿箱");

        //优课资源
        sWorkspaceResourceUpload = jsonObject.optString("front.workspace.resource.upload.area", "本级上传");
        sWorkspaceResourceRecommend = jsonObject.optString("front.workspace.resource.recommend.area", "下级推荐");
        sWorkspaceResourceCollect = jsonObject.optString("front.workspace.resource.collect.area", "专辑");

        //统计
        sWorkspaceCountClass = jsonObject.optString("front.workspace.count.class.area", "课堂统计");
        sWorkspaceCountTutiongeneral = jsonObject.optString("front.workspace.count.tutiongeneral.area", "开课概况");
        sWorkspaceCountTutiontate = jsonObject.optString("front.workspace.count.tutiontate.area", "开课比分析");
        sWorkspaceCountSubject = jsonObject.optString("front.workspace.count.subject.area", "学科统计");
        sWorkspaceCountResource = jsonObject.optString("front.workspace.count.resource.area", "资源统计");
        sWorkspaceCountBysubject = jsonObject.optString("front.workspace.count.bysubject.area", "按学科统计");
        sWorkspaceCountByclasslevel = jsonObject.optString("front.workspace.count.byclasslevel.area", "按年级统计");
        sWorkspaceCountBysub = jsonObject.optString("front.workspace.count.bysub.area", "按下级统计");
        sWorkspaceCountDisucss = jsonObject.optString("front.workspace.count.disucss.area", "评课统计");
        sWorkspaceCountSubject1 = jsonObject.optString("front.workspace.count.subject1.area", "学科统计");
        sWorkspaceCountClasslevel = jsonObject.optString("front.workspace.count.classlevel.area", "年级统计");
        sWorkspaceCountSublaunch = jsonObject.optString("front.workspace.count.sublaunch.area", "下级发起统计");
        sWorkspaceCountTeacher = jsonObject.optString("front.workspace.count.teacher.area", "主讲教师统计");

        //基础设置
        sWorkspaceBaseAccountmanage = jsonObject.optString("front.workspace.base.accountmanage.area", "帐号管理");
        sWorkspaceBaseRolemanage = jsonObject.optString("front.workspace.base.rolemanage.area", "角色管理");
        sWorkspaceBaseUsermanage = jsonObject.optString("front.workspace.base.usermanage.area", "用户管理");
        sWorkspaceBaseSubmanage = jsonObject.optString("front.workspace.base.submanage.area", "下级管理");
        sWorkspaceBaseSchoolmanage = jsonObject.optString("front.workspace.base.schoolmanage.area", "学校管理");

        //在线编辑
        sWorkspaceOnlineEditSection = jsonObject.optString("front.workspace.onlineedit.section.area", "视频切片");
        sWorkspaceOnlineEditLabel = jsonObject.optString("front.workspace.onlineedit.label.area", "看点标注");
        sWorkspaceOnlineEditTest = jsonObject.optString("front.workspace.onlineedit.test.area", "测验");

        //圈组
        sWorkspaceGroupAreaGroup = jsonObject.optString("front.workspace.group.areagroup.area", "辖区内圈组");
        sWorkspaceGroupMyGroup = jsonObject.optString("front.workspace.group.mygroup.area", "我的圈组");

        //题库
        sWorkspaceQuestionLibShare = jsonObject.optString("front.workspace.questionlib.share.area", "共享习题");
        sWorkspaceQuestionLibMyLib = jsonObject.optString("front.workspace.questionlib.mylib.area", "我的习题");

        //赛课
        sWorkspaceMatchActiveCenter = jsonObject.optString("front.workspace.match.activecenter.area", "活动中心");
        sWorkspaceMatchMyDisCourse = jsonObject.optString("front.workspace.match.mydiscourse.area", "我评的课");

        //调查问卷
        sWorkspaceQuestionnaireSameLevel = jsonObject.optString("front.workspace.questionnaire.samelevel.area", "本级调查");
        sWorkspaceQuestionnaireTask = jsonObject.optString("front.workspace.questionnaire.task.area", "调查任务");

        //网络授课
        sWorkspaceNetTeachMyCreate = jsonObject.optString("front.workspace.netteach.mycreate.area", "我创建的");
        sWorkspaceNetTeachManage = jsonObject.optString("front.workspace.netteach.manage.area", "区内课程管理");
    }

    private static void parseSchoolTitles(JSONObject jsonObject) {
        //专递课堂
        sWorkspaceSpeclassSchedule = jsonObject.optString("front.workspace.speclass.schedule.school", sWorkspaceSpeclassSchedule);
        sWorkspaceSpeclassLive = jsonObject.optString("front.workspace.speclass.live.school", sWorkspaceSpeclassLive);
        sWorkspaceSpeclassReplay = jsonObject.optString("front.workspace.speclass.replay.school", sWorkspaceSpeclassReplay);
        sWorkspaceSpeclassTask = jsonObject.optString("front.workspace.speclass.task.school", sWorkspaceSpeclassTask);
        sWorkspaceSpeclassRound = jsonObject.optString("front.workspace.speclass.round.school", sWorkspaceSpeclassRound);

        //名校网络课堂
        sWorkspaceNetClassClass = jsonObject.optString("front.workspace.netclass.class.school", sWorkspaceNetClassClass);//约课列表
        sWorkspaceNetClassLive = jsonObject.optString("front.workspace.netclass.live.school", sWorkspaceNetClassLive);//"正在直播"
        sWorkspaceNetClassRound = jsonObject.optString("front.workspace.netclass.round.school", sWorkspaceNetClassRound);//"实时轮巡";
        sWorkspaceNetClassReplay = jsonObject.optString("front.workspace.netclass.replay.school", sWorkspaceNetClassReplay);//"往期录播";
        sWorkspaceNetClassTask = jsonObject.optString("front.workspace.netclass.task.school", sWorkspaceNetClassTask);//"课堂作业";

        //集体备课
        sWorkspacePrepareLaunch = jsonObject.optString("front.workspace.prepare.launch.school", "我发起的");//"我发起的";
        sWorkspacePrepareAttend = jsonObject.optString("front.workspace.prepare.attend.school", "我参与的");//"我参与的";
        sWorkspacePreparePrepare = jsonObject.optString("front.workspace.schoolprepare.school", "本校备课管理");//"区内备课管理";

        //互动听课
        sWorkspaceListenLaunch = jsonObject.optString("front.workspace.listen.launch.school", "我发起的");//我发起的
        sWorkspaceListenManage = jsonObject.optString("front.workspace.listen.manage.school", "区内听课管理");//区内听课管理

        //评课议课
        sWorkspaceDisucssLaunch = jsonObject.optString("front.workspace.disucss.launch.school", "我发起的");//我发起的
        sWorkspaceDisucssInvited = jsonObject.optString("front.workspace.disucss.invited.school", "我受邀的");
        sWorkspaceDisucssTeaLaunch = jsonObject.optString("front.workspace.disucss.tealaunch.school", "教师发起的");
        sWorkspaceDisucssInSchool = jsonObject.optString("front.workspace.disucss.inschool.school", "本校主讲的");
        sWorkspaceDisucssEvaluStand = jsonObject.optString("front.workspace.disucss.evalustand.school", "我的评估标准");//我的评估标准
        sWorkspaceDisucssPublishStand = jsonObject.optString("front.workspace.disucss.publishstand.school", "公开的评估标准");//公开的评估标准

        //视频会议
        sWorkspaceVidmeetLaunch = jsonObject.optString("front.workspace.disucss.launch.school", "我发起的");
        sWorkspaceVidmeetAttend = jsonObject.optString("front.workspace.vidmeet.attend.school", "我参与的");
        sWorkspaceVidmeetManage = jsonObject.optString("front.workspace.vidmeet.manage.school", "本校会议管理");

        //新闻
        sWorkspaceInfoNew = jsonObject.optString("front.workspace.info.new.school", "新闻");
        sWorkspaceInfoDraft = jsonObject.optString("front.workspace.info.draft.school", "草稿箱");

        //通知公告
        sWorkspaceNoticeAnnouncementAnnouncement = jsonObject.optString("front.workspace.onticeannouncement.announcement.school", "公告");
        sWorkspaceNoticeAnnouncementNotice = jsonObject.optString("front.workspace.onticeannouncement.notice.school", "通告");
        sWorkspaceNoticeAnnouncementDraft = jsonObject.optString("front.workspace.onticeannouncement.draft.school", "草稿箱");

        //优课资源
        sWorkspaceResourceUpload = jsonObject.optString("front.workspace.resource.upload.school", "本级上传");
        sWorkspaceResourceRecommend = jsonObject.optString("front.workspace.resource.recommend.school", "教师推荐");
        sWorkspaceResourceSpecial = jsonObject.optString("front.workspace.resource.special.school", "专辑");

        //统计
        sWorkspaceCountClass = jsonObject.optString("front.workspace.count.class.school", "课堂统计");
        sWorkspaceCountTutiongeneral = jsonObject.optString("front.workspace.count.tutiongeneral.school", "开课概况");
        sWorkspaceCountTutiontate = jsonObject.optString("front.workspace.count.tutiontate.school", "开课比分析");
        sWorkspaceCountSubject = jsonObject.optString("front.workspace.count.subject.school", "学科统计");
        sWorkspaceCountResource = jsonObject.optString("front.workspace.count.resource.school", "资源统计");
        sWorkspaceCountBysubject = jsonObject.optString("front.workspace.count.bysubject.school", "按学科统计");
        sWorkspaceCountByclasslevel = jsonObject.optString("front.workspace.count.byclasslevel.school", "按年级统计");
        sWorkspaceCountBytea = jsonObject.optString("front.workspace.count.bytea.school", "按教师统计");
        sWorkspaceCountDisucss = jsonObject.optString("front.workspace.count.disucss.school", "评课统计");
        sWorkspaceCountSubject1 = jsonObject.optString("front.workspace.count.subject1.school", "学科统计");
        sWorkspaceCountClasslevel = jsonObject.optString("front.workspace.count.classlevel.school", "年级统计");
        sWorkspaceCountTeacher = jsonObject.optString("front.workspace.count.teacher.school", "主讲教师统计");

        //基础设置
        sWorkspaceBaseAccountmanage = jsonObject.optString("front.workspace.base.accountmanage.school", "帐号管理");
        sWorkspaceBaseRolemanage = jsonObject.optString("front.workspace.base.rolemanage.school", "角色管理");
        sWorkspaceBaseUsermanage = jsonObject.optString("front.workspace.base.usermanage.school", "用户管理");
        sWorkspaceBaseTeamanage = jsonObject.optString("front.workspace.base.teamanage.school", "教师管理");
        sWorkspaceBaseStumanage = jsonObject.optString("front.workspace.base.stumanage.school", "学生管理");
        sWorkspaceBaseParmanage = jsonObject.optString("front.workspace.base.parmanage.school", "家长管理");
        sWorkspaceBaseClassmanage = jsonObject.optString("front.workspace.base.classmanage.school", "班级管理");
        sWorkspaceBaseCurclass = jsonObject.optString("front.workspace.base.curclass.school", "当前班级");
        sWorkspaceBaseHisclass = jsonObject.optString("front.workspace.base.hisclass.school", "历史班级");
        sWorkspaceBaseCreateclass = jsonObject.optString("front.workspace.base.createclass.school", "创建班级");
        sWorkspaceBaseClsroommanage = jsonObject.optString("front.workspace.base.clsroommanage.school", "教室管理");
        sWorkspaceBaseSchedulemanage = jsonObject.optString("front.workspace.base.schedulemanage.school", "课表管理");

        //个人备课
        sWorkspacePrepareLessonPrepareLesson = jsonObject.optString("front.workspace.preparelesson.preparelesson.school", "教案");
        sWorkspacePrepareLessonPrepareImage = jsonObject.optString("front.workspace.preparelesson.prepareimage.school", "学科素材");

        //在线编辑
        sWorkspaceOnlineEditSection = jsonObject.optString("front.workspace.onlineedit.section.school", "视频切片");
        sWorkspaceOnlineEditLabel = jsonObject.optString("front.workspace.onlineedit.label.school", "看点标注");
        sWorkspaceOnlineEditTest = jsonObject.optString("front.workspace.onlineedit.test.school", "测验");

        //圈组
        sWorkspaceGroupSchoolGroup = jsonObject.optString("front.workspace.group.schoolgroup.school", "校内圈组");
        sWorkspaceGroupMyGroup = jsonObject.optString("front.workspace.group.mygroup.school", "我的圈组");

        //题库
        sWorkspaceQuestionLibShare = jsonObject.optString("front.workspace.questionlib.share.school", "共享习题");
        sWorkspaceQuestionLibMyLib = jsonObject.optString("front.workspace.questionlib.mylib.school", "我的习题");
        sWorkspaceQuestionLibCollect = jsonObject.optString("front.workspace.questionlib.collect.school", "真题试卷");

        //测试
        sWorkspaceTestGrade = jsonObject.optString("front.workspace.test.grade.school", "学校统考");
        sWorkspaceTestClass = jsonObject.optString("front.workspace.test.class.school", "班级测试");

        //辅导
        sWorkspaceTutorTution = jsonObject.optString("front.workspace.tutor.tution.school", "本校开课");
        sWorkspaceTutorListen = jsonObject.optString("front.workspace.tutor.listen.school", "跨校听课");
        sWorkspaceTutorSysconfig = jsonObject.optString("front.workspace.tutor.sysconfig.school", "系统设置");
        sWorkspaceTutorManage = jsonObject.optString("front.workspace.tutor.manage.school", "调查管理");
        sWorkspaceTutorWaterMark = jsonObject.optString("front.workspace.tutor.watermark.school", "视频水印");

        //赛课
        sWorkspaceMatchActiveCenter = jsonObject.optString("front.workspace.match.activecenter.school", "活动中心");
        sWorkspaceMatchMyDisCourse = jsonObject.optString("front.workspace.match.mydiscourse.school", "我评的课");

        //调查问卷
        sWorkspaceQuestionnaireSameLevel = jsonObject.optString("front.workspace.questionnaire.samelevel.school", "本校调查");
        sWorkspaceQuestionnaireTask = jsonObject.optString("front.workspace.questionnaire.task.school", "调查任务");

        //网络授课
        sWorkspaceNetTeachMyCreate = jsonObject.optString("front.workspace.netteach.mycreate.school", "我创建的");
        sWorkspaceNetTeachManage = jsonObject.optString("front.workspace.netteach.manage.school", "本校课程管理");

        //校园电视台
        sWorkspaceTvProgramLive = jsonObject.optString("front.workspace.tvProgram.live.school", "直播");
        sWorkspaceTvProgramReplay = jsonObject.optString("front.workspace.tvProgram.replay.school", "往期视频");
        sWorkspaceTvProgramProgramList = jsonObject.optString("front.workspace.tvProgram.programlist.school", "节目表");
    }

    private static void parseTeacherTitles(JSONObject jsonObject) {
        //专递课堂
        sWorkspaceSpeclassSchedule = jsonObject.optString("front.workspace.speclass.schedule.tea", sWorkspaceSpeclassSchedule);
        sWorkspaceSpeclassLive = jsonObject.optString("front.workspace.speclass.live.tea", sWorkspaceSpeclassLive);
        sWorkspaceSpeclassReplay = jsonObject.optString("front.workspace.speclass.replay.tea", sWorkspaceSpeclassReplay);
        sWorkspaceSpeclassTask = jsonObject.optString("front.workspace.speclass.task.tea", sWorkspaceSpeclassTask);
        sWorkspaceSpeclassBroadcast = jsonObject.optString("front.workspace.speclass.broadcast.tea", sWorkspaceSpeclassBroadcast);

        //名校网络课堂
        sWorkspaceNetClassClass = jsonObject.optString("front.workspace.netclass.class.tea", sWorkspaceNetClassClass);//约课列表
        sWorkspaceNetClassLive = jsonObject.optString("front.workspace.netclass.live.tea", sWorkspaceNetClassLive);//"正在直播"
        sWorkspaceNetClassReplay = jsonObject.optString("front.workspace.netclass.replay.tea", sWorkspaceNetClassReplay);//"往期录播";
        sWorkspaceNetClassBroadcast = jsonObject.optString("front.workspace.netclass.broadcast.tea", sWorkspaceNetClassBroadcast);//"远程导播";
        sWorkspaceNetClassTask = jsonObject.optString("front.workspace.netclass.task.tea", sWorkspaceNetClassTask);//"课堂作业";

        //集体备课
        sWorkspacePrepareLaunch = jsonObject.optString("front.workspace.prepare.launch.tea", "我发起的");
        sWorkspacePrepareAttend = jsonObject.optString("front.workspace.prepare.attend.tea", "我参与的");

        //互动听课
        sWorkspaceListenLaunch = jsonObject.optString("front.workspace.listen.launch.tea", "我发起的");//我发起的
        sWorkspaceListenAttend = jsonObject.optString("front.workspace.listen.attend.tea", "我参与的");//我参与的

        //评课议课
        sWorkspaceDisucssLaunch = jsonObject.optString("front.workspace.disucss.launch.tea", "我发起的");//我发起的
        sWorkspaceDisucssAttend = jsonObject.optString("front.workspace.disucss.attend.tea", "我参与的");
        sWorkspaceDisucssMySpack = jsonObject.optString("front.workspace.disucss.myspack.tea", "我主讲的");
        sWorkspaceDisucssEvaluStand = jsonObject.optString("front.workspace.disucss.evalustand.tea", "我的评估标准");//我的评估标准
        sWorkspaceDisucssPublishStand = jsonObject.optString("front.workspace.disucss.publishstand.tea", "公开的评估标准");//公开的评估标准

        //视频会议
        sWorkspaceVidmeetLaunch = jsonObject.optString("front.workspace.disucss.launch.tea", "我发起的");
        sWorkspaceVidmeetAttend = jsonObject.optString("front.workspace.vidmeet.attend.tea", "我参与的");

        //优课资源
        sWorkspaceResourceUpload = jsonObject.optString("front.workspace.resource.upload.tea", "我上传的");
        sWorkspaceResourceRecommend = jsonObject.optString("front.workspace.resource.recommend.tea", "学生推荐");
        sWorkspaceResourceSpecial = jsonObject.optString("front.workspace.resource.special.tea", "我的专辑");
        sWorkspaceResourceCollect = jsonObject.optString("front.workspace.resource.collect.tea", "我的收藏");

        //学科素材
        sWorkspacePrepareLessonPrepareLesson = jsonObject.optString("front.workspace.preparelesson.preparelesson.tea", "教案");
        sWorkspacePrepareLessonPrepareImage = jsonObject.optString("front.workspace.preparelesson.prepareimage.tea", "学科素材");

        //在线编辑
        sWorkspaceOnlineEditSection = jsonObject.optString("front.workspace.onlineedit.section.tea", "视频切片");
        sWorkspaceOnlineEditLabel = jsonObject.optString("front.workspace.onlineedit.label.tea", "看点标注");
        sWorkspaceOnlineEditTest = jsonObject.optString("front.workspace.onlineedit.test.tea", "测验");

        //课程表
        sWorkspaceSchedulePersonal = jsonObject.optString("front.workspace.schedule.personal.tea", "个人课表");
        sWorkspaceScheduleClass = jsonObject.optString("front.workspace.schedule.class.tea", "班级课表");

        //题库
        sWorkspaceQuestionLibShare = jsonObject.optString("front.workspace.questionlib.share.tea", "共享习题");
        sWorkspaceQuestionLibShare = jsonObject.optString("front.workspace.questionlib.mylib.tea", "我的习题");
        sWorkspaceQuestionLibCollect = jsonObject.optString("front.workspace.questionlib.collect.tea", "收藏习题");

        //测试
        sWorkspaceTestSelf = jsonObject.optString("front.workspace.test.self.tea", "我的试卷");
        sWorkspaceTestSubject = jsonObject.optString("front.workspace.test.subject.tea", "真题试卷");
        sWorkspaceTestTask = jsonObject.optString("front.workspace.test.task.tea", "测试任务");

        //辅导
        sWorkspaceTutorClsList = jsonObject.optString("front.workspace.tutor.clslist.stu", "课堂列表");
        sWorkspaceTutorTutorRoom = jsonObject.optString("front.workspace.tutor.tutorroom.stu", "辅导室");
//        sWorkspaceTutorTest = jsonObject.optString("front.workspace.tutor.test", "随堂测验");
        sWorkspaceTutorTest = jsonObject.optString("front.workspace.onlineclassexam", "随堂测验");

        //问答
        sWorkspaceAskAndAnsMyAsk = jsonObject.optString("front.workspace.askandans.myask.tea", "我的提问");
        sWorkspaceAskAndAnsMyAnswer = jsonObject.optString("front.workspace.askandans.myanswer.tea", "我的回答");
        sWorkspaceAskAndAnsStuAsk = jsonObject.optString("front.workspace.askandans.stuask.tea", "学生提问");
        sWorkspaceAskAndAnsWaitMyAnswer = jsonObject.optString("front.workspace.askandans.waitmyanswer.tea", "等我来答");

        //赛课
        sWorkspaceMatchMyActive = jsonObject.optString("front.workspace.match.myactive.tea", "我的活动");
        sWorkspaceMatchMyDisCourse = jsonObject.optString("front.workspace.match.mydiscourse.tea", "我评的课");

        //调查问卷
        sWorkspaceQuestionnaireMy = jsonObject.optString("front.workspace.questionnaire.my.tea", "我的调查");
        sWorkspaceQuestionnaireStuAppraisal = jsonObject.optString("front.workspace.questionnaire.stuappraisal.tea", "学生测评");
        sWorkspaceQuestionnaireCls = jsonObject.optString("front.workspace.questionnaire.cls.tea", "班级调查");

        //网络授课
        sWorkspaceNetTeachMyCreate = jsonObject.optString("front.workspace.netteach.mycreate.tea", "我创建的");
        sWorkspaceNetTeachMyCourse = jsonObject.optString("front.workspace.netteach.mycourse.tea", "我的课程");

        //校园电视台
        sWorkspaceTvProgramLive = jsonObject.optString("front.workspace.tvProgram.live.tea", "直播");
        sWorkspaceTvProgramReplay = jsonObject.optString("front.workspace.tvProgram.replay.tea", "往期视频");
        sWorkspaceTvProgramProgramList = jsonObject.optString("front.workspace.tvProgram.programlist.tea", "节目表");
    }

    private static void parseStudentTitles(JSONObject jsonObject) {
        //专递课堂
        sWorkspaceSpeclassLive = jsonObject.optString("front.workspace.speclass.live.stu", sWorkspaceSpeclassLive);
        sWorkspaceSpeclassReplay = jsonObject.optString("front.workspace.speclass.replay.stu", sWorkspaceSpeclassReplay);
        sWorkspaceSpeclassTask = jsonObject.optString("front.workspace.speclass.task.stu", sWorkspaceSpeclassTask);

        //名校网络课堂
        sWorkspaceNetClassLive = jsonObject.optString("front.workspace.netclass.live.stu", sWorkspaceNetClassLive);//"正在直播"
        sWorkspaceNetClassReplay = jsonObject.optString("front.workspace.netclass.replay.stu", sWorkspaceNetClassReplay);//"往期录播";
        sWorkspaceNetClassTask = jsonObject.optString("front.workspace.netclass.task.stu", sWorkspaceNetClassTask);//"课堂作业";

        //优课资源
        sWorkspaceResourceUpload = jsonObject.optString("front.workspace.resource.upload.stu", "我上传的");
        sWorkspaceResourceSpecial = jsonObject.optString("front.workspace.resource.special.stu", "我的专辑");
        sWorkspaceResourceCollect = jsonObject.optString("front.workspace.resource.collect.stu", "我的收藏");

        //在线编辑
        sWorkspaceOnlineEditSection = jsonObject.optString("front.workspace.onlineedit.section.stu", "视频切片");
        sWorkspaceOnlineEditLabel = jsonObject.optString("front.workspace.onlineedit.label.stu", "看点标注");
        sWorkspaceOnlineEditTest = jsonObject.optString("front.workspace.onlineedit.test.stu", "测验");

        //测试
        sWorkspaceTestTask = jsonObject.optString("front.workspace.test.task.stu", "测试任务");
        sWorkspaceTestAuto = jsonObject.optString("front.workspace.test.auto.stu", "自主测试");

        //作业
        sWorkspaceHomeworkList = jsonObject.optString("front.workspace.homework.list.stu", "我的作业");
        sWorkspaceHomeworkReadOver = jsonObject.optString("front.workspace.homework.readover.stu", "我的批阅");

        //辅导
        sWorkspaceTutorClsList = jsonObject.optString("front.workspace.tutor.clslist.stu", "课堂列表");
        sWorkspaceTutorTutorRoom = jsonObject.optString("front.workspace.tutor.tutorroom.stu", "辅导室");

        //问答
        sWorkspaceAskAndAnsMyAsk = jsonObject.optString("front.workspace.askandans.myask.stu", "我的提问");
        sWorkspaceAskAndAnsMyAnswer = jsonObject.optString("front.workspace.askandans.myanswer.stu", "我的回答");
        sWorkspaceAskAndAnsMateAsk = jsonObject.optString("front.workspace.askandans.mateask.stu", "同学提问");
        sWorkspaceAskAndAnsWaitMyAnswer = jsonObject.optString("front.workspace.askandans.waitmyanswer.stu", "等我来答");

        //调查问卷
        sWorkspaceQuestionnaireTask = jsonObject.optString("front.workspace.questionnaire.task.stu", "调查任务");

        //网络授课
        sWorkspaceNetTeachMyCourse = jsonObject.optString("front.workspace.netteach.mycourse.stu", "我的课程");
        sWorkspaceNetTeachSubCourse = jsonObject.optString("front.workspace.netteach.subcourse.stu", "课程订阅");

        //校园电视台
        sWorkspaceTvProgramLive = jsonObject.optString("front.workspace.tvProgram.live.stu", "直播");
        sWorkspaceTvProgramReplay = jsonObject.optString("front.workspace.tvProgram.replay.stu", "往期视频");
        sWorkspaceTvProgramProgramList = jsonObject.optString("front.workspace.tvProgram.programlist.stu", "节目表");
    }

    private static void parseParentTitles(JSONObject jsonObject) {
        //专递课堂
        sWorkspaceSpeclassLive = jsonObject.optString("front.workspace.speclass.live.par", sWorkspaceSpeclassLive);
        sWorkspaceSpeclassReplay = jsonObject.optString("front.workspace.speclass.replay.par", sWorkspaceSpeclassReplay);
        sWorkspaceSpeclassTask = jsonObject.optString("front.workspace.speclass.task.par", sWorkspaceSpeclassTask);

        //名校网络课堂
        sWorkspaceNetClassLive = jsonObject.optString("front.workspace.netclass.live.par", sWorkspaceNetClassLive);//"正在直播"
        sWorkspaceNetClassReplay = jsonObject.optString("front.workspace.netclass.replay.par", sWorkspaceNetClassReplay);//"往期录播";
        sWorkspaceNetClassTask = jsonObject.optString("front.workspace.netclass.task.par", sWorkspaceNetClassTask);//"课堂作业";

        //问答
        sWorkspaceAskAndAnsMyAsk = jsonObject.optString("front.workspace.askandans.myask.par", "我的提问");
        sWorkspaceAskAndAnsMyAnswer = jsonObject.optString("front.workspace.askandans.myanswer.par", "我的回答");
        sWorkspaceAskAndAnsWaitMyAnswer = jsonObject.optString("front.workspace.askandans.waitmyanswer.par", "等我来答");

        //校园电视台
        sWorkspaceTvProgramLive = jsonObject.optString("front.workspace.tvProgram.live.stu", "直播");
        sWorkspaceTvProgramReplay = jsonObject.optString("front.workspace.tvProgram.replay.stu", "往期视频");
        sWorkspaceTvProgramProgramList = jsonObject.optString("front.workspace.tvProgram.programlist.stu", "节目表");
    }

    /**
     * 解析开课相关标题
     */
    private static void parseLiveTitles(JSONObject jsonObject) {
        sMasterSchool = jsonObject.optString("front.field.masterschool", "主讲学校");
        sReceiveSchool = jsonObject.optString("front.field.receiveschool", "接收学校");
        sMasterRoom = jsonObject.optString("front.field.masterroom", "主讲教室");
        sReceiveRoom = jsonObject.optString("front.field.receiveroom", "接收教室");
        sMasterTeacher = jsonObject.optString("front.field.masterteacher", "主讲教师");
        sCoachTeacher = jsonObject.optString("front.field.coachteacher", "辅助教师");
        sMaster = jsonObject.optString("front.field.master", "主讲");
        sReceiver = jsonObject.optString("front.field.receive", "接收");
        sInvited = jsonObject.optString("front.field.invited", "受邀");
    }
}
/*
*以下是生成这个类代码的代码，导入jackson了再用。生成成员和其解析代码。
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class TitleJsonParser {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		String titlesStr = "{\n" +
                "\t\"front.workspace.listen.participation\": \"参与的听课\",\n" +
                "\t\"front.workspace.prepare.launch\": \"发起的备课\",\n" +
                "\t\"front.workspace.resource.upload\": \"上传的资源\",\n" +
                "\t\"front.workspace.vidmeet.participation\": \"参与的会议\",\n" +
                "\t\"front.pagetitle.netteach.listen\": \"热门听课\",\n" +
                "\t\"front.workspace.olclass\": \"专递课堂\",\n" +
                "\t\"front.workspace.in.questionlib.share\": \"共享题库\",\n" +
                "\t\"front.workspace.in.base\": \"基础设置\",\n" +
                "\t\"front.homepage.netclass\": \"直录播课堂\",\n" +
                "\t\"front.pagetitle.netteach.hotdisucss\": \"热门评课\",\n" +
                "\t\"front.homepage.netteach\": \"网络教研\",\n" +
                "\t\"front.workspace.questionlib\": \"题库\",\n" +
                "\t\"front.homepage.group.recommend\": \"推荐圈组\",\n" +
                "\t\"front.workspace.onticeannouncement\": \"通知公告\",\n" +
                "\t\"front.pagetitle.netteach.allprepare\": \"集体备课\",\n" +
                "\t\"front.pagetitle.speclass.replay\": \"课程回放\",\n" +
                "\t\"front.workspace.preparelesson\": \"个人备课\",\n" +
                "\t\"front.workspace.group.school\": \"校内圈组\",\n" +
                "\t\"front.workspace.resource.student\": \"学生上报资源\",\n" +
                "\t\"front.workspace.homework\": \"作业\",\n" +
                "\t\"front.workspace.test.class\": \"班级测试\",\n" +
                "\t\"front.workspace.vidmeet\": \"视频会议\",\n" +
                "\t\"front.workspace.onlineedit.test\": \"测试\",\n" +
                "\t\"front.homepage.blog.recommend\": \"推荐博文\",\n" +
                "\t\"front.workspace.olclass.broadcast\": \"远程导播\",\n" +
                "\t\"front.workspace.multimedia\": \"多媒体教室管理\",\n" +
                "\t\"front.workspace.group.self\": \"我的圈组\",\n" +
                "\t\"front.homepage.interact\": \"互动课堂开设情况\",\n" +
                "\t\"front.workspace.base.class\": \"班级管理\",\n" +
                "\t\"front.workspace.test.auto\": \"自主测试\",\n" +
                "\t\"front.workspace.prepare\": \"集体备课\",\n" +
                "\t\"front.workspace.in.questionlib\": \"题库\",\n" +
                "\t\"front.workspace.info.new\": \"新闻\",\n" +
                "\t\"front.workspace.olclass.schedule\": \"课程表\",\n" +
                "\t\"front.workspace.listen.launch\": \"发起的听课\",\n" +
                "\t\"front.workspace.base.schedule\": \"课表管理\",\n" +
                "\t\"front.workspace.in.resource\": \"优课资源\",\n" +
                "\t\"front.workspace.in.onticeannouncement\": \"通知公告\",\n" +
                "\t\"front.pagetitle.netteach.disucss\": \"评课议课\",\n" +
                "\t\"front.workspace.netclass.replay\": \"往期录播\",\n" +
                "\t\"front.workspace.tutor.system\": \"系统设置\",\n" +
                "\t\"front.workspace.schedule.class\": \"班级课表\",\n" +
                "\t\"front.workspace.base.junior\": \"下级管理\",\n" +
                "\t\"front.workspace.prepare.area\": \"管理辖区内备课\",\n" +
                "\t\"front.workspace.multimedia.classroom\": \"教室管理\",\n" +
                "\t\"front.workspace.in.questionlib.collect\": \"收藏的习题\",\n" +
                "\t\"front.workspace.test.subject\": \"真题试卷\",\n" +
                "\t\"front.workspace.tutor.classroom\": \"课堂列表\",\n" +
                "\t\"front.workspace.olclass.replay\": \"往期录播\",\n" +
                "\t\"front.workspace.tutor.test\": \"随堂测验\",\n" +
                "\t\"front.workspace.resource\": \"优课资源\",\n" +
                "\t\"front.workspace.rethink\": \"教学反思\",\n" +
                "\t\"front.workspace.count\": \"统计\",\n" +
                "\t\"front.workspace.in.repairs\": \"报修信息跟踪\",\n" +
                "\t\"front.pagetitle.netclass.hot\": \"热门视频\",\n" +
                "\t\"front.pagetitle.index.info\": \"资讯\",\n" +
                "\t\"front.workspace.disucss.sspack\": \"本校主讲的评课\",\n" +
                "\t\"front.workspace.netclass\": \"直录播课堂\",\n" +
                "\t\"front.pagetitle.netclass.live\": \"正在直播\",\n" +
                "\t\"front.workspace.test.grade\": \"年级统考\",\n" +
                "\t\"front.workspace.onticeannouncement.draft\": \"草稿箱\",\n" +
                "\t\"front.workspace.disucss\": \"评课议课\",\n" +
                "\t\"front.homepage.name\": \"互动学习平台\",\n" +
                "\t\"front.workspace.in.blog\": \"博文\",\n" +
                "\t\"front.pagetitle.info.notice\": \"通知\",\n" +
                "\t\"front.workspace.resource.junior\": \"下级推荐资源\",\n" +
                "\t\"front.workspace.in.info\": \"新闻\",\n" +
                "\t\"front.homepage.blog\": \"博文\",\n" +
                "\t\"front.workspace.homework.self\": \"我的作业\",\n" +
                "\t\"front.workspace.base.student\": \"学生管理\",\n" +
                "\t\"front.workspace.netclass.broadcast\": \"远程导播\",\n" +
                "\t\"front.workspace.in.homework\": \"作业\",\n" +
                "\t\"front.homepage.group.interest\": \"兴趣组\",\n" +
                "\t\"front.workspace.in.questionlib.self\": \"我的习题\",\n" +
                "\t\"front.workspace.tutor\": \"辅导\",\n" +
                "\t\"front.workspace.schedule.personal\": \"个人课表\",\n" +
                "\t\"front.workspace.preparelesson.preparelesson\": \"教案\",\n" +
                "\t\"front.workspace.in.netclass\": \"直录播课堂\",\n" +
                "\t\"front.workspace.olclass.tour\": \"课堂巡视\",\n" +
                "\t\"front.pagetitle.netteach.preparelesson\": \"个人备课\",\n" +
                "\t\"front.workspace.in.disucss\": \"评课议课\",\n" +
                "\t\"front.pagetitle.netclass.replay\": \"课程回放\",\n" +
                "\t\"front.workspace.info.notice\": \"通知\",\n" +
                "\t\"front.workspace.disucss.area\": \"辖区范围内的评课\",\n" +
                "\t\"front.workspace.disucss.launch\": \"发起的评课\",\n" +
                "\t\"front.pagetitle.index.clsresource\": \"优课资源\",\n" +
                "\t\"front.workspace.vidmeet.attend\": \"参加的会议\",\n" +
                "\t\"front.workspace.test.self\": \"我的试卷\",\n" +
                "\t\"front.workspace.olclass.areatable\": \"县区总表\",\n" +
                "\t\"front.workspace.disucss.steach\": \"本校教师的评课\",\n" +
                "\t\"front.workspace.olclass.tschedule\": \"教师课表\",\n" +
                "\t\"front.workspace.olclass.subclass\": \"接收教室课表\",\n" +
                "\t\"front.pagetitle.speclass.live\": \"正在直播\",\n" +
                "\t\"front.workspace.count.class\": \"课堂统计\",\n" +
                "\t\"front.pagetitle.netteach.rethink\": \"教学反思\",\n" +
                "\t\"front.workspace.tutor.listenclass\": \"听课列表\",\n" +
                "\t\"front.workspace.netclass.tour\": \"课堂巡视\",\n" +
                "\t\"front.homepage.clsresource\": \"优课资源\",\n" +
                "\t\"front.workspace.base.school\": \"学校管理\",\n" +
                "\t\"front.pagetitle.netteach.interact\": \"互动听课\",\n" +
                "\t\"front.workspace.tutor.coach\": \"辅导室\",\n" +
                "\t\"front.homepage.companyInfo\": \"Copyright©阔地教育科技有限公司 版权所有\",\n" +
                "\t\"front.workspace.tutor.startclass\": \"开课列表\",\n" +
                "\t\"front.workspace.base.patriarch\": \"家长管理\",\n" +
                "\t\"front.workspace.onlineedit.section\": \"视频切片\",\n" +
                "\t\"front.pagetitle.index.new\": \"新闻\",\n" +
                "\t\"front.workspace.info\": \"新闻\",\n" +
                "\t\"front.workspace.netclass.class\": \"约课列表\",\n" +
                "\t\"front.workspace.in.prepare\": \"集体备课\",\n" +
                "\t\"front.workspace.in.rethink\": \"教学反思\",\n" +
                "\t\"front.workspace.vidmeet.manage\": \"管理会议\",\n" +
                "\t\"front.workspace.base.classroom\": \"教室管理\",\n" +
                "\t\"front.homepage.blog.classify\": \"分类目录\",\n" +
                "\t\"front.homepage.speclass\": \"专递课堂\",\n" +
                "\t\"front.workspace.base\": \"基础设置\",\n" +
                "\t\"front.workspace.onlineedit.label\": \"看点标注\",\n" +
                "\t\"front.workspace.base.user\": \"用户管理\",\n" +
                "\t\"front.pagetitle.info.new\": \"新闻\",\n" +
                "\t\"front.workspace.olclass.task\": \"课堂作业\",\n" +
                "\t\"front.pagetitle.info.announcement\": \"公告\",\n" +
                "\t\"front.workspace.info.draft\": \"草稿箱\",\n" +
                "\t\"front.pagetitle.index.announcement\": \"公告\",\n" +
                "\t\"front.workspace.netclass.task\": \"课堂作业\",\n" +
                "\t\"front.homepage.blog.hot\": \"最热博文\",\n" +
                "\t\"front.homepage.group.teaching\": \"教研组\",\n" +
                "\t\"front.workspace.in.test\": \"测试\",\n" +
                "\t\"front.workspace.in.onlineedit\": \"在线编辑\",\n" +
                "\t\"front.homepage.siteFilingUrl\": \"信息产业部备案管理系统网址<a target=\\\"_blank\\\" href=\\\"http:\\/\\/www.miitbeian.gov.cn\\\">www.miitbeian.gov.cn<\\/a>\",\n" +
                "\t\"front.workspace.homework.read\": \"我的批阅\",\n" +
                "\t\"front.workspace.in.schedule\": \"课程表\",\n" +
                "\t\"front.homepage.group\": \"圈组\",\n" +
                "\t\"front.workspace.netclass.live\": \"实时直播\",\n" +
                "\t\"front.workspace.multimedia.count\": \"开课统计\",\n" +
                "\t\"front.workspace.in.listen\": \"互动听课\",\n" +
                "\t\"front.workspace.resource.teacher\": \"教师推荐资源\",\n" +
                "\t\"front.homepage.sys.title\": \"互动学习平台\",\n" +
                "\t\"front.workspace.member\": \"班级成员\",\n" +
                "\t\"front.workspace.count.resource\": \"资源统计\",\n" +
                "\t\"front.workspace.vidmeet.launch\": \"发起的会议\",\n" +
                "\t\"front.workspace.in.group\": \"圈组\",\n" +
                "\t\"front.workspace.disucss.participation\": \"参与的评课\",\n" +
                "\t\"front.workspace.in.vidmeet\": \"视频会议\",\n" +
                "\t\"front.pagetitle.index.olclass\": \"直播课堂\",\n" +
                "\t\"front.workspace.group.area\": \"辖区内圈组\",\n" +
                "\t\"front.workspace.olclass.live\": \"实时直播\",\n" +
                "\t\"front.homepage.index\": \"首页\",\n" +
                "\t\"front.workspace.onlineedit\": \"在线编辑\",\n" +
                "\t\"front.workspace.olclass.mainclass\": \"主讲教室课表\",\n" +
                "\t\"front.workspace.test.task\": \"测试任务\",\n" +
                "\t\"front.workspace.info.announcement\": \"公告\",\n" +
                "\t\"front.workspace.group\": \"圈组\",\n" +
                "\t\"front.workspace.schedule\": \"课程表\",\n" +
                "\t\"front.pagetitle.index.notice\": \"通知\",\n" +
                "\t\"front.workspace.blog\": \"博文\",\n" +
                "\t\"front.workspace.repairs\": \"报修信息跟踪\",\n" +
                "\t\"front.workspace.in.member\": \"班级成员\",\n" +
                "\t\"front.workspace.preparelesson.prepareimage\": \"学科素材\",\n" +
                "\t\"front.workspace.listen\": \"互动听课\",\n" +
                "\t\"front.workspace.base.teacher\": \"教师管理\",\n" +
                "\t\"front.workspace.disucss.invited\": \"受邀的评课\",\n" +
                "\t\"front.workspace.prepare.participation\": \"参与的备课\",\n" +
                "\t\"front.workspace.in.multimedia\": \"多媒体教室管理\",\n" +
                "\t\"front.workspace.disucss.spack\": \"主讲的评课\",\n" +
                "\t\"front.homepage.group.active\": \"活跃圈组\",\n" +
                "\t\"front.workspace.resource.collect\": \"收藏的资源\",\n" +
                "\t\"front.workspace.listen.manage\": \"管理听课\",\n" +
                "\t\"front.workspace.olclass.mainteacher\": \"主讲教师课表\",\n" +
                "\t\"front.workspace.prepare.manage\": \"管理备课\",\n" +
                "\t\"front.workspace.in.tutor\": \"辅导\",\n" +
                "\t\"front.homepage.info\": \"资讯\",\n" +
                "\t\"front.workspace.base.role\": \"角色管理\",\n" +
                "\t\"front.workspace.in.personal\": \"个人资料\",\n" +
                "\t\"front.workspace.in.olclass\": \"专递课堂\",\n" +
                "\t\"front.homepage.siteFiling\": \"苏B2-20100157-14\",\n" +
                "\t\"front.pagetitle.netteach.hotprepare\": \"热门备课\",\n" +
                "\t\"front.workspace.listen.area\": \"管理辖区内听课\",\n" +
                "\t\"front.workspace.in.preparelesson\": \"个人备课\",\n" +
                "\t\"front.pagetitle.netclass.recommend\": \"推荐视频\",\n" +
                "\t\"front.workspace.test\": \"测试\",\n" +
                "\t\"front.workspace.count.lesson\": \"评课统计\",\n" +
                "\t\"front.workspace.in.count\": \"统计\"\n" +
                "}";
		HashMap<String,String> result =
		        new ObjectMapper().readValue(titlesStr, HashMap.class);
//		File file = new File("haha.txt");
//		file.createNewFile();
//		PrintWriter pw = new PrintWriter(file);
		List<TitleMember> titleMembers = new ArrayList<>(result.size());
		Set<Entry<String, String>> entrySet = result.entrySet();
		for (Entry<String, String> entry: entrySet){
			String key = entry.getKey();
			String value = entry.getValue();

			String[] ss = key.split("\\.");
			String memberName = buildMemberName(ss);

//			pw.write("");
			TitleMember titleMember = new TitleMember();
			titleMember.memberName = memberName;
			titleMember.key = key;
			titleMember.value = value;
			titleMembers.add(titleMember);
		}
		Collections.sort(titleMembers);
		StringBuilder parseStr = new StringBuilder();
		for (TitleMember titleMember: titleMembers) {
			String sm = "public static String " + titleMember.memberName + " = \"" + titleMember.value +"\";\n";
			System.out.print(sm);
			parseStr.append(titleMember.memberName).append(" = jsonObject.optString(\"")
					.append(titleMember.key).append("\", ")
					.append(titleMember.memberName).append(");//")
					.append(titleMember.value).append("\n");
		}
		System.out.println();
		System.out.print(parseStr.toString());
	}

	private static String buildMemberName(String[] ss){
		StringBuilder sb = new StringBuilder("s");
		for (String s: ss) {
			if ("front".equals(s)) continue;
			sb.append(captureName(s));
		}
		return sb.toString();
	}

	public static String captureName(String name) {
		char[] cs = name.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);
	}

	public static class TitleMember implements Comparable<TitleMember>{
		String memberName;
		String key;
		String value;

		TitleMember(){}

		@Override
		public int compareTo(TitleMember o) {
			return this.memberName.compareTo(o.memberName);
		}
	}
}
 */
