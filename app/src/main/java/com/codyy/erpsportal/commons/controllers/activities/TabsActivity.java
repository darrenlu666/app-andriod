package com.codyy.erpsportal.commons.controllers.activities;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;

import butterknife.Bind;

/**
 * 标签Activity
 * Created by gujiajia on 2015/7/27.
 */
public abstract class TabsActivity extends ToolbarActivity {
    /**
     * 当tab数量为1时，隐藏SlidingTabLayout
     */
    private static final int TABS_COUNT = 1;
    /**
     * 标题栏
     */
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    /**
     * 标题
     */
    @Bind(R.id.toolbar_title)
    protected TextView mTitle;

    /**
     * 标签栏
     */
    @Bind(R.id.tabs)
    protected SlidingTabLayout mTabs;

    @Bind(R.id.pager)
    protected ViewPager mPager;

    protected TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onViewBound();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_tabs;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

    /**
     * butterknife绑定后将调用这个方法，
     * 初始化标题栏setCustomTitle在这里调用为妙，
     * 以及做些其它数据初始化也不错呀
     */
    protected void onViewBound() {
        mTabsAdapter = new TabsAdapter(this, getSupportFragmentManager(), mPager);
        addFragments();
        mPager.setAdapter(mTabsAdapter);
        hideTabsIfNeeded();
    }

    protected void hideTabsIfNeeded() {
        if (mTabsAdapter.getCount() == TABS_COUNT) {
            mTabs.setVisibility(View.GONE);
        } else {
            mTabs.setViewPager(mPager);
        }
    }

    /**
     * 设置自定义标题名
     *
     * @param stringId 标题名的字串资源id
     */
    protected void setCustomTitle(@StringRes int stringId) {
        mTitle.setText(stringId);
    }

    /**
     * 设置自定义标题名
     *
     * @param title 标题名
     */
    protected void setCustomTitle(CharSequence title) {
        mTitle.setText(title);
    }

    /**
     * 调用addFragment方法，把Fragment加入
     */
    protected abstract void addFragments();

    /**
     * 调用了往界面里加Fragment和标签
     *
     * @param title  标签名字
     * @param clazz  Fragment类对象
     * @param bundle 一些数据，可在Fragment里通过getArguments获得
     */
    protected void addFragment(String title, Class<? extends Fragment> clazz, Bundle bundle) {
        mTabsAdapter.addTab(title, clazz, bundle);
    }

    private View makeTabIndicator(String title) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView tabTitleTv = (TextView) view.findViewById(R.id.tab_title);
        tabTitleTv.setText(title);
        return view;
    }

    /**
     * 获取当前碎片
     *
     * @return
     */
    public Fragment getCurrentFragment() {
        int position = mPager.getCurrentItem();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + position);
        return fragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UIUtils.addExitTranAnim(this);
    }
}
