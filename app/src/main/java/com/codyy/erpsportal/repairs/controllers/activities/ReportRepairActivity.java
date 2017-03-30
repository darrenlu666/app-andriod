package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.repairs.controllers.fragments.SelectCategoriesDgFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 报修
 */
public class ReportRepairActivity extends AppCompatActivity {

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

    @Bind(R.id.ll_photos_container)
    LinearLayout mPhotosContainerLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_repair);
        ButterKnife.bind(this);
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
