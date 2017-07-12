package com.codyy.erpsportal.commons.controllers.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ChannelAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.CollectivePrepareLessonsFragment;
import com.codyy.erpsportal.commons.controllers.fragments.FilterFragment;
import com.codyy.erpsportal.commons.controllers.fragments.FilterGradeSubject;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.ConfirmTextFilterListener;
import com.codyy.erpsportal.commons.utils.SystemUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;
import com.codyy.tpmp.filterlibrary.entities.FilterConstants;
import com.codyy.tpmp.filterlibrary.fragments.CommentFilterFragment;
import com.codyy.tpmp.filterlibrary.interfaces.HttpGetInterface;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 集体备课、互动听课列表
 * Created by yangxinwu on 2015/7/27.
 */
public class CollectivePrepareLessonsActivity extends BaseHttpActivity implements HttpGetInterface {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)
    TextView mTitleTextView;
    @Bind(R.id.dl_collective_prepare_lesson_drawerlayout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;

    private ChannelAdapter mAdapter;
    private String mType;
    private UserInfo mUserInfo;
    private CommentFilterFragment mFilterFragment;
    private CommentFilterFragment mAreaFilterFragment;
    private FragmentManager mFragmentManager;
    private int mTabWidth;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_collective_prepare_lessons;
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
    public void onSuccess(JSONObject response, boolean isRefreshing) {

    }

    @Override
    public void onFailure(Throwable error) {

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
                mAdapter.addTab(Titles.sWorkspacePreparePrepare, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_MANAGE));
            } else if (mUserInfo.isSchool()) {//本校备课管理
                mAdapter.addTab(Titles.sWorkspacePrepareLaunch, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_LAUNCH));
                mAdapter.addTab(Titles.sWorkspacePrepareAttend, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_JOIN));
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
                mAdapter.addTab(Titles.sWorkspaceListenManage, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_MANAGE));
            } else if (mUserInfo.isSchool()) {
                mAdapter.addTab(Titles.sWorkspaceListenLaunch, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_LAUNCH));
                mAdapter.addTab(Titles.sWorkspaceListenManage, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_MANAGE));
            } else if (mUserInfo.isTeacher()) {
                if ("Y".equals(mUserInfo.getInteractiveListenFlag())) {//判断教师是否有发起听课的权限
                    mAdapter.addTab(Titles.sWorkspaceListenLaunch, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_LAUNCH));
                }
                mAdapter.addTab(Titles.sWorkspaceListenAttend, CollectivePrepareLessonsFragment.class, createBundle(CollectivePrepareLessonsFragment.TYPE_JOIN));
            }
        }
        mTabWidth = SystemUtils.getScreenWidth(this) / mAdapter.getCount();


        mSlidingTabLayout.setTabWidth(mTabWidth);
        mSlidingTabLayout.setViewPager(mViewPager);
        if (mAdapter.getCount() == 1) {
            mSlidingTabLayout.setVisibility(View.GONE);//如果只有一个其他Tab，就隐藏tab
        }

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (mAdapter.getPageTitle(position).equals(Titles.sWorkspacePreparePrepare) ||
                        mAdapter.getPageTitle(position).equals(Titles.sWorkspaceListenManage)) {
                    if (mUserInfo.isArea()) {
                        showFilterFragment(true);
                    } else {
                        showFilterFragment(false);
                    }
                    //当前tab在“管理辖区内听、备课”标签，加载含有省的筛选fragment
                } else if (mAdapter.getPageTitle(position).equals(Titles.sWorkspacePrepareLaunch) ||
                        mAdapter.getPageTitle(position).equals(Titles.sWorkspaceListenLaunch)) {
                    showFilterFragment(false);
                    //当前tab在“发起，管理"的tab下，加载不含有省的筛选fragment
                } else if (mAdapter.getPageTitle(position).equals(Titles.sWorkspacePrepareAttend) ||
                        mAdapter.getPageTitle(position).equals(Titles.sWorkspaceListenAttend)) {
                    showFilterFragment(false);
                    //当前tab在“发起，管理"的tab下，加载不含有省的筛选fragment
                }
            }
        });
        mViewPager.setSelected(true);

        setFilterListener(new ConfirmTextFilterListener(mDrawerLayout) {
            @Override
            protected void doFilterConfirmed() {
                doFilterConfirm();
            }
        });

        //carete filter .
        if(mUserInfo.isArea()){
            showFilterFragment(true);
        }
        if (mAdapter.getCount() > 1){
            showFilterFragment(false);
        }
    }


    /**
     * １．筛选－＂我创建的＂
     */
    private void createMyFilter() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (null != mFilterFragment) ft.hide(mFilterFragment);
        if (null != mAreaFilterFragment) ft.hide(mAreaFilterFragment);
        if (mFilterFragment == null) {
            mFilterFragment = CommentFilterFragment.newInstance(mUserInfo.getUuid()
                    , mUserInfo.getUserType()
                    , mUserInfo.getBaseAreaId()
                    , mUserInfo.getSchoolId()
                    , new int[]{
                            FilterConstants.LEVEL_CLASS_LEVEL//年级
                            , FilterConstants.LEVEL_CLASS_SUBJECT//学科
                            , FilterConstants.LEVEL_CLASS_STATE//状态
                    });
            ft.add(R.id.activity_collective_new_filter_fragment, mFilterFragment);
        } else {
            ft.show(mFilterFragment);
        }
        ft.commitAllowingStateLoss();
    }

    /**
     * 2.筛选－＂区内备课管理＂
     */
    private void createAreaFilter() {
        //2.筛选－＂区内课程管理＂
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (null != mFilterFragment) ft.hide(mFilterFragment);
        if (null != mAreaFilterFragment) ft.hide(mAreaFilterFragment);
        if (mAreaFilterFragment == null) {
            mAreaFilterFragment = CommentFilterFragment.newInstance(mUserInfo.getUuid()
                    , mUserInfo.getUserType()
                    , mUserInfo.getBaseAreaId()
                    , mUserInfo.getSchoolId()
                    , new int[]{
                            FilterConstants.LEVEL_AREA              //省
                            , FilterConstants.LEVEL_SCHOOL          //学校
                            , FilterConstants.LEVEL_CLASS_LEVEL     //年级
                            , FilterConstants.LEVEL_CLASS_SUBJECT   //学科
                            , FilterConstants.LEVEL_CLASS_STATE     //状态
                    });
            ft.add(R.id.activity_collective_new_filter_fragment, mAreaFilterFragment);
        } else {
            ft.show(mAreaFilterFragment);
        }
        ft.commitAllowingStateLoss();
    }

    //start filter data .
    private void doFilterConfirm() {

        Bundle bd = mFilterFragment.getFilterData();
        if(UserInfo.USER_TYPE_AREA_USER.equals(mUserInfo.getUserType())&& mViewPager.getCurrentItem()>0){
            bd = mAreaFilterFragment.getFilterData();
        }

        CollectivePrepareLessonsFragment collectivePrepareLessonsFragment = (CollectivePrepareLessonsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + mViewPager.getCurrentItem());
        String areaId = null;
        String schoolId = null;

        if (mUserInfo.isSchool()) {
            schoolId = mUserInfo.getSchoolId();
        }
        if (mUserInfo.isArea()) {
            areaId = bd.getString("areaId");
        }
        collectivePrepareLessonsFragment.execAreaSearch(bd.getString("class")
                , bd.getString("subject")
                , bd.getString("state")
                , schoolId
                , areaId);
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
    private void showFilterFragment(boolean area) {

        if (area) {
            createAreaFilter();
        } else {
            createMyFilter();
        }
    }

    //提供筛选必要的网络请求.
    @Override
    public void sendRequest(String url, Map<String, String> param, final Listener listener, final ErrorListener errorListener) {
        requestData(url, (HashMap<String, String>) param, false, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) throws Exception {
                if (null != listener) listener.onResponse(response);
            }

            @Override
            public void onRequestFailure(Throwable error) {
                if (null != errorListener) errorListener.onErrorResponse(error);
            }
        });
    }

    /**
     * 当筛选fragment已被实例化，就隐藏起来
     */
    /*public void hideFragments(FragmentTransaction ft) {
        //隐藏所有的筛选
        if (mFilterGradeSubject != null)
            ft.hide(mFilterGradeSubject);
        if (mFilterGradeSubject2 != null)
            ft.hide(mFilterGradeSubject2);
        if (filterFragment != null)
            ft.hide(filterFragment);
    }
*/
}
