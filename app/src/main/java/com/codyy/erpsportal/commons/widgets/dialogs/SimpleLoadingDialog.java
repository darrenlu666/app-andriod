package com.codyy.erpsportal.commons.widgets.dialogs;

import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 1. 实现简单的数据加载，返回功能
 * 2. 提供统一的进度条展示
 * Created by poe on 2017/4/10.
 */

public abstract class SimpleLoadingDialog extends DialogFragment {

    private static final String TAG = "SimpleLoadingDialog";
    public static final String ARG_EXTRA_API = "dialog.load.data";

    private View mRootView;
    /**
     * 网络请求
     */
    protected RequestSender mSender;
    protected UserInfo mUserInfo;
    public IResult mResultInterface = null;
    private AnimationDrawable mAnimationDrawable;

    /**
     * 获取数据的API.
     *
     * @return api
     */
    public abstract String obtainAPI();

    /**
     * 参数拼接
     *
     * @return 数据请求parmas
     */
    public abstract HashMap<String, String> getParam();

    /**
     * 数据请求返回结果
     *
     * @param response 数据请求成功返回
     */
    public abstract void onSuccess(JSONObject response) throws Exception;

    /**
     * 数据请求错误
     *
     * @param error 数据请求失败
     */
    public abstract void onFailure(Throwable error) throws Exception;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.input_dialog);
        if (null != getDialog()) {
//            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.copyFrom(window.getAttributes());
            lp.width = UIUtils.dip2px(getContext(), 100);
            lp.height = UIUtils.dip2px(getContext(), 100);
            window.setAttributes(lp);
            window.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.color.transparent));
        }

        if (null != getArguments()) {
            mUserInfo = getArguments().getParcelable(Constants.USER_INFO);
            if (null == mUserInfo) {
                mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
            }
        }
        mSender = new RequestSender(this);
        init();
    }

    /**
     * 初始化before视图加载
     */
    public abstract void init();

    /**
     * 显示fragmentDialog
     *
     * @param fragmentManager
     * @param TAG
     */
    public void showAllowStateLoss(FragmentManager fragmentManager, String TAG) {
        Cog.i(this.TAG, "showAllowStateLoss () ~~" + TAG);
        if (fragmentManager == null) return;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (ft == null) return;
        ft.add(this, TAG);
        ft.commitAllowingStateLoss();
        if (null != mAnimationDrawable) mAnimationDrawable.start();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        Cog.i(TAG, "onDismiss () ~~");
        super.onDismiss(dialog);
        if (null != mAnimationDrawable) mAnimationDrawable.stop();
        stopRequest();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //no title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (null == mRootView) {
            mRootView = inflater.inflate(R.layout.fragment_dialog_simple_loading, container, false);
            ImageView mLoadingImage = (ImageView) mRootView.findViewById(R.id.image_view);
            mAnimationDrawable = (AnimationDrawable) mLoadingImage.getDrawable();
        }

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //do something . data set .
//        mTextView.setText(mTitle);
    }


    public IResult getResultInterface() {
        return mResultInterface;
    }

    public void setResultInterface(IResult mResultInterface) {
        this.mResultInterface = mResultInterface;
    }

    /**
     * 请求数据
     */
    public void requestData() {
        requestData(obtainAPI(), getParam(), false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) {
                try {
                    onSuccess(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.log(e);
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {
                try {
                    onFailure(error);
                    if (null != mResultInterface) mResultInterface.onFailure(error);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.log(e);
                }
            }
        });
    }

    /**
     * 请求GET网络请求 .
     *
     * @param url             request url .
     * @param params          request params .
     * @param requestListener request listener .
     */
    public void requestData(String url, HashMap<String, String> params, final boolean isRefreshing, final BaseHttpActivity.IRequest requestListener) {

        if (!NetworkUtils.isConnected()) {
            ToastUtil.showToast(getString(R.string.net_error));
            requestListener.onRequestFailure(new Exception(getString(R.string.net_error)));
            return;
        }
        if (null == params) {
            Cog.e(TAG, getString(R.string.null_param_error));
            if (null != requestListener) {
                requestListener.onRequestFailure(new Exception(getString(R.string.null_param_error)));
            }
            return;
        }

        mSender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (null != requestListener) {
                    try {
                        requestListener.onRequestSuccess(response, isRefreshing);

                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.log(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                if (null != requestListener) {
                    requestListener.onRequestFailure(error);
                }
                ToastUtil.showToast(getString(R.string.net_connect_error));
                LogUtils.log(error);
            }
        }));
    }

    /**
     * 退出应用时调用
     */
    private void stopRequest() {
        Cog.i(TAG, "stopREquest() ~");
        if (null != mSender)
            mSender.stop();
    }


    public interface IResult {
        /**
         * 请求成功返回结果.
         */
        void onSuccess(String result);

        /**
         * 请求出错.
         *
         * @param error
         */
        void onFailure(Throwable error);
    }
}
