package com.codyy.erpsportal.reservation.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.CalendarScrollView;
import com.codyy.erpsportal.commons.widgets.CalendarViewPager;
import com.codyy.erpsportal.commons.widgets.RecycleViewPopuWindow;
import com.codyy.erpsportal.commons.widgets.TimeTable.TimeTableView2;
import com.codyy.erpsportal.reservation.controllers.fragments.ListDialog;
import com.codyy.erpsportal.reservation.controllers.fragments.ReservationDetialDialog;
import com.codyy.erpsportal.reservation.models.entities.ReservationClassDetial;
import com.codyy.erpsportal.reservation.models.entities.ReservationClassItem;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReservationClassDetailActivity extends AppCompatActivity {
    /**
     * 获取课表
     */
    private final static int GET_CLASS_DETIAL = 0x001;
    /**
     * 获取课表详情
     */
    private final static int GET_CLASS_TABLE_DETIAL = GET_CLASS_DETIAL + 1;
    public final static String EXTRA_RESERVATION = "reservation_item";
    private final static String DIALOG_TAG = "ReservationDetialDialog-----------------";
    private RecycleViewPopuWindow mPopupWindow;
    private ImageView mTitleFilterIV;
    private CalendarScrollView mCalendarScrollView;
    private RequestSender mRequestSender;
    private TextView mTitleTV;
    private TextView mClassRoomTV;
    private ReservationClassItem mReservationClassItem;
    private UserInfo mUserInfo;
    private ReservationClassDetial mReservationClassDetial;
    private ReservationClassDetial.RoomListBean mSelectRoom;
    private TimeTableView2 mTimeTableView2;
    private String mCurrentDate;
    private String mSchollID;
    private Integer mHashTag = this.hashCode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_class_detail);
        mReservationClassItem = getIntent().getParcelableExtra(EXTRA_RESERVATION);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        if (mReservationClassItem != null) {
            mSchollID = mReservationClassItem.getClsSchoolId();
        } else {
            mSchollID = mUserInfo.getSchoolId();
        }
        init();
        getClassDetial();
    }

    private void getClassDetial() {
        Map<String, String> param = new HashMap<>();
        param.put("uuid", mUserInfo.getUuid());
        if (mCurrentDate != null) {
            param.put("date", mCurrentDate);
        }
        if (mSelectRoom != null) {
            param.put("classroomId", mSelectRoom.getClsClassroomId());
        }
        param.put("schoolId", mSchollID);
        if (UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType())) {
            param.put("teacherId", mUserInfo.getBaseUserId());
        }
        httpConnect(URLConfig.GET_LIVE_SCHOOL_INFODETAIL, param, GET_CLASS_DETIAL);
    }

    private void init() {
        mCalendarScrollView = (CalendarScrollView) findViewById(R.id.calendarscrollview);
        mCurrentDate = mCalendarScrollView.getCurrentDate();
        mCalendarScrollView.setOndateChang(new CalendarViewPager.OnDateChange() {
            @Override
            public void onDateChange(int year, int month, int day) {

            }

            @Override
            public void onDateSelect(int year, int month, int day, int week) {
                String montyDay = String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", day);
                mCurrentDate = year + "-" + montyDay;
                boolean isRequest = false;
                for (TimeTableView2.Holiday bean : mReservationClassDetial.getWeekList()) {
                    if (bean.getmDate().equals(montyDay) && bean.getYear() == year) {
                        isRequest = true;
                        break;
                    }
                }
                if (!isRequest) {
                    getClassDetial();
                }
                mCalendarScrollView.open();
            }
        });
        mTitleFilterIV = (ImageView) findViewById(R.id.title_filter);
        mTitleFilterIV.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon));
        mTitleTV = (TextView) findViewById(R.id.title_text);
        mRequestSender = new RequestSender(this);
        mPopupWindow = new RecycleViewPopuWindow(this);
        mPopupWindow.setLayoutManager(new LinearLayoutManager(this));
        mTimeTableView2 = (TimeTableView2) findViewById(R.id.timetableview2);
        mTimeTableView2.setTimeTableListener(new TimeTableView2.TimeTableListener() {
            @Override
            public void onTimeTableClick(int day, int classSeq, float[] size) {
                if (getSupportFragmentManager().findFragmentByTag(DIALOG_TAG) == null && mReservationClassDetial != null && mReservationClassDetial.getScheduleList() != null) {
                    ArrayList<ReservationClassDetial.ScheduleListBean> listBeen = new ArrayList<ReservationClassDetial.ScheduleListBean>();
                    for (ReservationClassDetial.ScheduleListBean bean : mReservationClassDetial.getScheduleList()) {
                        if (bean.getDaySeq() == day && classSeq == bean.getClassSeq()) {
                            listBeen.add(bean);
                        }
                    }
                    if (listBeen.size() == 1) {
                        ReservationDetialDialog detialDialog = ReservationDetialDialog.newInstance(mSchollID, listBeen.get(0).getLiveAppointmentId());
                        detialDialog.show(getSupportFragmentManager(), DIALOG_TAG);
                    } else if (listBeen.size() > 1) {
                        ListDialog dialog = ListDialog.newInstance(listBeen);
                        dialog.show(getSupportFragmentManager(), DIALOG_TAG);
                        dialog.setOnItemClick(new ListDialog.OnItemClick() {
                            @Override
                            public void onClick(String id) {
                                ReservationDetialDialog detialDialog = ReservationDetialDialog.newInstance(mSchollID, id);
                                detialDialog.show(getSupportFragmentManager(), DIALOG_TAG);
                            }
                        });
                    }
                }
            }
        });
        mClassRoomTV = (TextView) findViewById(R.id.classroom_tv);
        if (mUserInfo != null) {
            switch (mUserInfo.getUserType()) {
                case UserInfo.USER_TYPE_AREA_USER:
                    if (mReservationClassItem != null) {
                        mTitleTV.setText(mReservationClassItem.getSchoolName());
                    }
                    break;
                case UserInfo.USER_TYPE_SCHOOL_USER:
                    mTitleTV.setText(mUserInfo.getSchoolName());
                    break;
                case UserInfo.USER_TYPE_TEACHER:
                    mClassRoomTV.setVisibility(View.GONE);
                    mTitleTV.setText("课程表");
                    break;
            }
        }
    }


    public void onClassRoomClick(View view) {
        mPopupWindow.showAsDropDown(view);
    }

    public void onFilterSelect(View view) {
        mCalendarScrollView.open();
    }

    public void onBack(View view) {
        finish();
    }

    public static void start(Context context, ReservationClassItem item) {
        Intent intent = new Intent(context, ReservationClassDetailActivity.class);
        intent.putExtra(EXTRA_RESERVATION, item);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    /**
     * 网络请求
     *
     * @param url
     * @param param
     * @param msg
     */
    private void httpConnect(String url, Map<String, String> param, final int msg) {
        mRequestSender.sendRequest(new RequestSender.RequestData(url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isFinishing()) {
                    return;
                }
                switch (msg) {
                    case GET_CLASS_DETIAL:
                        if ("success".equals(response.optString("result"))) {
                            new GetClassDetail().execute(response);
                        }
                        if (mCalendarScrollView.isOpen()) {
                            mCalendarScrollView.open();
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {

            }
        }, mHashTag));
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(mHashTag);
        super.onDestroy();
    }


    class ClassAdapter extends RecyclerView.Adapter {
        List<ReservationClassDetial.RoomListBean> mRoomListBeen;

        ClassAdapter(List<ReservationClassDetial.RoomListBean> roomListBeen) {
            mRoomListBeen = roomListBeen;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            final ReservationClassDetial.RoomListBean roomListBean = mRoomListBeen.get(position);
            TextView textView = new TextView(ReservationClassDetailActivity.this);
            textView.setGravity(Gravity.CENTER);
            //添加点击效果（5.0以上会有水波纹效果）
            // Attribute array
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            TypedArray a = getTheme().obtainStyledAttributes(attrs);
            // Drawable held by attribute 'selectableItemBackground' is at index '0'
            Drawable d = a.getDrawable(0);
            a.recycle();
            textView.setBackgroundDrawable(d);
            textView.setText(roomListBean.getRoomName());
            textView.setClickable(true);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, UIUtils.dip2px(parent.getContext(), 15));
            textView.setPadding(UIUtils.dip2px(ReservationClassDetailActivity.this, 10), 0, 0, 0);
            if (mSelectRoom != null) {
                if (mSelectRoom.getClsClassroomId().equals(roomListBean.getClsClassroomId())) {
                    textView.setTextColor(getResources().getColor(R.color.main_color));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.busy_indicator));
                }
            }
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(ReservationClassDetailActivity.this, 40)));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectRoom = roomListBean;
                    mClassRoomTV.setText("教室:" + mSelectRoom.getRoomName());
                    mPopupWindow.dismiss();
                    getClassDetial();
                }
            });
            return new RecyclerView.ViewHolder(textView) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mRoomListBeen.size();
        }
    }

    class GetClassDetail extends AsyncTask<JSONObject, Integer, ReservationClassDetial> {

        @Override
        protected ReservationClassDetial doInBackground(JSONObject... params) {
            ReservationClassDetial reservationClassDetial = new Gson().fromJson(params[0].toString(), ReservationClassDetial.class);
            if (reservationClassDetial != null && reservationClassDetial.getHolidayList() != null && reservationClassDetial.getScheduleList() != null) {
                ArrayList<TimeTableView2.Holiday> week = new ArrayList<>(7);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                for (ReservationClassDetial.HolidayListBean bean : reservationClassDetial.getHolidayList()) {
                    TimeTableView2.Holiday holiday = new TimeTableView2.Holiday();
                    holiday.setmDate(bean.getStrDate());
                    holiday.setIsholiday(bean.isHolidayFlag());
                    holiday.setYear(Integer.valueOf(simpleDateFormat.format(new Date(bean.getDateOfWeek()))));
                    week.add(holiday);
                }
                for (ReservationClassDetial.ScheduleListBean bean : reservationClassDetial.getScheduleList()) {
                    String date = DateUtil.dateToString(new Date(bean.getBeginTime()), "MM-dd");
                    for (ReservationClassDetial.HolidayListBean item : reservationClassDetial.getHolidayList()) {
                        if (date.equals(item.getStrDate())) {
                            bean.setDaySeq(item.getDaySeq());
                        }
                    }
                }
                List<TimeTableView2.TimeTable> tables = new ArrayList<>();
                for (ReservationClassDetial.ScheduleListBean bean : reservationClassDetial.getScheduleList()) {
                    boolean has = false;
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
                reservationClassDetial.setTimeTableContents(tables);
                reservationClassDetial.setWeekList(week);
            }
            return reservationClassDetial;
        }

        @Override
        protected void onPostExecute(ReservationClassDetial reservationClassDetial) {
            if (reservationClassDetial != null) {
                mReservationClassDetial = reservationClassDetial;
                if (mReservationClassDetial.getRoomList() != null && mReservationClassDetial.getRoomList().size() > 0) {
                    if (mSelectRoom == null) {
                        mSelectRoom = mReservationClassDetial.getRoomList().get(0);
                        mClassRoomTV.setText("教室:" + mSelectRoom.getRoomName());
                    }
                    mPopupWindow.setAdapter(new ClassAdapter(mReservationClassDetial.getRoomList()));
                }
                mTimeTableView2.setWeekDate(mReservationClassDetial.getWeekList());
                mTimeTableView2.setTimeTable(mReservationClassDetial.getTimeTableContents());
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        UIUtils.addExitTranAnim(this);
    }
}
