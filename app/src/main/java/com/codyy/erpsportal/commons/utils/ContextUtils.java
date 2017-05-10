package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

/**
 * 上下文工具
 * Created by gujiajia on 2017/3/8.
 */

public class ContextUtils {

    public static Activity getActivity(View view) {
        Context context = view.getContext();
        return getActivity(context);
    }

    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}
