package com.codyy.erpsportal.groups.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

/**
 * @author poe
 * 交互的适配器
 */
public class OnlineMeetingInteractAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private final ViewPager mViewPager;
    private ArrayList<Fragment> mFraments;
    private final ArrayList<TabInfo> mTabs = new ArrayList<>();
    private FragmentManager fm;

    static final class TabInfo {
        private  String tag;
        private  Class<?> clss;
        private  Bundle args;
        private  String name;

        TabInfo(String _tag, String _name, Class<?> _class, Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
            name = _name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public OnlineMeetingInteractAdapter(Activity activity, FragmentManager fm, ViewPager pager) {
        super(fm);
        this.fm = fm;
        mContext = activity;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mFraments = new ArrayList<>();
    }

    public void addTab(String tag, String name, Class<?> clss, Bundle args) {
        TabInfo info = new TabInfo(tag, name, clss, args);
        mTabs.add(info);
        notifyDataSetChanged();
    }

    /**
     * 动态更新tab的数字信息
     * @param name the new name xxxxx(3)
     * @param pos   current position
     */
    public void setTab(String name,int pos){
        mTabs.get(pos).setName(name);
        this.notifyDataSetChanged();
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

        return fm.findFragmentByTag(makeFragmentName(mViewPager.getId(), position));
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(mContext, info.clss.getName(), info.args);

    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
