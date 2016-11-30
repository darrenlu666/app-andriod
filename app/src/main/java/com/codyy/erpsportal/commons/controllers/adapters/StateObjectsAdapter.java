package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;

import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;

import java.util.List;

/**
 * 包含状态的实体数据适配器
 * Created by gujiajia on 2015/9/3.
 */
public class StateObjectsAdapter<T, VH extends StateObjectsAdapter.StateViewHolder<T>> extends ObjectsAdapter<T, VH>  {

    private boolean mIsSelectMode;

    private OnSelectedChangedListener mOnSelectedChangedListener;

    public boolean isSelectMode() {
        return mIsSelectMode;
    }

    public void setIsSelectMode(boolean isSelectMode) {
        mIsSelectMode = isSelectMode;
    }

    public StateObjectsAdapter(Context context, Class<VH> clazz, OnSelectedChangedListener listener) {
        super(context, clazz);
        this.mOnSelectedChangedListener = listener;
    }

    @Override
    protected void setDataToView(VH viewHolder, int position) {
        viewHolder.setDataToView(mData, position, mContext, mIsSelectMode, mOnSelectedChangedListener);
    }

    public static abstract class StateViewHolder<OBJ> extends AbsViewHolder<OBJ> {

        /**
         * 不要重写这个
         * @param data 数据
         * @param context 上下文
         */
        @Override
        final public void setDataToView(OBJ data, Context context) { }

        public abstract void setDataToView(OBJ data, Context context,boolean flag,
                                           OnSelectedChangedListener onSelectedChangedListener);

        public void setDataToView(List<OBJ> objects, int position, Context context, boolean flag,
                                  OnSelectedChangedListener onSelectedChangedListener) {
            OBJ object = objects.get(position);
            setDataToView(object, context, flag, onSelectedChangedListener);
        }
    }

    public interface OnSelectedChangedListener {
        void onSelectedChanged();
    }
}
