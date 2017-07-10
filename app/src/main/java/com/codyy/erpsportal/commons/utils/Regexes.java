package com.codyy.erpsportal.commons.utils;

/**
 * 正则表达式们
 * Created by gujiajia on 2017/1/4.
 */

public class Regexes {
    public final static String EMAIL_REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";

    public final static String PHONE_REGEX = "^((1[3|4|5|7|8]\\d{9})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)$";

    public final static String USERNAME_REGEX = "^[\\w,.;~!@#$%^&*()+\\-=\\\\/<>]{5,30}$";

    public final static String PASSWORD_REGEX = "^[\\w,.;~!@#$%^&*()+\\-=\\\\/<>]{6,18}$";
}
