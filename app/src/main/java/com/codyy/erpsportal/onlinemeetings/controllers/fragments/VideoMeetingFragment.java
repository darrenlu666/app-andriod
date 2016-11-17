package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.app.Activity;
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
import com.codyy.erpsportal.commons.controllers.fragments.TipProgressFragment;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.VideoMeetingDetailActivity;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.onlinemeetings.controllers.viewholders.VideoMeetingViewHolder;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.onlinemeetings.models.entities.VideoMeetingEntity;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频会议
 * Created by ldh on 2015/7/30.
 */
public class VideoMeetingFragment extends Fragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {
    private final static String TAG = "VideoMeetingFragment";
    private final static String ARG_LIST_TYPE = "video.meeting.type";
    public final static int TYPE_FOR_LAUNCH = 0;//我发起的
    public final static int TYPE_FOR_ATTEND = 1;//我参与的
    public final static int TYPE_FOR_AREA  =  2;//本级会议管理
    public final static int TYPE_FOR_SCHOOL = 2;//本校会议管理
    private int mCurType;
    private ObjectsAdapter<VideoMeetingEntity, VideoMeetingViewHolder> mAdapter;
    private List<VideoMeetingEntity> mData = new ArrayList<>();
    private DialogUtil progressDialog;
    //listView视图
    private View view = null;
    private PullToRefreshListView mListView;
    private EmptyView mEmptyView;

    public static final int REQUEST_VIDEOMEETING_OUT = 0x001;

    private UserInfo mUserInfo = null;//用户信息
    private RequestSender mRequestSender;

    //每页加载的条数
    private final static int ITEM_COUNT = 7;
    //请求开始位置：默认为0
    private int mStart = 0;

    private String mFilter = "";
    private boolean isFilter = false;

    public static VideoMeetingFragment newInstance(int type ,UserInfo userinfo){
        VideoMeetingFragment fragment = new VideoMeetingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LIST_TYPE, type);
        args.putParcelable(Constants.USER_INFO,userinfo);
        fragment.setArguments(args);
        return  fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //登录用户信息
//        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mUserInfo = getArguments().getParcelable(Constants.USER_INFO);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRequestSender = new RequestSender(getActivity());
        mCurType = getArguments().getInt(ARG_LIST_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_videomeeting, null);
            initView(view);
            loadData(true, mCurType, mFilter);
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;
    }

    private void initView(View view) {

        mListView = (PullToRefreshListView) view.findViewById(R.id.ptrl_videomeeting_list);
        mEmptyView = (EmptyView) view.findViewById(R.id.empty_view);
        mListView.setEmptyView(mEmptyView);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                //加载列表数据
                loadData(true, mCurType, mFilter);
            }
        });
        initPullToRefresh(mListView);
        mListView.setOnItemClickListener(this);
        mAdapter = new ObjectsAdapter<>(getActivity(), VideoMeetingViewHolder.class);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cog.d(TAG, "onActivityCreated");
    }

    private void initPullToRefresh(PullToRefreshAdapterViewBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.BOTH);
        view.getLoadingLayoutProxy(false, true).setPullLabel("下拉刷新");
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中…");
        view.getLoadingLayoutProxy(false, true).setReleaseLabel("释放刷新");
        view.setOnRefreshListener(this);

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData(true, mCurType, mFilter);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData(false, mCurType, mFilter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Cog.d(TAG, "onDestroy");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getActivity(), VideoMeetingDetailActivity.class);
        intent.putExtra("mid", mAdapter.getItem(position - 1).getId());
        intent.putExtra("meetingType", mCurType);
        intent.putExtra(Constants.USER_INFO , mUserInfo);
        //this.startActivity(intent);
        this.startActivityForResult(intent, REQUEST_VIDEOMEETING_OUT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEOMEETING_OUT && resultCode == 1) {
            loadData(true, mCurType, mFilter);
            TipProgressFragment fragment = TipProgressFragment.newInstance(TipProgressFragment.OUT_STATUS_TIP);
            fragment.show(getFragmentManager(), "showtips");
        }

    }

    public void execFilter(String Filter) {
        Cog.d(TAG, Filter);
        isFilter = true;
        loadData(true, mCurType, Filter);

        mFilter = Filter;
    }

    /**
     * @param type
     */
    private void loadData(final boolean refreash, int type, String meet_sate) {
        if (isFilter) {
            if (progressDialog == null) {
                progressDialog = new DialogUtil(getActivity());
                progressDialog.showDialog();
            } else {
                progressDialog.showDialog();
            }
            isFilter = false;
        }
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());

        if (refreash)
            mStart = 0;
        int end = mStart + ITEM_COUNT;
        params.put("start", mStart + "");
        params.put("end", end + "");
        params.put("meet_type",String.valueOf(type));

        if (!meet_sate.equals(""))
            params.put("meet_sate", meet_sate);

        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_VIDEOMEETING, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result"))) {
                    int total = response.optInt("total");//total为列表条数
                    if (total == 0) {
                        mAdapter.setData(null);
                        mAdapter.notifyDataSetChanged();
                        mListView.onRefreshComplete();
                        mEmptyView.setLoading(false);
                        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    } else {
                        JSONArray jsonArray = response.optJSONArray("list");
                        List<VideoMeetingEntity> result = VideoMeetingEntity.parseJsonArray(jsonArray);
                        if (refreash) {
                            mAdapter.setData(result);
                        } else {

                            mAdapter.addData(result);
                        }

                        mAdapter.notifyDataSetChanged();
                        mListView.onRefreshComplete();


                        if (total <= mAdapter.getCount()) {
                            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    }
                    mStart = mAdapter.getCount();
                } else {
                    mListView.onRefreshComplete();
                    mEmptyView.setLoading(false);
                }
                if(progressDialog != null)
                    progressDialog.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                if (refreash && mAdapter.getCount() == 0) {
                    mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                mListView.onRefreshComplete();
                mEmptyView.setLoading(false);
                if(progressDialog != null)
                    progressDialog.cancel();
            }
        }));

        mEmptyView.setLoading(true);
    }
}
