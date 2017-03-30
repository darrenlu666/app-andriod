package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.Builder;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.ListExtractor;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.codyy.erpsportal.repairs.controllers.adapters.CheckedTextAdapter;
import com.codyy.erpsportal.repairs.controllers.adapters.CheckedTextAdapter.OnItemClickListener;
import com.codyy.erpsportal.repairs.controllers.viewholders.RepairRecordVh;
import com.codyy.erpsportal.repairs.models.entities.ClassroomFilterItem;
import com.codyy.erpsportal.repairs.models.entities.RepairFilterItem;
import com.codyy.erpsportal.repairs.models.entities.RepairRecord;
import com.codyy.erpsportal.repairs.models.entities.StatusItem;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.disposables.ListCompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 学校报修记录
 */
public class SchoolRepairsActivity extends AppCompatActivity implements ListExtractor<RepairRecord, RepairRecordVh> {

    private final static String TAG = "SchoolRepairsActivity";

    @Bind(R.id.ll_filters_bar)
    LinearLayout mFiltersBarLl;

    @Bind(R.id.btn_common_malfunction)
    Button mCommonMalfuncBtn;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.rv_list)
    RecyclerView mListRv;

    @Bind(R.id.rv_filter_list)
    RecyclerView mFilterListRv;

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    @Bind(R.id.tv_classroom_filter)
    CheckedTextView mClassroomFilterTv;

    @Bind(R.id.fl_classroom_filter)
    FrameLayout mClassroomFilterFl;

    @Bind(R.id.tv_status_filter)
    CheckedTextView mStatusFilterTv;

    @Bind(R.id.fl_status_filter)
    FrameLayout mStatusFilterFl;

    @Bind(R.id.fab_report)
    FloatingActionButton mReportFab;

    @Bind(R.id.fl_list_container)
    FrameLayout mListContainerFl;

    @Bind(R.id.fl_filter_list_container)
    FrameLayout mFilterListFl;

    private UserInfo mUserInfo;

    private String mSchoolId;

    private RvLoader<RepairRecord, RepairRecordVh, Void> mLoader;

    private CheckedTextAdapter mStatusAdapter;

    private CheckedTextAdapter mClassroomAdapter;

    private WebApi mWebApi;

    private ListCompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_repairs);
        ButterKnife.bind(this);
        initAttributes();
        initComponent();
        loadData();
    }

    /**
     * 初始化属性
     */
    private void initAttributes() {
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mSchoolId = getIntent().getStringExtra(Extra.SCHOOL_ID);
        mWebApi = RsGenerator.create(WebApi.class);
        mCompositeDisposable = new ListCompositeDisposable();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color));
        List<StatusItem> statusItemList = new ArrayList<>(5);
        statusItemList.add(new StatusItem(getString(R.string.all), -1));
        statusItemList.add(new StatusItem(getString(R.string.status_await_handle), 1));
        statusItemList.add(new StatusItem(getString(R.string.status_handling), 2));
        statusItemList.add(new StatusItem(getString(R.string.status_handled), 3));
        statusItemList.add(new StatusItem(getString(R.string.status_accepted), 4));
        mStatusAdapter = new CheckedTextAdapter(statusItemList);
        mStatusAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public <E extends RepairFilterItem> void onItemClick(E item, int position) {
                if (position == 0) {
                    mStatusFilterTv.setText("处理状态（全部）");
                } else {
                    mStatusFilterTv.setText(item.content());
                }
                mLoader.addParam("status", Integer.toString(((StatusItem) item).getStatus()));
                mLoader.loadData(true);
                mFilterListFl.setVisibility(View.GONE);
            }
        });
        mClassroomAdapter = new CheckedTextAdapter();
        mClassroomAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public <E extends RepairFilterItem> void onItemClick(E item, int position) {
                if (position == 0) {
                    mClassroomFilterTv.setText("报修教室（全部）");
                } else {
                    mClassroomFilterTv.setText(item.content());
                }
                mLoader.addParam("classroomId", ((ClassroomFilterItem) item).getClassroomId());
                mLoader.loadData(true);
                mFilterListFl.setVisibility(View.GONE);
            }
        });
        mFilterListRv.setAdapter(mStatusAdapter);
        mFilterListRv.setLayoutManager(new LinearLayoutManager(this));
        mFilterListRv.addItemDecoration(new DividerItemDecoration(this));

        mFilterListFl.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFilterListFl.setVisibility(View.GONE);
                return true;
            }
        });

        Builder<RepairRecord, RepairRecordVh, Void> controllerBuilder = new Builder<>();
        mLoader = controllerBuilder
                .setActivity(this)
                .setRefreshLayout(mRefreshLayout)
                .setRecyclerView(mListRv)
                .setEmptyView(mEmptyTv)
                .setOnItemClickListener(new RecyclerAdapter.OnItemClickListener<RepairRecord>() {
                    @Override
                    public void onItemClick(int position, RepairRecord item) {
                        Cog.d(TAG, "onItemClick position=", position);
                        RepairDetailsActivity.start(SchoolRepairsActivity.this, item.getId());
                    }
                })
                .build();
        mLoader.showDivider();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        mLoader.loadData(true);
        Map<String, String> params = new HashMap<>();
        params.put("schoolId", mSchoolId);
        Disposable disposable = mWebApi.post4Json(URLConfig.GET_CLASSROOMS, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "load classrooms response=", response);
                        Type type = new TypeToken<List<ClassroomFilterItem>>(){}.getType();
                        List<ClassroomFilterItem> classroomList = new Gson()
                                .fromJson(response.optJSONArray("list").toString(), type);
                        classroomList.add(0, new ClassroomFilterItem(null, "", ""));
                        mClassroomAdapter.setItemList(classroomList);
                        if (mFilterListFl.getVisibility() == View.VISIBLE
                                && mClassroomFilterTv.isChecked()) {
                            mClassroomAdapter.notifyDataSetChanged();
                        }
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    @OnClick(R.id.btn_common_malfunction)
    public void onCommonMalfuncBtnClick(View view) {
        CommonMalfunctionsActivity.start(this);
    }

    @OnClick(R.id.fl_classroom_filter)
    public void onClassroomFilterClick(View view) {
        mStatusFilterTv.setChecked(false);
        if (mFilterListFl.getVisibility() == View.VISIBLE
                && mClassroomFilterTv.isChecked()) {
            mFilterListFl.setVisibility(View.GONE);
            mClassroomFilterTv.setChecked(false);
        } else if (mFilterListFl.getVisibility() == View.VISIBLE){
            mFilterListRv.setAdapter(mClassroomAdapter);
            mFilterListRv.scrollToPosition(mClassroomAdapter.getSelectedPosition());
            mClassroomFilterTv.setChecked(true);
        } else {
            if (mFilterListRv.getAdapter() != mClassroomAdapter) {
                mFilterListRv.setAdapter(mClassroomAdapter);
                mFilterListRv.scrollToPosition(mClassroomAdapter.getSelectedPosition());
            }
            mFilterListFl.setVisibility(View.VISIBLE);
            mClassroomFilterTv.setChecked(true);
        }
    }

    @OnClick(R.id.fl_status_filter)
    public void onStatusFilterClick(View view) {
        mClassroomFilterTv.setChecked(false);
        if (mFilterListFl.getVisibility() == View.VISIBLE
                && mStatusFilterTv.isChecked()) {
            mFilterListFl.setVisibility(View.GONE);
            mStatusFilterTv.setChecked(false);
        } else if (mFilterListFl.getVisibility() == View.VISIBLE){
            mFilterListRv.setAdapter(mStatusAdapter);
            mStatusFilterTv.setChecked(true);
        } else {
            if (mFilterListRv.getAdapter() != mStatusAdapter) {
                mFilterListRv.setAdapter(mStatusAdapter);
            }
            mFilterListFl.setVisibility(View.VISIBLE);
            mStatusFilterTv.setChecked(true);
        }
    }

    @OnClick(R.id.fab_report)
    public void onReportClick() {
        ReportRepairActivity.start(this);
    }

    @Override
    public String getUrl() {
        return URLConfig.GET_REPAIR_RECORDS;
    }

    @Override
    public List<RepairRecord> extractList(JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.optJSONArray("list").toString(),
                new TypeToken<List<RepairRecord>>() {
                }.getType());
    }

    @Override
    public ViewHolderCreator<RepairRecordVh> newViewHolderCreator() {
        return new EasyVhrCreator<>(RepairRecordVh.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public static void start(Context context, String schoolId) {
        Intent intent = new Intent(context, SchoolRepairsActivity.class);
        intent.putExtra(Extra.SCHOOL_ID, schoolId);
        context.startActivity(intent);
    }

}
