package com.codyy.erpsportal.repairs.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity.OnFilterObserver;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerAdapter.OnItemClickListener;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.Builder;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.ListExtractor;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingCommonRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.RxBus;
import com.codyy.erpsportal.repairs.controllers.activities.SchoolRepairsActivity;
import com.codyy.erpsportal.repairs.controllers.fragments.SchoolRepairListFragment.RepairSchoolVh;
import com.codyy.erpsportal.repairs.models.entities.RepairSchool;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 有报修记录的学校列表碎片
 */
public class SchoolRepairListFragment extends Fragment implements OnFilterObserver,ListExtractor<RepairSchool, RepairSchoolVh> {

    private final static String TAG = "SchoolRepairListFragment";

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    private UserInfo mUserInfo;

    private RvLoader<RepairSchool, RepairSchoolVh, ShowAreaPrefixFlag> mRvLoader;

    private ShowAreaPrefixFlag mShowAreaPrefixFlag;

    private Disposable mDisposable;

    public SchoolRepairListFragment() {
    }

    public static SchoolRepairListFragment newInstance() {
        SchoolRepairListFragment fragment = new SchoolRepairListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowAreaPrefixFlag = new ShowAreaPrefixFlag(true);
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(Extra.USER_INFO);
        }
        mDisposable = RxBus.getInstance().register(ShowAreaPrefixFlag.class, new Consumer<ShowAreaPrefixFlag>() {
            @Override
            public void accept(ShowAreaPrefixFlag showAreaPrefixFlag) throws Exception {
                Cog.d(TAG, "accept ShowAreaPrefixFlag:", showAreaPrefixFlag.shouldShow);
                if (mShowAreaPrefixFlag.shouldShow != showAreaPrefixFlag.shouldShow) {
                    mShowAreaPrefixFlag.shouldShow = showAreaPrefixFlag.shouldShow;
                    mRvLoader.notifyInfoChanged();
                }
            }
        });
    }

    @Override
    public String getUrl() {
        return URLConfig.GET_REPAIRS_SCHOOLS;
    }

    @Override
    public List<RepairSchool> extractList(JSONObject response) {
        Type listType = new TypeToken<List<RepairSchool>>() {}
                .getType();
        Gson gson = new Gson();
        return gson.fromJson(response.optJSONArray("data").toString(), listType);
    }

    @Override
    public ViewHolderCreator<RepairSchoolVh> newViewHolderCreator() {
        return new EasyVhrCreator<>(RepairSchoolVh.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_recycle, container, false);
        ButterKnife.bind(this, view);
        Builder<RepairSchool, RepairSchoolVh, ShowAreaPrefixFlag> loaderBuilder = new Builder<>();
        mRvLoader = loaderBuilder.setFragment(this)
                .setRecyclerView(mRecyclerView)
                .setRefreshLayout(mRefreshLayout)
                .setEmptyView(mEmptyTv)
                .setOnItemClickListener(new OnItemClickListener<RepairSchool>() {
                    @Override
                    public void onItemClick(int position, RepairSchool repairSchool) {
                        SchoolRepairsActivity.start(getContext(),
                                mUserInfo,
                                repairSchool.getSchoolId(),
                                repairSchool.getSchoolName());
                    }
                })
                .setInfo(mShowAreaPrefixFlag)
                .build();
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.main_color));
        mRvLoader.showDivider();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRvLoader.addParam("uuid", mUserInfo.getUuid());
        mRvLoader.loadData(true);
    }

    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        mRvLoader.updateParamsBaseOnMap(params, "baseAreaId");
        mRvLoader.loadData(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRvLoader.release();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @LayoutId(R.layout.item_repair_school)
    public static class RepairSchoolVh extends BindingCommonRvHolder<RepairSchool> {

        @Bind(R.id.tv_name)
        TextView mNameTv;

        @Bind(R.id.tv_total_count)
        TextView mTotalCountTv;

        @Bind(R.id.tv_handled_count)
        TextView mHandledCountTv;

        public RepairSchoolVh(View itemView) {
            super(itemView);
        }

        @Override
        public <INFO> void setDataToView(RepairSchool repairSchool, INFO info) {
            Context context = itemView.getContext();
            ShowAreaPrefixFlag flag = (ShowAreaPrefixFlag) info;
            if (flag.shouldShow) {
                mNameTv.setText(repairSchool.getAreaName() + "-" + repairSchool.getSchoolName());
            } else {
                mNameTv.setText(repairSchool.getSchoolName());
            }
            mTotalCountTv.setText(context.getString(R.string.malfunction_count, repairSchool.getAllCount()));
            String handledCountStr = context.getString(R.string.handled_count, repairSchool.getDealCount());
            SpannableStringBuilder ssb = new SpannableStringBuilder(handledCountStr);
            ForegroundColorSpan foreColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_color));
            ssb.setSpan(foreColorSpan, 7, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mHandledCountTv.setText(ssb);
        }
    }

    public static class ShowAreaPrefixFlag {
        public ShowAreaPrefixFlag(boolean shouldShow) {
            this.shouldShow = shouldShow;
        }
        public boolean shouldShow;
    }
}
