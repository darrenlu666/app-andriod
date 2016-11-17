package com.codyy.erpsportal.info.controllers.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.info.controllers.activities.InfoDetailActivity;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.info.utils.Info;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 更多资讯中的资讯列表
 * A simple {@link Fragment} subclass.
 */
public class InformationListFragment extends Fragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {


    private static final String TAG = "InformationListFragment";

    public static final String ARG_AREA_ID = "arg_area_id";

    public static final String ARG_SCHOOL_ID = "arg_school_id";

    public static final String ARG_TYPE = "arg_type";

    private static final int LOAD_COUNT = 10;

    @Bind(R.id.list_view)
    PullToRefreshListView mListView;

    @Bind(R.id.empty_view)
    EmptyView mEmptyView;

    private ObjectsAdapter<Info, InformationViewHolder> mAdapter;

    private String mType;

    private String mAreaId;

    private String mSchoolId;

    private int mStart;

    private RequestQueue mRequestQueue;


    /**
     * 参数
     */
    private Map<String,String> mParams;

    public InformationListFragment() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRequestQueue = RequestManager.getRequestQueue();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(ARG_TYPE);
            mAreaId = getArguments().getString(ARG_AREA_ID);
            mSchoolId = getArguments().getString(ARG_SCHOOL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_list, container, false);
        ButterKnife.bind(this,view);

        mAdapter = new ObjectsAdapter<>(getActivity(),InformationViewHolder.class);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(this);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                loadData(true);
            }
        });

        initPullToRefresh(mListView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(true);
    }

    public void loadData(Map<String, String> params, final boolean refresh) {
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + LOAD_COUNT;
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));

        addParams(params);
        Cog.d(TAG, "loadData:" + URLConfig.MORE_INFORMATION + params);
        mRequestQueue.add(new NormalPostRequest(URLConfig.MORE_INFORMATION, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {

                    int total = response.optInt("total");
                    if (total == 0) {
                        mAdapter.setData(null);
                        mAdapter.notifyDataSetChanged();

                        mListView.onRefreshComplete();
                        mEmptyView.setLoading(false);
                        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    } else {
                        JSONArray jsonArray = response.optJSONArray("data");
                        List<Info> infoList = Info.JSON_PARSER.parseArray(jsonArray);
                        if (refresh) {
                            mAdapter.setData(infoList);
                        } else {
                            mAdapter.addData(infoList);
                        }
                        mAdapter.notifyDataSetChanged();

                        mListView.onRefreshComplete();
                        //如果已经加载所有，下拉更多关闭
                        if (total <= mAdapter.getCount()) {
                            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    }
                    mStart = mAdapter.getCount();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                if (refresh && mAdapter.getCount() == 0) {
                    mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                mListView.onRefreshComplete();
                mEmptyView.setLoading(false);
            }
        }));
        mEmptyView.setLoading(true);
    }

    private void addParams(Map<String, String> map) {
        map.put("infoType", mType);
        if (!TextUtils.isEmpty(mSchoolId)) {
            map.put("schoolId", mSchoolId);
        }
        if (!TextUtils.isEmpty(mAreaId)) {
            map.put("baseAreaId", mAreaId);
        }
    }

    private void loadData(boolean refresh) {
        //?uuid=MOBILE:5f2081ddd4734f8eb0839e6902ad7c84
        // &start=0&end=9
        // &resourceName=
        // &categoryId=
        // &semesterId=&classLevelId=&subjectId=
        // &versionId=&volumeId=&chapterId=&sectionId=";
        Map<String,String> params;
        if (mParams != null) {
            params = mParams;
        } else {
            params = new HashMap<>();
        }
        loadData(params, refresh);
    }

    private void initPullToRefresh(PullToRefreshAdapterViewBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.BOTH);
        view.getLoadingLayoutProxy(false, true).setPullLabel(getResources().getString(R.string.pull_to_refresh));
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel(getResources().getString(R.string.loading));
        view.getLoadingLayoutProxy(false, true).setReleaseLabel(getResources().getString(R.string.release_to_refresh));
        view.setOnRefreshListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Resource resource = mAdapter.getItem(position);
//        Resource.gotoResDetails(getActivity(), mUserInfo, resource);
        Info info = mAdapter.getItem(position - 1 );
        InfoDetailActivity.startFromChannel(view.getContext(), info.getInformationId());
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData( false);
    }

    public static InformationListFragment newInstance(String type, String areaId, String schoolId) {
        InformationListFragment fragment = new InformationListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TYPE, type);
        bundle.putString(ARG_SCHOOL_ID, schoolId);
        bundle.putString(ARG_AREA_ID, areaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static class InformationViewHolder extends  AbsViewHolder<Info> {

        private TextView titleTv;

        private TextView introTv;

        private SimpleDraweeView iconDv;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_info;
        }

        @Override
        public void mapFromView(View view) {
            titleTv = (TextView) view.findViewById(R.id.tv_title);
            introTv = (TextView) view.findViewById(R.id.tv_intro);
            iconDv = (SimpleDraweeView) view.findViewById(R.id.dv_icon);
        }

        @Override
        public void setDataToView(Info data, Context context) {
            titleTv.setText(data.getTitle());
            introTv.setText(data.getContent());
            if (TextUtils.isEmpty(data.getThumb())) {
                iconDv.setVisibility(View.GONE);
            } else {
                iconDv.setVisibility(View.VISIBLE);
                ImageFetcher.getInstance(context).fetchSmall(iconDv, data.getThumb());
            }
        }
    }

}
