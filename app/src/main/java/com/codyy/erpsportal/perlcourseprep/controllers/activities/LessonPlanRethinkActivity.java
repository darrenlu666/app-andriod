package com.codyy.erpsportal.perlcourseprep.controllers.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.services.SaveLessonPlanRethinkService;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.perlcourseprep.models.entities.LessonPlanDetails;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LessonPlanRethinkActivity extends AppCompatActivity {

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.et_rethink)
    EditText mRethinkEt;

    private LessonPlanDetails mLessonPlanDetails;

    private UserInfo mUserInfo;

    private BroadcastReceiver mRethinkUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String lessonPlanId = intent.getStringExtra(SaveLessonPlanRethinkService.EXTRA_LESSON_PLAN_ID);
            String rethinkContent = intent.getStringExtra(SaveLessonPlanRethinkService.EXTRA_CONTENT);
            if (lessonPlanId.equals(mLessonPlanDetails.getLessonPlanId())) {
                mLessonPlanDetails.setRethink(rethinkContent);
                mRethinkEt.setText(mLessonPlanDetails.getRethink());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_plan_rethink);
        ButterKnife.bind(this);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mLessonPlanDetails = getIntent().getParcelableExtra(LessonPlanDetails.EXTRA_LESSON_PLAN_DETAILS);
        if (mLessonPlanDetails == null || mUserInfo == null){
            finish();
            return;
        }
        mRethinkEt.setText(mLessonPlanDetails.getRethink());
        if (mUserInfo.getBaseUserId().equals(mLessonPlanDetails.getBaseUserId())) {
            mRethinkEt.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String newRethinkContent = mRethinkEt.getText().toString();
        //反思内容有变动，退出时更新反思
        if (!newRethinkContent.equals(mLessonPlanDetails.getRethink())) {
            SaveLessonPlanRethinkService.start(this, mUserInfo.getUuid(), mLessonPlanDetails.getLessonPlanId(), newRethinkContent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter intentFilter = new IntentFilter(SaveLessonPlanRethinkService.ACTION_UPDATE_RETHINK);
        localBroadcastManager.registerReceiver(mRethinkUpdateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManager.unregisterReceiver(mRethinkUpdateReceiver);
    }

    public static void start(Activity activity, UserInfo userInfo, LessonPlanDetails lessonPlanDetails){
        Intent intent = new Intent(activity, LessonPlanRethinkActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(LessonPlanDetails.EXTRA_LESSON_PLAN_DETAILS, lessonPlanDetails);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
