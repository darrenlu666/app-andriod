package com.codyy.erpsportal.commons.widgets.infinitepager;

import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by gujiajia on 2015/9/17.
 */
public abstract class InfinitePagerAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        if (getItemCount() > 1) {
            return Integer.MAX_VALUE;
        }
        return getItemCount();
    }

    @Override
    public final boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    abstract public int getItemCount();
}
