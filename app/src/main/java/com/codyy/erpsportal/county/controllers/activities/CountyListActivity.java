package com.codyy.erpsportal.county.controllers.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.FilterFragment;
import com.codyy.erpsportal.county.controllers.fragments.CountyAllListFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.AreaBase;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CountyListActivity extends AppCompatActivity {
    @Bind(R.id.title_text)
    TextView mTextView;
    @Bind(R.id.drawerlayout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.title_filter_tv)
    TextView mFilterTextView;
    @Bind(R.id.title_filter)
    ImageView mFilterIV;
    private FilterFragment mFilterFragment;
    private CountyAllListFragment mCountyAllListFragment;
    /**
     * table页签切换筛选同步
     */
    private List<FilterFragment> mFilterFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_class);
        ButterKnife.bind(this);
        mFilterFragments = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            FilterFragment filterFragment = new FilterFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", FilterFragment.SCREEN_TIMETABLEVIEW);
            bundle.putParcelable("userInfo", UserInfoKeeper.obtainUserInfo());
            filterFragment.setArguments(bundle);
            mFilterFragments.add(filterFragment);
        }
        mFilterFragment = mFilterFragments.get(0);
        getSupportFragmentManager().beginTransaction().replace(R.id.filter_fragment, mFilterFragment).commitAllowingStateLoss();
        init();
        mCountyAllListFragment = CountyAllListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_fraglayout, mCountyAllListFragment).commitAllowingStateLoss();
        mCountyAllListFragment.setOnPageChange(new CountyAllListFragment.OnPageChange() {
            @Override
            public void OnPageChange(int position) {
                mFilterFragment = mFilterFragments.get(0);
//                getSupportFragmentManager().beginTransaction().replace(R.id.filter_fragment, mFilterFragment).commitAllowingStateLoss();
            }
        });
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (mFilterIV.getVisibility() == View.VISIBLE) {
                    mFilterIV.setAlpha(1 - slideOffset);
                    mFilterTextView.setAlpha(slideOffset);
                    if (mFilterTextView.getVisibility() == View.GONE) {
                        mFilterTextView.setVisibility(View.VISIBLE);
                    }

                } else if (mFilterTextView.getVisibility() == View.VISIBLE) {
                    mFilterTextView.setAlpha(slideOffset);
                    mFilterIV.setAlpha(1 - slideOffset);
                    if (mFilterIV.getVisibility() == View.GONE) {
                        mFilterIV.setVisibility(View.VISIBLE);
                    }

                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mFilterIV.setVisibility(View.GONE);
                mFilterTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mFilterTextView.setVisibility(View.GONE);
                mFilterIV.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    protected void init() {
        mTextView.setText(Titles.sWorkspaceSpeclassAllTable);
    }

    public void onFilterSelect(View view) {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
            AreaBase areaBase = mFilterFragment.getLastArea();
            if ("area".equals(areaBase.getType())) {
                onFilter(areaBase.getAreaId(), null, areaBase.isDirect);
            } else {
                onFilter(areaBase.getAreaId(), areaBase.getSchoolID(), areaBase.isDirect);
            }
        } else {
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    public void onFilter(String areaID, String schoolID, boolean isDirect) {
        mCountyAllListFragment.onFilter(areaID, schoolID, isDirect);
    }

    public void onBack(View view) {
        this.finish();
    }
}
