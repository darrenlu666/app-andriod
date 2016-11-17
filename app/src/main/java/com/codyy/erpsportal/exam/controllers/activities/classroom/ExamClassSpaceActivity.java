package com.codyy.erpsportal.exam.controllers.activities.classroom;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.fragment.ClassFilterFragment;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.exam.controllers.fragments.classspace.ClassSpaceExamFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.UIUtils;

import java.util.ArrayList;

/**
 * 班级空间-测试
 * Created by eachann on 2015/12/24.
 */
public class ExamClassSpaceActivity extends TabsWithFilterActivity {

    private UserInfo mUserInfo;


    @Override
    protected void onViewBound() {
        super.onViewBound();
        setViewAnim(false, mTitle);
        setCustomTitle(getIntent().getStringExtra(EXTRA_NAME));
    }

    @Override
    protected void addFragments() {
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        Bundle bundle = new Bundle();
        bundle.putString("ARG_CLASS_ID", getIntent().getStringExtra(EXTRA_DATA));
        if (mUserInfo != null)
            addFragment(getString(R.string.exam_school_class), ClassSpaceExamFragment.class, bundle);
    }

    public static void start(Activity activity, String classId,String title) {
        Intent intent = new Intent(activity, ExamClassSpaceActivity.class);
        intent.putExtra(EXTRA_DATA, classId);
        intent.putExtra(EXTRA_NAME,title);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    private static final int INDEX_ZERO = 0;

    @Override
    protected void addFilterFragments() {
        ArrayList<FilterItem> items;
        if (mUserInfo != null) {
            items = new ArrayList<>();
            items.add(new FilterItem(getString(R.string.exam_subject), "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_SUBJECTS_LIST  FilterItem.ARRAY
            items.add(new FilterItem(getString(R.string.exam_state), "schoolClsState", null, FilterItem.ARRAY));
            addFilterFragment(INDEX_ZERO, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
        }
    }
}
