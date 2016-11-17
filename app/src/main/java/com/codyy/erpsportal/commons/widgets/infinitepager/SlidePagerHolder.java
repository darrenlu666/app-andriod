package com.codyy.erpsportal.commons.widgets.infinitepager;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by gujiajia on 2016/5/17.
 */
public abstract class SlidePagerHolder<T> {
    protected View container;
    protected SimpleDraweeView iconDv;
    protected TextView titleTv;

    public SlidePagerHolder(View view) {
        container = view;
        iconDv = (SimpleDraweeView) view.findViewById(R.id.res_icon);
        titleTv = (TextView) view.findViewById(R.id.title);
    }

    public abstract void bindView(T obj);
}
