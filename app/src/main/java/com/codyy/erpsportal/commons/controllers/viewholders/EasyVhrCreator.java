package com.codyy.erpsportal.commons.controllers.viewholders;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.utils.Cog;

import java.lang.reflect.Constructor;

/**
 * 配合{@link LayoutId}注解设置布局id的ViewHolder创建器
 * Created by gujiajia on 2016/6/2.
 */
public class EasyVhrCreator<VH extends ViewHolder> extends AbsVhrCreator<VH> {

    private static final String TAG = "EasyVhrCreator";

    private Constructor<VH> mConstructor;

    private int mLayoutId;

    public EasyVhrCreator(Class<VH> vhClass) {
        try {
            mConstructor = vhClass.getConstructor(View.class);
        } catch (NoSuchMethodException e) {
            Cog.e(TAG, "No constructor with argument View!");
            e.printStackTrace();
        }
        if (mConstructor == null) {
            throw new RuntimeException("No constructor with argument View!");
        }
        LayoutId layoutId = vhClass.getAnnotation(LayoutId.class);
        if (layoutId == null) throw new RuntimeException("Please use LayoutId set layout id!");
        mLayoutId = layoutId.value();
    }

    @Override
    protected int obtainLayoutId() {
        return mLayoutId;
    }

    @Override
    protected VH doCreate(View view) {
        VH viewHolder = null;
        try {
            viewHolder = mConstructor.newInstance(view);
        } catch (Exception e) {
            Cog.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return viewHolder;
    }

}
