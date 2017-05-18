package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.commons.controllers.activities.SettingActivity;

/**
 * Created by gujiajia on 2015/6/1.
 */
public class NetworkUtils {

    public static boolean isNetWorkTypeWifi(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                return true;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断网络是否连接
     *
     * @return
     */
    public static boolean isConnected() {
        ConnectivityManager con = (ConnectivityManager) EApplication.instance().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean wifi = wifiNetworkInfo != null && wifiNetworkInfo.isConnectedOrConnecting();
        NetworkInfo internetNetworkInfo = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean internet = internetNetworkInfo != null &&internetNetworkInfo.isConnectedOrConnecting();
        return wifi | internet;
    }

    /**
     * 判断是否可以正常下载,如果在3G下开关关闭则不允许下载
     *
     * @param context
     * @return
     */
    public static boolean isDownloadEnable(Context context) {
        boolean result = true;

        if (!isNetWorkTypeWifi(context)) {
            //2G/3G/4G
            SharedPreferences mSp = context.getSharedPreferences(SettingActivity.SHARE_PREFERENCE_SETTING, Context.MODE_PRIVATE);
            boolean isCache = mSp.getBoolean(SettingActivity.KEY_ALLOW_CACHE, false);

            result = isCache;
        }
        return result;
    }
}
