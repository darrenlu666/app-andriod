package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.CollectivePrepareLessonsDetailActivity;
import com.codyy.erpsportal.commons.controllers.activities.ListenDetailsActivity;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.PrePareLessonsViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.models.entities.AreaBase;
import com.codyy.erpsportal.commons.models.entities.PreparationEntity;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集体备课、互动听课列表fragment
 * Created by yangxinwu on 2015/7/27.
 */
public class CollectivePrepareLessonsFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2, AdapterView.OnItemClickListener {

    private String TAG = "CollectivePrepareLessonsFragment";
    public static final String PREPARE_LESSONS_TYPE = "prepare_lessons_type";
    /**
     * 发起的
     */
    public static final int TYPE_LAUNCH = 0x01;
    /**
     * 参与的
     */
    public static final int TYPE_JOIN = 0x02;
    /**
     * 管理的
     */
    public static final int TYPE_MANAGE = 0x03;
    private static final int mLoadCount = 10;
    private String mGradeId = null;
    private String mSubjectId = null;
    private String mStatus = null;
    private String mAreaId = null;
    private String mClsSchoolId = null;
    private String mType;
    private int mStart;
    private int mCurType;
    private UserInfo mUserInfo;
    private PullToRefreshListView mListView;
    private EmptyView mEmptyView;
    private ObjectsAdapter<PreparationEntity, PrePareLessonsViewHolder> mAdapter;
    private RequestSender mRequestSender;
    private View view;//缓存Fragment view
    private List<PreparationEntity> mPreparationEntityList;
    public static final int REQUEST_COLLECTIVE_PREPARE_OUT = 0x001;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_collective_prepare_lessons, null);
            initView(view);
            loadData(true, mCurType, mType);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }


    private void initView(View view) {
        mCurType = getArguments().getInt(PREPARE_LESSONS_TYPE);
        mUserInfo = getArguments().getParcelable(Constants.USER_INFO);
        mType = getArguments().getString(Constants.TYPE_LESSON); //判断是集体备课还是互动听课
        Cog.d(TAG, "mType" + mType);
        mListView = (PullToRefreshListView) view.findViewById(R.id.ptrl_prepare_lseeons_list);
        mEmptyView = (EmptyView) view.findViewById(R.id.empty_view);
        mListView.setEmptyView(mEmptyView);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                loadData(true, mCurType, mType);
            }
        });
        initPullToRefresh(mListView);
        mListView.setOnItemClickListener(this);
        mAdapter = new ObjectsAdapter<>(getActivity(), PrePareLessonsViewHolder.class);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRequestSender = new RequestSender(getContext());
    }

    public void loadData(final boolean refresh, int type, String urlType) {
        mEmptyView.setLoading(true);
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + mLoadCount;
        params.put("start", "" + start);
        params.put("end", "" + end);
        if (mGradeId != null) {
            params.put("classlevelId", "" + mGradeId);
        }
        if (mSubjectId != null) {
            params.put("subjectId", "" + mSubjectId);
        }
        if (mStatus != null) {
            params.put("status", "" + mStatus);
        }
        if (mAreaId != null) {
            params.put("baseAreaId", "" + mAreaId);
        }
        if (mClsSchoolId != null) {
            params.put("clsSchoolId", "" + mClsSchoolId);
        }
        mRequestSender.sendRequest(new RequestSender.RequestData(getURL(type, urlType), params, new Response.Listener<JSONObject>() {
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
                        JSONArray jsonArray = null;
                        if (mType.equals(Constants.TYPE_PREPARE_LESSON)) {
                            jsonArray = response.optJSONArray("preparation");
                        } else {
                            jsonArray = response.optJSONArray("lecture");
                        }
                        mPreparationEntityList = PreparationEntity.parseJsonArray(jsonArray, mType);
                        if (refresh) {
                            mAdapter.setData(mPreparationEntityList);
                        } else {
                            mAdapter.addData(mPreparationEntityList);
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
                } else {
                    mAdapter.setData(null);
                    mAdapter.notifyDataSetChanged();
                    mListView.onRefreshComplete();
                    mEmptyView.setLoading(false);
                    mListView.setMode(PullToRefreshBase.Mode.DISABLED);
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
        if (mType.equals(Constants.TYPE_PREPARE_LESSON)) { //点击进入集体备课详情
            Intent intent = new Intent(getActivity(), CollectivePrepareLessonsDetailActivity.class);
            intent.putExtra(Constants.PREPARATIONID, mAdapter.getItem(position - 1).getPreparationId());
            this.startActivityForResult(intent, REQUEST_COLLECTIVE_PREPARE_OUT);
            UIUtils.addEnterAnim(getActivity());
        } else {
            Intent intent = new Intent(getActivity(), ListenDetailsActivity.class);//点击进入互动听课详情
            intent.putExtra(Constants.PREPARATIONID, mAdapter.getItem(position - 1).getPreparationId());
            this.startActivityForResult(intent, REQUEST_COLLECTIVE_PREPARE_OUT);
            UIUtils.addEnterAnim(getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_COLLECTIVE_PREPARE_OUT && resultCode == 1) {
            loadData(true, mCurType, mType);
            TipProgressFragment fragment = TipProgressFragment.newInstance(TipProgressFragment.OUT_STATUS_TIP);
            fragment.show(getFragmentManager(), "showtips");
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData(true, mCurType, mType);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData(false, mCurType, mType);
    }

    /**
     * 获取发起、参与、管理的备课、听课的URL
     *
     * @param type
     * @param mType
     * @return
     */
    private String getURL(int type, String mType) {
        String url = "";
        switch (type) {
            case TYPE_LAUNCH:
                if (mType.equals(Constants.TYPE_PREPARE_LESSON)) {
                    url = URLConfig.GET_SPONSOR_PREPARATION;
                } else {
                    url = URLConfig.GET_SPONSOR_LECTURE;
                }
                break;
            case TYPE_JOIN:
                if (mType.equals(Constants.TYPE_PREPARE_LESSON)) {
                    url = URLConfig.GET_PARTICIPANT_PREPARATION;
                } else {
                    url = URLConfig.GET_PARTICIPANT_LECTURE;
                }
                break;
            case TYPE_MANAGE:
                if (mType.equals(Constants.TYPE_PREPARE_LESSON)) {
                    url = URLConfig.GET_AREA_PREPARATION;
                } else {
                    url = URLConfig.GET_AREA_LECTURE;
                }
                break;
            default:
                break;
        }
        return url;
    }

    /**
     * 只有年级、学科、状态的筛选
     *
     * @param gradeId
     * @param subjectId
     */
    public void execSearch(String gradeId, String subjectId, String status) {
        mGradeId = gradeId;
        mSubjectId = subjectId;
        mStatus = status;
        Cog.d(TAG, "mGradeId = " + mGradeId);
        Cog.d(TAG, "mSubjectId = " + mSubjectId);
        Cog.d(TAG, "mStatus = " + mStatus);
        loadData(true, mCurType, mType);
    }

    /**
     * 包含省、年级、学科、状态的筛选
     *
     * @param gradeId
     * @param subjectId
     */
    public void execAreaSearch(String gradeId, String subjectId, String status, AreaBase areaBase) {
        mGradeId = gradeId;
        mSubjectId = subjectId;
        mStatus = status;
//        if ("学校".equals(areaBase.getLevel()) && !"全部".equals(areaBase.getAreaName())) {
//            mClsSchoolId = areaBase.getSchoolID();
//        } else {
//            mAreaId = areaBase.getAreaId();
//        }
        mClsSchoolId = "";
        mAreaId = "";
        if ("area".equals(areaBase.getType())) {
            mAreaId = areaBase.getAreaId();
        } else if ("school".equals(areaBase.getType())) {
            mClsSchoolId = areaBase.getSchoolID();
        }
        Cog.d(TAG, "mGradeId = " + mGradeId);
        Cog.d(TAG, "mSubjectId = " + mSubjectId);
        Cog.d(TAG, "mStatus = " + mStatus);
        Cog.d(TAG, "mAreaId = " + mAreaId);
        loadData(true, mCurType, mType);
    }
}
