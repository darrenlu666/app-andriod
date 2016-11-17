package com.codyy.erpsportal.commons.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.utils.Cog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 更改个人备课反思
 */
public class SaveLessonPlanRethinkService extends IntentService {

    private static final String TAG = "SaveLessonPlanRethinkService";

    public static final String ACTION_UPDATE_RETHINK = "com.codyy.erpsportal.services.action.UPDATE_RETHINK";

    private static final String EXTRA_UUID = "com.codyy.erpsportal.services.extra.UUID";

    public static final String EXTRA_LESSON_PLAN_ID = "com.codyy.erpsportal.services.extra.LESSON_PLAN_ID";

    public static final String EXTRA_CONTENT = "com.codyy.erpsportal.services.extra.CONTENT";

    public SaveLessonPlanRethinkService() {
        super("SaveLessonPlanRethinkService");
    }

    /**
     * 启动服务
     */
    public static void start(Context context, String uuid, String lessonPlanId, String content) {
        Intent intent = new Intent(context, SaveLessonPlanRethinkService.class);
        intent.setAction(ACTION_UPDATE_RETHINK);
        intent.putExtra(EXTRA_UUID, uuid);
        intent.putExtra(EXTRA_LESSON_PLAN_ID, lessonPlanId);
        intent.putExtra(EXTRA_CONTENT, content);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_RETHINK.equals(action)) {
                final String uuid = intent.getStringExtra(EXTRA_UUID);
                final String lessonPlanId = intent.getStringExtra(EXTRA_LESSON_PLAN_ID);
                final String content = intent.getStringExtra(EXTRA_CONTENT);
                commitLessonPlanRethink(uuid, lessonPlanId, content);
            }
        }
    }

    /**
     *
     */
    private void commitLessonPlanRethink(String uuid,final String lessonPlanId,final String content) {
        RequestQueue requestQueue = RequestManager.getRequestQueue();
        Map<String, String> params = new HashMap<>();
        params.put("uuid", uuid);
        params.put("lessonPlanId", lessonPlanId);
        params.put("rethink", content);
        requestQueue.add(new NormalPostRequest(URLConfig.UPDATE_LESSON_PLAN_RETHINK, params,
            new Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Cog.d(TAG, "commitLessonPlanRethink response=", response);
                    if ("success".equals(response.optString("result"))){
                        Toast.makeText(SaveLessonPlanRethinkService.this, response.optString("message"),Toast.LENGTH_SHORT).show();
                        broadcast(lessonPlanId, content);
                    }
                }
            }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "commitLessonPlanRethink error=", error);
                Toast.makeText(SaveLessonPlanRethinkService.this, R.string.save_rethink_failed,Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void broadcast(String lessonPlanId,String content) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        Intent intent = new Intent(ACTION_UPDATE_RETHINK);
        intent.putExtra(EXTRA_LESSON_PLAN_ID, lessonPlanId);
        intent.putExtra(EXTRA_CONTENT, content);
        localBroadcastManager.sendBroadcast(intent);
    }
}
