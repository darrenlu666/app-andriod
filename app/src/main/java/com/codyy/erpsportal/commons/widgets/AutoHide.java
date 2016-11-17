package com.codyy.erpsportal.commons.widgets;

/**
 * Created by caixingming on 2015/5/8.
 *
 * 自动隐藏设置
 *
 */
public interface AutoHide {

    /**
     * 隐藏
     */
    void hideControl();

    /**
     * 显示
     */
    void showControl();

    /**
     * 刷新显示时间
     */
    void touchControl();

    /**
     * onDestroy时候销毁
     */
    void destroyView();

}
