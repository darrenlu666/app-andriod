package com.codyy.erpsportal.tutorship.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.url.URLConfig;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 辅导详情界面
 * Created by gujiajia on 2015/12/29.
 */
public class TutorshipDetailsActivity extends ToolbarActivity {

    private final static String TAG = "TutorshipDetailsActivity";

    private final static String EXTRA_TUTORSHIP_ID = "com.codyy.erpsportal.TUTORSHIP_ID";

    private final static String EXTRA_USER_INFO = "com.codyy.erpsportal.USER_INFO";

    private UserInfo mUserInfo;

    private String mTutorshipId;

    @Bind(R.id.toolbar_title)
    TextView mToolbarTitleTv;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.tv_creator)
    TextView mCreatorTv;

    @Bind(R.id.tv_create_time)
    TextView mCreateTimeTv;

    @Bind(R.id.tv_tutorship_title)
    TextView mTutorshipTitleTv;

    @Bind(R.id.tv_begin_time)
    TextView mBeginTimeTv;

    @Bind(R.id.tv_lb_grade)
    TextView mGradeLbTv;

    @Bind(R.id.tv_grade)
    TextView mGradeTv;

    @Bind(R.id.tv_lb_subject)
    TextView mSubjectLbTv;

    @Bind(R.id.tv_subject)
    TextView mSubjectTv;

    @Bind(R.id.tv_lb_speaker)
    TextView mSpeakerLbTv;

    @Bind(R.id.tv_speaker)
    TextView mSpeakerTv;

    @Bind(R.id.tv_lb_assistant)
    TextView mAssistantLbTv;

    @Bind(R.id.tv_assistant)
    TextView mAssistantTv;

    @Bind(R.id.tv_lb_tutorship_summary)
    TextView mTutorshipSummaryLbTv;

    @Bind(R.id.tv_tutorship_summary)
    TextView mTutorshipSummaryTv;

    @Bind(R.id.tv_lb_register_situation)
    TextView mRegisterSituationLbTv;

    @Bind(R.id.tv_lb_registers)
    TextView mRegistersLbTv;

    @Bind(R.id.tv_registers)
    TextView mRegistersTv;

    @Bind(R.id.tv_lb_unregisters)
    TextView mUnregistersLbTv;

    @Bind(R.id.tv_unregisters)
    TextView mUnregistersTv;

    @Bind(R.id.tv_status)
    TextView mStatusTv;

    private RequestSender mRequestSender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolbarTitleTv.setText(getString(R.string.tutors_ship_detail_title));
        mUserInfo = getIntent().getParcelableExtra(EXTRA_USER_INFO);
        mTutorshipId = getIntent().getStringExtra(EXTRA_TUTORSHIP_ID);
        mRequestSender = new RequestSender(this);
        mSpeakerLbTv.setText(Titles.sMasterTeacher);
        loadData();
    }

    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("meetingId", mTutorshipId);
        mRequestSender.sendRequest(new RequestData(URLConfig.GET_TUTORSHIP_DETAILS, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadData response=", response);
//                if ("success".equals(response.optString("result"))){
                try {
                    JSONObject meetingObj = response.optJSONObject("meetingDetail");
                    mCreatorTv.setText(getString(R.string.tutors_ship_detail_create_real_name, meetingObj.optString("createRealName")));
                    long createTime = meetingObj.optLong("createTime");
                    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
                    mCreateTimeTv.setText(getString(R.string.tutors_ship_detail_create_time, createTime > 0 ? dateTimeFormatter.print(createTime) : ""));
                    mTutorshipTitleTv.setText(meetingObj.optString("meetingTitle"));
                    long beginTime = meetingObj.optLong("beginTime");
                    mBeginTimeTv.setText(getString(R.string.tutors_ship_detail_begin_time, beginTime > 0 ? dateTimeFormatter.print(beginTime) : ""));
                    mGradeTv.setText(meetingObj.optString("baseClasslevelName"));
                    mSubjectTv.setText(meetingObj.optString("baseSubjectName"));
                    mSpeakerTv.setText(meetingObj.optString("mainSpeakerUserName"));
                    if (!meetingObj.isNull("meetingDescription")) {
                        mTutorshipSummaryTv.setText(meetingObj.optString("meetingDescription"));
                    } else {
                        mTutorshipSummaryTv.setText("");
                    }

                    if (!meetingObj.isNull("assistantUserName")) {
                        mAssistantLbTv.setVisibility(View.VISIBLE);
                        mAssistantTv.setVisibility(View.VISIBLE);
                        mAssistantTv.setText(meetingObj.optString("assistantUserName"));
                    }

                    JSONArray registersArr = response.optJSONArray("registers");
                    JSONArray unregistersArr = response.optJSONArray("unregisters");
                    mRegistersLbTv.setText(getString(R.string.tutors_ship_detail_registers, registersArr.length()));
                    mUnregistersLbTv.setText(getString(R.string.tutors_ship_detail_unregisters, unregistersArr.length()));
                    mRegistersTv.setText(jsonArray2Str(registersArr));
                    mUnregistersTv.setText(jsonArray2Str(unregistersArr));
                    mStatusTv.setText(meetingObj.optString("chineseStatus"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "loadData error=", error);
            }
        }, TAG));
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(TAG);
        super.onDestroy();
    }

    private String jsonArray2Str(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            sb.append(jsonArray.optString(i)).append("  ");
        }
        return sb.toString();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_tutorship_details;
    }

    @Override
    protected void initToolbar() {
        initToolbar(mToolbar);
    }

    public static void start(Activity activity, UserInfo userInfo, String tutorshipId) {
        Intent intent = new Intent(activity, TutorshipDetailsActivity.class);
        intent.putExtra(EXTRA_USER_INFO, userInfo);
        intent.putExtra(EXTRA_TUTORSHIP_ID, tutorshipId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
