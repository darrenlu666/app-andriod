package com.codyy.erpsportal.tutorship.controllers.activities;

import android.os.Bundle;
import android.text.TextUtils;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.controllers.fragments.BaseFilterFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.tutorship.controllers.fragments.TutorialFilterFragment;
import com.codyy.erpsportal.tutorship.controllers.fragments.TutorialsTestsFragment;
import com.codyy.erpsportal.tutorship.controllers.fragments.TutorshipListFragment;

/**
 * 辅导
 * Created by gujiajia on 2015/12/18.
 */
public class TutorshipActivity extends TabsWithFilterActivity {

    private UserInfo mUserInfo;

    @Override
    protected void onInitData() {
        super.onInitData();
        mUserInfo = getIntent().getParcelableExtra(Constants.USER_INFO);
    }

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setCustomTitle(Titles.sWorkspaceTutor);
    }

    @Override
    protected void addFragments() {
        if (mUserInfo.isSchool()) {
            addFragment(Titles.sWorkspaceTutorTution, TutorshipListFragment.class, createBundle(TutorshipListFragment.TYPE_OPEN));
            addFragment(Titles.sWorkspaceTutorListen, TutorshipListFragment.class, createBundle(TutorshipListFragment.TYPE_LISTEN));
        } else if(mUserInfo.isTeacher()) {
            addFragment(Titles.sWorkspaceTutorClsList, TutorshipListFragment.class, createBundle());
            addFragment(Titles.sWorkspaceTutorTest, TutorialsTestsFragment.class, createBundle());
        } else {
            addFragment("", TutorshipListFragment.class, createBundle());
        }
    }

    private Bundle createBundle() {
        return createBundle(null);
    }

    private Bundle createBundle(String type) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USER_INFO, mUserInfo);
        if (!TextUtils.isEmpty(type)) {
            bundle.putString(TutorshipListFragment.ARG_TYPE, type);
        }
        return bundle;
    }

    @Override
    protected void addFilterFragments() {
        if (mUserInfo.isSchool()) {
            addFilterFragment(0, TutorialFilterFragment.newInstance(mUserInfo, null));
            addFilterFragment(1, TutorialFilterFragment.newInstance(mUserInfo, null));
        } else if (mUserInfo.isTeacher()) {
            addFilterFragment(0, TutorialFilterFragment.newInstance(mUserInfo, null));
            addFilterFragment(1, BaseFilterFragment.newInstance(mUserInfo));
        } else {
            addFilterFragment(0, TutorialFilterFragment.newInstance(mUserInfo));
        }
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
}
