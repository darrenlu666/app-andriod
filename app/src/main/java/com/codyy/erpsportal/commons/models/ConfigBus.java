package com.codyy.erpsportal.commons.models;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.codyy.erpsportal.commons.models.entities.ModuleConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置传输总线
 * Created by gujiajia on 2015/9/1.
 */
public class ConfigBus implements Callback{

    private final static String TAG = "ConfigBus";

    /**
     * 配置加载在注册监听器时之前已经完成，直接发消息给监听器，此时的消息类型
     */
    private final static int MSG_STICKY = 1;

    /**
     * 配置加载在注册监听器时之之后，正常发消息类型
     */
    private final static int MSG_NORMAL = 2;

    private List<OnModuleConfigListener> mListeners = new ArrayList<>();

    private LoadingHandler mLoadingHandler;

    private ModuleConfig mModuleConfig;

    /**
     * 获取配置失败失败
     */
    private boolean mFailed;

    private boolean mLoading;

    private Handler mHandler;

    private ConfigBus() {
        mHandler = new Handler(this);
    }

    private volatile static ConfigBus sInstance;

    public static ConfigBus getInstance() {
        if (sInstance == null) {
            synchronized (ConfigBus.class) {
                if (sInstance == null) {
                    sInstance = new ConfigBus();
                }
            }
        }
        return sInstance;
    }

    public LoadingHandler getLoadingHandler() {
        return mLoadingHandler;
    }

    /**
     * 设置错误处理器，如果获取配置已经失败，则直接回调错误接口
     * @param loadingHandler
     */
    public void setLoadingHandler(LoadingHandler loadingHandler) {
        mLoadingHandler = loadingHandler;
        if (mFailed && mLoadingHandler!= null) {
            mLoadingHandler.onError();
        }
    }

    public static boolean hasConfig() {
        return getInstance().getModuleConfig() != null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_STICKY || msg.what == MSG_NORMAL) {
            Pair<OnModuleConfigListener, ModuleConfig> pair = (Pair<OnModuleConfigListener, ModuleConfig>) msg.obj;
            pair.first.onConfigLoaded(pair.second);
            return true;
        }
        return false;
    }

    /**
     * 配置加载监听器
     */
    public interface OnModuleConfigListener {

        /**
         * 配置加载完成
         * @param config
         */
        void onConfigLoaded(ModuleConfig config);
    }

    public interface LoadingHandler {
        void onLoading();
        void onError();
    }

    public static void register(@NonNull OnModuleConfigListener onModuleConfigListener) {
        getInstance().registerListener(onModuleConfigListener);
    }

    public static void unregister(@NonNull OnModuleConfigListener onModuleConfigListener) {
        getInstance().unregisterListener(onModuleConfigListener);
    }

    public static void setErrorHandler(@NonNull LoadingHandler errorHandler) {
        getInstance().setLoadingHandler(errorHandler);
    }

    /**
     * 清除错误处理器
     */
    public static void clearErrorHandler() {
        getInstance().setLoadingHandler(null);
    }

    /**
     * 注册监听器，如果配置已加载，则直接发送回调监听器到消息队列
     * @param onModuleConfigListener
     */
    private void registerListener(@NonNull final OnModuleConfigListener onModuleConfigListener) {
        if (!mListeners.contains(onModuleConfigListener)) {
            mListeners.add(onModuleConfigListener);
        }
        if (mModuleConfig != null) {
            mHandler.obtainMessage(MSG_STICKY, Pair.create(onModuleConfigListener, mModuleConfig)).sendToTarget();
        }
    }

    private void unregisterListener(@NonNull OnModuleConfigListener onModuleConfigListener) {
        mListeners.remove(onModuleConfigListener);
    }

    private void postLoaded(@NonNull ModuleConfig moduleConfig) {
        mFailed = false;
        mModuleConfig = moduleConfig;
        for (OnModuleConfigListener listener: mListeners) {
            mHandler.obtainMessage(MSG_NORMAL, Pair.create(listener, moduleConfig)).sendToTarget();
        }
    }

    private void callPostError() {
        mFailed = true;
        mLoading = false;
        if (mLoadingHandler != null) {
            mLoadingHandler.onError();
        }
    }

    private void callPostLoading() {
        mLoading = true;
        if (mLoadingHandler != null) {
            mLoadingHandler.onLoading();
        }
    }

    public static void post(@NonNull ModuleConfig moduleConfig) {
        getInstance().postLoaded(moduleConfig);
    }

    public static void postError() {
        getInstance().callPostError();
    }

    public static void postLoading() {
        getInstance().callPostLoading();
    }

    /**
     * 清除配置
     */
    public static void clear() {
        getInstance().clearConfig();
    }

    public ModuleConfig getModuleConfig() {
        return mModuleConfig;
    }

    private void clearConfig() {
        mModuleConfig = null;
    }
}
