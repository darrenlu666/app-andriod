package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.TeachingResearchAdapter;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.MainPageConfig;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.TeachingResearchBase;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 网络教研
 * Created by kmdai on 2015/8/10.
 */
public class TeachingResearchFragment extends Fragment implements ConfigBus.OnModuleConfigListener {
    private final static String TAG = "TeachingResearchFragment";
    /**
     * 获取数据
     */
    private final static int GET_DATA = 0x001;
    private RecyclerView mRecyclerView;
    /**
     * 网络请求
     */
    private RequestSender mSender;
    /**
     *
     */
    private List<TeachingResearchBase> teachingResearchBases;
    private TeachingResearchAdapter mTeachingResearchAdapter;
    private String baseAreaId;
    private String schoolId;
    private MainPageConfig mMainPageConfig;
    private String AREA_USR;
    private UserInfo mUserInfo;
    private View mSwipeRefreshLayout;
    private EmptyView mEmptyView;
    private RefreshLayout mRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSender = new RequestSender(getActivity());
        teachingResearchBases = new ArrayList<>();
        mSwipeRefreshLayout = getActivity().getLayoutInflater().inflate(R.layout.fragment_firstpage_recycleview, null);
        mRecyclerView = (RecyclerView) mSwipeRefreshLayout.findViewById(R.id.fragment_firstpage_layout_recycleview);
        mEmptyView = (EmptyView) mSwipeRefreshLayout.findViewById(R.id.fragment_firstpage_layout_emptyview);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                teachingResearchBases.clear();
                getData();
            }
        });
        mRefreshLayout = (RefreshLayout) mSwipeRefreshLayout.findViewById(R.id.fragment_firstpage_layout_refreshlayout);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                teachingResearchBases.clear();
                mTeachingResearchAdapter.notifyDataSetChanged();
                getData();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ConfigBus.register(this);
    }

    /**
     * 获取数据
     */
    private void getData() {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("userType", mUserInfo.getUserType());
            data.put("uuid", mUserInfo.getUuid());
        }
        data.put("areaId", baseAreaId);
        data.put("schoolId", schoolId);
        httpConnect(URLConfig.GET_TEACHERING_RESEARCH, data, GET_DATA);
    }

    /**
     * 网络请求
     *
     * @param url
     * @param data
     * @param msg
     */
    private void httpConnect(String url, HashMap<String, String> data, final int msg) {
        mSender.sendRequest(new RequestSender.RequestData(url, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                switch (msg) {
                    case GET_DATA:
                        mEmptyView.setLoading(false);
                        TeachingResearchBase.getTeachingResear(response, teachingResearchBases, mMainPageConfig);
                        mTeachingResearchAdapter.notifyDataSetChanged();
                        if (teachingResearchBases.size() <= 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                if (teachingResearchBases.size() <= 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        }));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mSwipeRefreshLayout;
    }

    @Override
    public void onConfigLoaded(ModuleConfig config) {
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mMainPageConfig = config.getMainPageConfig();
        baseAreaId = config.getBaseAreaId();
        schoolId = config.getSchoolId();
        teachingResearchBases.clear();
        mTeachingResearchAdapter = new TeachingResearchAdapter(getActivity(), teachingResearchBases, baseAreaId, schoolId);
        mRecyclerView.setAdapter(mTeachingResearchAdapter);
        getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConfigBus.unregister(this);
        mSender.stop();
    }
}
