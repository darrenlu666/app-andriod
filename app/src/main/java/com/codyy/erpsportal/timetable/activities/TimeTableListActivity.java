package com.codyy.erpsportal.timetable.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.commons.controllers.activities.FilterBaseActivity;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.timetable.fragments.TimetableListFragment;

import org.json.JSONObject;

public class TimeTableListActivity extends FilterBaseActivity {
    private TimetableListFragment mTimetableListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mTimetableListFragment = TimetableListFragment.newInstance();
        setFragment(mTimetableListFragment);
    }

    private void init() {
        setTitle(Titles.sWorkspaceSpeclassSchedule);
    }

    @Override
    public void onFilter(String areaID, String schoolID, boolean isDirect) {
        mTimetableListFragment.onFilter(areaID, schoolID, isDirect);
    }

    @Override
    protected void onGetResult(JSONObject object, int msg) {

    }

    @Override
    protected void onError(VolleyError error, int msg) {

    }

    public static void start(Context context) {
        Intent intent = new Intent(context, TimeTableListActivity.class);
        context.startActivity(intent);
    }
}

