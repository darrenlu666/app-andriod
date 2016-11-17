package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by gujiajia on 2016/6/14.
 */
public class BindingRvHolder<T> extends RecyclerViewHolder<T> {
    public BindingRvHolder(View itemView) {
        super(itemView);
    }

    @Override
    public final void mapFromView(View view) {
        ButterKnife.bind(this, view);
    }
}
