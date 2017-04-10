package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.BuildConfig;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.ConfirmDownloadDialog;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.models.entities.configs.AppConfig;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Constants;
import com.codyy.erpsportal.commons.utils.SharedPreferenceUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.MyDialog;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.UserInfoDao;
import com.codyy.url.URLConfig;
import org.json.JSONObject;
import java.io.File;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.HashMap;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * obtained by poe 2016-03-29
 * 默认api #退出登录
 */
public class SettingActivity extends BaseHttpActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, ConfirmDownloadDialog.OnCancelUpdateListener, Handler.Callback {

    private final static String TAG = "SettingActivity";

    /**
     * 退出登录开始前
     */
    private static final int MSG_LOGIN_OUT_START = 101;

    /**
     * 退出登录结束后
     */
    private static final int MSG_LOGIN_OUT_END = 102;

    public static final String SHARE_PREFERENCE_SETTING = "setting";
    public static final String KEY_IMAGE_WIFI_ONLY = "image_wifi_only";
    public static final String KEY_ALLOW_CACHE = "allow_cache";

    @Bind(R.id.btn_clear_cache)
    RelativeLayout mClearCacheBtn;
    @Bind(R.id.cache_size)
    TextView mCacheSizeTv;
    /**
     * show image in wifi only toggle
     */
    @Bind(R.id.show_image)
    ToggleButton mImageTb;
    /**
     * allow caching in mobile network toggle
     */
    @Bind(R.id.allow_cache)
    ToggleButton mCachingTb;

    @Bind(R.id.please_update_to)
    TextView mVersionTv;

    @Bind(R.id.ltCheckNewVersion)
    View mCheckNewVersionLt;

    @Bind(R.id.tv_exit)
    TextView mExitTextView;

    private LoadingDialog mLoadingDialog;
    private LoadingDialog mCheckUpdateDialog;
    private SharedPreferences mSp;
    private Handler mHandler = new Handler(this);


    @Override
    public int obtainLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.LOGOUT;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap hashMap = new HashMap();
        hashMap.put("uuid", mUserInfo.getUuid());
        return hashMap;
    }

    public void init() {
        UiMainUtils.setNavigationTintColor(this, R.color.main_green);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mSp = getSharedPreferences(SHARE_PREFERENCE_SETTING, MODE_PRIVATE);
        mImageTb.setOnCheckedChangeListener(this);
        mCachingTb.setOnCheckedChangeListener(this);
        mClearCacheBtn.setClickable(true);
        mClearCacheBtn.setOnClickListener(this);
        mCheckNewVersionLt.setOnClickListener(this);
        mVersionTv.setText(BuildConfig.VERSION_NAME);
        obtainCacheSize();
        restoreSettingState();
    }

    @Override
    public void onSuccess(JSONObject response ,boolean isRefreshing) {
        if ("success".equals(response.optString("result"))) {
            Toast.makeText(SettingActivity.this, "退出成功!", Toast.LENGTH_SHORT).show();
            goLogin();
        } else {
            Toast.makeText(SettingActivity.this, response.optString("result"), Toast.LENGTH_SHORT).show();
        }
        mHandler.sendEmptyMessage(MSG_LOGIN_OUT_END);
    }

    /**
     * 退出之后清理及跳转到登录界面
     */
    private void goLogin() {
        clearLoginInfo();
        LoginActivity.startClearTask(SettingActivity.this);
        UIUtils.addEnterAnim(SettingActivity.this);
        finish();
        UIUtils.addExitTranAnim(SettingActivity.this);
    }

    @Override
    public void onFailure(VolleyError error) {
        Cog.e(TAG, "onFailure!");
        mHandler.sendEmptyMessage(MSG_LOGIN_OUT_END);
        LogUtils.log(error);
        String message = TextUtils.isEmpty(error.getMessage()) ? getString(R.string.net_error) : error.getMessage();
        Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
        goLogin();
    }

    /**
     * 恢复设置状态
     */
    private void restoreSettingState() {
        boolean imageWifiOnly = mSp.getBoolean(KEY_IMAGE_WIFI_ONLY, false);
        boolean allowCache = mSp.getBoolean(KEY_ALLOW_CACHE, false);
        mImageTb.setChecked(imageWifiOnly);
        mCachingTb.setChecked(allowCache);
    }

    @OnClick(R.id.tv_modify_password)
    void resetPassword() {
        ResetPasswordActivity.start(this);
    }

    @OnClick(R.id.tv_exit)
    void onLogoutClick() {
        MyDialog myDialog = MyDialog.newInstance("确认退出当前账号？", MyDialog.DIALOG_STYLE_TYPE_0, new MyDialog.OnclickListener() {
            @Override
            public void leftClick(MyDialog myDialog) {
                myDialog.dismiss();
            }

            @Override
            public void rightClick(MyDialog myDialog) {
                mHandler.sendEmptyMessage(MSG_LOGIN_OUT_START);
                goLogin();
                myDialog.dismiss();
            }

            @Override
            public void dismiss() { }
        });
        myDialog.getArguments().putString(MyDialog.ARG_EXTRA_RIGHT_BTN_TEXT, "退出");
        myDialog.showAllowStateLoss(getSupportFragmentManager(), "exit");
    }

    private void clearLoginInfo() {
        UserInfoDao.delete(this);
        UserInfoKeeper.getInstance().clearUserInfo();
        //清空应用缓存
        AppConfig.instance().destroy();
        //清空缓存数据.
        SharedPreferenceUtil.clearLoginData();
    }

    /**
     * 执行清除缓存
     */
    private void performCacheClear() {
        DeleteCacheTask deleteCacheTask = new DeleteCacheTask(this);
        deleteCacheTask.execute();
    }

    /**
     * 获取缓存大小
     */
    private void obtainCacheSize() {
        FetchCacheSizeTask fetchCacheSizeTask = new FetchCacheSizeTask(this);
        fetchCacheSizeTask.execute();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.show_image) {
            saveSetting(KEY_IMAGE_WIFI_ONLY, isChecked);
            ImageFetcher.getInstance(this)
                    .updateState(this, isChecked);
        } else if (buttonView.getId() == R.id.allow_cache) {
            saveSetting(KEY_ALLOW_CACHE, isChecked);
        }
    }

    private void saveSetting(String key, boolean value) {
        mSp.edit().putBoolean(key, value).apply();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ltCheckNewVersion) {
            fetchLatestVersion();
        } else if (v.getId() == R.id.btn_clear_cache) {

            if(mCacheSizeTv.getText().toString().contains("0.00")){
                ToastUtil.showSnake("缓存已全部清空",mCacheSizeTv);
            }else{

                //采用统一的弹出框
                MyDialog myDialog = MyDialog.newInstance("确定要清除缓存？", MyDialog.DIALOG_STYLE_TYPE_0, new MyDialog.OnclickListener() {
                    @Override
                    public void leftClick(MyDialog myDialog) {
                        myDialog.dismiss();
                    }

                    @Override
                    public void rightClick(MyDialog myDialog) {
                        performCacheClear();
                        myDialog.dismiss();
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                myDialog.showAllowStateLoss(getSupportFragmentManager() , "cache_clear");
            }
        }
    }

    private void fetchLatestVersion() {
        if (mCheckUpdateDialog == null) {
            mCheckUpdateDialog = LoadingDialog.newInstance(R.string.checking_update);
        }
        mCheckUpdateDialog.show(getSupportFragmentManager(), "check_update");
        final long startTime = System.currentTimeMillis();
        requestData(URLConfig.VERSION, new HashMap<String, String>(),false, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG, "+fetchLatestVersion response:" + response);
                String result = response.optString("result");
                final String version = response.optString("version");
                if ("success".equals(result) && !BuildConfig.VERSION_NAME.equals(version)) {
                    final String url = response.optString("appPhoneUrl");
                    long spendTime = System.currentTimeMillis() - startTime;
                    if (spendTime > 500) {
                        showConfirmUpdateDialog(url, version);
                    } else {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showConfirmUpdateDialog(url, version);
                            }
                        }, spendTime - 500);
                    }
                } else {
                    UIUtils.toast(SettingActivity.this, "当前版本已是最新版本！", Toast.LENGTH_SHORT);
                    mCheckUpdateDialog.dismiss();
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                Cog.d(TAG, "+fetchLatestVersion error:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                dismissCheckUpdateDialog();
            }
        });
    }

    private void dismissCheckUpdateDialog() {
        if (mCheckUpdateDialog != null)
            mCheckUpdateDialog.dismiss();
    }

    private void showConfirmUpdateDialog(String url, String version) {
        dismissCheckUpdateDialog();
        ConfirmDownloadDialog confirmDownloadDialog = ConfirmDownloadDialog.newInstance(url, version);
        confirmDownloadDialog.show(getSupportFragmentManager(), "confirmDownloadDialog");
    }

    @Override
    public void onCancelUpdate() {}

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_LOGIN_OUT_START://请求退出前
                if (null != mExitTextView) {
                    mExitTextView.setEnabled(false);
                }
                break;
            case MSG_LOGIN_OUT_END://请求退出后
                if (null != mExitTextView) {
                    mExitTextView.setEnabled(true);
                }
                break;
        }
        return false;
    }

    /**
     * 获取缓存大小，目前只获取下载的pdf大小
     */
    private static class FetchCacheSizeTask extends AsyncTask<Void, Void, Long> {

        private WeakReference<SettingActivity> mRef;

        FetchCacheSizeTask(SettingActivity activity) {
            mRef = new WeakReference<>(activity);
        }

        @Override
        protected Long doInBackground(Void... params) {
            SettingActivity settingActivity = mRef.get();
            if (settingActivity == null) return 0L;
            return settingActivity.getCacheSize();
        }

        @Override
        protected void onPostExecute(Long size) {
            super.onPostExecute(size);
            SettingActivity settingActivity = mRef.get();
            if (settingActivity != null)
                settingActivity.updateCacheSize(size);
        }
    }

    /**
     * 更新缓存大小
     */
    private void updateCacheSize(Long size) {
        if (mCacheSizeTv == null) return;
        if (size < 1024 * 1024) {//小于兆显示几k
            double k = (double) size / 1024;
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            String kStr = nf.format(k);
            if("0".equals(kStr)){
                kStr = "0.00" ;
            }
            mCacheSizeTv.setText(String.format("%s KB", kStr));
        } else {
            double m = (double) size / 1024 / 1024;
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            String mStr = nf.format(m);
            mCacheSizeTv.setText(String.format("%s MB", mStr));
        }
    }

    /**
     * 获取缓存大小
     *
     * @return 缓存大小，单位字节
     */
    private Long getCacheSize() {
        File internalCacheDir = getCacheDir();
        File resDocCacheDir = new File(getExternalCacheDir(), Constants.FOLDER_DOC_CACHE);
        return getFileSize(internalCacheDir) + getFileSize( resDocCacheDir);
    }

    public long getFileSize(File f) {
        long size = 0L;
        File[] fileArr = f.listFiles();
        if (fileArr != null) {
            for (File file : fileArr) {
                if (file.isDirectory()) {
                    size = size + getFileSize(file);
                } else {
                    size = size + file.length();
                }
            }
        }
        return size;
    }

    /**
     * 清除缓存任务
     */
    private static class DeleteCacheTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<SettingActivity> mRef;

        DeleteCacheTask(SettingActivity activity) {
            mRef = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SettingActivity activity = getActivity();
            if (activity != null)
                activity.showLoadingDialog();
        }

        private SettingActivity getActivity() {
            return mRef.get();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            long startTime = System.currentTimeMillis();
            SettingActivity activity = getActivity();
            boolean result = false;
            if (activity != null)
                result = activity.deleteDocCache();
            long spendTime = System.currentTimeMillis() - startTime;
            if (spendTime < 500) {
                try {
                    Thread.sleep(500 - spendTime);//清太快了dialog都看不到了
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Cog.d(TAG, "clear cache result:" + result);
            SettingActivity activity = getActivity();
            if (activity != null) {
                activity.hideLoadingDialog();
                if (result) {
                    activity.setCacheSizeZero();
                }
            }
        }
    }

    /**
     * 删除文档缓存
     */
    private boolean deleteDocCache() {
        boolean result = false;
        File resDocCacheDir = new File(getExternalCacheDir(), Constants.FOLDER_DOC_CACHE);
        deleteDirInternalFiles( resDocCacheDir);
        result = true;

        File cacheInternalDir = getCacheDir();
        if (cacheInternalDir != null) {
            deleteDirInternalFiles( cacheInternalDir);
            result = true;
        }
        return result;
    }

    private boolean deleteFile(File file) {
        deleteDirInternalFiles(file);
        return file.delete();
    }

    private void deleteDirInternalFiles(File file) {
        if (file == null) return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File inFile: files) {
                    deleteFile(inFile);
                }
            }
        }
    }

    /**
     * 显示加载中
     */
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog.newInstance(R.string.clearing_cache);
        }
        mLoadingDialog.show(getSupportFragmentManager(), "loading_dialog");
    }

    /**
     * 取消加载中
     */
    private void hideLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 设置缓存大小为0kb
     */
    private void setCacheSizeZero() {
        mCacheSizeTv.setText("0.00 KB");
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SettingActivity.class);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
