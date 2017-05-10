package com.codyy.erpsportal.statistics.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
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
import com.codyy.erpsportal.statistics.controllers.fragments.MonthlyStatFragment;
import com.codyy.erpsportal.statistics.controllers.fragments.StatTableFragment;
import com.codyy.erpsportal.statistics.controllers.fragments.StatTableFragment.OnRowClickListener;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
import com.codyy.erpsportal.statistics.models.entities.ProportionBaseResult;
import com.codyy.erpsportal.statistics.models.entities.StatCell;
import com.codyy.erpsportal.statistics.models.entities.StatRow;
import com.codyy.erpsportal.statistics.models.entities.StatTableModel;
import com.codyy.erpsportal.statistics.models.entities.TermEntity;
import com.codyy.erpsportal.statistics.models.entities.TermlyPropInfoResult;
import com.codyy.erpsportal.statistics.models.entities.TermlyPropItem;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 开课比分析
 */
public class CoursesProportionTableActivity extends AppCompatActivity {

    private final static String TAG = "CoursesProportionTableActivity";

    public final static String EXTRA_TYPE = "com.codyy.erpsportal.extra_stat_proportion";

    /**
     * 只显示学期统计表
     */
    public final static String EXTRA_TERMS = "com.codyy.erpsportal.extra_stat_ONLY_TERMLY";

    /**
     * 开课比分析-主讲教室（主讲）
     */
    public final static int TYPE_PROPORTION_MAIN = 0;

    /**
     * 开课比分析-主讲教室（受邀）
     */
    public final static int TYPE_PROPORTION_MAIN_INVITED = 1;

    /**
     * 开课比分析-接收教室
     */
    public final static int TYPE_PROPORTION_RECEIVING = 2;

    /**
     * 按月统计
     */
    private final static int SCOPE_MONTH = 0;

    /**
     * 学期统计
     */
    private final static int SCOPE_TERM = 1;

    /**
     * 范围类型：学期or按月
     */
    private int mScope;

    /**
     * 三种类型：主讲教室（主讲）or主讲教室（受邀）or接收教室
     * <p>见
     * {@link #TYPE_PROPORTION_MAIN}、
     * {@link #TYPE_PROPORTION_MAIN_INVITED}、
     * {@link #TYPE_PROPORTION_RECEIVING}
     */
    private int mType;

    private UserInfo mUserInfo;

    private AreaInfo mAreaInfo;

    private RequestSender mRequestSender;

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.btn_date_scope)
    Button mDateScopeBtn;

    private final static String TAG_MONTHLY = "TAG_MONTHLY";

    private final static String TAG_TERMLY = "TAG_TERMLY";

    /**
     * 按月统计表
     */
    private MonthlyStatFragment mMonthlyStatFragment;

    /**
     * 学期统计表
     */
    private StatTableFragment mTermlyStatFragment;

    /**
     * 学期列表
     */
    private List<TermEntity> mTermEntities;

    /**
     * 学期统计项列表
     */
    private List<TermlyPropItem> mTermlyPropItems;

    private LoadingDialog mLoadingDialog;

    private Object mRequestTag = new Object();

    private OnRowClickListener mOnRowClickListener = new OnRowClickListener() {
        @Override
        public void onRowClickListener(int position) {
            int itemPosition = mTermEntities.size() * position;
            if (mTermlyPropItems != null && itemPosition > 0 && itemPosition < mTermlyPropItems.size()) {
                TermlyPropItem termlyPropItem = mTermlyPropItems.get(itemPosition);
                AreaInfo areaInfo = new AreaInfo();
                if (TextUtils.isEmpty(areaInfo.getName())){
                    areaInfo.setName(termlyPropItem.getName());
                } else {
                    areaInfo.setName(areaInfo.getName() + "/" + termlyPropItem.getName());
                }
                if (termlyPropItem.getBaseAreaId() != null) {//地区id不为null，说明当前项是地区
                    areaInfo.setId(termlyPropItem.getBaseAreaId());
                    if (termlyPropItem.isDirect()) {
                        areaInfo.setType(AreaInfo.TYPE_DSCH);
                    } else {
                        areaInfo.setType(AreaInfo.TYPE_AREA);
                    }
                } else if (termlyPropItem.getClsSchoolId() != null) {//学校id不为null，说明当前项是学校
                    areaInfo.setId(termlyPropItem.getClsSchoolId());
                    areaInfo.setType(AreaInfo.TYPE_SCHOOL);
                } else {
                    return;
                }
                startOnlyTermly(CoursesProportionTableActivity.this, mUserInfo, areaInfo, mType, mTermEntities);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_proportion_table);
        ButterKnife.bind(this);
        initAttributes();
        initViews();
        loadData();
        if (savedInstanceState != null) {
            mTermlyStatFragment = (StatTableFragment) getSupportFragmentManager().findFragmentByTag(TAG_TERMLY);
            if (mTermlyStatFragment != null) {
                mTermlyStatFragment.setOnRowClickListener(mOnRowClickListener);
            }
        }
    }

    private void initAttributes() {
        mType = getIntent().getIntExtra(EXTRA_TYPE, TYPE_PROPORTION_MAIN);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mAreaInfo = getIntent().getParcelableExtra(Extra.AREA_INFO);
        mTermEntities = getIntent().getParcelableArrayListExtra(EXTRA_TERMS);
        mRequestSender = new RequestSender(this);
        if (mAreaInfo == null) {
            mAreaInfo = new AreaInfo(mUserInfo);
        }
    }

    private void initViews() {
        if (mTermEntities != null) {//如果是从上级数据列表点击进入此页面，则标题栏显示“XXXX省/市/县/校开课比分析”
            mTitleTv.setText(mAreaInfo.getName() + Titles.sWorkspaceCountTutiontate);
        } else if (mType == TYPE_PROPORTION_MAIN) {
            mTitleTv.setText(getString(R.string.stat_classroom_role_format,
                    Titles.sWorkspaceCountTutiontate,
                    Titles.sMasterRoom,
                    Titles.sMaster));
        } else if (mType == TYPE_PROPORTION_MAIN_INVITED) {
            mTitleTv.setText(getString(R.string.stat_classroom_role_format,
                    Titles.sWorkspaceCountTutiontate,
                    Titles.sMasterRoom,
                    Titles.sInvited));
        } else {
            mTitleTv.setText(getString(R.string.stat_receive_role_format,
                    Titles.sWorkspaceCountTutiongeneral,
                    Titles.sReceiveRoom));
        }

        if (mTermEntities != null) {//如果一开始就有学期列表说明是从上级地区的开课比分析进来的
            mDateScopeBtn.setVisibility(View.GONE);
            if (mTermlyStatFragment == null) {
                mTermlyStatFragment = StatTableFragment.newInstance();
            }
            mTermlyStatFragment.setOnRowClickListener(mOnRowClickListener);
            addNewFragment(mTermlyStatFragment, TAG_TERMLY);
        } else {
            mMonthlyStatFragment = MonthlyStatFragment.newInstance(mUserInfo , mType);
            addNewFragment(mMonthlyStatFragment, TAG_MONTHLY);
            if (mUserInfo.isArea() && TextUtils.isEmpty(mUserInfo.getParentId())) {//是地区且没父级，说明是国家
                mDateScopeBtn.setVisibility(View.GONE);
            }
        }
        mLoadingDialog = LoadingDialog.newInstance();
    }

    private void addNewFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_content, fragment, tag);
        fragmentTransaction.commit();
    }

    private void loadData() {
        if (mTermEntities != null) {
            loadTermsStat(mAreaInfo);
        } else {
            loadAreasAndTermsData();
        }
    }

    /**
     * 加载地区与学期数据
     */
    private void loadAreasAndTermsData() {
        String url = URLConfig.COURSES_PROPORTION_STAT_BASE
                + "?uuid=" + mUserInfo.getUuid()
                + "&statisticType=" + obtainStatisticType();
        mRequestSender.sendGetRequest(new RequestData(url, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadAreasAndTermsData response=", response);
                Gson gson = new Gson();
                ProportionBaseResult baseResult = gson.fromJson(response.toString(), ProportionBaseResult.class);
                if (baseResult.getAreaList() != null && mMonthlyStatFragment.isResumed()) {
                    mMonthlyStatFragment.setScopes(baseResult.getAreaList());
                }
                mTermEntities = baseResult.getTrimesterList();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "onError error=", error);
            }
        }, mRequestTag));
    }

    /**
     * 获取对应教室类型的参数
     * @return 教室类型字串
     */
    private String obtainStatisticType() {
        if (mType == TYPE_PROPORTION_MAIN) {
            return "MASTER";
        } else if (mType == TYPE_PROPORTION_MAIN_INVITED) {
            return "INVITE";
        } else {
            return "RECEIVE";
        }
    }

    @OnClick(R.id.btn_date_scope)
    public void onClick(View view) {
        if (mScope == SCOPE_MONTH) {//从按月统计，切换到按学期统计
            if (mTermEntities == null || mTermEntities.size() == 0) {
                ToastUtil.showToast(this, "没有学期数据！");
                return;
            }
            mScope = SCOPE_TERM;
            mDateScopeBtn.setText(R.string.scope_month);
            if (mTermlyStatFragment == null) {
                mTermlyStatFragment = StatTableFragment.newInstance();
                mTermlyStatFragment.setOnRowClickListener(mOnRowClickListener);
            }
            replaceContent(mTermlyStatFragment, TAG_TERMLY);
            loadTermsStat(mAreaInfo);
        } else {//从按学期统计，切换到按月统计
            mScope = SCOPE_MONTH;
            mDateScopeBtn.setText(R.string.scope_term);
            replaceContent(mMonthlyStatFragment, TAG_MONTHLY);
        }
    }

    /**
     * 替换内容碎片
     * @param fragment 碎片
     * @param tag 碎片标签
     */
    private void replaceContent(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_content, fragment, tag);
        fragmentTransaction.commit();
    }

    /**
     * 加载学期统计信息
     * @param areaInfo 地区信息
     */
    private void loadTermsStat(AreaInfo areaInfo) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        if (areaInfo.isArea()) {
            params.put("baseAreaId", mAreaInfo.getId());
            params.put("isDirect", "N");
        } else if (areaInfo.isSchool()) {
            params.put("clsSchoolId", mAreaInfo.getId());
        } else if (areaInfo.isDirectSchool()) {
            params.put("baseAreaId", mAreaInfo.getId());
            params.put("isDirect", "Y");
        }
        params.put("statisticType", obtainStatisticType());
        StringBuilder sb = new StringBuilder();
        for (TermEntity termEntity: mTermEntities) {
            sb.append(termEntity.getId()).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        params.put("trimesterIds", sb.toString());

        mLoadingDialog.show(getSupportFragmentManager(), "loading_dialog_termly_stat");
        RequestData requestData = new RequestData(URLConfig.COURSES_PROPORTION_STAT_TERM, params,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "loadTermsStat response=", response);
                        mLoadingDialog.dismiss();
                        Gson gson = new Gson();
                        TermlyPropInfoResult termlyPropInfoResult = gson
                                .fromJson(response.toString(), TermlyPropInfoResult.class);
                        if (termlyPropInfoResult.isResult()) {
                            stuffTermStatTable(termlyPropInfoResult);
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(Throwable error) {
                        Cog.d(TAG, "loadTermsStat error=", error);
                        mLoadingDialog.dismiss();
                    }
                }, mRequestTag
        );
        requestData.setTimeout(60000);
        mRequestSender.sendGetRequest( requestData);
    }

    /**
     * 填充按学期统计表数据
     * @param termlyPropInfoResult 按学期统计表数据请求结果
     */
    private void stuffTermStatTable(TermlyPropInfoResult termlyPropInfoResult) {
        mTermlyPropItems = termlyPropInfoResult.getData();
        int columnCount = mTermEntities.size();
        int rowCount = mTermlyPropItems.size() / columnCount;

        StatTableModel<StatRow> statTableModel = new StatTableModel<>();
        statTableModel.setTitle(termlyPropInfoResult.getTitle());
        String[] columnTitles = new String[columnCount];
        for (int i=0; i<columnTitles.length; i++) {
            columnTitles[i] = mTermEntities.get(i).getName();
        }
        statTableModel.setColumnTitles(columnTitles);

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(0);

        List<StatRow> statRows = new ArrayList<>(rowCount);
        for (int i=0; i<rowCount; i++) {
            StatRow statRow = new StatRow();//创建行
            StatCell[] statCells = new StatCell[columnCount];
            for (int j=0; j< columnCount; j++) {
                TermlyPropItem termlyPropItem = mTermlyPropItems.get(i * columnCount + j);
                if (j == 0) {
                    statRow.setRowTitle(termlyPropItem.getName());
                }
                statCells[j] = new StatCell(
                        numberFormat.format( termlyPropItem.isDirect()?//直属校取directRatio
                                termlyPropItem.getDirectRatio(): termlyPropItem.getRatio()) + "%",
                        termlyPropItem.isDownFlag()? -1: 0);
            }
            statRow.setStatCells(statCells);//设置行中数字内容
            statRows.add(statRow);
        }
        statTableModel.setRows(statRows);
        mTermlyStatFragment.setTableModel(statTableModel);
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(mRequestTag);
        super.onDestroy();
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

    private static void start(Context context, UserInfo userInfo, AreaInfo areaInfo, @ProportionType int type) {
        Intent intent = new Intent(context, CoursesProportionTableActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(Extra.AREA_INFO, areaInfo);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }

    private static void startOnlyTermly(Context context, UserInfo userInfo, AreaInfo areaInfo, @ProportionType int type, List<TermEntity> termEntities) {
        Intent intent = new Intent(context, CoursesProportionTableActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(Extra.AREA_INFO, areaInfo);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_TERMS, new ArrayList<>(termEntities));
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }

    private static void start(Context context, UserInfo userInfo, @ProportionType int type) {
        AreaInfo areaInfo = new AreaInfo(userInfo);
        start(context, userInfo, areaInfo, type);
    }

    public static void startMain(Context context, UserInfo userInfo) {
        start(context, userInfo, TYPE_PROPORTION_MAIN);
    }

    public static void startMainInvited(Context context, UserInfo userInfo) {
        start(context, userInfo, TYPE_PROPORTION_MAIN_INVITED);
    }

    public static void startReceiving(Context context, UserInfo userInfo) {
        start(context, userInfo, TYPE_PROPORTION_RECEIVING);
    }

    /**
     * 开课比类型限定词
     */
    @Target(ElementType.PARAMETER)
    @IntDef({TYPE_PROPORTION_MAIN, TYPE_PROPORTION_MAIN_INVITED, TYPE_PROPORTION_RECEIVING})
    @Retention(RetentionPolicy.SOURCE)
    @interface ProportionType{ }
}
