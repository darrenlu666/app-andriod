package com.codyy.erpsportal.exam.controllers.activities.school;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TabsActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.exam.controllers.fragments.school.OverallStatisticsFragment;
import com.codyy.erpsportal.exam.controllers.fragments.school.StudentStatisticsFragment;
import com.codyy.erpsportal.exam.controllers.fragments.school.TopicStatisticFragment;
import com.codyy.erpsportal.exam.models.entities.ExamClass;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eachann on 2016/1/13.
 */
public class SchoolClassDetailActivity extends TabsActivity {
    private static final String TAG = SchoolClassDetailActivity.class.getSimpleName();
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
        Map<String, String> map = new HashMap<>();
        map.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        map.put("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
        RequestSender sender = new RequestSender(this);
        sender.sendRequest(new RequestSender.RequestData(getIntent().getStringExtra(EXTRA_NO_EXAM_DETAIL) != null?URLConfig.GET_EXAM_CLASS_LIST:URLConfig.GET_CLASS_LIST, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result")) && mToolbar != null) {
                    List<ExamClass> classList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = response.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            if (!object.isNull("classRoomId") || !object.isNull("classRoomName") || !object.isNull("classId") || !object.isNull("classLevalName")) {
                                ExamClass examClass = new ExamClass();
                                examClass.setClassRoomId(getIntent().getStringExtra(EXTRA_NO_EXAM_DETAIL) != null?object.optString("classId"):object.optString("classRoomId"));
                                examClass.setClassRoomName(getIntent().getStringExtra(EXTRA_NO_EXAM_DETAIL) != null?object.optString("classLevalName")+object.optString("className"):object.optString("classRoomName"));
                                classList.add(examClass);
                            }
                        }
                        if (classList != null && classList.size() > 0) {
                            Bundle bundle = new Bundle();
                            bundle.putString(OverallStatisticsFragment.ARG_EXAM_TASK_ID, getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                            bundle.putParcelableArrayList("ARG_LIST", (ArrayList<? extends Parcelable>) classList);
                            addFragment(getString(R.string.exam_overall_statistics), OverallStatisticsFragment.class, bundle);
                            addFragment(getString(R.string.exam_topic_statistics), TopicStatisticFragment.class, bundle);
                            addFragment(getString(R.string.exam_student_statistics), StudentStatisticsFragment.class, bundle);
                            hideTabsIfNeeded();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
            }
        }));
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

    public static void startActivity(Context from, String title, String taskId, String id, String state) {
        Intent intent = new Intent(from, SchoolClassDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_EXAM_TASK_ID, taskId);
        intent.putExtra(EXTRA_EXAM_ID, id);
        if (state != null)
            intent.putExtra(EXTRA_NO_EXAM_DETAIL, state);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }
}
