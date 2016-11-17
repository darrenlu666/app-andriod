package com.codyy.erpsportal.commons.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourActivity;
import com.codyy.erpsportal.commons.controllers.activities.HistoryVideoPlayActivity;
import com.codyy.erpsportal.commons.controllers.activities.LiveVideoListPlayActivity;
import com.codyy.erpsportal.commons.controllers.activities.LiveVideoPlayActivity;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.NetClassHistoryViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.NetClassLiveViewHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Classroom;
import com.codyy.erpsportal.commons.models.entities.HistoryVideoDetail;
import com.codyy.erpsportal.commons.models.entities.SchoolNetClassListModel;
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
 * 直录播课堂fragment
 * Created by ldh on 2015/9/10.
 */
public class SchoolNetClassFragment extends Fragment implements PullToRefreshAdapterViewBase.OnRefreshListener2, AdapterView.OnItemClickListener {

    private String TAG = "SchoolNetClassFragment";

    @Bind(R.id.document_list)
    protected PullToRefreshListView mListView;
    @Bind(R.id.empty_view)
    protected EmptyView mEmptyView;

    private Bundle mBundleFilter = null;
    private int mStart;
    private static final int mLoadCount = 10;
    private UserInfo mUserInfo;
    private int mNum;
    private int mVideoType = 0; //0：实时直播 1：往期录播

    private ObjectsAdapter mAdapter;
    private DialogUtil progressDialog;
    private boolean isFilter = false;

    private List<Student> mStudents = null;
    private static Student student = null;

    /**
     * 主课堂
     */
    private Classroom mMainClassroom;

    public static SchoolNetClassFragment newInstance(int num, UserInfo userInfo) {

        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putParcelable("userInfo", userInfo);

        SchoolNetClassFragment fragment = new SchoolNetClassFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
            mUserInfo = getArguments().getParcelable("userInfo");
        }

        //判断item类型
        if (mNum == 0) {
            mVideoType = 0;
        } else {
            mVideoType = 1;
        }

        //判断是否是家长用户，若是，则获取其孩子信息
        if (mUserInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT)) {
            getCurrentStudentId();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_live, container, false);
        ButterKnife.bind(this, view);
        initPullToRefresh(mListView);

        if (mNum == 0) {
            mAdapter = new ObjectsAdapter<SchoolNetClassListModel, NetClassLiveViewHolder>(getActivity(), NetClassLiveViewHolder.class);
        } else {
            mAdapter = new ObjectsAdapter<SchoolNetClassListModel, NetClassHistoryViewHolder>(getActivity(), NetClassHistoryViewHolder.class);
        }
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mUserInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT))
            loadData(true);
    }

    private void initPullToRefresh(PullToRefreshAdapterViewBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.BOTH);
        view.getLoadingLayoutProxy(false, true).setPullLabel("下拉刷新");
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中…");
        view.getLoadingLayoutProxy(false, true).setReleaseLabel("释放刷新");
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

    private void loadData(final boolean refresh) {
        if (mBundleFilter != null) {
            loadData(refresh, mBundleFilter.getString("areaId"), mBundleFilter.getString("directSchoolId"),
                    mBundleFilter.getString("class"), mBundleFilter.getString("subject"), mBundleFilter.getBoolean("hasDirect"));
        } else
            loadData(refresh, null, null, null, null, false);
    }

    private void loadData(final boolean refresh, String areaId, String schoolId, String classLevelId, String subjectId, boolean hasDirect) {
        if (isFilter) {
            if (progressDialog == null) {
                progressDialog = new DialogUtil(getActivity());
                progressDialog.showDialog();
            } else
                progressDialog.showDialog();
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
            params.put("baseAreaId", areaId);
        } else
            params.put("baseAreaId", mUserInfo.getBaseAreaId());

        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        } else {
            params.put("schoolId", mUserInfo.getSchoolId());
        }

        if (!TextUtils.isEmpty(classLevelId)) {
            params.put("classlevelId", classLevelId);
        }

        if (!TextUtils.isEmpty(subjectId)) {
            params.put("subjectId", subjectId);
        }

        if (!hasDirect)
            params.put("type", "nodirectly");
        else
            params.put("type", "directly");

        params.put("start", start + "");
        params.put("end", (end - 1) + "");

        if (mVideoType == 0)
            params.put("LiveType", "PROGRESS");
        else
            params.put("LiveType", "END");

        if (mUserInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT)) {
            if (UserInfoKeeper.obtainUserInfo().getSelectedChild() == null) {
                params.put("studentId", student.getStudentId());
            } else {
                params.put("studentId", UserInfoKeeper.obtainUserInfo().getSelectedChild().getStudentId());
            }
        }

        RequestSender requestSender = new RequestSender(getActivity());
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_DIRECTOR_LIST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result"))) {
                    int total = response.optInt("total");
                    if (total == 0) {
                        mAdapter.setData(null);
                        mAdapter.notifyDataSetChanged();

                        mListView.onRefreshComplete();
                        mEmptyView.setLoading(false);
                        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                        if(progressDialog != null)
                            progressDialog.cancel();
                    } else {
                        List<SchoolNetClassListModel> models = SchoolNetClassListModel.parseObject(response);
                        if (refresh)
                            mAdapter.setData(models);
                        else
                            mAdapter.addData(models);

                        mAdapter.notifyDataSetChanged();
                        mListView.onRefreshComplete();

                        //如果已经加载所有，下拉更多关闭
                        if (total <= mAdapter.getCount())
                            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        else
                            mListView.setMode(PullToRefreshBase.Mode.BOTH);

                        if(progressDialog != null)
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
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                if (refresh && mAdapter.getCount() == 0)
                    mListView.setMode(PullToRefreshBase.Mode.DISABLED);

                mListView.onRefreshComplete();
                mEmptyView.setLoading(false);
            }
        }));

        mEmptyView.setLoading(true);
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData(false);
    }

    public void doSearch(Bundle bd) {
        mBundleFilter = bd;
        isFilter = true;
        loadData(true);
    }

    /**
     * 点击某一项
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//        final SchoolNetClassListModel model = (SchoolNetClassListModel)mAdapter.getItem(position - 1);
//        if(mVideoType == 0){
//            Map<String,String> params = new HashMap<>();
//            params.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
//            params.put("appointmentId",model.getAppointmentId());
//
//            RequestSender requestSender = new RequestSender(getActivity());
//            requestSender.sendRequest(new RequestSender.RequestData(URLConfig.URL_SCHCOOL_NET_LESSON, params, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    if("success".equals(response.optString("result"))){
//
//                        final LiveVideoDetail liveVideoDetail =  LiveVideoDetail.parseObject(response);
//                        if(liveVideoDetail.getStreamingServerType().equals("DMC")){
//                            String requestUrl = liveVideoDetail.getDmsServerHost()+"?method=play&stream=class_"+model.getClsClassroomId()+"_u_"+model.getAppointmentId()+"__main";
//                            RequestQueue queue = RequestManager.getRequestQueue();
//                            NormalGetRequest request = new NormalGetRequest(requestUrl, new Response.Listener<JSONObject>() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    Cog.d(TAG, "loadDmcStream response=", response);
//                                    String serverAddress = response.optString("result");
//                                    String streamAddress = "";
//                                    if(model.getRoomType().equals("RECEIVE")){
//                                        streamAddress = serverAddress + "/class_" + model.getMainClassId() + "_u_" + model.getAppointmentId() + "__main";
//                                    }else{
//                                        streamAddress = serverAddress + "/class_" + model.getClsClassroomId() + "_u_" + model.getAppointmentId() + "__main";
//                                    }
//                                    LiveVideoPlayActivity.startVideoFromSchoolNet(getActivity(),liveVideoDetail,streamAddress,model.getAppointmentId());
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    LiveVideoPlayActivity.startVideoFromSchoolNet(getActivity(),liveVideoDetail,"",model.getAppointmentId());
//                                }
//                            });
//                            queue.add(request);
//
//                        }else if(liveVideoDetail.getStreamingServerType().equals("PMS")){
//                            String playUrl = "";
//                            if(model.getRoomType().equals("RECEIVE")){
//                                playUrl = liveVideoDetail.getServerAddress()+"/class_" + model.getMainClassId() + "_u_"+model.getAppointmentId() + "__main";
//                            }else{
//                                playUrl = liveVideoDetail.getServerAddress()+"/class_" + model.getClsClassroomId() + "_u_"+model.getAppointmentId() + "__main";
//                            }
//                            LiveVideoPlayActivity.startVideoFromSchoolNet(getActivity(),liveVideoDetail,playUrl,model.getAppointmentId());
//                        }else if(liveVideoDetail.getStreamingServerType().equals("")){
//                            ToastUtil.showToast(getActivity(),"还未开始");
//                        }
//                    }else
//                        ToastUtil.showToast(getActivity(),"获取失败");
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    ToastUtil.showToast(getActivity(),"获取失败");
//                }
//            }));
//
//        }else if(mVideoType == 1){
//            if(model.getTransFlag().equals("Y")){
//                loadPeriodData(model.getAppointmentId());
//
//            }else if(model.getTransFlag().equals("N")){
//                ToastUtil.showToast(getActivity(), getActivity().getString(R.string.txt_live_history_has_no_play_back_tips));
//            }
//
//        }
//
//    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final SchoolNetClassListModel model = (SchoolNetClassListModel) mAdapter.getItem(position - 1);
        if (mVideoType == 0) {
            Map<String, String> params = new HashMap<>();
            params.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
            params.put("id", model.getAppointmentId());

            RequestSender requestSender = new RequestSender(getActivity());
            requestSender.sendRequest(new RequestSender.RequestData(URLConfig.SCHOOL_NET_CLASSROOM_VIDEOS, params, new Response.Listener<JSONObject>() {
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
                                        LiveVideoListPlayActivity.start(getActivity(), mMainClassroom, mUserInfo,
                                                ClassTourActivity.TYPE_SCHOOL_NET);
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
                                                LiveVideoPlayActivity.start(getActivity(), model, playUrl);
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
                                        LiveVideoPlayActivity.start(getActivity(), model, playUrl);
                                    }
                                }
                            }
                        }
                    } else
                        ToastUtil.showToast(getActivity(), "获取失败");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ToastUtil.showToast(getActivity(), "获取失败");
                }
            }));

        } else if (mVideoType == 1) {
            if (model.getTransFlag().equals("Y")) {
                loadPeriodData(model.getAppointmentId());

            } else if (model.getTransFlag().equals("N")) {
                ToastUtil.showToast(getActivity(), getActivity().getString(R.string.txt_live_history_has_no_play_back_tips));
            }

        }

    }

    /**
     * 加载当前课堂的所有录播视频
     *
     * @param appointmentId
     */
    private void loadPeriodData(String appointmentId) {
        if (progressDialog == null) {
            progressDialog = new DialogUtil(getActivity());
            progressDialog.showDialog();
        } else
            progressDialog.showDialog();

        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        params.put("appointmentId", appointmentId);

        RequestSender requestSender = new RequestSender(getActivity());
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.SCHOOLNET_VIDEO_BACK, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if ("success".equals(response.optString("result"))) {
                    HistoryVideoDetail historyVideoDetail = HistoryVideoDetail.parseObject(response);
                    HistoryVideoPlayActivity.start(getActivity(), false, "1", historyVideoDetail);
                    progressDialog.cancel();
                   /* if(historyVideoDetail.getData().size() == 1){
                        String liveAppointmentVideoId = historyVideoDetail.getData().get(0).getLiveAppointmentVideoId();
                        //跳转到播放界面
                        //HistoryVideoPlayActivity.startVideoFromSchoolNetList(getActivity(), null, historyVideoDetail);
                        HistoryVideoPlayActivity.startVideoFromSchoolNetList(getActivity(),URLConfig.HISTORY_VIDEO_SCHOOLNET + "?id=" + liveAppointmentVideoId,null,historyVideoDetail);
                    }else {
                        ArrayList<String> liveAppointmentVideoIdList = new ArrayList<>();
                        for(int i = 0;i < historyVideoDetail.getData().size();i++){
                            liveAppointmentVideoIdList.add(historyVideoDetail.getData().get(i).getLiveAppointmentVideoId());
                        }
                        HistoryVideoPlayActivity.startVideoFromSchoolNetList(getActivity(),URLConfig.HISTORY_VIDEO_SCHOOLNET + "?id=" + liveAppointmentVideoIdList.get(0) ,liveAppointmentVideoIdList,historyVideoDetail);
                        HistoryVideoPlayActivity.setDataEntity(historyVideoDetail.getData());
                    }*/
                } else {
                    UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                    if (null != progressDialog) progressDialog.cancle();
                }
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
