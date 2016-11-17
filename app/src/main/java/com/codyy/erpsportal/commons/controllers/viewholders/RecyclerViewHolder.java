package com.codyy.erpsportal.commons.controllers.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * 回收组件保持者
 * Created by gujiajia on 2015/12/21.
 */
public abstract class RecyclerViewHolder<T> extends RecyclerView.ViewHolder {
    public RecyclerViewHolder(View itemView) {
        super(itemView);
        mapFromView(itemView);
    }

    public abstract void mapFromView(View view);

//    /**
//     * 将数据设置到组件显示，附带额外数据
//     * @param list 数据列表
//     * @param position 位置
//     * @param extraObj 额外数据
//     */
//    public <OBJ> void setDataToView(List<T> list, int position, OBJ extraObj) {
//        setDataToView(list, position);
//    }

    /**
     * 将数据设置到组件显示，优先实现{@link #setDataToView(Object)}或{@link #setDataToView(Object, int)}
     * @param position 位置
     * @param list 数据实体列表
     */
    public void setDataToView(List<T> list, int position) {
        setDataToView(list.get(position), position);
    }

    /**
     * 将数据设置到组件显示，优先实现{@link #setDataToView(Object)}
     * @param position 位置
     * @param data 数据实体
     */
    public void setDataToView(T data, int position) {
        setDataToView(data);
    }

    /**
     *
     * @param data
     */
    public void setDataToView(T data){}
}
