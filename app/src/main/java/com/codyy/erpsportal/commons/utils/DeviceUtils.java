package com.codyy.erpsportal.commons.utils;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.codyy.erpsportal.EApplication;

import java.lang.reflect.Field;

/**
 * Created by yangxinwu on 2015/8/3.
 */
public class DeviceUtils {

    /**
     * 获取手机的IMEI
     * @param app
     * @return
     */
    public static String getDeviceIMEI(Application app){
        TelephonyManager tm = (TelephonyManager) app.getSystemService(Context.TELEPHONY_SERVICE);
        if(Build.VERSION.SDK_INT>25){
            return tm.getImei();
        }else{
            return tm.getDeviceId();
        }
    }
    /**
     * 获取设备名如oppol R9s .
     * @return
     */
    public static String getDeviceName(){
        return android.os.Build.BRAND+ Build.MODEL;
    }

    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return EApplication.instance().getResources()
                    .getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static void showSoftKeyboard(View view) {
        ((InputMethodManager) EApplication.instance().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(view,
                InputMethodManager.SHOW_FORCED);
    }

    public static void toogleSoftKeyboard(View view) {
        ((InputMethodManager) EApplication.instance().getSystemService(
                Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public static void hideSoftKeyboard(View view) {
        if (view == null)
            return;
        ((InputMethodManager) EApplication.instance().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }
    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) EApplication.instance().getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                displaymetrics);
        return displaymetrics;
    }
    public static float dpToPixel(float dp) {
        return dp * (getDisplayMetrics().densityDpi / 160F);
    }

}
