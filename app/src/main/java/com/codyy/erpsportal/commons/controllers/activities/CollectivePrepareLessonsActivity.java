package com.codyy.erpsportal.commons.controllers.activities;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ChannelAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.CollectivePrepareLessonsFragment;
import com.codyy.erpsportal.commons.controllers.fragments.FilterFragment;
import com.codyy.erpsportal.commons.controllers.fragments.FilterGradeSubject;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.SystemUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;

/**
 * 集体备课、互动听课列表
 * Created by yangxinwu on 2015/7/27.
 */
public class CollectivePrepareLessonsActivity extends BaseHttpActivity {

    @Bind(R.id.toolbar)    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)    TextView mTitleTextView;
    @Bind(R.id.dl_collective_prepare_lesson_drawerlayout)    DrawerLayout mDrawerLayout;
    @Bind(R.id.viewpager)    ViewPager mViewPager;
    @Bind(R.id.sliding_tabs)    SlidingTabLayout mSlidingTabLayout;

    private ChannelAdapter mAdapter;
    private String mType;
    private UserInfo mUserInfo;
    private FilterGradeSubject mFilterGradeSubject = null;
    private FilterGradeSubject mFilterGradeSubject2 = null;
    private FilterFragment filterFragment = null;
    private FragmentManager mFragmentManager;
    private int mTabWidth;
    private int mFilterType = 0;
    private final static int AREA = 1;//当前tab标签是在管理辖区下
    private final static int OTHER = 0;//当前tab标签是在发起、参与标签下、
    private final static int TAB_LAUNCH = 1;//发起的备/听课
    private final static int TAB_MANAGE = 2;//发起的备/听课
    private final static int TAB_AREA = 3;//管理辖区内备/听课

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_collective_prepare_lessons;
    }

    @Override
    public String obtainAPI() {
        return null;
    }

    @Override
    public HashMap<String, String> getParam() {
        return null;
    }

    @Override
    public void init() {
        mType = getIntent().getStringExtra(Constants.TYPE_LESSON);
        mUserInfo = getIntent().getParcelableExtra(Constants.USER_INFO);
        UiMainUtils.setNavigationTintColor(this, R.color.main_green);
        if (null == mUserInfo) return;
        initToolbar(mToolBar);
        mTitleTextView.setText(Titles.sWorkspaceGroup);
        initView();
    }

    @Override
    public void onSuccess(JSONObject response) {

    }

    @Override
    public void onFailure(VolleyError error) {

    }

    private void initView() {
        mFragmentManager = getSupportFragmentManager();
        mViewPager.setOffscreenPageLimit(2);
        mAdapter = new ChannelAdapter(this, getSupportFragmentManager(), mViewPager);
        if (mType.equals(Constants.TYPE_PREPARE_LESSON)) {//集体备课
            mTitleTextView.setText(Titles.sWorkspacePrepare);
            if (mUserInfo.isArea()) {//辖区内备课管理
                mAdapter.addTab(Titles.sWorkspacePrepareLaunch, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_LAUNCH));
                mAdapter.addTab(Titles.sWorkspacePrepareAttend, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_JOIN));
                if (mUserInfo.isAdmin())//如果是超级管理员就有管理辖区内备课的权限
                    mAdapter.addTab(Titles.sWorkspacePreparePrepare, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_MANAGE));
            } else if (mUserInfo.isSchool()) {//本校备课管理
                mAdapter.addTab(Titles.sWorkspacePrepareLaunch, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_LAUNCH));
                mAdapter.addTab(Titles.sWorkspacePrepareAttend, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_JOIN));
                if (mUserInfo.isAdmin())//校级管理员有管理备课的权限
                    mAdapter.addTab(Titles.sWorkspacePreparePrepare, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_MANAGE));
            } else if (mUserInfo.isTeacher()) {
                //我发起的
                if ("Y".equals(mUserInfo.getGroupPreparationFlag())) {//判断教师是否有发起备课的权限
                    mAdapter.addTab(Titles.sWorkspacePrepareLaunch, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_LAUNCH));
                }
                //我参与的
                mAdapter.addTab(Titles.sWorkspacePrepareAttend, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_JOIN));
            }

        } else if (mType.equals(Constants.TYPE_INTERACT_LESSON)) {//互动听课　.
            mTitleTextView.setText(Titles.sWorkspaceListen);
            if (mUserInfo.isArea()) {
                mAdapter.addTab(Titles.sWorkspaceListenLaunch, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_LAUNCH));
                //mAdapter.addTab(Titles.sWorkspaceListenParticipation, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_JOIN));
                if (mUserInfo.isAdmin()) {//如果是超级管理员就有管理辖区内听课的权限
                    mAdapter.addTab(Titles.sWorkspaceListenManage, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_MANAGE));
                }
            } else if (mUserInfo.isSchool()) {
                mAdapter.addTab(Titles.sWorkspaceListenLaunch, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_LAUNCH));
//                mAdapter.addTab(Titles.sWorkspaceListenParticipation, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_JOIN));
                if (mUserInfo.isAdmin()) {//校级管理员有管理听课的权限
                    mAdapter.addTab(Titles.sWorkspaceListenManage, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_MANAGE));
                }
            } else if (mUserInfo.isTeacher()) {
                if ("Y".equals(mUserInfo.getInteractiveListenFlag())) {//判断教师是否有发起听课的权限
                    mAdapter.addTab(Titles.sWorkspaceListenLaunch, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_LAUNCH));
                }
                mAdapter.addTab(Titles.sWorkspaceListenAttend, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_JOIN));
            }
        }
        mTabWidth = SystemUtils.getScreenWidth(this) / mAdapter.getCount();
        if (mAdapter.getCount() == 1 && (
                mAdapter.getPageTitle(0).equals(Titles.sWorkspaceListenManage) ||
                        mAdapter.getPageTitle(0).equals(Titles.sWorkspacePreparePrepare))) {
            showFilterFragment(TAB_AREA);//如果只有一个“管理辖区内听、备课”的Tab，就隐藏tab，加载包含省的筛选fragment
        } else {
            showFilterFragment(TAB_LAUNCH);//如果只有一个其他Tab，就隐藏tab，加载包不含省的筛选fragment
        }
        mSlidingTabLayout.setTabWidth(mTabWidth);
        mSlidingTabLayout.setViewPager(mViewPager);
        if (mAdapter.getCount() == 1) {
            mSlidingTabLayout.setVisibility(View.GONE);//如果只有一个其他Tab，就隐藏tab
        }

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mAdapter.getPageTitle(position).equals(Titles.sWorkspacePreparePrepare) ||
                        mAdapter.getPageTitle(position).equals(Titles.sWorkspaceListenManage)) {
                    if (mUserInfo.isSchool() && mUserInfo.isAdmin()) {
                        showFilterFragment(TAB_MANAGE);
                    } else {
                        showFilterFragment(TAB_AREA);
                    }
                    //当前tab在“管理辖区内听、备课”标签，加载含有省的筛选fragment
                } else if (mAdapter.getPageTitle(position).equals(Titles.sWorkspacePrepareLaunch) ||
                        mAdapter.getPageTitle(position).equals(Titles.sWorkspaceListenLaunch)) {
                    showFilterFragment(TAB_LAUNCH);
                    //当前tab在“发起，管理"的tab下，加载不含有省的筛选fragment
                } else if (mAdapter.getPageTitle(position).equals(Titles.sWorkspacePrepareAttend) ||
                        mAdapter.getPageTitle(position).equals(Titles.sWorkspaceListenAttend)) {
                    showFilterFragment(TAB_MANAGE);
                    //当前tab在“发起，管理"的tab下，加载不含有省的筛选fragment
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setSelected(true);

        this.setFilterListener(new IFilterListener() {
            @Override
            public void onFilterClick(MenuItem item) {
                if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    doFilterConfirm();
                }
            }

            @Override
            public void onPreFilterCreate(Menu menu) {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    menu.getItem(0).setIcon(R.drawable.ic_done_white);
                } else {
                    menu.getItem(0).setIcon(R.drawable.ic_filter);
                }
            }
        });
    }

    //start filter data .
    private void doFilterConfirm() {
        if (mFilterType == OTHER && mViewPager.getCurrentItem() == 0) {
            //发起的备、听课的确定搜索
            CollectivePrepareLessonsFragment collectivePrepareLessonsFragment = (CollectivePrepareLessonsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + mViewPager.getCurrentItem());
            collectivePrepareLessonsFragment.execSearch(mFilterGradeSubject.getmGradeID(), mFilterGradeSubject.getmSubjectID(), mFilterGradeSubject.getmStatusID());
        } else if (mFilterType == OTHER && mViewPager.getCurrentItem() >= 1) {
            //参与的备、听课的确定搜索
            CollectivePrepareLessonsFragment collectivePrepareLessonsFragment = (CollectivePrepareLessonsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + mViewPager.getCurrentItem());
            collectivePrepareLessonsFragment.execSearch(mFilterGradeSubject2.getmGradeID(), mFilterGradeSubject2.getmSubjectID(), mFilterGradeSubject2.getmStatusID());
        } else if (mFilterType == AREA) {
            //管理的备、听课的确定搜索
            CollectivePrepareLessonsFragment collectivePrepareLessonsFragment = (CollectivePrepareLessonsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + mViewPager.getCurrentItem());
            collectivePrepareLessonsFragment.execAreaSearch(filterFragment.getFilterGradeId(), filterFragment.getFilterSubjectId(), filterFragment.getFilterStateId(), filterFragment.getLastArea());
        }
    }

    private Bundle createBundle(int type) {
        Bundle args = new Bundle();
        args.putInt(CollectivePrepareLessonsFragment.PREPARE_LESSONS_TYPE, type);
        args.putParcelable(Constants.USER_INFO, mUserInfo);
        if (mType.equals(Constants.TYPE_PREPARE_LESSON)) {
            args.putString(Constants.TYPE_LESSON, Constants.TYPE_PREPARE_LESSON);
        } else {
            args.putString(Constants.TYPE_LESSON, Constants.TYPE_INTERACT_LESSON);
        }
        return args;
    }

    /**
     * 显示隐藏的筛选fragment
     */
    private void showFilterFragment(int index) {
        FragmentTransaction fm = mFragmentManager.beginTransaction();
        hideFragments(fm);
        switch (index) {
            case TAB_LAUNCH:
                mFilterType = OTHER;
                if (mFilterGradeSubject != null) {
                    //显示发起的备课、听课的筛选
                    fm.show(mFilterGradeSubject);
                } else {
                    mFilterGradeSubject = new FilterGradeSubject();
                    Bundle bundle = new Bundle();
                    bundle.putInt("filter_type", FilterGradeSubject.HAS_STATUS);
                    mFilterGradeSubject.setArguments(bundle);
                    fm.add(R.id.activity_collective_new_filter_fragment, mFilterGradeSubject);
                }
                break;
            case TAB_MANAGE:
                mFilterType = OTHER;
                if (mFilterGradeSubject2 != null) {
                    //显示参与的备课、听课的筛选
                    fm.show(mFilterGradeSubject2);
                } else {
                    mFilterGradeSubject2 = new FilterGradeSubject();
                    Bundle bundle = new Bundle();
                    bundle.putInt("filter_type", FilterGradeSubject.HAS_STATUS);
                    mFilterGradeSubject2.setArguments(bundle);
                    fm.add(R.id.activity_collective_new_filter_fragment, mFilterGradeSubject2);
                }
                break;
            case TAB_AREA:
                mFilterType = AREA;
                if (filterFragment != null) {
                    //显示管理的备课、听课的筛选
                    fm.show(filterFragment);
                } else {
                    filterFragment = new FilterFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 1);
                    bundle.putParcelable("userInfo", mUserInfo);
                    filterFragment.setArguments(bundle);
                    fm.add(R.id.activity_collective_new_filter_fragment, filterFragment);
                }
                break;
            default:
                break;

        }
        fm.commit();
    }

    /**
     * 当筛选fragment已被实例化，就隐藏起来
     */
    public void hideFragments(FragmentTransaction ft) {
        //隐藏所有的筛选
        if (mFilterGradeSubject != null)
            ft.hide(mFilterGradeSubject);
        if (mFilterGradeSubject2 != null)
            ft.hide(mFilterGradeSubject2);
        if (filterFragment != null)
            ft.hide(filterFragment);
    }

}
