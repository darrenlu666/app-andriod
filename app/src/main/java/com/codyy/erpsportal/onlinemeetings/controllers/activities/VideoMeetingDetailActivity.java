package com.codyy.erpsportal.onlinemeetings.controllers.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.TipProgressFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.DocListViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.PartnerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.ProductListViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.RecorderPriorityViewHolder;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.RecyclerView.FixedRecyclerView;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlinemeetings.models.entities.VideoMeetingDetailEntity;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 视频会议详情
 * by ldh 2015-07-31
 * modified by poe 2016-07-06
 */
public class VideoMeetingDetailActivity extends BaseHttpActivity {
    private static final String TAG = VideoMeetingDetailActivity.class.getSimpleName();

    @Bind(R.id.empty_view) EmptyView mEmptyView;
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.tv_launcher)TextView mLaunchTv;//发起方
    @Bind(R.id.tv_launch_time)TextView mLaunchTimeTv;//发起时间
    @Bind(R.id.tv_active_name)TextView mActiveNameTv;//活动标题
    @Bind(R.id.tv_reserve_start_time)TextView mReserveStartTimeTv;//预约开始时间
    @Bind(R.id.tv_reserve_end_time)TextView mReserveEndTimeTv;//预约结束时间
    @Bind(R.id.tv_real_start_time)TextView mRealStartTimeTv;//开始时间
    @Bind(R.id.tv_active_msg_value)TextView mActiveMagValueTv;//活动简介
    @Bind(R.id.tv_main_school_name)TextView mMainSchoolNameTv;//主讲学校
    @Bind(R.id.tv_main_room_text)TextView mPrepareLessonRoomDescTv;
    @Bind(R.id.tv_main_room)TextView mPrepareLessonRoomTv;//主会场
    @Bind(R.id.tv_prepare_people_name)TextView mPreparePeopleNameTv;//主持人
    @Bind(R.id.tv_prepare_attend_text)TextView mAttendDescTv;//参会者描述
    @Bind(R.id.tv_prepare_attend)TextView mAttendPeopleTv;//参会者
    @Bind(R.id.lv_receive_school)FixedRecyclerView mReceiveRecyclerView;//参与机构
    @Bind(R.id.tv_state)TextView mStateTv;//操作按钮  进入/观看视频/未开始
    @Bind(R.id.rl_state)RelativeLayout mStateRelativeLayout;
    @Bind(R.id.divider5)View mReceiveDivider ;//参与机构divider
    @Bind(R.id.divider6)View mPriorityDivider ;//有录制权限divider
    @Bind(R.id.divider7)View mDocDivider ;//文档divider
    @Bind(R.id.tv_receive_school_desc)TextView mReceiveSchoolDesc;
    @Bind(R.id.tv_document_desc)TextView mDocumentDesc;
    @Bind(R.id.lv_document)FixedRecyclerView mDocRecyclerView;
    @Bind(R.id.lv_priority)FixedRecyclerView mPriorityRecyclerView;
    @Bind(R.id.tv_product)TextView mProductTextView;//备课成果
    @Bind(R.id.lv_product)FixedRecyclerView mProductRecyclerView;

    private VideoMeetingDetailEntity mVideoMeetingDetailEntity;//实体对象
    private BaseRecyclerAdapter<VideoMeetingDetailEntity.ParticipatorEntity ,PartnerViewHolder> mReceiveAdapter;
    private BaseRecyclerAdapter<BaseTitleItemBar,RecorderPriorityViewHolder> mPriorityAdapter;
    private BaseRecyclerAdapter<String,DocListViewHolder> mDocAdapter;
    private BaseRecyclerAdapter<String , ProductListViewHolder> mProductAdapter;
    private List<String> mAchievementList = new ArrayList<>();//会议成果集合
    private List<VideoMeetingDetailEntity.ParticipatorEntity> mParticipatorList = new ArrayList<>();//参与机构
    private List<String> mDocList = new ArrayList<>();//相关文档列表
    private List<BaseTitleItemBar> mPriorityList = new ArrayList<>();
    private ProgressDialog mProgressBar;
    private String mMeetingID;//会议id
    public static int REQUEST_ONLINE_MEETING_CODE = 301;
    public static int RESPONSE_ONLINE_MEETING_CODE = 302;
    public static final String OUT_MEETING_RESULT = "out.meeting.result";//标识：被踢出会议时，要返回会议列表界面，并刷新会议列表
    public static final String STATUS_MEETING_OUT = "已被请出";

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_video_meeting_detail;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_VIDEOMEETING_DETAIL;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo == null ? "" : mUserInfo.getUuid());
        params.put("mid", mMeetingID);
        return params;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        if(null == mEmptyView) return;
        mEmptyView.setVisibility(View.GONE);
        mVideoMeetingDetailEntity = VideoMeetingDetailEntity.parseJsonObject(response);
        mLaunchTimeTv.setText(mVideoMeetingDetailEntity.getData().getSponsorDate());
        mLaunchTv.setText(mVideoMeetingDetailEntity.getData().getSponsorName());
        mActiveNameTv.setText(mVideoMeetingDetailEntity.getData().getTitle());
        mReserveStartTimeTv.setText(mVideoMeetingDetailEntity.getData().getBeginDateTime());
        mReserveEndTimeTv.setText(mVideoMeetingDetailEntity.getData().getEndDate());
        if(null == mVideoMeetingDetailEntity.getData().getStartDate() || TextUtils.isEmpty(mVideoMeetingDetailEntity.getData().getStartDate().trim())){
            mRealStartTimeTv.setText("未开始");
        }else{
            mRealStartTimeTv.setText(mVideoMeetingDetailEntity.getData().getStartDate());
        }
        if (!mVideoMeetingDetailEntity.getData().getDescription().equals("null")) {
            mActiveMagValueTv.setText(mVideoMeetingDetailEntity.getData().getDescription());
        }
        //主持机构
        if((mVideoMeetingDetailEntity.getMasterSchool().isSelfSchool())){
            mMainSchoolNameTv.setText(mVideoMeetingDetailEntity.getMasterSchool().getSchoolName() + "(本校)");
        }else{
            mMainSchoolNameTv.setText(mVideoMeetingDetailEntity.getMasterSchool().getSchoolName());
        }
        if(TextUtils.isEmpty(mVideoMeetingDetailEntity.getMasterSchool().getMasterclassroom())){
            //主会场为空
            mPrepareLessonRoomDescTv.setVisibility(View.GONE);
            mPrepareLessonRoomTv.setVisibility(View.GONE);
        }else{
            mPrepareLessonRoomTv.setText(mVideoMeetingDetailEntity.getMasterSchool().getMasterclassroom());
        }
        mPreparePeopleNameTv.setText(mVideoMeetingDetailEntity.getMasterSchool().getMasterteacher());
        if(TextUtils.isEmpty(mVideoMeetingDetailEntity.getMasterSchool().getParticipator())){
            //参会者为空
            mAttendDescTv.setVisibility(View.GONE);
            mAttendPeopleTv.setVisibility(View.GONE);
        }else{
            mAttendPeopleTv.setText(mVideoMeetingDetailEntity.getMasterSchool().getParticipator());
        }
        //参与机构
        if (mVideoMeetingDetailEntity.getParticipator().size() > 0) {
            mParticipatorList.addAll(mVideoMeetingDetailEntity.getParticipator());
            mReceiveAdapter.setData(mParticipatorList);
        } else{
            mReceiveDivider.setVisibility(View.GONE);
            mReceiveSchoolDesc.setVisibility(View.GONE);
            mReceiveRecyclerView.setVisibility(View.GONE);
        }
        //拥有录制权限 .
        VideoMeetingDetailEntity.PermissionEntity rp = new VideoMeetingDetailEntity.PermissionEntity();
        rp.setHasPermission(true);
        rp.setSchoolName(mVideoMeetingDetailEntity.getMasterSchool().getSchoolName());
        rp.setTeachName(mVideoMeetingDetailEntity.getMasterSchool().getMasterteacher());
        mPriorityList.add(rp);

        if(mVideoMeetingDetailEntity.getPermission().getSchoolName()!= null && !TextUtils.isEmpty(mVideoMeetingDetailEntity.getPermission().getSchoolName().trim())){
            mVideoMeetingDetailEntity.getPermission().setTeachName(mVideoMeetingDetailEntity.getPermission().getClassName());
            mPriorityList.add(mVideoMeetingDetailEntity.getPermission());
        }

        mPriorityAdapter.setData(mPriorityList);
        //相关文档
        List<VideoMeetingDetailEntity.InterrelatedDocEntity> listDoc = new ArrayList<>();
        List<String> docNameList = new ArrayList<>();
        listDoc = mVideoMeetingDetailEntity.getInterrelatedDoc();
        for (int i = 0; i < listDoc.size(); i++) {
            docNameList.add(listDoc.get(i).getDocumentName());
        }

        if (docNameList.size() > 0) {
            mDocList.addAll(docNameList);
            mDocAdapter.setData(mDocList);
        } else{
            mDocumentDesc.setVisibility(View.GONE);
            mDocRecyclerView.setVisibility(View.GONE);
            mDocDivider.setVisibility(View.GONE);
        }
        //会议成果
        List<VideoMeetingDetailEntity.AlternateachievementEntity> listAchive = new ArrayList<>();
        List<String> achiveNameList = new ArrayList<>();
        listAchive = mVideoMeetingDetailEntity.getAlternateachievement();
        for (int i = 0; i < listAchive.size(); i++) {
            achiveNameList.add(listAchive.get(i).getDocumentName());
        }
        if (achiveNameList.size() > 0) {
            mAchievementList.addAll(achiveNameList);
            mProductAdapter.setData(mAchievementList);
        } else{
            mProductTextView.setVisibility(View.GONE);
            mProductRecyclerView.setVisibility(View.GONE);
        }
        mStateTv.setText(parseStatus(mVideoMeetingDetailEntity.getData().getStatus()));
    }

    @Override
    public void onFailure(VolleyError error) {
        if(null == mEmptyView)  return;
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(false);
    }

    @OnClick(R.id.rl_state)
    public void actionClick() {
        if (getResources().getString(R.string.meeting_into).equals(mStateTv.getText().toString())) {
            enterOnlineMeeting(MeetingBase.BASE_MEET_ROLE_2 + "");
        } else if (getResources().getString(R.string.lesson_view).equals(mStateTv.getText().toString())) {
            enterOnlineMeeting(MeetingBase.BASE_MEET_ROLE_3 + "");
        } else if (getResources().getString(R.string.lesson_on).equals(mStateTv.getText().toString())) {
            //show progress dialog
           /* if (!mVideoMeetingDetailEntity.getUserType().equals("0"))
                enterOnlineMeeting(MeetingBase.BASE_MEET_ROLE_2 + "");*/
        } else if (getResources().getString(R.string.lesson_view_video).equals(mStateTv.getText().toString())) {
//            ActivityThemeVideoActivity.start(this, Constants.TYPE_ONLINE_MEETING, mMeetingID);
            VideoMeetingPlayActivity.start(this,"会议视频",mMeetingID);
        } else if (getResources().getString(R.string.lesson_unstart).equals(mStateTv.getText().toString())) {
            //测试
            TipProgressFragment fragment = TipProgressFragment.newInstance(TipProgressFragment.UNSTART_STATUS_TIP);
            fragment.show(getSupportFragmentManager(), "showtips");
        } else if (getResources().getString(R.string.lesson_end).equals(mStateTv.getText().toString())) {
            TipProgressFragment fragment = TipProgressFragment.newInstance(TipProgressFragment.END_STATUS_TIP);
            fragment.show(getSupportFragmentManager(), "showtips");
        }
    }

    //enter the meeting ...
    private void enterOnlineMeeting(String role) {
        Cog.d(TAG,"enter online meeting ...");
        if (null == mProgressBar) {
            mProgressBar = new ProgressDialog(this);
            mProgressBar.setIndeterminate(false);
        }
        mProgressBar.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(null != mStateRelativeLayout){
                    mStateRelativeLayout.setEnabled(true);
                }
            }
        });
        mProgressBar.setMessage(getResources().getString(R.string.tips_add_in));
        mProgressBar.show();
        if(null == mUserInfo){
            Cog.e(TAG,"用户信息为NULL ，userInfoKeepInfo ~ ");
            return;
        }

        //1.去获取视频会议基本信息
        UiOnlineMeetingUtils.loadMeetingBaseData(getSupportFragmentManager(), this, mUserInfo.getUuid(), mMeetingID, role, new UiOnlineMeetingUtils.ICallback() {
            @Override
            public void onSuccess(JSONObject response) {

                final JSONObject jsonObject = response ;
                mProgressBar.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mProgressBar.setOnDismissListener(null);
                        MeetingBase meetingBase = MeetingBase.parseJson(jsonObject);
                        // go to meeting page .
                        OnlineMeetingActivity.startForResult(VideoMeetingDetailActivity.this, mMeetingID, mUserInfo ,meetingBase, REQUEST_ONLINE_MEETING_CODE);
                    }
                });
                if (null != mProgressBar && mProgressBar.isShowing()){
                    mProgressBar.dismiss();
                }
                if(null != mStateRelativeLayout){
                    mStateRelativeLayout.setEnabled(true);
                }
            }

            @Override
            public void onFailure(JSONObject response) {
                if (null != mProgressBar && mProgressBar.isShowing()){
                    mProgressBar.dismiss();
                }
                if(null != mStateRelativeLayout){
                    mStateRelativeLayout.setEnabled(true);
                }

            }

            @Override
            public void onNetError() {
                if (null != mProgressBar && mProgressBar.isShowing()){
                    mProgressBar.dismiss();
                }
                if(null != mStateRelativeLayout){
                    mStateRelativeLayout.setEnabled(true);
                }
            }
        });
    }

    public void init() {
        //获取传递的参数
        mMeetingID = getIntent().getExtras().getString("mid");
        initToolbar(mToolBar);
        mTitleTextView.setText(Titles.sWorkspaceVidmeet + "详情");
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                requestData(true);
            }
        });
        mReceiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDocRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPriorityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setAdapter();
        showProgress();
        //get detail data .
        requestData(true);
    }

    private void setAdapter() {
        mReceiveAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<PartnerViewHolder>() {
            @Override
            public PartnerViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new PartnerViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_partner_or_grouper));
            }

            @Override
            public int getItemViewType(int position) {
                return PartnerViewHolder.ITEM_TYPE_JOIN_GROUP;
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

        mProductAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<ProductListViewHolder>() {
            @Override
            public ProductListViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new ProductListViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_resource_prepare_lseeons));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mProductRecyclerView.setAdapter(mProductAdapter);
    }

    private void showProgress() {
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(true);
    }

    private String parseStatus(String Status) {
        if (Status.equals("INIT")) {
            return getResources().getString(R.string.lesson_unstart);
        }
        if (Status.equals("PROGRESS")) {
            if (mVideoMeetingDetailEntity.getUserType().equals("0")){//主讲人
                return getResources().getString(R.string.lesson_on);
            }else if(mVideoMeetingDetailEntity.getUserType().equals("1")){//发言人
                return   getResources().getString(R.string.meeting_into);
            } else if(mVideoMeetingDetailEntity.getUserType().equals("2")){//参会者
                return   getResources().getString(R.string.meeting_into);
            }else if(mVideoMeetingDetailEntity.getUserType().equals("3")){//观摩者
                return getResources().getString(R.string.lesson_view);
            }else {
                return getResources().getString(R.string.lesson_on);
            }
        }

        if (Status.equals("END")) {
            if (mVideoMeetingDetailEntity.getHasVideo()) {
                mStateTv.setEnabled(true);
                return getResources().getString(R.string.lesson_view_video);
            } else {
                return getResources().getString(R.string.lesson_end);
            }
        }
        return "未知";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ONLINE_MEETING_CODE) {
            if (resultCode == RESPONSE_ONLINE_MEETING_CODE) {
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
                            intent.putExtra(OUT_MEETING_RESULT,"001");
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
}
