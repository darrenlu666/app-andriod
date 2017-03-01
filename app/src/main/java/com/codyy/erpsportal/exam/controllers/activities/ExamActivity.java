package com.codyy.erpsportal.exam.controllers.activities;


import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.fragment.ClassFilterFragment;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.exam.controllers.fragments.parents.ParentsStatisticsFragment;
import com.codyy.erpsportal.exam.controllers.fragments.parents.ParentsTaskFragment;
import com.codyy.erpsportal.exam.controllers.fragments.school.SchoolClassFragment;
import com.codyy.erpsportal.exam.controllers.fragments.school.SchoolGradeFragment;
import com.codyy.erpsportal.exam.controllers.fragments.student.SelfTaskFragment;
import com.codyy.erpsportal.exam.controllers.fragments.student.TaskFragment;
import com.codyy.erpsportal.exam.controllers.fragments.teacher.ExamFragment;
import com.codyy.erpsportal.exam.controllers.fragments.teacher.MineFragment;
import com.codyy.erpsportal.exam.controllers.fragments.teacher.RealFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * 测试
 * Created by eachann on 2015/12/24.
 */
public class ExamActivity extends TabsWithFilterActivity {

    private SharedPreferences mSharedPreferences;
    private static final String SHOW_MASK_STU = "stu_show_mask";
    private static final String SHOW_MASK_TEA = "tea_show_mask";
    private UserInfo mUserInfo;
    @Bind(R.id.rl_exam_task)
    RelativeLayout mMaskRl;
    @Bind(R.id.iv_close)
    ImageView mIvClose;

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setViewAnim(false, mTitle);
        setCustomTitle(Titles.sWorkspaceTest);
        initSharedObject();
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaskRl.setVisibility(View.GONE);
            }
        });
    }

    private void initSharedObject() {
        mSharedPreferences = getSharedPreferences("setting", 0);
        if (mSharedPreferences == null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(SHOW_MASK_STU, false);
            editor.putBoolean(SHOW_MASK_TEA, false);
            editor.commit();
        }
    }

    private void setSharedObject(String key, boolean value) {
        mSharedPreferences = getSharedPreferences("setting", 0);
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    private boolean getSharedObject(String key) {
        mSharedPreferences = getSharedPreferences("setting", 0);
        if (mSharedPreferences != null) {
            return mSharedPreferences.getBoolean(key, false);
        }
        return false;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        super.initToolbar(toolbar);
    }

    @Override
    protected void addFragments() {
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        if (mUserInfo != null)
            switch (mUserInfo.getUserType()) {
                case UserInfo.USER_TYPE_SCHOOL_USER:
                    addFragment(Titles.sWorkspaceTestGrade, SchoolGradeFragment.class, null);
                    addFragment(Titles.sWorkspaceTestClass, SchoolClassFragment.class, null);
                    break;
                case UserInfo.USER_TYPE_TEACHER:
                    addFragment(Titles.sWorkspaceTestSelf, MineFragment.class, null);
                    addFragment(Titles.sWorkspaceTestSubject, RealFragment.class, null);
                    addFragment(Titles.sWorkspaceTestTask, ExamFragment.class, null);
                    break;
                case UserInfo.USER_TYPE_STUDENT:
                    addFragment(Titles.sWorkspaceTestTask, TaskFragment.class, null);
                    addFragment(Titles.sWorkspaceTestAuto, SelfTaskFragment.class, null);
                    if (!getSharedObject(SHOW_MASK_STU)) {
                        mMaskRl.setVisibility(View.VISIBLE);
                        setSharedObject(SHOW_MASK_STU, true);
                    }
                    break;
                case UserInfo.USER_TYPE_PARENT:
                    addFragment(getString(R.string.exam_parents_situation), ParentsTaskFragment.class, null);
                    addFragment(getString(R.string.exam_parents_analysis), ParentsStatisticsFragment.class, null);
                    break;
            }
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        if (mUserInfo != null) {
            switch (mUserInfo.getUserType()) {
                case UserInfo.USER_TYPE_TEACHER:
                    if (position == 2 && !getSharedObject(SHOW_MASK_TEA)) {
                        mMaskRl.setVisibility(View.VISIBLE);
                        setSharedObject(SHOW_MASK_TEA, true);
                    }
                    break;
                case UserInfo.USER_TYPE_STUDENT:
                    if (position == 0 && !getSharedObject(SHOW_MASK_STU)) {
                        mMaskRl.setVisibility(View.VISIBLE);
                        setSharedObject(SHOW_MASK_STU, true);
                    }
                    break;
            }
        }
    }

    private static final int INDEX_ZERO = 0;
    private static final int INDEX_ONE = 1;
    private static final int INDEX_TWO = 2;

    @Override
    protected void addFilterFragments() {
        ArrayList<FilterItem> items;
        if (mUserInfo != null)
            switch (mUserInfo.getUserType()) {
                case UserInfo.USER_TYPE_SCHOOL_USER:
                    items = new ArrayList<>();
                    items.add(new FilterItem(getString(R.string.exam_grade), "classLevelId", URLConfig.ALL_CLASS_LEVEL_BY_SCHOOL_ID, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_CLASS_LEVEL ClassLevelJsonParser()
                    items.add(new FilterItem(getString(R.string.exam_subject), "subjectId", URLConfig.ALL_SUBJECTS_BY_CLASS_ID, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_SUBJECTS_LIST  FilterItem.ARRAY
                    addFilterFragment(INDEX_ZERO, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    items = new ArrayList<>();
                    items.add(new FilterItem(getString(R.string.exam_grade), "classLevelId", URLConfig.ALL_CLASS_LEVEL_BY_SCHOOL_ID, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_CLASS_LEVEL ClassLevelJsonParser()
                    items.add(new FilterItem(getString(R.string.exam_subject), "subjectId", URLConfig.ALL_SUBJECTS_BY_CLASS_ID, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_SUBJECTS_LIST  FilterItem.ARRAY
                    items.add(new FilterItem(getString(R.string.exam_class), "classId", URLConfig.ALL_CLASS_BY_CLASSLEVEL_ID, FilterItem.OBJECT, new Choice.BaseClassParser()));
                    items.add(new FilterItem(getString(R.string.exam_state), "schoolClsState", null, FilterItem.ARRAY));
                    addFilterFragment(INDEX_ONE, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    break;
                case UserInfo.USER_TYPE_TEACHER:
                    items = new ArrayList<>();
                    items.add(new FilterItem(getString(R.string.exam_grade), "classLevelId", URLConfig.ALL_CLASS_LEVEL_BY_SCHOOL_ID, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_CLASS_LEVEL ClassLevelJsonParser()
                    items.add(new FilterItem(getString(R.string.exam_subject), "subjectId", URLConfig.ALL_SUBJECTS_BY_CLASS_ID, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_SUBJECTS_LIST  FilterItem.ARRAY
                    addFilterFragment(INDEX_ZERO, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    items = new ArrayList<>();
                    items.add(new FilterItem(getString(R.string.exam_area), "areaName", null, FilterItem.ARRAY));
                    items.add(new FilterItem(getString(R.string.exam_year), "year", null, FilterItem.ARRAY));
                    items.add(new FilterItem(getString(R.string.exam_grade), "classLevelId", URLConfig.ALL_CLASS_LEVEL_BY_SCHOOL_ID, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_CLASS_LEVEL ClassLevelJsonParser()
                    items.add(new FilterItem(getString(R.string.exam_subject), "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_SUBJECTS_LIST  FilterItem.ARRAY
                    addFilterFragment(INDEX_ONE, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    items = new ArrayList<>();
                    items.add(new FilterItem(getString(R.string.exam_grade), "classLevelId", URLConfig.ALL_CLASS_LEVEL_BY_SCHOOL_ID, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_CLASS_LEVEL ClassLevelJsonParser()
                    items.add(new FilterItem(getString(R.string.exam_subject), "subjectId", URLConfig.ALL_SUBJECTS_BY_CLASS_ID, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_SUBJECTS_LIST  FilterItem.ARRAY
                    addFilterFragment(INDEX_TWO, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    break;
                case UserInfo.USER_TYPE_STUDENT:
                    items = new ArrayList<>();
                    items.add(new FilterItem(getString(R.string.exam_subject), "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_SUBJECTS_LIST  FilterItem.ARRAY
                    items.add(new FilterItem(getString(R.string.exam_state), "state", null, FilterItem.ARRAY));
                    addFilterFragment(INDEX_ZERO, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    items = new ArrayList<>();
                    items.add(new FilterItem(getString(R.string.exam_subject), "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_SUBJECTS_LIST  FilterItem.ARRAY
                    items.add(new FilterItem(getString(R.string.exam_state), "stateSelf", null, FilterItem.ARRAY));
                    addFilterFragment(INDEX_ONE, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    break;
                case UserInfo.USER_TYPE_PARENT:
                    items = new ArrayList<>();
                    items.add(new FilterItem(getString(R.string.exam_subject), "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_SUBJECTS_LIST  FilterItem.ARRAY
                    items.add(new FilterItem(getString(R.string.exam_state), "parState", null, FilterItem.ARRAY));
                    addFilterFragment(INDEX_ZERO, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    //addFilterFragment(INDEX_ONE, ExamFilterFragment.newInstance());
                    break;
            }
    }
}
