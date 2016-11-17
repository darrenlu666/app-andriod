package com.codyy.erpsportal.county.controllers.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.CalendarScrollView;
import com.codyy.erpsportal.commons.widgets.CalendarViewPager;
import com.codyy.erpsportal.commons.widgets.RecycleViewPopuWindow;
import com.codyy.erpsportal.commons.widgets.TimeTableView2;
import com.codyy.erpsportal.county.controllers.fragments.DialogStatisticsFragment;
import com.codyy.erpsportal.county.controllers.fragments.ListDialog;
import com.codyy.erpsportal.county.controllers.models.entities.CountyClassDetial;
import com.codyy.erpsportal.county.controllers.models.entities.CountyDetialFilter;
import com.codyy.erpsportal.county.widgets.ClassDetailDialog;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;

public class CountyClassDetailActivity extends ToolbarActivity implements View.OnClickListener {
    private Integer mHashTag = this.hashCode();
    /**
     * 主讲教室
     */
    public final static int TYPE_MASTERCLASSROOM = 0x001;
    /**
     * 接收教室
     */
    public final static int TYPE_RECEIVEROOM = 0x002;
    /**
     * 教师
     */
    public final static int TYPE_TEACHER = 0x003;
    private final static String DIALOG_TAG = "detailDialog---";

    public final static String EXTRA_CLASSROOMID = "class_room_id";
    public final static String EXTRA_TEACHERID = "teacher_id";
    public final static String EXTRA_TITLE = "title_";
    public final static String EXTRA_TYPE = "type_";
    /**
     * 获取详情
     */
    private final static int GET_DETIAL = 0x001;
    /**
     * 获取教室
     */
    private final static int GET_FILTER = 0x002;
    /**
     * 获取课表详情
     */
    private final static int GET_CLASS_DETAIL = 0x003;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextView;
    @Bind(R.id.calendarscrollview)
    CalendarScrollView mCalendarScrollView;
    @Bind(R.id.timetableview2)
    TimeTableView2 mTimeTableView2;
    @Bind(R.id.date_text)
    TextView mDateTV;
    @Bind(R.id.week_text)
    TextView mWeekTV;
    @Bind(R.id.class_tv)
    TextView mClassTV;
    /**
     * 课表详情弹框
     */
    private Dialog mDialog;
    private RecycleViewPopuWindow mPopupWindowFilter;
    private RecycleViewPopuWindow mPopupWeek;
    private RequestSender mRequestSender;
    private UserInfo mUserInfo;
    private String mClassRoomID;
    private String mReceiveClassRoomId;
    private String mTeacherID;
    private String mCurrentDate;
    /**
     * 当前周
     */
    private int mCurrentWeek;
    private CountyClassDetial mCountyClassDetial;
    /**
     * 日期计算
     */
    private Calendar mModifiedCalender;
    private int mType;
    private String mUrl;
    private CountyDetialFilter mCountyDetialFilter;
    /**
     * 是否可点击
     */
    private boolean mCanClick;
    private WeekAdapter mWeekAdapter;

    private String mRealityNum;
    private String mRealityNumRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClassRoomID = getIntent().getStringExtra(EXTRA_CLASSROOMID);
        mTeacherID = getIntent().getStringExtra(EXTRA_TEACHERID);
        mType = getIntent().getIntExtra(EXTRA_TYPE, TYPE_TEACHER);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title == null) {
            title = "课表";
        }
        switch (mType) {
            case TYPE_MASTERCLASSROOM:
                mRealityNum = "实际开课数 ";
                mRealityNumRate = "实际开课占比 ";
                mWeekTV.setVisibility(View.VISIBLE);
                mDateTV.setVisibility(View.GONE);
                mUrl = URLConfig.CONTY_GET_MAINCLASSROOM;
                break;
            case TYPE_RECEIVEROOM:
                mRealityNum = "实际听课数 ";
                mRealityNumRate = "实际听课占比 ";
                mUrl = URLConfig.CONTY_GET_RECEIVECLASSROOM;
                break;
            case TYPE_TEACHER:
                mRealityNum = "实际开课数 ";
                mRealityNumRate = "实际开课占比 ";
                mUrl = URLConfig.CONTY_GET_MAINTEACHER_SCHEDULE;
                break;
        }
        mTextView.setText(title);
        getFilter();
    }

    private void getFilter() {
        Map<String, String> param = new HashMap<>();
        param.put("uuid", mUserInfo.getUuid());
        param.put("classroomId", mClassRoomID);
        if (mTeacherID != null) {
            param.put("masterTeaId", mTeacherID);
        }
        param.put("type", String.valueOf(mType));
        httpConnect(URLConfig.CONTY_GET_FILTER, param, GET_FILTER);
    }

    private void loadData() {
        Map<String, String> param = new HashMap<>();
        param.put("uuid", mUserInfo.getUuid());
        switch (mType) {
            case TYPE_MASTERCLASSROOM:
                if (mClassRoomID != null) {
                    param.put("masterClassRoomId", mClassRoomID);
                }
                param.put("weekSque", String.valueOf(mCurrentWeek));
                if (mReceiveClassRoomId != null) {
                    param.put("receiveClassRoomId", mReceiveClassRoomId);
                }
                break;
            case TYPE_RECEIVEROOM:
                if (mClassRoomID != null) {
                    param.put("receiveClassRoomId", mClassRoomID);
                }
                if (mReceiveClassRoomId != null) {
                    param.put("masterClassRoomId", mReceiveClassRoomId);
                }
                param.put("selectedDate", mCurrentDate);
                break;
            case TYPE_TEACHER:
                if (mClassRoomID != null) {
                    param.put("masterClassRoomId", mClassRoomID);
                }
                if (mTeacherID != null) {
                    param.put("masterTeaId", mTeacherID);
                }
                if (mReceiveClassRoomId != null) {
                    param.put("receiveClassRoomId", mReceiveClassRoomId);
                }
                param.put("selectedDate", mCurrentDate);
                break;
        }
        httpConnect(mUrl, param, GET_DETIAL);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_conty_class_detail;
    }

    @Override
    protected void initToolbar() {
        initToolbar(mToolbar);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mRequestSender = new RequestSender(this);
        mPopupWindowFilter = new RecycleViewPopuWindow(this);
        mPopupWeek = new RecycleViewPopuWindow(this);
        mPopupWeek.setLayoutManager(new GridLayoutManager(this, 4));
        mPopupWindowFilter.setLayoutManager(new LinearLayoutManager(this));
        mDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        View mSelectDialogView = getLayoutInflater().inflate(R.layout.conty_classdetail_popu, null);
        mDialog.setContentView(mSelectDialogView);
        mDialog.getWindow().setWindowAnimations(R.style.dialog_filter_animation);
        mTimeTableView2.setTimeTableListener(new TimeTableView2.TimeTableListener() {
            @Override
            public void onTimeTableClick(int day, int classSeq, float[] size) {
                if (getSupportFragmentManager().findFragmentByTag(DIALOG_TAG) == null && mCountyClassDetial != null) {
                    ArrayList<CountyClassDetial.DataListBean> dataListBeen = new ArrayList<CountyClassDetial.DataListBean>();
                    for (CountyClassDetial.DataListBean bean : mCountyClassDetial.getDataList()) {
                        if (day == bean.getDaySque() && classSeq == bean.getClassSque()) {
                            dataListBeen.add(bean);
                        }
                    }
                    if (dataListBeen.size() == 1) {
                        ClassDetailDialog detailDialog = ClassDetailDialog.newInstance(null, ClassDetailDialog.TYPE_CONTY_DETAIL, dataListBeen.get(0).getClsScheduleDetailId());
                        detailDialog.show(getSupportFragmentManager(), DIALOG_TAG);
                        //                    getClassDetail(dataListBeen.get(0).getClsScheduleDetailId());
                    } else if (dataListBeen.size() > 1) {
                        ListDialog dialog = ListDialog.newInstance(dataListBeen);
                        dialog.setOnItemClick(new ListDialog.OnItemClick() {
                            @Override
                            public void onClick(String id) {
                                ClassDetailDialog detailDialog = ClassDetailDialog.newInstance(null, ClassDetailDialog.TYPE_CONTY_DETAIL, id);
                                detailDialog.show(getSupportFragmentManager(), DIALOG_TAG);
                            }
                        });
                        dialog.show(getSupportFragmentManager(), DIALOG_TAG);
                    }
                }
            }
        });
        mCurrentDate = mCalendarScrollView.getCurrentDate();
        mDateTV.setText(mCurrentDate);
        mCalendarScrollView.setOndateChang(new CalendarViewPager.OnDateChange() {
            @Override
            public void onDateChange(int year, int month, int day) {

            }

            @Override
            public void onDateSelect(int year, int month, int day, int week) {
                mModifiedCalender.set(year, month - 1, day);
                mCurrentDate = year + "-" + String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", day);
                mDateTV.setText(mCurrentDate);
                loadData();
                mCalendarScrollView.open();
            }
        });
        mModifiedCalender = Calendar.getInstance();
        mModifiedCalender.set(mCalendarScrollView.getYear(), mCalendarScrollView.getMonth() - 1, mCalendarScrollView.getDay());
    }

    /**
     * 获取课表详细信息
     *
     * @param id
     */
    private void getClassDetail(String id) {
        Map<String, String> param = new HashMap<>();
        param.put("uuid", mUserInfo.getUuid());
        param.put("scheduleDetailId", id);
        httpConnect(URLConfig.CONTY_GET_SCHEDULE_DETAIL, param, GET_CLASS_DETAIL);
    }

    private void httpConnect(String url, Map<String, String> param, final int msg) {
        mRequestSender.sendRequest(new RequestSender.RequestData(url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isFinishing()) {
                    return;
                }
                switch (msg) {
                    case GET_DETIAL:
                        mCanClick = false;
                        if ("success".equals(response.optString("result"))) {
                            new GetClassDetail().execute(response);
                        }
                        break;
                    case GET_FILTER:
                        if ("success".equals(response.optString("result"))) {
                            Gson gson = new Gson();
                            try {
                                mCountyDetialFilter = gson.fromJson(response.toString(), CountyDetialFilter.class);
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                                ToastUtil.showToast(CountyClassDetailActivity.this, "数据异常！");
                            }
                            if (mCountyDetialFilter != null) {
                                CountyDetialFilter.ClassroomListBean bean = new CountyDetialFilter.ClassroomListBean();
                                bean.setClassId(null);
                                bean.setCheck(true);
                                bean.setClassName("总课表");
                                mCountyDetialFilter.getClassroomList().add(0, bean);
                                FilterAdapter adapter = new FilterAdapter(mCountyDetialFilter.getClassroomList());
                                mPopupWindowFilter.setAdapter(adapter);
                                if (mType == TYPE_MASTERCLASSROOM) {
                                    mWeekTV.setText("第" + mCountyDetialFilter.getCurrentWeek() + "周");
                                    mCurrentWeek = mCountyDetialFilter.getCurrentWeek();
                                    mWeekAdapter = new WeekAdapter(mCountyDetialFilter.getTotalWeek());
                                    mPopupWeek.setAdapter(mWeekAdapter);
                                }
                                loadData();
                            }
                        }
                        break;
                    case GET_CLASS_DETAIL:

                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                switch (msg) {
                    case GET_DETIAL:
                        mCanClick = false;
                        ToastUtil.showToast(CountyClassDetailActivity.this, "获取数据失败！");
                        break;
                }
            }
        }, mHashTag));
    }

    /**
     * 日期点击
     *
     * @param view
     */
    public void onCalendarClick(View view) {
        mCalendarScrollView.open();
    }

    /**
     * 周点击
     *
     * @param view
     */
    public void onWeekClick(View view) {
        mPopupWeek.showAsDropDown(view);
    }

    public void onTableListClick(View view) {
        mPopupWindowFilter.showAsDropDown(view);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CountyClassDetailActivity.class);
        context.startActivity(intent);
    }


    private void setData() {
        if (mCountyClassDetial != null) {
            DecimalFormat df = new DecimalFormat("0.##");
            TextView week_plan = (TextView) findViewById(R.id.week_plan);
            week_plan.setText("计划课时数 " + mCountyClassDetial.getWeekClass().getWeekPlanNum());
            TextView week_week = (TextView) findViewById(R.id.week_week);
            week_week.setText("周课时数 " + mCountyClassDetial.getWeekClass().getWeekScheNum());
            TextView week_reality = (TextView) findViewById(R.id.week_Reality);
            week_reality.setText(mRealityNum);

            String week_realityStr = String.valueOf(mCountyClassDetial.getWeekClass().getWeekRealityNum());
            SpannableStringBuilder week_realitySpan = new SpannableStringBuilder(week_realityStr);
            week_realitySpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, week_realityStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            week_reality.append(week_realitySpan);
            week_reality.setOnClickListener(this);

            TextView week_rate = (TextView) findViewById(R.id.week_rate);
            float rate;
            if (mCountyClassDetial.getWeekClass().getWeekPlanNum() > 0) {
                rate = ((float) mCountyClassDetial.getWeekClass().getWeekRealityNum() / mCountyClassDetial.getWeekClass().getWeekPlanNum()) * 100;
            } else {
                rate = 0;
            }
            week_rate.setText(mRealityNumRate);
            String week_rateStr = df.format(rate) + "%";
            SpannableStringBuilder week_rateSpan = new SpannableStringBuilder(week_rateStr);
            week_rateSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, week_rateStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            week_rate.append(week_rateSpan);
            week_rate.setOnClickListener(this);

            TextView term_all = (TextView) findViewById(R.id.term_all);
            term_all.setText("计划总课时数 " + mCountyClassDetial.getTermClass().getMaxTermPlanNum());
            TextView term_stu = (TextView) findViewById(R.id.term_stu);
            term_stu.setText("受益学生数 " + mCountyClassDetial.getTermClass().getBenefitStuNum());
            TextView term_plan = (TextView) findViewById(R.id.term_plan);
            term_plan.setText("计划课时数 " + mCountyClassDetial.getTermClass().getPlanScheduleNum());

            TextView term_reality = (TextView) findViewById(R.id.term_reality);
            term_reality.setText(mRealityNum);
            String term_realityStr = String.valueOf(mCountyClassDetial.getTermClass().getRealityScheduleNum());
            SpannableStringBuilder term_realitySpan = new SpannableStringBuilder(term_realityStr);
            term_realitySpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, term_realitySpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            term_reality.append(term_realitySpan);
            term_reality.setOnClickListener(this);
            float rateTerm;
            if (mCountyClassDetial.getTermClass().getPlanScheduleNum() > 0) {
                rateTerm = ((float) mCountyClassDetial.getTermClass().getRealityScheduleNum() / mCountyClassDetial.getTermClass().getPlanScheduleNum()) * 100;
            } else {
                rateTerm = 0;
            }
            TextView term_rate = (TextView) findViewById(R.id.term_rate);
            term_rate.setText(mRealityNumRate);
            String str = df.format(rateTerm) + "%";
            SpannableStringBuilder term_rateSpan = new SpannableStringBuilder(str);
            term_rateSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            term_rate.append(term_rateSpan);
            term_rate.setOnClickListener(this);
        }
    }

    /**
     * 日期周次切换----前一个
     *
     * @param view
     */
    public void previous(View view) {
        if (mCanClick) {
            return;
        }
        if (mType == TYPE_MASTERCLASSROOM) {
            if (mCurrentWeek > 1) {
                mCurrentWeek--;
                mWeekTV.setText("第" + mCurrentWeek + "周");
                if (mWeekAdapter != null) {
                    mWeekAdapter.notifyDataSetChanged();
                }
            }
        } else {
            mModifiedCalender.add(Calendar.DAY_OF_MONTH, -7);
            int year = mModifiedCalender.get(Calendar.YEAR);
            int month = mModifiedCalender.get(Calendar.MONTH) + 1;
            int day = mModifiedCalender.get(Calendar.DAY_OF_MONTH);
            mCurrentDate = year + "-" + String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", day);
            mCalendarScrollView.setSelectDate(year, month, day);
            mDateTV.setText(mCurrentDate);
        }
        mCanClick = true;
        loadData();
    }

    /**
     * 日期周次切换----后一个
     *
     * @param view
     */
    public void next(View view) {
        if (mCanClick) {
            return;
        }
        mCanClick = true;
        if (mType == TYPE_MASTERCLASSROOM) {
            if (mCurrentWeek < mCountyDetialFilter.getTotalWeek()) {
                mCurrentWeek++;
                mWeekTV.setText("第" + mCurrentWeek + "周");
                if (mWeekAdapter != null) {
                    mWeekAdapter.notifyDataSetChanged();
                }
            }
        } else {
            mModifiedCalender.add(Calendar.DAY_OF_MONTH, 7);
            int year = mModifiedCalender.get(Calendar.YEAR);
            int month = mModifiedCalender.get(Calendar.MONTH) + 1;
            int day = mModifiedCalender.get(Calendar.DAY_OF_MONTH);
            mCurrentDate = year + "-" + String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", day);
            mCalendarScrollView.setSelectDate(year, month, day);
            mDateTV.setText(mCurrentDate);
        }
        loadData();
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(mHashTag);
        super.onDestroy();
    }

    /**
     * @param context
     * @param classRoomID
     * @param teacherID
     */
    public static void start(Context context, String classRoomID, String teacherID, String title, int type) {
        Intent intent = new Intent(context, CountyClassDetailActivity.class);
        intent.putExtra(EXTRA_CLASSROOMID, classRoomID);
        intent.putExtra(EXTRA_TEACHERID, teacherID);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int type = DialogStatisticsFragment.DIALOG_TYPE_DETIAL;
        Bundle bundle = new Bundle();
        String urlType = null;
        switch (v.getId()) {
            case R.id.week_Reality:
                urlType = DialogStatisticsFragment.URL_TYPE_WEEK;
            case R.id.term_reality:
                if (urlType == null) {
                    urlType = DialogStatisticsFragment.URL_TYPE_SEMESTER;
                }
                type = DialogStatisticsFragment.DIALOG_TYPE_DETIAL;
                bundle.putString(DialogStatisticsFragment.EXTRA_URL_TYPE, urlType);
                if (mType == CountyClassDetailActivity.TYPE_MASTERCLASSROOM) {//主讲教室
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putInt(DialogStatisticsFragment.EXTRA_WEEKSEQ, mCurrentWeek);
                } else if (mType == CountyClassDetailActivity.TYPE_RECEIVEROOM) {//接收教室
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putString(DialogStatisticsFragment.EXTRA_DATATIME, mCurrentDate);
                } else {//主讲教师
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putString(DialogStatisticsFragment.EXTRA_BASEUSERID, mTeacherID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_DATATIME, mCurrentDate);
                }
                break;
            case R.id.week_rate:
                urlType = DialogStatisticsFragment.URL_TYPE_WEEK;
            case R.id.term_rate:
                if (urlType == null) {
                    urlType = DialogStatisticsFragment.URL_TYPE_SEMESTER;
                }
                type = DialogStatisticsFragment.DIALOG_TYPE_STATISTICS;
                bundle.putString(DialogStatisticsFragment.EXTRA_URL_TYPE, urlType);
                if (mType == CountyClassDetailActivity.TYPE_MASTERCLASSROOM) {//主讲教室
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putInt(DialogStatisticsFragment.EXTRA_WEEKSEQ, mCurrentWeek);
                } else if (mType == CountyClassDetailActivity.TYPE_RECEIVEROOM) {//接收教室
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putString(DialogStatisticsFragment.EXTRA_DATATIME, mCurrentDate);
                } else {//主讲教师
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putString(DialogStatisticsFragment.EXTRA_BASEUSERID, mTeacherID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_DATATIME, mCurrentDate);
                }
                break;
        }
        DialogStatisticsFragment dialogStatisticsFragment = DialogStatisticsFragment.newInstance(type, mType, bundle);
        dialogStatisticsFragment.show(getSupportFragmentManager(), "dialog--");
    }

    class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<CountyDetialFilter.ClassroomListBean> mBeens;

        FilterAdapter(List<CountyDetialFilter.ClassroomListBean> beens) {
            mBeens = beens;
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
            textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(parent.getContext(), 40)));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CountyDetialFilter.ClassroomListBean bean = mBeens.get(viewType);
                    if (bean != null) {
                        bean.setCheck(true);
                        mReceiveClassRoomId = bean.getClassId();
                        mClassTV.setText(bean.getClassName());
                        notifyDataSetChanged();
                        mPopupWindowFilter.dismiss();
                        loadData();
                    }

                }
            });
            return new RecyclerView.ViewHolder(textView) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
            TextView textView = (TextView) holder.itemView;
            final CountyDetialFilter.ClassroomListBean bean = mBeens.get(position);
            if (bean != null) {
                if (position == 0) {
                    textView.setText(bean.getClassName());
                } else {
                    textView.setText(bean.getSchoolName() + "(" + bean.getClassName() + ")");
                }
                if (bean.getClassId() == null && mReceiveClassRoomId == null) {
                    textView.setTextColor(getResources().getColor(R.color.main_color));
                } else if (mReceiveClassRoomId != null && mReceiveClassRoomId.equals(bean.getClassId())) {
                    textView.setTextColor(getResources().getColor(R.color.main_color));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.busy_indicator));
                }
            }
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
            return mBeens == null ? 0 : mBeens.size();
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
            return new RecyclerView.ViewHolder(textView) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position, List<Object> payloads) {
            TextView textView = (TextView) holder.itemView;
            textView.setText("第" + (position + 1) + "周");
            if (mCountyDetialFilter != null) {
                if (position + 1 == mCurrentWeek) {
                    textView.setTextColor(getResources().getColor(R.color.main_color));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.busy_indicator));
                }
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeekTV.setText("第" + (position + 1) + "周");
                    mCurrentWeek = position + 1;
                    notifyDataSetChanged();
                    loadData();
                    mPopupWeek.dismiss();
                }
            });
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

    class GetClassDetail extends AsyncTask<JSONObject, Integer, CountyClassDetial> {

        @Override
        protected CountyClassDetial doInBackground(JSONObject... params) {
            CountyClassDetial countyClassDetial = null;
            try {
                countyClassDetial = new Gson().fromJson(params[0].toString(), CountyClassDetial.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                ToastUtil.showToast(CountyClassDetailActivity.this, "数据异常！");
            }
            if (countyClassDetial != null) {
                if (countyClassDetial.getWeekDateList() != null) {
                    List<TimeTableView2.Holiday> holidays = new ArrayList<>(countyClassDetial.getWeekDateList().size());
                    for (CountyClassDetial.WeekDateListBean bean : countyClassDetial.getWeekDateList()) {
                        TimeTableView2.Holiday holiday = new TimeTableView2.Holiday();
                        holiday.setmDate(bean.getDate());
                        holiday.setIsholiday(bean.isHoliday());
                        holidays.add(holiday);
                    }
                    countyClassDetial.setHolidays(holidays);
                }
                if (countyClassDetial.getDataList() != null && countyClassDetial.getDataList().size() > 0) {
                    List<TimeTableView2.TimeTable> tables = new ArrayList<>();
                    for (int i = 0; i < countyClassDetial.getDataList().size(); i++) {
                        CountyClassDetial.DataListBean bean1 = countyClassDetial.getDataList().get(i);
                        if (!bean1.isHas()) {
                            for (int j = i + 1; j < countyClassDetial.getDataList().size(); j++) {
                                CountyClassDetial.DataListBean bean2 = countyClassDetial.getDataList().get(j);
                                if (bean1.getDaySque() == bean2.getDaySque() && bean1.getClassSque() == bean2.getClassSque()) {
                                    bean2.setHas(true);
                                    bean1.setCount(bean1.getCount() + 1);
                                    bean2.setCount(bean1.getCount());
                                }
                            }
                            TimeTableView2.TimeTable timeTable = new TimeTableView2.TimeTable();
                            timeTable.setDaySeq(bean1.getDaySque());
                            timeTable.setClassSeq(bean1.getClassSque());
                            if ("PORGRESS".equals(bean1.getStatus())) {
                                timeTable.setProcessing(true);
                            }
                            if (bean1.getCount() > 0) {
                                timeTable.setClassName((bean1.getCount() + 1) + "节课");
                            } else {
                                timeTable.setClassName(bean1.getCourseName());
                            }
                            tables.add(timeTable);
                        }
                    }
                    countyClassDetial.setTimeTables(tables);
                }
            }
            return countyClassDetial;
        }

        @Override
        protected void onPostExecute(CountyClassDetial timetableDetail) {
            if (timetableDetail != null && mTimeTableView2 != null) {
                mCountyClassDetial = timetableDetail;
                setData();
                mTimeTableView2.setTimeTable(mCountyClassDetial.getTimeTables());
                mTimeTableView2.setWeekDate(mCountyClassDetial.getHolidays());
            }
        }
    }
}
