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
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.adapters.SkeletonAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.SkeletonAdapter.OnFleshStabbedListener;
import com.codyy.erpsportal.commons.controllers.viewholders.EmptyFleshVhr;
import com.codyy.erpsportal.commons.controllers.viewholders.HistoryCourseFleshBigVhr;
import com.codyy.erpsportal.commons.controllers.viewholders.HistoryCourseFleshVhr;
import com.codyy.erpsportal.commons.controllers.viewholders.RecentCourseFleshVhr;
import com.codyy.erpsportal.commons.controllers.viewholders.SubtitleFleshVhr;
import com.codyy.erpsportal.commons.controllers.viewholders.VhrFactory;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.EmptyFlesh;
import com.codyy.erpsportal.commons.models.entities.Flesh;
import com.codyy.erpsportal.commons.models.entities.HistoryCourseBigFlesh;
import com.codyy.erpsportal.commons.models.entities.HistoryCourseFlesh;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.RecentCourseFlesh;
import com.codyy.erpsportal.commons.models.entities.SubtitleFlesh;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResClassroom;
import com.codyy.erpsportal.commons.models.listeners.MainLiveClickListener;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 海宁直播课堂
 */
public class HaiNingCustomizedFragment extends Fragment{

    private final static String TAG = "HaiNingCustomizedFragment";

    private View mRootView;

    private RefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    private EmptyView mEmptyView;

    private SkeletonAdapter mAdapter;

    private WebApi mWebApi;

    private CompositeDisposable mCompositeDisposable;

    private OnFleshStabbedListener mOnFleshStabbedListener = new OnFleshStabbedListener() {
        @Override
        public void onStabbed(Flesh flesh) {
            if (flesh instanceof RecentCourseFlesh) {
                RecentCourseFlesh recentCourseFlesh = (RecentCourseFlesh) flesh;
                MainResClassroom room = new MainResClassroom();
                room.setId(recentCourseFlesh.getId());
                room.setType(MainResClassroom.TYPE_ONLINE_CLASS);
                room.setStatus(recentCourseFlesh.getStatus());
                room.setSubjectName(recentCourseFlesh.getSubjectName());
                new MainLiveClickListener(HaiNingCustomizedFragment.this, UserInfoKeeper.obtainUserInfo())
                        .onLiveClassroomClick(room);
            } else if (flesh instanceof HistoryCourseFlesh) {
                HistoryCourseFlesh historyCourseFlesh = (HistoryCourseFlesh) flesh;
                ClassRoomDetailActivity.startActivity(getActivity(),
                        UserInfoKeeper.obtainUserInfo(),
                        historyCourseFlesh.getId(),
                        ClassRoomContants.TYPE_CUSTOM_RECORD,
                        historyCourseFlesh.getSubjectName());
            }
        }
    };

    public HaiNingCustomizedFragment() { }

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
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.dot_gray));
            VhrFactory vhrFactory = new VhrFactory();
            vhrFactory.addViewHolder(SubtitleFleshVhr.class);
            vhrFactory.addViewHolder(RecentCourseFleshVhr.class);
            vhrFactory.addViewHolder(EmptyFleshVhr.class);
            vhrFactory.addViewHolder(HistoryCourseFleshVhr.class);
            vhrFactory.addViewHolder(HistoryCourseFleshBigVhr.class);
            mAdapter = new SkeletonAdapter(vhrFactory);
            mAdapter.setOnFleshStabbedListener(mOnFleshStabbedListener);
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
        Observable.just(1);
        List<Observable<JSONObject>> observables = new ArrayList<>();
        observables.add(loadLatestLesson(baseAreaId, schoolId));
//        observables.add(loadLatestLesson("", ""));
        observables.add(loadHistoryCourses(baseAreaId, schoolId));
        Observable.concat(observables)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Cog.d(TAG, "concat doOnSubscribe:", Thread.currentThread());
                        mCompositeDisposable.add(disposable);
                        mAdapter.clearItems();
                        onLoadingStart();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.d(TAG, "concat doOnError:", Thread.currentThread());
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Cog.d(TAG, "concat doOnComplete:", Thread.currentThread());
                        mRefreshLayout.setRefreshing(false);
                        onLoadingFinish();
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Cog.d(TAG, "concat subscribe:", Thread.currentThread());
                    }
                });
    }

    /**
     * 获取近期课程
     * @param areaId 地区id
     * @param schoolId 学校id
     */
    private Observable<JSONObject> loadLatestLesson(String areaId, String schoolId) {
        Map<String, String> map = new HashMap<>();
        map.put("baseAreaId", areaId);
        if (!TextUtils.isEmpty(schoolId)) {
            map.put("schoolId", schoolId);
        }
        map.put("size", "4");
        map.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());

        Cog.d(TAG, "@loadData url=" + URLConfig.GET_SIP_RECENT_LESSON + map);
        return mWebApi.post4Json(URLConfig.GET_SIP_RECENT_LESSON, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mCompositeDisposable.add(disposable);
                        SubtitleFlesh subtitleFlesh = new SubtitleFlesh();
                        subtitleFlesh.setName(Titles.sPagetitleIndexSipRecentClass);
                        mAdapter.addItem(subtitleFlesh);
                    }
                })
                .doOnNext(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "onResponse response=" + response);
                        String result = response.optString("result");
                        if ("success".equals(result)) {
                            List<RecentCourseFlesh> recentCourseFleshes = new Gson()
                                    .fromJson(response.optString("data"), new TypeToken<List<RecentCourseFlesh>>(){}.getType());
                            if (recentCourseFleshes != null && recentCourseFleshes.size() != 0) {
                                mAdapter.addItems(recentCourseFleshes);
                            } else {
                                addEmptyItem();
                            }
                        } else {
                            addEmptyItem();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "onErrorResponse error=", error);
                        mAdapter.addItem(new EmptyFlesh());
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 课程回放
     * @param areaId 地区id
     * @param schoolId 学校id
     */
    private Observable<JSONObject> loadHistoryCourses(String areaId, String schoolId) {
        Map<String, String> map = new HashMap<>();
        map.put("baseAreaId", areaId);
        if (!TextUtils.isEmpty(schoolId)) {
            map.put("schoolId", schoolId);
        }
        map.put("size", "5");
        map.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());

        Cog.d(TAG, "@loadData url=" + URLConfig.GET_RECOMMEND_SCHEDULE + map);
        return mWebApi.post4Json(URLConfig.GET_RECOMMEND_SCHEDULE, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mCompositeDisposable.add(disposable);
                        SubtitleFlesh subtitleFlesh = new SubtitleFlesh();
                        subtitleFlesh.setName(Titles.sPagetitleSpeclassReplay);
                        mAdapter.addItem(subtitleFlesh);
                    }
                })
                .doOnNext(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "onResponse response=" + response);
                        String result = response.optString("result");
                        if ("success".equals(result)) {
                            List<HistoryCourseFlesh> historyCourseFleshes = new Gson()
                                    .fromJson(response.optString("data"), new TypeToken<List<HistoryCourseFlesh>>(){}.getType());
                            if (historyCourseFleshes != null && historyCourseFleshes.size() != 0) {
                                if (historyCourseFleshes.size() % 2 == 0) {
                                    mAdapter.addItems(historyCourseFleshes);
                                } else {
                                    HistoryCourseFlesh historyCourseFlesh = historyCourseFleshes.remove(0);
                                    mAdapter.addItem(new HistoryCourseBigFlesh(historyCourseFlesh));
                                    mAdapter.addItems(historyCourseFleshes);
                                }
                            } else {
                                addEmptyItem();
                            }
                        } else {
                            addEmptyItem();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "onErrorResponse error=", error);
                        mAdapter.addItem(new EmptyFlesh());
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void addEmptyItem() {
        mAdapter.addItem(new EmptyFlesh());
    }

    Observable<JSONObject> testSleep(int i) {
        return Observable.just(i)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, JSONObject>() {
                    @Override
                    public JSONObject apply(Integer s) throws Exception {
                        String jsonStr = String.format(Locale.getDefault(), "{'name':'bb%d'}", s);
                        TimeUnit.SECONDS.sleep(3);
                        return new JSONObject(jsonStr);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        SubtitleFlesh subtitleFlesh = new SubtitleFlesh();
                        subtitleFlesh.setName(jsonObject.optString("name"));
                        mAdapter.addItem(subtitleFlesh);
                        mAdapter.notifyDataSetChanged();
                    }
                });
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
