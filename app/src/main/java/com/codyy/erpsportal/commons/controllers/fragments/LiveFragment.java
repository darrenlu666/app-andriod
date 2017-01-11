package com.codyy.erpsportal.commons.controllers.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourNewActivity;
import com.codyy.erpsportal.commons.controllers.activities.HistoryVideoPlayActivity;
import com.codyy.erpsportal.commons.controllers.activities.LiveVideoListPlayActivity;
import com.codyy.erpsportal.commons.controllers.activities.LiveVideoPlayActivity;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.LiveHistoryViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.LiveViewHolder;
import com.codyy.erpsportal.commons.models.entities.Classroom;
import com.codyy.erpsportal.commons.models.entities.HistoryVideoDetail;
import com.codyy.erpsportal.commons.models.entities.LiveClassListModel;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.parsers.ClassTourClassroomParser;
import com.codyy.erpsportal.commons.models.personal.Student;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.url.URLConfig;
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
 * 直播/往期录播
 * Created by caixingming on 2015/5/7.
 */
public class LiveFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2, AdapterView.OnItemClickListener {
    private String TAG = "LiveFragment";
    /** 选择的index**/
    private static final String ARG_NUM_COUNT = "num";
    /** 用户信息**/
    private static final String ARG_USER_INFO = "user";
    private int mNum;
    private UserInfo mUserInfo;
    public static final String TYPE_LIVE = "live";
    public static final String TYPE_HISTORY = "history";
    private String mType;
    private ObjectsAdapter mAdapter;
    private static final int mLoadCount = 10;
    private int mStart;
    private Bundle mBundleFilter = null;
    private boolean isFilter = false;

    @Bind(R.id.document_list)
    protected PullToRefreshListView mListView;
    @Bind(R.id.empty_view)
    protected EmptyView mEmptyView;
    private DialogUtil progressDialog;
    private List<Student> mStudents = null;
    private static Student student = null;
    /**
     * 主课堂
     */
    private Classroom mMainClassroom;

    /**
     * @param num       0：直播 1：往期录播
     * @param mUserInfo
     * @return
     */
    public static LiveFragment newInstance(int num, UserInfo mUserInfo) {

        LiveFragment f = new LiveFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(ARG_NUM_COUNT, num);
        args.putParcelable(ARG_USER_INFO, mUserInfo);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNum = getArguments() != null ? getArguments().getInt(ARG_NUM_COUNT) : 1;
            mUserInfo = getArguments().getParcelable(ARG_USER_INFO);

            //判断item类型
            if (mNum == 0) {
                mType = TYPE_LIVE;
            } else {
                mType = TYPE_HISTORY;
            }

            //判断是否是家长用户，若是，则获取其孩子信息
            if (mUserInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT)) {
                getCurrentStudentId();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        ButterKnife.bind(this, view);

        initPullToRefresh(mListView);

        if (mType.equals(TYPE_LIVE)) {
            mAdapter = new ObjectsAdapter<>(getActivity(), LiveViewHolder.class);
        } else {
            mAdapter = new ObjectsAdapter<>(getActivity(), LiveHistoryViewHolder.class);
        }

        mListView.setAdapter(mAdapter);
        return view;
    }

    //init the pulltorefresh listview
    private void initPullToRefresh(PullToRefreshAdapterViewBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.BOTH);
        view.getLoadingLayoutProxy(false, true).setPullLabel(getResources().getString(R.string.pull_to_refresh));
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel(getResources().getString(R.string.loading));
        view.getLoadingLayoutProxy(false, true).setReleaseLabel(getResources().getString(R.string.release_to_refresh));
        view.setOnRefreshListener(this);

        mListView.setEmptyView(mEmptyView);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                loadData(true);
            }
        });
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!mUserInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT))
            loadData(true);
    }

    /**
     * 执行搜索操作
     */
    public void doSearch(Bundle bd) {
        mBundleFilter = bd;
        isFilter = true;
        loadData(true);
    }


    private void loadData(final boolean refresh) {
        if (mBundleFilter != null) {
            loadData(refresh, mBundleFilter.getString("areaId"), mBundleFilter.getString("directSchoolId"), mBundleFilter.getString("class"), mBundleFilter.getString("subject"), mBundleFilter.getBoolean("hasDirect"));
        } else {
            loadData(refresh, null, null, null, null, false);
        }
    }

    /**
     * 没有的传递空值
     *
     * @param refresh
     * @param areaId
     * @param classLevelId
     * @param subjectId
     */
    private void loadData(final boolean refresh, String areaId, String schoolId, String classLevelId, String subjectId, boolean hasDirect) {
        if (isFilter) {
            if (progressDialog == null) {
                progressDialog = new DialogUtil(getActivity());
                progressDialog.showDialog();
            } else
                progressDialog.showDialog();
            Cog.d(TAG, "areaId=" + areaId + " ,schoolId=" + schoolId + ", classLevelId=" + classLevelId + ", subjectId=" + subjectId + ", hasDirect=" + hasDirect);
            isFilter = false;
        }
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + mLoadCount;

        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());

        if (!TextUtils.isEmpty(areaId)) {
            params.put("areaid", areaId);
        } else {
            params.put("areaid", mUserInfo.getBaseAreaId());
        }

        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        } else if (!TextUtils.isEmpty(mUserInfo.getSchoolId())) {
            params.put("schoolId", mUserInfo.getSchoolId());
        }

        if (!TextUtils.isEmpty(classLevelId)) {
            params.put("classlevelId", classLevelId);
        }

        if (!TextUtils.isEmpty(subjectId)) {
            params.put("subjectId", subjectId);
        }

        if (!hasDirect) {
            params.put("type", "nodirectly");
        } else {
            params.put("type", "directly");
        }
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));

        if (mUserInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT)) {
            if (mUserInfo.getSelectedChild() == null) {
                params.put("studentUserId", student.getStudentId());//studentId
            } else {
                params.put("studentUserId", mUserInfo.getSelectedChild().getStudentId());//studentId
            }
        }

        //requestQueue.add(npr);
        RequestSender requestSender = new RequestSender(getActivity());
        requestSender.sendRequest(new RequestSender.RequestData(getLiveUrl(), params, new Response.Listener<JSONObject>() {
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

                        if (progressDialog != null)
                            progressDialog.cancel();
                    } else {
                        List<LiveClassListModel> models = LiveClassListModel.parseList(response);
                        if (refresh) {
                            mAdapter.setData(models);
                        } else {
                            mAdapter.addData(models);
                        }
                        mAdapter.notifyDataSetChanged();

                        mListView.onRefreshComplete();
                        //如果已经加载所有，下拉更多关闭
                        if (total <= mAdapter.getCount()) {
                            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        if (progressDialog != null)
                            progressDialog.cancel();
                    }
                    mStart = mAdapter.getCount();
                } else {
                    mListView.onRefreshComplete();
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
                if (progressDialog != null)
                    progressDialog.cancel();
            }
        }));
        mEmptyView.setLoading(true);
    }


    /**
     * 获取 url地址
     *
     * @return
     */
    private String getLiveUrl() {

        String url = URLConfig.URL_LIVE_LESSON;

        if (!TextUtils.isEmpty(mType)) {

            if (mType.equals(TYPE_HISTORY)) {
                url = URLConfig.URL_HISTORY_LESSON;
            } else if (mType.equals(TYPE_LIVE)) {
                url = URLConfig.URL_LIVE_LESSON;
            }
        }

        return url;
    }

    /**
     * 获取当角色为家长时的播放URL,主课堂和辅课堂信息
     *
     * @return
     */
    private String getParentPlayUrl() {
        String url = URLConfig.SPECIAL_DELIVERY_CLASSROOM_VIDEOS;
        return url;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final LiveClassListModel lcm = (LiveClassListModel) mAdapter.getItem(position - 1);

        if (mType.equals(TYPE_LIVE)) {
            Map<String, String> params = new HashMap<>();
            params.put("id", lcm.getId());
            params.put("uuid", mUserInfo.getUuid());
            RequestSender sender = new RequestSender(getActivity());
            sender.sendRequest(new RequestSender.RequestData(getParentPlayUrl(), params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ("success".equals(response.optString("result"))) {
                        JSONArray jsonArray = response.optJSONArray("watchPath");
                        List<Classroom> classroomList = new ClassTourClassroomParser().parseArray(jsonArray);
                        if (classroomList != null && classroomList.size() > 0) {
                            //获取主课堂信息放于mMainClassroom
                            for (int i = 0; i < classroomList.size(); i++) {
                                if (classroomList.get(i).getType().equals("main")) {
                                    mMainClassroom = classroomList.get(i);

                                    if (mUserInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT)) {
                                        LiveVideoListPlayActivity.start(getActivity(), mMainClassroom, mUserInfo, ClassTourNewActivity.TYPE_SPECIAL_DELIVERY_CLASSROOM);
                                        return;
                                    }

                                    if (mMainClassroom.getStreamingServerType().equals("DMC")) {
                                        String url = mMainClassroom.getDmsServerHost() + "?method=play&stream=class_"
                                                + mMainClassroom.getClassRoomId() + "_u_" + mMainClassroom.getId() + "__main";

                                        RequestQueue queue = Volley.newRequestQueue(getContext());
                                        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                String result = response.substring(response.indexOf(":") + 2, response.length() - 2);
                                                String playUrl = result + "/class_" + mMainClassroom.getClassRoomId() + "_u_" + mMainClassroom.getId() + "__main";
                                                LiveVideoPlayActivity.start(getActivity(), lcm, playUrl);
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                ToastUtil.showToast(getActivity(), error.toString());
                                            }
                                        });
                                        queue.add(stringRequest);

                                    } else if (mMainClassroom.getStreamingServerType().equals("PMS")) {
                                        String playUrl = mMainClassroom.getPmsServerHost() + "/class_" + mMainClassroom.getClassRoomId() + "_u_" + mMainClassroom.getId() + "__main";
                                        LiveVideoPlayActivity.start(getActivity(), lcm, playUrl);
                                    }
                                }
                            }
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }));

        } else if (mType.equals(TYPE_HISTORY)) {
            //判断视频是否转化完成
            if (lcm.isHasPlayBack()) {
                loadPeriodData(lcm.getId());
            } else {
                //toast tips
                ToastUtil.showToast(getActivity(), getActivity().getString(R.string.txt_live_history_has_no_play_back_tips));
            }
        }
    }

/*    private void launchLivePeriodFragment(ArrayList<PVideo> models) {
        //弹出 Fragment列表
        LivePeriodFragment lpf = new LivePeriodFragment();
        Bundle bd = new Bundle();
        bd.putParcelableArrayList("data", models);
        bd.putParcelable("user", mUserInfo);
        lpf.setArguments(bd);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();//获取FragmentTransaction 实例
        ft.replace(R.id.framePeriodOfLive, lpf);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();//提交
    }*/


    //获取数据
    private void loadPeriodData(String scheduleDetailId) {
        if (progressDialog == null) {
            progressDialog = new DialogUtil(getActivity());
            progressDialog.showDialog();
        } else {
            progressDialog.showDialog();
        }
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("clsScheduleDetailId", scheduleDetailId);

        RequestSender requestSender = new RequestSender(getActivity());
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.URL_HISTORY_VIDEO_LIST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Cog.d(TAG, " loadPeriodData onResponse:" + response);

                if ("success".equals(response.optString("result"))) {
                    int total = response.optInt("total");
                    if (total == 0) {
                        UIUtils.toast(getActivity(), "没有直播视频", Toast.LENGTH_SHORT);
                    } else {
                        HistoryVideoDetail historyVideoDetail = HistoryVideoDetail.parseObject(response);
                        //只有一条数据、则直接播放
                        HistoryVideoPlayActivity.start(getActivity(), false, "0", historyVideoDetail);
                    }
                }

                if (null != progressDialog) progressDialog.cancle();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                if (null != progressDialog) progressDialog.cancle();
            }
        }));

    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData(false);
    }

    private void getCurrentStudentId() {
        RequestQueue requestQueue = RequestManager.getRequestQueue();
        requestQueue.add(new JsonObjectRequest(URLConfig.PARENT_CHILDREN + "?userId=" + mUserInfo.getBaseUserId() + "&uuid=" + mUserInfo.getUuid(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result"))) {
                    mStudents = Student.parseData(response);
                    if (mStudents.size() > 0) {
                        student = mStudents.get(0);
                        loadData(true);
                    }
                }
            }
        }, null));
    }


}
