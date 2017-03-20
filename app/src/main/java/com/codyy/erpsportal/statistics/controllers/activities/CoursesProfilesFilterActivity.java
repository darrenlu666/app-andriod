package com.codyy.erpsportal.statistics.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.models.parsers.JsonParser.OnParsingListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.statistics.controllers.fragments.dialogs.DayPickerDialog;
import com.codyy.erpsportal.statistics.controllers.fragments.dialogs.DayPickerDialog.OnClickTimePicker;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
import com.codyy.erpsportal.statistics.models.entities.BaseEntity;
import com.codyy.erpsportal.statistics.models.entities.StatFilterCarrier;
import com.codyy.erpsportal.statistics.models.entities.TermEntity;
import com.codyy.erpsportal.statistics.widgets.OrderLayout;
import com.codyy.url.URLConfig;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 统计过滤
 */
public class CoursesProfilesFilterActivity extends AppCompatActivity {

    private final static String TAG = "CoursesProfilesFilterActivity";

    private final static String EXTRA_BY_SPECIFIC_DATE = "com.codyy.erpsportal.EXTRA_BY_SPECIFIC_DATE";

    private final static String EXTRA_BY_TERM = "com.codyy.erpsportal.EXTRA_BY_TERM";

    private final static String EXTRA_BY_SUBJECT = "com.codyy.erpsportal.EXTRA_BY_SUBJECT";

    private final static String EXTRA_TITLE = "com.codyy.erpsportal.ARG_EXTRA_TITLE";

    public final static String EXTRA_IN_FILTER = "com.codyy.erpsportal.EXTRA_IN_FILTER";

    public final static String EXTRA_OUT_FILTER = "com.codyy.erpsportal.EXTRA_OUT_FILTER";

    public final static int REQUEST_CODE = 1;

    public final static int BY_WEEK = 1;

    public final static int BY_MONTH = 2;

    public final static int BY_SPECIFIC_DATE = 3;

    public final static int BY_TERM = 4;

    /**
     * 按什么筛选
     */
    @FilterBy
    private int mFilterBy;

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    /* +按周统计相关+ */
    @Bind(R.id.rb_by_week)
    RadioButton mByWeekRb;

    @Bind(R.id.ib_previous_week)
    ImageButton mPreviousWeekIb;

    @Bind(R.id.tv_week_begin)
    TextView mWeekBeginTv;

    @Bind(R.id.tv_week_end)
    TextView mWeekEndTv;

    @Bind(R.id.ib_next_week)
    ImageButton mNextWeekIb;
    /* -按周统计相关- */

    /* +按月统计相关+ */
    @Bind(R.id.rb_by_month)
    RadioButton mByMonthRb;

    @Bind(R.id.ib_previous_month)
    ImageButton mPreviousMonthIb;

    @Bind(R.id.tv_month_begin)
    TextView mMonthBeginTv;

    @Bind(R.id.tv_month_end)
    TextView mMonthEndTv;

    @Bind(R.id.ib_next_month)
    ImageButton mNextMonthIb;
    /* -按月统计相关- */

    /* +按具体日期统计相关+ */
    @Bind(R.id.rb_by_specific_date)
    RadioButton mBySpecificDateRb;

    @Bind(R.id.btn_specific_date_begin)
    Button mSpecificDateBeginBtn;

    @Bind(R.id.btn_specific_date_end)
    Button mSpecificDateEndBtn;

    @Bind(R.id.ll_by_specific_date)
    LinearLayout mBySpecificDateLl;
    /* -按具体日期统计相关- */

    /* +学期选择相关+ */
    @Bind(R.id.rb_by_term)
    RadioButton mByTermRb;

    @Bind(R.id.gl_terms)
    GridLayout mTermsGl;
    /* -学期选择相关- */

    /* +学科选择相关+ */
    @Bind(R.id.tv_select_subject)
    TextView mSelectSubjectTv;

    @Bind(R.id.ol_subjects)
    OrderLayout mSubjectsOl;
    /* -学科选择相关- */

    @Bind(R.id.rg_filter_type)
    RadioGroup mFilterTypeRg;

    private LocalDate mWeekDate;

    private LocalDate mMonthDate;

    private UserInfo mUserInfo;

    private AreaInfo mAreaInfo;

    private String mTitle;

    /**
     * 是否需要显示按具体日期选择
     */
    private boolean mBySpecificDate;

    /**
     * 是否需要显示按学期选择
     */
    private boolean mByTerm;

    /**
     * 是否需要显示按学科选择
     */
    private boolean mBySubject;

    private int mTermItemPadding;

    private int mTermMargin;

    private int mSubjectItemPadding;

    /**
     * 原筛选参数
     */
    private StatFilterCarrier mFilterCarrier;

    private List<CheckBox> mTermCbList;

    private List<CheckBox> mSubjectCbList;

    private RequestSender mRequestSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_profiles_filter);
        ButterKnife.bind(this);
        initAttributes();
        initViews();
        initState();
        loadData();
        if (savedInstanceState != null) {
            resetDialogListener();
        }
    }

    private void initAttributes() {
        Intent intent = getIntent();
        mRequestSender = new RequestSender(this);
        mTermItemPadding = UIUtils.dip2px(this, 12);
        mTermMargin = UIUtils.dip2px(this, 4);
        mSubjectItemPadding = UIUtils.dip2px(this, 8);
        mUserInfo = intent.getParcelableExtra(Extra.USER_INFO);
        mBySpecificDate = intent.getBooleanExtra(EXTRA_BY_SPECIFIC_DATE, false);
        mByTerm = intent.getBooleanExtra(EXTRA_BY_TERM, false);
        mBySubject = intent.getBooleanExtra(EXTRA_BY_SUBJECT, false);
        mAreaInfo = intent.getParcelableExtra(Extra.AREA_INFO);
        if (mAreaInfo == null) {
            mAreaInfo = new AreaInfo(mUserInfo);
        }
        mTitle = intent.getStringExtra(EXTRA_TITLE);
        mFilterCarrier = intent.getParcelableExtra(EXTRA_IN_FILTER);
//        mInsetTermIndex = mFilterTypeRg.indexOfChild(mSelectSubjectTv);
    }

    private void initViews() {
        mTitleTv.setText( mTitle);
        initWeeks();
        initMonths();
        initSpecificDates();
        initTerms();
        initSubjects();
    }

    private void initState() {
        if (mFilterCarrier == null) {//默认筛选
            if (mByTerm) {
                setFilterByTerm();
            } else {
                setFilterByWeek();
            }
        } else {//二次筛选，原来筛选参数
            setFilterBy(mFilterCarrier.getFilterBy());
            switch(mFilterBy) {
                case BY_WEEK:
                    mWeekDate = LocalDate.parse(mFilterCarrier.getStartDate());
                    mWeekBeginTv.setText(mFilterCarrier.getStartDate());
                    mWeekEndTv.setText(mFilterCarrier.getEndDate());
                    break;
                case BY_MONTH:
                    mMonthDate = LocalDate.parse(mFilterCarrier.getStartDate());
                    mMonthBeginTv.setText(mFilterCarrier.getStartDate());
                    mMonthEndTv.setText(mFilterCarrier.getEndDate());
                    break;
                case BY_SPECIFIC_DATE:
                    mSpecificDateBeginBtn.setText(mFilterCarrier.getStartDate());
                    mSpecificDateEndBtn.setText(mFilterCarrier.getEndDate());
                    break;
                case BY_TERM:
                    break;
            }
        }
    }

    private void setFilterByTerm() {
        mFilterBy = BY_TERM;
        mByTermRb.setChecked(true);
    }

    private void setFilterBy(int filterBy) {
        mFilterBy = filterBy;
        switch(mFilterBy) {
            case BY_WEEK:
                mByWeekRb.setChecked(true);
                break;
            case BY_MONTH:
                mByMonthRb.setChecked(true);
                break;
            case BY_SPECIFIC_DATE:
                mBySpecificDateRb.setChecked(true);
                break;
            case BY_TERM:
                mByTermRb.setChecked(true);
                break;
        }
    }

    private void loadData() {
        if (mByTerm) {
            loadTerms();
        }
        if (mBySubject) {
            loadSubjects();
        }
    }

    /**
     * 加载学期
     */
    private void loadTerms() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        if (mAreaInfo.isArea()) {
            params.put("baseAreaId", mAreaInfo.getId());
        } else if (mAreaInfo.isSchool()) {
            params.put("clsSchoolId", mAreaInfo.getId());
        }
        mRequestSender.sendGetRequest(new RequestData(URLConfig.GET_TERMS, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadTerms response=", response);
                if ("success".equals(response.optString("result"))){
                    JSONArray termJa = response.optJSONArray("trimesters");
                    mTermCbList = new ArrayList<>(termJa.length());
                    final String formerTermId;
                    if (mFilterCarrier != null) {//上次选择的学期id
                        formerTermId = mFilterCarrier.getTermId();
                    } else {
                        formerTermId = null;
                    }
                    List<TermEntity> termEntities = TermEntity.PARSER.parseArrayAdditionally(
                            termJa, new OnParsingListener<TermEntity>() {
                        @Override
                        public void handleParsing(JSONObject jsonObject, TermEntity termEntity) {
                            CheckBox checkBox = createTermItem(termEntity);
                            if (!TextUtils.isEmpty(formerTermId)) {
                                if (formerTermId.equals(termEntity.getId()))
                                    checkBox.setChecked(true);
                            } else if (jsonObject.optInt("currentFlag") == 1) {
                                checkBox.setChecked(true);
                            }
                            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                            layoutParams.width = 0;
                            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                            layoutParams.topMargin = mTermMargin * 2;
                            layoutParams.leftMargin = mTermMargin;
                            layoutParams.rightMargin = mTermMargin;
                            mTermsGl.addView(checkBox, layoutParams);
                            mTermCbList.add(checkBox);
                            checkBox.setOnClickListener(mOnTermClickListener);
                        }
                    });
                    if (termEntities.size() % 2 == 1) {
                        View view = new View(CoursesProfilesFilterActivity.this);
                        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                        layoutParams.width = 0;
                        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                        mTermsGl.addView(view, layoutParams);
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "loadTerms error=", error);
                ToastUtil.showToast(CoursesProfilesFilterActivity.this, "获取学期失败！");
            }
        }));
    }

    private OnClickListener mOnTermClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setFilterByTerm();
            for (CheckBox checkBox: mTermCbList) {
                checkBox.setChecked(v.equals(checkBox));
            }
        }
    };

    /**
     * 创建学期组件项
     * @param termEntity 学期项数据
     * @return CheckBox组件
     */
    private CheckBox createTermItem(TermEntity termEntity) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(termEntity.getName());
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setTextColor(getResources().getColorStateList(R.color.sl_tv_stat_subject));
        checkBox.setButtonDrawable(null);
        checkBox.setPadding(mTermItemPadding, mTermItemPadding
                , mTermItemPadding, mTermItemPadding);
        checkBox.setBackgroundResource(R.drawable.bg_cb_statistics_filter_subject);
        checkBox.setTag(termEntity);
        return checkBox;
    }

    /**
     * 加载学科
     */
    private void loadSubjects() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        mRequestSender.sendGetRequest(new RequestData(URLConfig.GET_SUBJECTS, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadSubjects response=", response);
                boolean result = response.optBoolean("result");
                if (result) {
                    JSONArray subjectJa = response.optJSONArray("subjectList");
                    if (subjectJa != null) {
                        mSubjectCbList = new ArrayList<>(subjectJa.length() + 1);
                        addSubjectCbList( new BaseEntity("-1", "全部"));
                        List<BaseEntity> subjectList = new JsonParser<BaseEntity>() {
                            @Override
                            public BaseEntity parse(JSONObject jsonObject) {
                                BaseEntity baseEntity = new BaseEntity();
                                baseEntity.setId(jsonObject.optString("id"));
                                baseEntity.setName(jsonObject.optString("name"));

                                addSubjectCbList(baseEntity);
                                return baseEntity;
                            }
                        }.parseArray(subjectJa);
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "loadSubjects error=", error);
                ToastUtil.showToast(CoursesProfilesFilterActivity.this, "获取学科失败！");
            }
        }));
    }

    /**
     * 添加一个学科项
     * @param subjectEntity 学科项数据
     */
    private void addSubjectCbList(BaseEntity subjectEntity) {
        CheckBox subjectCb = createSubjectCb(subjectEntity);
        mSubjectsOl.addView(subjectCb);
        mSubjectCbList.add(subjectCb);
        subjectCb.setOnClickListener(mOnSubjectClickListener);
        if (mFilterCarrier != null && mFilterCarrier.getSubjectId() != null) {
            if (mFilterCarrier.getSubjectId().equals(subjectEntity.getId())) {
                subjectCb.setChecked(true);
            }
        }
    }

    private OnClickListener mOnSubjectClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            for (CheckBox checkBox: mSubjectCbList) {
                checkBox.setChecked(v.equals(checkBox));
            }
        }
    };

    private CheckBox createSubjectCb(BaseEntity subject) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setTag(subject);
        checkBox.setButtonDrawable(null);
        checkBox.setPadding(mSubjectItemPadding, mSubjectItemPadding,
                mSubjectItemPadding, mSubjectItemPadding);
        checkBox.setTextColor(getResources().getColorStateList(R.color.sl_tv_stat_subject));
        checkBox.setBackgroundResource(R.drawable.bg_tv_statistics_filter_subject);
        checkBox.setText(subject.getName());
        return checkBox;
    }

    @OnClick(R.id.btn_return)
    public void onReturnClick() {
        finish();
        addSlideDownExitAnim();
    }

    /**
     * 初始化按周统计
     */
    private void initWeeks() {
        mWeekDate = new LocalDate();
        updateWeekDates();
    }

    private void updateWeekDates() {
        mWeekBeginTv.setText(mWeekDate.withDayOfWeek(DateTimeConstants.MONDAY).toString());
        mWeekEndTv.setText(mWeekDate.withDayOfWeek(DateTimeConstants.SUNDAY).toString());
    }

    /**
     * 初始化按月统计
     */
    private void initMonths() {
        mMonthDate = new LocalDate();
        updateMonthDates();
    }

    private void updateMonthDates() {
        mMonthBeginTv.setText(mMonthDate.dayOfMonth().withMinimumValue().toString());
        mMonthEndTv.setText(mMonthDate.dayOfMonth().withMaximumValue().toString());
    }

    /**
     * 初始化按具体日期统计块日期
     */
    private void initSpecificDates() {
        if (mBySpecificDate) {
            mBySpecificDateRb.setVisibility(View.VISIBLE);
            mBySpecificDateLl.setVisibility(View.VISIBLE);
            LocalDate currDate = LocalDate.now();
            mSpecificDateEndBtn.setText(currDate.toString("yyyy-MM-dd"));
            //开始时间默认向前一个月
            mSpecificDateBeginBtn.setText(currDate.minusMonths(1).toString("yyyy-MM-dd"));
        }
    }

    private void initTerms() {
        if (mByTerm) {
            mByTermRb.setVisibility(View.VISIBLE);
            mTermsGl.setVisibility(View.VISIBLE);
        }
    }

    private void initSubjects() {
        if (mBySubject) {
            mSelectSubjectTv.setVisibility(View.VISIBLE);
            mSubjectsOl.setVisibility(View.VISIBLE);
        }
    }

    private void resetDialogListener() {
        DayPickerDialog beginPickerDialog = (DayPickerDialog) getSupportFragmentManager().findFragmentByTag("begin");
        if (beginPickerDialog != null) {
            beginPickerDialog.setOnConfirmListener( mOnBeginDateConfirmListener);
        }
        DayPickerDialog endPickerDialog = (DayPickerDialog) getSupportFragmentManager().findFragmentByTag("end");
        if (endPickerDialog != null) {
            endPickerDialog.setOnConfirmListener( mOnEndDateConfirmListener);
        }
    }

    @OnClick({R.id.ib_previous_week, R.id.ib_next_week, R.id.ib_previous_month, R.id.ib_next_month, R.id.btn_specific_date_begin, R.id.btn_specific_date_end })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_previous_week:
                setFilterByWeek();
                minusWeek();
                break;
            case R.id.ib_next_week:
                setFilterByWeek();
                plusWeek();
                break;
            case R.id.ib_previous_month:
                setFilterByMonth();
                minusMonth();
                break;
            case R.id.ib_next_month:
                setFilterByMonth();
                plusMonth();
                break;
            case R.id.btn_specific_date_begin: {
                String beginDateStr = mSpecificDateBeginBtn.getText().toString();
                String endDateStr = mSpecificDateEndBtn.getText().toString();
                LocalDate defaultDate = null;
                LocalDate endDate = null;
                if (!TextUtils.isEmpty(endDateStr)) {
                    endDate = parseToDate(endDateStr);
                }
                if (!TextUtils.isEmpty(beginDateStr)) {
                    defaultDate = parseToDate(beginDateStr);
                }
                DayPickerDialog beginDayPickerDialog = DayPickerDialog.newInstance(null, endDate, defaultDate);
                beginDayPickerDialog.setOnConfirmListener(mOnBeginDateConfirmListener);
                beginDayPickerDialog.show(getSupportFragmentManager(), "begin");
                break;
            }
            case R.id.btn_specific_date_end: {
                String beginDateStr = mSpecificDateBeginBtn.getText().toString();
                String endDateStr = mSpecificDateEndBtn.getText().toString();
                LocalDate defaultDate = null;
                LocalDate beginDate = null;
                if (!TextUtils.isEmpty(beginDateStr)) {
                    beginDate = parseToDate(beginDateStr);
                }
                if (!TextUtils.isEmpty(endDateStr)) {
                    defaultDate = parseToDate(endDateStr);
                }
                DayPickerDialog endDayPickerDialog = DayPickerDialog.newInstance(beginDate, LocalDate.now(), defaultDate);
                endDayPickerDialog.setOnConfirmListener(mOnEndDateConfirmListener);
                endDayPickerDialog.show(getSupportFragmentManager(), "end");
                break;
            }
        }
    }

    private void setFilterByWeek() {
        mFilterBy = BY_WEEK;
        mByWeekRb.setChecked(true);
    }

    private void setFilterByMonth() {
        mFilterBy = BY_MONTH;
        mByMonthRb.setChecked(true);
    }

    private OnClickTimePicker mOnBeginDateConfirmListener = new OnClickTimePicker() {
        @Override
        public void onConfirmClickListener(String time) {
            setFilterBySpecificDate();
            mSpecificDateBeginBtn.setText(time);
        }
    };

    private OnClickTimePicker mOnEndDateConfirmListener = new OnClickTimePicker() {
        @Override
        public void onConfirmClickListener(String time) {
            setFilterBySpecificDate();
            mSpecificDateEndBtn.setText(time);
        }
    };

    private void setFilterBySpecificDate() {
        mFilterBy = BY_SPECIFIC_DATE;
        mBySpecificDateRb.setChecked(true);
    }

    private LocalDate parseToDate(String str) {
        return LocalDate.parse(str, DateTimeFormat.forPattern("yyyy-MM-dd"));
    }

    /**
     * 减一周
     */
    private void minusWeek() {
        mWeekDate = mWeekDate.minusWeeks(1);
        updateWeekDates();
    }

    /**
     * 加一周
     */
    private void plusWeek() {
        mWeekDate = mWeekDate.plusWeeks(1);
        updateWeekDates();
    }

    /**
     * 减一个月
     */
    private void minusMonth() {
        mMonthDate = mMonthDate.minusMonths(1);
        updateMonthDates();
    }

    /**
     * 加一个月
     */
    private void plusMonth() {
        mMonthDate = mMonthDate.plusMonths(1);
        updateMonthDates();
    }

    @OnClick({R.id.rb_by_week, R.id.rb_by_month, R.id.rb_by_specific_date, R.id.rb_by_term})
    public void onRadioButtonClick(View view) {
        switch (view.getId()) {
            case R.id.rb_by_week:
                mFilterBy = BY_WEEK;
                break;
            case R.id.rb_by_month:
                mFilterBy = BY_MONTH;
                break;
            case R.id.rb_by_specific_date:
                mFilterBy = BY_SPECIFIC_DATE;
                break;
            case R.id.rb_by_term:
                mFilterBy = BY_TERM;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        addSlideDownExitAnim();
    }

    private void addSlideDownExitAnim() {
        overridePendingTransition(R.anim.layout_show, R.anim.slide_down_to_hide);
    }

    @OnClick({R.id.btn_confirm_filter})
    public void onConfirmFilter(View view) {
        Intent intent = new Intent();
        StatFilterCarrier statFilterCarrier = createFilterCarrier();
        intent.putExtra(EXTRA_OUT_FILTER, statFilterCarrier);
        setResult(RESULT_OK, intent);
        finish();
        addSlideDownExitAnim();
    }

    private StatFilterCarrier createFilterCarrier() {
        StatFilterCarrier statFilterCarrier = new StatFilterCarrier();
        statFilterCarrier.setFilterBy(mFilterBy);
        int checkedRbId = mFilterTypeRg.getCheckedRadioButtonId();
        if (mByWeekRb.getId() == checkedRbId) {
            statFilterCarrier.setStartDate(mWeekBeginTv.getText().toString());
            statFilterCarrier.setEndDate(mWeekEndTv.getText().toString());
        } else if (mByMonthRb.getId() == checkedRbId) {
            statFilterCarrier.setStartDate(mMonthBeginTv.getText().toString());
            statFilterCarrier.setEndDate(mMonthEndTv.getText().toString());
        } else if (mBySpecificDateRb.getId() == checkedRbId) {
            statFilterCarrier.setStartDate(mSpecificDateBeginBtn.getText().toString());
            statFilterCarrier.setEndDate(mSpecificDateEndBtn.getText().toString());
        } else if (checkedRbId == mByTermRb.getId()) {
            for (CheckBox termCb: mTermCbList) {
                if (termCb.isChecked()) {
                    TermEntity termEntity = (TermEntity) termCb.getTag();
                    statFilterCarrier.setTermId(termEntity.getId());
                }
            }
        }
        if (mBySubject && mSubjectCbList != null) {
            for (CheckBox subjectCb: mSubjectCbList){
                if (subjectCb.isChecked()) {
                    BaseEntity subject = (BaseEntity) subjectCb.getTag();
                    statFilterCarrier.setSubjectId(subject.getId());
                    break;
                }
            }
        }
        return statFilterCarrier;
    }

    @IntDef({BY_WEEK, BY_MONTH, BY_SPECIFIC_DATE, BY_TERM})
    public @interface FilterBy{}

    /**
     * 学科统计筛选
     * @param activity
     * @param userInfo
     * @param title
     */
    public static void startSubjectFilter(Activity activity, UserInfo userInfo, String title) {
        startSubjectFilter(activity, userInfo, title, null);
    }

    public static void startSubjectFilter(Activity activity, UserInfo userInfo, String title,
                                          StatFilterCarrier statFilterCarrier) {
        Intent intent = new Intent(activity, CoursesProfilesFilterActivity.class);
        intent.putExtra(EXTRA_BY_SPECIFIC_DATE, true);
        if (!userInfo.isSchool()) {
            intent.putExtra(EXTRA_BY_SUBJECT, true);
        }
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_TITLE, title);
        if (statFilterCarrier != null) {
            intent.putExtra(EXTRA_IN_FILTER, statFilterCarrier);
        }
        activity.startActivityForResult(intent, REQUEST_CODE);
        activity.overridePendingTransition(R.anim.slide_up_to_show, R.anim.layout_hide);
    }

    public static void startProfileFilter(Activity activity, UserInfo userInfo, String title) {
        startProfileFilter(activity, userInfo, title, null);
    }

    /**
     * 统计概况筛选
     * @param activity
     * @param userInfo
     * @param title
     * @param statFilterCarrier 筛选参数
     */
    public static void startProfileFilter(Activity activity, UserInfo userInfo, String title,
                                          StatFilterCarrier statFilterCarrier) {
        Intent intent = new Intent(activity, CoursesProfilesFilterActivity.class);
        //国家查看省级：提供按周、按月、具体日期区间筛选
        //省市县校级查看：提供按周、按月、按学期筛选
        if (userInfo.isArea() && TextUtils.isEmpty(userInfo.getParentId())) {
            intent.putExtra(EXTRA_BY_SPECIFIC_DATE, true);
        } else {
            intent.putExtra(EXTRA_BY_TERM, true);
        }
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_TITLE, title);
        if (statFilterCarrier != null) {
            intent.putExtra(EXTRA_IN_FILTER, statFilterCarrier);
        }
        activity.startActivityForResult(intent, REQUEST_CODE);
        activity.overridePendingTransition(R.anim.slide_up_to_show, R.anim.layout_hide);
    }
}
