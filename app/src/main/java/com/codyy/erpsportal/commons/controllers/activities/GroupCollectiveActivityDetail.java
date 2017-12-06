package com.codyy.erpsportal.commons.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.TipProgressFragment;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsDetailEntity;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 圈组-集体备课详情
 * Created by kmdai on 16-4-11.
 */
public class GroupCollectiveActivityDetail extends ToolbarActivity implements View.OnClickListener {

    public final static String COLLECTIVE_ID = "collective_id";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextView;
    @Bind(R.id.tv_active_name)
    TextView mTitleTV;
    @Bind(R.id.tv_active_start_time)
    TextView mStartTimeTV;
    @Bind(R.id.group_collective_et_textview)
    TextView mETTimeTV;
    @Bind(R.id.group_collective_collective_enter)
    TextView mEnterTV;
    @Bind(R.id.group_collective_collective_cont)
    TextView mContentTV;
    @Bind(R.id.group_collective_main_people)
    TextView mMainName;
    @Bind(R.id.group_collective_collective_join)
    TextView mTeachersTV;
    private RequestSender mRequestSender;
    private UserInfo mUserInfo;
    private String mPreparationId;
    private PrepareLessonsDetailEntity mPrepareLessonsDetailEntity;
    private LoadingDialog mLoadingDialog;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_group_collective;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCollectiveInfo();
        mLoadingDialog.show(getSupportFragmentManager(), "GroupCollectiveActivityDetail");
    }

    @Override
    protected void initToolbar() {
        initToolbar(mToolbar);
        mUserInfo   =   getIntent().getParcelableExtra(Constants.USER_INFO);
        if(null == mUserInfo){
            mUserInfo = UserInfoKeeper.obtainUserInfo();
        }
        mTextView.setText("集体备课详情");
        mRequestSender = new RequestSender(this);
        mPreparationId = getIntent().getStringExtra("collective_id");
        mLoadingDialog = LoadingDialog.newInstance(true);
    }

    private void getCollectiveInfo() {
        Map<String, String> parm = new HashMap<>();
        parm.put("uuid", mUserInfo.getUuid());
        parm.put("preparationId", mPreparationId);
        mRequestSender.sendGetRequest(new RequestSender.RequestData(URLConfig.GET_PREPARATION_DETAIL, parm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mLoadingDialog.dismiss();
                mPrepareLessonsDetailEntity = PrepareLessonsDetailEntity.parseJson(response, Constants.TYPE_PREPARE_LESSON);
                setData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                mLoadingDialog.dismiss();
            }
        }));
    }

    private void setData() {
        mTitleTV.setText(mPrepareLessonsDetailEntity.getData().getTitle().equals("null") ? "暂无主题" : mPrepareLessonsDetailEntity.getData().getTitle());
        mMainName.setText("主备人:" + mPrepareLessonsDetailEntity.getData().getSponsorName());
        String startDate = mPrepareLessonsDetailEntity.getData().getStartDate() != null && !mPrepareLessonsDetailEntity.getData().getStartDate().equals("null") ? mPrepareLessonsDetailEntity.getData().getStartDate() : "无";
        String endDate = mPrepareLessonsDetailEntity.getData().getFinishDate() != null && !mPrepareLessonsDetailEntity.getData().getFinishDate().equals("null") ? mPrepareLessonsDetailEntity.getData().getFinishDate() : "无";
        mStartTimeTV.setText(startDate + " 至 " + endDate);
        mETTimeTV.setText("预计时长:" + getData(mPrepareLessonsDetailEntity.getData().getDuration()));
        mContentTV.setText(mPrepareLessonsDetailEntity.getData().getDescription().equals("nill") ? "暂无备课内容!" : mPrepareLessonsDetailEntity.getData().getDescription());
        String name = "";
        for (PrepareLessonsDetailEntity.MeetMember meetMember : mPrepareLessonsDetailEntity.getMeetMembers()) {
            name += meetMember.getRealName() + "，";
        }
        mTeachersTV.setText(name);
        mEnterTV.setOnClickListener(this);
        if (mPrepareLessonsDetailEntity.getData().getStatus().equals("INIT")) { //集体备课未开始
            mEnterTV.setText(getResources().getString(R.string.lesson_unstart));
        } else if (mPrepareLessonsDetailEntity.getData().getStatus().equals("PROGRESS")) { //集体备课进行中
            if (mPrepareLessonsDetailEntity.getUserType().equals("2") || mPrepareLessonsDetailEntity.getUserType().equals("1") || mPrepareLessonsDetailEntity.getUserType().equals("4")) {//参会者
                mEnterTV.setText(getResources().getString(R.string.lesson_into_prepare));
            } else if (mPrepareLessonsDetailEntity.getUserType().equals("3")) {//发起者
                mEnterTV.setText(getResources().getString(R.string.lesson_view));
            } else if (mPrepareLessonsDetailEntity.getUserType().equals("5")) {//其他人
                mEnterTV.setText(getResources().getString(R.string.lesson_view));
            } else if (mPrepareLessonsDetailEntity.getUserType().equals("0")) {//主讲人
                mEnterTV.setText(getResources().getString(R.string.lesson_on));
            }
        } else { //集体备课已结束
            if (mPrepareLessonsDetailEntity.getHasVideo()) {
                mEnterTV.setText(getResources().getString(R.string.lesson_view_video));
            } else {
                mEnterTV.setText(getResources().getString(R.string.lesson_end));
            }
        }
    }

    private String getData(long date) {
        if (date <= 0) {
            return "未知";
        }
        long h = date / 60;
        long m = date % 60;
        if (h > 0) {
            return h + "小时" + m + "分钟";
        }
        return m + "分钟";
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop();
        super.onDestroy();
    }

    public static void start(Context context, String id) {
        Intent intent = new Intent(context, GroupCollectiveActivityDetail.class);
        intent.putExtra(COLLECTIVE_ID, id);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (UserInfo.USER_TYPE_PARENT.equals(mUserInfo.getUserType()) || UserInfo.USER_TYPE_STUDENT.equals(mUserInfo.getUserType())) {
            if (mEnterTV.getText().equals(getResources().getString(R.string.lesson_view_video))) {
                Intent intent = new Intent(GroupCollectiveActivityDetail.this, ActivityThemeVideoActivity.class);
                intent.putExtra(Constants.PREPARATIONID, mPreparationId);
                intent.putExtra(Constants.USER_INFO, mUserInfo);
                intent.putExtra(Constants.PREPARE_DATA, mPrepareLessonsDetailEntity);
                intent.putExtra(Constants.TYPE_LESSON, Constants.TYPE_PREPARE_LESSON);
                intent.putExtra(ActivityThemeVideoActivity.FROM_GROUP, true);
                startActivity(intent);
            } else {
                ToastUtil.showToast(this, "不能进入集体备课");
            }
        } else {
            if (mEnterTV.getText().equals(getResources().getString(R.string.lesson_unstart))) {
                TipProgressFragment tipProgressFragment = TipProgressFragment.newInstance(TipProgressFragment.UNSTART_STATUS_TIP);
                tipProgressFragment.show(getSupportFragmentManager(), "");
                return;
            } else if (mEnterTV.getText().equals(getResources().getString(R.string.lesson_into_prepare))) {
                actionClick(MeetingBase.BASE_MEET_ROLE_2 + "");
                return;
            } else if (mEnterTV.getText().equals(getResources().getString(R.string.lesson_view))) {
                actionClick(MeetingBase.BASE_MEET_ROLE_3 + "");
                return;
            } else if (mEnterTV.getText().equals(getResources().getString(R.string.lesson_on))) {
                if (!mPrepareLessonsDetailEntity.getUserType().equals("0")) {
                    actionClick(MeetingBase.BASE_MEET_ROLE_2 + "");
                    return;
                }
            } else if (mEnterTV.getText().equals(getResources().getString(R.string.lesson_end))) {
                TipProgressFragment tipProgressFragment = TipProgressFragment.newInstance(TipProgressFragment.END_STATUS_TIP);
                tipProgressFragment.show(getSupportFragmentManager(), "");
                return;
            } else if (mEnterTV.getText().equals(getResources().getString(R.string.lesson_view_video))) {
                Intent intent = new Intent(GroupCollectiveActivityDetail.this, ActivityThemeVideoActivity.class);
                intent.putExtra(Constants.PREPARATIONID, mPreparationId);
                intent.putExtra(Constants.USER_INFO, mUserInfo);
                intent.putExtra(Constants.PREPARE_DATA, mPrepareLessonsDetailEntity);
                intent.putExtra(Constants.TYPE_LESSON, Constants.TYPE_PREPARE_LESSON);
                intent.putExtra(ActivityThemeVideoActivity.FROM_GROUP, true);
                startActivity(intent);
            }
        }
    }

    //跳转到视频会议
    public void actionClick(String role) {
        //show progress dialog
        if (null == mLoadingDialog) {
            mLoadingDialog.show(getSupportFragmentManager(), "GroupCollectiveActivityDetail");
        }
        //1.去获取视频会议基本信息
        UiOnlineMeetingUtils.loadMeetingBaseData(getSupportFragmentManager(), this, mUserInfo.getUuid(), mPreparationId, role, new UiOnlineMeetingUtils.ICallback() {
            @Override
            public void onSuccess(JSONObject response) {

                final MeetingBase meetingBase = MeetingBase.parseJson(response);
                UiOnlineMeetingUtils.loadCocoInfo(GroupCollectiveActivityDetail.this, meetingBase.getBaseMeetID(),
                        mUserInfo.getUuid(), new UiOnlineMeetingUtils.ICallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                JSONObject server = response.optJSONObject("server");
                                if(null != server){
                                    meetingBase.setToken(server.optString("token"));
                                    meetingBase.getBaseCoco().setCocoIP(server.optString("serverHost"));
                                    meetingBase.getBaseCoco().setCocoPort(server.optString("port"));
                                    OnlineMeetingActivity.startForResult(GroupCollectiveActivityDetail.this,
                                            mPreparationId, mUserInfo,
                                            meetingBase, 101);
                                }else{
                                    ToastUtil.showToast(GroupCollectiveActivityDetail.this,"无法连接通讯服务器!");
                                }
//                                mHandler.sendEmptyMessage(MSG_PROGRESS_DISMISS);
                                mLoadingDialog.dismiss();
                            }

                            @Override
                            public void onFailure(JSONObject response) {
                                mLoadingDialog.dismiss();
                            }

                            @Override
                            public void onNetError() {
                                mLoadingDialog.dismiss();
                            }
                        });
            }

            @Override
            public void onFailure(JSONObject response) {
                //                mProgressBar.dismiss();
                //do someting ... except dialog .
                mLoadingDialog.dismiss();
            }

            @Override
            public void onNetError() {
                mLoadingDialog.dismiss();
            }
        });
    }
}
