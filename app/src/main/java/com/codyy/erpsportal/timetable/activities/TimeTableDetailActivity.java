package com.codyy.erpsportal.timetable.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.CalendarScrollView;
import com.codyy.erpsportal.commons.widgets.CalendarViewPager;
import com.codyy.erpsportal.commons.widgets.RecycleViewPopuWindow;
import com.codyy.erpsportal.commons.widgets.TimeTable.SuperTimeTableLayout;
import com.codyy.erpsportal.commons.widgets.TimeTable.TimeTableView2;
import com.codyy.erpsportal.commons.widgets.components.FilterButton;
import com.codyy.erpsportal.county.widgets.ClassDetailDialog;
import com.codyy.erpsportal.timetable.fragments.ListDialog;
import com.codyy.erpsportal.timetable.models.entities.TimetableDetail;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 区县总表-课表详情
 *
 * @author kmdai
 */
public class TimeTableDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "TimeTableDetailActivity------:";
    /**
     * school id
     */
    public final static String SCHOOL_ID = "schoolId";
    /**
     * 学校名字
     */
    public final static String SCHOOL_NAME = "schoolName";
    /**
     * 主讲教室
     */
    private final static String MASTER_CLASS = "MASTER";
    /**
     * 接受教室
     */
    private final static String RECEIVE_CLASS = "RECEIVE";
    /**
     * 获取基本信息
     */
    private final static int GET_BASE_INFO = 0x001;
    /**
     * 获取课表信息
     */
    private final static int GET_TABLE_INFO = GET_BASE_INFO + 1;

    private final static String DIALOG_TAG = "detailDialog---";
    private CalendarScrollView mCalendarScrollView;
    private DrawerLayout mDrawerLayout;
    //    private TextView mFilterTV;
//    private ImageView mFilterIV;
    private FilterButton mFilterBtn;
    private TextView mDateTV;
    private TextView mWeekTV;
    private RequestSender mRequestSender;
    private String mSchoolID;
    private UserInfo mUserInfo;
    private List<ClassRoom> mClassRooms;
    private ListView mClassRoomLV;
    private TextView mClassRoomTV;
    private TextView mClassTableTV;
    private TextView mTitleTextTV;

    /**
     * 当前选择的教室
     */
    private ClassRoom mClassRoomSelect;
    private String mClsSchoolTrimesterId;
    private TimetableDetail mTimetableDetail;
    private RecycleViewPopuWindow mRecycleViewPopuWindow;
    //    private TimeTableView2 mTimeTableView2;
    private SuperTimeTableLayout mSuperTimeTableLayout;
    private RadioGroup mRadioGroup;
    private ClassAdapter mClassAdapter;
    private String mCurrentDate;
    private int mCurrentWeek;
    private WeekAdapter mWeekAdapter;
    private Calendar mModifiedCalender;
    /**
     * 能否切换日期或周次
     */
    private boolean mCanChangeDate;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_detail);
        mSchoolID = getIntent().getStringExtra(SCHOOL_ID);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        init();
        if (UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType())) {
            getTeacherClassDetail();
        } else {
            getBaseInfo();
        }
    }

    /**
     * 获取基础信息
     */
    public void getBaseInfo() {
        if (mSchoolID != null && mUserInfo != null) {
            Map<String, String> param = new HashMap<>();
            param.put("uuid", mUserInfo.getUuid());
            param.put(SCHOOL_ID, mSchoolID);
            if (httpConnect(URLConfig.GET_BASESCHEDULEINFO, param, GET_BASE_INFO)) {
                if (!mLoadingDialog.isShowing()) {
                    mLoadingDialog.show(getSupportFragmentManager(), TAG);
                }
            }
        }
    }

    /**
     * 获取老师课表详情
     */
    public void getTeacherClassDetail() {
        Map<String, String> param = new HashMap<>();
        param.put("uuid", mUserInfo.getUuid());
        param.put("strDate", mCurrentDate);
        param.put("isNewVersion", "1");
        if (httpConnect(URLConfig.GET_TEACHER_SCHEDULE, param, GET_TABLE_INFO)) {
            if (!mLoadingDialog.isShowing()) {
                mLoadingDialog.show(getSupportFragmentManager(), TAG);
            }
        }

    }

    /**
     * 获取课表详情
     *
     * @param classRoom
     */
    private void getTableInfo(ClassRoom classRoom) {
        if (mUserInfo != null) {
            Map<String, String> param = new HashMap<>();
            param.put("uuid", mUserInfo.getUuid());
            param.put("isNewVersion", "1");
            if (MASTER_CLASS.equals(classRoom.getRoomType())) {
                if (mRadioGroup.getCheckedRadioButtonId() == R.id.master_rb) {
                    param.put(SCHOOL_ID, mSchoolID);
                    if (mCurrentWeek > 0) {
                        param.put("weekSeq", String.valueOf(mCurrentWeek));
                    }
                    param.put("clsClassroomId", classRoom.getClsClassroomId());
                    param.put("clsSchoolTrimesterId", mClsSchoolTrimesterId);
                    if (httpConnect(URLConfig.GET_MASTERCLASSROOM_DETAIL, param, GET_TABLE_INFO)) {
                        if (!mLoadingDialog.isShowing()) {
                            mLoadingDialog.show(getSupportFragmentManager(), TAG);
                        }
                    }
                } else {
                    param.put(SCHOOL_ID, mSchoolID);
                    if (mCurrentDate != null) {
                        param.put("startDate", mCurrentDate);
                    }
                    param.put("clsClassroomId", classRoom.getClsClassroomId());
                    if (httpConnect(URLConfig.GET_RECEIVECLASSROOM_DETAIL, param, GET_TABLE_INFO)) {
                        if (!mLoadingDialog.isShowing()) {
                            mLoadingDialog.show(getSupportFragmentManager(), TAG);
                        }
                    }
                }
            } else {
                if (mCurrentDate != null) {
                    param.put("startDate", mCurrentDate);
                }
                param.put(SCHOOL_ID, mSchoolID);
                param.put("clsClassroomId", classRoom.getClsClassroomId());
                if (httpConnect(URLConfig.GET_RECEIVECLASSROOM_DETAIL, param, GET_TABLE_INFO)) {
                    if (!mLoadingDialog.isShowing()) {
                        mLoadingDialog.show(getSupportFragmentManager(), TAG);
                    }
                }
            }

        }
    }

    private void init() {
        mRequestSender = new RequestSender(this);
        mLoadingDialog = LoadingDialog.newInstance();
        mCalendarScrollView = (CalendarScrollView) findViewById(R.id.calendarscrollview);
        mCurrentDate = mCalendarScrollView.getCurrentDate();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//      mFilterTV = (TextView) findViewById(R.id.title_filter_tv);
//      mFilterIV = (ImageView) findViewById(R.id.title_filter);
        mFilterBtn = (FilterButton) findViewById(R.id.btn_filter);
        mFilterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onFilterSelect(v);
            }
        });
        mDateTV = (TextView) findViewById(R.id.date_text);
        mDateTV.setText(mCalendarScrollView.getCurrentDate());
        mWeekTV = (TextView) findViewById(R.id.week_tv);
        mWeekTV.setOnClickListener(this);
        mClassRoomLV = (ListView) findViewById(R.id.listview);
        mClassRoomTV = (TextView) findViewById(R.id.classroom_tv);
        mClassRoomTV.setOnClickListener(this);
        mClassTableTV = (TextView) findViewById(R.id.classtable_tv);
        mClassTableTV.setOnClickListener(this);
//      mTimeTableView2 = (TimeTableView2) findViewById(R.id.timetableview2);
        mSuperTimeTableLayout = (SuperTimeTableLayout) findViewById(R.id.supertimetablelayout);
        mRecycleViewPopuWindow = new RecycleViewPopuWindow(this);
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.master_rb) {
                    mClassTableTV.setText("课表:发起的");
                } else {
                    mClassTableTV.setText("课表:参与的");
                }
            }
        });
        mTitleTextTV = (TextView) findViewById(R.id.title_text);
        String name = getIntent().getStringExtra(SCHOOL_NAME);
        if (name != null) {
            mTitleTextTV.setText(name);
        } else {
            mTitleTextTV.setText("我的课表");
        }
        mRecycleViewPopuWindow.setLayoutManager(new GridLayoutManager(this, 4));
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerOpened(View drawerView) {
                mFilterBtn.setFiltering(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mFilterBtn.setFiltering(false);
                if (mClassRoomSelect != null && MASTER_CLASS.equals(mClassRoomSelect.getRoomType())) {
                    mClassTableTV.setVisibility(View.VISIBLE);
                } else {
                    mClassTableTV.setVisibility(View.GONE);
                }
                if (mClassAdapter != null && mClassRoomSelect != null) {
                    mClassAdapter.mClassRoom = mClassRoomSelect;
                    mClassAdapter.notifyDataSetChanged();
                }
            }
        });
        mCalendarScrollView.setOndateChang(new CalendarViewPager.OnDateChange() {
            @Override
            public void onDateChange(int year, int month, int day) {
            }

            @Override
            public void onDateSelect(int year, int month, int day, int week) {
                mModifiedCalender.set(year, month - 1, day);
                String montyDay = String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", day);
                mCurrentDate = year + "-" + montyDay;
                mDateTV.setText(mCurrentDate);
                boolean isLoad = false;
                if (mTimetableDetail != null && mTimetableDetail.getHolidayList() != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                    for (TimetableDetail.HolidayListBean bean : mTimetableDetail.getHolidayList()) {
                        int y = Integer.valueOf(simpleDateFormat.format(new Date(bean.getDateOfWeek())));
                        if (montyDay.equals(bean.getStrDate()) && y == year) {
                            isLoad = true;
                            break;
                        }
                    }
                    if (!isLoad && mClassRoomSelect != null) {
                        getTableInfo(mClassRoomSelect);
                    } else if (!isLoad && UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType())) {
                        getTeacherClassDetail();
                    }
                }
                mCalendarScrollView.open();
            }
        });
        mSuperTimeTableLayout.setTimeTableListener(new TimeTableView2.TimeTableListener() {
            @Override
            public void onTimeTableClick(int day, int classSeq, float[] size) {
                if (getSupportFragmentManager().findFragmentByTag(DIALOG_TAG) == null && mTimetableDetail != null && mTimetableDetail.getScheduleList() != null) {
                    //主讲教师发起的
                    if (mClassRoomSelect != null && MASTER_CLASS.equals(mClassRoomSelect.getRoomType()) && mRadioGroup.getCheckedRadioButtonId() == R.id.master_rb) {
                        for (TimetableDetail.ScheduleListBean bean : mTimetableDetail.getScheduleList()) {
                            if (day == bean.getDaySeq() && classSeq == bean.getClassSeq()) {
                                ClassDetailDialog detailDialog = ClassDetailDialog.newInstance(bean, ClassDetailDialog.TYPE, null);
                                detailDialog.show(getSupportFragmentManager(), DIALOG_TAG);
                                break;
                            }
                        }
                    } else {//主讲教师参与的或者接收教室
                        ArrayList<TimetableDetail.ScheduleListBean> scheduleListBeanList = new ArrayList<TimetableDetail.ScheduleListBean>();
                        for (TimetableDetail.ScheduleListBean bean : mTimetableDetail.getScheduleList()) {
                            if (day == bean.getDaySeq() && classSeq == bean.getClassSeq()) {
                                scheduleListBeanList.add(bean);
                            }
                        }
                        if (scheduleListBeanList.size() == 1) {
                            ClassDetailDialog detailDialog = ClassDetailDialog.newInstance(scheduleListBeanList.get(0), ClassDetailDialog.TYPE, null);
                            detailDialog.show(getSupportFragmentManager(), DIALOG_TAG);
                        } else if (scheduleListBeanList.size() > 1) {
                            ListDialog.newInstance(scheduleListBeanList).show(getSupportFragmentManager(), DIALOG_TAG);
                        }
                    }
                }
            }
        });
        mModifiedCalender = Calendar.getInstance();
        mModifiedCalender.set(mCalendarScrollView.getYear(), mCalendarScrollView.getMonth() - 1, mCalendarScrollView.getDay());
        if (mUserInfo != null) {
            switch (mUserInfo.getUserType()) {
                case UserInfo.USER_TYPE_AREA_USER:
                    break;
                case UserInfo.USER_TYPE_SCHOOL_USER:
                    mSchoolID = mUserInfo.getSchoolId();
                    break;
                case UserInfo.USER_TYPE_TEACHER:
                    mFilterBtn.setVisibility(View.GONE);
                    mWeekTV.setVisibility(View.GONE);
                    mDateTV.setVisibility(View.VISIBLE);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    break;
            }
        }
    }

    public void onCalendarClick(View view) {
        mCalendarScrollView.open();
    }

    public void onBack(View view) {
        this.finish();
    }

    /**
     * 筛选
     *
     * @param view
     */
    public void onFilterSelect(View view) {
        mFilterBtn.toggle();
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            if (mClassAdapter != null && mClassAdapter.mClassRoom != null && !mClassAdapter.mClassRoom.getClsClassroomId().equals(mClassRoomSelect.getClsClassroomId())) {
                mClassRoomSelect = mClassAdapter.mClassRoom;
                //选择不同的教室的时候重置日期
                mCalendarScrollView.reSetDate();
                mRadioGroup.check(R.id.master_rb);
                mCurrentDate = mCalendarScrollView.getCurrentDate();
                mDateTV.setText(mCurrentDate);
                mClassAdapter.notifyDataSetChanged();
            }
            if (mClassRoomSelect != null) {
                if (MASTER_CLASS.equals(mClassRoomSelect.getRoomType()) && mRadioGroup.getCheckedRadioButtonId() == R.id.master_rb) {
                    mDateTV.setVisibility(View.GONE);
                    mWeekTV.setVisibility(View.VISIBLE);
                } else {
                    mDateTV.setVisibility(View.VISIBLE);
                    mWeekTV.setVisibility(View.GONE);
                }
                getTableInfo(mClassRoomSelect);
            }
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            if (mCalendarScrollView.isOpen()) {
                mCalendarScrollView.open();
            }
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    /**
     * 日期上一个
     *
     * @param view
     */
    public void previous(View view) {
        //周次切换
        if (!mCanChangeDate && mTimetableDetail != null) {
            mCanChangeDate = true;
            if (mClassRoomSelect != null && MASTER_CLASS.equals(mClassRoomSelect.getRoomType()) && mRadioGroup.getCheckedRadioButtonId() == R.id.master_rb) {
                if (mCurrentWeek > 1) {
                    mCurrentWeek--;
                    getTableInfo(mClassRoomSelect);
                } else {
                    mCanChangeDate = false;
                }
            } else {//日期切换
                mModifiedCalender.add(Calendar.DAY_OF_MONTH, -7);
                int year = mModifiedCalender.get(Calendar.YEAR);
                int month = mModifiedCalender.get(Calendar.MONTH) + 1;
                int day = mModifiedCalender.get(Calendar.DAY_OF_MONTH);
                mCurrentDate = year + "-" + String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", day);
                mCalendarScrollView.setSelectDate(year, month, day);
                mDateTV.setText(mCurrentDate);
                if (UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType())) {
                    getTeacherClassDetail();
                } else {
                    getTableInfo(mClassRoomSelect);
                }
            }
        }
    }

    /**
     * 日期下一个
     *
     * @param view
     */
    public void next(View view) {
        if (!mCanChangeDate && mTimetableDetail != null) {
            mCanChangeDate = true;
            if (mClassRoomSelect != null && MASTER_CLASS.equals(mClassRoomSelect.getRoomType()) && mRadioGroup.getCheckedRadioButtonId() == R.id.master_rb) {
                if (mCurrentWeek < mTimetableDetail.getWeekCount()) {
                    mCurrentWeek++;
                    getTableInfo(mClassRoomSelect);
                } else {
                    mCanChangeDate = false;
                }
            } else {
                mModifiedCalender.add(Calendar.DAY_OF_MONTH, 7);
                int year = mModifiedCalender.get(Calendar.YEAR);
                int month = mModifiedCalender.get(Calendar.MONTH) + 1;
                int day = mModifiedCalender.get(Calendar.DAY_OF_MONTH);
                mCurrentDate = year + "-" + String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", day);
                mCalendarScrollView.setSelectDate(year, month, day);
                mDateTV.setText(mCurrentDate);
                if (UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType())) {
                    getTeacherClassDetail();
                } else {
                    getTableInfo(mClassRoomSelect);
                }
            }
        }
    }


    /**
     * 网络请求
     *
     * @param url
     * @param param
     * @param msg
     */
    private boolean httpConnect(String url, Map<String, String> param, final int msg) {
        return mRequestSender.sendRequest(new RequestSender.RequestData(url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isFinishing()) {
                    return;
                }
                switch (msg) {
                    case GET_BASE_INFO:
                        if (response.optBoolean("result")) {
                            JSONArray array = response.optJSONArray("roomList");
                            mClassRooms = new ArrayList<>(array.length());
                            mClsSchoolTrimesterId = response.optString("clsSchoolTrimesterId");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.optJSONObject(i);
                                ClassRoom classRoom = new ClassRoom();
                                classRoom.setClsClassroomId(object.optString("clsClassroomId"));
                                classRoom.setRoomName(object.optString("roomName"));
                                classRoom.setRoomType(object.optString("roomType"));
                                mClassRooms.add(classRoom);
                            }
                            if (mClassRooms.size() > 0) {
                                mClassRoomSelect = mClassRooms.get(0);
                                mClassRoomTV.setText("教室:" + mClassRoomSelect.getRoomName());
                                if (!MASTER_CLASS.equals(mClassRoomSelect.getRoomType())) {
                                    mClassTableTV.setVisibility(View.GONE);
                                } else {
                                    mClassTableTV.setVisibility(View.VISIBLE);
                                }
                                mClassAdapter = new ClassAdapter(mClassRooms);
                                mClassAdapter.mClassRoom = mClassRoomSelect;
                                mClassRoomLV.setAdapter(mClassAdapter);
                                if (MASTER_CLASS.equals(mClassRoomSelect.getRoomType())) {
                                    mWeekTV.setVisibility(View.VISIBLE);
                                    mDateTV.setVisibility(View.GONE);
                                    mRadioGroup.check(R.id.master_rb);
                                } else {
                                    mWeekTV.setVisibility(View.GONE);
                                    mDateTV.setVisibility(View.VISIBLE);
                                }
                                getTableInfo(mClassRoomSelect);
                            }
                        } else {
                            if (mLoadingDialog.isShowing()) {
                                mLoadingDialog.dismiss();
                            }
                            ToastUtil.showToast(TimeTableDetailActivity.this, "暂无信息！");
                        }
                        break;
                    case GET_TABLE_INFO:
                        mCanChangeDate = false;
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (response.optBoolean("result")) {
                            new GetClassDetail().execute(response);
                        } else {
                            ToastUtil.showToast(TimeTableDetailActivity.this, "暂无课表！");
                            mTimetableDetail = null;
                            mSuperTimeTableLayout.setTimeTable(new ArrayList<TimeTableView2.TimeTable>());
                            mWeekAdapter = new WeekAdapter(0);
                            mRecycleViewPopuWindow.setAdapter(mWeekAdapter);
                            mWeekTV.setText("第0周");
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                if (isFinishing()) {
                    return;
                }
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                mCanChangeDate = false;
                ToastUtil.showToast(TimeTableDetailActivity.this, R.string.net_error);
            }
        }));
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop();
        super.onDestroy();
    }

    public static void start(Context context, String schoolID, String schoolName) {
        Intent intent = new Intent(context, TimeTableDetailActivity.class);
        intent.putExtra(SCHOOL_ID, schoolID);
        intent.putExtra(SCHOOL_NAME, schoolName);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.week_tv://日期点击
                mRecycleViewPopuWindow.showAsDropDown(v);
                break;
            case R.id.classroom_tv:
                if (mClassRoomSelect != null && !MASTER_CLASS.equals(mClassRoomSelect.getRoomType())) {
                    mClassTableTV.setVisibility(View.GONE);
                }
                mClassRoomTV.setVisibility(View.VISIBLE);
                mRadioGroup.setVisibility(View.GONE);
                mClassRoomLV.setVisibility(View.VISIBLE);
                break;
            case R.id.classtable_tv:
                mRadioGroup.setVisibility(View.VISIBLE);
                mClassRoomLV.setVisibility(View.GONE);
                break;
        }
    }

    class ClassAdapter extends BaseAdapter {
        List<ClassRoom> mClassRooms;
        ClassRoom mClassRoom;

        ClassAdapter(List<ClassRoom> classRooms) {
            mClassRooms = classRooms;
        }

        @Override
        public int getCount() {
            return mClassRooms != null ? mClassRooms.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(parent.getContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            //添加点击效果（5.0以上会有水波纹效果）
            // Attribute array
            final ClassRoom classRoom = mClassRooms.get(position);
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            TypedArray a = getTheme().obtainStyledAttributes(attrs);
            // Drawable held by attribute 'selectableItemBackground' is at index '0'
            Drawable d = a.getDrawable(0);
            a.recycle();
            textView.setBackgroundDrawable(d);
            textView.setClickable(true);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, UIUtils.dip2px(parent.getContext(), 15));
            textView.setPadding(UIUtils.dip2px(parent.getContext(), 10), 0, 0, 0);
            textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(parent.getContext(), 50)));
            textView.setText(classRoom.getRoomName());
            if (mClassRoom != null) {
                if (mClassRoom.getClsClassroomId().equals(classRoom.getClsClassroomId())) {
                    textView.setTextColor(getResources().getColor(R.color.main_color));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.busy_indicator));
                }
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClassRoom = classRoom;
                    mClassRoomTV.setText("教室:" + mClassRoom.getRoomName());
                    if (!MASTER_CLASS.equals(mClassRoom.getRoomType())) {
                        mClassTableTV.setVisibility(View.GONE);
                    } else {
                        mClassTableTV.setVisibility(View.VISIBLE);
                    }
                    notifyDataSetChanged();
                }
            });
            return textView;
        }
    }

    class WeekAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        int mCount;

        WeekAdapter(int count) {
            mCount = count;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setGravity(Gravity.CENTER);
            //添加点击效果（5.0以上会有水波纹效果）
            // Attribute array
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            TypedArray a = getTheme().obtainStyledAttributes(attrs);
            // Drawable held by attribute 'selectableItemBackground' is at index '0'
            Drawable d = a.getDrawable(0);
            a.recycle();
            textView.setBackgroundDrawable(d);
            textView.setClickable(true);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, UIUtils.dip2px(parent.getContext(), 15));
            textView.setPadding(UIUtils.dip2px(parent.getContext(), 10), 0, 0, 0);
            textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(parent.getContext(), 30)));
            textView.setText("第" + (viewType + 1) + "周");
            if (mTimetableDetail != null) {
                if (viewType + 1 == mTimetableDetail.getCurrentWeek()) {
                    textView.setTextColor(getResources().getColor(R.color.main_color));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.busy_indicator));
                }
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeekTV.setText("第" + (viewType + 1) + "周");
                    mCurrentWeek = viewType + 1;
                    getTableInfo(mClassRoomSelect);
                    mRecycleViewPopuWindow.dismiss();
                }
            });
            return new RecyclerView.ViewHolder(textView) {
            };
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mCount;
        }
    }

    class ClassRoom {

        /**
         * clsClassroomId : c9008ada47af4484ba67735b837b4a76
         * roomName : 接收教室1
         * roomType : RECEIVE
         */

        private String clsClassroomId;
        private String roomName;
        private String roomType;

        public String getClsClassroomId() {
            return clsClassroomId;
        }

        public void setClsClassroomId(String clsClassroomId) {
            this.clsClassroomId = clsClassroomId;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }
    }

    class GetClassDetail extends AsyncTask<JSONObject, Integer, TimetableDetail> {

        @Override
        protected TimetableDetail doInBackground(JSONObject... params) {
            JSONObject object = params[0];
            TimetableDetail timetableDetail = null;
            Gson gson = new Gson();
            try {
                timetableDetail = gson.fromJson(object.toString(), TimetableDetail.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                ToastUtil.showToast(TimeTableDetailActivity.this, "数据异常！");
            }
            return timetableDetail;
        }

        @Override
        protected void onPostExecute(TimetableDetail timetableDetail) {
            mTimetableDetail = timetableDetail;
            if (mTimetableDetail != null) {
                mSuperTimeTableLayout.setClassCount(mTimetableDetail.getMorningCount(), mTimetableDetail.getAfternoonCount());
                mCurrentWeek = timetableDetail.getCurrentWeek();
                if (mTimetableDetail.getCurrentDate() != null) {
                    mDateTV.setText(mTimetableDetail.getCurrentDate());
                }
                mWeekTV.setText("第" + mTimetableDetail.getCurrentWeek() + "周");
                mWeekAdapter = new WeekAdapter(timetableDetail.getWeekCount());
                mRecycleViewPopuWindow.setAdapter(mWeekAdapter);
                ArrayList<TimeTableView2.Holiday> week = new ArrayList<>(7);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                for (int i = 0; i < mTimetableDetail.getHolidayList().size(); i++) {
                    TimeTableView2.Holiday holiday = new TimeTableView2.Holiday();
                    holiday.setIsholiday(mTimetableDetail.getHolidayList().get(i).isHolidayFlag());
                    holiday.setmDate(mTimetableDetail.getHolidayList().get(i).getStrDate());
                    Date date = new Date(mTimetableDetail.getHolidayList().get(i).getDateOfWeek());
                    holiday.setYear(Integer.valueOf(simpleDateFormat.format(date)));
                    week.add(holiday);
                }
                mSuperTimeTableLayout.setWeekDate(week);
                if (mTimetableDetail.getScheduleList() != null) {
                    if (!UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType()) && MASTER_CLASS.equals(mClassRoomSelect.getRoomType()) && mRadioGroup.getCheckedRadioButtonId() == R.id.master_rb) {
                        List<TimeTableView2.TimeTable> tables = new ArrayList<>(mTimetableDetail.getScheduleList().size());
                        for (TimetableDetail.ScheduleListBean bean : mTimetableDetail.getScheduleList()) {
                            TimeTableView2.TimeTable timeTableContent = new TimeTableView2.TimeTable();
                            timeTableContent.setDaySeq(bean.getDaySeq());
                            timeTableContent.setClassSeq(bean.getClassSeq());
                            timeTableContent.setClassName(bean.getSubjectName());
                            if ("PROGRESS".equals(bean.getStatus())) {
                                timeTableContent.setProcessing(true);
                            }
                            tables.add(timeTableContent);
                        }
                        mSuperTimeTableLayout.setTimeTable(tables);
                    } else {
                        List<TimeTableView2.TimeTable> tables = new ArrayList<>();
                        List<TimetableDetail.ScheduleListBean> scheduleLists = mTimetableDetail.getScheduleList();
                        for (TimetableDetail.ScheduleListBean bean : scheduleLists) {
                            boolean has = false;
                            if (!bean.isAdd()) {
                                if (tables.size() == 0) {
                                    TimeTableView2.TimeTable timeTableContent = new TimeTableView2.TimeTable();
                                    timeTableContent.setDaySeq(bean.getDaySeq());
                                    timeTableContent.setClassSeq(bean.getClassSeq());
                                    timeTableContent.setClassName(bean.getSubjectName());
                                    if ("PROGRESS".equals(bean.getStatus())) {
                                        timeTableContent.setProcessing(true);
                                    }
                                    tables.add(timeTableContent);
                                } else {
                                    for (TimeTableView2.TimeTable timeTable : tables) {
                                        if (timeTable.getDaySeq() == bean.getDaySeq() && timeTable.getClassSeq() == bean.getClassSeq()) {
                                            timeTable.setCount(timeTable.getCount() + 1);
                                            timeTable.setClassName((timeTable.getCount() + 1) + "节课");
                                            if ("PROGRESS".equals(bean.getStatus())) {
                                                timeTable.setProcessing(true);
                                            }
                                            has = true;
                                            break;
                                        }
                                    }
                                    if (!has) {
                                        TimeTableView2.TimeTable timeTable = new TimeTableView2.TimeTable();
                                        timeTable.setDaySeq(bean.getDaySeq());
                                        timeTable.setClassSeq(bean.getClassSeq());
                                        timeTable.setClassName(bean.getSubjectName());
                                        if ("PROGRESS".equals(bean.getStatus())) {
                                            timeTable.setProcessing(true);
                                        }
                                        tables.add(timeTable);
                                    }
                                }
                            }
                        }
                        mSuperTimeTableLayout.setTimeTable(tables);
                    }
                }
                if (mWeekAdapter != null) {
                    mWeekAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        UIUtils.addExitTranAnim(this);
    }
}
