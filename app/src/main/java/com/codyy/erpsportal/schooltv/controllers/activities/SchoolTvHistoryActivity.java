package com.codyy.erpsportal.schooltv.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.groups.controllers.activities.SimpleRecyclerActivity;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerDelegate;
import com.codyy.erpsportal.schooltv.models.SchoolVideo;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 校园电视台-往期视频
 * Created by poe on 17-3-14.
 */

public class SchoolTvHistoryActivity extends SimpleRecyclerActivity<SchoolVideo> {
    private static final String TAG = "SchoolTvHistoryActivity";
    @Override
    public void preInitArguments() {

    }

    @Override
    public void init() {
        super.init();
        setTitle(Titles.sWorkspaceTvProgramReplay);

    }

    @Override
    public SimpleRecyclerDelegate<SchoolVideo> getSimpleRecyclerDelegate() {
        return new SimpleRecyclerDelegate<SchoolVideo>() {
            @Override
            public String obtainAPI() {
                return URLConfig.GET_SCHOOL_TV_HISTORY_LIST;
            }

            @Override
            public HashMap<String, String> getParams() {

                return null;
            }

            @Override
            public void parseData(JSONObject response) {

            }

            @Override
            public BaseRecyclerViewHolder<SchoolVideo> getViewHolder(ViewGroup parent) {
                return null;
            }

            @Override
            public void OnItemClicked(View v, int position, SchoolVideo data) {

            }

            @Override
            public int getTotal() {
                return 0;
            }
        };
    }

    public static void start(Activity act , UserInfo userInfo){
        Intent intent = new Intent(act,SchoolTvHistoryActivity.class);
        intent.putExtra(Constants.USER_INFO,userInfo);
        act.startActivity(intent);
        UIUtils.addEnterAnim(act);
    }
}
