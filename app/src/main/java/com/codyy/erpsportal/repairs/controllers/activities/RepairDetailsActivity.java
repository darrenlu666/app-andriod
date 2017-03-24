package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.repairs.controllers.fragments.RepairDetailsFragment;
import com.codyy.erpsportal.repairs.controllers.fragments.RepairTrackingFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 报修详情
 */
public class RepairDetailsActivity extends AppCompatActivity {

    private final static String TAG = "RepairDetailsActivity";

    private final static String EXTRA_REPAIR_ID = "com.codyy.erpsportal.EXTRA_REPAIR_ID";

    @Bind(R.id.titleBar)
    TitleBar mTitleBar;

    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;

    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    private String mReportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_details);
        ButterKnife.bind(this);
        TabsAdapter tabsAdapter = new TabsAdapter(this, getSupportFragmentManager(), mViewPager);
        mViewPager.setAdapter(tabsAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        tabsAdapter.addTab("报修信息", RepairDetailsFragment.class, null);
        tabsAdapter.addTab("问题追踪", RepairTrackingFragment.class, null);
    }

    public static void start(Context context, String repairId){
        Intent intent = new Intent(context, RepairDetailsActivity.class);
        intent.putExtra(EXTRA_REPAIR_ID, repairId);
        context.startActivity(intent);
    }
}
