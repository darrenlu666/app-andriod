package com.codyy.erpsportal.commons.receivers;

import android.content.Context;
import android.content.SharedPreferences;

import com.codyy.erpsportal.commons.controllers.activities.SettingActivity;


/**
 * Created by caixingming on 2015/6/4.
 *
 *  设置获取状态工具类
 *
 */
public class SettingUtils {


    private SharedPreferences sp;

    public static SettingUtils getInstance(){
//        sp = mContext.getSharedPreferences(SettingActivity.SHARE_PREFERENCE_SETTING, Context.MODE_PRIVATE);
        return SettingUtilsDelegate.instance;
    }


    /**
     * 是否可以下载文件
     * false:
     *      非Wi-Fi网络下,10分钟需要约40M
     *          是否继续观看视频?
     *          否   |   是
     */
    public boolean getCacheState(Context mContext){
        initSharedPreference(mContext);
        return sp.getBoolean(SettingActivity.KEY_ALLOW_CACHE, false);
    }

    /**
     * 是否仅wifi下显示图片
     * @param mContext
     * @return
     */
    public boolean getShowImageWiFiOnly(Context mContext){
        initSharedPreference(mContext);
        return sp.getBoolean(SettingActivity.KEY_IMAGE_WIFI_ONLY, false);
    }

    /**
     * 初始化 settings sharedPreference
     * @param mContext
     */
    private void initSharedPreference(Context mContext) {
        if(sp == null){
            sp = mContext.getSharedPreferences(SettingActivity.SHARE_PREFERENCE_SETTING, Context.MODE_PRIVATE);
        }
    }


    //单列模式
    private static class SettingUtilsDelegate{
        private static final SettingUtils instance = new SettingUtils();
    }

}
