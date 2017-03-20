package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.ScheduleLiveView;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取直播地址
 * Created by kmdai on 2015/9/15.
 */
public class LiveViewUtils {

    final static String TAG = "LiveViewUtils";

    /**
     * 网络请求
     */
    private RequestSender mSender;

    private UserInfo mUserInfo;

    private Context mContext;

    private OnUrlResultListener mOnUrlResultListener;

    /**
     * 需要等待获取的流地址数量
     */
    private int mStreamCount;

    private List<ScheduleLiveView> mScheduleLiveViewList;

    public LiveViewUtils(Context context) {
        mContext = context;
        mSender = new RequestSender((Activity) context);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
    }


    public void setOnUrlResultListener(@NonNull OnUrlResultListener onUrlResultListener) {
        this.mOnUrlResultListener = onUrlResultListener;
    }

    public void playLiveVideo(String id, String liveType) {
        if (mUserInfo != null) {
            HashMap<String, String> data = new HashMap<>();
            data.put("id", id);
            data.put("liveType", liveType);
            data.put("uuid", mUserInfo.getUuid());
            fetchPermission(data);
        } else {
            ToastUtil.showToast(mContext, "请先登录");
        }
    }

    /**
     * 抓取直播权限，查看是否能进入。
     * @param data
     */
    //    String URL = "http://10.1.0.1/dmc?method=play&stream=class_3c699cf489774ba2be70b1851bc78a46_u_d255fb39a4c44820a924cfc00f30e321__main";
    private void fetchPermission(HashMap<String, String> data) {
        mSender.sendRequest(new RequestSender.RequestData(URLConfig.CHECK_PERMISSION, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "fetchPermission", response);
                if ("success".equals(response.optString("result"))) {
                    if ("CAN".equals(response.optString("canVisit"))) {
                        JSONArray jsonArray = response.optJSONArray("scheduleLiveViews");
                        mScheduleLiveViewList = ScheduleLiveView.parseJsonArray(jsonArray);
                        if (mOnUrlResultListener != null) {
                            mOnUrlResultListener.onMainClassroomLoaded(mScheduleLiveViewList.get(0));
                        }
                        if ("DMC".equals(mScheduleLiveViewList.get(0).getStreamingServerType())) {//如果主讲教室的流是dmc的，则接受教室的都是dmc的，所以只需判断一次
                            mStreamCount = mScheduleLiveViewList.size();//设置获取流的课堂数量，每成功获取一个则递减下，直到为0执行回调
                            for (ScheduleLiveView scheduleLiveView: mScheduleLiveViewList) {
                                loadDmcStream(scheduleLiveView);
                            }

                        } else {
                            for (ScheduleLiveView scheduleLiveView: mScheduleLiveViewList) {
                                String url = scheduleLiveView.getServerAddress() + "/class_" + scheduleLiveView.getClassroomId() + "_u_" + scheduleLiveView.getScheduleDetailId() + "__main";
                                scheduleLiveView.setStreamUrl(url);
                            }
                            if (mOnUrlResultListener != null) {
                                mOnUrlResultListener.onStreamFetched(mScheduleLiveViewList);
                            }
                        }

                    } else if ("CANNOT".equals(response.optString("canVisit"))) {
                        String errorCode = response.optString("code");
                        if ("0".equals(errorCode)) {
                            ToastUtil.showToast(mContext, "未登录");
                        } else if ("1".equals(errorCode)) {
                            ToastUtil.showToast(mContext, "家长或学生无权限访问");
                        } else if ("2".equals(errorCode)) {
                            String schoolName = response.optString( "schoolName");
                            ToastUtil.showToast(mContext, "您不是" + schoolName + "的用户，无法观看直播。");
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                ToastUtil.showToast(mContext, mContext.getString(R.string.net_error));
            }
        }));
    }

    /**
     * 请求dmc服务器流地址。
     * @param scheduleLiveView
     */
    private void loadDmcStream(final ScheduleLiveView scheduleLiveView) {
        WebApi webApi = RsGenerator.create(WebApi.class);
        String url = scheduleLiveView.getDmsServerHost() + "?method=play&stream=class_" + scheduleLiveView.getClassroomId() + "_u_" + scheduleLiveView.getScheduleDetailId() + "__main";
        webApi.getJson(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadDmcStream response=", response);
                        String serverAddress = response.optString("result");
                        String streamAddress = serverAddress + "/class_" + scheduleLiveView.getClassroomId() + "_u_" + scheduleLiveView.getScheduleDetailId() + "__main";
                        scheduleLiveView.setStreamUrl(streamAddress);
                        notifyListener();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "loadDmcStream error=", error);
                        ToastUtil.showToast(mContext, mContext.getString(R.string.net_error));
                        notifyListener();
                    }
                });
    }

    /**
     * 如果流地址都获取好了，通知监听器
     */
    private void notifyListener() {
        mStreamCount--;
        if (mStreamCount == 0 && mOnUrlResultListener != null) {
            mOnUrlResultListener.onStreamFetched( mScheduleLiveViewList);
        }
    }

    public interface OnUrlResultListener {
        /**
         * 流地址已获取
         * @param scheduleLiveViews
         */
        void onStreamFetched(List<ScheduleLiveView> scheduleLiveViews);

        /**
         * 主讲课堂信息获取
         * @param scheduleLiveView
         */
        void onMainClassroomLoaded(ScheduleLiveView scheduleLiveView);
    }
}
