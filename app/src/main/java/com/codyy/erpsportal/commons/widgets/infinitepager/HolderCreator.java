package com.codyy.erpsportal.commons.widgets.infinitepager;

import android.view.View;

/**
 * Created by gujiajia on 2016/5/17.
 */
public abstract class HolderCreator<HOLDER extends SlidePagerHolder<?>> {
    public abstract HOLDER create(View view);
}
