package com.codyy.erpsportal.commons.controllers.activities;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;

import java.util.Map;

import butterknife.Bind;

/**
 * 标签Activity，右侧滑筛选
 * Created by eachann on 2015/12/24.
 */
public abstract class TabsWithFilterActivity extends ToolbarActivity implements OnPageChangeListener {
    /**
     * 当tab数量为1时，隐藏SlidingTabLayout
     */
    private static final int TABS_COUNT = 1;

    /**
     * 过滤菜单默认位置
     */
    private static final int FIRST_MENU_ITEM_POSITION = 0;

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
     * 侧滑抽屉
     */
    @Bind(R.id.filter_drawer_layout)
    protected DrawerLayout mDrawerLayout;

    /**
     * 标签栏
     */
    @Bind(R.id.tabs)
    protected SlidingTabLayout mTabs;

    @Bind(R.id.pager)
    protected ViewPager mPager;

    @Bind(R.id.fl_filter)
    protected FrameLayout mFilterLayout;

    protected TabsAdapter mTabsAdapter;

    protected FragmentManager mFragmentManager;

    /**
     * 这个稀疏列表，存着提供过滤参数的Fragment，
     * 对应被过滤的列表Fragment的position为key
     */
    private SparseArray<Fragment> mFilterFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onInitData();
        onViewBound();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_tabs_with_filter;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

    protected void replaceFilterFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.fl_filter, fragment);
        ft.commit();
    }

    /**
     * butterknife绑定后将调用这个方法，
     * 初始化标题栏setCustomTitle在这里调用为妙，
     * 以及做些其它数据初始化也不错呀
     */
    protected void onViewBound() {
        mFragmentManager = getSupportFragmentManager();
        mFilterFragments = new SparseArray<>();
        mTabsAdapter = new TabsAdapter(this, getSupportFragmentManager(), mPager);
        addFragments();
        mPager.setAdapter(mTabsAdapter);
        if (mTabsAdapter.getCount() == TABS_COUNT) {
            mTabs.setVisibility(View.GONE);
        } else {
            mTabs.setViewPager(mPager);
        }
        mPager.addOnPageChangeListener(this);
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        });
        addFilterFragments();
        addFirstFilterFragment();
    }

    /**
     * 初始化数据，如初始化登陆信息之类的。View绑定，初始化标题之后，onViewBound()之前。
     */
    protected void onInitData() {
    }

    /**
     * 这个里面调用{@link #addFilterFragment(int, Fragment)}添加右侧滑出来的过滤Fragment
     */
    protected abstract void addFilterFragments();

    /**
     * 添加过滤Fragment
     *
     * @param index    过滤Fragment针对第几页列表Fragment
     * @param fragment 过滤Fragment
     */
    protected void addFilterFragment(int index, Fragment fragment) {
        if (index < 0 || index >= mTabsAdapter.getCount()) {
            throw new IndexOutOfBoundsException("序号越界，要对应被过滤的fragment");
        }
        if (fragment instanceof FilterParamsProvider) {
            mFilterFragments.put(index, fragment);
        } else {
            throw new IllegalArgumentException("你的Fragment必须实现FilterParamProvider过滤参数提供者来提供过滤参数。");
        }
    }

    /**
     * 刚进入初始化第0页对应的过滤Fragment
     */
    private void addFirstFilterFragment() {
        Fragment fragment = mFilterFragments.get(0);
        if (fragment != null) {
            replaceFilterFragment(fragment);
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

    /**
     * 调用了往界面里加Fragment和标签
     *
     * @param titleStrId 标签名字字串id
     * @param clazz      Fragment类对象
     * @param bundle     一些数据，可在Fragment里通过getArguments获得
     * @see #addFragment(String, Class, Bundle)
     */
    protected void addFragment(@StringRes int titleStrId, Class<? extends Fragment> clazz, Bundle bundle) {
        mTabsAdapter.addTab(getResources().getString(titleStrId), clazz, bundle);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mDrawerLayout.getDrawerLockMode(GravityCompat.END) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {//当前fragment无筛选时,隐藏右上角筛选
            menu.getItem(FIRST_MENU_ITEM_POSITION).setVisible(false);
            return true;
        } else {
            menu.getItem(FIRST_MENU_ITEM_POSITION).setVisible(true);
        }
//        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
//            menu.getItem(FIRST_MENU_ITEM_POSITION).setIcon(R.drawable.ic_done_white);
//        } else {
//            menu.getItem(FIRST_MENU_ITEM_POSITION).setIcon(R.drawable.ic_filter);
//        }
        menu.getItem(FIRST_MENU_ITEM_POSITION).setActionView(R.layout.menu_item_filter_btn);
        View view = menu.getItem(FIRST_MENU_ITEM_POSITION).getActionView();
        Button filterBtn = (Button) view.findViewById(R.id.btn_filter);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            turnFiltering(filterBtn);
        } else {
            turnToFilter(filterBtn);
        }
        filterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onFilterBtnClick((Button)v);
            }
        });
        return true;
    }

    private void turnToFilter(Button filterBtn) {
        ViewCompat.setBackground(filterBtn, null);
        filterBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_filter,0,0,0);
        filterBtn.setText("");
    }

    private void turnFiltering(Button filterBtn) {
        filterBtn.setBackgroundResource(R.drawable.bg_filter_rectangle_green);
        filterBtn.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        filterBtn.setText("确认筛选");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                View view = item.getActionView();
                Button filterBtn = (Button) view.findViewById(R.id.btn_filter);
                onFilterBtnClick(filterBtn);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void onFilterBtnClick(Button filterBtn) {
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            turnFiltering(filterBtn);
            mDrawerLayout.openDrawer(GravityCompat.END);
        } else {
            turnToFilter(filterBtn);
            mDrawerLayout.closeDrawer(GravityCompat.END);
            doFilterConfirmed();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Fragment filterFragment = mFilterFragments.get(position);
        if (filterFragment != null) {
            replaceFilterFragment(filterFragment);
            if (mDrawerLayout.getDrawerLockMode(Gravity.RIGHT) != DrawerLayout.LOCK_MODE_UNLOCKED) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                invalidateOptionsMenu();
            }
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 执行过滤动作，从当前过滤参数提供者FilterParamProvider中通过acquireFilterParams方法获取过滤参数Map，
     * 通过调用过滤操作观察者OnFilterObserver的onFilterConfirmed方法传递给列表Fragment
     * 当然这边的列表Fragment要实现OnFilterObserver，在OnFilterObserver中发送添加过滤参数的数据请求
     */
    protected void doFilterConfirmed() {
        Fragment fragment = getCurrentFragment();
        FilterParamsProvider filterParamsProvider = (FilterParamsProvider) mFilterFragments.get(mPager.getCurrentItem());
        ((OnFilterObserver) fragment).onFilterConfirmed(filterParamsProvider.acquireFilterParams());
    }

    /**
     * 过滤操作观察者
     */
    public interface OnFilterObserver {

        /**
         * 确认过滤时调用
         *
         * @param params 过滤参数，用于附加到url请求参数列表，实现过滤。
         */
        void onFilterConfirmed(Map<String, String> params);
    }

    /**
     * 过滤参数提供者,抽屉里的Fragment需实现这个接口
     */
    public interface FilterParamsProvider {
        /**
         * 获取过滤参数
         *
         * @return
         */
        Map<String, String> acquireFilterParams();
    }
}
