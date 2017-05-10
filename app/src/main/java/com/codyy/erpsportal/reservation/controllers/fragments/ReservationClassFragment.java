package com.codyy.erpsportal.reservation.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.BaseRefreshFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.reservation.controllers.adapter.ReservationClassAdapter;
import com.codyy.erpsportal.reservation.models.entities.ReservationClassItem;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kmdai on 16-6-13.
 */
public class ReservationClassFragment extends BaseRefreshFragment<ReservationClassItem> {
    private UserInfo mUserInfo;
    private int mStart;
    private int mOnePage;
    private int mEnd;
    private String mAreaId;
    private String mSchoolId;
    private boolean mIsDirect;

    public static ReservationClassFragment newInstance() {

        Bundle args = new Bundle();

        ReservationClassFragment fragment = new ReservationClassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setURL(URLConfig.GET_RESERVATION_CLASS);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mAreaId=mUserInfo.getBaseAreaId();
        mSchoolId=mUserInfo.getSchoolId();
        mStart = 0;
        mOnePage = 9;
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
    public RefreshBaseAdapter<ReservationClassItem> getAdapter(List<ReservationClassItem> data) {
        return new ReservationClassAdapter(getContext(), data);
    }

    @Override
    protected void onRequestError(Throwable error, int msg) {
        super.onRequestError(error, msg);
    }

    @Override
    protected boolean hasData() {
        if (mDatas.size() >= mEnd) {
            return true;
        }
        return super.hasData();
    }

    @NonNull
    @Override
    public Map<String, String> getParam(int state) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        if (state == STATE_ON_UP_REFRESH) {
            mStart = mDatas.size();
            mEnd = mStart + mOnePage;
        } else {
            mStart = 0;
            mEnd = mStart + mOnePage;
        }
        if (mAreaId != null) {
            data.put("baseAreaId", mAreaId);
        }
        if (mSchoolId != null) {
            data.put("schoolId", mSchoolId);
        }
        if (mIsDirect && mSchoolId == null) {
            data.put("isDirectSchoolAreaId", mAreaId);
        }
        data.put("start", String.valueOf(mStart));
        data.put("end", String.valueOf(mEnd));
        return data;
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
    public List<ReservationClassItem> getDataOnJSON(JSONObject object) {
        if (object != null && "success".equals(object.optString("result"))) {
            JSONArray array = object.optJSONArray("list");
            ArrayList<ReservationClassItem> arrayList = new ArrayList<>(array.length());
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                ReservationClassItem item = gson.fromJson(array.optJSONObject(i).toString(), ReservationClassItem.class);
                item.setmHolderType(ReservationClassItem.TYPE_CONTENT);
                arrayList.add(item);
            }
            return arrayList;
        }
        return new ArrayList<>();
    }

}
