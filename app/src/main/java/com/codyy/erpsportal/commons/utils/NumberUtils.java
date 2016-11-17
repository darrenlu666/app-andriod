package com.codyy.erpsportal.commons.utils;

/**
 * Created by gujiajia on 2016/9/23.
 */
public class NumberUtils {

    public static float floatOf(String str) {
        float f;
        try {
            f = Float.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            f = 0;
        }
        return f;
    }

}
