package com.codyy.erpsportal.classroom.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.classroom.models.Watcher;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerDelegate;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerFragment;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by poe on 17-3-13.
 */

public class PeopleTreeFragment extends SimpleRecyclerFragment<Watcher> {
    private final static String TAG = "PeopleTreeFragment";
    private static final String ARG_CLASS_ID = "class.id";

    private String mClassId;//class id .

    public static PeopleTreeFragment newInstance(UserInfo userInfo, String scheduleDetailId) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.USER_INFO, userInfo);
        args.putString(ARG_CLASS_ID, scheduleDetailId);
        PeopleTreeFragment fragment = new PeopleTreeFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != getArguments()){
            mUserInfo = getArguments().getParcelable(Constants.USER_INFO);
            mClassId  = getArguments().getString(ARG_CLASS_ID);
            if(null == mUserInfo) mUserInfo = UserInfoKeeper.obtainUserInfo();
        }
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        //to get the people datasoiurce .
        initData();
    }

    @Override
    public SimpleRecyclerDelegate<Watcher> getSimpleRecyclerDelegate() {
        return new SimpleRecyclerDelegate<Watcher>() {
            @Override
            public String obtainAPI() {
                return null;
            }

            @Override
            public HashMap<String, String> getParams() {
                return new HashMap<String, String>();
            }

            @Override
            public void parseData(JSONObject response) {

            }

            @Override
            public BaseRecyclerViewHolder<Watcher> getViewHolder(ViewGroup parent) {
                return null;
            }

            @Override
            public void OnItemClicked(View v, int position, Watcher data) {

            }

            @Override
            public int getTotal() {
                return 0;
            }
        };
    }

}
