package com.codyy.erpsportal.repairs.controllers.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.AspectRatioDraweeView;
import com.codyy.erpsportal.repairs.models.entities.RepairDetails;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 报修详情碎片
 */
public class RepairDetailsFragment extends Fragment {

    private final static String TAG = "RepairDetailsFragment";

    public final static String ARG_REPAIR_ID = "repair_id";

    @Bind(R.id.tv_repair_serial)
    TextView mRepairSerialTv;

    @Bind(R.id.tv_classroom)
    TextView mClassroomTv;

    @Bind(R.id.tv_lb_description)
    TextView mLbDescriptionTv;

    @Bind(R.id.tv_desc)
    TextView mDescTv;

    @Bind(R.id.dv_icon1)
    AspectRatioDraweeView mIcon1Dv;

    @Bind(R.id.dv_icon2)
    AspectRatioDraweeView mIcon2Dv;

    @Bind(R.id.ll_photos_container)
    LinearLayout mPhotosContainerLl;

    @Bind(R.id.tv_categories)
    TextView mCategoriesTv;

    @Bind(R.id.tv_reporter)
    TextView mReporterTv;

    @Bind(R.id.tv_phone)
    TextView mPhoneTv;

    @Bind(R.id.tv_repair_time)
    TextView mRepairTimeTv;

    @Bind(R.id.tv_status)
    TextView mStatusTv;

    @Bind(R.id.tv_handler)
    TextView mHandlerTv;

    @Bind(R.id.ll_handler)
    LinearLayout mHandlerLl;

    private String mRepairId;

    private RequestSender mRequestSender;

    public RepairDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRepairId = getArguments().getString(ARG_REPAIR_ID);
        }
        mRequestSender = new RequestSender(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repair_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Map<String, String> params = new HashMap<>();
        params.put("repairId", mRepairId);
        mRequestSender.sendRequest(new RequestData(URLConfig.GET_REPAIR_DETAILS, params,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "load repairDetails=", response);
                        if ("success".equals(response.optString("result"))) {
                            RepairDetails repairDetails = new Gson()
                                    .fromJson(response.optString("data"), RepairDetails.class);
                            mRepairSerialTv.setText(repairDetails.getSerial());
                            mClassroomTv.setText(getString(R.string.classroom_role_format
                                    , repairDetails.getClassroomSerial()
                                    , repairDetails.getClassroomName()));
                            mDescTv.setText(repairDetails.getDescription());
                            mCategoriesTv.setText(repairDetails.getCategories());
                            mReporterTv.setText(repairDetails.getReporter());
                            mPhoneTv.setText(repairDetails.getPhone());
                            mRepairTimeTv.setText(DateTimeFormat.forPattern("YYYY-MM-dd HH:mm")
                                    .print(repairDetails.getReportTime()));
                            mStatusTv.setText(repairDetails.statusStr());
                            if (repairDetails.getStatus() > 2) {
                                mHandlerLl.setVisibility(View.VISIBLE);
                                mHandlerTv.setText(repairDetails.getHandlerName());
                            } else {
                                mHandlerLl.setVisibility(View.GONE);
                            }
                        }
                    }
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(Throwable error) {
                        Cog.e(TAG, "load repairDetails error=" ,error.getMessage());
                    }
                }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
