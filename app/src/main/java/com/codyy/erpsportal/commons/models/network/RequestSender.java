package com.codyy.erpsportal.commons.models.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.dao.UserInfoDao;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.configs.AppConfig;
import com.codyy.erpsportal.commons.models.tasks.SavePasswordTask;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.LoginActivity;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Constants;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求发送者，自动附带uuid失效验证处理
 * Created by gujiajia on 2015/5/3.
 */
public class RequestSender {

    private final static String TAG = "RequestSender";

    private final static int DEFAULT_TIMEOUT_MS = 15000;

    private RequestQueue mRequestQueue;

    private Context mContext;

    private Handler mHandler;

    public RequestSender(Fragment fragment) {
        this(fragment.getActivity());
    }

    public RequestSender(Context context) {
        mContext = context;
        mRequestQueue = RequestManager.getRequestQueue();
        mHandler = new Handler();
    }

    protected void gotoLoginActivity() {
        LoginActivity.startClearTask(mContext);
    }

    /**
     * 发送一般请求
     *
     * @param requestData
     */
    public boolean sendRequest(final RequestData requestData) {
        if (!NetworkUtils.isConnected()) {
            UIUtils.toast(R.string.please_connect_internet, Toast.LENGTH_SHORT);
            return false;
        }

        requestData.addSendCount();
        requestData.setStartTime();
        Cog.d(TAG, "sendRequest:", requestData.toString());
        Request<JSONObject> request = new NormalPostRequest(requestData.getUrl(), requestData.getParam(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Cog.d(TAG, "+onResponse:" + response);
                        String result = response.optString("result");
                        if ("forbidden".equals(result)) {//登录超时
                            if (requestData.getSendCount() > 1) {//已经尝试登录失败
                                UIUtils.toast(R.string.login_invalid, Toast.LENGTH_SHORT);
                                clearLoginData();
                                gotoLoginActivity();
                            } else {
                                sendLoginRequest(requestData);
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
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "+onErrorResponse:" + error);
                requestData.getErrorListener().onErrorResponse(error);
            }
        });

        int timeOut = requestData.getTimeout();
        if (requestData.isNeedRetry()) {
            setRequestRetryPolicy(request, timeOut > 0? timeOut: DEFAULT_TIMEOUT_MS, 1);
        } else {
            setRequestRetryPolicy(request, timeOut > 0? timeOut: DEFAULT_TIMEOUT_MS * 2, 0);
        }
        //长期最小化后在最大化之后，请求队列为空
        if (mRequestQueue == null) {
            mRequestQueue = RequestManager.getRequestQueue();
        }
        request.setTag(requestData.getTag());
        mRequestQueue.add(request);
        return true;
    }

    /**
     * 发送一般GET请求
     *
     * @param requestData
     */
    public boolean sendGetRequest(final RequestData requestData) {
        if (!NetworkUtils.isConnected()) {
            UIUtils.toast(R.string.please_connect_internet, Toast.LENGTH_SHORT);
            return false;
        }

        requestData.addSendCount();
        requestData.setStartTime();

        String url = requestData.mUrl + requestData.getParams();
        Cog.d(TAG, "sendRequest:" + url);
        Request<JSONObject> request = new NormalGetRequest(url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Cog.d(TAG, "+onResponse:" + response);
                        String result = response.optString("result");
                        if ("forbidden".equals(result)) {//登录超时
                            if (requestData.getSendCount() > 1) {//已经尝试登录失败
                                UIUtils.toast(R.string.login_invalid, Toast.LENGTH_SHORT);
                                clearLoginData();
                                gotoLoginActivity();
                            } else {
                                sendLoginRequest(requestData);
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
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "+onErrorResponse:" + error);
                requestData.getErrorListener().onErrorResponse(error);
            }
        });

        int timeOut = requestData.getTimeout();
        if (requestData.isNeedRetry()) {
            setRequestRetryPolicy(request, timeOut > 0? timeOut: DEFAULT_TIMEOUT_MS, 1);
        } else {
            setRequestRetryPolicy(request, timeOut > 0? timeOut: DEFAULT_TIMEOUT_MS * 2, 0);
        }
        //长期最小化后在最大化之后，请求队列为空
        if (mRequestQueue == null) {
            mRequestQueue = RequestManager.getRequestQueue();
        }
        request.setTag(requestData.getTag());
        mRequestQueue.add(request);
        return true;
    }



    /**
     * 添加一般的请求，无需验证登陆的
     *
     * @param request 请求
     * @param <T>     所请求数据类型
     */
    public <T> void add(Request<T> request) {
        mRequestQueue.add(request);
    }

    private void setRequestRetryPolicy(Request request, int timeoutMs, int retryTimes) {
        request.setRetryPolicy(new DefaultRetryPolicy(timeoutMs, retryTimes, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    /**
     * 取消对应tag的请求
     *
     * @param tag
     */
    public void stop(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    /**
     * 发送登录请求
     *
     * @param requestData
     */
    private void sendLoginRequest(final RequestData requestData) {
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
        NormalPostRequest loginRequest = new NormalPostRequest(URLConfig.LOGIN, data,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
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
                                sendRequest(requestData);
                            } else {
                                gotoLoginActivity();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "sendLoginRequest error:" + error);
                requestData.getErrorListener().onErrorResponse(error);
            }
        });
        loginRequest.setTag(requestData.getTag());
        mRequestQueue.add(loginRequest);
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
        private Object mTag;

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

        public RequestData(String url, Map<String, String> param,
                           Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Object tag) {
            this(url, param, listener, errorListener, true, true);
            this.mTag = tag;
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

        public RequestData(String url, Map<String, String> param, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, boolean needRetry, Object tag) {
            this(url, param, listener, errorListener, true, needRetry);
            this.mTag = tag;
        }

        public RequestData(String url, Map<String, String> param,
                           Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, boolean isToShowLoading) {
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

        public void setListener(Response.Listener<JSONObject> mListener) {
            this.mListener = mListener;
        }

        public Response.ErrorListener getErrorListener() {
            return mErrorListener;
        }

        public void setErrorListener(Response.ErrorListener mErrorListener) {
            this.mErrorListener = mErrorListener;
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

        public Object getTag() {
            return mTag;
        }

        public RequestData setTag(Object tag) {
            this.mTag = tag;
            return this;
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
