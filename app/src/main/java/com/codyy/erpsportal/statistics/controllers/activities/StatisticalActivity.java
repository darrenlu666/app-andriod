package com.codyy.erpsportal.statistics.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.statistics.controllers.fragments.StatisticalFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;


/**
 * 统计界面
 */
public class StatisticalActivity extends AppCompatActivity {

    private TitleBar mTitleTv;

    private TextView mStatisticalTitle;

    private UserInfo mUserInfo;

    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistical);
        mTitleTv = (TitleBar) findViewById(R.id.title_bar);
        mStatisticalTitle = (TextView) findViewById(R.id.title_statistic);

        if (getIntent() != null) {
            mType = getIntent().getIntExtra(StatisticalFragment.ARG_TYPE, 0);
            mUserInfo = getIntent().getParcelableExtra(StatisticalFragment.ARG_USER_INFO);
        }
        if (mType == StatisticalFragment.TYPE_ACTIVITY) {//评课活动统计
            mTitleTv.setTitle(Titles.sWorkspaceCountDisucss);
        } else if (mType == StatisticalFragment.TYPE_RESOURCE) {//资源统计
            mTitleTv.setTitle(Titles.sWorkspaceCountResource);
        }
        initStatisticsTitle();
    }

    /**
     * 根据统计不同类型与当前用户账号类型设置标题
     */
    private void initStatisticsTitle() {
        if (mType == StatisticalFragment.TYPE_RESOURCE) {
            mStatisticalTitle.setText("本月上传资源总数");
        } else if (mType == StatisticalFragment.TYPE_ACTIVITY) {
            if (mUserInfo.isSchool()) {
                mStatisticalTitle.setText("本月评课活动总数");
            } else {
                mStatisticalTitle.setText("本月参与评课活动总数");
            }
        }
    }

    public static void start(Activity activity, UserInfo userInfo,int type) {
        Intent intent = new Intent(activity, StatisticalActivity.class);
        intent.putExtra(StatisticalFragment.ARG_USER_INFO, userInfo);
        intent.putExtra(StatisticalFragment.ARG_TYPE, type);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    /**
     * 进入活动统计
     * @param activity 当前activity
     * @param userInfo 登陆信息
     */
    public static void startActivityStat(Activity activity, UserInfo userInfo) {
        Intent intent = new Intent(activity, StatisticalActivity.class);
        intent.putExtra(StatisticalFragment.ARG_USER_INFO, userInfo);
        intent.putExtra(StatisticalFragment.ARG_TYPE, StatisticalFragment.TYPE_ACTIVITY);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    /**
     * 进入资源统计
     * @param activity 当前activity
     * @param userInfo 登陆信息
     */
    public static void startResourceStat(Activity activity, UserInfo userInfo) {
        Intent intent = new Intent(activity, StatisticalActivity.class);
        intent.putExtra(StatisticalFragment.ARG_USER_INFO, userInfo);
        intent.putExtra(StatisticalFragment.ARG_TYPE, StatisticalFragment.TYPE_RESOURCE);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UIUtils.addExitTranAnim(this);
    }

    public void onBackClick(View view) {
        finish();
        UIUtils.addExitTranAnim(this);
    }
}
