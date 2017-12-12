package com.codyy.erpsportal.resource.utils;

/**
 * 接口相关工具类
 * Created by gujiajia on 2017/12/12.
 */

public class WebUtils {

    public static String toHttp(String url) {
        if (url != null && url.startsWith("https://")){
            return url.replace("https://", "http://");
        } else {
            return url;
        }
    }

}
