package com.codyy.erpsportal.rethink.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.rethink.controllers.fragments.RethinkFilterFragment;
import com.codyy.erpsportal.rethink.controllers.fragments.RethinkListFragment;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * 更多教学反思
 * Created by gujiajia on 2016/3/8.
 */
public class MoreRethinkListActivity extends RethinkListActivity {

    private String mSchoolId;

    private String mBaseAreaId;

    @Override
    protected void onInitData() {
        super.onInitData();
        mSchoolId = getIntent().getStringExtra(Extra.SCHOOL_ID);
        mBaseAreaId = getIntent().getStringExtra(Extra.AREA_ID);
    }

    @Override
    protected void addFragments() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(RethinkListFragment.ARG_MORE, true);
        bundle.putString(RethinkListFragment.ARG_SCHOOL_ID, mSchoolId);
        bundle.putString(RethinkListFragment.ARG_BASE_AREA_ID, mBaseAreaId);
        addFragment("开课列表", RethinkListFragment.class, bundle);
    }

    @Override
    protected void addFilterFragments() {
        if (!TextUtils.isEmpty(mBaseAreaId)) {
            addFilterFragment(0, RethinkFilterFragment.newInstance(mUserInfo, mBaseAreaId));
        } else {
            addFilterFragment(0, RethinkFilterFragment.newInstance(mUserInfo, null));
        }
    }

    public static void start(Activity activity, String baseAreaId, String schoolId) {
        Intent intent = new Intent(activity, MoreRethinkListActivity.class);
        intent.putExtra(Extra.SCHOOL_ID, schoolId);
        intent.putExtra(Extra.AREA_ID, baseAreaId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
