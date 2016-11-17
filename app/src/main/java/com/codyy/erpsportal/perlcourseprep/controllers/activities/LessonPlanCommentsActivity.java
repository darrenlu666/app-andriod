package com.codyy.erpsportal.perlcourseprep.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.SendMsgDialog;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.perlcourseprep.controllers.fragments.LessonPlanCommentsNewFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人备课评论
 * Created by gujiajia on 2016/2/1.
 */
public class LessonPlanCommentsActivity extends AppCompatActivity {

    private final static String TAG = "LessonPlanCommentsActivity";

    private final static String EXTRA_LESSON_PLAN_ID = "com.codyy.erpsportal.lesson_plan_id";

    private static final String EXTRA_RATE = "com.codyy.erpsportal.lesson_plan_rate";

//    private LessonPlanCommentsFragment mCommentListFragment;

    private String mLessonPlanId;

    private UserInfo mUserInfo;

    private float mRate;

    private LoadingDialog mLoadingDialog;

    private SendMsgDialog mSendCommentDialog;

    private RequestSender mRequestSender;

    private SendMsgDialog.OnSendClickListener mOnSendClickListener = new SendMsgDialog.OnSendClickListener() {
        @Override
        public void onSendClick(String msg) {
            sendComment(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_plan_comments);

        mRequestSender = new RequestSender(this);
        mLessonPlanId = getIntent().getStringExtra(EXTRA_LESSON_PLAN_ID);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mRate = getIntent().getFloatExtra(EXTRA_RATE, 0f);
        LessonPlanCommentsNewFragment fragment = LessonPlanCommentsNewFragment
                .newInstance(mUserInfo, mLessonPlanId, mRate);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss();
    }

    public void onCommentClick(View view) {
        Cog.d(TAG, "onCommentClick");
        if (mSendCommentDialog == null) {
            mSendCommentDialog = SendMsgDialog.newInstance();
            mSendCommentDialog.setOnSendClickListener(mOnSendClickListener);
        }
        if (!mSendCommentDialog.isVisible()) {
            mSendCommentDialog.show(getSupportFragmentManager(), "SendMsgDialog");
        }
    }

    public static void start(Activity activity, UserInfo userInfo, String lessonPlanId, float rate) {
        Intent intent = new Intent(activity, LessonPlanCommentsActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_LESSON_PLAN_ID, lessonPlanId);
        intent.putExtra(EXTRA_RATE, rate);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    /**
     * 发送评论
     *
     * @param comment
     */
    private void sendComment(String comment) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("lessonPlanId", mLessonPlanId);
        params.put("content", comment);
        Cog.d(TAG, "sendComment params = " + params);
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog.newInstance(R.string.sending);
        }
        mLoadingDialog.show(getSupportFragmentManager(), "sending");
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.LESSON_PLAN_ADD_COMMENT, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "sendComment response:" + response);
                String replyMsg = response.optString("message");
                UIUtils.toast(LessonPlanCommentsActivity.this, replyMsg, Toast.LENGTH_SHORT);
                if ("success".equals(response.optString("result"))) {
                    mLoadingDialog.dismiss();
                    mSendCommentDialog.dismiss();
//                    mCommentListFragment.loadData(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "sendComment error:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                mLoadingDialog.dismiss();
            }
        }, true/*需要显示提交画面*/, false/*无需重试*/));
    }
}
