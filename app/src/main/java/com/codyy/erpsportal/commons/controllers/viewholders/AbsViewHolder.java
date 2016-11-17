package com.codyy.erpsportal.commons.controllers.viewholders;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * view holder abstract class
 * @author Jia
 *
 * @param <T>
 */
public abstract class AbsViewHolder<T> {

	public AbsViewHolder() {}

    public AbsViewHolder(View view) {
		mapFromView(view);
	}

    /**
     * 获取布局id
     * @return
     */
	public abstract int obtainLayoutId();

	/**
	 * Map views in container view to viewHolder
	 * @param view
	 */
	public abstract void mapFromView(View view);

	/**
	 * Fill data to UI
	 * @param data
	 */
	public abstract void setDataToView(T data, Context context);

	/**
	 * 数据塞到View中
	 * @param position
	 * @param objects
	 * @param context
	 */
	public void setDataToView(List<T> objects, int position, Context context){
		T object = objects.get(position);
		setDataToView(object, context);
	}
}