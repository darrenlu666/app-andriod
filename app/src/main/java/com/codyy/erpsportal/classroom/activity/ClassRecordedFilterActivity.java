package com.codyy.erpsportal.classroom.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.classroom.fragment.AreaRecordListFragment;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.FilterBaseActivity;
import com.codyy.erpsportal.commons.models.Titles;

import org.json.JSONObject;

/**
 * 往期录播 学校列表activity 附筛选功能
 * Created by ldh on 2016/8/20.
 */
public class ClassRecordedFilterActivity extends FilterBaseActivity {
    private String mFrom;//来自哪个模块（专递课堂/直录播课堂）
    private AreaRecordListFragment mAreaRecordListFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setTitle(ClassRoomContants.FROM_WHERE_MODEL.equals(ClassRoomContants.TYPE_CUSTOM_RECORD) ? Titles.sWorkspaceSpeclassLive : Titles.sWorkspaceNetClassReplay);
        mFrom = getIntent().getExtras().getString(ClassRoomContants.FROM_WHERE_MODEL);
        mAreaRecordListFragment = AreaRecordListFragment.newInstance(mFrom);
        setFragment(mAreaRecordListFragment);
    }

    @Override
    public void onFilter(String areaID, String schoolID, boolean isDirect) {
        mAreaRecordListFragment.onFilter(areaID, schoolID, isDirect);
    }

    @Override
    protected void onGetResult(JSONObject object, int msg) {

    }

    @Override
    protected void onError(VolleyError error, int msg) {

    }
}
