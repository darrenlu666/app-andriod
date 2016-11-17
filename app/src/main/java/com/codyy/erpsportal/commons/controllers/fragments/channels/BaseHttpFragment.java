package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.common.EndlessRecyclerOnScrollListener;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;

import org.json.JSONObject;
import java.util.HashMap;
import butterknife.ButterKnife;

/**
 * 1.ButterKnife包裹基础类型
 * 2.对数据请求进行了过程回调封装，增加易读性
 * 3.对View进行缓存
 * Created by poe on 16-1-8.
 */
public abstract class BaseHttpFragment extends Fragment {
    private static final String TAG = "BaseHttpFragment";
    protected boolean mInit = false;//是否初始化
    protected View mRootView;//缓存view，防止FragmentTabHost多次加载视图
    /**
     * 单页加载的数据
     */
    public static int sPageCount = 10;
    /**
     * 网络请求
     */
    private RequestSender mSender;
    public UserInfo mUserInfo ;
    private int mCurrentPageIndex = 1 ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInit    =   true;
        mSender = new RequestSender(getActivity());
        if(null != getArguments()){
            mUserInfo = getArguments().getParcelable(Constants.USER_INFO);
        }
        if(null == mUserInfo){
            mUserInfo   = UserInfoKeeper.getInstance().getUserInfo();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mInit){
            mInit    =   false;
            onViewLoadCompleted();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRootView == null){
            mRootView   =   inflater.inflate(obtainLayoutId(),null);
            ButterKnife.bind(this, mRootView);
        }
        return mRootView;
    }

    /**
     * 布局id
     * @return
     */
    public abstract  int obtainLayoutId();

    /**
     * 获取数据的API.
     * @return
     */
    public abstract String obtainAPI();

    /**
     * 参数拼接
     * @return
     */
    public abstract HashMap<String,String> getParam();

    /**
     * 数据请求返回结果
     * @param response
     */
    public abstract void onSuccess(JSONObject response) throws Exception;

    /**
     * 数据请求错误
     * @param error
     */
    public abstract void onFailure(VolleyError error) throws Exception;

    /**
     * 视图加载完成....执行数据操作
     */
    public void onViewLoadCompleted(){

    }

    /**
     * 请求数据
     */
    public void requestData(){
        requestData(obtainAPI(), getParam(), new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    onSuccess(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.log(e);
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                try {
                    onFailure(error);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.log(e);
                }
            }
        });
    }

    /**
     * 请求GET网络请求 .
     * @param url
     * @param params
     * @param requestListener
     */
    public void requestData(String url , HashMap<String ,String> params , final BaseHttpActivity.IRequest requestListener){
        if(!NetworkUtils.isConnected()){
            ToastUtil.showToast(getString(R.string.net_error));
            return;
        }
        mSender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(null != requestListener){
                    try {
                        requestListener.onRequestSuccess(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.log(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(null != requestListener){
                    requestListener.onRequestFailure(error);
                }
                ToastUtil.showToast(getString(R.string.net_connect_error));
                LogUtils.log(error);
            }
        },TAG));
    }

    /**
     * 设置自动加载更多 默认不会自动加载更多...
     * @param recyclerView
     */
    protected void enableLoadMore(RecyclerView recyclerView){
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int current_page) {
                Cog.d(TAG , "current page index :"+current_page);
                //更新下拉刷新...
                if(current_page != mCurrentPageIndex){
                    mCurrentPageIndex = current_page;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //加载view停留1s ，防止太快闪现!
                            SystemClock.sleep(500);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    requestData();
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 退出应用时调用
     */
    private void stopRequest(){
        if(null != mSender)
            mSender.stop(TAG);
    }

    @Override
    public void onDestroy() {
        stopRequest();
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
