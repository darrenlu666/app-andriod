package com.codyy.erpsportal.commons.utils;

/**
 * 数字工具
 * Created by gujiajia on 2016/9/23.
 */
public class NumberUtils {

    /**
     * 字符串转为float
     * @param str 字符串
     * @return 转换好的float
     */
    public static float floatOf(String str) {
        float f;
        try {
            f = Float.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            f = 0f;
        }
        return f;
    }

    public static int intOf(String str) {
        int i;
        try {
            i = Integer.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            i = 0;
        }
        return i;
    }
}
