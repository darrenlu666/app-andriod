package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringDef;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourNewActivity.ClassroomViewHolder;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.Builder;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.ListExtractor;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingCommonRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator.LayoutId;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.commons.controllers.fragments.filters.BaseFilterFragment;
import com.codyy.erpsportal.commons.controllers.fragments.filters.NetTeachFilterFragment;
import com.codyy.erpsportal.commons.models.Jumpable;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.TourClassroom;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.models.parsers.SchoolNetClassroomParser;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ClassTourNewActivity extends AppCompatActivity implements ListExtractor<TourClassroom, ClassroomViewHolder> {

    private static final String TAG = "ClassTourNewActivity";

    private static final String EXTRA_USER_INFO = "user_info";

    private final static String EXTRA_TYPE = "type";

    /**
     * 专递课堂
     */
    public final static String TYPE_SPECIAL_DELIVERY_CLASSROOM = "specialDeliveryClassroom";

    /**
     * 名校网络课堂
     */
    public final static String TYPE_SCHOOL_NET = "schoolNet";

    private UserInfo mUserInfo;

    private String mType;

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.btn_filter)
    Button mFilterBtn;

    @Bind(R.id.ib_switch_list)
    ImageButton mSwitchListIb;

    @Bind(R.id.filter_drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.rv_course_tour)
    RecyclerView mCourseTourRv;

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    private NetTeachFilterFragment mFilterFragment ;

//    private LiveFilterFragment mLiveFilterFragment;

    private Bundle mBundleFilter = null;

    private FilterBtnController mFilterBtnController;

    private boolean mHasScreenshot;

    private Status mStatus = new Status();

    private RvLoader<TourClassroom, ClassroomViewHolder, Status> mLoader;

    private DrawerListener mDrawerListener = new SimpleDrawerListener() {

        @Override
        public void onDrawerOpened(View drawerView) {
            mFilterBtnController.setFiltering(true);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mFilterBtnController.setFiltering(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_tour_new);
        ButterKnife.bind(this);
        RvLoader.Builder<TourClassroom, ClassroomViewHolder, Status> controllerBuilder = new Builder<>();
        mLoader = controllerBuilder
                .setActivity(this)
                .setRefreshLayout(mRefreshLayout)
                .setRecyclerView(mCourseTourRv)
                .setEmptyView(mEmptyTv)
                .setInfo(mStatus)
                .build();
        mLoader.showDivider();
        if (getIntent() != null) {
            mUserInfo = getIntent().getParcelableExtra(EXTRA_USER_INFO);
            mType = getIntent().getStringExtra(EXTRA_TYPE);
        }
        initTitle();
        mFilterBtnController = new FilterBtnController(mFilterBtn);
        mDrawerLayout.addDrawerListener(mDrawerListener);
//        mLiveFilterFragment = (LiveFilterFragment) getSupportFragmentManager().findFragmentByTag("class_tour_filter");
        createFilter();
        loadData(true);
    }

    /**
     *１．筛选－＂我创建的＂
     */
    private void createFilter() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFilterFragment = new NetTeachFilterFragment();
        Bundle bd = new Bundle();
        if(mUserInfo.isArea()){
            bd.putString(BaseFilterFragment.EXTRA_TYPE, BaseFilterFragment.TYPE_FILTER_LIVE_WATCH_AREA);
        }else{
            bd.putString(BaseFilterFragment.EXTRA_TYPE, BaseFilterFragment.TYPE_FILTER_LIVE_WATCH_SCHOOL);
        }
        mFilterFragment.setArguments(bd);
        ft.replace(R.id.fl_filter, mFilterFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 设置标题栏标题
     */
    private void initTitle() {
        if (TYPE_SPECIAL_DELIVERY_CLASSROOM.equals(mType)) {
            mTitleBar.setTitle(Titles.sWorkspaceSpeclassRound);
        } else {
            mTitleBar.setTitle(Titles.sWorkspaceNetClassRound);
        }
    }

    @OnClick(R.id.btn_filter)
    public void onFilterBtnClick(View view) {
        if (mFilterBtnController.isFiltering()) {
            mBundleFilter = mFilterFragment.getFilterData();
            loadData(true);
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
        mFilterBtnController.toggle();
    }

    @OnClick(R.id.ib_switch_list)
    public void onSwitchListClick(View view) {
        mHasScreenshot = !mHasScreenshot;
        mStatus.showThumb = mHasScreenshot;
        mLoader.notifyInfoChanged();
        if (mHasScreenshot) {
            mLoader.hideDivider();
            mSwitchListIb.setImageResource(R.drawable.btn_no_image_list);
            if (delayMillis > 0L) {
                isNeedRefresh = true;
                handler.postDelayed(runnable, delayMillis);
            }
        } else {
            mLoader.showDivider();
            mSwitchListIb.setImageResource(R.drawable.btn_with_images_list);
            isNeedRefresh = false;
            handler.removeCallbacks(runnable);
        }
    }

    private boolean isNeedRefresh = false;
    private long delayMillis = 0L;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mLoader.notifyInfoChanged();
            handler.postDelayed(this, delayMillis);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedRefresh && delayMillis > 0L) {
            handler.postDelayed(runnable, delayMillis);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    //default search
    private void loadData(final boolean refresh) {
        if (mBundleFilter != null) {
            loadData(refresh, obtainParam("areaId"), obtainParam("directSchoolId"), obtainParam("class"), obtainParam("subject"), mBundleFilter.getBoolean("hasDirect"));
        } else {
            loadData(refresh, null, null, null, null, false);
        }
    }

    private String obtainParam(String key) {
        return mBundleFilter.getString(key);
    }

    /**
     * 加载课堂巡视
     *
     * @param refresh      是否是刷新
     * @param areaId       地区id
     * @param schoolId     学校id
     * @param classLevelId 年级id
     * @param subjectId    学科id
     * @param hasDirect    是否是直属
     */
    private void loadData(final boolean refresh, String areaId, String schoolId, String classLevelId, String subjectId, boolean hasDirect) {
        mLoader.addParam("uuid", mUserInfo.getUuid());

        if (!TextUtils.isEmpty(schoolId)) {
            mLoader.addParam("schoolId", schoolId);
            mLoader.removeParam("areaId");
        } else if (!TextUtils.isEmpty(mUserInfo.getSchoolId())) {
            mLoader.addParam("schoolId", mUserInfo.getSchoolId());
            mLoader.removeParam("areaId");
        } else if (!TextUtils.isEmpty(areaId)) {
            mLoader.addParam("areaId", areaId);
            mLoader.removeParam("schoolId");
        } else {
            mLoader.addParam("areaId", mUserInfo.getBaseAreaId());
            mLoader.removeParam("schoolId");
        }

        if (!TextUtils.isEmpty(classLevelId)) {
            mLoader.addParam("baseClasslevelId", classLevelId);
        }

        if (!TextUtils.isEmpty(subjectId)) {
            mLoader.addParam("baseSubjectId", subjectId);
        }
        if (!hasDirect) {
            mLoader.addParam("directly", "false");
        } else {
            mLoader.addParam("directly", "true");
        }
        mLoader.loadData(true);
    }

    private void showRefreshing(final boolean refreshing) {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    @Override
    public String getUrl() {
        String url = null;
        if (TYPE_SPECIAL_DELIVERY_CLASSROOM.equals(mType)) {
            url = URLConfig.SPECIAL_MONITOR_CLASSROOM;
        } else {
            url = URLConfig.NET_MONITOR_CLASSROOM;
        }
        Cog.d(TAG, "url:", url);
        return url;
    }

    @Override
    public List<TourClassroom> extractList(JSONObject response) {
        JsonParser<TourClassroom> jsonParser;
        JSONArray jsonArray;
        List<TourClassroom> list;
        try {
            delayMillis = response.optInt("refreshInterval") * 1000;
            jsonArray = response.optJSONArray("list");
            jsonParser = new SchoolNetClassroomParser();
            list = jsonParser.parseArray(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public ViewHolderCreator<ClassroomViewHolder> newViewHolderCreator() {
        return new EasyVhrCreator<>(ClassroomViewHolder.class);
    }

    private static class FilterBtnController {
        private Button mFilterBtn;

        private boolean mIsFiltering;

        public FilterBtnController(Button filterBtn) {
            this.mFilterBtn = filterBtn;
        }

        public void toggle() {
            setFiltering(!mIsFiltering);
        }

        public boolean isFiltering() {
            return mIsFiltering;
        }

        public void setFiltering(boolean filtering) {
            if (mIsFiltering == filtering) return;
            mIsFiltering = filtering;
            if (mIsFiltering) {
                mFilterBtn.setText(R.string.confirm_filter);
                mFilterBtn.setBackgroundResource(R.drawable.bg_btn_aqua);
                mFilterBtn.setCompoundDrawables(null, null, null, null);
            } else {
                mFilterBtn.setText("");
                mFilterBtn.setBackgroundResource(0);
                mFilterBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.filter_bg_selector, 0);
            }
        }
    }

    public static void start(Activity activity, UserInfo userInfo, String type) {
        Intent intent = new Intent(activity, ClassTourNewActivity.class);
        intent.putExtra(EXTRA_USER_INFO, userInfo);
        intent.putExtra(EXTRA_TYPE, type);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    public static class ClassTourJumper implements Jumpable {

        private String type;

        public ClassTourJumper(@TourType String type) {
            this.type = type;
        }

        @Override
        public void jump(Context context) {
            UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
            ClassTourNewActivity.start((Activity) context, userInfo, type);
        }
    }

    @LayoutId(R.layout.item_course_tour)
    public static class ClassroomViewHolder extends BindingCommonRvHolder<TourClassroom, Status> {

        @Bind(R.id.iv_thumb)
        SimpleDraweeView mThumbIv;

        @Bind(R.id.tv_receive)
        TextView mReceiveTv;

        @Bind(R.id.tv_school_name)
        TextView mSchoolNameTv;

        @Bind(R.id.tv_scope)
        TextView mScopeTv;

        @Bind(R.id.tv_enter_view)
        TextView mEnterView;

        @Bind(R.id.rl_course_tour)
        RelativeLayout mRelativeLayout;
        private Context mContext;

        public ClassroomViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
        }

        @Override
        public void setDataToView(final TourClassroom classroom, Status status) {
            if(UserInfoKeeper.obtainUserInfo().isSchool()){
                mSchoolNameTv.setText(classroom.getTeacherName());
            }else{
                mSchoolNameTv.setText(classroom.getSchoolName());
            }
            if (status.showThumb) {
                mThumbIv.setVisibility(View.VISIBLE);
                if ("main".equals(classroom.getType())) {
                    mReceiveTv.setVisibility(View.GONE);
                } else {
                    mReceiveTv.setVisibility(View.VISIBLE);
                }
                mEnterView.setVisibility(View.GONE);
                setScopeTvMarginTopDp(6);
                mSchoolNameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                mScopeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            } else {
                mThumbIv.setVisibility(View.GONE);
                mReceiveTv.setVisibility(View.GONE);
                mEnterView.setVisibility(View.VISIBLE);
                setScopeTvMarginTopDp(12);
                mSchoolNameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                mScopeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
            mScopeTv.setText(classroom.getGradeName() + "/" + classroom.getSubjectName());
//            Uri avaURI = Uri.parse(classroom.getCaptureUrl().replace("china.codyy.com", "10.5.223.59"));
            Uri avaURI = Uri.parse(classroom.getCaptureUrl());
            Fresco.getImagePipeline().evictFromMemoryCache(avaURI);
            Fresco.getImagePipelineFactory().getMainDiskStorageCache().remove(new SimpleCacheKey(avaURI.toString()));
            Fresco.getImagePipelineFactory().getSmallImageDiskStorageCache().remove(new SimpleCacheKey(avaURI.toString()));
            mThumbIv.setImageURI(avaURI);
            mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(classroom);
                }
            });
        }

        private void setScopeTvMarginTopDp(int dp) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) mScopeTv.getLayoutParams();
            layoutParams.topMargin = UIUtils.dip2px(itemView.getContext(), dp);
            mScopeTv.setLayoutParams(layoutParams);
        }
    }

    public void onEventMainThread(TourClassroom classroom) {
        ClassTourPagerActivity.start(this, classroom, mUserInfo, mType);
    }

    public static class Status {
        boolean isParent;
        boolean showThumb;
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_SPECIAL_DELIVERY_CLASSROOM, TYPE_SCHOOL_NET})
    @interface TourType {}
}
