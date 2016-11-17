package com.codyy.erpsportal.commons.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import com.codyy.erpsportal.commons.utils.Cog;
import java.util.ArrayList;

/**
 * 频道页适配器
 * Created by kmdai on 2015/7/20.
 */
public class ChannelAdapter extends FragmentPagerAdapter {

    private final static String TAG = "ChannelAdapter";
    private final Context mContext;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<>();
    private FragmentManager fm;

    static final class TabInfo {
        private final Class<?> clss;
        private final Bundle args;
        private final String name;
        private final Fragment fragment;

        TabInfo(String _name, Class<?> _class, Bundle _args) {
            clss = _class;
            args = _args;
            name = _name;
            fragment = null;
        }

        TabInfo(String _name, Fragment _fragment, Bundle _args) {
            fragment = _fragment;
            args = _args;
            name = _name;
            clss = null;
        }

        @Override
        public String toString() {
            return "ChannelTabInfo:clss=" + clss + ",name=" + name + ",args=" + args
                    + ",fragment=" + fragment;
        }
    }

    public ChannelAdapter(Activity activity, FragmentManager fm, ViewPager pager) {
        super(fm);
        this.fm = fm;
        mContext = activity;
        if (pager == null) throw new IllegalArgumentException("ViewPager must not been null!");
        mViewPager = pager;
        mViewPager.setAdapter(this);
    }

    private void removeFragment(FragmentManager fm, FragmentTransaction transaction, int position) {
        if (position < 0 || position >= mViewPager.getAdapter().getCount()) return;
        Fragment fragment = fm.findFragmentByTag(makeFragmentName(mViewPager.getId(), position));
        if (fragment != null) {
            transaction.remove(fragment);
        }
    }

    public void addTab(String name, Class<?> clss, Bundle args) {
        addTabInfo(name, clss, args);
        notifyDataSetChanged();
    }

    public void addTabInfo(String name, Class<?> clss, Bundle args) {
        TabInfo info = new TabInfo(name, clss, args);
        mTabs.add(info);
    }

    public void addTab(String name, Fragment fragment, Bundle args) {
        addTabInfo(name, fragment, args);
        notifyDataSetChanged();
    }

    public void addTabInfo(String name, Fragment fragment, Bundle args) {
        TabInfo info = new TabInfo(name, fragment, args);
        mTabs.add(info);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).name;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    public Fragment getFragmentAt(int position) {
        if (mTabs.get(position).fragment != null) {
            return mTabs.get(position).fragment;
        }
        return fm.findFragmentByTag(makeFragmentName(mViewPager.getId(), position));
    }

    @Override
    public Fragment getItem(int position) {
        if (mTabs.get(position).fragment != null) {
            return mTabs.get(position).fragment;
        }
        TabInfo info = mTabs.get(position);
        Cog.d(TAG, "getItem:position=", position, ",info=", info);
        return Fragment.instantiate(mContext, info.clss.getName(), info.args);
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }


}
