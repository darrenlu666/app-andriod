package com.codyy.erpsportal.homework.controllers.activities;

import android.os.Bundle;

import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.fragment.ClassFilterFragment;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.homework.controllers.fragments.parent.WorkListParFragment;
import com.codyy.erpsportal.homework.controllers.fragments.student.WorkListStuReadFragment;
import com.codyy.erpsportal.homework.controllers.fragments.student.WorkListStuWorkFragment;
import com.codyy.erpsportal.homework.controllers.fragments.teacher.WorkListTeacherFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

import java.util.ArrayList;

public class WorkListsActivity extends TabsWithFilterActivity {
    private final static String ARG_ROLE = "userRole";
    private static final int INDEX_ZERO = 0;
    private static final int INDEX_ONE = 1;
    private UserInfo mUserInfo;

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setCustomTitle(Titles.sWorkspaceHomework);
    }

    @Override
    protected void addFragments() {
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        if (mUserInfo != null) {
            switch (mUserInfo.getUserType()) {
                case UserInfo.USER_TYPE_AREA_USER:
                    addFragment("", WorkListTeacherFragment.class,null);
                    break;
                case UserInfo.USER_TYPE_TEACHER:
                    addFragment("", WorkListTeacherFragment.class, null);
                    break;
                case UserInfo.USER_TYPE_PARENT:
                    Bundle parBundle = new Bundle();
                    parBundle.putString(ARG_ROLE,UserInfo.USER_TYPE_PARENT);
                    addFragment("",WorkListParFragment.class,parBundle);//家长作业中心 同学生-我的作业
                    break;
                case UserInfo.USER_TYPE_STUDENT:
                    Bundle stuBundle = new Bundle();
                    stuBundle.putString(ARG_ROLE,UserInfo.USER_TYPE_STUDENT);
                    addFragment(Titles.sWorkspaceHomeworkList, WorkListStuWorkFragment.class, stuBundle);//学生-我的作业  getResources().getString(R.string.my_work_list)
                    addFragment(Titles.sWorkspaceHomeworkReadOver, WorkListStuReadFragment.class, null);//学生-我的批阅 getResources().getString(R.string.my_read)
                    break;
            }
        }
    }

    @Override
    protected void addFilterFragments() {
        ArrayList<FilterItem> items;
        if (mUserInfo != null)
            switch (mUserInfo.getUserType()) {
                case UserInfo.USER_TYPE_TEACHER:
                    items = new ArrayList<>();
                    items.add(new FilterItem("学科", "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT));
                    items.add(new FilterItem("状态", "teaState", null, FilterItem.ARRAY));
                    addFilterFragment(INDEX_ZERO, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    break;
                case UserInfo.USER_TYPE_STUDENT:
                    items = new ArrayList<>();
                    items.add(new FilterItem("学科", "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT));
                    items.add(new FilterItem("状态", "stuState", null, FilterItem.ARRAY));
                    addFilterFragment(INDEX_ZERO, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    items = new ArrayList<>();
                    items.add(new FilterItem("学科", "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT));
                    items.add(new FilterItem("状态", "stuSelfState", null, FilterItem.ARRAY));
                    addFilterFragment(INDEX_ONE, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    break;
                case UserInfo.USER_TYPE_PARENT:
                    items = new ArrayList<>();
                    items.add(new FilterItem("学科", "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT));
                    items.add(new FilterItem("状态", "stuState", null, FilterItem.ARRAY));
                    addFilterFragment(INDEX_ZERO, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
                    break;
            }
    }
}
