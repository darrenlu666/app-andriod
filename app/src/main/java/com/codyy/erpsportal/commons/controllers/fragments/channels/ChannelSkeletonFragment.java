/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.fragments.channels;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.SkeletonAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.PastRecordingFleshVhr;
import com.codyy.erpsportal.commons.controllers.viewholders.VhrFactory;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 频道页骨架碎片
 */
public class ChannelSkeletonFragment extends Fragment{

    private final static String TAG = "ChannelSkeletonFragment";

    private View mRootView;

    private RefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    private EmptyView mEmptyView;

    private SkeletonAdapter mAdapter;

    private WebApi mWebApi;

    private CompositeDisposable mCompositeDisposable;

    public ChannelSkeletonFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebApi = RsGenerator.create(WebApi.class);
        mCompositeDisposable = new CompositeDisposable();
        initViews();
        ConfigBus.register(mOnModuleConfigListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
        ConfigBus.unregister(mOnModuleConfigListener);
    }

    private void initViews(){
        if (mRootView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            mRootView = inflater.inflate(R.layout.fragment_resource_intro, null);
            mRefreshLayout = (RefreshLayout) mRootView.findViewById(R.id.rl_resource_intro);
            mRefreshLayout.setOnRefreshListener(mOnRefreshListener);
            mRefreshLayout.setColorSchemeResources(R.color.main_color);

            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
            mEmptyView = (EmptyView) mRootView.findViewById(R.id.empty_view);

            initEmptyView();
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(layoutManager);
            VhrFactory vhrFactory = new VhrFactory();
            vhrFactory.addViewHolder(PastRecordingFleshVhr.class);
            mAdapter = new SkeletonAdapter(vhrFactory);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mRootView;
    }

    private void initEmptyView() {
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                ModuleConfig moduleConfig = ConfigBus.getInstance().getModuleConfig();
                loadData(moduleConfig.getBaseAreaId(), moduleConfig.getSchoolId());
            }
        });
    }

    private void loadData(String baseAreaId, String schoolId) {
        onLoadingStart();
        loadSemesterResData( baseAreaId, schoolId);
    }

    /**
     * 获取以学段分类的资源
     * @param areaId 地区id
     * @param schoolId 学校id
     */
    private void loadSemesterResData(String areaId, String schoolId) {
        Map<String, String> map = new HashMap<>();
        map.put("baseAreaId", areaId);
        if (!TextUtils.isEmpty(schoolId)) {
            map.put("schoolId", schoolId);
        }
//        final long start = System.currentTimeMillis();
        Cog.d(TAG, "@loadData url=" + URLConfig.RESOURCE_INTRO + map);
        Disposable disposable = mWebApi.post4Json(URLConfig.RESOURCE_INTRO, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "onResponse response=" + response);
                        mRefreshLayout.setRefreshing(false);
                        String result = response.optString("result");
                        if ("success".equals(result)) {

                        }
                        mRefreshLayout.setRefreshing(false);
                        onLoadingFinish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "onErrorResponse error=" + error);
                        mRefreshLayout.setRefreshing(false);
                        onLoadingFinish();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void onLoadingStart(){
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    /**
     * 加载完成，有数据显示数据，没有显示没有数据点击刷新
     */
    private void onLoadingFinish() {
        if (mAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mEmptyView.setLoading(false);
        }
    }

    private OnModuleConfigListener mOnModuleConfigListener = new OnModuleConfigListener() {
        @Override
        public void onConfigLoaded(ModuleConfig config) {
            if (mRootView != null) {//判断下视图是否在
                loadData(config.getBaseAreaId(), config.getSchoolId());
            }
        }
    };

    private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            ModuleConfig config = ConfigBus.getInstance().getModuleConfig();
            loadData(config.getBaseAreaId(), config.getSchoolId());
        }
    };
}
