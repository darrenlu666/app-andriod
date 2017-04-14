package com.codyy.erpsportal.schooltv.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.groups.controllers.activities.SimpleRecyclerActivity;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerDelegate;
import com.codyy.erpsportal.schooltv.controllers.viewholders.SchoolProgramViewHolder;
import com.codyy.erpsportal.schooltv.models.SchoolProgram;
import com.codyy.erpsportal.schooltv.models.SchoolProgramParse;
import com.codyy.erpsportal.schooltv.models.WeekDay;
import com.codyy.erpsportal.schooltv.utils.DatePickTheme;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;
import cn.aigestudio.datepicker.bizs.themes.DPTManager;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

/**
 * 校园电视台-节目单
 * Created by poe on 17-3-14.
 */
public class SchoolTvProgramListActivity extends SimpleRecyclerActivity<SchoolProgram> {
    private static final String TAG = "SchoolTvProgramListActivity";
    /** has picture layout **/
    private static final int TYPE_VIEW_HOLDER_WITH_PICTURE= 0x001;
    /** item without picture **/
    private static final int TYPE_VIEW_HOLDER_WITHOUT_PICTURE= 0x002;
    private static final String EXTRA_SCHOOL_ID = "school.id";

    @Bind(R.id.tab_layout)TabLayout mTabLayout;
    private List<WeekDay> mWeekDayList = new ArrayList<>();
    private String mLiveDate ;//默认今天
    private String mSchoolId;

    @Override
    public int obtainLayoutId() {
        //return a customized layout .
        return R.layout.activity_school_tv_program_list;
    }

    @Override
    public void preInitArguments() {
        mSchoolId = getIntent().getStringExtra(EXTRA_SCHOOL_ID);
        mLiveDate = DateUtil.getNow(DateUtil.YEAR_MONTH_DAY);
    }

    TabLayout.OnTabSelectedListener mTabSelectListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int index = mTabLayout.getSelectedTabPosition();
            Cog.i(TAG,"onTabSelected : "+index);
            String date = mWeekDayList.get(index).getWeekDate();
            mLiveDate = date;
            refresh();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    @Override
    public void init() {
        super.init();
        setTitle(Titles.sWorkspaceTvProgramProgramList);
        setEmptyText(R.string.tv_no_data_for_now,R.color.remote_text_nor);
        mTabLayout.setTabTextColors(R.color.grey_444,R.color.main_color);
        mTabLayout.setSelectedTabIndicatorHeight(0);
        //不需要分页显示，一次加载全部数据
        setPageListEnable(false);
        //set filter
        setFilterListener(new IFilterListener() {
            @Override
            public void onFilterClick(MenuItem item) {
                pickerDate();
            }

            @Override
            public void onPreFilterCreate(Menu menu) {

            }
        });
        //init date table .
        refreshTabLayout(mLiveDate);
    }

    //更新头部的时间表
    private void refreshTabLayout(String date) {
        Cog.i(TAG,"update date : "+date);
        mWeekDayList = DateUtil.getCurrentWeek(date,DateUtil.YEAR_MONTH_DAY);
        mTabLayout.removeOnTabSelectedListener(mTabSelectListener);
        mTabLayout.removeAllTabs();
        for(WeekDay wd : mWeekDayList){
            Cog.i(TAG,wd.getWeekDate()+" : "+wd.getShortWeekDate());
            View customView = LayoutInflater.from(this).inflate(R.layout.tab_item_two_line_text,null);
            ((TextView)customView.findViewById(R.id.tab_item_title)).setText(wd.getWeekDay());
            ((TextView)customView.findViewById(R.id.tab_item_content)).setText(wd.getShortWeekDate());
            mTabLayout.addTab(mTabLayout.newTab().setText(wd.getWeekDay()).setCustomView(customView));
        }
        mTabLayout.addOnTabSelectedListener(mTabSelectListener);
        //count today index .
        for(WeekDay wd :mWeekDayList){
            if(wd.getWeekDate().equals(date)){
                int index = mWeekDayList.indexOf(wd);
                Cog.i(TAG,"jump to index : "+ index);
                mTabLayout.getTabAt(index).select();
                if(index == 0){
                    //index 0 will be not invoked by mTabSelectListener . so you need request new data .
                    mLiveDate = date;
                    refresh();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_daily_note, menu);
        return true;
    }

    @Override
    public SimpleRecyclerDelegate<SchoolProgram> getSimpleRecyclerDelegate() {
        return new SimpleRecyclerDelegate<SchoolProgram>() {
            @Override
            public String obtainAPI() {
                return URLConfig.GET_SCHOOL_TV_PROGRAM_LIST;
            }

            @Override
            public HashMap<String, String> getParams(boolean isRefreshing) {
                HashMap<String,String> param = new HashMap<>();
                if(null != mUserInfo) {
                    param.put("uuid",mUserInfo.getUuid());
                }
                if(null != mSchoolId) param.put("schoolId",mSchoolId);
                if(null != mLiveDate) param.put("liveDate",mLiveDate);
                return param;
            }

            @Override
            public void parseData(JSONObject response,boolean isRefreshing) {
                SchoolProgramParse parse = new Gson().fromJson(response.toString(),SchoolProgramParse.class);
                if(null != parse ){
                    if(null != parse.getList() && parse.getList().size() >0 ){
                        for(SchoolProgram sp : parse.getList()){
                            if("TRANS_SUCCESS".equals(sp.getTransFlag())){
                                sp.setBaseViewHoldType(TYPE_VIEW_HOLDER_WITH_PICTURE);
                            }else{
                                sp.setBaseViewHoldType(TYPE_VIEW_HOLDER_WITHOUT_PICTURE);
                            }
                            mDataList.add(sp);
                        }
                    }
                }
            }

            @Override
            public BaseRecyclerViewHolder<SchoolProgram> getViewHolder(ViewGroup parent,int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType){
                    case TYPE_VIEW_HOLDER_WITH_PICTURE:
                        viewHolder= new SchoolProgramViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_school_tv_program_picture));
                        break;
                    case TYPE_VIEW_HOLDER_WITHOUT_PICTURE:
                        viewHolder= new SchoolProgramViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_school_tv_program));
                        break;
                }
                return viewHolder;
            }

            @Override
            public void OnItemClicked(View v, int position, SchoolProgram data) {
                SchoolProgramDetail.start(SchoolTvProgramListActivity.this,mUserInfo,data.getTvProgramDetailId(),mSchoolId);
            }

            @Override
            public int getTotal() {
                return mDataList==null?0:mDataList.size();
            }
        };
    }

    private AlertDialog mDialog;
    private DatePicker mDatePicker;
    private void pickerDate() {
        if(null == mDialog){
            mDialog  = new AlertDialog.Builder(this).create();
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();
            if(null == mDatePicker){
                DPTManager.getInstance().initCalendar(new DatePickTheme());
                mDatePicker = new DatePicker(this);
                mDatePicker.setDate(DateTime.now().getYear(), DateTime.now().getMonthOfYear());
                mDatePicker.setMode(DPMode.SINGLE);
                mDatePicker.setTodayDisplay(true);
                mDatePicker.setHolidayDisplay(false);
                mDatePicker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
                    @Override
                    public void onDatePicked(String date) {
                        mDialog.dismiss();
                        try{
                            Date newDate = DateUtil.stringToDate(date,DateUtil.YEAR_MONTH_DAY);
                            String dd = DateUtil.dateToString(newDate,DateUtil.YEAR_MONTH_DAY);
                            refreshTabLayout(dd);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (mDialog.getWindow() != null) {
                mDialog.getWindow().setContentView(mDatePicker, params);
                mDialog.getWindow().setGravity(Gravity.CENTER);
            }
        }else{
            mDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null !=mDialog){
            mDialog = null;
        }
        if(null != mDatePicker) {
            mDatePicker.destroyDrawingCache();
            mDatePicker = null;
        }
    }

    public static void start(Activity act , UserInfo userInfo,String schoolId){
        Intent intent = new Intent(act,SchoolTvProgramListActivity.class);
        intent.putExtra(Constants.USER_INFO,userInfo);
        intent.putExtra(EXTRA_SCHOOL_ID,schoolId);
        act.startActivity(intent);
        UIUtils.addEnterAnim(act);
    }
}
