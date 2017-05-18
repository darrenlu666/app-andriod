package com.codyy.erpsportal;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;

import com.codyy.bennu.framework.BNAVFramework;
import com.codyy.erpsportal.commons.exception.CrashHandler;
import com.codyy.erpsportal.commons.models.entities.configs.ConfigurationManager;
import com.codyy.erpsportal.commons.services.Engine;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.url.URLConfig;
import com.codyy.widgets.imagepipeline.ImagePipelineConfigFactory;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by poe on 15-7-20.
 */
public class EApplication extends MultiDexApplication {
    private static String TAG = "EApplication";
    public final static String ACTION_BENNU_INIT = "com.codyy.remotedirector.action.bennuInit";
    private static final String KEY_SOFT_KEYBOARD_HEIGHT = "KEY_SOFTKEYBOARD_HEIGHT";
    private static final String PREF_NAME = "ErpsPortal.pref";
    private static EApplication mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        init();
        initUrlConfigBase();
    }

    public static EApplication instance() {
        return mApp;
    }

    /**
     * Intialize the request manager and the image cache
     */
    private void init() {
        CrashHandler.getInstance().init(this);
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));
        initConfig();
        initBnVideoFramework();
    }

    /**
     * 初始化配置文件
     */
    private void initConfig() {
        ConfigurationManager.create(instance());
        Engine.create(instance());//启动下载监听服务
    }

    private void initBnVideoFramework() {
        new InitBnVideoFramework().execute();
    }

    private class InitBnVideoFramework extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                BNAVFramework.init(getApplicationContext());
            } catch (Exception e) {
                Cog.e(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(EApplication.this);
            Intent intent = new Intent(ACTION_BENNU_INIT);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    public static void setSoftKeyboardHeight(int height) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(KEY_SOFT_KEYBOARD_HEIGHT, height);
        editor.apply();
    }

    public static SharedPreferences getPreferences() {
        return mApp.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
    }

    public static int getSoftKeyboardHeight() {
        return getPreferences().getInt(KEY_SOFT_KEYBOARD_HEIGHT, 0);
    }

    public void initUrlConfigBase() {
        SharedPreferences sharedPreferences = getSharedPreferences("url", MODE_PRIVATE);
        String base = sharedPreferences.getString("base", null);
        if (base != null && !URLConfig.BASE.equals(base)) {
            URLConfig.updateUrls(base);
        }
    }
}
