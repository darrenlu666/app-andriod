package com.codyy.erpsportal.statistics.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog.OnCancelListener;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.CenterLongTextView;
import com.codyy.erpsportal.statistics.controllers.fragments.StatTableFragment;
import com.codyy.erpsportal.statistics.controllers.fragments.StatTableFragment.OnRowClickListener;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
import com.codyy.erpsportal.statistics.models.entities.CourseProfile;
import com.codyy.erpsportal.statistics.models.entities.CourseProfilesResult;
import com.codyy.erpsportal.statistics.models.entities.StatFilterBy;
import com.codyy.erpsportal.statistics.models.entities.StatFilterCarrier;
import com.codyy.erpsportal.statistics.models.entities.StatRow;
import com.codyy.erpsportal.statistics.models.entities.StatTableModel;
import com.codyy.url.URLConfig;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 开课概况
 */
public class ClassStatTableActivity extends AppCompatActivity implements OnRowClickListener {

    private final static String TAG = "ClassStatTableActivity";

    public final static String EXTRA_TYPE = "com.codyy.erpsportal.extra_stat_profile";

    /**
     * 开课概况-主讲教室（主讲）
     */
    public final static int TYPE_PROFILE_MAIN = 0;

    /**
     * 开课概况-主讲教室（受邀）
     */
    public final static int TYPE_PROFILE_MAIN_INVITED = 1;

    /**
     * 开课概况-接收教室
     */
    public final static int TYPE_PROFILE_RECEIVING = 2;

    public static final String EXTRA_STAT_FILTER_CARRIER = "com.codyy.erpsportal.StatFilterCarrier";

    private RequestSender mRequestSender;

    private int mType;

    private UserInfo mUserInfo;

    /**
     * 教室类型 主讲教室-主讲(MASTER)、主讲教室-受邀(RECEIVE)、接收教室(ACCEPT)
     */
    private String mClassroomType;

    private AreaInfo mAreaInfo;

    /**
     * 类型：开课概况-主讲教室（主讲）/开课概况-主讲教室（受邀）/开课概况-接收教室
     */
    @Bind(R.id.tv_title)
    CenterLongTextView mTitleTv;

    private StatTableFragment mTableFragment;

    private List<CourseProfile> mCourseProfiles;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_stat_table);
        ButterKnife.bind(this);
        initAttributes();
        initViews();
        loadData();
    }

    private void initAttributes() {
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mType = getIntent().getIntExtra(EXTRA_TYPE, TYPE_PROFILE_MAIN);
        mAreaInfo = getIntent().getParcelableExtra(Extra.AREA_INFO);
        mStatFilterCarrier = getIntent().getParcelableExtra(EXTRA_STAT_FILTER_CARRIER);
        if (mAreaInfo == null) {
            mAreaInfo = new AreaInfo(mUserInfo);
        }
        mRequestSender = new RequestSender(this);
    }

    private void initViews() {
        mTableFragment = (StatTableFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_stat_table);
        mTableFragment.setOnRowClickListener(this);
        if (TextUtils.isEmpty( mAreaInfo.getName())) {
            if (mType == TYPE_PROFILE_MAIN) {
                mTitleTv.setText( getString(R.string.stat_classroom_role_format,
                        Titles.sWorkspaceCountTutiongeneral,
                        Titles.sMasterRoom,
                        Titles.sMaster));
            } else if (mType == TYPE_PROFILE_MAIN_INVITED) {
                mTitleTv.setText( getString(R.string.stat_classroom_role_format,
                        Titles.sWorkspaceCountTutiongeneral,
                        Titles.sMasterRoom,
                        Titles.sInvited));
            } else if (mType == TYPE_PROFILE_RECEIVING){
                mTitleTv.setText( getString(R.string.stat_receive_role_format,
                        Titles.sWorkspaceCountTutiongeneral,
                        Titles.sReceiveRoom));
            } else {
                mTitleTv.setText(Titles.sWorkspaceCountSubject);
            }
        } else {
            mTitleTv.setText(mAreaInfo.getName() + Titles.sWorkspaceCountTutiongeneral);
        }
        mLoadingDialog = LoadingDialog.newInstance(true);
        mLoadingDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel() {
                Cog.d(TAG, "cancel loading");
                mRequestSender.stop();
            }
        });
    }

    private void loadData() {
        loadData(mStatFilterCarrier);
    }

    private void loadData(StatFilterCarrier statFilterCarrier) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        if (mType == TYPE_PROFILE_MAIN) {
            mClassroomType = "MASTER";
        } else if (mType == TYPE_PROFILE_MAIN_INVITED) {
            mClassroomType = "RECEIVE";
        } else if (mType == TYPE_PROFILE_RECEIVING) {
            mClassroomType = "ACCEPT";
        }
        params.put("classroomType", mClassroomType);
        params.put("areaType", mAreaInfo.getType());
        params.put("id", mAreaInfo.getId());
        if (statFilterCarrier == null) {//没有筛选参数时
            if (TextUtils.isEmpty(mUserInfo.getParentId())) {//国家级查看省级数据列表为按周统计的当前周数据
                LocalDate localDate = LocalDate.now();
                params.put("startDate",localDate.withDayOfWeek(DateTimeConstants.MONDAY).toString());
                params.put("endDate", localDate.withDayOfWeek(DateTimeConstants.SUNDAY).toString());
            } else {//省市级及以下查看的列表数据默认按学期统计的当前学期数据
                params.put("trimesterId", "-1");
            }
        } else {
            putFilterParams(statFilterCarrier, params);
        }
        mLoadingDialog.show(getSupportFragmentManager(), "loading");
        mRequestSender.sendRequest(new RequestData(URLConfig.COURSES_PROFILE_STAT, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "+loadDate response=" ,response);
                mLoadingDialog.dismiss();
                Gson gson;
                if (mUserInfo.isSchool()) {//如果是学校跳过doClsroomRate和avgRate字段，web端不给力
                    gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            String fieldName = f.getName();
                            return "doClsroomRate".equals(fieldName) || "avgRate".equals(fieldName);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    }).create();
                } else {
                    gson = new Gson();
                }
                CourseProfilesResult courseProfilesResult = gson.fromJson(response.toString(), CourseProfilesResult.class);
                if ("success".equals(courseProfilesResult.getResult())) {
                    setTableData(courseProfilesResult);
                } else {
                    ToastUtil.showToast(ClassStatTableActivity.this, "获取数据失败！");
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "+loadDate error=", error);
                mLoadingDialog.dismiss();
                ToastUtil.showToast(ClassStatTableActivity.this, getString(R.string.net_error));
            }
        }));
    }

    /**
     * 附上筛选参数
     * @param statFilterCarrier 筛选信息
     * @param params 筛选参数
     */
    private void putFilterParams(StatFilterCarrier statFilterCarrier, Map<String, String> params) {
        if (statFilterCarrier != null) {
            if (!TextUtils.isEmpty(statFilterCarrier.getStartDate())) {
                params.put("startDate", statFilterCarrier.getStartDate());
            }
            if (!TextUtils.isEmpty(statFilterCarrier.getEndDate())) {
                params.put("endDate", statFilterCarrier.getEndDate());
            }
            if (!TextUtils.isEmpty(statFilterCarrier.getTermId())) {
                params.put("trimesterId", statFilterCarrier.getTermId());
            } else {
                params.put("trimesterId", "");
            }
            if (statFilterCarrier.getFilterBy() == StatFilterBy.BY_SPECIFIC_DATE) {
                params.put("isCustomized", "Y");
            } else {
                params.put("isCustomized", "N");
            }
        }
    }

    private void setTableData(CourseProfilesResult courseProfilesResult) {
        mCourseProfiles = courseProfilesResult.getDataList();
        if (mCourseProfiles == null || mCourseProfiles.size() == 0) return;
        StatTableModel<StatRow> statTableModel = new StatTableModel<>();
        List<StatRow> statRows = new ArrayList<>();
        statTableModel.setTitle(courseProfilesResult.getAreaLevel());
        if (mAreaInfo.isSchool()) {
            statTableModel.setEms(5);
            statTableModel.setColumnTitles(getResources().getStringArray(R.array.profile_school_table_titles));
        } else if (mType == TYPE_PROFILE_MAIN) {
            statTableModel.setColumnTitles(getResources().getStringArray(R.array.profile_main_table_titles));
        } else {
            statTableModel.setColumnTitles(getResources().getStringArray(R.array.profile_invited_table_titles));
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(0);
        if (mAreaInfo.isSchool()) {//学校查看只需要看计划课时|实开课时|实开课时占比三个
            for (CourseProfile courseProfile: mCourseProfiles) {
                statRows.add(new StatRow(courseProfile.getName()
                        , courseProfile.getPlanCnt()
                        , courseProfile.getDownCnt()
                        , numberFormat.format(courseProfile.getDownRate()) + "%"
                ));
            }
        } else {
            for (CourseProfile courseProfile: mCourseProfiles) {
                statRows.add(new StatRow(courseProfile.getName()
                        , courseProfile.getClassroomCnt()
                        , courseProfile.getDoClsroomCnt()
                        , numberFormat.format(courseProfile.getDoClsroomRate()) + "%"
                        , courseProfile.getPlanCnt()
                        , courseProfile.getDownCnt()
                        , numberFormat.format(courseProfile.getDownRate()) + "%"
                        , numberFormat.format(courseProfile.getAvgRate())
                ));
            }
        }
        statTableModel.setRows(statRows);
        if (mCourseProfiles.size() > 0) {
            CourseProfile courseProfile = mCourseProfiles.get(0);
            if (courseProfile != null && mAreaInfo.isArea()
                    && AreaInfo.TYPE_SCHOOL.equals(courseProfile.getType())) {
                statTableModel.setEms(5);
            }
        }
        mTableFragment.setTableModel(statTableModel);
    }

    @OnClick(R.id.ib_filter)
    public void onFilterClick(View view) {
        String title = mTitleTv.getText().toString();
        CoursesProfilesFilterActivity.startFilter(this, mUserInfo, title, mStatFilterCarrier, mSpecificDate);
    }

    /**
     * 当前筛选参数
     */
    private StatFilterCarrier mStatFilterCarrier;

    private int mSpecificDate;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CoursesProfilesFilterActivity.REQUEST_CODE
                && resultCode == RESULT_OK) {
            mStatFilterCarrier = data.getParcelableExtra(
                    CoursesProfilesFilterActivity.EXTRA_OUT_FILTER);
            loadData(mStatFilterCarrier);
        } else if (requestCode == CoursesProfilesFilterActivity.REQUEST_CODE
                && resultCode == RESULT_CANCELED) {
            mSpecificDate = data.getIntExtra(
                    CoursesProfilesFilterActivity.EXTRA_SPECIFIC_DATE, 0);
        }
    }

    @Override
    public void onRowClickListener(int position) {
        Cog.d(TAG, "onRowClickListener position=" + position);
        if (mUserInfo.isSchool()) return;//学校查看的下级是班级，班级没有下级，点击项直接不做处理
        CourseProfile courseProfile = mCourseProfiles.get(position);
        if (!"TOTAL".equals(courseProfile.getType())
                && !AreaInfo.TYPE_SCHOOL.equals(courseProfile.getType())) {//排除总计项点击事件,学校也不能往下点了
            AreaInfo areaInfo = new AreaInfo(courseProfile.getId(), courseProfile.getType());
            if (TextUtils.isEmpty(mAreaInfo.getName())) {
                areaInfo.setName(courseProfile.getName());
            } else {
                areaInfo.setName(mAreaInfo.getName()+ "/" + courseProfile.getName());
            }
            start(this, mUserInfo, mType, areaInfo, mStatFilterCarrier);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UIUtils.addExitTranAnim(this);
    }

    @OnClick(R.id.btn_return)
    public void onReturnClick() {
        finish();
        UIUtils.addExitTranAnim(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private static void start(Context context, UserInfo userInfo, @ProfileType int type) {
        start(context, userInfo, type, null, null);
    }

    private static void start(Context context, UserInfo userInfo, @ProfileType int type,
                              AreaInfo areaInfo, StatFilterCarrier statFilterCarrier) {
        Intent intent = new Intent(context, ClassStatTableActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_TYPE, type);
        if (areaInfo != null)
            intent.putExtra(Extra.AREA_INFO, areaInfo);
        if (statFilterCarrier != null) {
            intent.putExtra(EXTRA_STAT_FILTER_CARRIER, statFilterCarrier);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }

    public static void startMain(Context context, UserInfo userInfo) {
        start(context, userInfo, TYPE_PROFILE_MAIN);
    }

    public static void startMainInvited(Context context, UserInfo userInfo) {
        start(context, userInfo, TYPE_PROFILE_MAIN_INVITED);
    }

    public static void startReceiving(Context context, UserInfo userInfo) {
        start(context, userInfo, TYPE_PROFILE_RECEIVING);
    }

    public static void startSubject(Context context, UserInfo userInfo) {
        SubjectStatTbActivity.startSubject(context, userInfo);
    }

    /**
     * 概况类型限定词
     */
    @IntDef(value = {TYPE_PROFILE_MAIN, TYPE_PROFILE_MAIN_INVITED, TYPE_PROFILE_RECEIVING})
    @Retention(RetentionPolicy.SOURCE)
    @interface ProfileType{}
}
