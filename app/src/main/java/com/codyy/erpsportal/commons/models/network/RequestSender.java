package com.codyy.erpsportal.commons.models.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.LoginActivity;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.dao.UserInfoDao;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.configs.AppConfig;
import com.codyy.erpsportal.commons.models.tasks.SavePasswordTask;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Constants;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 请求发送者，自动附带uuid失效验证处理
 * Created by gujiajia on 2015/5/3.
 */
public class RequestSender {

    private final static String TAG = "RequestSender";

    private WebApi mWebApi;

    private CompositeDisposable mCompositeDisposable;

    private Context mContext;

    private Handler mHandler;

    public RequestSender(Fragment fragment) {
        this(fragment.getActivity());
    }

    public RequestSender(Context context) {
        mContext = context;
        mWebApi = RsGenerator.create(WebApi.class);
        mHandler = new Handler();
        mCompositeDisposable = new CompositeDisposable();
    }

    protected void gotoLoginActivity() {
        LoginActivity.startClearTask(mContext);
    }

    /**
     * 发送一般请求
     *
     * @param requestData 请求数据
     */
    public boolean sendRequest(final RequestData requestData) {
        if (!NetworkUtils.isConnected()) {
            UIUtils.toast(R.string.please_connect_internet, Toast.LENGTH_SHORT);
            return false;
        }

        requestData.addSendCount();
        requestData.setStartTime();

        Cog.d(TAG, "sendRequest:", requestData.toString());
        Observable<JSONObject> observable = requestData.getParam() != null?
                mWebApi.post4Json(requestData.getUrl(), requestData.getParam()):
                mWebApi.post4Json(requestData.getUrl());
        Disposable disposable =  observeRequest(observable, requestData);
        mCompositeDisposable.add( disposable);
        return true;
    }

    /**
     * 发送一般GET请求
     *
     * @param requestData 请求数据
     */
    public boolean sendGetRequest(final RequestData requestData) {
        if (!NetworkUtils.isConnected()) {
            UIUtils.toast(R.string.please_connect_internet, Toast.LENGTH_SHORT);
            return false;
        }

        requestData.addSendCount();
        requestData.setStartTime();

        String url = requestData.mUrl + (requestData.getParams() == null? "": requestData.getParams());
        Cog.d(TAG, "sendGetRequest:" + url);
        Disposable disposable = observeRequest(mWebApi.getJson(url), requestData);
        mCompositeDisposable.add(disposable);
        return true;
    }

    private Disposable observeRequest(Observable<JSONObject> observable, final RequestData requestData) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(final JSONObject response) throws Exception {
                        Cog.d(TAG, "+onResponse:" + response);
                        String result = response.optString("result");
                        if ("forbidden".equals(result)) {//登录超时
                            if (requestData.getSendCount() > 1) {//已经尝试登录失败
                                UIUtils.toast(R.string.login_invalid, Toast.LENGTH_SHORT);
                                clearLoginData();
                                gotoLoginActivity();
                            } else {
                                sendLoginRequest(requestData, false);
                            }
                        } else {
                            if (requestData.isToShowLoading()) {
                                long spendTime = System.currentTimeMillis() - requestData.getStartTime();
                                if (spendTime > 500) {
                                    requestData.getListener().onResponse(response);
                                } else {
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            requestData.getListener().onResponse(response);
                                        }
                                    }, 500 - spendTime);
                                }
                            } else {
                                requestData.getListener().onResponse(response);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.e(TAG, "+onErrorResponse:" + throwable);
                        requestData.getErrorListener().onErrorResponse(throwable);
                    }
                });
    }

    private void clearLoginData() {
        UserInfoDao.delete(mContext);
        //清空应用缓存
        AppConfig.instance().destroy();
    }

    /**
     * 取消所有请求
     */
    public void stop() {
        mCompositeDisposable.dispose();
    }


    /**
     * 发送登录请求
     *
     * @param requestData
     */
    protected void sendLoginRequest(final RequestData requestData, final boolean isGet) {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.SHARED_KEY_PASSWORD, Context.MODE_PRIVATE);
        final String username = sp.getString("username", null);
        if (username == null) {
            LoginActivity.startClearTask(mContext);
            return;
        }
        final String password = sp.getString("password", null);
        Map<String, String> data = new HashMap<>();
        data.put("userName", username);
        data.put("password", password);
        Disposable disposable = mWebApi.post4Json(URLConfig.LOGIN, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "sendLoginRequest:" + response);
                        String result = response.optString("result");
                        if (!"success".equals(result)) {
                            UIUtils.toast(R.string.login_invalid, Toast.LENGTH_SHORT);
                            clearLoginData();
                            gotoLoginActivity();
                        } else {
                            //更新UUID
                            updateLoginInfo(response);
                            //保存账号密码
                            new SavePasswordTask(mContext).execute(username, password);
                            String uuid = response.optString("uuid");
                            if (uuid != null) {
                                requestData.getParam().put("uuid", uuid);
                                if (isGet) {
                                    sendGetRequest(requestData);
                                } else {
                                    sendRequest(requestData);
                                }
                            } else {
                                gotoLoginActivity();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.e(TAG, "sendLoginRequest error:" + throwable);
                        requestData.getErrorListener().onErrorResponse(throwable);
                    }
                });
        mCompositeDisposable.add( disposable);
    }

    private void updateLoginInfo(JSONObject response) {
        UserInfo userInfo = UserInfo.parseJson(response);
        UserInfoDao.save(mContext, userInfo);
    }


    /**
     * 请求数据
     */
    public static class RequestData {
        private String mUrl;
        private Map<String, String> mParam;
        private Response.Listener<JSONObject> mListener;
        private Response.ErrorListener mErrorListener;
        private volatile int mSendCount;

        /**
         * 显示内容加载中
         */
        private boolean mIsToShowLoading;
        private long mStartTime;

        /**
         * 请求是否需要重试。
         */
        private boolean mNeedRetry = true;

        private int mTimeout = -1;

        public RequestData(String url, Map<String, String> param,
                           Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            this(url, param, listener, errorListener, true, true);
        }

        public RequestData(String url, Map<String, String> param, Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener, boolean isToShowLoading, boolean needRetry) {
            this.mUrl = url;
            this.mParam = param;
            this.setListener(listener);
            this.setErrorListener(errorListener);
            this.setIsToShowLoading(isToShowLoading);
            this.mNeedRetry = needRetry;
        }

        public RequestData(String url, Map<String, String> param,
                           Response.Listener<JSONObject> listener, Response.ErrorListener errorListener,
                           boolean isToShowLoading) {
            this.mUrl = url;
            this.mParam = param;
            this.setListener(listener);
            this.setErrorListener(errorListener);
            this.setIsToShowLoading(isToShowLoading);
        }

        public void addSendCount() {
            mSendCount++;
        }

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String url) {
            this.mUrl = url;
        }

        public Map<String, String> getParam() {
            return mParam;
        }

        /**
         * 返回GET拼接参数  ？mid=xxxx&uid=xxxx
         *
         * @return
         */
        public String getParams() {
            if (null != mParam && mParam.size() > 0) {
                StringBuilder result = new StringBuilder("?");
                Object[] keys = mParam.keySet().toArray();
                for (int i = 0; i < keys.length; i++) {
                    if (i > 0) {
                        result.append("&");
                    }
                    result.append(keys[i]).append("=").append(mParam.get(keys[i]));
                }
                return result.toString();
            } else {
                return "";
            }
        }

        public void setParam(Map<String, String> param) {
            this.mParam = param;
        }

        public Response.Listener<JSONObject> getListener() {
            return mListener;
        }

        public void setListener(Response.Listener<JSONObject> listener) {
            this.mListener = listener;
        }

        public Response.ErrorListener getErrorListener() {
            return mErrorListener;
        }

        public void setErrorListener(Response.ErrorListener errorListener) {
            this.mErrorListener = errorListener;
        }

        public int getSendCount() {
            return mSendCount;
        }

        public void setSendCount(int sendCount) {
            this.mSendCount = sendCount;
        }

        public long getStartTime() {
            return mStartTime;
        }

        public boolean isToShowLoading() {
            return mIsToShowLoading;
        }

        public void setIsToShowLoading(boolean isToShowLoading) {
            this.mIsToShowLoading = isToShowLoading;
        }

        public void setStartTime() {
            if (this.mStartTime == 0L) {//未设置开始时间时设置请求时间
                this.mStartTime = System.currentTimeMillis();
            }
        }

        public boolean isNeedRetry() {
            return mNeedRetry;
        }

        public void setNeedRetry(boolean needRetry) {
            this.mNeedRetry = needRetry;
        }

        public int getTimeout() {
            return mTimeout;
        }

        public void setTimeout(int timeout) {
            mTimeout = timeout;
        }

        @Override
        public String toString() {
            return (mUrl + '?' + mParam).replace(", ", "&").replace("{", "").replace("}", "");
        }
    }
}
