package com.codyy.erpsportal.schooltv.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.schooltv.models.SchoolProgram;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;

/**
 * 校园电视台-节目单-详情
 * Created by poe on 17-3-16.
 */
public class SchoolProgramDetail extends BaseHttpActivity {
    private static final String TAG = "SchoolProgramDetail";
    private static final String EXTRA_PROGRAM_ID = "school.tv.program.id";
    private String mProgramId;
    private SchoolProgram mProgramDetail;//节目详情
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)
    TextView mTitleTextView;
    @Bind(R.id.empty_view)
    EmptyView mEmptyView;
    @Bind(R.id.detail_title_tv)
    TextView mProgramTitleBigTv;
    @Bind(R.id.program_name_linear_layout)
    LinearLayout mTitleLinearLayout;
    @Bind(R.id.detail_title_small_tv)
    TextView mProgramTitleSmallTv;
    @Bind(R.id.time_tv)
    TextView mProgramTimeTv;
    @Bind(R.id.master_tv)
    TextView mProgramMasterTv;
    @Bind(R.id.desc_tv)
    TextView mProgramDescTv;
    //viewStub content .
    ViewStub mViewStub;
    /**
     * 视频布局
     */
    FrameLayout mVideoFrameLayout;
    /**
     * 标题栏
     */
    RelativeLayout mVideoTitleRlt;
    /**
     * 顶部包括返回按钮和学校名称的条
     */
    LinearLayout mVideoTitleLinearLayout;
    /**
     * 实时直播播放器
     */
    BnVideoLayout2 mVideoLayout;
    /**
     * 实时直播未开始的提示
     */
    TextView mVideoFailureTv;
    /**
     * 横竖平控制及播放进度控制
     **/
    BNVideoControlView mVideoControlView;
    /**
     * back image
     **/
    ImageView mBackImage;//back img .

    private String mSchoolId = null ;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_school_tv_program_detail;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_SCHOOL_TV_PROGRAM_DETAIL;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> param = new HashMap<>();
        if (null != mUserInfo) {
            param.put("uuid", mUserInfo.getUuid());
            if (!TextUtils.isEmpty(mSchoolId)) {
                param.put("schoolId", mSchoolId);
            }
        }
        if (null != mProgramId) param.put("tvProgramDetailId", mProgramId);
        return param;
    }

    @Override
    public void init() {
        mProgramId = getIntent().getStringExtra(EXTRA_PROGRAM_ID);
        mSchoolId = getIntent().getStringExtra(Constants.USER_INFO_SCHOOL_ID);
        mTitleTextView.setText("节目详情");
        initToolbar(mToolBar);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                requestData(true);
            }
        });
    }

    @Override
    public void onSuccess(JSONObject response, boolean isRefreshing) throws Exception {
        if (null == mEmptyView) return;
        mEmptyView.setLoading(false);

        if (null != response && null != response.optJSONObject("detail")) {
            SchoolProgram sp = new Gson().fromJson(response.optJSONObject("detail").toString(), SchoolProgram.class);
            if (null != sp) {
                //mock data .
                mProgramDetail = sp;
            }
        }
        initStub();
        initData();

        if (null == mProgramDetail) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(Throwable error) throws Exception {
        if (null == mProgramDetail) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getData();
    }

    private void getData() {
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(true);
        requestData(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(isFinishing()) return;
        //横竖屏后做相应的控件大小设置
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mViewStub.setLayoutParams(lparam);
            mVideoFrameLayout.setLayoutParams(lparam);
        } else {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mViewStub.setLayoutParams(param);
            int height = UIUtils.dip2px(this, 180);
            LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            mVideoFrameLayout.setLayoutParams(lparam);
        }
    }
    //控制视频控件是否应该显示
    private void initStub() {
        if (null != mProgramDetail && mProgramDetail.getStatus() != SchoolProgram.STATUS_INIT) {
            mViewStub = (ViewStub) findViewById(R.id.view_stub);
            mViewStub.inflate();

            mVideoFrameLayout = (FrameLayout) findViewById(R.id.video_frame_layout);
            mVideoTitleRlt = (RelativeLayout) findViewById(R.id.rl_video_title);
            mVideoTitleLinearLayout = (LinearLayout) findViewById(R.id.ll_top_title);
            mVideoLayout = (BnVideoLayout2) findViewById(R.id.bn_video_layout2);
            mVideoFailureTv = (TextView) findViewById(R.id.video_un_start_tip_tv);
            /** 横竖平控制及播放进度控制**/
            mVideoControlView = (BNVideoControlView) findViewById(R.id.video_control);
            /** back image **/
            mBackImage = (ImageView) findViewById(R.id.iv_back);
            mBackImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        UIUtils.setPortrait(SchoolProgramDetail.this);
                    } else {
                        finish();
                        UIUtils.addExitTranAnim(SchoolProgramDetail.this);
                    }
                }
            });

            initVideoLayout();
        }
    }

    private void initVideoControlListener() {
        mVideoLayout.setTextClickListener(new BnVideoLayout2.ITextClickListener() {
            @Override
            public void onClick(View v) {
                mVideoControlView.showControl();
            }
        });
        mVideoControlView.bindVideoView(mVideoLayout, new IFragmentMangerInterface() {
            @Override
            public FragmentManager getNewFragmentManager() {
                return getSupportFragmentManager();
            }
        });
    }

    //填充对应的数据
    private void initData() {
        if (null != mProgramDetail) {
            if (mProgramDetail.getStatus() != SchoolProgram.STATUS_INIT) {
                mToolBar.setVisibility(View.GONE);
                mProgramTitleBigTv.setVisibility(View.GONE);
                mTitleLinearLayout.setVisibility(View.VISIBLE);
                mProgramTitleSmallTv.setText(mProgramDetail.getProgramName());
            } else {
                mProgramTitleBigTv.setText(mProgramDetail.getProgramName());
            }
            String time = DateUtil.getDateStr(mProgramDetail.getStartTime(), DateUtil.DEF_FORMAT) + "-" + DateUtil.getDateStr(mProgramDetail.getEndTime(), DateUtil.HH_MM);
            mProgramTimeTv.setText(time);
            mProgramMasterTv.setText(TextUtils.isEmpty(mProgramDetail.getSpeaker()) ? "无" : mProgramDetail.getSpeaker());
            mProgramDescTv.setText(TextUtils.isEmpty(mProgramDetail.getBrief()) ? "无" : mProgramDetail.getBrief());
        }
    }

    /**
     * 初始化视频相关操作
     **/
    private void initVideoLayout() {

        if (mProgramDetail.getStatus() == SchoolProgram.STATUS_ON) {//直播流
            //直播没有视频流提示
            if (!TextUtils.isEmpty(mProgramDetail.getStreamUrl())) {
                mVideoFailureTv.setVisibility(View.GONE);
                initVideoControlListener();
                mVideoControlView.setPlayMode(BNVideoControlView.MODE_LIVING);
                mVideoControlView.setVideoPath(mProgramDetail.getStreamUrl(), BnVideoView2.BN_URL_TYPE_RTMP_LIVE, false);
                //超时设置
                mVideoLayout.setTimeOut(15);
            } else {
                //tips no video .
                mVideoControlView.setVisibility(View.GONE);
                mVideoFailureTv.setVisibility(View.VISIBLE);
                mVideoFailureTv.setText(getString(R.string.tv_detail_video_un_start));

            }
        } else if (SchoolProgram.STATUS_END == mProgramDetail.getStatus()) {//历史录播流
            mVideoControlView.setExpandable(true);
            //录播没有视频提示
            if (SchoolProgram.TRANS_SUCCESS.equals(mProgramDetail.getTransFlag())) {
                mVideoFailureTv.setVisibility(View.GONE);
                initVideoControlListener();
                mVideoControlView.setVideoPath(mProgramDetail.getVideoPath(), BnVideoView2.BN_URL_TYPE_HTTP, false);
            } else {
                //tips no video .
                mVideoControlView.setVisibility(View.GONE);
                mVideoFailureTv.setVisibility(View.VISIBLE);
                mVideoFailureTv.setText(getString(R.string.tv_no_data_for_video));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mProgramDetail && SchoolProgram.STATUS_END == mProgramDetail.getStatus()) {//历史录播流)
            mVideoControlView.onPause();
        }
    }

    public static void start(Activity act, UserInfo userInfo, String programId,String schoolId) {
        Intent intent = new Intent(act, SchoolProgramDetail.class);
        intent.putExtra(EXTRA_PROGRAM_ID, programId);
        intent.putExtra(Constants.USER_INFO, userInfo);
        intent.putExtra(Constants.USER_INFO_SCHOOL_ID,schoolId);
        act.startActivity(intent);
        UIUtils.addEnterAnim(act);
    }
}
