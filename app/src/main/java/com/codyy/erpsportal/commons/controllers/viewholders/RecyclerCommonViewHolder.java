package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;

import java.util.List;

/**
 * Created by gujiajia on 2016/7/13.
 */
public abstract class RecyclerCommonViewHolder<T, INFO> extends RecyclerViewHolder<T> {
    public RecyclerCommonViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 将数据设置到组件显示，优先实现{@link #setDataToView(Object)}或{@link #setDataToView(Object, int)}
     * @param position 位置
     * @param list 数据实体列表
     */
    public void setDataToView(List<T> list, int position, INFO info) {
        setDataToView(list.get(position), position, info);
    }

    /**
     * 将数据设置到组件显示，优先实现{@link #setDataToView(Object)}
     * @param position 位置
     * @param data 数据实体
     */
    public void setDataToView(T data, int position, INFO info) {
        setDataToView(data, info);
    }

    /**
     *
     * @param data
     */
    public void setDataToView(T data, INFO info){}
}
