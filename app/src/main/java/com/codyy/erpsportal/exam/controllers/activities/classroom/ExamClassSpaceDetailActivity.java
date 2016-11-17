package com.codyy.erpsportal.exam.controllers.activities.classroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TabsActivity;
import com.codyy.erpsportal.exam.controllers.activities.school.SchoolGradeDetailActivity;
import com.codyy.erpsportal.exam.controllers.fragments.classspace.OverallStatisticsFragment;
import com.codyy.erpsportal.exam.controllers.fragments.classspace.StudentStatisticsFragment;
import com.codyy.erpsportal.exam.controllers.fragments.classspace.TopicStatisticFragment;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * Created by eachann on 2016/1/13.
 */
public class ExamClassSpaceDetailActivity extends TabsActivity {
    private static final String TAG = ExamClassSpaceDetailActivity.class.getSimpleName();
    private Context mContext = this;

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setViewAnim(false, mTitle);
        if (getIntent().getStringExtra(EXTRA_TITLE) != null)
            setCustomTitle(getIntent().getStringExtra(EXTRA_TITLE));
    }

    /**
     * 调用addFragment方法，把Fragment加入
     */
    @Override
    protected void addFragments() {
        if (getIntent().getStringExtra(EXTRA_CLASS_ID) != null) {
            Bundle bundle = new Bundle();
            bundle.putString(com.codyy.erpsportal.exam.controllers.fragments.school.OverallStatisticsFragment.ARG_EXAM_TASK_ID, getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
            bundle.putString("ARG_CLASS_ID", getIntent().getStringExtra(EXTRA_CLASS_ID));
            addFragment(getString(R.string.exam_overall_statistics), OverallStatisticsFragment.class, bundle);
            addFragment(getString(R.string.exam_topic_statistics), TopicStatisticFragment.class, bundle);
            addFragment(getString(R.string.exam_student_statistics), StudentStatisticsFragment.class, bundle);
            hideTabsIfNeeded();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getStringExtra(EXTRA_NO_EXAM_DETAIL) != null) {
            return super.onCreateOptionsMenu(menu);
        }
        getMenuInflater().inflate(R.menu.menu_task_button, menu);
        MenuItem menuItem = menu.findItem(R.id.task_read_menu);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        TextView textView = (TextView) linearLayout.findViewById(R.id.task_title);
        textView.setText(getString(R.string.exam_detail));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, SchoolGradeDetailActivity.class);
                intent.putExtra(EXTRA_TITLE, mTitle.getText().toString());
                intent.putExtra(EXTRA_EXAM_IS_ARRANGE, false);
                intent.putExtra(EXTRA_EXAM_TASK_ID, getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                intent.putExtra(EXTRA_TYPE, 1);
                intent.putExtra(EXTRA_TASK_URL, URLConfig.CLASS_TEST_EXAM_DETAIL);
                startActivity(intent);
                UIUtils.addEnterAnim((Activity) mContext);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private static final String EXTRA_CLASS_ID = "EXTRA_CLASS_ID";

    public static void startActivity(Context from, String title, String taskId, String id, String state, String classId) {
        Intent intent = new Intent(from, ExamClassSpaceDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_EXAM_TASK_ID, taskId);
        intent.putExtra(EXTRA_EXAM_ID, id);
        intent.putExtra(EXTRA_CLASS_ID, classId);
        if (state != null)
            intent.putExtra(EXTRA_NO_EXAM_DETAIL, state);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }
}
