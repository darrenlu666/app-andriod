package com.codyy.erpsportal.homework.utils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * 作业模块工具类
 * Created by ldh on 2016/1/25.
 */
public class WorkUtils {
    public static final String READ_TYPE_TEACHER = "TEACHER";
    public static final String READ_TYPE_STU_SPECIFIED = "STU_SPECIFIED";
    public static final String READ_TYPE_STU_EACH_OTHER = "STU_EACH_OTHER";

    public static SpannableStringBuilder switchStr(int color, int currentIndex, int totalItemCount) {
        String indexStr = (currentIndex + 1) + "/" + totalItemCount;
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(indexStr);
        int index = indexStr.indexOf("/");
        stringBuilder.setSpan(new ForegroundColorSpan(color), 0, index, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }

    public static SpannableStringBuilder switchStr(String percent, int color) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(percent);
        int index = percent.indexOf("/");
        stringBuilder.setSpan(new ForegroundColorSpan(color), 0, index, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }

    public static String parserStuFillAnswer(String answer) {
        String parserString = "";
        if (answer.contains("∷")) {
            String[] answerList = answer.split("∷");
            for (int i = 0; i < answerList.length; i++) {
                parserString = parserString + (parserString.equals("") ? "" : "   、  ") + String.valueOf(i + 1) + "." + answerList[i];
            }
            return parserString;
        } else {
            return answer;
        }
    }

    public static String splitString(List<String> answerList) {
        String answerStr = "";
        for (int i = 0; i < answerList.size(); i++) {
            answerStr = answerStr + answerList.get(i);
        }
        return answerStr;
    }

    public static String getBlankAnswer(Map<Integer, String> answerMap) {
        String returnString = "";
        for (int i = 0; i < answerMap.size(); i++) {
            String answer = answerMap.get(i);
            if (i == 0) {
                returnString = returnString + answer;
            } else {
                returnString = returnString + "∷" + answer;
            }
        }
        return returnString;
    }

    /**
     * 是否是主观题
     *
     * @return
     */
    public static boolean isSubjective(String type) {
        if (type.equals(TaskFragment.TYPE_TEXT) || type.equals(TaskFragment.TYPE_COMPUTING) || type.equals(TaskFragment.TYPE_ASK_ANSWER)) {
            return true;
        }
        return false;
    }

    /**
     * 变色
     *
     * @param text
     * @param color
     * @return
     */
    public static SpannableStringBuilder setSpanString(String text, int color) {
        SpannableStringBuilder newText = new SpannableStringBuilder(text);
        newText.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return newText;
    }

    /**
     * 时间格式化
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date(time));
    }

    /**
     * 当录制的语音小于60秒时使用
     *
     * @param time
     * @return
     */
    public static String formatCommentTime(long time, boolean isCommentAudio) {
        String returnTime = "";
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String audioTime = formatter.format(new Date(time));
        if (isCommentAudio) {
            String secTime = audioTime.substring(6, 8);
            String minTime = audioTime.substring(3, 5);
            if (Integer.valueOf(minTime) > 0)
                returnTime = Integer.valueOf(minTime) + "′";
            return returnTime + Integer.valueOf(secTime) + "″";
        } else {
            return audioTime;
        }
    }

    public static List<String> splitFillAnswer(String answer) {
        String[] strAnswer = answer.split("∷");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < strAnswer.length; i++) {
            list.add(strAnswer[i]);
        }
        if (answer.substring(0, 1).equals("∷")) {
            list.add(0, "");
        }
        if (answer.lastIndexOf("∷") == answer.length() - 1) {
            list.add(list.size(), "");
        }
        return list;
    }

    public static StringBuilder splitFillAnswerToString(String answer) {
        String[] strAnswer = answer.split("∷");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < strAnswer.length; i++) {
            list.add(strAnswer[i]);
        }
        if (answer.substring(0, 1).equals("∷")) {
            list.add(0, "");
        }
        if (answer.lastIndexOf("∷") == answer.length() - 1) {
            list.add(list.size(), "");
        }

        StringBuilder answerStr = new StringBuilder();
        for (int j = 0; j < list.size(); j++) {
            answerStr.append(list.get(j));
        }
        return answerStr;
    }

    /**
     * 获取上传音频的地址
     *
     * @param mUserInfo
     * @return
     */
    public static String getUploadAudioUrl(UserInfo mUserInfo) {
        return mUserInfo.getServerAddress() + "/res/mix/" + mUserInfo.getAreaCode() +
                "/upload.do?printscreen=Y&printscreenType=auto&sourceType=work_rec_stu_que_answer&validateCode=" +
                mUserInfo.getValidateCode() + "&sizeLimit=100";
    }

    public static String roundFloat(String number) {
        if ("".equals(number))
            return "";
        float f = Float.valueOf(number.replace("%",""));
        int num = Math.round(f);
        return String.valueOf(num)+"%";
    }
}
