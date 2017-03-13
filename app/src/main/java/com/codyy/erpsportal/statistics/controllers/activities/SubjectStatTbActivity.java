package com.codyy.erpsportal.statistics.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog.OnCancelListener;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.CenterLongTextView;
import com.codyy.erpsportal.statistics.controllers.fragments.StatTableFragment;
import com.codyy.erpsportal.statistics.controllers.fragments.StatTableFragment.OnRowClickListener;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
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

/**
 * 学科统计
 */
public class SubjectStatTbActivity extends AppCompatActivity implements OnRowClickListener {

    private final static String TAG = "SubjectStatTbActivity";

    public static final String EXTRA_STAT_FILTER_CARRIER = "com.codyy.erpsportal.StatFilterCarrier";

    private RequestSender mRequestSender;

    private UserInfo mUserInfo;

    private AreaInfo mAreaInfo;

    @Bind(R.id.tv_title)
    CenterLongTextView mTitleTv;

    private StatTableFragment mTableFragment;

    private List<SubjectsStatEntity> mStatEntities;

    private Object mRequestTag = new Object();

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
        mRequestSender.stop(mRequestTag);
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initAttributes() {
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mAreaInfo = getIntent().getParcelableExtra(Extra.AREA_INFO);
        mStatFilterCarrier = getIntent().getParcelableExtra(EXTRA_STAT_FILTER_CARRIER);
        if (mAreaInfo == null) {
            mAreaInfo = new AreaInfo(mUserInfo);
        }

        mRequestSender = new RequestSender(this);
    }

    private void initViews() {
        mTableFragment = (StatTableFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_stat_table);
        mTableFragment.setOnRowClickListener(this);
        mTitleTv.setText(R.string.subject_statistics);
        mLoadingDialog = LoadingDialog.newInstance(true);
        mLoadingDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel() {
                Cog.d(TAG, "cancel loading");
                mRequestSender.stop(mRequestTag);
            }
        });
    }

    private void loadData() {
        LocalDate localDate = LocalDate.now();
        if (mStatFilterCarrier == null) {
            mStatFilterCarrier = new StatFilterCarrier();
            mStatFilterCarrier.setStartDate(
                    localDate.withDayOfWeek(DateTimeConstants.MONDAY).toString());
            mStatFilterCarrier.setEndDate(
                    localDate.withDayOfWeek(DateTimeConstants.SUNDAY).toString());
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
        String url;
        if (mAreaInfo.isArea() || mAreaInfo.isDirectSchool()) {
            url = URLConfig.SUBJECT_STAT_AREA;
            if (mAreaInfo.isDirectSchool()) {
                params.put("isDirect", "Y");
            } else {
                params.put("isDirect", "N");
            }
            params.put("areaId", mAreaInfo.getId());
        } else {
            url = URLConfig.SUBJECT_STAT_SCHOOL;
            params.put("schoolId", mAreaInfo.getId());
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
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "+loadData error=", error);
                mLoadingDialog.dismiss();
                ToastUtil.showToast(SubjectStatTbActivity.this, getString(R.string.net_error));
            }
        }, mRequestTag);
        requestData.setTimeout(60000);
        mRequestSender.sendRequest( requestData);
    }

    /**
     * 附上筛选参数
     * @param statFilterCarrier 筛选信息
     * @param params 筛选参数
     */
    private void putFilterParams(StatFilterCarrier statFilterCarrier, Map<String, String> params) {
        if (statFilterCarrier != null) {
            if (!TextUtils.isEmpty(statFilterCarrier.getStartDate())) {
                params.put("startDt", statFilterCarrier.getStartDate());
            }
            if (!TextUtils.isEmpty(statFilterCarrier.getEndDate())) {
                params.put("endDt", statFilterCarrier.getEndDate());
            }
            if (!TextUtils.isEmpty(statFilterCarrier.getSubjectId())) {
                params.put("subjectId", statFilterCarrier.getSubjectId());
            } else {
                params.put("subjectId", "-1");
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
        statTableModel.setTitle(courseProfilesResult.getTitle());
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(0);
        if (mAreaInfo.isSchool()) {
            statTableModel.setColumnTitles(getResources().getStringArray(R.array.subject_school_table_titles));
            for (SubjectsStatEntity statEntity: mStatEntities) {
                statRows.add(new StatRow(statEntity.getSubjectName()
                        , statEntity.getPlanCnt()
                        , statEntity.getRequiredCnt()
                        , statEntity.getDownCnt()
                        , numberFormat.format(statEntity.getDownRate()) + "%"
                ));
            }
        } else {
            statTableModel.setColumnTitles(getResources().getStringArray(R.array.subject_area_table_titles));
            for (SubjectsStatEntity statEntity: mStatEntities) {
                statRows.add(new StatRow(statEntity.getAreaName()
                        , statEntity.getSubjectName()
                        , statEntity.getPlanCnt()
                        , statEntity.getRequiredCnt()
                        , statEntity.getDownCnt()
                        , numberFormat.format(statEntity.getDownRate()) + "%"
                ));
            }
            if (mStatEntities != null && mStatEntities.get(0) != null
                    && mStatEntities.get(0).getAreaType().equals("school")) {//如果是学校第一列显示5个字
                statTableModel.setEms(5);
            }
        }
        statTableModel.setRows(statRows);
        mTableFragment.setTableModel(statTableModel);
    }

    @OnClick(R.id.ib_filter)
    public void onFilterClick(View view) {
        String title = mTitleTv.getText().toString();
        CoursesProfilesFilterActivity.startSubjectFilter(this, mUserInfo, title, mStatFilterCarrier);
    }

    @Override
    public void onRowClickListener(int position) {
        Cog.d(TAG, "onRowClickListener position=" + position);
        SubjectsStatEntity statEntity = mStatEntities.get(position);
        AreaInfo areaInfo = null;
        if ("area".equals(statEntity.getAreaType())) {
            areaInfo = new AreaInfo(statEntity.getAreaId(), AreaInfo.TYPE_AREA);
        } else if ("dirSch".equals(statEntity.getAreaType())) {
            areaInfo = new AreaInfo(statEntity.getAreaId(), AreaInfo.TYPE_DSCH);
        }
        if (areaInfo == null) return;
        if (TextUtils.isEmpty(mAreaInfo.getName())) {
            areaInfo.setName(statEntity.getAreaName());
        } else {
            areaInfo.setName(mAreaInfo.getName()+ "/" + statEntity.getAreaName());
        }
        start(this, mUserInfo, areaInfo, mStatFilterCarrier);
    }

    private StatFilterCarrier mStatFilterCarrier;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CoursesProfilesFilterActivity.REQUEST_CODE
                && resultCode == RESULT_OK) {
            mStatFilterCarrier = data.getParcelableExtra(
                    CoursesProfilesFilterActivity.EXTRA_OUT_FILTER);
            loadData(mStatFilterCarrier);
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
