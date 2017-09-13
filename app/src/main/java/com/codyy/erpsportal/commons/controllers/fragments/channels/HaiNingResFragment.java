/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.engine.ItemFillerUtil;
import com.codyy.erpsportal.commons.models.engine.ViewStuffer;
import com.codyy.erpsportal.commons.models.entities.MainPageConfig;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.models.parsers.JsonParseUtil;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.resource.controllers.viewholders.VideoItemViewHolder;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 频道页 首页（资源&海宁）
 * Created by gujiajia on 2015/9/11.
 */
public class HaiNingResFragment extends Fragment {

    private final static String TAG = "HaiNingResFragment";

    private View mRootView;

    @Bind(R.id.ll_container)
    LinearLayout mContainerLl;

    @Bind(R.id.rl_main_res)
    RefreshLayout mRefreshLayout;

    @Bind({R.id.fl_title1, R.id.fl_title2, R.id.fl_title3})
    FrameLayout[] mTitleFls;

    @Bind({R.id.tv_title1, R.id.tv_title2, R.id.tv_title3})
    protected TextView[] mTitleTvs;

    @Bind({R.id.gv_resource1, R.id.gv_resource2, R.id.gv_resource3})
    protected GridLayout[] mResourceGvs;

    private WebApi mWebApi;

    private CompositeDisposable mCompositeDisposable;

    private LayoutInflater mInflater;

    /**
     * 正在加载中的任务个数
     */
    private volatile int mOnLoadingCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAttributes();
        initViews();
        ConfigBus.register(mOnModuleConfigListener);
    }

    private void initAttributes() {
        mInflater = LayoutInflater.from(getActivity());
        mWebApi = RsGenerator.create(WebApi.class);
        mCompositeDisposable = new CompositeDisposable();
    }

    private void initViews(){
        if (mRootView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            mRootView = inflater.inflate(R.layout.fragment_hai_ning_res, null);
            ButterKnife.bind(this, mRootView);
            mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ModuleConfig moduleConfig = ConfigBus.getInstance().getModuleConfig();
                    mOnModuleConfigListener.onConfigLoaded(moduleConfig);
                }
            });
            mRefreshLayout.setColorSchemeResources(R.color.main_color);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
        mOnLoadingCount = 0;
        ConfigBus.unregister(mOnModuleConfigListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mRootView;
    }

    private void minusLoadingCount() {
        mOnLoadingCount--;
        if (mOnLoadingCount == 0) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 加载资源数据
     * @param areaId 地区id
     * @param schoolId 学校id
     */
    private void loadMainResources(String areaId, String schoolId) {
        Cog.d(TAG, "loadMainResources areaId=",areaId,",schoolId=",schoolId);
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("baseAreaId", areaId);
        params.put("categorySize", "3");
        params.put("resourceSize", "4");
        mOnLoadingCount++;
        Cog.d(TAG, "Url:", URLConfig.MAIN_RESOURCES, params);
        Disposable disposable = mWebApi.post4Json(URLConfig.MAIN_RESOURCES, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadMainResources response=", response);
                        minusLoadingCount();
                        if ("success".equals(response.optString("result"))) {
                            JSONArray resourceGroups = response.optJSONArray("data");
                            for (ViewGroup ll : mResourceGvs) {//清空原来的资源
                                ll.removeAllViews();
                            }
                            for (int i = 0; i < mTitleTvs.length; i++ ) {
                                if (i >= resourceGroups.length()) {
                                    mTitleFls[i].setVisibility(View.GONE);
                                    mResourceGvs[i].removeAllViews();
                                    mResourceGvs[i].setVisibility(View.GONE);
                                } else {
                                    mTitleFls[i].setVisibility(View.VISIBLE);
                                    mResourceGvs[i].setVisibility(View.VISIBLE);

                                    JSONObject resourceGroup = resourceGroups.optJSONObject(i);
                                    mTitleTvs[i].setText(resourceGroup.optString("categoryName"));
                                    JSONArray resourceArray = resourceGroup.optJSONArray("resources");
                                    List<Resource> resources = JsonParseUtil.parseArray(resourceArray, mResourceParser);

                                    if (resources != null && resources.size() > 0) {
                                        for (final Resource resource : resources) {
                                            VideoItemViewHolder videoItemViewHolder = new VideoItemViewHolder();
                                            View view = mInflater.inflate(videoItemViewHolder.obtainLayoutId(), mResourceGvs[i], false);
                                            videoItemViewHolder.mapFromView(view);
                                            videoItemViewHolder.setDataToView(resource, getActivity());
                                            mResourceGvs[i].addView(view, createGridItemLp());
                                            view.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Resource.gotoResDetails(getActivity(), UserInfoKeeper.obtainUserInfo(), resource);
                                                }
                                            });
                                        }
                                        if (resources.size() == 1) {
                                            mResourceGvs[i].addView(new View(getActivity()), createGridItemLp());
                                        }
                                    } else {
                                        TextView noContentsTv = new TextView(getActivity());
                                        noContentsTv.setText(R.string.sorry_for_no_contents);
                                        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                        layoutParams.gravity = Gravity.CENTER;
                                        noContentsTv.setGravity(Gravity.CENTER);
                                        int padding = UIUtils.dip2px(getContext(), 8);
                                        noContentsTv.setPadding(0, padding, 0, padding);
                                        mResourceGvs[i].addView(noContentsTv, layoutParams);
                                    }
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                        minusLoadingCount();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 创建一行两个项格子布局参数
     * @return 布局参数
     */
    private GridLayout.LayoutParams createGridItemLp() {
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        return layoutParams;
    }

    /**
     * 资源json数据解析器
     */
    private JsonParser<Resource> mResourceParser = new JsonParser<Resource>() {
        @Override
        public Resource parse(JSONObject jsonObject) {
            Resource resource = new Resource();
            resource.setId(jsonObject.optString("resourceId"));
            resource.setTitle(jsonObject.optString("resourceName"));
            resource.setType(jsonObject.optString("resourceColumn"));
            resource.setIconUrl(jsonObject.optString("thumbPath"));
            return resource;
        }
    };


    /**
     * 在container的head与tail之间插入布局id为layoutId的view
     * @param container 容器
     * @param titleView 标题
     * @param emptyView 空提示view
     * @param list 实体列表
     * @param viewStuffer 视图组件数据填充器
     * @param <T> 实体类型
     */
    private <T> void addItemsBetween(LinearLayout container, View titleView, View emptyView,
            List<T> list, ViewStuffer<T> viewStuffer) {
        ItemFillerUtil.addItems(container, titleView, emptyView, list, viewStuffer);
    }

    private OnModuleConfigListener mOnModuleConfigListener = new OnModuleConfigListener() {
        @Override
        public void onConfigLoaded(ModuleConfig config) {
            Cog.d(TAG, "onConfigLoaded config=", config);
            if (mContainerLl == null) return;//界面没加载好直接返回
            MainPageConfig mainPageConfig = config.getMainPageConfig();

            if (mainPageConfig.hasResource()) {
                loadMainResources(config.getBaseAreaId(), config.getSchoolId());
            }
        }
    };
}
