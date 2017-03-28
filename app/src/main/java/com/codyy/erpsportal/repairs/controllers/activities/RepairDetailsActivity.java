package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.repairs.controllers.fragments.RepairDetailsFragment;
import com.codyy.erpsportal.repairs.controllers.fragments.RepairTrackingFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.codyy.erpsportal.R.id.tabLayout;

/**
 * 报修详情
 */
public class RepairDetailsActivity extends AppCompatActivity {

    private final static String TAG = "RepairDetailsActivity";

    private final static String EXTRA_REPAIR_ID = "com.codyy.erpsportal.EXTRA_REPAIR_ID";

    @Bind(R.id.titleBar)
    TitleBar mTitleBar;

    @Bind(tabLayout)
    TabLayout mTabLayout;

    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    private String mReportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_details);
        ButterKnife.bind(this);
        mReportId = getIntent().getStringExtra(EXTRA_REPAIR_ID);
        TabsAdapter tabsAdapter = new TabsAdapter(this, getSupportFragmentManager(), mViewPager);
        mViewPager.setAdapter(tabsAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        Bundle bundle1 = new Bundle();
        bundle1.putString(RepairDetailsFragment.ARG_REPAIR_ID, mReportId);
        tabsAdapter.addTab("报修信息", RepairDetailsFragment.class, bundle1);
        tabsAdapter.addTab("问题追踪", RepairTrackingFragment.class, null);
        addDividerBetweenTabs();
    }

    private void addDividerBetweenTabs() {
        View tabsRoot = mTabLayout.getChildAt(0);
        if (tabsRoot instanceof LinearLayout) {
            ((LinearLayout) tabsRoot).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(0xffcccccc);
            drawable.setSize(2, 1);
            ((LinearLayout) tabsRoot).setDividerPadding(getResources().getDimensionPixelSize(R.dimen.dp16));
            ((LinearLayout) tabsRoot).setDividerDrawable(drawable);
        }
    }

    public static void start(Context context, String repairId){
        Intent intent = new Intent(context, RepairDetailsActivity.class);
        intent.putExtra(EXTRA_REPAIR_ID, repairId);
        context.startActivity(intent);
    }
}
