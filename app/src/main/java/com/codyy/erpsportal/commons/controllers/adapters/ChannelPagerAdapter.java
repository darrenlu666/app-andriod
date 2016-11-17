package com.codyy.erpsportal.commons.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.codyy.erpsportal.commons.utils.Cog;

import java.util.ArrayList;

/**
 * 频道页适配器
 * Created by kmdai on 2015/7/20.
 */
public class ChannelPagerAdapter extends FragmentPagerAdapter {

    private final static String TAG = "ChannelPagerAdapter";

    private final Context mContext;
    private final ViewPager mViewPager;
    private final ArrayList<ChannelTabInfo> mTabs = new ArrayList<>();
    private FragmentManager mFragmentManager;

    public static final class ChannelTabInfo {
        private long id;
        private final String name;
        private final Class<?> clss;
        private Bundle args;

        public ChannelTabInfo(String name, Class<?> clss, Bundle args, long id) {
            this.name = name;
            this.clss = clss;
            this.args = args;
            this.id = id;
        }

        @Override
        public String toString() {
            return "ChannelTabInfo:clss=" + clss + ",name=" + name + ",args=" + args;
        }
    }

    public ChannelPagerAdapter(Activity activity, FragmentManager fragmentManager, ViewPager pager) {
        super(fragmentManager);
        this.mFragmentManager = fragmentManager;
        mContext = activity;
        if (pager == null) throw new IllegalArgumentException("ViewPager must not been null!");
        mViewPager = pager;
        mViewPager.setAdapter(this);
    }

    public void addTab(String name, Class<?> clss, Bundle args, long id) {
        addTabInfo(name, clss, args, id);
        notifyDataSetChanged();
    }

    public void addTabInfo(String name, Class<?> clss,  Bundle args, long id) {
        ChannelTabInfo info = new ChannelTabInfo(name, clss, args, id);
        mTabs.add(info);
    }

    public void addTabInfo(ChannelTabInfo tabInfo) {
        mTabs.add(tabInfo);
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
        return mFragmentManager.findFragmentByTag(makeFragmentName(mViewPager.getId(), position));
    }

    @Override
    public Fragment getItem(int position) {
        ChannelTabInfo info = mTabs.get(position);
        if (info.args == null) {
            info.args = new Bundle();
        }
        info.args.putInt("position", position);//将位置设置给fragment
        Cog.d(TAG, "getItem:position=", position, ",info=", info);
        return Fragment.instantiate(mContext, info.clss.getName(), info.args);
    }

    @Override
    public long getItemId(int position) {
        return mTabs.get(position).id;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

}
