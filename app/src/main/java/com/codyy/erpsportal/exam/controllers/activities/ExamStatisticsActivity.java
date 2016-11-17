package com.codyy.erpsportal.exam.controllers.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.exam.controllers.fragments.student.NormalStatisticsFragment;
import com.codyy.erpsportal.exam.controllers.fragments.student.SelfStatisticsFragment;

import butterknife.Bind;

/**
 * 统计
 * Created by eachann on 2016/2/26.
 */
public class ExamStatisticsActivity extends ToolbarActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ("SELF".equals(getIntent().getStringExtra(EXTRA_TYPE))) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_exam_statstics_content, SelfStatisticsFragment.getInstance(getIntent().getStringExtra(EXTRA_DATA)))
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_exam_statstics_content, NormalStatisticsFragment.getInstance(getIntent().getStringExtra(EXTRA_DATA)))
                    .commit();

        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_exam_statistics_layout;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
        mTitle.setText(getString(R.string.exam_statistics_title));
    }
}
