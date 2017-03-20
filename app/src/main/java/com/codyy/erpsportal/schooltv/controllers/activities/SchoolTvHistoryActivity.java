package com.codyy.erpsportal.schooltv.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.UpOrDownButton;
import com.codyy.erpsportal.groups.controllers.activities.SimpleRecyclerActivity;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerDelegate;
import com.codyy.erpsportal.schooltv.controllers.viewholders.SchoolVideoViewHolder;
import com.codyy.erpsportal.schooltv.models.SchoolVideo;
import com.codyy.erpsportal.schooltv.models.SchoolVideoParse;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;

/**
 * 校园电视台-往期视频
 * Created by poe on 17-3-14.
 */

public class SchoolTvHistoryActivity extends SimpleRecyclerActivity<SchoolVideo> implements View.OnClickListener{
    private static final String TAG = "SchoolTvHistoryActivity";
    private static final String TYPE_ORDER_TIME="time";
    private static final String TYPE_ORDER_NAME="programName";
    private static final String TYPE_ORDER_CLICK_COUNT="viewCnt";
    private static final String ORDER_ASC = "asc";
    private static final String ORDER_DESC = "desc";

    @Bind(R.id.time_order_udb)UpOrDownButton mTimeUdp;
    @Bind(R.id.name_order_udb)UpOrDownButton mNameUdp;
    @Bind(R.id.count_order_udb)UpOrDownButton mCountUdp;

    private int mTotal = 0 ;//总数
    private String mFilterType = TYPE_ORDER_TIME;//按什么排序	string	programName 节目名称；viewCnt 点击量；time 按时间
    private String mOrderType = ORDER_DESC;//排序规则	string	asc 正序； desc 倒序

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_school_tv_program_history_list;
    }

    @Override
    public void preInitArguments() {

    }

    @Override
    public void init() {
        super.init();
        setTitle(Titles.sWorkspaceTvProgramReplay);

        mTimeUdp.setOnClickListener(this);
        mNameUdp.setOnClickListener(this);
        mCountUdp.setOnClickListener(this);
        //按照时间倒序排列
        mTimeUdp.setChecked();
        mNameUdp.setInitView();
        mCountUdp.setInitView();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        initData();
    }

    @Override
    public SimpleRecyclerDelegate<SchoolVideo> getSimpleRecyclerDelegate() {
        return new SimpleRecyclerDelegate<SchoolVideo>() {
            @Override
            public String obtainAPI() {
                return URLConfig.GET_SCHOOL_TV_HISTORY_LIST;
            }

            @Override
            public HashMap<String, String> getParams(boolean isRefreshing) {
                HashMap<String,String> param = new HashMap<>();
                if(null != mUserInfo) {
                    param.put("uuid",mUserInfo.getUuid());
                    if(UserInfo.USER_TYPE_PARENT.equals(mUserInfo.getUserType())){
                        param.put("schoolId",mUserInfo.getSelectedChild().getSchoolId());
                    }else{
                        param.put("schoolId",mUserInfo.getSchoolId());
                    }
                }
                if(null != mFilterType) param.put("orderBy",mFilterType);
                if(null != mOrderType) param.put("orderType",mOrderType);
                param.put("start",mDataList.size()+"");
                param.put("end",mDataList.size()+sPageCount-1+"");
                return param;
            }

            @Override
            public void parseData(JSONObject response,boolean isRefreshing) {
                SchoolVideoParse sp = new Gson().fromJson(response.toString(),SchoolVideoParse.class);
                if(null != sp){
                    mTotal = sp.getTotal();
                    if(null != sp.getPastProgramList() && sp.getPastProgramList().size()>0){
                        for(SchoolVideo sv: sp.getPastProgramList()){
                            sv.setBaseViewHoldType(0);
                            mDataList.add(sv);
                        }
                    }
                }
            }

            @Override
            public BaseRecyclerViewHolder<SchoolVideo> getViewHolder(ViewGroup parent,int viewType) {
                return new SchoolVideoViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_school_tv_history));
            }

            @Override
            public void OnItemClicked(View v, int position, SchoolVideo data) {
                SchoolProgramDetail.start(SchoolTvHistoryActivity.this,mUserInfo,data.getTvProgramDetailId());
            }

            @Override
            public int getTotal() {
                return mTotal;
            }
        };
    }

    public static void start(Activity act , UserInfo userInfo){
        Intent intent = new Intent(act,SchoolTvHistoryActivity.class);
        intent.putExtra(Constants.USER_INFO,userInfo);
        act.startActivity(intent);
        UIUtils.addEnterAnim(act);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.time_order_udb:
                mTimeUdp.setChecked();
                mNameUdp.setInitView();
                mCountUdp.setInitView();

                mFilterType = TYPE_ORDER_TIME;
                mOrderType = mTimeUdp.isUp()?ORDER_DESC:ORDER_ASC;
                refresh();
                break;
            case R.id.name_order_udb:
                mTimeUdp.setInitView();
                mNameUdp.setChecked();
                mCountUdp.setInitView();

                mFilterType = TYPE_ORDER_NAME;
                mOrderType = mNameUdp.isUp()?ORDER_DESC:ORDER_ASC;
                refresh();
                break;
            case R.id.count_order_udb:
                mTimeUdp.setInitView();
                mNameUdp.setInitView();
                mCountUdp.setChecked();

                mFilterType = TYPE_ORDER_CLICK_COUNT;
                mOrderType = mCountUdp.isUp()?ORDER_DESC:ORDER_ASC;
                refresh();
                break;
        }
    }
}
