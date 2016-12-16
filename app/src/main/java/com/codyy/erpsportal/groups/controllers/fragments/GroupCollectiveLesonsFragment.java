package com.codyy.erpsportal.groups.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.GroupCollectiveAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.BaseRefreshFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.GroupCollective;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kmdai on 16-3-9.
 */
public class GroupCollectiveLesonsFragment extends BaseRefreshFragment<GroupCollective> {
    public final static String ARG_GROUP_ID = "group_id";
    private String mGroupId;
    private UserInfo mUserInfo;
    private int mStart;
    private int mEnd;
    private final int CONT = 9;

    public static GroupCollectiveLesonsFragment newInstance(String groupId) {
        Bundle args = new Bundle();
        GroupCollectiveLesonsFragment fragment = new GroupCollectiveLesonsFragment();
        args.putString(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mGroupId = getArguments().getString(ARG_GROUP_ID);
        mStart = 0;
        mEnd = mStart + CONT;
        setURL(URLConfig.GROUP_MEET_LIST);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadData() {
        Map<String, String> parm = new HashMap<>();
        //uuid=MOBILE:383a2fef07a44eee8164e454c73b37de&groupId=2602dc7a42b2414694f9fb868a19fd8d&start=0&end=9
        parm.put("uuid", mUserInfo.getUuid());
        parm.put("groupId", mGroupId);
        parm.put("start", String.valueOf(mStart));
        parm.put("end", String.valueOf(mEnd));
        httpConnect(getURL(), parm, STATE_ON_DOWN_REFRESH);
    }

    @NonNull
    @Override
    public RefreshBaseAdapter<GroupCollective> getAdapter(List<GroupCollective> data) {
        return new GroupCollectiveAdapter(getContext(), data);
    }

    @NonNull
    @Override
    public Map<String, String> getParam(int state) {
        Map<String, String> parm = new HashMap<>();
        parm.put("uuid", mUserInfo.getUuid());
        parm.put("groupId", mGroupId);
        switch (state) {
            case STATE_ON_DOWN_REFRESH:
                mStart = 0;
                mEnd = mStart + CONT;
                break;
            case STATE_ON_UP_REFRESH:
                mStart = mDatas.size();
                mEnd = mStart + CONT;
                break;
        }
        parm.put("start", String.valueOf(mStart));
        parm.put("end", String.valueOf(mEnd));
        return parm;
    }

    @Override
    protected boolean hasData() {
        return super.hasData();
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
    }

    @Override
    public boolean onBottom() {
        return super.onBottom();
    }

    @NonNull
    @Override
    public List<GroupCollective> getDataOnJSON(JSONObject object) {
        return GroupCollective.getGroupCollective(object);
    }
}
