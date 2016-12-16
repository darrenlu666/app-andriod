package com.codyy.erpsportal.county.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.codyy.erpsportal.county.controllers.models.entities.CountyListItem;
import com.codyy.erpsportal.county.controllers.models.entities.CountyListItemDetail;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.BaseRefreshFragment;
import com.codyy.erpsportal.county.controllers.adapters.CountyListAdapter;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.RefreshEntity;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kmdai on 16-6-6.
 */
public class ContyListFragment extends BaseRefreshFragment<RefreshEntity> {
    /**
     * 计划开课
     */
    public final static int TYPE_PLAN = 0x001;
    /**
     * 自主开课
     */
    public final static int TYPE_LIBERTY = 0x002;
    public final static String EXTRA_TYPE = "conty_type";
    private UserInfo mUserInfo;
    private int mType;
    private int mStart;
    private final int mCount = 9;
    private int mEnd;
    private int mDataListSize;
    private String mAreaId;
    private String mSchoolId;
    private boolean mIsDirect;

    public static ContyListFragment newInstance() {

        Bundle args = new Bundle();

        ContyListFragment fragment = new ContyListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mAreaId = mUserInfo.getBaseAreaId();
        mSchoolId = mUserInfo.getSchoolId();
        mType = getArguments().getInt(EXTRA_TYPE, TYPE_LIBERTY);
        if (mType == TYPE_PLAN) {
            setURL(URLConfig.CONTY_GET_PLANDATA);
        } else {
            setURL(URLConfig.CONTY_GET_LIBERTYDATA);
        }
        mStart = mDataListSize = 0;
        mEnd = mStart + mCount;
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
    public RefreshBaseAdapter<RefreshEntity> getAdapter(List<RefreshEntity> data) {
        return new CountyListAdapter(getContext(), mDatas);
    }

    @NonNull
    @Override
    public Map<String, String> getParam(int state) {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());

        if (state == STATE_ON_DOWN_REFRESH) {
            mStart = mDataListSize = 0;
            mEnd = mCount + mStart;
        } else {
            mStart = mDataListSize + 1;
            mEnd = mStart + mCount;
        }
        if (mAreaId != null) {
            data.put("areaId", mAreaId);
        } else {
            data.put("areaId", mUserInfo.getBaseAreaId());
        }
        if (mSchoolId != null) {
            data.put("schoolId", mSchoolId);
        }
        if (mIsDirect) {
            data.put("directSchoolFlag", "Y");
        } else {
            data.put("directSchoolFlag", "N");
        }
        data.put("start", String.valueOf(mStart));
        data.put("end", String.valueOf(mEnd));
        return data;
    }

    @Override
    protected boolean hasData() {
        if (mDataListSize >= mEnd) {
            return true;
        }
        return super.hasData();
    }

    @NonNull
    @Override
    public List<RefreshEntity> getDataOnJSON(JSONObject object) {
        if ("success".equals(object.optString("result"))) {
            ArrayList<RefreshEntity> items = new ArrayList<>();
            JSONArray array = object.optJSONArray("dataList");
            if (array != null) {
                mDataListSize += array.length();
                Gson gson = new Gson();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.optJSONObject(i);
                    CountyListItem item = gson.fromJson(array.optString(i), CountyListItem.class);
                    item.setmHolderType(CountyListItem.ITEM_TYPE_TITLE);
                    items.add(item);
                    JSONArray array1;
                    if (mType == TYPE_PLAN) {
                        array1 = object1.optJSONArray("mainScheduleList");
                    } else {
                        array1 = object1.optJSONArray("mainSelfScheduleList");
                    }
                    if (array1 != null) {
                        for (int j = 0; j < array1.length(); j++) {
                            CountyListItem.ScheduleItem scheduleItem = gson.fromJson(array1.optString(j), CountyListItem.ScheduleItem.class);
                            scheduleItem.setDayStr(CountyListItemDetail.getDayStr(scheduleItem.getDaySeq()));
                            scheduleItem.setClassStr(CountyListItemDetail.getNumberStr(scheduleItem.getClassSeq()));
                            scheduleItem.setClassroomId(item.getClsClassroomId());
                            scheduleItem.setmHolderType(CountyListItem.ITEM_TYPE_COUNT);
                            scheduleItem.setContyType(mType);
                            items.add(scheduleItem);
                        }
                    }
                }
            }
            return items;
        }
        return new ArrayList<>();
    }

    public void onFilter(String areaid, String schoolId, boolean isDirect) {
        mAreaId = areaid;
        mSchoolId = schoolId;
        mIsDirect = isDirect;
        loadData();
    }
}
