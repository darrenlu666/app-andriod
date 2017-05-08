package com.codyy.erpsportal.repairs.controllers.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.RxBus;
import com.codyy.erpsportal.repairs.controllers.activities.MakeDetailedInquiryActivity;
import com.codyy.erpsportal.repairs.controllers.adapters.InquiriesAdapter.OnItemClickListener;
import com.codyy.erpsportal.repairs.models.engines.InquiriesLoader;
import com.codyy.erpsportal.repairs.models.engines.InquiriesLoader.Builder;
import com.codyy.erpsportal.repairs.models.entities.InquiryItem;
import com.codyy.erpsportal.repairs.models.entities.RepairDetails;
import com.codyy.erpsportal.repairs.models.entities.StatusItem;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 问题追踪碎片
 */
public class RepairTrackingFragment extends Fragment implements InquiriesLoader.ListExtractor<InquiryItem>{

    public final static String TAG = "RepairTrackingFragment";

    public final static String ARG_SKEY = "com.codyy.erpsportal.ARG_SKEY";

    private final static int RC_MAKE_DETAILED_INQUIRY = 110;

    /**
     * 追问按钮
     */
    @Bind(R.id.btn_make_inquiry)
    Button mMakeInquiryBtn;

    @Bind(R.id.rv_list)
    RecyclerView mListRv;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    @Bind(R.id.fl_list_container)
    FrameLayout mListContainerFl;

    /**
     * 追问数据加载器
     */
    private InquiriesLoader mInquiriesLoader;

    private UserInfo mUserInfo;

    private String mRepairId;

    private String mSkey;

    private Disposable mDisposable;

    public RepairTrackingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(Extra.USER_INFO);
            mRepairId = getArguments().getString(Extra.ID);
            mSkey = getArguments().getString(ARG_SKEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repair_tracking, container, false);
        ButterKnife.bind(this, view);
        if (mUserInfo.isArea()) {
            mMakeInquiryBtn.setVisibility(View.GONE);
        }
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.main_color));
        InquiriesLoader.Builder builder = new Builder();
        mInquiriesLoader = builder.setFragment(this)
                .setRefreshLayout(mRefreshLayout)
                .setRecyclerView(mListRv)
                .setEmptyView(mEmptyTv)
                .setOnItemClickListener(new OnItemClickListener<InquiryItem>() {
                    @Override
                    public void onItemClick(int position, InquiryItem item) { }
                })
                .build();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInquiriesLoader.addParam("malDetailId", mRepairId);
        mInquiriesLoader.addParam("uuid", mUserInfo.getUuid());
        mInquiriesLoader.loadData(true);
        mDisposable = RxBus.getInstance().register(RepairDetails.class, new Consumer<RepairDetails>() {
            @Override
            public void accept(RepairDetails repairDetails) throws Exception {
                if (StatusItem.STATUS_DONE.equals(repairDetails.getStatus()) ||
                        StatusItem.STATUS_VERIFIED.equals(repairDetails.getStatus())) {
                    mMakeInquiryBtn.setVisibility(View.GONE);
                } else {
                    mMakeInquiryBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick(R.id.btn_make_inquiry)
    public void onMakeInquiryClick() {
        MakeDetailedInquiryActivity.start(this, RC_MAKE_DETAILED_INQUIRY, mUserInfo, mRepairId, mSkey);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_MAKE_DETAILED_INQUIRY && resultCode == Activity.RESULT_OK) {
            mInquiriesLoader.loadData(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

    @Override
    public String getUrl() {
        return URLConfig.GET_REPAIR_TRACKING;
    }

    @Override
    public List<InquiryItem> extractList(JSONObject response) {
        Type type = new TypeToken<List<InquiryItem>>(){}.getType();
        return new Gson().fromJson(response.optJSONArray("data").toString(), type);
    }
}
