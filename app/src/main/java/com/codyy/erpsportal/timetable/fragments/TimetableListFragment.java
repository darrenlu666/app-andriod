package com.codyy.erpsportal.timetable.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.BaseRefreshFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.TimeTableListContent;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.timetable.adapter.TimeTableListAdapter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kmdai on 16-6-14.
 */
public class TimetableListFragment extends BaseRefreshFragment<TimeTableListContent> {
    private UserInfo mUserInfo;
    private int mStart;
    private final static int mOnePage = 9;
    private int mEnd;
    private String mAreaId;
    private String mSchoolId;
    private boolean mIsDirect;

    public static TimetableListFragment newInstance() {

        Bundle args = new Bundle();

        TimetableListFragment fragment = new TimetableListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mAreaId=mUserInfo.getBaseAreaId();
        mSchoolId=mUserInfo.getSchoolId();
        setURL(URLConfig.TIMETABLE_URL);
        mStart = 0;
        mEnd = mStart + mOnePage;

    }

    @Override
    public void loadData() {
        mEmptyView.setVisibility(View.GONE);
        mRefreshRecycleView.post(new Runnable() {
            @Override
            public void run() {
                mRefreshRecycleView.setRefreshing(true);
            }
        });
        if (getView() != null) {
            getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    httpConnect(getURL(), getParam(STATE_ON_DOWN_REFRESH), STATE_ON_DOWN_REFRESH);
                }
            }, 500);
        }
    }

    @NonNull
    @Override
    public RefreshBaseAdapter<TimeTableListContent> getAdapter(List<TimeTableListContent> data) {
        return new TimeTableListAdapter(getContext(), mDatas);
    }

    @Override
    protected boolean hasData() {
        if (mDatas.size() >= mEnd) {
            return true;
        }
        return super.hasData();
    }

    /**
     * 筛选
     *
     * @param areaid
     * @param schoolId
     * @param isDirect
     */
    public void onFilter(String areaid, String schoolId, boolean isDirect) {
        mAreaId = areaid;
        mSchoolId = schoolId;
        mIsDirect = isDirect;
        loadData();
    }

    @NonNull
    @Override
    public Map<String, String> getParam(int state) {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        if (state == STATE_ON_DOWN_REFRESH) {
            mStart = 0;
            mEnd = mStart + mOnePage;
        } else {
            mStart = mDatas.size();
            mEnd = mStart + mOnePage;
        }
        data.put("start", String.valueOf(mStart));
        data.put("end", String.valueOf(mEnd));
        if (mAreaId != null) {
            data.put("baseAreaId", mAreaId);
        }
        if (mSchoolId != null) {
            data.put("schoolId", mSchoolId);
        }
        if (mIsDirect) {
            data.put("isDirect", "Y");
        } else {
            data.put("isDirect", "N");
        }
        return data;
    }

    @NonNull
    @Override
    public List<TimeTableListContent> getDataOnJSON(JSONObject object) {
        return TimeTableListContent.getTimeTableList(object, null);
    }
}
