package com.codyy.erpsportal.commons.controllers.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ChannelAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.EvaluationsFragment;
import com.codyy.erpsportal.commons.controllers.fragments.FilterFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.AreaBase;
import com.codyy.erpsportal.commons.utils.ConfirmTextFilterListener;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * 应用-评课议课
 * Created by kmdai on 2015/4/9.
 */
public class AssessmentClassActivity extends BaseHttpActivity {
    /**
     * 发起的评课
     */
    public static final int SPONSOR = 0;
    /**
     * 管辖区的评课
     */
    public static final int AREA = 1;
    /**
     * 受邀的评课
     */
    public static final int INVITED = 2;
    /**
     * 本校教师的评课
     */
    public static final int SCHOOLTEACHER = 3;
    /**
     * 我主讲的评课
     */
    public static final int MASTER = 4;
    /**
     * 参与的评课
     */
    public static final int ATTEND = 5;
    /**
     * 本校主讲的
     */
    public static final int SCHOOLMASTER = 6;

    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.assessment_class_drawerlayout) DrawerLayout mDrawerLayout;
    @Bind(R.id.assessment_class_viewpager) ViewPager mViewPager;
    @Bind(R.id.assessment_class_slidingtablayout) SlidingTabLayout mSlidingTabLayout;
    private List<FilterFragment> mScreenFragments;
    private ChannelAdapter mChannelAdapter;

    @Override
    public int obtainLayoutId() {
        return R.layout.assessment_class_layout;
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
    public void onFailure(VolleyError error) {
    }

    /**
     * view初始化
     */
    @Override
    public void init() {
        mScreenFragments = new ArrayList<>();
        initToolbar(mToolBar);
        mTitleTextView.setText(Titles.sWorkspaceDisucss);

        mViewPager = (ViewPager) findViewById(R.id.assessment_class_viewpager);
        mChannelAdapter = new ChannelAdapter(this, getSupportFragmentManager(), mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportFragmentManager().beginTransaction().replace(R.id.assessment_list_select_layout, mScreenFragments.get(position)).commit();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.assessment_class_drawerlayout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
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
        addTabFragment();
        if (mUserInfo != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.assessment_list_select_layout, mScreenFragments.get(0)).commit();
        }
        this.setFilterListener(new ConfirmTextFilterListener(mDrawerLayout) {
            @Override
            protected void doFilterConfirmed() {
                doFilter();
            }
        });
    }


    private void addTabFragment() {
        if ("AREA_USR".equals(mUserInfo.getUserType())) {//区域用户
            mSlidingTabLayout.setAverageTabs(true);
            Bundle bundle1 = new Bundle();
            bundle1.putParcelable("userInfo", mUserInfo);
            bundle1.putInt("class_type", SPONSOR);
            mChannelAdapter.addTab(Titles.sWorkspaceDisucssLaunch, EvaluationsFragment.class, bundle1);
            mScreenFragments.add(addScreen(SPONSOR));
            Bundle bundle2 = new Bundle();
            bundle2.putParcelable("userInfo", mUserInfo);
            bundle2.putInt("class_type", AREA);
            mChannelAdapter.addTab(Titles.sWorkspaceDisucssInArea, EvaluationsFragment.class, bundle2);
            mScreenFragments.add(addScreen(FilterFragment.AREA_EVALUATION));
        } else if ("SCHOOL_USR".equals(mUserInfo.getUserType())) {//学校
            mSlidingTabLayout.setAverageTabs(false);
            Bundle bundle1 = new Bundle();
            bundle1.putParcelable("userInfo", mUserInfo);
            bundle1.putInt("class_type", SPONSOR);
            mChannelAdapter.addTab(Titles.sWorkspaceDisucssLaunch, EvaluationsFragment.class, bundle1);
            mScreenFragments.add(addScreen(SPONSOR));
            Bundle bundle2 = new Bundle();
            bundle2.putParcelable("userInfo", mUserInfo);
            bundle2.putInt("class_type", INVITED);
            mChannelAdapter.addTab(Titles.sWorkspaceDisucssInvited, EvaluationsFragment.class, bundle2);
            mScreenFragments.add(addScreen(INVITED));
            Bundle bundle3 = new Bundle();
            bundle3.putParcelable("userInfo", mUserInfo);
            bundle3.putInt("class_type", SCHOOLTEACHER);
            mChannelAdapter.addTab(Titles.sWorkspaceDisucssTeaLaunch, EvaluationsFragment.class, bundle3);
            mScreenFragments.add(addScreen(SCHOOLTEACHER));
            Bundle bundle4 = new Bundle();
            bundle4.putParcelable("userInfo", mUserInfo);
            bundle4.putInt("class_type", SCHOOLMASTER);
            mChannelAdapter.addTab(Titles.sWorkspaceDisucssInSchool, EvaluationsFragment.class, bundle4);
            mScreenFragments.add(addScreen(SCHOOLMASTER));
        } else if ("TEACHER".equals(mUserInfo.getUserType())) {//教师
            mSlidingTabLayout.setAverageTabs(true);
            if ("Y".equals(mUserInfo.getEvaFlag())) {
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable("userInfo", mUserInfo);
                bundle1.putInt("class_type", SPONSOR);
                mChannelAdapter.addTab(Titles.sWorkspaceDisucssLaunch, EvaluationsFragment.class, bundle1);
                mScreenFragments.add(addScreen(SPONSOR));
            }
            Bundle bundle2 = new Bundle();
            bundle2.putParcelable("userInfo", mUserInfo);
            bundle2.putInt("class_type", ATTEND);
            mChannelAdapter.addTab(Titles.sWorkspaceDisucssAttend, EvaluationsFragment.class, bundle2);
            mScreenFragments.add(addScreen(ATTEND));
            Bundle bundle3 = new Bundle();
            bundle3.putParcelable("userInfo", mUserInfo);
            bundle3.putInt("class_type", MASTER);
            mChannelAdapter.addTab(Titles.sWorkspaceDisucssMySpack, EvaluationsFragment.class, bundle3);
            mScreenFragments.add(addScreen(MASTER));
        }
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private FilterFragment addScreen(int type) {
        FilterFragment screenFragment1 = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putParcelable("userInfo", mUserInfo);
        screenFragment1.setArguments(bundle);
        return screenFragment1;
    }

    /**
     * 确定筛选
     */
    private void doFilter() {
        mDrawerLayout.closeDrawer(Gravity.RIGHT);
        AreaBase areaBase = mScreenFragments.get(mViewPager.getCurrentItem()).getLastArea();
        EvaluationsFragment fragment = (EvaluationsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + mViewPager.getCurrentItem());
        fragment.setStatus(mScreenFragments.get(mViewPager.getCurrentItem()).getState(), areaBase);
    }
}
