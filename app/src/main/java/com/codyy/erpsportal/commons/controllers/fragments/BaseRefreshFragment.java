package com.codyy.erpsportal.commons.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.models.entities.RefreshEntity;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshRecycleView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kmdai on 15-12-21.
 */
public abstract class BaseRefreshFragment<T extends RefreshEntity> extends Fragment implements RefreshRecycleView.OnStateChangeLstener {
    /**
     * 下拉刷新
     */
    public static final int STATE_ON_DOWN_REFRESH = 0x0c01;
    /**
     * 上拉刷新
     */
    public static final int STATE_ON_UP_REFRESH = 0x0c02;
    private RequestSender mRequestSender;
    protected RefreshRecycleView mRefreshRecycleView;
    protected TextView mEmptyView;
    private View mRootView;
    protected List<T> mDatas;
    protected RefreshBaseAdapter<T> mRefreshBaseAdapter;
    /**
     * 列表请求的url
     */
    private String mURL = null;
    protected LinearLayoutManager mLinearLayoutManager;
    private int mLastVisibleNB;
    private Integer mHashTag = this.hashCode();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestSender = new RequestSender(getActivity());
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mDatas = new ArrayList<>();
        mRefreshBaseAdapter = getAdapter(mDatas);
    }

    public void setEnable(boolean enable) {
        mRefreshRecycleView.setEnabled(enable);
    }

    public abstract void loadData();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_refresh_base, container, false);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRefreshRecycleView = (RefreshRecycleView) view.findViewById(R.id.fragment_refresh_refreshview);
        mRefreshRecycleView.setOnStateChangeLstener(this);
        mEmptyView = (TextView) view.findViewById(R.id.fragment_refresh_emptyview);
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        if (mRefreshRecycleView.getAdapter() == null) {
            mRefreshRecycleView.setAdapter(mRefreshBaseAdapter);
        }
        mRefreshRecycleView.setLayoutManager(mLinearLayoutManager);
        mRefreshRecycleView.setColorSchemeColors(getResources().getColor(R.color.refresh_scheme_main), getResources().getColor(R.color.refresh_scheme_pink), getResources().getColor(R.color.refresh_scheme_blue));
        emptyViewState();
        loadData();
    }

    protected void emptyViewState() {
        if (getView() != null) {
            getView().post(new Runnable() {
                @Override
                public void run() {
                    mRefreshRecycleView.setRefreshing(false);
                }
            });
        }
        if (mDatas == null || mDatas.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    protected void httpConnect(String url, Map<String, String> param, final int msg) {
        mRequestSender.sendRequest(new RequestSender.RequestData(url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isRemoving()) {
                    return;
                }
                if (!onRequestSuccess(response, msg)) {
                    switch (msg) {
                        case STATE_ON_DOWN_REFRESH:
                            List<T> datas = getDataOnJSON(response);
                            mRefreshBaseAdapter.clearDatas();
                            mRefreshRecycleView.setRefreshing(false);
                            if (mRefreshBaseAdapter != null && datas != null) {
                                mRefreshBaseAdapter.addListDatas(datas);
                            }
                            if (hasData()) {
                                mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_UP_LOADEMORE);
                            } else {
                                mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
                            }
//                            mRefreshRecycleView.setLastVisibility();
                            break;
                        case STATE_ON_UP_REFRESH:
                            List<T> data = getDataOnJSON(response);
                            if (data != null && data.size() == 0) {
                                mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
                            } else if (mRefreshBaseAdapter != null) {
                                mRefreshBaseAdapter.addListDatas(data);
                                if (hasData()) {
                                    mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_UP_LOADEMORE);
                                } else {
                                    mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
                                }
                            }
                            break;
                    }
                    if (mLastVisibleNB > mDatas.size()) {
                        mRefreshRecycleView.setLastVisibility(false);
                    } else {
                        mRefreshRecycleView.setLastVisibility(true);
                    }
                    emptyViewState();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isRemoving()) {
                    return;
                }
                onRequestError(error, msg);
                emptyViewState();
                mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_LOADE_ERROR);
            }
        }, mHashTag));
    }


    protected void setLastViewState(boolean flag) {
        mRefreshRecycleView.setLastVisibility(flag);
    }

    @Override
    public void onRefresh() {
        if (mURL != null) {
            mRefreshRecycleView.setRefreshing(true);
            httpConnect(getURL(), getParam(STATE_ON_DOWN_REFRESH), STATE_ON_DOWN_REFRESH);
        } else {
            mRefreshRecycleView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshRecycleView.setRefreshing(false);
                }
            }, 1500);
        }
    }

    @Override
    public boolean onBottom() {
        if (mURL != null && hasData()) {
            mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_LOADING);
            httpConnect(getURL(), getParam(STATE_ON_UP_REFRESH), STATE_ON_UP_REFRESH);
            return true;
        } else {
            mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
            return false;
        }
    }

    protected boolean hasData() {
        return false;
    }

    public String getURL() {
        return mURL;
    }

    public void setURL(String mURL) {
        this.mURL = mURL;
    }

    /**
     * 获取adapter
     *
     * @param data
     * @return
     */
    @NonNull
    abstract public RefreshBaseAdapter<T> getAdapter(List<T> data);

    /**
     * @param object
     * @param msg
     */
    protected boolean onRequestSuccess(JSONObject object, int msg) {
        return false;
    }


    protected void onRequestError(VolleyError error, int msg) {

    }

    /**
     * 返回请求参数
     *
     * @param state STATE_ON_DOWN_REFRESH:下拉
     *              STATE_ON_UP_REFRESH：上拉
     * @return
     */
    @NonNull
    abstract public Map<String, String> getParam(int state);

    /**
     * 解析json，返回数据list
     *
     * @param object
     * @return
     */
    @NonNull
    abstract public List<T> getDataOnJSON(JSONObject object);

    protected void setLastVisibleNB(int number) {
        mLastVisibleNB = number;
    }

    @Override
    public void onDestroy() {
        mRequestSender.stop(mHashTag);
        super.onDestroy();
    }
}
