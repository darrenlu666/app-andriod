package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签下页面适配器
 * Created by yangxinwu on 2015/7/30.
 */
public  class TabsAdapter extends FragmentPagerAdapter{
    private final Context mContext;
    private final ViewPager mViewPager;
    private final List<TabInfo> mTabs = new ArrayList<>();

    static final class TabInfo {
        private final String title;
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(String _title, Class<?> _class, Bundle _args) {
            title = _title;
            clss = _class;
            args = _args;
        }
    }

    public TabsAdapter(FragmentActivity activity, FragmentManager fm, ViewPager pager) {
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

    public void remove(int start) {
        while (mTabs.size() > start) {
            mTabs.remove(mTabs.size() - 1);
        }
        notifyDataSetChanged();
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
        return Fragment.instantiate(mContext, info.clss.getName(), info.args);
    }
}
