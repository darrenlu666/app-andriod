package com.codyy.erpsportal.statistics.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 课堂统计首页
 */
public class CoursesStatisticsActivity extends AppCompatActivity {

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.tv_lb_giving_course)
    TextView mGivingCourseLbTv;

    /**
     * 开课概况：主讲教室（主讲）
     */
    @Bind(R.id.tv_main_classroom)
    TextView mMainClassroomTv;

    /**
     * 开课概况：主讲教室（受邀）
     */
    @Bind(R.id.tv_main_classroom_invited)
    TextView mMainClassroomInvitedTv;

    /**
     * 开课概述：接收教室
     */
    @Bind(R.id.tv_receiving_classroom)
    TextView mReceivingClassroomTv;

    @Bind(R.id.tv_lb_proportion)
    TextView mProportionLbTv;

    /**
     * 开课比分析：主讲教室（主讲）
     */
    @Bind(R.id.tv_proportion_main_classroom)
    TextView mProportionMainClassroomTv;

    /**
     * 开课比分析：主讲教室（受邀）
     */
    @Bind(R.id.tv_proportion_main_classroom_invited)
    TextView mProportionMainClassroomInvitedTv;

    /**
     * 接收教室
     */
    @Bind(R.id.tv_proportion_receiving_classroom)
    TextView mProportionReceivingClassroomTv;

    @Bind(R.id.tv_lb_proportion_subject)
    TextView mProportionSubjectLbTv;

    @Bind(R.id.tv_proportion_subject)
    TextView mProportionSubjectTv;

    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_statistics);
        ButterKnife.bind(this);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        initTitles();
    }

    private void initTitles() {
        mTitleTv.setText(Titles.sWorkspaceCountClass);
        mGivingCourseLbTv.setText(Titles.sWorkspaceCountTutiongeneral + ":");
        mProportionLbTv.setText(Titles.sWorkspaceCountTutiontate + ":");
        mProportionSubjectLbTv.setText(Titles.sWorkspaceCountSubject + ":");
        mProportionSubjectTv.setText(Titles.sWorkspaceCountSubject);
        mMainClassroomTv.setText(getString(R.string.classroom_role_format,
                Titles.sMasterRoom, Titles.sMaster));//主讲教室（主讲）
        mMainClassroomInvitedTv.setText(getString(R.string.classroom_role_format,
                Titles.sMasterRoom, Titles.sInvited));
        mReceivingClassroomTv.setText( Titles.sReceiveRoom);

        mProportionMainClassroomTv.setText(getString(R.string.classroom_role_format,
                Titles.sMasterRoom, Titles.sMaster));
        mProportionMainClassroomInvitedTv.setText(getString(R.string.classroom_role_format,
                Titles.sMasterRoom, Titles.sInvited));
        mProportionReceivingClassroomTv.setText( Titles.sReceiveRoom);
    }

    public static void start(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, CoursesStatisticsActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UIUtils.addExitTranAnim(this);
    }

    @OnClick(R.id.btn_return)
    public void onReturnBtnClick() {
        finish();
        UIUtils.addExitTranAnim(this);
    }

    @OnClick({R.id.tv_main_classroom, R.id.tv_main_classroom_invited, R.id.tv_receiving_classroom, R.id.tv_proportion_main_classroom, R.id.tv_proportion_main_classroom_invited, R.id.tv_proportion_receiving_classroom, R.id.tv_proportion_subject})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_main_classroom://开课概况：主讲教室（主讲）
                ClassStatTableActivity.startMain(this, mUserInfo);
                break;
            case R.id.tv_main_classroom_invited://开课概况：主讲教室（受邀）
                ClassStatTableActivity.startMainInvited(this, mUserInfo);
                break;
            case R.id.tv_receiving_classroom://开课概述：接收教室
                ClassStatTableActivity.startReceiving(this, mUserInfo);
                break;
            case R.id.tv_proportion_main_classroom://开课比分析：主讲教室（主讲）
                CoursesProportionTableActivity.startMain(this, mUserInfo);
                break;
            case R.id.tv_proportion_main_classroom_invited://开课比分析：主讲教室（受邀）
                CoursesProportionTableActivity.startMainInvited(this, mUserInfo);
                break;
            case R.id.tv_proportion_receiving_classroom://接收教室
                CoursesProportionTableActivity.startReceiving(this, mUserInfo);
                break;
            case R.id.tv_proportion_subject://学科统计
                ClassStatTableActivity.startSubject(this, mUserInfo);
                break;
        }
    }
}
