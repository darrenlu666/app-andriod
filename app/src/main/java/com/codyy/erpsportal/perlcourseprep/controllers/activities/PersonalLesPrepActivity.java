package com.codyy.erpsportal.perlcourseprep.controllers.activities;

import android.os.Bundle;

import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.perlcourseprep.controllers.fragments.LessonPlanListFragment;
import com.codyy.erpsportal.perlcourseprep.controllers.fragments.SubjectMaterialFragment;
import com.codyy.erpsportal.commons.controllers.fragments.BaseFilterFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

/**
 * 个人备课界面
 * Created by gujiajia on 2016/1/18.
 */
public class PersonalLesPrepActivity extends TabsWithFilterActivity {

    private UserInfo mUserInfo;

    @Override
    protected void onInitData() {
        mUserInfo = UserInfoKeeper.obtainUserInfo();
    }

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setCustomTitle(Titles.sWorkspacePrepareLesson);
    }

    @Override
    protected void addFragments() {
        if (mUserInfo.isTeacher()) {
            Bundle bundle1 = new Bundle();
            bundle1.putParcelable(LessonPlanListFragment.ARG_USER_INFO, mUserInfo);
            addFragment(Titles.sWorkspacePrepareLessonPrepareLesson, LessonPlanListFragment.class, bundle1);

            Bundle bundle = new Bundle();
            bundle.putParcelable(SubjectMaterialFragment.ARG_USER_INFO, mUserInfo);
            addFragment(Titles.sWorkspacePrepareLessonPrepareImage, SubjectMaterialFragment.class, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(LessonPlanListFragment.ARG_USER_INFO, mUserInfo);
            addFragment(Titles.sWorkspacePrepareLessonPrepareLesson, LessonPlanListFragment.class, bundle);
        }
    }

    @Override
    protected void addFilterFragments() {
        if (mUserInfo.isTeacher()) {
            addFilterFragment(0, BaseFilterFragment.newInstance(mUserInfo));
            addFilterFragment(1, BaseFilterFragment.newInstance(mUserInfo));
        } else if (mUserInfo.isSchool()){
            addFilterFragment(0, BaseFilterFragment.newInstance(mUserInfo));
        } else {
            addFilterFragment(0, BaseFilterFragment.newInstance(mUserInfo));
        }
    }
}
