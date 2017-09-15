package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.ChannelFragment;
import com.codyy.erpsportal.commons.controllers.fragments.ChannelFragment.TitleBarRiseListener;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.mainpage.TaiZhouPanel;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * (v5.3.7)台州首页
 * Created by poe on 2017/9/1.
 */
public class TaiZhouFragment extends Fragment implements TitleBarRiseListener {

    private final static String TAG = "TaiZhouFragment";

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

    @Bind(R.id.tv_resource_count)
    TextView mResourceCountTv;

    @Bind(R.id.tv_net_teach_count)
    TextView mNetTeachCountTv;

    private Handler mHandler;

    private OnModuleConfigListener mOnModuleConfigListener = new OnModuleConfigListener() {
        @Override
        public void onConfigLoaded(ModuleConfig config) {
            loadAreaMapAndInfo(config.getAreaCode());
            loadHomePageCount(config.getAreaCode());
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        ChannelFragment channelFragment = (ChannelFragment) getParentFragment();
        channelFragment.setTitleRiseListener(this);
        ConfigBus.register(mOnModuleConfigListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_tai_zhou, container, false);
            ButterKnife.bind(this, mRootView);
            initWebView();
        } else {
            mPanelTitleTv.setTranslationY(-mVerticalOffset);
            mDataPanelGl.setTranslationY(-mVerticalOffset);
        }
        return mRootView;
    }

    /**
     * 标题竖向偏移
     */
    private int mVerticalOffset;

    @Override
    public void notifyRise(int verticalOffset) {
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
    public void loadHomePageCount(String areaCode) {
        Cog.d(TAG, "+loadHomePageCount areaCode=" + areaCode);
        if (TextUtils.isEmpty(areaCode)) return;
        WebApi webApi = RsGenerator.create(WebApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("areaCode", areaCode);
        webApi.post4Json(URLConfig.PANEL_DATA_TZ, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadHomePageCount:" + response);
                        if ("success".equals(response.optString("result"))) {
                            TaiZhouPanel coursesProfile = new Gson().fromJson(response.optJSONObject("data").toString(),
                                    TaiZhouPanel.class);
                            setCountDataToViews(coursesProfile);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "onErrorResponse:" + error);
                        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                    }
                });
    }

    @JavascriptInterface
    public void jumpIntoArea() {
        Cog.d(TAG, "jumpIntoArea");
    }

    private void loadAreaMapAndInfo(String areaCode) {
        Cog.d(TAG, "loadAreaMapAndInfo url=", URLConfig.URL_MAP_TZ);
        if (mMapView != null) {
            mMapView.loadUrl(URLConfig.URL_MAP_TZ+"?areaCode="+areaCode);
        }
    }

    /**
     * 将统计数据设置到
     *
     * @param coursesProfile 统计数据
     */
    private void setCountDataToViews(TaiZhouPanel coursesProfile) {
        if (mPanelTitleTv == null) return;//界面没初始化好，防止fc
        mPanelTitleTv.setText(coursesProfile.getAreaName());
        mSchoolCountTv.setText(coursesProfile.getScheduleCount() + "");
        mClassroomCountTv.setText(coursesProfile.getScheduleCount() + "");
        mResourceCountTv.setText(coursesProfile.getResourceCount() + "");
        mNetTeachCountTv.setText(coursesProfile.getNetTeachCount() + "");
    }
}
