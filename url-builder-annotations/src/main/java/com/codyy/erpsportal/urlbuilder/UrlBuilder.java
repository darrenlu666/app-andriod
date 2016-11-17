package com.codyy.erpsportal.urlbuilder;

import com.codyy.erpsportal.urlbuilder.annotations.UrlSuffix;

import java.lang.reflect.Method;

/**
 * Created by gujiajia on 2016/4/13.
 */
public class UrlBuilder {
    public static void updateUrls() {
        try {
            Class<?> clazz = Class.forName(UrlSuffix.PACKAGE_NAME + "." + UrlSuffix.CLASS_NAME);
            Method method = clazz.getMethod(UrlSuffix.METHOD_NAME);
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
