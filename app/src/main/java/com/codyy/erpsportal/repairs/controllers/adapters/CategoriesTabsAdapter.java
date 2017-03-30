package com.codyy.erpsportal.repairs.controllers.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 标签下页面适配器
 * Created by G++
 */
public  class CategoriesTabsAdapter extends FragmentPagerAdapter{
    private final Context mContext;
    private final ViewPager mViewPager;
    private final List<TabInfo> mTabs = new ArrayList<>();

    private final SparseArray<WeakReference<Fragment>> mFragments = new SparseArray<>();

    static final class TabInfo {
        private String title;
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(String _title, Class<?> _class, Bundle _args) {
            title = _title;
            clss = _class;
            args = _args;
        }
    }

    public CategoriesTabsAdapter(FragmentActivity activity, FragmentManager fm, ViewPager pager) {
        super(fm);
        mContext = activity;
        mViewPager = pager;
        mViewPager.setAdapter(this);
    }

    public void addTab(String title, Class<?> clss, Bundle args) {
        TabInfo info = new TabInfo(title, clss, args);
        mTabs.add(info);
        notifyDataSetChanged();
    }

    public void setTitle(int position, String title) {
        if (position < 0 || position >= mTabs.size()) return;
        mTabs.get(position).title = title;
    }

    public void remove(int start) {
        while (mTabs.size() > start) {
            int index = mTabs.size() - 1;
            mTabs.remove( index);
            mFragments.remove( index);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取所在位置的碎片
     * @param position 位置
     * @return 所在位置的碎片
     */
    public Fragment getFragmentAt(int position) {
        WeakReference<Fragment> reference = mFragments.get(position);
        if (reference == null) return null;
        return reference.get();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).title;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        Fragment fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
        mFragments.put(position, new WeakReference<>(fragment));
        return fragment;
    }
}
