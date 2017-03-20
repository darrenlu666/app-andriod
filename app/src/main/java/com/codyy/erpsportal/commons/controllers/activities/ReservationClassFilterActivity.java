package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.andexert.calendarlistview.library.DatePickerController;
import com.andexert.calendarlistview.library.DayPickerView;
import com.andexert.calendarlistview.library.SimpleMonthAdapter;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.ReservationClass;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 约课或课堂作业筛选
 * Created by kmdai on 2015/9/7.
 */
public class ReservationClassFilterActivity extends Activity implements DatePickerController, View.OnClickListener {
    /**
     * 约课
     */
    public static final int TYPE_RESERVATION = 0x001;
    /**
     * 作业
     */
    public static final int TYPE_HOMEWORK = 0x002;
    /**
     * 类型
     */
    public static final String TYPE_NAME = "type";

    public static final int RESULT_CODE = 0x001;
    public final static String STATE_SUBJECT = "subjectId";
    public final static String STATE_TEACHER = "speakerUserId";
    public final static String STATE_TIME_START = "startTime";
    public final static String STATE_TIME_END = "endTime";
    public final static String STATE_STATUS = "status";
    public final static String STATE_GRADE = "classLevelId";
    /**
     * 获取老师
     */
    private final static int GET_TEACHER = 0x001;
    /**
     * 获取学科
     */
    private final static int GET_SUBJECT = 0x002;
    /**
     * 获取年级
     */
    private final static int GET_GRADE = 0x003;
    private Dialog mDialogDate;
    private Dialog mDialogList;
    private DayPickerView mDayPickerView;
    @Bind(R.id.reservation_filter_text_time)
    TextView mTextDate;
    @Bind(R.id.reservation_filter_text_state)
    TextView mTextState;
    @Bind(R.id.reservation_filter_text_grade)
    TextView mTextGrade;
    @Bind(R.id.reservation_filter_text_subject)
    TextView mTextSubject;
    @Bind(R.id.reservation_filter_text_teacher)
    TextView mTextTeacher;
    @Bind(R.id.textView7)
    TextView mTextStateTitle;
    TextView mTextTitle;
    /**
     * 网络请求
     */
    private RequestSender mSender;
    private ReservationClass mReservationClass;
    private UserInfo mUserInfo;
    /**
     * 状态列表
     */
    private ListView mListView;
    private ArrayList<Listitem> mSubjects;
    private ArrayList<Listitem> mGrades;
    private ArrayList<Listitem> mStatus;
    private ArrayList<Listitem> mTeachers;
    private String[][] state = {{"全部", ""}, {"未开始", "INIT"}, {"进行中", "PROGRESS"}, {"已结束", "END"}};

    private String mSelectKey;
    private Bundle mData;
    private int mFilterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReservationClass = getIntent().getParcelableExtra("reservation");
        mData = getIntent().getExtras();
        mFilterType = getIntent().getIntExtra(TYPE_NAME, TYPE_RESERVATION);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        setContentView(R.layout.activity_reservation_filter);
        ButterKnife.bind(this);
        mSender = new RequestSender(this);
        init();
        getAllSubject(null);
        getAllGrade();
        switch (mFilterType) {
            case TYPE_HOMEWORK:
                mTextStateTitle.setVisibility(View.GONE);
                mTextState.setVisibility(View.GONE);
                getTeacherHomeWork();
                break;
            case TYPE_RESERVATION:
                getTeacherList();
                break;
        }
    }

    /**
     * 初始化
     */
    private void init() {
        if (mData == null) {
            mData = new Bundle();
        }
        mSubjects = new ArrayList<>();
        mGrades = new ArrayList<>();
        mStatus = new ArrayList<>();
        mTeachers = new ArrayList<>();
        mDialogDate = new Dialog(this, R.style.input_dialog);
        mDialogDate.setContentView(R.layout.dialog_reservation_filter);
        mDayPickerView = (DayPickerView) mDialogDate.findViewById(R.id.reservation_pickerView);
        mDialogDate.findViewById(R.id.dialog_reservation_filter_cancel).setOnClickListener(this);
        mDialogDate.findViewById(R.id.dialog_reservation_filter_sure).setOnClickListener(this);
        mDayPickerView.setController(this);
        Window window = mDialogDate.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = UIUtils.dip2px(this, 280);
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_filter_animation);

        mDialogList = new Dialog(this, R.style.input_dialog);
        mDialogList.setContentView(R.layout.dialog_reservation_filter2);
        mListView = (ListView) mDialogList.findViewById(R.id.dialog_reservation_filter2_listview);
        mDialogList.findViewById(R.id.dialog_reservation_filter2_cancel).setOnClickListener(this);
        mDialogList.findViewById(R.id.dialog_reservation_filter2_sure).setOnClickListener(this);
        mTextTitle = (TextView) mDialogList.findViewById(R.id.dialog_reservation_title);
        Window window1 = mDialogList.getWindow();
        WindowManager.LayoutParams lp1 = window1.getAttributes();
        lp1.copyFrom(window1.getAttributes());
        lp1.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp1.height = UIUtils.dip2px(this, 280);
        window1.setAttributes(lp);
        window1.setGravity(Gravity.BOTTOM);
        window1.setWindowAnimations(R.style.dialog_filter_animation);
        for (int i = 0; i < state.length; i++) {
            Listitem listitem = new Listitem();
            listitem.setName(state[i][0]);
            listitem.setId(state[i][1]);
            mStatus.add(listitem);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogAdapter adapter = (DialogAdapter) parent.getAdapter();
                Listitem listitem = adapter.getItem(position);
                if (mSelectKey.equals(STATE_GRADE)) {
                    mTextGrade.setText(listitem.getName());
                    mData.putString(STATE_GRADE, listitem.getId());
                    getAllSubject(listitem.getId());
                    mTextSubject.setText("");
                    mData.putString(STATE_STATUS, "");
                    mTextTeacher.setText("");
                    mData.putString(STATE_TEACHER, "");
                    switch (mFilterType) {
                        case TYPE_HOMEWORK:
                            getTeacherHomeWork();
                            break;
                        case TYPE_RESERVATION:
                            getTeacherList();
                            break;
                    }
                } else if (mSelectKey.equals(STATE_STATUS)) {
                    mTextState.setText(listitem.getName());
                    mData.putString(STATE_STATUS, listitem.getId());
                } else if (mSelectKey.equals(STATE_SUBJECT)) {
                    mTextSubject.setText(listitem.getName());
                    mData.putString(STATE_SUBJECT, listitem.getId());
                    mTextTeacher.setText("");
                    mData.putString(STATE_TEACHER, "");
                    switch (mFilterType) {
                        case TYPE_HOMEWORK:
                            getTeacherHomeWork();
                            break;
                        case TYPE_RESERVATION:
                            getTeacherList();
                            break;
                    }
                } else if (mSelectKey.equals(STATE_TEACHER)) {
                    mTextTeacher.setText(listitem.getName());
                    mData.putString(STATE_TEACHER, listitem.getId());
                }
                mDialogList.cancel();
            }
        });

        if (mData != null) {
            if (!"".equals(mData.getString(STATE_TIME_START, ""))) {
                mTextDate.setText(mData.getString(STATE_TIME_START) + "/" + mData.getString(STATE_TIME_END));
            }
            for (Listitem listitem : mStatus) {
                if (listitem.getId().equals(mData.getString(STATE_STATUS))) {
                    mTextState.setText(listitem.getName());
                    break;
                }
            }
        }
    }

    private void getTeacherHomeWork() {
        HashMap<String, String> data = new HashMap<>();
        if ("SCHOOL_USR".equals(mUserInfo.getUserType())) {
            data.put("schoolId", mUserInfo.getSchoolId());
        }
        data.put("classlevelId", mData.getString(STATE_GRADE, ""));
        data.put("subjectId", mData.getString(STATE_SUBJECT, ""));
        httpConnect(URLConfig.GET_HOMEWORK_TEACHERLIST, data, GET_TEACHER);
    }

    /**
     * 获取所有学科
     */
    private void getAllSubject(String classlevelId) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        if (classlevelId != null) {
            data.put("classlevelId", classlevelId);
        }
        httpConnect(URLConfig.GET_ALL_SUBJECT, data, GET_SUBJECT);
    }

    /**
     * 获取老师列表
     */
    private void getTeacherList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        data.put("baseClasslevelId", mData.getString(STATE_GRADE, ""));
        data.put("baseSubjectId", mData.getString(STATE_SUBJECT, ""));
        data.put("classroomId", mReservationClass.getClsClassroomId());
        httpConnect(URLConfig.GET_TEACHER_LIST, data, GET_TEACHER);
    }

    /**
     * 获取所有年级
     */
    private void getAllGrade() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        if (mReservationClass == null && mUserInfo != null) {
            data.put("schoolId", mUserInfo.getSchoolId());
        } else {
            data.put("schoolId", mReservationClass.getClsSchoolId());
        }
        httpConnect(URLConfig.GET_ALL_GRADE, data, GET_GRADE);
    }

    /**
     * 选择日期
     */
    @OnClick(R.id.reservation_filter_text_time)
    void selectDate() {
        mDialogDate.show();
        Calendar calendar = Calendar.getInstance();
        int mont = calendar.get(Calendar.MONTH);
        int a = (((calendar.get(Calendar.YEAR)) - 2007) * SimpleMonthAdapter.MONTHS_IN_YEAR) + mont;
        mDayPickerView.scrollToPosition(a);
    }

    /**
     * 选择状态
     */
    @OnClick(R.id.reservation_filter_text_state)
    void selectState() {
        mSelectKey = STATE_STATUS;
        mDialogList.show();
        mListView.setAdapter(new DialogAdapter(mStatus));
        mTextTitle.setText("状态");
    }

    /**
     * 选择年级
     */
    @OnClick(R.id.reservation_filter_text_grade)
    void selectGrade() {
        mSelectKey = STATE_GRADE;
        mDialogList.show();
        mListView.setAdapter(new DialogAdapter(mGrades));
        mTextTitle.setText("年级");
    }

    /**
     * 选择学科
     */
    @OnClick(R.id.reservation_filter_text_subject)
    void selectSubject() {
        mSelectKey = STATE_SUBJECT;
        mDialogList.show();
        mListView.setAdapter(new DialogAdapter(mSubjects));
        mTextTitle.setText("学科");
    }

    /**
     * 选择老师
     */
    @OnClick(R.id.reservation_filter_text_teacher)
    void selectTeacher() {
        mSelectKey = STATE_TEACHER;
        mDialogList.show();
        mListView.setAdapter(new DialogAdapter(mTeachers));
        mTextTitle.setText(Titles.sMasterTeacher);
    }

    @OnClick(R.id.reservation_filter_btn_sure)
    void sureFilter() {
        Intent data = new Intent();
        data.putExtra("data", mData);
        setResult(RESULT_CODE, data);
        this.finish();
    }

    @Override
    public int getMaxYear() {
        Time time = new Time("GMT+8");
        time.setToNow();
        return 2200;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {

    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {
        SimpleMonthAdapter.CalendarDay first;
        SimpleMonthAdapter.CalendarDay last;
        if (selectedDays.getFirst().getYear() < selectedDays.getLast().getYear()) {
            first = selectedDays.getFirst();
            last = selectedDays.getLast();
        } else if (selectedDays.getFirst().getMonth() < selectedDays.getLast().getMonth()) {
            first = selectedDays.getFirst();
            last = selectedDays.getLast();
        } else if (selectedDays.getFirst().getDay() < selectedDays.getLast().getDay()) {
            first = selectedDays.getFirst();
            last = selectedDays.getLast();
        } else {
            last = selectedDays.getFirst();
            first = selectedDays.getLast();
        }
        String startTime = first.getYear() + "-" + (first.getMonth() + 1) + "-" + first.getDay();
        String endTime = last.getYear() + "-" + (last.getMonth() + 1) + "-" + last.getDay();
        if (first.getDate().getTime() > last.getDate().getTime()) {
            mData.putString(STATE_TIME_END, startTime);
            mData.putString(STATE_TIME_START, endTime);
            mTextDate.setText(endTime + "/" + startTime);
        } else {
            mData.putString(STATE_TIME_END, endTime);
            mData.putString(STATE_TIME_START, startTime);
            mTextDate.setText(startTime + "/" + endTime);
        }
    }

    /**
     * 网络请求
     *
     * @param url
     * @param data
     * @param msg
     */
    private void httpConnect(String url, HashMap<String, String> data, final int msg) {
        mSender.sendRequest(new RequestSender.RequestData(url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                switch (msg) {
                    case GET_SUBJECT:
                        if ("success".equals(response.optString("result"))) {
                            mSubjects.clear();
                            JSONArray array = response.optJSONArray("list");
                            Listitem all = new Listitem();
                            all.setId("");
                            all.setName("全部");
                            mSubjects.add(all);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.optJSONObject(i);
                                Listitem listitem = new Listitem();
                                listitem.setId(object.optString("subjectId"));
                                listitem.setName(object.optString("subjectName"));
                                mSubjects.add(listitem);
                            }
                            if (mData != null) {
                                for (Listitem listitem : mSubjects) {
                                    if (listitem.getId().equals(mData.getString(STATE_SUBJECT))) {
                                        mTextSubject.setText(listitem.getName());
                                        break;
                                    }
                                }
                            }
                            if (mListView.getAdapter() != null) {
                                ((DialogAdapter) mListView.getAdapter()).notifyDataSetChanged();
                            }
                        }
                        break;
                    case GET_GRADE:
                        if ("success".equals(response.optString("result"))) {
                            mGrades.clear();
                            JSONArray array = response.optJSONArray("list");
                            Listitem all = new Listitem();
                            all.setId("");
                            all.setName("全部");
                            mGrades.add(all);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.optJSONObject(i);
                                Listitem listitem = new Listitem();
                                listitem.setId(object.optString("classLevelId"));
                                listitem.setName(object.optString("className"));
                                mGrades.add(listitem);
                            }
                            if (mData != null) {
                                if (mData != null) {
                                    for (Listitem listitem : mGrades) {
                                        if (listitem.getId().equals(mData.getString(STATE_GRADE))) {
                                            mTextGrade.setText(listitem.getName());
                                            break;
                                        }
                                    }
                                }
                            }
                            if (mListView.getAdapter() != null) {
                                ((DialogAdapter) mListView.getAdapter()).notifyDataSetChanged();
                            }
                        }
                        break;
                    case GET_TEACHER:
                        switch (mFilterType) {
                            case TYPE_HOMEWORK:
                                if ("success".equals(response.optString("result"))) {
                                    mTeachers.clear();
                                    JSONArray array = response.optJSONArray("teacherlist");
                                    Listitem all = new Listitem();
                                    all.setId("");
                                    all.setName("全部");
                                    mTeachers.add(all);
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.optJSONObject(i);
                                        Listitem listitem = new Listitem();
                                        listitem.setId(object.optString("id"));
                                        listitem.setName(object.optString("name"));
                                        mTeachers.add(listitem);
                                    }
                                    if (mData != null) {
                                        if (mData != null) {
                                            for (Listitem listitem : mTeachers) {
                                                if (listitem.getId().equals(mData.getString(STATE_TEACHER))) {
                                                    mTextTeacher.setText(listitem.getName());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (mListView.getAdapter() != null) {
                                        ((DialogAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                    }
                                }
                                break;
                            case TYPE_RESERVATION:
                                if ("success".equals(response.optString("result"))) {
                                    mTeachers.clear();
                                    JSONArray array = response.optJSONArray("teacherList");
                                    Listitem all = new Listitem();
                                    all.setId("");
                                    all.setName("全部");
                                    mTeachers.add(all);
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.optJSONObject(i);
                                        Listitem listitem = new Listitem();
                                        listitem.setId(object.optString("baseUserId"));
                                        listitem.setName(object.optString("realName"));
                                        mTeachers.add(listitem);
                                    }
                                    if (mData != null) {
                                        if (mData != null) {
                                            for (Listitem listitem : mTeachers) {
                                                if (listitem.getId().equals(mData.getString(STATE_TEACHER))) {
                                                    mTextTeacher.setText(listitem.getName());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (mListView.getAdapter() != null) {
                                        ((DialogAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                    }
                                }
                                break;
                        }

                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {

            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_reservation_filter_cancel:
                mDialogDate.cancel();
                mTextDate.setText("");
                mData.remove(STATE_TIME_START);
                mData.remove(STATE_TIME_END);
                break;
            case R.id.dialog_reservation_filter_sure:
                mDialogDate.cancel();
                break;
            case R.id.dialog_reservation_filter2_cancel:
                mDialogList.cancel();
                break;
            case R.id.dialog_reservation_filter2_sure:
                mDialogList.cancel();
                break;
        }
    }


    class DialogAdapter extends BaseAdapter {
        ArrayList<Listitem> listitems;

        DialogAdapter(ArrayList<Listitem> listitems) {
            this.listitems = listitems;
        }

        @Override
        public int getCount() {
            return listitems.size();
        }

        @Override
        public Listitem getItem(int position) {
            return listitems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(ReservationClassFilterActivity.this);
            textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(ReservationClassFilterActivity.this, 50)));
            textView.setText(listitems.get(position).getName());
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            if (mSelectKey.equals(STATE_GRADE) && listitems.get(position).getId().equals(mData.getString(STATE_GRADE, "-1"))) {
                textView.setTextColor(getResources().getColor(R.color.main_color));
            } else if (mSelectKey.equals(STATE_STATUS) && listitems.get(position).getId().equals(mData.getString(STATE_STATUS, "-1"))) {
                textView.setTextColor(getResources().getColor(R.color.main_color));
            } else if (mSelectKey.equals(STATE_SUBJECT) && listitems.get(position).getId().equals(mData.getString(STATE_SUBJECT, "-1"))) {
                textView.setTextColor(getResources().getColor(R.color.main_color));
            } else if (mSelectKey.equals(STATE_TEACHER) && listitems.get(position).getId().equals(mData.getString(STATE_TEACHER, "-1"))) {
                textView.setTextColor(getResources().getColor(R.color.main_color));
            } else {
                textView.setTextColor(getResources().getColor(R.color.fistpageclass_gridview_item_textcolor));
            }
            return textView;
        }
    }

    public class Listitem {
        String name;
        String id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
