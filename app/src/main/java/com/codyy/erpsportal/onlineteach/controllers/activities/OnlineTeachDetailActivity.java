package com.codyy.erpsportal.onlineteach.controllers.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.TipProgressFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.DocListViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.MainOrReceiveSchoolViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.RecorderPriorityViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.FixedRecyclerView;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlineteach.models.entities.NetDetailParse;
import com.codyy.erpsportal.onlineteach.models.entities.NetDocument;
import com.codyy.erpsportal.onlineteach.models.entities.NetMemberView;
import com.codyy.erpsportal.onlineteach.models.entities.NetParticipator;
import com.codyy.erpsportal.onlineteach.models.entities.NetPermission;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;

/**
 * 网络授课-详情
 * Created by poe on 16-6-22.
 */
public class OnlineTeachDetailActivity extends BaseHttpActivity implements View.OnClickListener,Handler.Callback{
    private final String TAG = "OnlineTeachDetailActivity";
    public static final int MSG_PROGRESS_DISMISS = 200;//进度条消失 ...
    public static int REQUEST_LISTEN_LESSON_CODE = 201;
    public static int RESPONSE_LISTEN_LESSON_CODE = 202;
    public static final String STATUS_MEETING_OUT = "已被请出";
    public static final String REQUEST_LISTEN_OUT = "outmeetingResult";//标识：被踢出会议时，要返回列表界面，并刷新列表
    @Bind(R.id.empty_view) EmptyView mEmptyView;
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.tv_launcher)TextView mLaunchTv;//发起方
    @Bind(R.id.tv_launch_time)TextView mTimeTv;//发起时间
    @Bind(R.id.tv_active_name)TextView mActiveNameTv;//活动标题
    @Bind(R.id.tv_grade_name)TextView mGradeNameTv;//年级
    @Bind(R.id.tv_subject_name)TextView mSubjectNameTv;//学科
    @Bind(R.id.tv_active_msg_value)TextView mActiveMagValueTv;//活动简介
    @Bind(R.id.tv_speaker_school)TextView mMainSchoolTitleTv;//标题－主讲学校
    @Bind(R.id.tv_prepare_people_name_text) TextView mMainTeacherTitleTv;//标题－主讲教师
    @Bind(R.id.tv_main_school_name)TextView mMainSchoolNameTv;//主讲学校
    @Bind(R.id.tv_prepare_lesson_room_text)TextView mPrepareLessonRoomDescTv;//主讲学校－教室描述
    @Bind(R.id.tv_prepare_lesson_room)TextView mPrepareLessonRoomTv;//主讲教室
    @Bind(R.id.tv_prepare_people_name)TextView mPreparePeopleNameTv;//主讲人
    @Bind(R.id.tv_listen_people_name_text)TextView mListenTeacherDescTv;//主讲学校－听课教师　ｄｅｓｃ
    @Bind(R.id.tv_listen_people_name)TextView mListenTeacherTv;//主讲学校－听课教师
    @Bind(R.id.tv_listen_student)TextView mListenStudentTv;//主讲学校－听课学生
    @Bind(R.id.tv_reserve_start_time)TextView mReserveStartTimeTv;//预约开始时间
    @Bind(R.id.tv_reserve_end_time)TextView mReserveEndTimeTv;//预约结束时间
    @Bind(R.id.tv_real_start_time)TextView mRealStartTimeTv;//开始时间
    @Bind(R.id.tv_state)TextView mStateTv;//操作按钮  进入/观看视频/未开始
    @Bind(R.id.rl_state)RelativeLayout mRlState;
    @Bind(R.id.rb_star)RatingBar mRbStar;//评分
    @Bind(R.id.divider5)View mReceiveDivider ;//接收学校divider
    @Bind(R.id.divider7)View mPriorityDivider ;//有录制权限divider
    @Bind(R.id.divider8)View mDocDivider ;//文档divider
    @Bind(R.id.tv_receive_school_desc)TextView mReceiveSchoolDesc;
    @Bind(R.id.tv_document_desc)TextView mDocumentDesc;
    @Bind(R.id.lv_document)FixedRecyclerView mDocRecyclerView;
    @Bind(R.id.lv_receive_school)FixedRecyclerView mReceiveRecyclerView;
    @Bind(R.id.lv_priority)FixedRecyclerView mPriorityRecyclerView;

    private String mPreparationId;
    private BaseRecyclerAdapter<NetParticipator ,MainOrReceiveSchoolViewHolder> mReceiveAdapter;
    private BaseRecyclerAdapter<NetPermission,RecorderPriorityViewHolder> mPriorityAdapter;
    private BaseRecyclerAdapter<NetDocument,DocListViewHolder> mDocAdapter;
    private NetDetailParse mNetTeachDetail ;
    private List<NetPermission> mPriorityList = new ArrayList<>();
    private List<NetDocument> mDocList = new ArrayList<>();//相关文档列表
    private List<NetParticipator> participatorItems = new ArrayList<>();//听课端列表
    private ProgressDialog mProgressBar;
    private Handler mHandler = new Handler(this);

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_online_teach_detail;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_NET_TEACH_DETAIL;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("mid", mPreparationId);//"2b10394a9fc3440691280a3ac96a26b2");//mPreparationId
        return params;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG, "onResponse:" + response);
        if(mEmptyView == null) return;
        mEmptyView.setVisibility(View.GONE);
        mEmptyView.setLoading(false);
        mNetTeachDetail = new Gson().fromJson(response.toString() , NetDetailParse.class);
        refreshUI();
    }

    private void refreshUI() {
        if(null == mNetTeachDetail) return;
        mActiveNameTv.setText(mNetTeachDetail.getData().getMeetingTitle());
        mActiveMagValueTv.setText(mNetTeachDetail.getData().getMeetingDescription());
        mLaunchTv.setText(mNetTeachDetail.getData().getCreateRealName());
        mRbStar.setMax(100);//#bug fix: 11579 Android5.3.3补充版本-网络首页详情页，评分显示不正确 70分显示5颗星
        mRbStar.setProgress(mNetTeachDetail.getData().getIntRatingAverage());
        mGradeNameTv.setText(mNetTeachDetail.getData().getBaseClasslevelName());
        mSubjectNameTv.setText(mNetTeachDetail.getData().getBaseSubjectName());

        mReserveStartTimeTv.setText(mNetTeachDetail.getData().getStrBeginTime());
        mReserveEndTimeTv.setText(mNetTeachDetail.getData().getStrEndTime());

        mTimeTv.setText(mNetTeachDetail.getData().getStrCreateTime());
        if (mNetTeachDetail.getData().getStrRealBeginTime().equals("null")|| TextUtils.isEmpty(mNetTeachDetail.getData().getStrRealBeginTime())) {
            mRealStartTimeTv.setText(getResources().getString(R.string.lesson_unstart));
        } else {
            mRealStartTimeTv.setText(mNetTeachDetail.getData().getStrRealBeginTime());
        }

        if (mNetTeachDetail.getMasterSchool().isSelfSchool()) {
            mMainSchoolNameTv.setText(mNetTeachDetail.getMasterSchool().getSchoolName() + "(本校)");
        } else {
            mMainSchoolNameTv.setText(mNetTeachDetail.getMasterSchool().getSchoolName());
        }
        if(null !=mNetTeachDetail.getMasterSchool().getRoomName() &&!TextUtils.isEmpty(mNetTeachDetail.getMasterSchool().getRoomName().trim())){
            mPrepareLessonRoomDescTv.setVisibility(View.VISIBLE);
            mPrepareLessonRoomTv.setVisibility(View.VISIBLE);
            mPrepareLessonRoomTv.setText(mNetTeachDetail.getMasterSchool().getRoomName());
        }else{
            mPrepareLessonRoomDescTv.setVisibility(View.GONE);
            mPrepareLessonRoomTv.setVisibility(View.GONE);
        }
        mPreparePeopleNameTv.setText(mNetTeachDetail.getMasterSchool().getMainMemberName());
        //听课学生
        StringBuffer studentBuffer = new StringBuffer();
        StringBuffer teacherBuffer = new StringBuffer();
        if(mNetTeachDetail.getMasterSchool()!=null && mNetTeachDetail.getMasterSchool().getReceiveMemberViewList() != null){
            List<NetMemberView> viewList = mNetTeachDetail.getMasterSchool().getReceiveMemberViewList();
            for(int i= 0 ;i < viewList.size();i++){

                if("STUDENT".equals(viewList.get(i).getMemberType())){//听课学生
                    if(!TextUtils.isEmpty(studentBuffer)){
                        studentBuffer.append("、");
                    }
                    studentBuffer.append(viewList.get(i).getRealName());
                }else if("TEACHER".equals(viewList.get(i).getMemberType())){//听课教师
                    if(!TextUtils.isEmpty(teacherBuffer)){
                        teacherBuffer.append("、");
                    }
                    teacherBuffer.append(viewList.get(i).getRealName());
                }


            }
        }
        mListenStudentTv.setText(studentBuffer.toString());
        if(!TextUtils.isEmpty(teacherBuffer)){
            mListenTeacherDescTv.setVisibility(View.VISIBLE);
            mListenTeacherTv.setText(teacherBuffer.toString());
        }else{
            mListenTeacherDescTv.setVisibility(View.GONE);
        }

        NetPermission rp = new NetPermission();
        rp.setSchoolName(mNetTeachDetail.getData().getMainSchoolName());
        rp.setClassName(mNetTeachDetail.getData().getMainSpeakerUserName());
        rp.setHasPermission(true);
        mPriorityList.add(rp);

        if(null != mNetTeachDetail.getPermission().getSchoolName() && !TextUtils.isEmpty(mNetTeachDetail.getPermission().getSchoolName().trim())){
            mPriorityList.add(mNetTeachDetail.getPermission());
        }
        mPriorityAdapter.setData(mPriorityList);

        //相关文档
        if(null != mNetTeachDetail.getInterrelatedDoc()){
            mDocList.addAll(mNetTeachDetail.getInterrelatedDoc());
        }
        if(mDocList == null || mDocList.size() == 0){
            //隐藏 -文档
            mDocDivider.setVisibility(View.GONE);
            mDocumentDesc.setVisibility(View.GONE);
            mDocRecyclerView.setVisibility(View.GONE);
        }else{
            mDocAdapter.setData(mDocList);
        }

        //接收学校
        if(null != mNetTeachDetail.getParticipator()){
            participatorItems.addAll(mNetTeachDetail.getParticipator());
        }
        if(participatorItems == null || participatorItems.size() == 0){
            //隐藏-接收学校
            mReceiveDivider.setVisibility(View.GONE);
            mReceiveSchoolDesc.setVisibility(View.GONE);
            mReceiveRecyclerView.setVisibility(View.GONE);
        }else{
            mReceiveAdapter.setData(participatorItems);
        }


        if (mNetTeachDetail.getData().getStatus().equals("INIT")) {
            mStateTv.setText(getResources().getString(R.string.lesson_unstart));
        } else if (mNetTeachDetail.getData().getStatus().equals("PROGRESS")) {
            if (mNetTeachDetail.getUserType().equals("2") || mNetTeachDetail.getUserType().equals("1") || mNetTeachDetail.getUserType().equals("4")) {
                mStateTv.setText(getResources().getString(R.string.lesson_into));
            } else if (mNetTeachDetail.getUserType().equals("3")) {
                mStateTv.setText(getResources().getString(R.string.lesson_view));
            } else if (mNetTeachDetail.getUserType().equals("5")) {
                mStateTv.setText(getResources().getString(R.string.lesson_view));
            } else if (mNetTeachDetail.getUserType().equals("0")) {
                mStateTv.setText(getResources().getString(R.string.lesson_on));
            }else{//其他非法状态不让进入
                mStateTv.setText(getResources().getString(R.string.lesson_view));
            }
        } else {
            if (mNetTeachDetail.isHasVideo()) {
                mStateTv.setText(getResources().getString(R.string.lesson_view_video));
            } else {
                mStateTv.setText(getResources().getString(R.string.lesson_end));
            }
        }

    }

    @Override
    public void onFailure(Throwable error) {
        Cog.e(TAG, "onErrorResponse:" + error);
        if(mEmptyView == null) return;
        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(false);
    }

    @Override
    public void init() {
        mPreparationId = getIntent().getStringExtra(Constants.PREPARATIONID);
        initToolbar(mToolBar);

        mTitleTextView.setText(Titles.sWorkspaceNetTeach+"详情");
        //主讲学校
        mMainSchoolTitleTv.setText(Titles.sMasterSchool);
        //主讲教师
        mMainTeacherTitleTv.setText(Titles.sMasterTeacher);

        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                requestData(true);
            }
        });
        mRlState.setOnClickListener(this);
        mReceiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                return new MainOrReceiveSchoolViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_receive_school));
            }

            @Override
            public int getItemViewType(int position) {
                return MainOrReceiveSchoolViewHolder.ITEM_TYPE_RECEIVE_SCHOOL;
            }
        });
        mReceiveRecyclerView.setAdapter(mReceiveAdapter);
        mDocAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<DocListViewHolder>() {
            @Override
            public DocListViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new DocListViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_resource_prepare_lseeons));
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
                return new RecorderPriorityViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_resource_prepare_lseeons));
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
                final JSONObject jsonObject = response;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MeetingBase meetingBase = MeetingBase.parseJson(jsonObject);
                        OnlineMeetingActivity.startForResult(OnlineTeachDetailActivity.this, mPreparationId,mUserInfo,meetingBase,REQUEST_LISTEN_LESSON_CODE);
                        mHandler.sendEmptyMessage(MSG_PROGRESS_DISMISS);
                    }
                }).start();
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
                            Intent intent =  new Intent();
                            intent.putExtra(REQUEST_LISTEN_OUT,"001");
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
                if (mStateTv.getText().equals(getResources().getString(R.string.lesson_into))) {
                    actionClick(MeetingBase.BASE_MEET_ROLE_2 + "");
                    return;
                }
                if (mStateTv.getText().equals(getResources().getString(R.string.lesson_view))) {
                    actionClick(MeetingBase.BASE_MEET_ROLE_3 + "");
                    return;
                }
                if (mStateTv.getText().equals(getResources().getString(R.string.lesson_on))) {
                    if (!mNetTeachDetail.getUserType().equals("0")) {
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
                    NetTechVideoActivity.start(OnlineTeachDetailActivity.this ,mPreparationId);//mPreparationId
                }
                break;
            default:
                break;
        }
    }

    public static void start(Context context , String id){
        Intent intent = new Intent(context ,OnlineTeachDetailActivity.class);
        intent.putExtra(Constants.PREPARATIONID , id );
        context.startActivity(intent);
    }
}
