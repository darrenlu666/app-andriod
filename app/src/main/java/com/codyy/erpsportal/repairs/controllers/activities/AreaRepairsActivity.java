package com.codyy.erpsportal.repairs.controllers.activities;

import android.os.Bundle;

import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.models.entities.AreaFilterItem;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.RxBus;
import com.codyy.erpsportal.repairs.controllers.fragments.AreaFilterFragment;
import com.codyy.erpsportal.repairs.controllers.fragments.SchoolRepairListFragment;
import com.codyy.erpsportal.repairs.controllers.fragments.SchoolRepairListFragment.ShowAreaPrefixFlag;
import com.codyy.erpsportal.repairs.utils.AreasFetcher;
import com.codyy.erpsportal.repairs.utils.AreasFetcher.OnAreasFetchedListener;

/**
 * 地区报修，显示有报修记录的学校列表
 */
public class AreaRepairsActivity extends TabsWithFilterActivity {

    private UserInfo mUserInfo;

    @Override
    protected void onInitData() {
        super.onInitData();
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
    }

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setCustomTitle("报修信息跟踪");
        if (mUserInfo.isArea()) {
            AreasFetcher areasFetcher = new AreasFetcher();
            areasFetcher.setOnAreasFetchedListener(new OnAreasFetchedListener() {
                @Override
                public void onNoAreaFetched(AreaFilterItem areaFilterItem) {
                    RxBus.getInstance().send(new ShowAreaPrefixFlag(false));
                }

                @Override
                public void onAreasFetched(AreaFilterItem areaFilterItem) {
                    addFilterFragment(0, AreaFilterFragment.newInstance(areaFilterItem));
                    RxBus.getInstance().send(new ShowAreaPrefixFlag(true));
                }
            });
            areasFetcher.fetchAreaFilterItem(mUserInfo.getBaseAreaId());
        }
    }

    @Override
    protected void addFragments() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Extra.USER_INFO, mUserInfo);
        addFragment("学校列表", SchoolRepairListFragment.class, bundle);
    }

    @Override
    protected void addFilterFragments() { }
}
