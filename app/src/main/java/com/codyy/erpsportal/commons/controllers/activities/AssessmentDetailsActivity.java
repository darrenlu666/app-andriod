package com.codyy.erpsportal.commons.controllers.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.EvaluationsFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.ParticipateSchoolViewHolder;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;
import com.codyy.erpsportal.commons.models.entities.EmumIndex;
import com.codyy.erpsportal.commons.models.entities.SchoolTeacher;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.FixedRecyclerView;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;

/**
 * 评课议课详情
 * Created by kmdai on 2015/4/20.
 * modified by poe on 2015/6/27
 */
public class AssessmentDetailsActivity extends BaseHttpActivity implements View.OnClickListener {
    public static final String TAG = "AssessmentDetailsActivity";
    /**     * 设置老师     */
    public static final int REQUEST_CODE_SET_TEACHER = 0x001;

    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.tv_launcher)TextView mLaunchTv;//发起方
    @Bind(R.id.tv_launch_time)TextView mTimeTv;//发起时间
    @Bind(R.id.tv_active_name)TextView mActiveNameTv;//活动标题
    @Bind(R.id.tv_reserve_start_time)TextView mReserveStartTimeTv;//预约开始时间
    @Bind(R.id.tv_reserve_end_time)TextView mReserveEndTimeTv;//预约结束时间
    @Bind(R.id.tv_active_profile)TextView mProfileTextView;//活动要求
    @Bind(R.id.tv_main_school_name)TextView mMainSchoolNameTv;//主讲学校
    @Bind(R.id.tv_main_speaker)TextView mMainSpeakerTv ;//主讲老师
    @Bind(R.id.tv_grade_name)TextView mGradeNameTv;//年级
    @Bind(R.id.tv_subject_name)TextView mSubjectNameTv;//学科
    @Bind(R.id.tv_schedule_name)TextView mScheduleTime;//排课日期
    @Bind(R.id.tv_real_start_time)TextView mRealStartTimeTv;//开始时间
    @Bind(R.id.tv_view_type)TextView mViewTypeTextView;//观摩方式
    @Bind(R.id.lv_participate)FixedRecyclerView mRecyclerView;//参与范围
    @Bind(R.id.assessment_details_bottom_text)TextView mStateTextView;
    @Bind(R.id.assessment_details_bottom_divider)View mVerticalDivider;
    @Bind(R.id.assessment_details_bottom_set_teacher)TextView mSetTeacherTv;
    @Bind(R.id.tv_main_speaker_desc)TextView mMasterTeacherTitleTv;//标题－主讲教师


    private int type;
    private DialogUtil dialogUtil;
    private DialogUtil dialogUtil1;
    private AssessmentDetails assessmentDetails;
    private BaseRecyclerAdapter<SchoolTeacher , ParticipateSchoolViewHolder> mAdapter;


    @Override
    public int obtainLayoutId() {
        return R.layout.assessment_details;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_EVALUATION_DETAIL;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        if (type == AssessmentClassActivity.INVITED) {
            data.put("isInvited", "Y");
        }
        data.put("evaluationId", assessmentDetails.getEvaluationId());
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        AssessmentDetails.getAssessmentDetail(response, assessmentDetails);
        setData();
    }

    @Override
    public void onFailure(Throwable error) {

    }

    /**
     * view初始化
     */
    public void init() {
        assessmentDetails = getIntent().getParcelableExtra("assessmentDetails");
        type = getIntent().getIntExtra("type", 0);
        if (assessmentDetails == null || assessmentDetails.getTitle() == null) {
            return;
        }
        initToolbar(mToolBar);
        mMasterTeacherTitleTv.setText(Titles.sMasterTeacher);
        dialogUtil = new DialogUtil(this);
        dialogUtil1 = new DialogUtil(this, left, right);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setAdapter();
        setData();
    }

    /**
     * 填充数据
     */
    private void setData() {
        mTitleTextView.setText(Titles.sWorkspaceDisucss+"详情");
        mLaunchTv.setText(assessmentDetails.getSponsorName());
        if(!TextUtils.isEmpty(assessmentDetails.getSponsorDate())){
            mTimeTv.setText(DateUtil.getDateStr(Long.valueOf(assessmentDetails.getSponsorDate()),DateUtil.DEF_FORMAT));
        }
        mActiveNameTv.setText(Html.fromHtml(assessmentDetails.getTitle()));
        if(!TextUtils.isEmpty(assessmentDetails.getStartDate())){
            mReserveStartTimeTv.setText(DateUtil.getDateStr(Long.valueOf(assessmentDetails.getStartDate()),DateUtil.DEF_FORMAT));
        }
        if(!TextUtils.isEmpty(assessmentDetails.getEndDate())){
            mReserveEndTimeTv.setText(DateUtil.getDateStr(Long.valueOf(assessmentDetails.getEndDate()),DateUtil.DEF_FORMAT));
        }
        mProfileTextView.setText(Html.fromHtml(assessmentDetails.getDescription()));
        mMainSchoolNameTv.setText(Html.fromHtml(assessmentDetails.getScheduleSchoolName()));
        mMainSpeakerTv.setText(Html.fromHtml(assessmentDetails.getMasterTeacherName()));
        mGradeNameTv.setText(Html.fromHtml(assessmentDetails.getClassLevelName()));
        mSubjectNameTv.setText(Html.fromHtml(assessmentDetails.getSubjectName()));
        if(!TextUtils.isEmpty(assessmentDetails.getScheduleDate())){
            //排课日期：　2015-1-1 星期五　第五节
            String date = DateUtil.getDateStr(Long.valueOf(assessmentDetails.getScheduleDate()),DateUtil.YEAR_MONTH_DAY);
            String week = DateUtil.getWeek(date);
            String sequence = "第"+ EmumIndex.getIndex(Integer.valueOf(assessmentDetails.getClassSeq()))+"节";

            mScheduleTime.setText(date+" "+week+" "+sequence);
        }
        if(!TextUtils.isEmpty(assessmentDetails.getRealBeginTime())){
            mRealStartTimeTv.setText(DateUtil.getDateStr(Long.valueOf(assessmentDetails.getRealBeginTime()),DateUtil.DEF_FORMAT));
        }else{
            mRealStartTimeTv.setText("无");
        }
        if ("LIVE".equals(assessmentDetails.getEvaType())) {
            mViewTypeTextView.setText("直播课堂");
        } else if ("VIDEO".equals(assessmentDetails.getEvaType())) {
            mViewTypeTextView.setText("录播课堂");
        }
        mVerticalDivider.setVisibility(View.GONE);
        mSetTeacherTv.setVisibility(View.GONE);
        mStateTextView.setOnClickListener(this);
        mSetTeacherTv.setOnClickListener(this);
        if ("INIT".equals(assessmentDetails.getStatus())) {
            mStateTextView.setText("未开始");
            Drawable drawable = getResources().getDrawable(R.drawable.xxhdpi_init_);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mStateTextView.setCompoundDrawables(drawable, null, null, null);
        } else if ("PROGRESS".equals(assessmentDetails.getStatus())) {
            mStateTextView.setText("进入评课");
            Drawable drawable = getResources().getDrawable(R.drawable.xxhdpi_assessmenting);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mStateTextView.setCompoundDrawables(drawable, null, null, null);
        } else if ("END".equals(assessmentDetails.getStatus())) {
            mStateTextView.setText("查看结果");
            Drawable drawable = getResources().getDrawable(R.drawable.xxhdpi_look_d);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mStateTextView.setCompoundDrawables(drawable, null, null, null);
        } else if ("WAIT".equals(assessmentDetails.getStatus())) {//待处理
            if ("SCHOOL_USR".equals(mUserInfo.getUserType())) {
                mSetTeacherTv.setVisibility(View.VISIBLE);
                mVerticalDivider.setVisibility(View.VISIBLE);
            }
            Drawable drawable = getResources().getDrawable(R.drawable.xxhdpi_ini_stop);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mStateTextView.setCompoundDrawables(drawable, null, null, null);
            mStateTextView.setText("拒绝");
        } else if ("TIMEOUT".equals(assessmentDetails.getStatus())) {
            Drawable drawable = getResources().getDrawable(R.drawable.xxhdpi_evluation_timeout);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mStateTextView.setCompoundDrawables(drawable, null, null, null);
            mStateTextView.setText("已过期");
        } else if ("REJECT".equals(assessmentDetails.getStatus())) {
            Drawable drawable = getResources().getDrawable(R.drawable.xxhdpi_evluation_reject);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mStateTextView.setCompoundDrawables(drawable, null, null, null);
            mStateTextView.setText("已拒绝");
        }

        //参与范围
        if (assessmentDetails.getSchoolTeacherList() != null && assessmentDetails.getSchoolTeacherList().size()>0) {
            mAdapter.setData(assessmentDetails.getSchoolTeacherList());
        }
    }

    /**
     * 设置接收范围
     */
    private void setAdapter() {
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<ParticipateSchoolViewHolder>() {
            @Override
            public ParticipateSchoolViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new ParticipateSchoolViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext() ,
                        R.layout.item_assessment_detail_participate));
            }

            @Override
            public int getItemViewType(int position) {
                return ParticipateSchoolViewHolder.ITEM_TYPE_ATTEND_TEACHER;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private View.OnClickListener left = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogUtil1.cancle();
        }
    };
    private View.OnClickListener right = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            rejectEvaluation();
            dialogUtil1.cancle();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.assessment_details_bottom_text://进入评课 /拒绝评课 /查看评课
                if ("WAIT".equals(assessmentDetails.getStatus())) {
                    dialogUtil1.showDialog("确定拒绝评课吗？");
                } else if ("PROGRESS".equals(assessmentDetails.getStatus()) || "END".equals(assessmentDetails.getStatus())) {
                    EvaluationDetails();
                }
                break;
            case R.id.assessment_details_bottom_set_teacher://设置参与老师
                Intent intent = new Intent(this, SetTeacherActivity.class);
                intent.putExtra("assessmentDetails", assessmentDetails);
                intent.putExtra("userInfo", mUserInfo);
                startActivityForResult(intent, REQUEST_CODE_SET_TEACHER);
                overridePendingTransition(R.anim.slidemenu_show, R.anim.layout_hide);
                break;
        }
    }

    /**
     * 拒绝评课
     */
    private void rejectEvaluation() {
        dialogUtil.showDialog();
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        data.put("evaluationId", assessmentDetails.getEvaluationId());
        requestData(URLConfig.REJECT_EVLUATION, data, false,new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                dialogUtil.cancel();
                if ("success".equals(response.optString("result"))) {
                    ToastUtil.showToast(AssessmentDetailsActivity.this, "已拒绝");
                    assessmentDetails.setStatus("REJECT");
                    init();
                    setResult(EvaluationsFragment.LIST_REFRESH);
                } else {
                    ToastUtil.showToast(AssessmentDetailsActivity.this, "设置失败！");
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {

            }
        });
    }

    /**
     * 进入评课或查看评课详情
     */
    public void EvaluationDetails() {
        Intent intent = new Intent(this, EvaluationActivity.class);
        intent.putExtra("assessmentDetails", assessmentDetails);
        intent.putExtra(Constants.USER_INFO,mUserInfo);
        intent.putExtra("type", type);
        startActivityForResult(intent, REQUEST_CODE_SET_TEACHER);
        overridePendingTransition(R.anim.slidemenu_show, R.anim.layout_hide);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (REQUEST_CODE_SET_TEACHER == resultCode) {
            requestData(true);
        }
    }
}
