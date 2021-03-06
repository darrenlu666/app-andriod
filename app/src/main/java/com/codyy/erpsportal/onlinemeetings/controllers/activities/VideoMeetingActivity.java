package com.codyy.erpsportal.onlinemeetings.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.adapters.SimpleFragmentAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.FilterVideoMeetingFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ConfirmTextFilterListener;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.VideoMeetingFragment;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;

/**
 * 视频会议
 * Created by ldh on 2015/7/30.
 * modified by poe on 2016.
 */
public class VideoMeetingActivity extends BaseHttpActivity{
    public static final String TAG = "VideoMeetingActivity";
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.rlt_tab_container)RelativeLayout mTabContainerRlt;
    @Bind(R.id.tab_layout)TabLayout mTabLayout;
    @Bind(R.id.videoMeeting_ViewPager) ViewPager mViewPager;
    @Bind(R.id.drawer_videoMeeting) DrawerLayout mDrawerLayout;
    @Bind(R.id.divider_portrait)View mDivider;

    private List<VideoMeetingFragment> mListFragments = new ArrayList<>();//{辖区内的圈组/校内圈组/我的圈组}
    private SimpleFragmentAdapter<VideoMeetingFragment> mAdapter;
    private FilterVideoMeetingFragment mFilterVideoMeetingFragment;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_video_meeting;
    }

    @Override
    public String obtainAPI() {
        return null;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        return null;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {

    }

    @Override
    public void onFailure(Throwable error) {

    }

    @Override
    public void init() {
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);
        if(null == mUserInfo) return;
        //title
        mTitleTextView.setText(Titles.sWorkspaceVidmeet);
        initToolbar(mToolBar);
        initTabLayout();
        mListFragments = makeTabs();
        mAdapter = new SimpleFragmentAdapter<>(getSupportFragmentManager(),mListFragments);
        mViewPager.setAdapter(mAdapter);
        /** 解决自动销毁fragment->onDestroyView的问题**/
        mViewPager.setOffscreenPageLimit(3);
        addFilterFragment();
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener(){
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }
        });
    }

    private List<VideoMeetingFragment> makeTabs() {
        List<VideoMeetingFragment> listFragments = new ArrayList<>();
        int sWidth = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        switch (mUserInfo.getUserType()){
            case UserInfo.USER_TYPE_AREA_USER://管理员
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceVidmeetLaunch));//我发起的
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceVidmeetAttend));//我参与的

                listFragments.add(VideoMeetingFragment.newInstance(VideoMeetingFragment.TYPE_FOR_LAUNCH,mUserInfo));
                listFragments.add(VideoMeetingFragment.newInstance(VideoMeetingFragment.TYPE_FOR_ATTEND ,mUserInfo));
//                if(mUserInfo.isAdmin()){
                    mDivider.setVisibility(View.GONE);
                    mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceVidmeetManage));//本级会议管理
                    listFragments.add(VideoMeetingFragment.newInstance(VideoMeetingFragment.TYPE_FOR_AREA,mUserInfo));
//                }
                break;
            case UserInfo.USER_TYPE_SCHOOL_USER://学校管理员
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceVidmeetLaunch));//我发起的
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceVidmeetAttend));//我参与的

                listFragments.add(VideoMeetingFragment.newInstance(VideoMeetingFragment.TYPE_FOR_LAUNCH,mUserInfo));
                listFragments.add(VideoMeetingFragment.newInstance(VideoMeetingFragment.TYPE_FOR_ATTEND,mUserInfo));
//                if(mUserInfo.isAdmin()){
                    mDivider.setVisibility(View.GONE);
                    mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceVidmeetManage));//本级会议管理
                    listFragments.add(VideoMeetingFragment.newInstance(VideoMeetingFragment.TYPE_FOR_SCHOOL,mUserInfo));
//                }
                break;
            case UserInfo.USER_TYPE_TEACHER://教师
                if("Y".equals(mUserInfo.getVideoConferenceFlag())){
                    mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceVidmeetLaunch));//我发起的
                    listFragments.add(VideoMeetingFragment.newInstance(VideoMeetingFragment.TYPE_FOR_LAUNCH,mUserInfo));
                }else{
                    mTabContainerRlt.setVisibility(View.GONE);
                }
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceVidmeetAttend));//我参与的
                listFragments.add(VideoMeetingFragment.newInstance(VideoMeetingFragment.TYPE_FOR_ATTEND,mUserInfo));
                break;
            case UserInfo.USER_TYPE_STUDENT://学生
            case UserInfo.USER_TYPE_PARENT://家长
                mTabContainerRlt.setVisibility(View.GONE);
                listFragments.add(VideoMeetingFragment.newInstance(VideoMeetingFragment.TYPE_FOR_ATTEND,mUserInfo));
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                break;
        }
        return listFragments;
    }

    private void initTabLayout() {
        //add tabs
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.main_green));
        mTabLayout.setSelectedTabIndicatorHeight((int)(getResources().getDimension(R.dimen.tab_layout_select_indicator_height)));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Cog.i(TAG,"onTabSelected : " +mTabLayout.getSelectedTabPosition());
                Cog.i(TAG,"onTabSelected : tab.getPosition :" +tab.getPosition());
                mViewPager.setCurrentItem(tab.getPosition());
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setFilterListener(new ConfirmTextFilterListener(mDrawerLayout) {
            @Override
            protected void doFilterConfirmed() {
                Bundle bd = new Bundle();
                bd.putString("state",mFilterVideoMeetingFragment.GetSelectedItem());
                mListFragments.get(mTabLayout.getSelectedTabPosition()).doFilter(bd);
            }
        });

    }

    private void addFilterFragment() {
        mFilterVideoMeetingFragment = new FilterVideoMeetingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_filter,mFilterVideoMeetingFragment).commit();
    }

    public static void start(Activity from){
        Intent intent = new Intent(from , VideoMeetingActivity.class);
        from.startActivity(intent);
        UIUtils.addEnterAnim(from);
    }
}
