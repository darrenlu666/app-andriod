package com.codyy.erpsportal.county.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ChannelAdapter;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;

/**
 * Created by kmdai on 16-7-4.
 */
public class CountyAllListFragment extends Fragment {
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private ChannelAdapter mChannelAdapter;
    private OnPageChange mOnPageChange;

    public static CountyAllListFragment newInstance() {

        Bundle args = new Bundle();

        CountyAllListFragment fragment = new CountyAllListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnPageChange(OnPageChange mOnPageChange) {
        this.mOnPageChange = mOnPageChange;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conty_list_all, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.conty_lsit_viewpager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.conty_list_sliding);
        mSlidingTabLayout.setAverageTabs(true);
        mChannelAdapter = new ChannelAdapter(getActivity(), getChildFragmentManager(), mViewPager);
        Bundle plan = new Bundle();
        plan.putInt(ContyListFragment.EXTRA_TYPE, ContyListFragment.TYPE_PLAN);
        mChannelAdapter.addTab("计划开课", ContyListFragment.class, plan);
        Bundle liberty = new Bundle();
        liberty.putInt(ContyListFragment.EXTRA_TYPE, ContyListFragment.TYPE_LIBERTY);
        mChannelAdapter.addTab("自主开课", ContyListFragment.class, liberty);
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mOnPageChange != null) {
                    mOnPageChange.OnPageChange(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * @param areaid
     * @param schoolId
     * @param isDirect
     */
    public void onFilter(String areaid, String schoolId, boolean isDirect) {
        for (int i = 0; i < mChannelAdapter.getCount(); i++) {
            ContyListFragment fragment = (ContyListFragment) mChannelAdapter.getFragmentAt(i);
            if (fragment != null) {
                fragment.onFilter(areaid, schoolId, isDirect);
            }
        }
    }

    public interface OnPageChange {
        void OnPageChange(int position);
    }
}
