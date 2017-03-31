package com.codyy.erpsportal.commons.models.listeners;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.models.engine.LiveClassroomViewStuffer.OnLiveClassroomClickListener;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResClassroom;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页直播被点击了
 * Created by gujiajia on 2016/8/23.
 */
public class MainLiveClickListener implements OnLiveClassroomClickListener {
    private final static String TAG = "MainLiveClickListener";

    private Fragment mFragment;
    private UserInfo mUserInfo;

    public MainLiveClickListener(Fragment fragment,UserInfo userInfo) {
        mFragment = fragment;
        mUserInfo = userInfo;
    }

    @Override
    public void onLiveClassroomClick(final MainResClassroom liveClassroom) {
        Cog.d(TAG, "onLiveClassroomClick liveClassroom=" + liveClassroom.getSchoolName());
        RequestSender requestSender = new RequestSender(mFragment.getContext());
        Map<String, String> params = new HashMap<>();
        params.put("id", liveClassroom.getId());
        params.put("liveType", liveClassroom.getType());
        MainActivity mainActivity = (MainActivity) mFragment.getActivity();
        if (mainActivity == null) return;
        UserInfo userInfo = mainActivity.getUserInfo();
        if (userInfo == null) return;
        params.put("uuid", userInfo.getUuid());
        requestSender.sendRequest(new RequestData(URLConfig.MAIN_LIVE_PERMISSION, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onLiveClassroomClick response=", response);
                if ("success".equals(response.optString("result"))){
                    boolean canView = response.optBoolean("canView");
                    if (canView) {
                        if (MainResClassroom.TYPE_LIVE.equals(liveClassroom.getType())) {
                            ClassRoomDetailActivity.startActivity(mFragment.getActivity(),mUserInfo,
                                    liveClassroom.getId(), ClassRoomContants.TYPE_LIVE_LIVE,liveClassroom.getSubjectName());
                        } else {
                            ClassRoomDetailActivity.startActivity(mFragment.getActivity(),mUserInfo,
                                    liveClassroom.getId(), ClassRoomContants.TYPE_CUSTOM_LIVE,liveClassroom.getSubjectName());
                        }
                    } else {
                        String msg = response.optString("msg");
                        if (TextUtils.isEmpty(msg)) {
                            Toast.makeText(mFragment.getContext(), "获取权限失败！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mFragment.getContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(mFragment.getContext(), "获取权限失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "onLiveClassroomClick error=", error);
                Toast.makeText(mFragment.getContext(), "获取权限失败！", Toast.LENGTH_SHORT).show();
            }
        }));
    }
}
