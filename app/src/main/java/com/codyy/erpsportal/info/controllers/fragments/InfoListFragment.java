package com.codyy.erpsportal.info.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.info.controllers.activities.InfoDetailActivity;
import com.codyy.erpsportal.info.controllers.fragments.InfoListFragment.InfoViewHolder;
import com.codyy.erpsportal.info.utils.Info;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 资讯列表，使用RecyclerView
 * Created by gujiajia on 2015/12/15.
 */
public class InfoListFragment extends LoadMoreFragment<Info, InfoViewHolder> {

    private static final String TAG = "InfoListFragment";

    public static final String ARG_AREA_ID = "arg_area_id";

    public static final String ARG_SCHOOL_ID = "arg_school_id";

    public static final String ARG_TYPE = "arg_type";

    private String mType;

    private String mAreaId;

    private String mSchoolId;

    public InfoListFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(ARG_TYPE);
            mAreaId = getArguments().getString(ARG_AREA_ID);
            mSchoolId = getArguments().getString(ARG_SCHOOL_ID);
        }
    }

    @Override
    protected AbsVhrCreator<InfoViewHolder> newViewHolderCreator() {
        return new EasyVhrCreator<>(InfoViewHolder.class);
    }

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
    }

    @Override
    protected String getUrl() {
        return URLConfig.MORE_INFORMATION;
    }

    @Override
    protected List<Info> getList(JSONObject response) {
        JSONArray jsonArray = response.optJSONArray("data");
        return Info.JSON_PARSER.parseArray(jsonArray);
    }

    @Override
    protected void addParams(Map<String, String> map) {
        map.put("infoType", mType);
        if (!TextUtils.isEmpty(mSchoolId)) {
            map.put("schoolId", mSchoolId);
        }
        if (!TextUtils.isEmpty(mAreaId)) {
            map.put("baseAreaId", mAreaId);
        }
    }

    public static InfoListFragment newInstance(String type, String areaId, String schoolId) {
        InfoListFragment fragment = new InfoListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TYPE, type);
        bundle.putString(ARG_SCHOOL_ID, schoolId);
        bundle.putString(ARG_AREA_ID, areaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @LayoutId(R.layout.item_info_intro)
    public static class InfoViewHolder extends RecyclerViewHolder<Info>{

        private TextView titleTv;

        private TextView introTv;

        private SimpleDraweeView iconDv;

        private View container;

        public InfoViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void mapFromView(View view) {
            container = itemView;
            titleTv = (TextView) view.findViewById(R.id.tv_title);
            introTv = (TextView) view.findViewById(R.id.tv_intro);
            iconDv = (SimpleDraweeView) view.findViewById(R.id.dv_icon);
        }

        @Override
        public void setDataToView(final Info data) {
            titleTv.setText(data.getTitle());
            String content = data.getContent();
            if (TextUtils.isEmpty(content)) {
                content = "";
            }
            introTv.setText(content);
            if (TextUtils.isEmpty(data.getThumb())) {
                iconDv.setVisibility(View.GONE);
            } else {
                iconDv.setVisibility(View.VISIBLE);
                ImageFetcher.getInstance(iconDv.getContext()).fetchSmall(iconDv, data.getThumb());
            }
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    InfoDetailActivity.startFromChannel(v.getContext(), data.getInformationId());
                }
            });
        }
    }

}
