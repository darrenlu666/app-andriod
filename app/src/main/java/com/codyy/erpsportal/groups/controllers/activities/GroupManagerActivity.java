package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.entities.FilterConstants;
import com.codyy.tpmp.filterlibrary.fragments.CommentFilterFragment;
import com.codyy.tpmp.filterlibrary.interfaces.HttpGetInterface;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.adapters.SimpleFragmentAdapter;
import com.codyy.erpsportal.groups.controllers.fragments.GroupManagerListFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.CodyyViewPager;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 应用-圈组
 * Created by poe on 16-1-19.
 */
public class GroupManagerActivity extends BaseHttpActivity implements HttpGetInterface{
    private static final String TAG = "GroupManagerActivity";
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.rlt_tab_container)RelativeLayout mTabContainerRlt;
    @Bind(R.id.tab_layout)TabLayout mTabLayout;
    @Bind(R.id.vp_group)CodyyViewPager mViewPager;
    @Bind(R.id.drawer_layout)DrawerLayout mDrawerLayout;
//    private GroupManagerFilterFragment mFilterFragment ;
    private CommentFilterFragment mFilterFragment ;
    /**
     * {辖区内的圈组/校内圈组/我的圈组}
     */
    private List<GroupManagerListFragment> mListFragments = new ArrayList<>();
    private SimpleFragmentAdapter<GroupManagerListFragment> mAdapter;
    private String mGrade ;
    private String mSubject ;
    private String mSemester ;
    private String mBaseAreaId ;
    private String mSchoolId ;
    private String mCategory ;//分类
    private String mState ;//状态
    private boolean mIsDirectory = false;//是否为直属校 nodirectory /directory

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_group_manager;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_GROUP_MANAGER_LIST;
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

    public void init() {
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);
        if(null == mUserInfo) return;
        initToolbar(mToolBar);
        mTitleTextView.setText(Titles.sWorkspaceGroup);
        initView();
        mListFragments = makeTabs();
        mAdapter = new SimpleFragmentAdapter<>(getSupportFragmentManager(),mListFragments);
        mViewPager.setAdapter(mAdapter);

        initFilterFragment();

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

    private void initFilterFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        int[] filters ;
        if(UserInfo.USER_TYPE_AREA_USER.equals(mUserInfo.getUserType())){
            filters = new int[]{
                      FilterConstants.LEVEL_AREA       //省
                    , FilterConstants.LEVEL_CLASS_SEMESTER //学段
                    , FilterConstants.LEVEL_SCHOOL     //学校
                    , FilterConstants.LEVEL_CLASS_TEAM//年级
            };
        }else{
            filters = new int[]{
                      FilterConstants.LEVEL_CLASS_TEAM     //学校
                    , FilterConstants.LEVEL_MANAGER_STATE//状态
            };
        }

        mFilterFragment = CommentFilterFragment.newInstance(mUserInfo.getUuid()
                ,mUserInfo.getUserType()
                ,mUserInfo.getBaseAreaId()
                ,mUserInfo.getSchoolId()
                ,filters);

        ft.replace(R.id.fl_filter, mFilterFragment);
        ft.commitAllowingStateLoss();
    }

    @NonNull
    private List<GroupManagerListFragment> makeTabs() {
        List<GroupManagerListFragment> listFragments = new ArrayList<>();
        switch (mUserInfo.getUserType()){
            case UserInfo.USER_TYPE_AREA_USER://管理员
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceGroupAreaGroup));
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceGroupMyGroup));
                listFragments.add(GroupManagerListFragment.newInstance(GroupManagerListFragment.TYPE_MANAGER_AREA));
                listFragments.add(GroupManagerListFragment.newInstance(GroupManagerListFragment.TYPE_MY_LIST));
                break;
            case UserInfo.USER_TYPE_SCHOOL_USER://学校管理员
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceGroupSchoolGroup));
                mTabLayout.addTab(mTabLayout.newTab().setText(Titles.sWorkspaceGroupMyGroup));
                listFragments.add(GroupManagerListFragment.newInstance(GroupManagerListFragment.TYPE_MANAGER_SCHOOL));
                listFragments.add(GroupManagerListFragment.newInstance(GroupManagerListFragment.TYPE_MY_LIST));
                break;
            case UserInfo.USER_TYPE_TEACHER://教师
            case UserInfo.USER_TYPE_STUDENT://学生
            case UserInfo.USER_TYPE_PARENT://家长
                mTabContainerRlt.setVisibility(View.GONE);
                listFragments.add(GroupManagerListFragment.newInstance(GroupManagerListFragment.TYPE_MY_LIST));
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
        mTabLayout.setSelectedTabIndicatorColor(UiMainUtils.getColor(R.color.main_green));
        mTabLayout.setSelectedTabIndicatorHeight((int)(getResources().getDimension(R.dimen.tab_layout_select_indicator_height)));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setPagingEnabled(true);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        this.setFilterListener(new IFilterListener() {
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
                if(mTabLayout.getSelectedTabPosition()>0 || mListFragments.size()<2){
                    menu.setGroupVisible(0,false);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }else{
                    menu.setGroupVisible(0,true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
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
        });
    }

    private void doFilterConfirmed() {
        Cog.i(TAG," doFilterConfirmed ~");
        Bundle bd = mFilterFragment.getFilterData();
        if(null != bd){
            mBaseAreaId = bd.getString("areaId");
            mIsDirectory = bd.getBoolean("hasDirect");
            mSemester   =   bd.getString("semester");
            mSchoolId   =   bd.getString("directSchoolId");
            mGrade      =   bd.getString("class");
            mSubject    =   bd.getString("subject");
            mCategory   =   bd.getString("category");
            mState      =   bd.getString("state");
        }
        Cog.i(TAG,"mBaseAreaId"+mBaseAreaId+" mIsDirectory"+mIsDirectory+" mSemester"+mSemester
                +" mSchoolId"+mSchoolId+" mGrade"+mGrade
                +" mSubject"+mSubject+" mCategory"+mCategory
                +" mState" + mState);
        //refresh .
        mListFragments.get(0).refresh(bd);
    }

    public static void start(Activity from){
        Intent intent = new Intent(from , GroupManagerActivity.class);
        from.startActivity(intent);
        UIUtils.addEnterAnim(from);
    }

    @Override
    public void sendRequest(String url, Map<String, String> param, final Listener listener, final ErrorListener errorListener) {
        requestData(url, (HashMap<String, String>) param, true, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) throws Exception {
                if(null != listener) listener.onResponse(response);
            }

            @Override
            public void onRequestFailure(Throwable error) {
                if(null != errorListener) errorListener.onErrorResponse(error);
            }
        });
    }
}
