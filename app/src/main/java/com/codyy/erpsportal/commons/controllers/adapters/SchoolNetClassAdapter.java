package com.codyy.erpsportal.commons.controllers.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codyy.erpsportal.commons.controllers.fragments.SchoolNetClassFragment;

import java.util.List;

/**
 * Created by ldh on 2015/9/11.
 */
public class SchoolNetClassAdapter extends FragmentPagerAdapter {
    private List<SchoolNetClassFragment> mListFragment ;

    public SchoolNetClassAdapter(FragmentManager fm, List<SchoolNetClassFragment> mListFragment) {
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
