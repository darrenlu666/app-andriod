package com.codyy.erpsportal.commons.controllers.fragments.filters;

import android.os.Bundle;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;

/**
 * 公共的筛选接口
 * ------------
 * |全部 |北京  |
 * |item1|王府井|
 * |item2|学段  |
 * |item3|学校  |
 * |item4|分类  |
 * -------------
 * Created by poe on 16-4-25.
 */
public interface GroupFilterInterface {
    /**
     * 初始化视图
     */
    void initView();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 选择子item
     * @param position
     * @param fe
     */
    void onLeftSelected(int position , FilterEntity fe);

    /**
     * 选择一级item条件
     * @param position
     * @param fe
     */
    void onRightSelected(int position , FilterEntity fe);

    /**
     * 更新余下的筛选条件
     * @param fe
     */
    void updateNextCondition(FilterEntity fe);

    /**
     * 更新左侧的子条件
     * @param fe
     */
    void updateChildren(FilterEntity fe);

    /**
     * 根据返回结果初始化Area部分区域
     * @param fe
     */
    void refreshOrigin(FilterEntity fe);

    /**
     * 获取最新的返回数据-刷新左边子选项
     * @param fe
     */
    void refreshLeft(FilterEntity fe);

    /**
     * 刷新右侧筛选条件
     * @param fe
     */
    void refreshRight(FilterEntity fe);

    /**
     * 获取筛选结果
     */
    Bundle getFilterData();
}
