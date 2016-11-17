package com.codyy.erpsportal.rethink.controllers.activities;

import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.rethink.controllers.fragments.RethinkFilterFragment;
import com.codyy.erpsportal.rethink.controllers.fragments.RethinkListFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

/**
 * 教学反思列表Activity
 * Created by gujiajia on 2015/12/31.
 */
public class RethinkListActivity extends TabsWithFilterActivity {

    protected UserInfo mUserInfo;

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setCustomTitle(Titles.sWorkspaceRethink);
    }

    @Override
    protected void onInitData() {
        super.onInitData();
        mUserInfo = UserInfoKeeper.obtainUserInfo();
    }

    @Override
    protected void addFragments() {
        addFragment("开课列表", RethinkListFragment.class, null);
    }

    @Override
    protected void addFilterFragments() {
        addFilterFragment(0 , RethinkFilterFragment.newInstance(mUserInfo));
    }
}
