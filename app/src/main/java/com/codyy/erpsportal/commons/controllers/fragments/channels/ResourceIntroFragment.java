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
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ResourceIntroAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.ResourceIntroAdapter.GridItem;
import com.codyy.erpsportal.commons.controllers.adapters.ResourceIntroAdapter.TopItem;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 优课资源频道页
 */
public class ResourceIntroFragment extends Fragment{

    private final static String TAG = "ResourceIntroFragment";

    private View mRootView;

    private RefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    private EmptyView mEmptyView;

    private ResourceIntroAdapter mAdapter;

    private WebApi mWebApi;

    private CompositeDisposable mCompositeDisposable;

    private int mOnLoadingCount;

    public ResourceIntroFragment() { }

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
            mAdapter = new ResourceIntroAdapter(getActivity(), layoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.onStop();
        }
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
        mOnLoadingCount = 0;
        loadSlides( baseAreaId, schoolId);
        loadSemesterResData( baseAreaId, schoolId);
    }

    /**
     * 加载资源幻灯片数据
     * @param schoolId 学校id
     * @param areaId 地区id
     */
    private void loadSlides(String areaId, String schoolId) {
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(areaId)) params.put("baseAreaId" ,areaId);
        if (!TextUtils.isEmpty(schoolId)) params.put("schoolId", schoolId);
        mOnLoadingCount++;
        Cog.d(TAG, "loadSlides url=" + URLConfig.SLIDE_RESOURCES + params);
        Disposable disposable = mWebApi.post4Json(URLConfig.SLIDE_RESOURCES, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadSlides response=", response);
                        minusLoadingCount();
                        if ("success".equals(response.optString("result"))) {
                            JSONArray slideItems = response.optJSONArray("data");
                            if (slideItems == null || slideItems.length() == 0) {
                                GridItem firstItem = mAdapter.getItemAt(0);
                                if (firstItem != null && firstItem instanceof TopItem) {
                                    mAdapter.removeItem(0);
                                    mAdapter.notifyDataSetChanged();
                                }
                            } else {
                                GridItem firstItem = mAdapter.getItemAt(0);
                                List<Resource> resources = mResourceParser.parseArray(slideItems);
                                if (firstItem != null && firstItem instanceof TopItem) {
                                    TopItem firstTopItem = (TopItem) firstItem;
                                    firstTopItem.setResources(resources);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    if (resources != null && resources.size() > 0) {
                                        mAdapter.addItem(0, new TopItem(resources));
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        onLoadingFinish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                        minusLoadingCount();
                        onLoadingFinish();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void minusLoadingCount() {
        mOnLoadingCount--;
        if (mOnLoadingCount == 0) {
            mRefreshLayout.setRefreshing(false);
        }
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
        mOnLoadingCount++;
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
                            JSONArray channelResourceList = response.optJSONArray("semesterResList");
                            if (mAdapter.getItemCount() > 0) {//已经有数据了，清除老数据
                                GridItem firstItem = mAdapter.getItemAt(0);
                                if (firstItem != null && firstItem instanceof TopItem) {//有幻灯片，首数据幻灯片不清
                                    mAdapter.removeItems(1);
                                } else {
                                    mAdapter.clearItems();
                                }
                            }
                            for (int i = 0; i < channelResourceList.length(); i++) {
                                JSONObject semesterResource = channelResourceList.optJSONObject(i);
                                String semesterName = semesterResource.optString("semesterName");
                                String semesterId = semesterResource.optString("baseSemesterId");
                                mAdapter.addItem( new ResourceIntroAdapter.TitleItem(semesterName, semesterId));
                                if ( !semesterResource.isNull("resListViewList")) {
                                    List<Resource> resources = mSemesterResParser.parseArray(semesterResource.optJSONArray("resListViewList"));
                                    if (resources != null && resources.size() > 0) {
                                        for (Resource resource : resources) {
                                            mAdapter.addItem(new ResourceIntroAdapter.ResourceItem( resource));
                                        }
                                    } else {
                                        mAdapter.addItem(new ResourceIntroAdapter.EmptyItem());
                                    }
                                } else {
                                    mAdapter.addItem(new ResourceIntroAdapter.EmptyItem());
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        minusLoadingCount();
                        onLoadingFinish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "onErrorResponse error=" + error);
                        minusLoadingCount();
                        onLoadingFinish();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private JsonParser<Resource> mSemesterResParser = new JsonParser<Resource>() {
        @Override
        public Resource parse(JSONObject jsonObject) {
            Resource resource = new Resource();
            resource.setId(jsonObject.optString("resourceId"));
            if (!jsonObject.isNull("thumbPathUrl")) {
                resource.setIconUrl(jsonObject.optString("thumbPathUrl").trim());
            }
            resource.setTitle(jsonObject.optString("resourceName"));
            resource.setType(jsonObject.optString("resourceColumn"));
            resource.setViewCnt(jsonObject.optInt("viewCnt"));
            return resource;
        }
    };

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
