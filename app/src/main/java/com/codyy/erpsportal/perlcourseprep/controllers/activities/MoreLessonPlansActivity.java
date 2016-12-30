package com.codyy.erpsportal.perlcourseprep.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.controllers.fragments.BaseFilterFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.UpOrDownButton;
import com.codyy.erpsportal.commons.widgets.UpOrDownButton.OnStateChangedListener;
import com.codyy.erpsportal.perlcourseprep.controllers.fragments.MoreLessonPlansFragment;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;

import butterknife.Bind;

public class MoreLessonPlansActivity extends ToolbarActivity {

    private final static String TAG = "MoreLessonPlansActivity";

    /**
     * 标题栏
     */
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Bind(R.id.toolbar_title)
    protected TextView mTitleTv;

    /**
     * 侧滑抽屉
     */
    @Bind(R.id.filter_drawer_layout)
    protected DrawerLayout mDrawerLayout;

    @Bind(R.id.fl_content)
    protected FrameLayout mContentLayout;

    /**
     * 按发布时间
     */
//    public static final String BY_TIME = "TIME";
    public static final String BY_TIME = "createTimeSort";

    /**
     * 按评分
     */
//    public static final String BY_RATING = "SCORE";
    public static final String BY_RATING = "avgScoreSort";

    /**
     * 按热度
     */
//    public static final String BY_HEAT = "VIEW";
    public static final String BY_HEAT = "viewCountSort";

    /**
     * 升序
     */
    public static final String ORDER_ASC = "ASC";

    /**
     * 降序
     */
    public static final String ORDER_DESC = "DESC";

    @Bind(R.id.btn_by_time)
    UpOrDownButton mByTimeBtn;

    @Bind(R.id.btn_by_rate)
    UpOrDownButton mByRateBtn;

    @Bind(R.id.btn_by_heat)
    UpOrDownButton mByHeatBtn;

    private String mAreaId;

    private String mSchoolId;

    private MoreLessonPlansFragment mFragment;

    private BaseFilterFragment mFilterFragment;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_more_personal_les_prep;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
        mTitleTv.setText(Titles.sPagetitleNetteachPrepare);
        mAreaId = getIntent().getStringExtra(Constants.EXTRA_AREA_ID);
        mSchoolId = getIntent().getStringExtra(Constants.EXTRA_SCHOOL_ID);
        mFragment = MoreLessonPlansFragment.newInstance(mAreaId, mSchoolId, "", "", ORDER_DESC, "");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_content, mFragment);
        ft.commit();
        AreaInfo areaInfo;
        if ( !TextUtils.isEmpty(mSchoolId)) {
            areaInfo = new AreaInfo(mSchoolId, AreaInfo.TYPE_SCHOOL);
        } else {
            areaInfo = new AreaInfo(mAreaId, AreaInfo.TYPE_AREA);
        }

        mFilterFragment = BaseFilterFragment.newInstance(areaInfo, null);
        replaceFilterFragment(mFilterFragment);
        initSortBtns();

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        });
    }

    private void initSortBtns() {
        mByTimeBtn.setStateChangedListener(new OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                mByRateBtn.setChecked(false);
                mByHeatBtn.setChecked(false);
                loadDataOrderBy(BY_TIME, isUp ? ORDER_ASC : ORDER_DESC);
            }

            @Override
            public void onUp() {
                loadDataOrderBy(BY_TIME, ORDER_ASC);
            }

            @Override
            public void onDown() {
                loadDataOrderBy(BY_TIME, ORDER_DESC);
            }
        });

        mByRateBtn.setStateChangedListener(new UpOrDownButton.OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                mByTimeBtn.setChecked(false);
                mByHeatBtn.setChecked(false);
                loadDataOrderBy(BY_RATING, isUp ? ORDER_ASC : ORDER_DESC);
            }

            @Override
            public void onUp() {
                loadDataOrderBy(BY_RATING, ORDER_ASC);
            }

            @Override
            public void onDown() {
                loadDataOrderBy(BY_RATING, ORDER_DESC);
            }
        });

        mByHeatBtn.setStateChangedListener(new UpOrDownButton.OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                mByTimeBtn.setChecked(false);
                mByRateBtn.setChecked(false);
                loadDataOrderBy(BY_HEAT, isUp ? ORDER_ASC : ORDER_DESC);
            }

            @Override
            public void onUp() {
                loadDataOrderBy(BY_HEAT, ORDER_ASC);
            }

            @Override
            public void onDown() {
                loadDataOrderBy(BY_HEAT, ORDER_DESC);
            }
        });
    }

    public void loadDataOrderBy(String by, String order) {
        Cog.d(TAG, "loadDataOrderBy orderBy=", by, " ", order);
        mFragment.addParam("sortType", by);
        mFragment.addParam("orderType", order);
        mFragment.loadData(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
//            menu.getItem(0).setIcon(R.drawable.ic_done_white);
//        } else {
//            menu.getItem(0).setIcon(R.drawable.ic_filter);
//        }

        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            menu.getItem(0).setActionView(R.layout.textview_filter_confirm_button);
            TextView tv = (TextView) menu.getItem(0).getActionView().findViewById(R.id.tv_title);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                    doFilterConfirmed();
                }
            });
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_filter);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                } else {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                    doFilterConfirmed();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 执行过滤动作
     */
    protected void doFilterConfirmed() {
        mFragment.onFilterConfirmed(mFilterFragment.acquireFilterParams());
    }

    protected void replaceFilterFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_filter, fragment);
        ft.commit();
    }

    public static void start(Activity activity, String baseAreaId, String schoolId) {
        Intent intent = new Intent(activity, MoreLessonPlansActivity.class);
        intent.putExtra(Constants.EXTRA_AREA_ID, baseAreaId);
        intent.putExtra(Constants.EXTRA_SCHOOL_ID, schoolId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

}
