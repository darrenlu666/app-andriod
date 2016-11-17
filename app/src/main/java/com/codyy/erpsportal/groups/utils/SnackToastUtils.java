package com.codyy.erpsportal.groups.utils;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * 对话工具
 * Created by poe on 16-6-16.
 */

public class SnackToastUtils {
    /**
     * length short
     * @param view
     * @param message
     */
    public static void toastShort(@NonNull View view , String message ){
        Snackbar.make(view , message ,Snackbar.LENGTH_SHORT).show();
    }

    /**
     * length long
     * @param view
     * @param message
     */
    public static void toastLong(@NonNull View view , String message ){
        Snackbar.make(view , message ,Snackbar.LENGTH_LONG).show();
    }
}
