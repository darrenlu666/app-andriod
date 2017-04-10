package com.codyy.erpsportal.commons.utils;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;

/**
 * Eapplication.getPreference util .
 * Created by poe on 10/04/17.
 */

public class SharedPreferenceUtil {

    /**
     * 清楚一些记录的缓存数据
     * ex: 家长-孩子id
     */
    public static void clearLoginData(){
        SharedPreferences.Editor editor = EApplication.getPreferences().edit();
        editor.putString(ClassRoomContants.SHARE_PREFERENCE_STUDENT_ID,"");
        editor.commit();
    }

    public static void putString(@NonNull String key ,@NonNull  String value ){
        SharedPreferences.Editor editor = EApplication.getPreferences().edit();
        editor.putString(key,value);
        editor.commit();
    }
}
