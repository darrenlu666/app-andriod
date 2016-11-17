package com.codyy.erpsportal.commons.controllers.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;


/**
 * 我的直播适配器
 */
public class SimpleFragmentAdapter<T extends Fragment> extends FragmentPagerAdapter {

    private List<T> mListFragment ;

    public SimpleFragmentAdapter(FragmentManager fm , List<T> list) {
        super(fm);
        this.mListFragment  =   list;
    }

    public void addFragment(T frag){
        if(!mListFragment.contains(frag)){
            mListFragment.add(frag);
//            this.notifyDataSetChanged();
        }
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
