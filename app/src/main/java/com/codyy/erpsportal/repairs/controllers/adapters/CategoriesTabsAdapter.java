package com.codyy.erpsportal.repairs.controllers.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.codyy.erpsportal.repairs.controllers.fragments.MalfuncCategoriesFragment;
import com.codyy.erpsportal.repairs.models.entities.CategoriesPageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签下页面适配器
 * Created by G++
 */
public  class CategoriesTabsAdapter extends FragmentStatePagerAdapter {
    private final Context mContext;
    private final ViewPager mViewPager;
    private final List<TabInfo> mTabs = new ArrayList<>();

    private final SparseArray<Fragment> mFragments = new SparseArray<>();

    public void update(int position, CategoriesPageInfo categoriesPageInfo) {
        if (position < 0 || position >= mTabs.size()) {
            return;
        }
        mTabs.get(position).categoriesPageInfo = categoriesPageInfo;
    }

    static final class TabInfo {

        private CategoriesPageInfo categoriesPageInfo;

        TabInfo(CategoriesPageInfo _info) {
            categoriesPageInfo = _info;
        }

        private String getTitle() {
            return categoriesPageInfo.getSelectedName();
        }
    }

    public CategoriesTabsAdapter(FragmentActivity activity, FragmentManager fm, ViewPager pager) {
        super(fm);
        mContext = activity;
        mViewPager = pager;
        mViewPager.setAdapter(this);
    }

    public void addTab(CategoriesPageInfo pageInfo) {
        TabInfo info = new TabInfo(pageInfo);
        mTabs.add(info);
        notifyDataSetChanged();
    }

    public void addTabs(List<CategoriesPageInfo> pageInfos) {
        if (pageInfos != null && pageInfos.size() > 0) {
            for (CategoriesPageInfo pageInfo: pageInfos) {
                TabInfo info = new TabInfo(pageInfo);
                mTabs.add(info);
            }
            notifyDataSetChanged();
        }
    }

    public void remove(int start) {
        while (mTabs.size() > start) {
            int index = mTabs.size() - 1;
            mTabs.remove( index);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取所在位置的碎片
     * @param position 位置
     * @return 所在位置的碎片
     */
    public Fragment getFragmentAt(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MalfuncCategoriesFragment fragment = (MalfuncCategoriesFragment) super.instantiateItem(container,
                position);
        mFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        MalfuncCategoriesFragment fragment = (MalfuncCategoriesFragment) Fragment
                .instantiate(mContext, MalfuncCategoriesFragment.class.getName(), null);
        fragment.setInfo(info.categoriesPageInfo);
        mFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mFragments.remove(position);
    }
}
