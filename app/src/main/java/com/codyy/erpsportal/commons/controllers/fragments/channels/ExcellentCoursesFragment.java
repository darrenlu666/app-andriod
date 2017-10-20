/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.fragments.channels;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.adapters.SkeletonAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.SkeletonAdapter.OnFetchMoreListener;
import com.codyy.erpsportal.commons.controllers.adapters.SkeletonAdapter.OnFleshStabbedListener;
import com.codyy.erpsportal.commons.controllers.viewholders.ExcellentCourseFlesh;
import com.codyy.erpsportal.commons.controllers.viewholders.ExcellentCourseFleshVhr;
import com.codyy.erpsportal.commons.controllers.viewholders.VhrFactory;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Flesh;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 频道页精品课程（海宁定制）碎片
 */
public class ExcellentCoursesFragment extends Fragment{

    private final static String TAG = "ExcellentCoursesFragment";

    private View mRootView;

    private RefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    private EmptyView mEmptyView;

    private SkeletonAdapter mAdapter;

    private WebApi mWebApi;

    private CompositeDisposable mCompositeDisposable;

    private int mStart;

    private static final int LOAD_COUNT = 10;

    public ExcellentCoursesFragment() { }

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
            mRootView = inflater.inflate(R.layout.fragment_skeleton_rv, null);
            mRefreshLayout = (RefreshLayout) mRootView.findViewById(R.id.rl_resource_intro);
            mRefreshLayout.setOnRefreshListener(mOnRefreshListener);
            mRefreshLayout.setColorSchemeResources(R.color.main_color);

            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
            mEmptyView = (EmptyView) mRootView.findViewById(R.id.empty_view);

            initEmptyView();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.dot_gray));
            VhrFactory vhrFactory = new VhrFactory();
            vhrFactory.addViewHolder(ExcellentCourseFleshVhr.class);
            mAdapter = new SkeletonAdapter(vhrFactory, new OnFetchMoreListener() {
                @Override
                public void onLoadMore() {
                    loadExcellentCoursesData(false);
                }
            });
            mAdapter.setOnFleshStabbedListener(new OnFleshStabbedListener() {
                @Override
                public void onStabbed(Flesh flesh) {
                    if (flesh instanceof ExcellentCourseFlesh) {
                        ExcellentCourseFlesh excellentCourse = (ExcellentCourseFlesh) flesh;
                        ClassRoomDetailActivity.startActivity(
                                getActivity(),
                                UserInfoKeeper.obtainUserInfo(),
                                excellentCourse.getScheduleDetailId(),
                                ClassRoomContants.TYPE_CUSTOM_RECORD,
                                excellentCourse.getSubjectName());
                    }
                }
            });
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
                loadData();
            }
        });
    }

    private void loadData() {
        onLoadingStart();
        loadExcellentCoursesData(true);
    }

    /**
     * 获取精品课程数据
     */
    private void loadExcellentCoursesData(final boolean refresh) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + LOAD_COUNT;
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));
        Cog.d(TAG, "@loadData url=" + URLConfig.GET_REPLY_LIST_FOR_HAI_NING + params);
        Disposable disposable = mWebApi.post4Json(URLConfig.GET_REPLY_LIST_FOR_HAI_NING, params)
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "onResponse response=" + response);
                        mAdapter.setFetchingMore(false);
                        setNotRefreshing();
                        String result = response.optString("result");
                        if ("success".equals(result)) {
//                            mAdapter.setFleshes(createPastRecodingFleshes());
//                            List<ExcellentCourseFlesh> list = createExcellentCourseFleshes();
                            List<ExcellentCourseFlesh> list = new Gson()
                                    .fromJson(response.optString("data"), new TypeToken<List<ExcellentCourseFlesh>>(){}.getType());
                            if (list.size() == 0) {
                                if (refresh) {
                                    handleEmpty();//
                                } else {
                                    //删除加载更多项
                                    mAdapter.removeFetching();
                                }
                            } else {
                                if (refresh) {
                                    mEmptyView.setVisibility(View.GONE);
                                    mAdapter.setFleshes(list);
                                    mAdapter.notifyItemRangeInserted(0, list.size());
                                } else {
                                    //删除加载更多项
                                    mAdapter.removeFetching();
                                    mAdapter.addItems(list);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                            //如果已经加载所有，下拉更多关闭
                            if (checkHasMore(response, mAdapter.getItemCount())) {
                                mAdapter.enableFetchMore();
                            } else {
                                mAdapter.disableFetchMore();
                            }
                            mAdapter.notifyDataSetChanged();
                            mStart = mAdapter.getItemCount();
                        }
                        onLoadingFinish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "onErrorResponse error=" + error);
                        if (!refresh) {
                            mAdapter.removeItem(mAdapter.getItemCount() - 1);
                            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        } else {
                            setNotRefreshing();
                        }
                        mAdapter.setFetchingMore(false);
                        if (mAdapter.isEmpty()) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        }

                        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void setNotRefreshing() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    private boolean checkHasMore(JSONObject response, int itemCount) {
        return response.optInt("total") > itemCount;
    }

    /**
     * 处理空情况
     */
    protected void handleEmpty() {
        mAdapter.clearItems();
        mAdapter.notifyDataSetChanged();
        mEmptyView.setVisibility(View.VISIBLE);
    }

    private void onLoadingStart(){
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    private List<ExcellentCourseFlesh> createExcellentCourseFleshes() {
        List<ExcellentCourseFlesh> pastRecordingFleshes = new ArrayList<>(10);
        for (int i=0; i<10; i++) {
            ExcellentCourseFlesh excellentCourseFlesh = new ExcellentCourseFlesh();
            excellentCourseFlesh.setTitle("精品课程" + i);
            excellentCourseFlesh.setSchoolName("第" + i + "小学");
            excellentCourseFlesh.setSubjectName( i + "学科");
            pastRecordingFleshes.add(excellentCourseFlesh);
        }
        return pastRecordingFleshes;
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
                loadData();
            }
        }
    };

    private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData();
        }
    };
}
