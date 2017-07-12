package com.codyy.tpmp.filterlibrary.entities.interfaces;

import java.util.List;

/**
 * 一个可插拔的筛选模块
 * Created by poe on 27/04/17.
 */

public interface FilterModuleInterface<T> {

    /**
     * 右侧的item点击
     * 这里采用同步机制
     * @return
     */
    void onItemClick(DataBuilder.BuildListener<List<T>> listener);

    /**
     *  左侧点击后触发一系列操作.
     *  -> 更新父类的selectId(内部) *****
     *   -> 更新其他级别低于当前Module的Id .(外部)
     *   -> jump to next .(外部)
     *   -> builder data . (外部)
     *   -> update current Module .(外部)
     *   -> ...
     * @param data
     * @return
     */
    void onChildrenClick(int position, T data, DataBuilder.BuildListener<List<T>> listener);

    /**
     * 筛选模块的基本类型.
     * {@link #SINGLE } 固定不变的单个Module .
     * {@link #COMPLEX} 会增加新Module的复杂模块 .
     */
    public enum Mode {
        /**
         * 固定不变
         */
        SINGLE,
        /**
         * 可变的部分
         * 举个栗子： 区域用户.
         */
        COMPLEX;
    }
}
