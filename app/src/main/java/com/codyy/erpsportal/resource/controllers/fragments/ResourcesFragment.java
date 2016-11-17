package com.codyy.erpsportal.resource.controllers.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.resource.controllers.viewholders.VideoItemViewHolder;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优课资源
 * Created by gujiajia
 */
public class ResourcesFragment extends Fragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2{

    private static final String TAG = "ResourcesFragment";

    public static final String ARG_USER_INFO = "arg_user_info";

    public static final String ARG_TYPE = "arg_type";

    /**
     * 上传的资源
     */
    public static final String TYPE_SELF = "Self";

    /**
     * 下级推荐的资源
     */
    public static final String TYPE_RECOMMENDED = "Recommend";

    /**
     * 收藏的资源
     */
    public static final String TYPE_FAVORITE = "Favorite";

    private static final int LOAD_COUNT = 10;

    private PullToRefreshGridView mGridView;

    private EmptyView mEmptyView;

    private ObjectsAdapter<Resource, VideoItemViewHolder> mAdapter;

    private UserInfo mUserInfo;

    private String mType;

    private int mStart;

    private RequestSender mSender;

    /**
     * 筛选参数
     */
    private Map<String,String> mFilterParams;

    public ResourcesFragment() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSender = new RequestSender(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(ARG_USER_INFO);
            mType = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource, container, false);
        mGridView = (PullToRefreshGridView) view.findViewById(R.id.grid_view);
        mEmptyView = (EmptyView) view.findViewById(R.id.empty_view);

        mAdapter = new ObjectsAdapter<>(getActivity(),VideoItemViewHolder.class);
        mGridView.setAdapter(mAdapter);
        mGridView.setEmptyView(mEmptyView);
        mGridView.setOnItemClickListener(this);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                loadData(true);
            }
        });

        initPullToRefresh(mGridView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(true);
    }

    public void loadData(Map<String, String> params, final boolean refresh) {
        mFilterParams = params;
        params.put("uuid", mUserInfo.getUuid());
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + LOAD_COUNT;
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));

        params.put("from", mType);
        Cog.d(TAG, "params:" + params);
        mSender.sendRequest(new RequestSender.RequestData(URLConfig.RESOURCE_LIST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {

                    int total = response.optInt("total");
                    if (total == 0) {
                        mAdapter.setData(null);
                        mAdapter.notifyDataSetChanged();

                        mGridView.onRefreshComplete();
                        mEmptyView.setLoading(false);
                        mGridView.setMode(PullToRefreshBase.Mode.DISABLED);
                    } else {
                        JSONArray jsonArray = response.optJSONArray("list");
                        List<Resource> resources = Resource.Parser1.parseArray(jsonArray);
                        if (refresh) {
                            mAdapter.setData(resources);
                        } else {
                            mAdapter.addData(resources);
                        }
                        mAdapter.notifyDataSetChanged();

                        mGridView.onRefreshComplete();
                        //如果已经加载所有，下拉更多关闭
                        if (total <= mAdapter.getCount()) {
                            mGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mGridView.setMode(PullToRefreshBase.Mode.BOTH);
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
                    mGridView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                mGridView.onRefreshComplete();
                mEmptyView.setLoading(false);
            }
        }));
        mEmptyView.setLoading(true);
    }

    private void loadData(boolean refresh) {
        //?uuid=MOBILE:5f2081ddd4734f8eb0839e6902ad7c84
        // &start=0&end=9
        // &resourceName=
        // &categoryId=
        // &semesterId=&classLevelId=&subjectId=
        // &versionId=&volumeId=&chapterId=&sectionId=";
        Map<String,String> params;
        if (mFilterParams != null) {
            params = mFilterParams;
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
        Resource resource = mAdapter.getItem(position);
        Resource.gotoResDetails(getActivity(), mUserInfo, resource);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData( true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData( false);
    }

    /**
     * 限定资源来源类型注解
     */
    @StringDef(value={TYPE_SELF, TYPE_RECOMMENDED, TYPE_FAVORITE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResourceFromType{}
}
