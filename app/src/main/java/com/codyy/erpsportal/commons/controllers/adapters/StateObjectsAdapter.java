package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;

import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;

import java.util.List;

/**
 *
 * Created by gujiajia on 2015/9/3.
 */
public class StateObjectsAdapter<T, VH extends StateObjectsAdapter.StateViewHolder<T>> extends ObjectsAdapter<T, VH>  {

    private boolean mIsSelectMode;

    public boolean isSelectMode() {
        return mIsSelectMode;
    }

    public void setIsSelectMode(boolean isSelectMode) {
        mIsSelectMode = isSelectMode;
    }

    public StateObjectsAdapter(Context context, Class<VH> clazz) {
        super(context, clazz);
    }

    public StateObjectsAdapter(Context context, ViewHolderBuilder<VH> builder) {
        super(context, builder);
    }

    @Override
    protected void setDataToView(VH viewHolder, int position) {
        viewHolder.setDataToView(mData, position, mContext, mIsSelectMode);
    }

    public static abstract class StateViewHolder<OBJ> extends AbsViewHolder<OBJ> {

        /**
         * 不要重写这个
         * @param data
         * @param context
         */
        @Override
        final public void setDataToView(OBJ data, Context context) { }

        public abstract void setDataToView(OBJ data, Context context,boolean flag);

        public void setDataToView(List<OBJ> objects, int position, Context context, boolean flag) {
            OBJ object = objects.get(position);
            setDataToView(object, context, flag);
        }
    }
}
