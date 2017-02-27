package com.codyy.erpsportal.classroom.models;

/**
 * 专递课堂 常量 （直录播共用）
 * Created by ldh on 2016/7/12.
 */
public class ClassRoomContants {
    public final static String FROM_WHERE_MODEL = "FROM_WHERE";//来自哪个模块
    public final static String FROM_WHERE_LINE = "FROM_LINE";//专递课堂(也叫在线课堂)
    public final static String FROM_WHERE_SCHOOL = "FROM_SCHOOL";//直录播课堂（也叫名校网络课堂）

    public final static String FROM_ONLINE_CLASS = "ONLINE_CLASS";//专递课堂
    public final static String FROM_LIVE = "LIVE";//名校网络课堂

    public final static String TYPE_CUSTOM_LIVE = "type.custom.live";//专递课堂－实时直播
    public final static String TYPE_CUSTOM_RECORD = "type.custom.record";//专递课堂－往期录播
    public final static String TYPE_LIVE_LIVE = "type.live.live";//直录播－实时直播
    public final static String TYPE_LIVE_RECORD = "type.live.record";//直录播－往期录播

    public static final String EXTRA_SCHOOL_ID = "schoolId";
    public static final String EXTRA_SCHEDULE_DETAIL_ID = "scheduleDetailId";

    public static final String EXTRA_LIVE_STATUS = "live.status";
    public static final String EXTRA_LIVE_SUBJECT = "live.custom.subject";//自定义学科
}
