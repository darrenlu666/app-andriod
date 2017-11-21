package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.TipProgressFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.DocListViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.MainOrReceiveSchoolViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.ParticipateSchoolViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.RecorderPriorityViewHolder;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsDetailEntity;
import com.codyy.erpsportal.commons.models.entities.interact.RecordPriority;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.FixedRecyclerView;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * 互动听课详情
 * Created by yangxinwu on 2015/9/1.
 * modified by poe on 2016/6/22
 */
public class ListenDetailsActivity extends BaseHttpActivity implements View.OnClickListener, Handler.Callback {
    private final String TAG = "ListenDetailsActivity";
    public static final int MSG_PROGRESS_DISMISS = 200;//进度条消失 ...
    public static final int REQUEST_LISTEN_LESSON_CODE = 201;
    public static final int RESPONSE_LISTEN_LESSON_CODE = 202;
    public static final String STATUS_MEETING_OUT = "已被请出";
    public static final String REQUEST_LISTEN_OUT = "outmeetingResult";//标识：被踢出会议时，要返回列表界面，并刷新列表

    @Bind(R.id.empty_view)    EmptyView mEmptyView;
    @Bind(R.id.toolbar)    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)    TextView mTitleTextView;
    @Bind(R.id.tv_launcher)    TextView mLaunchTv;//发起方
    @Bind(R.id.tv_launch_time)    TextView mTimeTv;//发起时间
    @Bind(R.id.tv_active_name)    TextView mActiveNameTv;//活动标题
    @Bind(R.id.tv_grade_name)    TextView mGradeNameTv;//年级
    @Bind(R.id.tv_subject_name)    TextView mSubjectNameTv;//学科
    @Bind(R.id.tv_active_msg_value)    TextView mActiveMagValueTv;//后动简介
    @Bind(R.id.tv_main_school_name)    TextView mMainSchoolNameTv;//主讲学校
    @Bind(R.id.tv_prepare_lesson_room)    TextView mPrepareLessonRoomTv;//主讲教室
    @Bind(R.id.tv_prepare_people_name)    TextView mPreparePeopleNameTv;//主讲人
    @Bind(R.id.tv_reserve_start_time)    TextView mReserveStartTimeTv;//预约开始时间
    @Bind(R.id.tv_reserve_end_time)    TextView mReserveEndTimeTv;//预约结束时间
    @Bind(R.id.tv_real_start_time)    TextView mRealStartTimeTv;//开始时间
    @Bind(R.id.tv_state)    TextView mStateTv;//操作按钮  进入/观看视频/未开始
    @Bind(R.id.rl_state)    RelativeLayout mRlState;
    @Bind(R.id.rb_star)    RatingBar mRbStar;//评分
    @Bind(R.id.divider5)    View mReceiveDivider;//接收学校divider
    @Bind(R.id.divider6)    View mListenDivider;//听课端divider
    @Bind(R.id.divider7)    View mPriorityDivider;//有录制权限divider
    @Bind(R.id.divider8)    View mDocDivider;//文档divider
    @Bind(R.id.tv_listen_desc)    TextView mListenDesc;
    @Bind(R.id.tv_document_desc)    TextView mDocumentDesc;
    @Bind(R.id.lv_document)    FixedRecyclerView mDocRecyclerView;
    @Bind(R.id.lv_receive_school)    FixedRecyclerView mReceiveRecyclerView;
    @Bind(R.id.lv_listen)    FixedRecyclerView mListenRecyclerView;
    @Bind(R.id.lv_priority)    FixedRecyclerView mPriorityRecyclerView;
    @Bind(R.id.tv_speaker_school)TextView mMasterSchoolTitleTv;//标题－主讲学校
    @Bind(R.id.tv_prepare_lesson_room_text)TextView mMasterClassroomTitleTv;//标题－主讲教室
    @Bind(R.id.tv_prepare_people_name_text)TextView mMasterTeacherTitleTv;//标题－主讲教师
    @Bind(R.id.tv_receive_school_desc)    TextView mReceiveSchoolTitleTv;//标题－接收学校


    private String mPreparationId;
    private BaseRecyclerAdapter<PrepareLessonsDetailEntity.ReceiveSchoolItem, MainOrReceiveSchoolViewHolder> mReceiveAdapter;
    private BaseRecyclerAdapter<PrepareLessonsDetailEntity.ParticipatorItem, ParticipateSchoolViewHolder> mListenAdapter;
    private BaseRecyclerAdapter<RecordPriority, RecorderPriorityViewHolder> mPriorityAdapter;
    private BaseRecyclerAdapter<String, DocListViewHolder> mDocAdapter;
    private PrepareLessonsDetailEntity mDetailEntity;
    private List<String> mDocList = new ArrayList<>();//相关文档列表
    private List<PrepareLessonsDetailEntity.ParticipatorItem> participatorItems = new ArrayList<>();//听课端列表
    private List<PrepareLessonsDetailEntity.ReceiveSchoolItem> receiveSchoolItems = new ArrayList<>();//接收学校列表
    private List<RecordPriority> mPriorityList = new ArrayList<>();
    private ProgressDialog mProgressBar;
    private Handler mHandler = new Handler(this);

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_lessons_detail;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_LECTURE_DETAIL;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("lectureId", mPreparationId);
        return params;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG, "onResponse:" + response);
        if (mEmptyView == null) return;
        mEmptyView.setVisibility(View.GONE);
        mEmptyView.setLoading(false);
        mDetailEntity = PrepareLessonsDetailEntity.parseJson(response, "");
        mActiveNameTv.setText(mDetailEntity.getData().getTitle());
        mTimeTv.setText(mDetailEntity.getData().getSponsorDate());
        mActiveMagValueTv.setText(mDetailEntity.getData().getDescription());
        mLaunchTv.setText(mDetailEntity.getData().getSponsorName());
        mRbStar.setProgress(mDetailEntity.getData().getAverageScore());
        if(TextUtils.isEmpty(mDetailEntity.getData().getClassLevelName())||"不限年级".equals(mDetailEntity.getData().getClassLevelName())){
            mGradeNameTv.setText("不限");
        }else{
            String levels = mDetailEntity.getData().getClassLevelName();
            if(levels.contains(",")){
                levels = levels.replace(","," ");
            }
            mGradeNameTv.setText(levels);
        }

        mSubjectNameTv.setText(mDetailEntity.getData().getSubjectName());
        mReserveStartTimeTv.setText(mDetailEntity.getData().getBeginDate());
        mReserveEndTimeTv.setText(mDetailEntity.getData().getEndDate());
        if (mDetailEntity.getData().getStartDate().equals("null")|| TextUtils.isEmpty(mDetailEntity.getData().getStartDate().trim())) {
            mRealStartTimeTv.setText(getResources().getString(R.string.lesson_unstart));
        } else {
            mRealStartTimeTv.setText(mDetailEntity.getData().getStartDate());
        }
        if (mDetailEntity.getMasterSchool().isSelfSchool()) {
            mMainSchoolNameTv.setText(mDetailEntity.getMasterSchool().getSchoolName() + "(本校)");
        } else {
            mMainSchoolNameTv.setText(mDetailEntity.getMasterSchool().getSchoolName());
        }
        mPrepareLessonRoomTv.setText(mDetailEntity.getMasterSchool().getMasterclassroom());
        mPreparePeopleNameTv.setText(mDetailEntity.getMasterSchool().getMasterteacher());

        //modify by kmdai
        RecordPriority permissionrp = new RecordPriority();
        permissionrp.setRecorder(true);
        permissionrp.setRecorderName(mDetailEntity.getMasterSchool().getMasterteacher());
        permissionrp.setRecorderSchool(mDetailEntity.getMasterSchool().getSchoolName());
        mPriorityList.add(permissionrp);
        if (null != mDetailEntity.getPermission().getSchoolName() && !TextUtils.isEmpty(mDetailEntity.getPermission().getSchoolName().trim())) {
            RecordPriority rp = new RecordPriority();
            rp.setRecorder(mDetailEntity.getPermission().isHasPermission());
            rp.setRecorderName(mDetailEntity.getPermission().getClassName());
            rp.setRecorderSchool(mDetailEntity.getPermission().getSchoolName());
            mPriorityList.add(rp);
        }
        mDocList.addAll(mDetailEntity.getInterrelatedDoc());
        participatorItems.addAll(mDetailEntity.getParticipator());
        receiveSchoolItems.addAll(mDetailEntity.getReceiveSchoolItems());
        //相关文档
        if (mDocList == null || mDocList.size() == 0) {
            //隐藏 -文档
            mDocDivider.setVisibility(View.GONE);
            mDocumentDesc.setVisibility(View.GONE);
            mDocRecyclerView.setVisibility(View.GONE);
        } else {
            mDocAdapter.setData(mDocList);
        }


        //接收学校
        if (receiveSchoolItems == null || receiveSchoolItems.size() == 0) {

            //隐藏-接收学校
            mReceiveDivider.setVisibility(View.GONE);
            mReceiveSchoolTitleTv.setVisibility(View.GONE);
            mReceiveRecyclerView.setVisibility(View.GONE);
        } else {
            mReceiveAdapter.setData(receiveSchoolItems);
        }

        //听课端
        if (participatorItems == null || participatorItems.size() == 0) {
            //隐藏-听课端
            mListenDivider.setVisibility(View.GONE);
            mListenDesc.setVisibility(View.GONE);
            mListenRecyclerView.setVisibility(View.GONE);
        } else {
            mListenAdapter.setData(participatorItems);
        }

        mPriorityAdapter.setData(mPriorityList);

        if (mDetailEntity.getData().getStatus().equals("INIT")) {
            mStateTv.setText(getResources().getString(R.string.lesson_unstart));
        } else if (mDetailEntity.getData().getStatus().equals("PROGRESS")) {


            int role = Integer.valueOf(mDetailEntity.getUserType());
            switch (role){
                case MeetingBase.BASE_MEET_ROLE_0:
                    mStateTv.setText(getResources().getString(R.string.lesson_on));
                    break;
                case MeetingBase.BASE_MEET_ROLE_1://发言人
                case MeetingBase.BASE_MEET_ROLE_2://参会者
                case MeetingBase.BASE_MEET_ROLE_4://来宾
                    mStateTv.setText(getResources().getString(R.string.lesson_into_listen));
                    break;
                case MeetingBase.BASE_MEET_ROLE_3://观摩者
                    mStateTv.setText(getResources().getString(R.string.lesson_view));
                    break;
                default:
                    mStateTv.setText(getResources().getString(R.string.lesson_view));
                    break;
            }
        } else {
            if (mDetailEntity.getHasVideo()) {
                mStateTv.setText(getResources().getString(R.string.lesson_view_video));
            } else {
                mStateTv.setText(getResources().getString(R.string.lesson_end));
            }
        }
    }

    @Override
    public void onFailure(Throwable error) {
        Cog.e(TAG, "onErrorResponse:" + error);
        if (mEmptyView == null) return;
        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(false);
    }

    @Override
    public void init() {
        mPreparationId = getIntent().getStringExtra(Constants.PREPARATIONID);
        initToolbar(mToolBar);
        mTitleTextView.setText(Titles.sWorkspaceListen+"详情");
        mMasterSchoolTitleTv.setText(Titles.sMasterSchool);
        mMasterTeacherTitleTv.setText(Titles.sMasterTeacher);
        mMasterClassroomTitleTv.setText(Titles.sMasterRoom);
        mReceiveSchoolTitleTv.setText(Titles.sReceiveSchool);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                requestData(true);
            }
        });
        mRlState.setOnClickListener(this);
        mReceiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mListenRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDocRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPriorityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setAdapter();
        showProgress();
        //get detail data .
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(true);
        requestData(true);
    }

    private void setAdapter() {
        mReceiveAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<MainOrReceiveSchoolViewHolder>() {
            @Override
            public MainOrReceiveSchoolViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new MainOrReceiveSchoolViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(), R.layout.item_receive_school));
            }

            @Override
            public int getItemViewType(int position) {
                return MainOrReceiveSchoolViewHolder.ITEM_TYPE_RECEIVE_SCHOOL;
            }
        });
        mReceiveRecyclerView.setAdapter(mReceiveAdapter);
        mListenAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<ParticipateSchoolViewHolder>() {
            @Override
            public ParticipateSchoolViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new ParticipateSchoolViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(), R.layout.item_assessment_detail_participate));
            }

            @Override
            public int getItemViewType(int position) {
                return ParticipateSchoolViewHolder.ITEM_TYPE_LISTEN_TERMINAL;
            }
        });
        mListenRecyclerView.setAdapter(mListenAdapter);
        mDocAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<DocListViewHolder>() {
            @Override
            public DocListViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new DocListViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(), R.layout.item_resource_prepare_lseeons));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mDocRecyclerView.setAdapter(mDocAdapter);
        mPriorityAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<RecorderPriorityViewHolder>() {
            @Override
            public RecorderPriorityViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new RecorderPriorityViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(), R.layout.item_resource_prepare_lseeons));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mPriorityRecyclerView.setAdapter(mPriorityAdapter);
    }

    private void showProgress() {
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_PROGRESS_DISMISS://dismiss the progress bar .
                if (null != mProgressBar)
                    mProgressBar.dismiss();
                break;
        }
        return false;
    }


    public void actionClick(String role) {
        //show progress dialog
        if (null == mProgressBar) {
            mProgressBar = new ProgressDialog(this);
            mProgressBar.setIndeterminate(false);
        }
        mProgressBar.setMessage(getResources().getString(R.string.tips_add_in));
        mProgressBar.show();
        //1.去获取视频会议基本信息
        UiOnlineMeetingUtils.loadMeetingBaseData(getSupportFragmentManager(), this, mUserInfo.getUuid(), mPreparationId, role, new UiOnlineMeetingUtils.ICallback() {
            @Override
            public void onSuccess(JSONObject response) {
                final MeetingBase meetingBase = MeetingBase.parseJson(response);
                UiOnlineMeetingUtils.loadCocoInfo(ListenDetailsActivity.this, meetingBase.getBaseMeetID(),
                        mUserInfo.getUuid(), new UiOnlineMeetingUtils.ICallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                JSONObject server = response.optJSONObject("server");
                                if(null != server){

                                    meetingBase.setToken(server.optString("token"));
                                    meetingBase.getBaseCoco().setCocoIP(server.optString("serverHost"));
                                    meetingBase.getBaseCoco().setCocoPort(server.optString("port"));
                                    OnlineMeetingActivity.startForResult(ListenDetailsActivity.this,
                                            mPreparationId, mUserInfo, meetingBase,
                                            REQUEST_LISTEN_LESSON_CODE);
                                }else{
                                    ToastUtil.showToast(ListenDetailsActivity.this,"无法连接通讯服务器!");
                                }

                                mHandler.sendEmptyMessage(MSG_PROGRESS_DISMISS);
                            }

                            @Override
                            public void onFailure(JSONObject response) {
                                mHandler.sendEmptyMessage(MSG_PROGRESS_DISMISS);
                            }

                            @Override
                            public void onNetError() {
                                mHandler.sendEmptyMessage(MSG_PROGRESS_DISMISS);
                            }
                        });
            }

            @Override
            public void onFailure(JSONObject response) {
                mHandler.sendEmptyMessage(MSG_PROGRESS_DISMISS);
            }

            @Override
            public void onNetError() {
                mHandler.sendEmptyMessage(MSG_PROGRESS_DISMISS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LISTEN_LESSON_CODE) {
            if (resultCode == RESPONSE_LISTEN_LESSON_CODE) {
                //根据返回值 给出不同的提示 .
                String status = data.getStringExtra(TipProgressFragment.ARG_TIP_STATUS_TYPE);
                if (status != null) {
                    switch (status) {
                        case TipProgressFragment.UNSTART_STATUS_TIP://has't start .
                            mStateTv.setText(getResources().getString(R.string.lesson_unstart));
                            break;
                        case TipProgressFragment.END_STATUS_TIP://meeting has over .
                            mStateTv.setText(getResources().getString(R.string.lesson_end));
                            break;
                        case TipProgressFragment.OUT_STATUS_TIP://be kick out from the meeting .
                            mStateTv.setText(STATUS_MEETING_OUT);
                            Intent intent = new Intent();
                            intent.putExtra(REQUEST_LISTEN_OUT, "001");
                            this.setResult(1, intent);
                            this.finish();
                            break;
                        case TipProgressFragment.LOADED_STATUS_TIP://login on other devices .
                            //mVideoStatusTv.setText("已登录");
                            break;
                    }
                    TipProgressFragment fragment = TipProgressFragment.newInstance(status);
                    fragment.show(getSupportFragmentManager(), "showtips");

                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_state:
                if (mStateTv.getText().equals(getResources().getString(R.string.lesson_unstart))) {
                    TipProgressFragment tipProgressFragment = TipProgressFragment.newInstance(TipProgressFragment.UNSTART_STATUS_TIP);
                    tipProgressFragment.show(getSupportFragmentManager(), "");
                    return;
                }
                if (mStateTv.getText().equals(getResources().getString(R.string.lesson_into_listen))) {
                    actionClick(MeetingBase.BASE_MEET_ROLE_2 + "");
                    return;
                }
                if (mStateTv.getText().equals(getResources().getString(R.string.lesson_view))) {
                    actionClick(MeetingBase.BASE_MEET_ROLE_3 + "");
                    return;
                }
                if (mStateTv.getText().equals(getResources().getString(R.string.lesson_on))) {
                    if (!mDetailEntity.getUserType().equals("0")) {
                        actionClick(MeetingBase.BASE_MEET_ROLE_2 + "");
                        return;
                    }
                }
                if (mStateTv.getText().equals(getResources().getString(R.string.lesson_end))) {
                    TipProgressFragment tipProgressFragment = TipProgressFragment.newInstance(TipProgressFragment.END_STATUS_TIP);
                    tipProgressFragment.show(getSupportFragmentManager(), "");
                    return;
                }
                if (mStateTv.getText().equals(getResources().getString(R.string.lesson_view_video))) {
                    Intent intent = new Intent(ListenDetailsActivity.this, ActivityThemeVideoActivity.class);
                    intent.putExtra(Constants.PREPARATIONID, mPreparationId);
                    intent.putExtra(Constants.USER_INFO, mUserInfo);
                    intent.putExtra(Constants.TYPE_LESSON, Constants.TYPE_INTERACT_LESSON);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    public static void start(Activity from, String preparationId) {
        Intent intent = new Intent(from, ListenDetailsActivity.class);
        intent.putExtra(Constants.PREPARATIONID, preparationId);
        from.startActivity(intent);
        UIUtils.addEnterAnim(from);
    }
}
