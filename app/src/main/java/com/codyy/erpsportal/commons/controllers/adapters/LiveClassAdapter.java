package com.codyy.erpsportal.commons.controllers.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codyy.erpsportal.commons.controllers.fragments.LiveFragment;

import java.util.List;


/**
 * 我的直播适配器
 */
public class LiveClassAdapter extends FragmentPagerAdapter {

    private List<LiveFragment> mListFragment ;

    public LiveClassAdapter(FragmentManager fm, List<LiveFragment> mListFragment) {
        super(fm);
        this.mListFragment  =   mListFragment;
    }

    @Override
    public int getCount() {
        return mListFragment.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mListFragment.get(position);
    }

}
