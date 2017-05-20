package com.codyy.erpsportal.commons.models.engine;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.codyy.erpsportal.BuildConfig;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.UpdateDialog;
import com.codyy.erpsportal.commons.data.source.remote.VersionApi;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 版本检查者
 * Created by gujiajia on 2017/5/19.
 */

public class VersionChecker {

    private final static String TAG = "VersionChecker";

    private static VersionChecker sInstance;

    private VersionCheckerListener mVersionCheckerListener;

    private Disposable mDisposable;

    /**
     * 上次检查更新检查到的版本号
     */
    private String mLastDetectedVersion;

    private VersionChecker() {
    }

    public void setVersionCheckerListener(VersionCheckerListener versionCheckerListener) {
        mVersionCheckerListener = versionCheckerListener;
    }

    public static VersionChecker getInstance() {
        if (sInstance == null) {
            sInstance = new VersionChecker();
        }
        return sInstance;
    }

    /**
     * 检查新版本
     */
    public void checkNewVersion(FragmentActivity activity) {
        final WeakReference<FragmentActivity> actRef = new WeakReference<>(activity);
        VersionApi versionApi = RsGenerator.create(VersionApi.class);
        mDisposable = versionApi.getVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "checkNewVersion response=", response);
                        FragmentActivity activity = actRef.get();
                        if (activity == null) return;//活动已经没了，啥都不用搞了
                        String result = response.optString("result");
                        if ("success".equals(result)){
                            String version = response.optString("version");
                            boolean forceUpdate = "Y".equals(response.optString("upgrade_ind"));
                            if(!TextUtils.isEmpty(version)
                                    && (forceUpdate || !version.equals(mLastDetectedVersion))
                                    && !BuildConfig.VERSION_NAME.equals(version)) {
                                final String url = response.optString("appPhoneUrl");
                                Cog.d(TAG, "checkNewVersion url=", url);
                                if (mVersionCheckerListener != null)
                                    mVersionCheckerListener.onNewVersionDetected();
                                UpdateDialog updateDialog = UpdateDialog.newInstance(
                                        forceUpdate, url);
                                updateDialog.show(activity.getSupportFragmentManager(), "update");
                            }
                            mLastDetectedVersion = version;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.d(TAG, "checkNewVersion error", throwable);
                        throwable.printStackTrace();
                    }
                });
    }

    public void release() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        mDisposable = null;
        mVersionCheckerListener = null;
    }

    public interface VersionCheckerListener {
        void onNewVersionDetected();
    }

}
