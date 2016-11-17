package com.codyy.erpsportal.commons.models.entities;

/**
 * 专递课堂
 * Created by kmdai on 2015/8/7.
 */
public class DeliveryBase {
    /**
     * 标题分隔
     */
    public static final int TITLE_DIVIDE = 0x001;
    /**
     * 正在直播
     */
    public static final int SCHEDULE_LIVE = 0x002;
    /**
     * 课程回放
     * RecommendSchedule
     */
    public static final int RECOMMEND_SCHEDULE = 0x003;
    /**
     * 课程回放第一个
     */
    public static final int RECOMMEND_SCHEDULE_FIRST = 0x004;
    /**
     * 分隔view
     */
    public static final int DIVIDE_VIEW = 0x005;
    /**
     * 类型
     */
    public int Type;
    /**
     * 标题
     */
    public String Title;
    /**
     * 位置
     */
    public int position;

    public int spanSize;

    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
