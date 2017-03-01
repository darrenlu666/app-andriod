package com.codyy.erpsportal.classroom.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.fragment.ClassFilterFragment;
import com.codyy.erpsportal.classroom.fragment.NoAreaRecordedListFragment;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.FilterItem;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * 非区域的往期录播列表Activity
 * Created by ldh on 2016/7/5.
 */
public class ClassRecordedNoAreaActivity extends TabsWithFilterActivity {
    private String mSchoolId;
    private String mFrom;
    /**
     * 是否是管理员，是：使用管理员的筛选接口 否：使用普通筛选接口 接口：查询年级信息
     */
    private boolean isAreaUser;
    /**
     * 标题名称
     */
    @Bind(R.id.toolbar_title)
    protected TextView mTitle;
    /**
     * 标题栏
     */
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private void init() {
        mTitle.setText(ClassRoomContants.FROM_WHERE_MODEL.equals(ClassRoomContants.TYPE_CUSTOM_RECORD) ?
                Titles.sWorkspaceSpeclassLive : Titles.sWorkspaceNetClassReplay);
        mSchoolId = getIntent().getExtras().getString(ClassRoomContants.EXTRA_SCHOOL_ID);
        mFrom = getIntent().getStringExtra(ClassRoomContants.FROM_WHERE_MODEL);
        isAreaUser = UserInfoKeeper.obtainUserInfo().isAdmin();
    }

    @Override
    protected void addFilterFragments() {
        ArrayList<FilterItem> items;
        items = new ArrayList<>();
        items.add(new FilterItem(getString(R.string.exam_grade), "classLevelId",URLConfig.ALL_CLASS_LEVEL_BY_SCHOOL_ID,
                FilterItem.OBJECT, new Choice.BaseChoiceParser()));//以前代码 isAreaUser ? URLConfig.ALL_CLASS_LEVEL : URLConfig.ALL_CLASS_LEVEL_BY_SCHOOL_ID
        items.add(new FilterItem(getString(R.string.exam_subject), "subjectId",URLConfig.ALL_SUBJECTS_BY_CLASS_ID , FilterItem.OBJECT, new Choice.BaseChoiceParser()));//URLConfig.ALL_SUBJECTS_LIST
        items.add(new FilterItem(getString(R.string.exam_teacher), "teacherId", URLConfig.GET_TEACHER_BY_CLASS, FilterItem.OBJECT, new Choice.BaseTeacherParser()));
        addFilterFragment(0, ClassFilterFragment.getInstance(items, UserInfoKeeper.obtainUserInfo().getUuid(),mSchoolId));
    }

    @Override
    protected void addFragments() {
        init();
        Bundle bundle = new Bundle();
        bundle.putString(ClassRoomContants.EXTRA_SCHOOL_ID, mSchoolId);
        bundle.putString(ClassRoomContants.FROM_WHERE_MODEL, mFrom);
        addFragment("", NoAreaRecordedListFragment.class, bundle);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

    public static void startActivity(Context context, String schoolId, String from) {
        Intent intent = new Intent(context, ClassRecordedNoAreaActivity.class);
        intent.putExtra(ClassRoomContants.EXTRA_SCHOOL_ID, schoolId);
        intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, from);
        context.startActivity(intent);
    }
}
