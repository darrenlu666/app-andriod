package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.TimeTableContent;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;
import com.codyy.erpsportal.commons.models.entities.my.TeacherClassParse;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TimeTable.TimeTableView2;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的课表
 * Created by kmdai on 2015/8/31.
 */
public class UserTimeTableActivity extends Activity {
    /**
     * 用户类别
     */
    public static final String USER_TYPE = "mUserType";
    /**
     * 用户id
     */
    public static final String USER_ID = "userId";
    /**
     * 课堂id
     */
    public static final String CLASS_ID = "classId";
    /**
     * 课堂title
     */
    public static final String CLASS_TITLE = "class_title";
    /**
     * 课堂类型
     */
    public static final String EXTRA_ROOM_TYPE = "classroom_type";
    /**
     * 教室类型
     */
    public static int ROOM_TYPE_CLASS = 0x001;
    /**
     * 获取课表
     */
    private final static int GET_TIMETABLE = 0x001;
    /**
     * 获取班级列表
     */
    private final static int GET_CLASS_LIST = 0x002;
    private RequestSender mSender;
    @Bind(R.id.user_timeable_layout_title)
    TextView mTitleText;
    @Bind(R.id.user_timeable_layout_timetableview)
    TimeTableView2 mTimeTableView;
    @Bind(R.id.user_timeable_layout_class)
    TextView mClassTV;
    RadioGroup mRadioGroup;
    /**
     * 选择的班级
     */
    private ClassCont mClassCont;


    private ArrayList<TimeTableContent> timeTables;

    private ArrayList<ClassCont> mClassConts;
    private PopupWindow mPopupWindow;
    private ListView mListView;
    private String mUserID;
    private String mUserType;
    private String mTitle;
    private int mRoomType;

    private String mClassID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_timetable);
        ButterKnife.bind(this);
        mUserID = getIntent().getStringExtra(USER_ID);
        mUserType = getIntent().getStringExtra(USER_TYPE);
        mClassID = getIntent().getStringExtra(CLASS_ID);
        mTitle = getIntent().getStringExtra(CLASS_TITLE);
        mRoomType = getIntent().getIntExtra(EXTRA_ROOM_TYPE, 0);
        init();
        if (mRoomType == ROOM_TYPE_CLASS) {
            getClassTimeTable(mClassID);
        } else {
            getData(null);
            switch (mUserType) {
                case UserInfo.USER_TYPE_TEACHER:
                    getClassList();
                    break;
                case UserInfo.USER_TYPE_STUDENT:
                case UserInfo.USER_TYPE_PARENT:
                    break;
            }
        }
    }

    /**
     * 初始化
     */
    private void init() {
        mSender = new RequestSender(this);
        timeTables = new ArrayList<>();
        mClassConts = new ArrayList<>();
        switch (mUserType) {
            case UserInfo.USER_TYPE_TEACHER:
                if (mRoomType == ROOM_TYPE_CLASS) {
                    mTitleText.setText(TextUtils.isEmpty(mTitle) ? Titles.sWorkspaceScheduleClass : mTitle);
                    mClassTV.setVisibility(View.GONE);
                    return;
                }
                View view = getLayoutInflater().inflate(R.layout.user_timetable_select, null);
                mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(this, 150));
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                mPopupWindow.setTouchable(true);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        Drawable drawable = getResources().getDrawable(R.drawable.pull_down_arrow);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                        mTitleText.setCompoundDrawables(null, null, drawable, null);
                        mClassTV.setCompoundDrawables(null, null, drawable, null);
                    }
                });
                mTitleText.setText(Titles.sWorkspaceSchedulePersonal);
                Drawable drawable = getResources().getDrawable(R.drawable.pull_down_arrow);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                mTitleText.setCompoundDrawables(null, null, drawable, null);
                mRadioGroup = (RadioGroup) view.findViewById(R.id.user_timetable_select_radio);
                RadioButton personalRB = (RadioButton) view.findViewById(R.id.user_timetable_select_radio_personal);
                personalRB.setText(Titles.sWorkspaceSchedulePersonal);
                RadioButton classRB = (RadioButton) view.findViewById(R.id.user_timetable_select_radio_class);
                classRB.setText(Titles.sWorkspaceScheduleClass);
                mRadioGroup.check(R.id.user_timetable_select_radio_personal);
                mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.user_timetable_select_radio_personal:
                                mTitleText.setText(Titles.sWorkspaceSchedulePersonal);
                                getData(null);
                                mClassTV.setVisibility(View.GONE);
                                mPopupWindow.dismiss();
                                break;
                            case R.id.user_timetable_select_radio_class:
                                mTitleText.setText(Titles.sWorkspaceScheduleClass);
                                if (mClassCont != null) {
                                    getData(mClassCont);
                                } else {
                                    if (mClassConts != null && mClassConts.size() >= 1) {
                                        mClassCont = mClassConts.get(0);
                                        getData(mClassCont);
                                    }
                                }
                                mClassTV.setText(mClassCont != null ? mClassCont.getClassLevelName() + mClassCont.getBaseClassName() : "");
                                mClassTV.setVisibility(View.VISIBLE);
                                mPopupWindow.dismiss();
                                break;
                        }
                    }
                });
                mListView = (ListView) view.findViewById(R.id.user_timetable_select_listview);
                mTitleText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Drawable drawable = getResources().getDrawable(R.drawable.title_triangle_small);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                        mTitleText.setCompoundDrawables(null, null, drawable, null);
                        mRadioGroup.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                        mPopupWindow.showAsDropDown(mTitleText);
                    }
                });
                mClassTV.setCompoundDrawables(null, null, drawable, null);
                mClassTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Drawable drawable = getResources().getDrawable(R.drawable.title_triangle_small);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                        mClassTV.setCompoundDrawables(null, null, drawable, null);
                        mRadioGroup.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        mPopupWindow.showAsDropDown(mTitleText);
                    }
                });
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ClassAdapter adapter = (ClassAdapter) mListView.getAdapter();
                        ClassCont classCont = adapter.getItem(position);
                        mClassCont = classCont;
                        if (classCont != null) {
                            mClassTV.setText(mClassCont != null ? mClassCont.getClassLevelName() + classCont.getBaseClassName() : "");
                            getData(classCont);
                            adapter.notifyDataSetChanged();
                        }
                        mPopupWindow.dismiss();
                    }
                });
                break;
            case UserInfo.USER_TYPE_STUDENT:
                mTitleText.setText(Titles.sWorkspaceSchedule);
                mClassTV.setVisibility(View.GONE);
                break;
            case UserInfo.USER_TYPE_PARENT:
                mTitleText.setText(Titles.sWorkspaceSchedule);
                mClassTV.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 获取数据
     *
     * @param classCont
     */
    private void getData(ClassCont classCont) {
        Map<String, String> data = new HashMap<>();
        if (!TextUtils.isEmpty(mUserID)) {
            data.put("userId", mUserID);
        }
        switch (mUserType) {
            case UserInfo.USER_TYPE_TEACHER:
                if (classCont == null) {
                    data.put("type", "Personal");
                } else {
                    data.put("type", "class");
                    data.put("baseClassId", classCont.getBaseClassId());
                }
                break;
            case UserInfo.USER_TYPE_STUDENT:
                data.put("baseClassId", mClassID);
                data.put("type", "class");
                break;
            case UserInfo.USER_TYPE_PARENT:
                data.put("baseClassId", mClassID);
                data.put("type", "class");
                break;
        }
        httpConnect(URLConfig.GET_TEACHER_TIMETABLE, data, GET_TIMETABLE);
    }

    /**
     * 获取班级课表数据
     *
     * @param classId
     */
    private void getClassTimeTable(String classId) {
        Map<String, String> data = new HashMap<>();
        data.put("baseClassId", mClassID);
        data.put("type", "class");
        httpConnect(URLConfig.GET_TEACHER_TIMETABLE, data, GET_TIMETABLE);
    }

    /**
     * 获取班级列表
     */
    private void getClassList() {
        Map<String, String> data = new HashMap<>();
        data.put("userId", mUserID);
        httpConnect(URLConfig.GET_TEACHER_CLASS_LIST, data, GET_CLASS_LIST);
    }

    /**
     * 网络连接
     *
     * @param url
     * @param data
     * @param msg
     */
    private void httpConnect(String url, Map<String, String> data, final int msg) {
        mSender.sendRequest(new RequestSender.RequestData(url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                switch (msg) {
                    case GET_TIMETABLE:
                        timeTables.clear();
                        TimeTableContent.getTimeTable(response, timeTables);
                        if (timeTables.size() <= 0) {
                            ToastUtil.showToast(UserTimeTableActivity.this, "暂无课程表信息");
                        }
                        mTimeTableView.setClass(timeTables);
                        break;
                    case GET_CLASS_LIST:
                        TeacherClassParse teacherClassParse = new Gson().fromJson(response.toString(), TeacherClassParse.class);
                        if (null != teacherClassParse && teacherClassParse.getDataList() != null) {
                            mClassConts = (ArrayList<ClassCont>) teacherClassParse.getDataList();
                        }
                        mListView.setAdapter(new ClassAdapter(mClassConts));
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                switch (msg) {
                    case GET_TIMETABLE:
                        Snackbar.make(mTimeTableView, "获取课程表失败！", Snackbar.LENGTH_SHORT).show();
                        break;
                    case GET_CLASS_LIST:
                        Snackbar.make(mTimeTableView, "获取教室列表失败！", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        }));
    }


    @OnClick(R.id.user_timetable_layout_btn_back)
    void back() {
        this.finish();
    }


    class ClassAdapter extends BaseAdapter {
        ArrayList<ClassCont> mClassConts;

        ClassAdapter(ArrayList<ClassCont> mClassConts) {
            this.mClassConts = mClassConts;
        }

        @Override
        public int getCount() {
            return mClassConts == null ? 0 : mClassConts.size();
        }

        @Override
        public ClassCont getItem(int position) {
            return mClassConts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(UserTimeTableActivity.this);
            textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(UserTimeTableActivity.this, 50)));
            textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            textView.setPadding(0, 0, UIUtils.dip2px(UserTimeTableActivity.this, 20), 0);
            textView.setSingleLine();
            textView.setText(mClassConts.get(position).getClassLevelName() + " " + mClassConts.get(position).getBaseClassName());
            textView.setTextSize(15);
            if (mClassCont.getBaseClassId().equals(mClassConts.get(position).getBaseClassId())) {
                textView.setTextColor(getResources().getColor(R.color.main_color));
            } else {
                textView.setTextColor(Color.parseColor("#707070"));
            }
            return textView;
        }
    }

    public static void start(Context from, UserInfo userInfo, String classId, String title, int type) {
        Intent intent = new Intent(from, UserTimeTableActivity.class);
        intent.putExtra(UserTimeTableActivity.USER_ID, userInfo.getBaseUserId());
        intent.putExtra(UserTimeTableActivity.USER_TYPE, userInfo.getUserType());
        intent.putExtra(UserTimeTableActivity.CLASS_ID, classId);
        intent.putExtra(CLASS_TITLE, title);
        intent.putExtra(EXTRA_ROOM_TYPE, type);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }
}
