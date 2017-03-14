package com.codyy.erpsportal.commons.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.EvaluationCommentFragment;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;

/**
 * 评课议课所有评论
 * Created by kmdai on 2015/4/30.
 */
public class EvaluationAllActivity extends AppCompatActivity {

    private AssessmentDetails assessmentDetails;
    private EvaluationCommentFragment mEvaluationCommentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluation_all_layout);
        assessmentDetails = getIntent().getParcelableExtra("assessmentDetails");
//        comments = getIntent().getParcelableArrayListExtra("mComments");
        mEvaluationCommentFragment = EvaluationCommentFragment.newInstance(assessmentDetails);
        getSupportFragmentManager().beginTransaction().replace(R.id.comment_fragment, mEvaluationCommentFragment).commit();
    }


    public void onBackClick(View view) {
        this.finish();
        overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
