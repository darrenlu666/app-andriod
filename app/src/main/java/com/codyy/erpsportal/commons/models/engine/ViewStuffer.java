package com.codyy.erpsportal.commons.models.engine;

import android.view.View;

/**
 * 视图填充者
 * @param <I>
 */
public interface ViewStuffer<I>{
    void onStuffView(View view, I item);
}
