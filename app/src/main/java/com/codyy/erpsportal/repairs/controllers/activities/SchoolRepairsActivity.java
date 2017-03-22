package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.Builder;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.ListExtractor;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.repairs.controllers.activities.SchoolRepairsActivity.RepairRecordVh;
import com.codyy.erpsportal.repairs.models.entities.RepairRecord;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 学校报修记录
 */
public class SchoolRepairsActivity extends AppCompatActivity implements ListExtractor<RepairRecord, RepairRecordVh> {

    @Bind(R.id.btn_common_malfunction)
    Button mCommonMalfuncBtn;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.rv_list)
    RecyclerView mListRv;

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    private UserInfo mUserInfo;

    private String mSchoolId;

    private RvLoader<RepairRecord, RepairRecordVh, Void> mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_repairs);
        ButterKnife.bind(this);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color));
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mSchoolId = getIntent().getStringExtra(Extra.SCHOOL_ID);
        RvLoader.Builder<RepairRecord, RepairRecordVh, Void> controllerBuilder = new Builder<>();
        mLoader = controllerBuilder
                .setActivity(this)
                .setRefreshLayout(mRefreshLayout)
                .setRecyclerView(mListRv)
                .setEmptyView(mEmptyTv)
                .build();
        mLoader.showDivider();
        mLoader.loadData(true);
    }

    @OnClick(R.id.btn_common_malfunction)
    public void onCommonMalfuncBtnClick() {

    }

    @Override
    public String getUrl() {
        return URLConfig.GET_REPAIR_RECORDS;
    }

    @Override
    public List<RepairRecord> extractList(JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.optJSONArray("list").toString(),
                new TypeToken<List<RepairRecord>>(){}.getType());
    }

    @Override
    public ViewHolderCreator<RepairRecordVh> newViewHolderCreator() {
        return new EasyVhrCreator<>(RepairRecordVh.class);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SchoolRepairsActivity.class);
        context.startActivity(intent);
    }

    @LayoutId(R.layout.item_repair_record)
    public static class RepairRecordVh extends BindingRvHolder<RepairRecord>{

        @Bind(R.id.tv_classroom)
        TextView mClassroomTv;

        public RepairRecordVh(View itemView) {
            super(itemView);
        }

        @Override
        public void setDataToView(RepairRecord repairRecord) {
            mClassroomTv.setText(repairRecord.getClassroomName());
        }
    }
}
