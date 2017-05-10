package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
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

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.ChannelFragment;
import com.codyy.erpsportal.commons.controllers.fragments.ChannelFragment.TitleBarRiseListener;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 费县模板
 * Created by gujiajia on 2016/7/19.
 */
public class FeiXianFragment extends Fragment implements TitleBarRiseListener {

    private final static String TAG = "FeiXianFragment";

    private View mRootView;

    @Bind(R.id.wv_area)
    WebView mMapView;

    @Bind(R.id.tv_panel_title)
    TextView mPanelTitleTv;

    @Bind(R.id.gl_data_panel)
    GridLayout mDataPanelGl;

    @Bind(R.id.tv_school_count)
    TextView mMasterClassroomCountTv;

    @Bind(R.id.tv_classroom_count)
    TextView mReceiveClassroomCountTv;

    @Bind(R.id.tv_teacher_count)
    TextView mTeacherCountTv;

    @Bind(R.id.tv_student_count)
    TextView mStudentCountTv;

    private OnModuleConfigListener mOnModuleConfigListener = new OnModuleConfigListener() {
        @Override
        public void onConfigLoaded(ModuleConfig config) {
            loadAreaMapAndInfo(config.getBaseAreaId(), config.getAreaCode());
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChannelFragment channelFragment = (ChannelFragment) getParentFragment();
        channelFragment.setTitleRiseListener(this);
        ConfigBus.register(mOnModuleConfigListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_fei_xian, container, false);
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
    public void loadHomePageCount(String areaId) {
        Cog.d(TAG, "+loadHomePageCount areaId=", areaId);
    }

    @JavascriptInterface
    public void jumpIntoArea() {
        Cog.d(TAG, "jumpIntoArea");
    }

    /**
     * 已加载的地区编码
     */
    private String mLoadedAreaCode;

    private void loadAreaMapAndInfo(String areaId, String areaCode) {
        Cog.d(TAG, "loadAreaMapAndInfo areaCode=", areaCode);
        if (areaCode != null && !areaCode.equals(mLoadedAreaCode) && mMapView != null) {
            String url = URLConfig.BASE +
                    "/map/mapPage.html?areaCode=" +
                    areaCode;
            Cog.d(TAG, "loadAreaMapAndInfo url=", url);
            mMapView.loadUrl(url);
            mLoadedAreaCode = areaCode;
        }

        Map<String, String> params = new HashMap<>();
        params.put("baseAreaId",areaId);
        WebApi webApi = RsGenerator.create(WebApi.class);
        webApi.post4Json(URLConfig.GET_FX_DATA, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadHomePageCount:" + response);
                        if ("success".equals(response.optString("result"))) {
                            if (mMasterClassroomCountTv == null) return;
                            mMasterClassroomCountTv.setText(String.valueOf(response.optInt("masterClassroomCount")));
                            mReceiveClassroomCountTv.setText(String.valueOf(response.optInt("receiveClassroomCount")));
                            mStudentCountTv.setText(String.valueOf(response.optInt("studentCount")));
                            mTeacherCountTv.setText(String.valueOf(response.optInt("teacherCount")));
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
}
