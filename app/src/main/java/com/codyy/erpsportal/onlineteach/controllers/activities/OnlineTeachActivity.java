package com.codyy.erpsportal.onlineteach.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.adapters.SimpleFragmentAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.filters.GroupManagerFilterFragment;
import com.codyy.erpsportal.commons.controllers.fragments.filters.NetTeachFilterFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ConfirmTextFilterListener;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.CodyyViewPager;
import com.codyy.erpsportal.onlineteach.controllers.fragments.OnlineTeachFragment;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * 应用-网络授课
 * Created by poe on 16-6-20.
 */
public class OnlineTeachActivity extends BaseHttpActivity {
    private static final String TAG = "OnlineTeachActivity";
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.rlt_tab_container)RelativeLayout mTabContainerRlt;
    @Bind(R.id.tab_layout)TabLayout mTabLayout;
    @Bind(R.id.vp_group)CodyyViewPager mViewPager;
    @Bind(R.id.drawer_layout)DrawerLayout mDrawerLayout;
    private NetTeachFilterFragment mFilterFragment ;
    private NetTeachFilterFragment mAreaFilterFragment ;
    /**
     * {辖区内的圈组/校内圈组/我的圈组}
     */
    private List<OnlineTeachFragment> mListFragments = new ArrayList<>();
    private SimpleFragmentAdapter<OnlineTeachFragment> mAdapter;
    private String mGrade ;
    private String mSubject ;
    private String mSemester ;
    private String mBaseAreaId ;
    private String mSchoolId ;
    private String mStatus ;//状态
    private boolean mIsDirectory = false;//是否为直属校 nodirectory /directory

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_group_manager;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_MY_CREATE_TEACH_LIST;
    }

    @Override
    public HashMap<String, String> getParam() {
        return null;
    }

    @Override
    public void onSuccess(JSONObject response) {

    }

    @Override
    public void onFailure(VolleyError error) {

    }

    public void init() {
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);
        if(null == mUserInfo) return;
        initToolbar(mToolBar);
        mTitleTextView.setText(Titles.sWorkspaceNetTeach);
        initView();
        mListFragments = makeTabs();
        mAdapter = new SimpleFragmentAdapter<>(getSupportFragmentManager(),mListFragments);
        mViewPager.setAdapter(mAdapter);
        crateMyFilter();

        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener(){
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

    @NonNull
    private List<OnlineTeachFragment> makeTabs() {
        List<OnlineTeachFragment> listFragments = new ArrayList<>();
        switch (mUserInfo.getUserType()){
            case UserInfo.USER_TYPE_AREA_USER://管理员
                //我穿件的
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceNetTeachMyCreate));
                listFragments.add(OnlineTeachFragment.newInstance(OnlineTeachFragment.TYPE_MY_CREATE));
                //区内课程管理　
                if(mUserInfo.isAdmin()){
                    mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceNetTeachManage));
                    listFragments.add(OnlineTeachFragment.newInstance(OnlineTeachFragment.TYPE_MANAGER_AREA));
                }else{
                    mTabContainerRlt.setVisibility(View.GONE);
                }
                break;
            case UserInfo.USER_TYPE_SCHOOL_USER://学校管理员
                //我创建的
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceNetTeachMyCreate));
                listFragments.add(OnlineTeachFragment.newInstance(OnlineTeachFragment.TYPE_MY_CREATE));
                //本校课程管理
                if(mUserInfo.isAdmin()){
                    mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceNetTeachManage));
                    listFragments.add(OnlineTeachFragment.newInstance(OnlineTeachFragment.TYPE_MANAGER_SCHOOL));
                }else{
                    mTabContainerRlt.setVisibility(View.GONE);
                }
                break;
            case UserInfo.USER_TYPE_TEACHER://教师
                //我创建的
                if ("Y".equals( mUserInfo.getNetTeachingFlag())) {//判断教师是否有发起备课的权限
                    mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceNetTeachMyCreate));
                    listFragments.add(OnlineTeachFragment.newInstance(OnlineTeachFragment.TYPE_MY_CREATE));
                }else{
                    mTabContainerRlt.setVisibility(View.GONE);
                }
                //我的课程
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceNetTeachMyCourse));
                listFragments.add(OnlineTeachFragment.newInstance(OnlineTeachFragment.TYPE_MY));
                break;
            case UserInfo.USER_TYPE_STUDENT://学生
            case UserInfo.USER_TYPE_PARENT://家长
                mTabContainerRlt.setVisibility(View.GONE);
                listFragments.add(OnlineTeachFragment.newInstance(OnlineTeachFragment.TYPE_MY));
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                break;
        }
        return listFragments;
    }

    /**
     * 非逻辑的视图初始化
     */
    private void initView() {
        //add tabs
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.main_green));
        mTabLayout.setSelectedTabIndicatorHeight((int)(getResources().getDimension(R.dimen.tab_layout_select_indicator_height)));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setPagingEnabled(true);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Cog.i(TAG,"onTabSelected : " +mTabLayout.getSelectedTabPosition());
                Cog.i(TAG,"onTabSelected : tab.getPosition :" +tab.getPosition());
                mViewPager.setCurrentItem(tab.getPosition());
                supportInvalidateOptionsMenu();
                if(UserInfo.USER_TYPE_AREA_USER.equals(mUserInfo.getUserType())){
                    if(0 == mTabLayout.getSelectedTabPosition()){
                        crateMyFilter();
                    }else{
                        createAreaFilter();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        /*this.setFilterListener(new IFilterListener() {
            @Override
            public void onFilterClick(MenuItem item) {
                if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    doFilterConfirmed();
                }
            }

            @Override
            public void onPreFilterCreate(Menu menu) {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    menu.getItem(0).setActionView(R.layout.textview_filter_confirm_button);
                    TextView tv = (TextView) menu.getItem(0).getActionView().findViewById(R.id.tv_title);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                            doFilterConfirmed();
                        }
                    });
                } else {
                    menu.getItem(0).setIcon(R.drawable.ic_filter);
                }
            }
        });*/
        setFilterListener(new ConfirmTextFilterListener(mDrawerLayout) {
            @Override
            protected void doFilterConfirmed() {
                OnlineTeachActivity.this.doFilterConfirmed();
            }
        });
    }

    /**
     *１．筛选－＂我创建的＂
     */
    private void crateMyFilter() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFilterFragment = new NetTeachFilterFragment();
        Bundle bd = new Bundle();
        bd.putString(GroupManagerFilterFragment.EXTRA_TYPE, GroupManagerFilterFragment.TYPE_FILTER_NET_TEACH);
        mFilterFragment.setArguments(bd);
        ft.replace(R.id.fl_filter, mFilterFragment);
        ft.commitAllowingStateLoss();
    }
    /**
     *  2.筛选－＂区内课程管理＂
     */
    private void createAreaFilter() {
        //2.筛选－＂区内课程管理＂
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(mAreaFilterFragment == null){
            mAreaFilterFragment = new NetTeachFilterFragment();
            Bundle bd = new Bundle();
            bd.putString(GroupManagerFilterFragment.EXTRA_TYPE, GroupManagerFilterFragment.TYPE_FILTER_NET_TEACH_AREA_MANAGER);
            mAreaFilterFragment.setArguments(bd);
        }
        ft.replace(R.id.fl_filter, mAreaFilterFragment);
        ft.commitAllowingStateLoss();
    }

    private void doFilterConfirmed() {
        Cog.i(TAG," doFilterConfirmed ~");
        Bundle bd = mFilterFragment.getFilterData();
        if(UserInfo.USER_TYPE_AREA_USER.equals(mUserInfo.getUserType())&&mTabLayout.getSelectedTabPosition()>0){
            bd = mAreaFilterFragment.getFilterData();
        }

        if(null != bd){
            mBaseAreaId = bd.getString("areaId");
            mIsDirectory = bd.getBoolean("hasDirect");
            mSemester   =   bd.getString("semester");
            mSchoolId   =   bd.getString("directSchoolId");
            mGrade      =   bd.getString("class");
            mSubject    =   bd.getString("subject");
            mStatus   =   bd.getString("state");
        }
        Cog.i(TAG,"mBaseAreaId"+mBaseAreaId+" mIsDirectory"+mIsDirectory+" mSemester"+mSemester+" mSchoolId"+mSchoolId+" mGrade"+mGrade+" mSubject"+mSubject+" mState"+mStatus);
        //refresh .
        mListFragments.get(mTabLayout.getSelectedTabPosition() > 0? mTabLayout.getSelectedTabPosition(): 0).refresh(bd);
    }

    public static void start(Activity from){
        Intent intent = new Intent(from , OnlineTeachActivity.class);
        from.startActivity(intent);
        UIUtils.addEnterAnim(from);
    }
}
