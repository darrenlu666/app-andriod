package com.codyy.tpmp.filterlibrary.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * Created by poe on 26/04/17.
 */

public class FilterUtil {
    /**
     * 过滤html符号
     *
     * @param str1
     * @return
     */
    public static String replaceHtml(String str1) {
        return str1.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&");
    }

    /**
     * 获取颜色
     * @param context
     * @param resId
     * @return
     */
    public static int getColor(Context context, int resId){
        return ContextCompat.getColor(context , resId);
    }

    /**
     * 获取资源drawable
     * @param context
     * @param resId
     * @return
     */
    public static Drawable loadDrawable(Context context , int resId){
        return ContextCompat.getDrawable(context , resId);
    }
}
