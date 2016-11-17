package com.codyy.erpsportal.homework.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;

import butterknife.Bind;

/**
 * 作业信息
 * Created by ldh on 2016/6/12.
 */
public class WorkInfoActivity extends ToolbarActivity {

    public static final String EXTRA_WORK_ID = "workId";

    @Bind(R.id.toolbar_title)
    protected TextView mTitle;
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.tv_work_info_subject)
    protected TextView mTvSubject;
    @Bind(R.id.tv_work_info_reader)
    protected TextView mTvReader;
    @Bind(R.id.tv_work_info_submit)
    protected TextView mTvSubmit;
    @Bind(R.id.tv_work_info_not_submit)
    protected TextView mTvNotSubmit;
    @Bind(R.id.tv_work_info_assign_time)
    protected TextView mTvAssignTime;
    @Bind(R.id.tv_work_info_deadline)
    protected TextView mTvDeadline;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_work_info;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        mTitle.setText(R.string.work_info_title);

    }

    public static void startActivity(Context context,String workId){
        Intent intent = new Intent(context,WorkInfoActivity.class);
        intent.putExtra(EXTRA_WORK_ID,workId);
        context.startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close,0);
    }
}
