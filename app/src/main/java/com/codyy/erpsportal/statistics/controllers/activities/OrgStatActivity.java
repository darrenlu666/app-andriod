package com.codyy.erpsportal.statistics.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TabsActivity;
import com.codyy.erpsportal.statistics.controllers.fragments.StatisticalFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * 机构课堂统计
 */
public class OrgStatActivity extends TabsActivity {

    private static final String TAG = "OrgStatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomTitle(Titles.sWorkspaceCountClass);
    }

    @Override
    protected void addFragments() {
        UserInfo userInfo = null;
        if (getIntent() != null) {
            userInfo = getIntent().getParcelableExtra(StatisticalFragment.ARG_USER_INFO);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(StatisticalFragment.ARG_USER_INFO, userInfo);
        bundle.putInt(StatisticalFragment.ARG_TYPE, StatisticalFragment.TYPE_CLASS_DAY);
        addFragment( getString(R.string.count_lessons_today), StatisticalFragment.class, bundle);

        Bundle bundle1 = new Bundle();
        bundle1.putParcelable(StatisticalFragment.ARG_USER_INFO, userInfo);
        bundle1.putInt(StatisticalFragment.ARG_TYPE, StatisticalFragment.TYPE_CLASS_TERM);
        addFragment( getString(R.string.count_lessons_semester), StatisticalFragment.class, bundle1);
    }

    public static void start(Activity activity, UserInfo userInfo) {
        Intent intent = new Intent(activity, OrgStatActivity.class);
        intent.putExtra(StatisticalFragment.ARG_USER_INFO, userInfo);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
    }
}
