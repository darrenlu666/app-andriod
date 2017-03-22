package com.codyy.erpsportal.repairs.controllers.activities;

import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.repairs.controllers.fragments.AreaFilterFragment;
import com.codyy.erpsportal.repairs.controllers.fragments.SchoolRepairListFragment;

/**
 * 地区报修，显示有报修记录的学校列表
 */
public class AreaRepairsActivity extends TabsWithFilterActivity {

    private UserInfo mUserInfo;

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setCustomTitle("报修信息跟踪");
    }

    @Override
    protected void onInitData() {
        super.onInitData();
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
    }

    @Override
    protected void addFragments() {
        addFragment("学校列表", SchoolRepairListFragment.class, null);
    }

    @Override
    protected void addFilterFragments() {
        addFilterFragment(0 , AreaFilterFragment.newInstance(mUserInfo));
    }
}
