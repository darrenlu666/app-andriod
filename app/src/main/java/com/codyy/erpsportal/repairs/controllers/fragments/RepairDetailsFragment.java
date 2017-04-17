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
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.widgets.AspectRatioDraweeView;
import com.codyy.erpsportal.repairs.models.entities.ImageBean;
import com.codyy.erpsportal.repairs.models.entities.RepairDetails;
import com.codyy.erpsportal.repairs.models.entities.StatusItem;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.DraweeView;
import com.google.gson.Gson;

import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 报修详情碎片
 */
public class RepairDetailsFragment extends Fragment {

    private final static String TAG = "RepairDetailsFragment";

    @Bind(R.id.tv_repair_serial)
    TextView mRepairSerialTv;

    @Bind(R.id.tv_classroom)
    TextView mClassroomTv;

    @Bind(R.id.tv_lb_description)
    TextView mLbDescriptionTv;

    @Bind(R.id.et_desc)
    TextView mDescTv;

    @Bind({R.id.dv_icon1, R.id.dv_icon2,R.id.dv_icon3})
    AspectRatioDraweeView[] mPhotoDvs;

    @Bind(R.id.tv_images_count)
    TextView mImagesCountTv;

    @Bind(R.id.ll_photos_container)
    LinearLayout mPhotosContainerLl;

    @Bind(R.id.tv_categories)
    TextView mCategoriesTv;

    @Bind(R.id.tv_reporter)
    TextView mReporterTv;

    @Bind(R.id.et_phone)
    TextView mPhoneTv;

    @Bind(R.id.tv_repair_time)
    TextView mRepairTimeTv;

    @Bind(R.id.tv_status)
    TextView mStatusTv;

    @Bind(R.id.tv_handler)
    TextView mHandlerTv;

    @Bind(R.id.ll_handler)
    LinearLayout mHandlerLl;

    private UserInfo mUserInfo;

    private String mRepairId;

    private RequestSender mRequestSender;

    public RepairDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRepairId = getArguments().getString(Extra.ID);
            mUserInfo = getArguments().getParcelable(Extra.USER_INFO);
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
        params.put("malDetailId", mRepairId);
        params.put("uuid", mUserInfo.getUuid());
        mRequestSender.sendRequest(new RequestData(URLConfig.GET_REPAIR_DETAILS, params,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "load repairDetails=", response);
                        if ("success".equals(response.optString("result"))) {
                            RepairDetails repairDetails = new Gson()
                                    .fromJson(response.optString("data"), RepairDetails.class);
                            mRepairSerialTv.setText(repairDetails.getMalCode());
                            mClassroomTv.setText(getString(R.string.classroom_role_format
                                    , repairDetails.getSkey()
                                    , repairDetails.getClassRoomName()));
                            mDescTv.setText(repairDetails.getMalDescription());
                            mCategoriesTv.setText(repairDetails.getCategories());
                            mReporterTv.setText(repairDetails.getReporter());
                            mPhoneTv.setText(repairDetails.getReporterContact());
                            mRepairTimeTv.setText(DateTimeFormat.forPattern("YYYY-MM-dd HH:mm")
                                    .print(repairDetails.getCreateTime()));
                            mStatusTv.setText(repairDetails.statusStr());
                            if (StatusItem.STATUS_NEW.equals(repairDetails.getStatus())) {
                                mHandlerLl.setVisibility(View.GONE);
                            } else {
                                mHandlerLl.setVisibility(View.VISIBLE);
                                mHandlerTv.setText(repairDetails.getRepairman());
                            }

                            List<ImageBean> images = repairDetails.getImgs();
                            if (images != null && images.size() > 0) {
                                mPhotosContainerLl.setVisibility(View.VISIBLE);
                                if (images.size() > 3) {
                                    mImagesCountTv.setText("共" + images.size() + "张");
                                    mImagesCountTv.setVisibility(View.VISIBLE);
                                } else {
                                    mImagesCountTv.setVisibility(View.INVISIBLE);
                                }
                                for (int i = 0; i < mPhotoDvs.length; i++) {
                                    DraweeView dv = mPhotoDvs[i];
                                    if (i < images.size()) {
                                        dv.setVisibility(View.VISIBLE);
                                        ImageFetcher.getInstance(getContext()).fetchSmall(dv,
                                                images.get(i).getImgPath());
                                    } else {
                                        dv.setVisibility(View.INVISIBLE);
                                    }
                                }
                            } else {
                                mPhotosContainerLl.setVisibility(View.GONE);
                            }
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "load repairDetails error=", error.getMessage());
            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
