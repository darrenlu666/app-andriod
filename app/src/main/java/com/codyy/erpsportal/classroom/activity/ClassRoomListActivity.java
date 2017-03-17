package com.codyy.erpsportal.classroom.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.fragment.ClassRoomListFragment;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

import butterknife.Bind;

/**
 * 专递课堂/直录播课堂（名校网络课堂） - 直播列表
 * Created by lah on 2016/6/28.
 */
public class ClassRoomListActivity extends ToolbarActivity implements ClassRoomListFragment.TodayClassCountListener {
    /**
     * 标题
     */
    @Bind(R.id.toolbar_title)
    protected TextView mTitle;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tv_today_class)
    TextView mTodayClassTv;
    private UserInfo mUserInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = getIntent().getParcelableExtra(Constants.USER_INFO);
        if(null == mUserInfo) mUserInfo = UserInfoKeeper.obtainUserInfo();
        initViews();
    }

    private void initViews() {
        mTitle.setText(getIntent().getStringExtra(ClassRoomContants.FROM_WHERE_MODEL).equals(ClassRoomContants.TYPE_CUSTOM_LIVE) ? Titles.sWorkspaceSpeclassLive : Titles.sWorkspaceNetClassLive);
        ClassRoomListFragment classRoomListFragment = ClassRoomListFragment.newInstance(getIntent().getStringExtra(ClassRoomContants.FROM_WHERE_MODEL),mUserInfo);
        classRoomListFragment.setTodayClassCountListener(this);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, classRoomListFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_classroom_list;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

    @Override
    public void getTodayClassCount(int count) {
        if (mTodayClassTv != null) {
            mTodayClassTv.setVisibility(View.VISIBLE);
            mTodayClassTv.setText(getString(R.string.todayClass, count));
        }
    }
}
