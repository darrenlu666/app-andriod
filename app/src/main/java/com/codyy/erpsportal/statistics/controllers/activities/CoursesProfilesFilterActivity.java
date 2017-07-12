package com.codyy.erpsportal.statistics.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.models.parsers.JsonParser.OnParsingListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.statistics.controllers.fragments.dialogs.DayPickerDialog;
import com.codyy.erpsportal.statistics.controllers.fragments.dialogs.DayPickerDialog.OnClickTimePicker;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
import com.codyy.erpsportal.statistics.models.entities.StatFilterBy;
import com.codyy.erpsportal.statistics.models.entities.StatFilterCarrier;
import com.codyy.erpsportal.statistics.models.entities.TermEntity;
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

    private final static String EXTRA_BY_TERM = "com.codyy.erpsportal.EXTRA_BY_TERM";

    private final static String EXTRA_TITLE = "com.codyy.erpsportal.ARG_EXTRA_TITLE";

    public final static String EXTRA_IN_FILTER = "com.codyy.erpsportal.EXTRA_IN_FILTER";

    public final static String EXTRA_OUT_FILTER = "com.codyy.erpsportal.EXTRA_OUT_FILTER";

    public final static String EXTRA_SPECIFIC_DATE = "com.codyy.erpsportal.EXTRA_SPECIFIC_DATE";

    public final static int REQUEST_CODE = 1;

    /**
     * 按什么筛选
     */
    @StatFilterBy
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

    @Bind(R.id.rg_filter_type)
    RadioGroup mFilterTypeRg;

    private LocalDate mWeekDate;

    private LocalDate mMonthDate;

    private UserInfo mUserInfo;

    private AreaInfo mAreaInfo;

    private String mTitle;

    /**
     * 是否需要显示按学期选择
     */
    private boolean mByTerm;

    /**
     * 按特定日期 0.未加载、1.加载了需要特定日期、2.加载了不要特定日期
     */
    private int mSpecificDate;

    private int mTermItemPadding;

    private int mTermMargin;

    /**
     * 原筛选参数
     */
    private StatFilterCarrier mFilterCarrier;

    private List<CheckBox> mTermCbList;

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
        mUserInfo = intent.getParcelableExtra(Extra.USER_INFO);
        mByTerm = intent.getBooleanExtra(EXTRA_BY_TERM, false);
        mAreaInfo = intent.getParcelableExtra(Extra.AREA_INFO);
        if (mAreaInfo == null) {
            mAreaInfo = new AreaInfo(mUserInfo);
        }
        mTitle = intent.getStringExtra(EXTRA_TITLE);
        mFilterCarrier = intent.getParcelableExtra(EXTRA_IN_FILTER);
        mSpecificDate = intent.getIntExtra(EXTRA_SPECIFIC_DATE, 0);
    }

    private void initViews() {
        mTitleTv.setText( mTitle);
        initWeeks();
        initMonths();
        initDates();
        initTerms();
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
                case StatFilterBy.BY_WEEK:
                    mWeekDate = LocalDate.parse(mFilterCarrier.getStartDate());
                    mWeekBeginTv.setText(mFilterCarrier.getStartDate());
                    mWeekEndTv.setText(mFilterCarrier.getEndDate());
                    break;
                case StatFilterBy.BY_MONTH:
                    mMonthDate = LocalDate.parse(mFilterCarrier.getStartDate());
                    mMonthBeginTv.setText(mFilterCarrier.getStartDate());
                    mMonthEndTv.setText(mFilterCarrier.getEndDate());
                    break;
                case StatFilterBy.BY_SPECIFIC_DATE:
                    mSpecificDateBeginBtn.setText(mFilterCarrier.getStartDate());
                    mSpecificDateEndBtn.setText(mFilterCarrier.getEndDate());
                    break;
                case StatFilterBy.BY_TERM:
                    break;
            }
        }
    }

    private void setFilterByTerm() {
        mFilterBy = StatFilterBy.BY_TERM;
        mByTermRb.setChecked(true);
    }

    private void setFilterBy(int filterBy) {
        mFilterBy = filterBy;
        switch(mFilterBy) {
            case StatFilterBy.BY_WEEK:
                mByWeekRb.setChecked(true);
                break;
            case StatFilterBy.BY_MONTH:
                mByMonthRb.setChecked(true);
                break;
            case StatFilterBy.BY_SPECIFIC_DATE:
                mBySpecificDateRb.setChecked(true);
                break;
            case StatFilterBy.BY_TERM:
                mByTermRb.setChecked(true);
                break;
        }
    }

    private void loadData() {
        if (mByTerm) {
            loadTerms();
        }
        if (mSpecificDate == 0) {//首次进入此页面未加载
            loadFilterConfiguration();
        } else if (mSpecificDate == 1) {//已经知道要显示特定日期筛选
            showSpecificDates();
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
                            if (!TextUtils.isEmpty(formerTermId) && !"-1".equals(formerTermId)) {
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
     * 加载是否显示指定日期筛选
     */
    private void loadFilterConfiguration() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        mRequestSender.sendGetRequest(new RequestData(URLConfig.IS_CUSTOMIZED, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadSpecificDateNeeded response=", response);
                if (response.optBoolean("result", false)
                        && response.optBoolean("isCustomized", false)) {
                    mSpecificDate = 1;
                    showSpecificDates();
                } else {
                    mSpecificDate = 2;//没有配置按日期筛选
                    if (mBySpecificDateRb.isChecked()) {//没有配置按日期筛选时选中按日期筛选没有意义，按周吧
                        mFilterTypeRg.check(mByWeekRb.getId());
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                error.printStackTrace();
                mSpecificDate = 0;
            }
        }));
    }

    @OnClick(R.id.btn_return)
    public void onReturnClick() {
        setCancelResult();
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
     * 初始化按日期统计
     */
    private void initDates() {
        LocalDate currDate = LocalDate.now();
        //开始时间默认也是当天
        mSpecificDateBeginBtn.setText(currDate.toString("yyyy-MM-dd"));
        mSpecificDateEndBtn.setText(currDate.toString("yyyy-MM-dd"));
    }

    /**
     * 初始化按具体日期统计块日期
     */
    private void showSpecificDates() {
        mBySpecificDateRb.setVisibility(View.VISIBLE);
        mBySpecificDateLl.setVisibility(View.VISIBLE);
    }

    private void initTerms() {
        if (mByTerm) {
            mByTermRb.setVisibility(View.VISIBLE);
            mTermsGl.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 重新设置日期选择弹框回调
     */
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

    private long mLastTime;

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
                if (!mBySpecificDateRb.isChecked()) return;
                long currTime = System.currentTimeMillis();
                if (currTime - mLastTime > 500L) {
                    String beginDateStr = mSpecificDateBeginBtn.getText().toString();
                    LocalDate defaultDate = null;
                    if (!TextUtils.isEmpty(beginDateStr)) {
                        defaultDate = parseToDate(beginDateStr);
                    }
                    DayPickerDialog beginDayPickerDialog = DayPickerDialog.newInstance(null, null, defaultDate);
                    beginDayPickerDialog.setOnConfirmListener(mOnBeginDateConfirmListener);
                    beginDayPickerDialog.show(getSupportFragmentManager(), "begin");
                }
                mLastTime = currTime;
                break;
            }
            case R.id.btn_specific_date_end: {
                if (!mBySpecificDateRb.isChecked()) return;
                long currTime = System.currentTimeMillis();
                if (currTime - mLastTime > 500L) {
                    String endDateStr = mSpecificDateEndBtn.getText().toString();
                    LocalDate defaultDate = null;
                    if (!TextUtils.isEmpty(endDateStr)) {
                        defaultDate = parseToDate(endDateStr);
                    }
                    DayPickerDialog endDayPickerDialog = DayPickerDialog.newInstance(null, null, defaultDate);
                    endDayPickerDialog.setOnConfirmListener(mOnEndDateConfirmListener);
                    endDayPickerDialog.show(getSupportFragmentManager(), "end");
                }
                mLastTime = currTime;
                break;
            }
        }
    }

    private void setFilterByWeek() {
        mFilterBy = StatFilterBy.BY_WEEK;
        mByWeekRb.setChecked(true);
    }

    private void setFilterByMonth() {
        mFilterBy = StatFilterBy.BY_MONTH;
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
        mFilterBy = StatFilterBy.BY_SPECIFIC_DATE;
        mBySpecificDateRb.setChecked(true);
    }

    /**
     * 字符串转为LocalDate
     * @param str
     * @return
     */
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
                mFilterBy = StatFilterBy.BY_WEEK;
                break;
            case R.id.rb_by_month:
                mFilterBy = StatFilterBy.BY_MONTH;
                break;
            case R.id.rb_by_specific_date:
                mFilterBy = StatFilterBy.BY_SPECIFIC_DATE;
                break;
            case R.id.rb_by_term:
                mFilterBy = StatFilterBy.BY_TERM;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setCancelResult();
        super.onBackPressed();
        addSlideDownExitAnim();
    }

    /**
     * 设置取消结果
     */
    private void setCancelResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SPECIFIC_DATE, mSpecificDate);
        setResult(RESULT_CANCELED, intent);
    }

    /**
     * 添加下滑退出动画
     */
    private void addSlideDownExitAnim() {
        overridePendingTransition(R.anim.layout_show, R.anim.slide_down_to_hide);
    }

    @OnClick({R.id.btn_confirm_filter})
    public void onConfirmFilter(View view) {
        if (mBySpecificDateRb.getId() == mFilterTypeRg.getCheckedRadioButtonId()) {
            LocalDate specificDateBeginDate = parseToDate(mSpecificDateBeginBtn.getText().toString());
            LocalDate specificDateEndDate = parseToDate(mSpecificDateEndBtn.getText().toString());
            if (specificDateBeginDate.isAfter(specificDateEndDate)) {
                ToastUtil.showToast(this, "结束日期不能早于开始日期");
                return;
            }
        }
        Intent intent = new Intent();
        StatFilterCarrier statFilterCarrier = createFilterCarrier();
        intent.putExtra(EXTRA_OUT_FILTER, statFilterCarrier);
        intent.putExtra(EXTRA_SPECIFIC_DATE, mSpecificDate);
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
            statFilterCarrier.setStartDate( mSpecificDateBeginBtn.getText().toString());
            statFilterCarrier.setEndDate( mSpecificDateEndBtn.getText().toString());
        } else if (checkedRbId == mByTermRb.getId()) {
            for (CheckBox termCb: mTermCbList) {
                if (termCb.isChecked()) {
                    TermEntity termEntity = (TermEntity) termCb.getTag();
                    statFilterCarrier.setTermId(termEntity.getId());
                }
            }
        }
        return statFilterCarrier;
    }

    /**
     * 提供按周、按月、按日、按学期筛选
     * @param activity
     * @param userInfo
     * @param title
     * @param statFilterCarrier 筛选参数
     */
    public static void startFilter(Activity activity, UserInfo userInfo, String title,
                                   StatFilterCarrier statFilterCarrier, int specificDate) {
        Intent intent = new Intent(activity, CoursesProfilesFilterActivity.class);
        if (!userInfo.isArea() || !TextUtils.isEmpty(userInfo.getParentId())) {
            intent.putExtra(EXTRA_BY_TERM, true);
        }
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_TITLE, title);
        if (statFilterCarrier != null) {
            intent.putExtra(EXTRA_IN_FILTER, statFilterCarrier);
        }
        intent.putExtra(EXTRA_SPECIFIC_DATE, specificDate);
        activity.startActivityForResult(intent, REQUEST_CODE);
        activity.overridePendingTransition(R.anim.slide_up_to_show, R.anim.layout_hide);
    }
}
