package com.codyy.erpsportal.statistics.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog.OnCancelListener;
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
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
import com.codyy.erpsportal.statistics.models.entities.StatFilterBy;
import com.codyy.erpsportal.statistics.models.entities.StatFilterCarrier;
import com.codyy.erpsportal.statistics.models.entities.StatRow;
import com.codyy.erpsportal.statistics.models.entities.StatTableModel;
import com.codyy.erpsportal.statistics.models.entities.SubjectsStatEntity;
import com.codyy.erpsportal.statistics.models.entities.SubjectsStatResult;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.codyy.erpsportal.statistics.models.entities.StatFilterBy.BY_TERM;

/**
 * 学科统计
 */
public class SubjectStatTbActivity extends AppCompatActivity{

    private final static String TAG = "SubjectStatTbActivity";

    public static final String EXTRA_STAT_FILTER_CARRIER = "com.codyy.erpsportal.StatFilterCarrier";

    private RequestSender mRequestSender;

    private UserInfo mUserInfo;

    private AreaInfo mAreaInfo;

    @Bind(R.id.tv_title)
    CenterLongTextView mTitleTv;

    private StatTableFragment mTableFragment;

    private List<SubjectsStatEntity> mStatEntities;

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

    @Override
    protected void onDestroy() {
        mRequestSender.stop();
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initAttributes() {
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mAreaInfo = getIntent().getParcelableExtra(Extra.AREA_INFO);
        mStatFilterCarrier = getIntent().getParcelableExtra(EXTRA_STAT_FILTER_CARRIER);

        mRequestSender = new RequestSender(this);
    }

    private void initViews() {
        mTableFragment = (StatTableFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_stat_table);
//        mTableFragment.setOnRowClickListener(this);
        mTitleTv.setText(R.string.subject_statistics);
        mLoadingDialog = LoadingDialog.newInstance(true);
        mLoadingDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel() {
                Cog.d(TAG, "cancel loading");
                mRequestSender.stop();
            }
        });
    }

    /**
     * 默认数据加载
     */
    private void loadData() {
        LocalDate localDate = LocalDate.now();
        if (mStatFilterCarrier == null) {
            mStatFilterCarrier = new StatFilterCarrier();
            //省市县校用户进入默认选中【按学期】，国家用户进入默认选中【按周】
            if (mUserInfo.isNation()) {
                mStatFilterCarrier.setStartDate(
                        localDate.withDayOfWeek(DateTimeConstants.MONDAY).toString());
                mStatFilterCarrier.setEndDate(
                        localDate.withDayOfWeek(DateTimeConstants.SUNDAY).toString());
                mStatFilterCarrier.setFilterBy(StatFilterBy.BY_WEEK);
            } else {
                mStatFilterCarrier.setTermId("-1");
                mStatFilterCarrier.setFilterBy(StatFilterBy.BY_TERM);
            }
//            mStatFilterCarrier.setSubjectId("-1");
        }
        loadData( mStatFilterCarrier);
    }

    /**
     * 加载数据
     * @param statFilterCarrier 统计筛选参数
     */
    private void loadData(StatFilterCarrier statFilterCarrier) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        String url = URLConfig.SUBJECT_STAT;
        if (mAreaInfo != null) {
            if (mAreaInfo.isArea() || mAreaInfo.isDirectSchool()) {
                params.put("areaId", mAreaInfo.getId());
            } else {
                params.put("schoolId", mAreaInfo.getId());
            }
        } else {
            params.put("areaId", mUserInfo.getBaseAreaId());
            if (mUserInfo.isSchool()) {
                params.put("schoolId", mUserInfo.getSchoolId());
            }
        }
        putFilterParams(statFilterCarrier, params);
        mLoadingDialog.show(getSupportFragmentManager(), "loading");
        RequestData requestData = new RequestData(url, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "+loadData response=" ,response);
                mLoadingDialog.dismiss();
                Gson gson = new Gson();
                SubjectsStatResult subjectsStatResult = gson.fromJson(response.toString(), SubjectsStatResult.class);
                if (subjectsStatResult.isResult()) {
                    setTableData(subjectsStatResult);
                } else {
                    ToastUtil.showToast(SubjectStatTbActivity.this, "获取数据失败！");
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "+loadData error=", error);
                mLoadingDialog.dismiss();
                ToastUtil.showToast(SubjectStatTbActivity.this, getString(R.string.net_error));
            }
        });
        mRequestSender.sendRequest( requestData);
    }

    /**
     * 附上筛选参数
     * @param statFilterCarrier 筛选信息
     * @param params 筛选参数
     */
    private void putFilterParams(StatFilterCarrier statFilterCarrier, Map<String, String> params) {
        if (statFilterCarrier != null) {
            if (statFilterCarrier.getFilterBy() == BY_TERM) {
                params.put("trimesterId", statFilterCarrier.getTermId());
            } else {
                if (!TextUtils.isEmpty(statFilterCarrier.getStartDate())) {
                    params.put("startDt", statFilterCarrier.getStartDate());
                }
                if (!TextUtils.isEmpty(statFilterCarrier.getEndDate())) {
                    params.put("endDt", statFilterCarrier.getEndDate());
                }
            }
        }
    }

    /**
     * 填充表数据
     * @param courseProfilesResult 学科统计请求结果数据
     */
    private void setTableData(SubjectsStatResult courseProfilesResult) {
        mStatEntities = courseProfilesResult.getStatEntities();
        StatTableModel<StatRow> statTableModel = new StatTableModel<>();
        List<StatRow> statRows = new ArrayList<>();
        statTableModel.setTitle("学科");
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(0);
        statTableModel.setColumnTitles(getResources().getStringArray(R.array.subject_table_titles));
        for (SubjectsStatEntity statEntity: mStatEntities) {
            statRows.add(new StatRow( statEntity.getSubjectName()
                    , statEntity.getPlanCnt()
                    , statEntity.getDownCnt()
                    , numberFormat.format(statEntity.getDownRate()) + "%"
            ));
        }

        statTableModel.setRows(statRows);
        mTableFragment.setTableModel(statTableModel);
    }

    @OnClick(R.id.ib_filter)
    public void onFilterClick(View view) {
        String title = mTitleTv.getText().toString();
        CoursesProfilesFilterActivity.startFilter(this, mUserInfo, title, mStatFilterCarrier, mSpecificDate);
    }

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

    @OnClick(R.id.btn_return)
    public void onReturnClick() {
        finish();
        UIUtils.addExitTranAnim(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UIUtils.addExitTranAnim(this);
    }

    private static void start(Context context, UserInfo userInfo) {
        start(context, userInfo, null, null);
    }

    private static void start(Context context, UserInfo userInfo, AreaInfo areaInfo,
                StatFilterCarrier statFilterCarrier) {
        Intent intent = new Intent(context, SubjectStatTbActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        if (areaInfo != null) {
            intent.putExtra(Extra.AREA_INFO, areaInfo);
        }
        if (statFilterCarrier != null) {
            intent.putExtra(EXTRA_STAT_FILTER_CARRIER, statFilterCarrier);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }

    public static void startSubject(Context context, UserInfo userInfo) {
        start(context, userInfo);
    }
}
