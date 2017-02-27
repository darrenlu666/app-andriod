package com.codyy.erpsportal.commons.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.SearchInputActivity;
import com.codyy.erpsportal.commons.controllers.adapters.ChannelPagerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.channels.MapFragment;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.LoadingHandler;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.entities.ChannelTab;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.OnFiveEvenClickListener;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.EmptyView.OnReloadClickListener;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;
import com.codyy.url.URLConfig;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 首页-频道页
 * Created by gujiajia on 2015/7/15.
 */
public class ChannelFragment extends Fragment implements OnModuleConfigListener, LoadingHandler, OnReloadClickListener {

    private final static String TAG = "ChannelFragment";

    @Bind(R.id.app_bar_layout)
    protected AppBarLayout mAppBarLayout;

    @Bind(R.id.search)
    protected ImageButton mSearchIb;

    @Bind(R.id.title_bar)
    protected RelativeLayout mTitleRl;

    @Bind(R.id.view_pager)
    protected ViewPager mViewPager;

    @Bind(R.id.ev_channel)
    protected EmptyView mEmptyView;

    @Bind(R.id.sliding_tabs)
    protected SlidingTabLayout mSlidingTabLayout;

//    @Bind(R.id.sliding_tabs_image)
//    ImageView mImageView;

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    protected View mRootView;

    private OnOffsetChangedListener mOnOffsetChangedListener = new OnOffsetChangedListener() {
        private int mPrevOffset;
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (mPrevOffset != verticalOffset) {
                Cog.d(TAG, "onOffsetChanged verticalOffset=", verticalOffset);
                if (mMapFragmentRf != null) {
                    if (mMapFragmentRf.get() != null) {
                        MapFragment mapFragment = mMapFragmentRf.get();
                        mapFragment.notifyParentRise(verticalOffset);
                    }
                }
                mPrevOffset = verticalOffset;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConfigBus.unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_channel, container, false);
            ButterKnife.bind(this, mRootView);
            mAppBarLayout.addOnOffsetChangedListener(mOnOffsetChangedListener);
            mEmptyView.setLoading(false);
            mEmptyView.setOnReloadClickListener(this);
            mTitleTv.setOnTouchListener(new OnFiveEvenClickListener() {
                @Override
                public void onFiveEvenClick(){
                    Toast.makeText(getContext(), "接口地址：" + URLConfig.BASE, Toast.LENGTH_LONG).show();
                }
            });
        }
        ConfigBus.setErrorHandler(this);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cog.i(TAG,"onActvityCreated() ! reloaded ...");
        ModuleConfig moduleConfig = ConfigBus.getInstance().getModuleConfig();
        if (moduleConfig != null) {//若已经有配置了
            initViewsWithConfig(moduleConfig);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ConfigBus.clearErrorHandler();
    }

    @OnClick(R.id.search)
    public void onSearchClick() {
        Cog.d(TAG, "onSearchClick mSearchFlag=", mSearchFlag);
        SearchInputActivity.start(getActivity(), mSearchFlag);
    }

    public void initializeTabs(List<ChannelTab> channelTabs, String indexTemplateId) {
        Cog.d(TAG, "+initializeTabs channelTabs=", channelTabs, ":indexTemplateId=", indexTemplateId);
        if(mViewPager == null) return;
        ChannelPagerAdapter adapter = new ChannelPagerAdapter(getActivity(), getChildFragmentManager(), mViewPager);
        for (ChannelTab channelTab : channelTabs) {
            Cog.d(TAG, "add tab channelTab", channelTab);
            adapter.addTabInfo(channelTab.createTabInfo(indexTemplateId));
        }
        adapter.notifyDataSetChanged();
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onConfigLoaded(final ModuleConfig config) {
        Cog.d(TAG, "+onConfigLoaded", config);
        initViewsWithConfig(config);
    }

    /**
     * 根据配置加载数据
     * @param config 配置数据
     */
    private void initViewsWithConfig(ModuleConfig config) {
        if(isDetached()) return;
        mEmptyView.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        initializeTabs(config.getChannelTabs(), config.getIndexTemplateId());
        initSearchBtn(config);
    }

    private int mSearchFlag = 0;

    private void initSearchBtn(ModuleConfig config) {
        mSearchFlag = 0;
        List<ChannelTab> channelTabs = config.getChannelTabs();
        for (ChannelTab channelTab:channelTabs) {
            if (channelTab.getId() != null) {
                if ("informationid".equals(channelTab.getId())) {
                    mSearchFlag = mSearchFlag | 0x4;
                } else if ("classresourceid".equals(channelTab.getId())){
                    mSearchFlag = mSearchFlag | 0x2;
                } else if ("groupid".equals(channelTab.getId())){
                    mSearchFlag = mSearchFlag | 0x1;
                }
            }
        }
        if (mSearchFlag > 0) {
            mSearchIb.setVisibility(View.VISIBLE);
        } else {
            mSearchIb.setVisibility(View.GONE);
        }
    }

    /**
     * 展开或缩起标题
     * @param expanded 是否已展开
     */
    public void setTitleExpanded(boolean expanded) {
        mAppBarLayout.setExpanded(expanded, true);
    }

    @Override
    public void onLoading() {
        if (!ConfigBus.hasConfig()) {
            mEmptyView.setVisibility(View.VISIBLE);
        }

        if (mEmptyView.getVisibility() == View.VISIBLE) {
            mEmptyView.setLoading(true);
        }
    }

    @Override
    public void onError() {
        Cog.d(TAG, "onError");
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(false);
        mViewPager.setVisibility(View.GONE);
    }

    @Override
    public void onReloadClick() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).loadModuleConfig();
        }
    }

    private WeakReference<MapFragment> mMapFragmentRf;

    public void setMapFragment(MapFragment mapFragment) {
        mMapFragmentRf = new WeakReference<>(mapFragment);
    }

    public interface OnAreaClickListener {
        void onAreaClick();
    }

}
