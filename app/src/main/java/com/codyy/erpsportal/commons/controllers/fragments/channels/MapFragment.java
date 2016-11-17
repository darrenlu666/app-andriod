package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.ChannelFragment;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.mainpage.TianJinCoursesProfile;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 天津课堂模板
 * Created by gujiajia on 2016/7/19.
 */
public class MapFragment extends Fragment{

    private final static String TAG = "MapFragment";

    private View mRootView;

    @Bind(R.id.wv_area)
    WebView mMapView;

    @Bind(R.id.tv_panel_title)
    TextView mPanelTitleTv;

    @Bind(R.id.gl_data_panel)
    GridLayout mDataPanelGl;

    @Bind(R.id.tv_school_count)
    TextView mSchoolCountTv;

    @Bind(R.id.tv_classroom_count)
    TextView mClassroomCountTv;

    @Bind(R.id.tv_teacher_count)
    TextView mTeacherCountTv;

    @Bind(R.id.tv_student_count)
    TextView mStudentCountTv;

    private Handler mHandler;

    private OnModuleConfigListener mOnModuleConfigListener = new OnModuleConfigListener() {
        @Override
        public void onConfigLoaded(ModuleConfig config) {
            loadAreaMapAndInfo(config.getBaseAreaId());
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        ChannelFragment channelFragment = (ChannelFragment) getParentFragment();
        channelFragment.setMapFragment(this);
        ConfigBus.register(mOnModuleConfigListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_map, container, false);
            ButterKnife.bind(this, mRootView);
            initWebView();
        } else {
            mPanelTitleTv.setTranslationY( -mVerticalOffset);
            mDataPanelGl.setTranslationY( -mVerticalOffset);
        }
        return mRootView;
    }

    /**
     * 标题竖向偏移
     */
    private int mVerticalOffset;

    public void notifyParentRise(int verticalOffset) {
        mVerticalOffset = verticalOffset;
        mPanelTitleTv.setTranslationY(-verticalOffset);
        mDataPanelGl.setTranslationY(-verticalOffset);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(mRootView);
        ConfigBus.unregister(mOnModuleConfigListener);
    }

    private void initWebView() {
        mMapView.getSettings().setSupportZoom(true);
        mMapView.getSettings().setBuiltInZoomControls(true);
        mMapView.getSettings().setUseWideViewPort(true);
        mMapView.getSettings().setLoadWithOverviewMode(true);
        mMapView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            mMapView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.TEXT_AUTOSIZING);
        }
        mMapView.setBackgroundColor(0x00ffffff);
        if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
            Drawable bgDrawable = mMapView.getBackground();
            if (bgDrawable != null) bgDrawable.setAlpha(0);
        }
        mMapView.getSettings().setJavaScriptEnabled(true);
        mMapView.addJavascriptInterface(this, "android");
        mMapView.getSettings().setDisplayZoomControls(false);
    }

    /**
     * 加载统计数据
     */
    @JavascriptInterface
    public void loadHomePageCount(String areaId) {
        Cog.d(TAG, "+loadHomePageCount areaId=", areaId);
        if (areaId.matches("^\\d+$")) {//非天津的传入的是数字地区码，
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPanelTitleTv.setVisibility(View.GONE);
                    mDataPanelGl.setVisibility(View.GONE);
                }
            });
        } else {//回到天津时传入areaId为undefined
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPanelTitleTv.setVisibility(View.VISIBLE);
                    mDataPanelGl.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @JavascriptInterface
    public void jumpIntoArea() {
        Cog.d(TAG, "jumpIntoArea");
    }

    private void loadAreaMapAndInfo(String areaId) {
        Cog.d(TAG, "loadAreaMapAndInfo url=", URLConfig.URL_MAP);
        if (mMapView != null) {
            mMapView.loadUrl(URLConfig.URL_MAP);
        }
        RequestQueue requestQueue = RequestManager.getRequestQueue();
        Map<String, String> params = new HashMap<>();
        params.put("baseAreaId",areaId);
        requestQueue.add(new NormalPostRequest(
                URLConfig.PANEL_DATA, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadHomePageCount:" + response);
                if ("success".equals(response.optString("result"))) {
                    Gson gson = new Gson();
                    TianJinCoursesProfile coursesProfile = gson.fromJson(response.toString(),
                            TianJinCoursesProfile.class);
                    if (mSchoolCountTv == null) return;
                    mSchoolCountTv.setText(coursesProfile.getSchoolCount() + "");
                    mClassroomCountTv.setText(coursesProfile.getClassroomCount() + "");
                    mStudentCountTv.setText(coursesProfile.getStudentCount() + "");
                    mTeacherCountTv.setText(coursesProfile.getTeacherCount() + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
            }
        }).setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f)));
    }
}
