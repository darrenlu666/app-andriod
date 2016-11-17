package com.codyy.erpsportal.commons.utils;

import java.util.regex.Pattern;

/**
 * 判断中文字符
 * Created by eachann on 2016/1/14.
 */
public class CharUtils {
    // 根据Unicode编码完美的判断中文汉字和符号

    private static boolean isChinese(char c) {

        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B

                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS

                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {

            return true;

        }

        return false;

    }

    // 完整的判断中文汉字和符号
    public static boolean isChinese(String strName) {

        char[] ch = strName.toCharArray();

        for (int i = 0; i < ch.length; i++) {

            char c = ch[i];

            if (isChinese(c)) {

                return true;

            }

        }

        return false;

    }


    // 只能判断部分CJK字符（CJK统一汉字）

    public static boolean isChineseByREG(String str) {

        if (str == null) {

            return false;

        }

        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");

        return pattern.matcher(str.trim()).find();

    }

    public static boolean strIsEnglish(String word) {
        boolean sign = true; // 初始化标志为为'true'
        for (int i = 0; i < word.length(); i++) {
            if (!(word.charAt(i) >= 'A' && word.charAt(i) <= 'Z')
                    && !(word.charAt(i) >= 'a' && word.charAt(i) <= 'z')) {
                return false;
            }
        }
        return true;
    }


}
