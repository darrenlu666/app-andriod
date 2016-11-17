package com.codyy.erpsportal.commons.models.engine;

import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by gujiajia on 2016/8/19.
 */
public class ItemFillerUtil {
    public static <T> void addItems(LinearLayout container, View titleView, View emptyView, List<T> list, ViewStuffer<T> viewStuffer){
        new MainItemFiller<>(container, titleView, emptyView, list, viewStuffer).doStuffWork();
    }
}
