package com.codyy.erpsportal.commons.controllers.activities;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.fragment.ClassFilterFragment;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.fragments.RemoteDirectorNewFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.FilterItem;

import java.util.ArrayList;

/**
 * 新的远程导播列表页
 * Created by ldh on 2016/8/1.
 */
public class RemoteDirectorNewActivity extends TabsWithFilterActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setCustomTitle(ClassRoomContants.FROM_WHERE_MODEL.equals(ClassRoomContants.FROM_WHERE_LINE) ? Titles.sWorkspaceSpeclassBroadcast : Titles.sWorkspaceNetClassBroadcast);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
    }

    @Override
    protected void addFragments() {
        Bundle bundle = new Bundle();
        bundle.putString(ClassRoomContants.FROM_WHERE_MODEL, getIntent().getExtras().getString(ClassRoomContants.FROM_WHERE_MODEL));
        addFragment("", RemoteDirectorNewFragment.class, bundle);
    }

    @Override
    protected void addFilterFragments() {
        ArrayList<FilterItem> items;
        items = new ArrayList<>();
        items.add(new FilterItem(getString(R.string.exam_grade), "classLevelId", URLConfig.ALL_CLASS_LEVEL_BY_SCHOOL_ID, FilterItem.OBJECT, new Choice.BaseChoiceParser()));//ALL_CLASS_LEVEL ClassLevelJsonParser()
        items.add(new FilterItem(getString(R.string.exam_subject), "subjectId", URLConfig.ALL_SUBJECTS_LIST,FilterItem.OBJECT, new Choice.BaseChoiceParser() ));//ALL_SUBJECTS_LIST  FilterItem.ARRAY
        addFilterFragment(0, ClassFilterFragment.newInstance(items, UserInfoKeeper.obtainUserInfo().getUuid()));
    }
}
