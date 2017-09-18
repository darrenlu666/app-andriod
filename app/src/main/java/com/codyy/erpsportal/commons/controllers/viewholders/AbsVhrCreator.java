package com.codyy.erpsportal.commons.controllers.viewholders;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 视图保持器创建者
 * Created by gujiajia on 2015/12/21.
 */
public abstract class AbsVhrCreator<VH extends ViewHolder> {

    final public VH createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(obtainLayoutId(), parent, false);
        return doCreate(view);
    }

    /**
     * 返回视图保持器ViewHolder对应要显示数据的布局的layoutId。
     * @return
     */
    abstract protected @LayoutRes int obtainLayoutId();

    /**
     * 执行创建视图保持器ViewHolder回调，一般就写new XxxViewHolder(view)就可。
     * @param view
     * @return
     */
    abstract protected VH doCreate(View view);
}
