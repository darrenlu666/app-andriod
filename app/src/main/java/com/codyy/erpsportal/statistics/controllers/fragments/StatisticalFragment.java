package com.codyy.erpsportal.statistics.controllers.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.statistics.models.entities.StatisticalItem;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.statistics.widgets.StatBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计
 */
public class StatisticalFragment extends Fragment {

    private final static String TAG = "StatisticalFragment";

    /**
     * 统计类型参数
     */
    public final static String ARG_TYPE = "arg_type";

    /**
     * 登陆用户数据参数
     */
    public final static String ARG_USER_INFO = "arg_user_info";

    /**
     * 学校课堂统计
     */
    public final static int TYPE_CLASS = 0;

    /**
     * 活动统计
     */
    public final static int TYPE_ACTIVITY = 1;

    /**
     * 资源统计
     */
    public final static int TYPE_RESOURCE = 2;

    /**
     * 机构按日课堂统计
     */
    public final static int TYPE_CLASS_DAY = 3;

    /**
     * 机构按月课堂统计
     */
    public final static int TYPE_CLASS_TERM = 4;

    private ListView mListView;

    private UserInfo mUserInfo;

    private EmptyView mEmptyView;

    private int mType;

    private ObjectsAdapter<StatisticalItem, StatisticalViewHolder> mAdapter;

    private RequestSender mSender;

    private OnIsLeafAreaConfirm mOnIsLeafAreaConfirm;

    public static StatisticalFragment newInstance() {
        StatisticalFragment fragment = new StatisticalFragment();
        return fragment;
    }

    public StatisticalFragment() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSender = new RequestSender(activity);
        if (activity instanceof OnIsLeafAreaConfirm) {
            mOnIsLeafAreaConfirm = (OnIsLeafAreaConfirm) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 先尝试从fragment的argument中取用户数据与统计类型
         * 没有argument则在Activity的Intent中取
         */
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(ARG_USER_INFO);
            mType = getArguments().getInt(ARG_TYPE);
        } else if (getActivity().getIntent() != null) {
            mUserInfo = getActivity().getIntent().getParcelableExtra(ARG_USER_INFO);
            mType = getActivity().getIntent().getIntExtra(ARG_TYPE, 0);
        }
    }

    private void loadData() {
        String url = null;
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        if (mUserInfo.isSchool()) {//若登陆的是学校账号，请求参数加入学校id，否则加入基础地区id
            params.put("schoolId", mUserInfo.getSchoolId());
        } else {
            params.put("baseAreaId", mUserInfo.getBaseAreaId());
        }

        StatisticalItem.TYPE type = null;
        switch (mType) {
            case TYPE_CLASS://学校课堂统计
                url = URLConfig.STATISTIC_CLASS;
                type = StatisticalItem.TYPE.CLASS_SCHOOL;
                break;
            case TYPE_ACTIVITY://活动统计
                if (mUserInfo.isSchool()) {
                    url = URLConfig.STATISTIC_ACTIVITY;
                    type = StatisticalItem.TYPE.ACTIVITY_SCHOOL;
                } else {
                    url = URLConfig.STATISTIC_ACTIVITY_ORG;
                    type = StatisticalItem.TYPE.ACTIVITY_ORG;
                }
                break;
            case TYPE_RESOURCE://资源统计
                if (mUserInfo.isSchool()) {
                    url = URLConfig.STATISTIC_RESOURCE;
                    type = StatisticalItem.TYPE.RESOURCE_SCHOOL;
                } else {
                    url = URLConfig.STATISTIC_RESOURCE_ORG;
                    type = StatisticalItem.TYPE.RESOURCE_ORG;
                }
                break;
            case TYPE_CLASS_DAY://机构课堂统计今日开课数
                url = URLConfig.STATISTIC_CLASS_ORG;
                params.put("statType", "day");
                type = StatisticalItem.TYPE.CLASS_COUNT;
                break;
            case TYPE_CLASS_TERM://机构课堂统计学期开课比例
                url = URLConfig.STATISTIC_CLASS_ORG;
                params.put("statType", "term");
                type = StatisticalItem.TYPE.CLASS_PERCENT;
                break;
        }
        final StatisticalItem.TYPE type1 = type;
        Cog.d(TAG, "send request url" + url);
        Cog.d(TAG, "send request params" + params);
        mSender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                String result = response.optString("result");
                if ("success".equals(result) || "true".equals(result)) {
                    boolean isLeaf = response.optBoolean("isLeaf");
                    if (mOnIsLeafAreaConfirm != null) {
                        mOnIsLeafAreaConfirm.onIsLeafAreaConfirmed(isLeaf);
                    }
                    JSONArray jsonArray = response.optJSONArray("list");
                    List<StatisticalItem> items = StatisticalItem.parseJsonArray(jsonArray, type1);
                    if (type1 != StatisticalItem.TYPE.CLASS_PERCENT) {
                        setItemsMax(items);
                    }
                    mAdapter.setData(items);
                    mAdapter.notifyDataSetChanged();
                }
                mEmptyView.setLoading(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                mEmptyView.setLoading(false);
            }
        }));
        mEmptyView.setLoading(true);
    }

    private void setItemsMax(List<StatisticalItem> items) {
        int max = 0;
        for (StatisticalItem item : items) {
            if (item.getCount() > max) {
                max = item.getCount();
            }
        }
        for (StatisticalItem item : items) {
            item.setMax(max);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistical, container, false);
        findViews(view);

        initEmptyView();
        mAdapter = new ObjectsAdapter<>(getActivity(), StatisticalViewHolder.class);
        mListView.setAdapter(mAdapter);
        return view;
    }

    private void findViews(View view) {
        mListView = (ListView) view.findViewById(R.id.lv_statistic);
        mEmptyView = (EmptyView) view.findViewById(R.id.empty_view);
    }

    /**
     * 初始化空视图
     */
    private void initEmptyView() {
        mListView.setEmptyView(mEmptyView);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                loadData();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    /**
     * 统计数据ViewHolder
     */
    public static class StatisticalViewHolder extends AbsViewHolder<StatisticalItem> {

        private TextView title;

        private StatBar bar;

        public StatisticalViewHolder() { }

        public StatisticalViewHolder(View view) {
            super(view);
        }

        @Override
        public int obtainLayoutId() {
            return R.layout.item_statistic;
        }

        @Override
        public void mapFromView(View view) {
            this.title = (TextView) view.findViewById(R.id.statistic_title);
            this.bar = (StatBar) view.findViewById(R.id.stat_bar);
        }

        @Override
        public void setDataToView(StatisticalItem data, Context context) {
            title.setText(data.getTitle());
            Cog.d(TAG, "item content=" + data.getCount());
            bar.setText(data.getContent());
            //统计项统计类型为学期开课比率比率条最大为1000
            if (data.getType() != StatisticalItem.TYPE.CLASS_PERCENT && data.getMax() != 0) {
                bar.setMax(data.getMax());
            } else {
                bar.setMax(1000);
            }
            bar.setCurrent(data.getCount());
        }
    }

    /**
     * 获取的统计信息是否是叶级地区（无下级地区）
     */
    interface OnIsLeafAreaConfirm{
        /**
         * 获取的统计信息是否是叶级地区（无下级地区），提供回调给Activity更新界面（如有需求，当前没有用）
         * @param isLeaf 是叶级
         */
        void onIsLeafAreaConfirmed(boolean isLeaf);
    }
}
