package com.codyy.erpsportal.homework.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.fragment.ClassFilterFragment;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.homework.controllers.fragments.classSpace.WorkListClassSpaceFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

import java.util.ArrayList;

/**
 * 班级空间作业列表
 * Created by ldh on 2016/3/6.
 */
public class WorkListsClassSpaceActivity extends TabsWithFilterActivity{

    public final static String EXTRA_CLASS_ID = "classId";
    public final static String EXTRA_CLASS_NAME = "className";
    private static final int INDEX_ZERO = 0;
    private UserInfo mUserInfo;

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setCustomTitle(getResources().getString(R.string.work_title));
    }

    @Override
    protected void addFilterFragments() {
        ArrayList<FilterItem> items = new ArrayList<>();
        items.add(new FilterItem("学科", "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT));
        items.add(new FilterItem("状态", "teaState", null, FilterItem.ARRAY));
        addFilterFragment(INDEX_ZERO, ClassFilterFragment.newInstance(items, mUserInfo.getUuid()));
    }

    @Override
    protected void addFragments() {
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CLASS_ID,getIntent().getExtras().getString(EXTRA_CLASS_ID));
        bundle.putString(EXTRA_CLASS_NAME,getIntent().getExtras().getString(EXTRA_CLASS_NAME));
        addFragment("", WorkListClassSpaceFragment.class,bundle);
    }

    public static void startActivity(Context context,String classId,String className){
        Intent intent = new Intent(context,WorkListsClassSpaceActivity.class);
        intent.putExtra(EXTRA_CLASS_ID,classId);
        intent.putExtra(EXTRA_CLASS_NAME,className);
        context.startActivity(intent);
    }
}
