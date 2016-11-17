package com.codyy.erpsportal.commons.exception;


/**
 * 打印日志到本地
 * Created by poe on 16-2-4.
 */
public class LogUtils {

    /**
     * 打印log
     * @param e
     */
    public static void log( Throwable e) {
        if (e != null) {
            CrashHandler.log(e);
        }
    }

    /**
     * 打印一段文字
     * @param e
     */
    public static void log(String e) {
        if (e != null) {
            CrashHandler.log(e);
        }
    }
}
