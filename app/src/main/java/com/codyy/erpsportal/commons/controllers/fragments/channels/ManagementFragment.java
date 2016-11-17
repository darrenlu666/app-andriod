package com.codyy.erpsportal.commons.controllers.fragments.channels;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageButton;
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
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.CountData;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.WordyNestedScrollView;
import com.codyy.erpsportal.commons.widgets.WordyNestedScrollView.OnScrollListener;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页（监管）
 * A simple {@link Fragment} subclass.
 */
public class ManagementFragment extends Fragment{

    private static final String TAG = "ManagementFragment";

    private View mRootView;

    private WordyNestedScrollView mScrollView;

    private WebView mMapView;

    private ImageButton mArrowBtn;

    private TextView mAreaNameTv;

    private TextView mLbMasterClassroomCountTv;
    private TextView mMasterClassroomCountTv;
    private TextView mLbReceiveClassroomCountTv;
    private TextView mReceiveClassroomCountTv;

    private TextView mPlanScheduleCountTv;
    private TextView mStudentBenefitCountTv;

    private TextView mTeachTeacherCountTv;
    private TextView mWeekScheduleCountTv;

    private TextView mWeekPlanScheduleCountTv;
    private TextView mWeekScheduleOverCountTv;

    public static ManagementFragment newInstance() {
        return new ManagementFragment();
    }

    public ManagementFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigBus.register(mOnModuleConfigListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConfigBus.unregister(mOnModuleConfigListener);
    }

    private OnModuleConfigListener mOnModuleConfigListener = new OnModuleConfigListener() {
        @Override
        public void onConfigLoaded(ModuleConfig config) {
            loadData(config.getAreaCode());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_manager, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        findViews(mRootView);
        initMapView();

        mScrollView.setOnScrollListener(new OnScrollListener() {
            private int scrollingY = 0;

            @Override
            public void onScroll(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mMapView.setTranslationY(scrollY);
                scrollingY = oldScrollY - scrollY;
            }

            @Override
            public void onActionUp(MotionEvent ev) {
                Cog.d(TAG, "onActionUp");
                if (scrollingY > 0) {
                    collapse();
                    scrollingY = 0;
                } else if (scrollingY < 0) {
                    expand();
                    scrollingY = 0;
                } else {
                    float upY = ev.getY();
                    if (upY < mArrowBtn.getTop() && upY > mArrowBtn.getBottom()) {
                        if (mIsExpanded) {
                            collapse();
                        } else {
                            expand();
                        }
                    }
                }
            }
        });

        mArrowBtn.setOnClickListener(mOnArrowBtnClickListener);
        return mRootView;
    }

    private void findViews(View view) {
        mScrollView = (WordyNestedScrollView) view.findViewById(R.id.scroll_view);
        mMapView = (WebView) view.findViewById(R.id.map_view);
        mArrowBtn = (ImageButton) view.findViewById(R.id.btn_slide_up);
        mAreaNameTv = (TextView) view.findViewById(R.id.area_name);
        mLbMasterClassroomCountTv = (TextView) view.findViewById(R.id.lb_main_classroom_count);
        mMasterClassroomCountTv = (TextView) view.findViewById(R.id.master_classroom_count);
        mLbReceiveClassroomCountTv = (TextView) view.findViewById(R.id.lb_receiving_classroom_count);
        mReceiveClassroomCountTv = (TextView) view.findViewById(R.id.receiving_classroom_count);
        mPlanScheduleCountTv = (TextView) view.findViewById(R.id.planed_total);
        mStudentBenefitCountTv = (TextView) view.findViewById(R.id.students_total);
        mTeachTeacherCountTv = (TextView) view.findViewById(R.id.teachers_total);
        mWeekScheduleCountTv = (TextView) view.findViewById(R.id.week_all);
        mWeekPlanScheduleCountTv = (TextView) view.findViewById(R.id.should_give);
        mWeekScheduleOverCountTv = (TextView) view.findViewById(R.id.already_given);
    }

    /**
     * 加载地图
     */
    private void initMapView() {
        mMapView.getSettings().setSupportZoom(true);
        mMapView.getSettings().setBuiltInZoomControls(true);
        mMapView.getSettings().setUseWideViewPort(true);
        mMapView.setBackgroundColor(0x00ffffff);
        mMapView.getBackground().setAlpha(0);
        mMapView.getSettings().setJavaScriptEnabled(true);
        mMapView.addJavascriptInterface(this, "android");
        mMapView.getSettings().setDisplayZoomControls(false);
    }

    public void loadData(String areaCode) {
        loadHomePageCount(areaCode);
        loadAreaMapAndInfo(areaCode);
    }

    /**
     * 加载统计数据
     */
    @JavascriptInterface
    public void loadHomePageCount(String areaCode) {
        Cog.d(TAG, "+loadHomePageCount areaCode=" + areaCode);
        if (TextUtils.isEmpty(areaCode)) return;
        RequestQueue requestQueue = RequestManager.getRequestQueue();
        Map<String, String> params = new HashMap<>();
        params.put("areaCode", areaCode);
        Cog.d(TAG, "loadHomePageCount url=", URLConfig.URL_HOME_PAGE_COUNT, params);
        requestQueue.add(new NormalPostRequest(
                URLConfig.URL_HOME_PAGE_COUNT, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadHomePageCount:" + response);
                if ("success".equals(response.optString("result"))) {
                    CountData countData = CountData.parseJson(response);
                    setCountDataToViews(countData);
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

    /**
     * 已加载的地区编码
     */
    private String mLoadedAreaCode;

    private void loadAreaMapAndInfo(String areaCode) {
        Cog.d(TAG, "loadAreaMapAndInfo areaCode=", areaCode);
        if (areaCode != null && !areaCode.equals(mLoadedAreaCode) && mMapView != null) {
            String url = URLConfig.BASE +
                    "/map/mapPage.html?areaCode=" +
                    areaCode;
            Cog.d(TAG, "loadAreaMapAndInfo url=", url);
            mMapView.loadUrl(url);
            mLoadedAreaCode = areaCode;
        }
    }

    /**
     * 将统计数据设置到
     *
     * @param countData 统计数据
     */
    private void setCountDataToViews(CountData countData) {
        if (mAreaNameTv == null) return;//界面没初始化好，防止fc
        mAreaNameTv.setText(countData.getAreaName());
        mLbMasterClassroomCountTv.setText(getString(R.string.classroom_count_lb, Titles.sMaster));
        mMasterClassroomCountTv.setText(countData.getMasterClassroomCount() + "");
        mLbReceiveClassroomCountTv.setText(getString(R.string.classroom_count_lb, Titles.sReceiver));
        mReceiveClassroomCountTv.setText(countData.getReceiveClassroomCount() + "");
        mPlanScheduleCountTv.setText(countData.getPlanScheduleCount() + "");
        mStudentBenefitCountTv.setText(countData.getStudentBenefitCount() + "");
        mTeachTeacherCountTv.setText(countData.getTeachTeacherCount() + "");
        mWeekScheduleCountTv.setText(countData.getWeekScheduleCount() + "");
        mWeekPlanScheduleCountTv.setText(countData.getWeekPlanScheduleCount() + "");
        mWeekScheduleOverCountTv.setText(countData.getWeekScheduleOverCount() + "");
    }

    private OnClickListener mOnArrowBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Cog.d(TAG, "onArrowClick mIsExpanded=", mIsExpanded);
            if (mIsExpanded) {
                collapse();
            } else {
                expand();
            }
        }
    };

    private Handler mHandler = new Handler();

    private boolean mIsExpanded = false;

    /**
     * 收起统计区域
     */
    private void collapse() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ((ChannelFragment) getParentFragment()).setTitleExpanded(true);
                mScrollView.fullScroll(View.FOCUS_UP);
                mScrollView.smoothScrollBy(0, -1000);
                mIsExpanded = false;
            }
        });
    }

    /**
     * 展开统计区域
     */
    private void expand() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ((ChannelFragment) getParentFragment()).setTitleExpanded(false);
                mScrollView.fullScroll(View.FOCUS_DOWN);
                mScrollView.smoothScrollBy(0, 1000);
                mIsExpanded = true;
            }
        });
    }

}
