package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.LiveClassAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.SchoolNetClassAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.LiveFragment;
import com.codyy.erpsportal.commons.controllers.fragments.SchoolNetClassFragment;
import com.codyy.erpsportal.commons.controllers.fragments.filters.GroupFilterFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.AppPriority;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专递课堂、直录播课堂 共用
 * Created by kmdai on 2015/4/7.
 * modified by poe 2015/5/7
 */
public class LiveClassActivity extends FragmentActivity  {
    private String TAG = "LiveClassActivity";
    public final static String EXTRA_USER_INFO = "user_info";
    public final static String EXTRA_FROM_WHERE_MODEL = "from_where";

    @Bind(R.id.live_class_viewpager)ViewPager mViewPager;
    @Bind(R.id.live_class_slipview1)View mSlipView1;
    @Bind(R.id.live_class_slipview2)View mSlipView2;
    @Bind(R.id.live_class_text_real)TextView mRealTimeTextView;
    @Bind(R.id.live_class_text_old)TextView mOldTimeTextView;
    @Bind(R.id.btnBackOfLive)Button mBackBtn;
    @Bind(R.id.live_class_drawerlayout)DrawerLayout mDrawerLayout;
    @Bind(R.id.btn_select) CheckBox mSelectCheckBox;
    @Bind(R.id.linear_tab)LinearLayout mTabLinearLayout;
    @Bind(R.id.tv_title_live)TextView mTitleTextView;

    private GroupFilterFragment mFilterFragment ;
    private UserInfo mUserInfo = null;
    private LiveClassAdapter mLiveClassAdapter;
    private SchoolNetClassAdapter mSchoolNetClassAdapter;
    private int mType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_class);
        ButterKnife.bind(this);
        init();
        initListView();
    }

    /**
     * 初始化view
     */
    private void init() {
        //get user
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mType = getIntent().getExtras().getInt(EXTRA_FROM_WHERE_MODEL);
        mBackBtn.setOnClickListener(mOnclickListener);
        mRealTimeTextView.setText(Titles.sWorkspaceSpeclassLive);
        mOldTimeTextView.setText(Titles.sWorkspaceSpeclassReplay);
        mRealTimeTextView.setOnClickListener(mOnclickListener);
        mOldTimeTextView.setOnClickListener(mOnclickListener);
        mSelectCheckBox.setOnClickListener(mOnclickListener);
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mSelectCheckBox.setChecked(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mSelectCheckBox.setChecked(false);
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFilterFragment = new GroupFilterFragment();
        Bundle bd = new Bundle();
        bd.putString(GroupFilterFragment.EXTRA_TYPE, GroupFilterFragment.TYPE_FILTER_TEACH);
        mFilterFragment.setArguments(bd);
        ft.replace(R.id.fl_filter, mFilterFragment);
        ft.commit();
    }

    public UserInfo getUserInfo(){
        return  mUserInfo;
    }

    private void initListView() {

        if(mType == 0){
            //mTitleTextView.setText(Titles.sWorkspaceOlclassLive);
            List<LiveFragment> liveList = new ArrayList<>();
            liveList.add(LiveFragment.newInstance(0, UserInfoKeeper.getInstance().getUserInfo()));

            AppPriority role = AppPriority.getByRole(mUserInfo.getUserType());
            //家长无权限观看 .
            if(role != AppPriority.PARENT){
                mTabLinearLayout.setVisibility(View.VISIBLE);
                liveList.add(LiveFragment.newInstance(1, UserInfoKeeper.getInstance().getUserInfo()));
            }else{
                mTabLinearLayout.setVisibility(View.GONE);
                //hide the filter function .
                hideFilter();
            }
            //若为学生，隐藏筛选
            if(role == AppPriority.STUDENT){
                hideFilter();
            }

            mLiveClassAdapter = new LiveClassAdapter(getSupportFragmentManager(), liveList);
            mViewPager.setAdapter(mLiveClassAdapter);
        }else if(mType == 1){
            //mTitleTextView.setText(Titles.sWorkspaceNetclassLive);
            List<SchoolNetClassFragment> liveList = new ArrayList<>();
            liveList.add(SchoolNetClassFragment.newInstance(0, UserInfoKeeper.getInstance().getUserInfo()));
            if(!UserInfoKeeper.getInstance().getUserInfo().getUserType().equals(UserInfo.USER_TYPE_PARENT) ){
                mTabLinearLayout.setVisibility(View.VISIBLE);
                liveList.add(SchoolNetClassFragment.newInstance(1, UserInfoKeeper.getInstance().getUserInfo()));
            }
            else{
                mTabLinearLayout.setVisibility(View.GONE);
                //hide the filter function .
                hideFilter();
            }

            //若为学生，隐藏筛选
            if(UserInfoKeeper.getInstance().getUserInfo().getUserType().equals(UserInfo.USER_TYPE_STUDENT)){
                hideFilter();
            }

            mSchoolNetClassAdapter = new SchoolNetClassAdapter(getSupportFragmentManager(), liveList);
            mViewPager.setAdapter(mSchoolNetClassAdapter);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Cog.d(TAG, position + ":" + positionOffset + ":" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRealTimeTextView.setTextColor(getResources().getColor(R.color.green));
                        mSlipView1.setBackgroundColor(0xff1bac22);
                        mOldTimeTextView.setTextColor(getResources().getColor(R.color.personal_text_color));
                        mSlipView2.setBackgroundDrawable(null);
                        break;
                    case 1:
                        mOldTimeTextView.setTextColor(getResources().getColor(R.color.green));
                        mSlipView2.setBackgroundColor(0xff1bac22);
                        mRealTimeTextView.setTextColor(getResources().getColor(R.color.personal_text_color));
                        mSlipView1.setBackgroundDrawable(null);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void hideFilter() {
        mSelectCheckBox.setVisibility(View.INVISIBLE);
    }

    private View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBackOfLive:
                    finish();
                    overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
                    break;
                case R.id.live_class_text_real:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.live_class_text_old:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.btn_select:
                    if (!mSelectCheckBox.isChecked()) {
                        Bundle bd = mFilterFragment.getFilterData();
                        if(mType == 0 ){
                            if (null != mLiveClassAdapter && bd != null) {
                                int pos = mViewPager.getCurrentItem();
                                LiveFragment lf = (LiveFragment) mLiveClassAdapter.getItem(pos);
                                lf.doSearch(bd);
                            }
                        }else{
                            if (null != mSchoolNetClassAdapter && bd != null) {
                                int pos = mViewPager.getCurrentItem();
                                SchoolNetClassFragment lf = (SchoolNetClassFragment) mSchoolNetClassAdapter.getItem(pos);
                                lf.doSearch(bd);
                            }
                        }

                    }
                    if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    } else {
                        mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * 启动直播课堂
     *
     * @param activity 源界面
     * @param userInfo 用户登录信息
     */
    public static void start(Activity activity, UserInfo userInfo) {
        Intent intentLive = new Intent(activity, LiveClassActivity.class);
        intentLive.putExtra(EXTRA_USER_INFO, userInfo);
        activity.startActivity(intentLive);
    }
}
