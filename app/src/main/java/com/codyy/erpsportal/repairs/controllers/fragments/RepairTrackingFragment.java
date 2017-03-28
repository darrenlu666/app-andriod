package com.codyy.erpsportal.repairs.controllers.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.repairs.controllers.adapters.InquiriesAdapter.OnItemClickListener;
import com.codyy.erpsportal.repairs.models.engines.InquiriesLoader;
import com.codyy.erpsportal.repairs.models.engines.InquiriesLoader.Builder;
import com.codyy.erpsportal.repairs.models.entities.InquiryItem;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 问题追踪碎片
 */
public class RepairTrackingFragment extends Fragment implements InquiriesLoader.ListExtractor<InquiryItem>{

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

    private InquiriesLoader mInquiriesLoader;

    public RepairTrackingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repair_tracking, container, false);
        ButterKnife.bind(this, view);
        InquiriesLoader.Builder builder = new Builder();
        mInquiriesLoader = builder.setFragment(this)
                .setRefreshLayout(mRefreshLayout)
                .setRecyclerView(mListRv)
                .setEmptyView(mEmptyTv)
                .setOnItemClickListener(new OnItemClickListener<InquiryItem>() {
                    @Override
                    public void onItemClick(int position, InquiryItem item) {

                    }
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
        mInquiriesLoader.loadData(false);
    }

    @OnClick(R.id.btn_make_inquiry)
    public void onMakeInquiryClick() {
    }

    @Override
    public String getUrl() {
        return URLConfig.GET_REPAIR_TRACKING;
    }

    @Override
    public List<InquiryItem> extractList(JSONObject response) {
        Type type = new TypeToken<List<InquiryItem>>(){}.getType();
        return new Gson().fromJson(response.optJSONArray("list").toString(), type);
    }
}
