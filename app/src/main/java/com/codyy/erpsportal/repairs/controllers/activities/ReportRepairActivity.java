package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter;
import com.codyy.erpsportal.repairs.controllers.fragments.SelectCategoriesDgFragment;
import com.codyy.erpsportal.repairs.widgets.ImageItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter.REQUEST_CODE_ADD_IMAGES;

/**
 * 报修
 */
public class ReportRepairActivity extends AppCompatActivity {

    private static final int MAX_IMAGES = 10;

    @Bind(R.id.btn_commit)
    Button mCommitBtn;

    @Bind(R.id.tv_classroom)
    TextView mClassroomTv;

    @Bind(R.id.iv_scan_serial)
    ImageView mScanSerialIv;

    @Bind(R.id.ll_classroom)
    LinearLayout mClassroomLl;

    @Bind(R.id.tv_categories)
    TextView mCategoriesTv;

    @Bind(R.id.et_reporter)
    EditText mReporterEt;

    @Bind(R.id.et_phone)
    EditText mPhoneEt;

    @Bind(R.id.tv_lb_description)
    TextView mDescriptionLbTv;

    @Bind(R.id.et_desc)
    EditText mDescEt;

    @Bind(R.id.rv_images)
    RecyclerView mImagesRv;

    private RepairImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_repair);
        ButterKnife.bind(this);
        mAdapter = new RepairImageAdapter(MAX_IMAGES);
        mImagesRv.setAdapter(mAdapter);
        mImagesRv.addItemDecoration(new ImageItemDecoration(UIUtils.dip2px(this, 5), 4));
        mImagesRv.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_IMAGES && resultCode == RESULT_OK) {
            mAdapter.processResultData(data);
        }
    }

    @OnClick(R.id.ll_malfunction_categories)
    public void onMalfunctionCategoriesClick() {
        SelectCategoriesDgFragment fragment = new SelectCategoriesDgFragment();
        fragment.show(getSupportFragmentManager(), "selectCategories");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ReportRepairActivity.class);
        context.startActivity(intent);
    }
}
