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
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
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

    @Bind(R.id.tab_layout)TabLayout mTabLayout;
    private List<WeekDay> mWeekDayList = new ArrayList<>();
    private String mLiveDate ;//默认今天

    @Override
    public int obtainLayoutId() {
        //return a customized layout .
        return R.layout.activity_school_tv_program_list;
    }

    @Override
    public void preInitArguments() {
        mLiveDate = DateUtil.getNow(DateUtil.YEAR_MONTH_DAY);
//        DateUtil.getWeek()
        mWeekDayList = DateUtil.getCurrentWeek(DateUtil.YEAR_MONTH_DAY);
        for(int i=0;i<mWeekDayList.size();i++){
            System.out.print(" day: "+mWeekDayList.get(i).getWeekDate());
        }
    }

    @Override
    public void init() {
        super.init();
        setTitle(Titles.sWorkspaceTvProgramProgramList);

        for(WeekDay wd : mWeekDayList){
            Cog.i(TAG,wd.getWeekDate()+" : "+wd.getShortWeekDate());
            View customView = LayoutInflater.from(this).inflate(R.layout.tab_item_two_line_text,null);
            ((TextView)customView.findViewById(R.id.tab_item_title)).setText(wd.getWeekDay());
            ((TextView)customView.findViewById(R.id.tab_item_content)).setText(wd.getShortWeekDate());
            mTabLayout.addTab(mTabLayout.newTab().setText(wd.getWeekDay()).setCustomView(customView));
        }

        mTabLayout.setTabTextColors(R.color.grey_444,R.color.main_color);
        mTabLayout.setSelectedTabIndicatorHeight(0);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int index = mTabLayout.getSelectedTabPosition();
                mLiveDate = mWeekDayList.get(index).getWeekDate();
                refresh();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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

        //count today index .
        for(WeekDay wd :mWeekDayList){
            if(wd.getWeekDate().equals(mLiveDate)){
                mTabLayout.getTabAt(mWeekDayList.indexOf(wd)).select();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        initData(); //use getTabAt(i).select() .
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
                    if(UserInfo.USER_TYPE_PARENT.equals(mUserInfo.getUserType())){
                        param.put("schoolId",mUserInfo.getSelectedChild().getSchoolId());
                    }else{
                        param.put("schoolId",mUserInfo.getSchoolId());
                    }
                }
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

            }

            @Override
            public int getTotal() {
                return mDataList==null?0:mDataList.size();
            }
        };
    }

    private void pickerDate() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        DatePicker picker = new DatePicker(this);
        picker.setDate(DateTime.now().getYear(), DateTime.now().getMonthOfYear());
        picker.setMode(DPMode.SINGLE);
        picker.setTodayDisplay(true);
        picker.setHolidayDisplay(false);
        picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                dialog.dismiss();
                try{
                    Date newDate = DateUtil.stringToDate(date,DateUtil.YEAR_MONTH_DAY);
                    String dd = DateUtil.dateToString(newDate,DateUtil.YEAR_MONTH_DAY);
                    if(null != mLiveDate && !mLiveDate.equals(dd)){
                        mLiveDate = dd;
                        refresh();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setContentView(picker, params);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }

    public static void start(Activity act , UserInfo userInfo){
        Intent intent = new Intent(act,SchoolTvProgramListActivity.class);
        intent.putExtra(Constants.USER_INFO,userInfo);
        act.startActivity(intent);
        UIUtils.addEnterAnim(act);
    }
}
