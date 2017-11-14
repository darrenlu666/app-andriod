package com.codyy.erpsportal.rethink.controllers.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.rethink.controllers.activities.RethinkDetailsActivity;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity.OnFilterObserver;
import com.codyy.erpsportal.rethink.controllers.fragments.RethinkListFragment.RethinkViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsVhrCreator;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.rethink.models.entities.Rethink;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 教学反思列表
 * Created by gujiajia on 2015/12/31.
 */
public class RethinkListFragment extends LoadMoreFragment<Rethink ,RethinkViewHolder> implements OnFilterObserver {

    private final static String TAG = "RethinkListFragment";

    public final static String ARG_MORE = "more";

    public final static String ARG_BASE_AREA_ID = "baseAreaId";

    public final static String ARG_SCHOOL_ID = "schoolId";

    private UserInfo mUserInfo;

    /**
     * 是否是更多
     */
    private boolean mMore;

    private String mBaseAreaId;

    private String mSchoolId;

    @Override
    protected AbsVhrCreator<RethinkViewHolder> newViewHolderCreator() {
        return new AbsVhrCreator<RethinkViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_rethink;
            }

            @Override
            protected RethinkViewHolder doCreate(View view) {
                return new RethinkViewHolder(view);
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        if(getArguments() != null) {
            mMore = getArguments().getBoolean(ARG_MORE);
            mBaseAreaId = getArguments().getString(ARG_BASE_AREA_ID);
            mSchoolId = getArguments().getString(ARG_SCHOOL_ID);
        }
    }

    @Override
    protected String getUrl() {
        return URLConfig.RETHINK_LIST;
    }

    @Override
    protected List<Rethink> getList(JSONObject response) {
        return Rethink.JSON_PARSER.parseArray(response.optJSONArray("list"));
    }

    @Override
    protected void addParams(Map<String, String> map) {
        addParam("uuid", mUserInfo.getUuid());
        if (mMore) {
            addParam("loc", "home");
            if (!TextUtils.isEmpty(mSchoolId))
                addParam("schoolId", mSchoolId);
            if (!TextUtils.isEmpty(mBaseAreaId))
                addParam("baseAreaId", mBaseAreaId);
        }
    }

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        Cog.d(TAG, "onFilterConfirmed params=", params);
        if (params != null) {
            updateParamsBaseOnMap(params, "schoolId");
            if (TextUtils.isEmpty(params.get("schoolId"))) {
                updateParamsBaseOnMap(params, "baseAreaId");
            } else {
                removeParam("baseAreaId");
            }
            updateParamsBaseOnMap(params, "classLevelId", "classlevelId");
            updateParamsBaseOnMap(params, "subjectId");
            updateParamsBaseOnMap(params, "type");
            loadData(true);
        }
    }

    public static class RethinkViewHolder extends RecyclerViewHolder<Rethink>{

        private SimpleDraweeView draweeView;

        private TextView titleTv;

        private TextView summaryTv;

        public RethinkViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void mapFromView(View view) {
            draweeView = (SimpleDraweeView) view.findViewById(R.id.dv_icon);
            titleTv = (TextView) view.findViewById(R.id.tv_title);
            summaryTv = (TextView) view.findViewById(R.id.tv_summary);
        }

        @Override
        public void setDataToView(final Rethink data) {
            if (!TextUtils.isEmpty(data.getIcon())) {
                ImageFetcher.getInstance(itemView)
                        .fetchImage(draweeView, URLConfig.IMAGE_URL + data.getIcon());
            } else {
                draweeView.setImageURI("");
            }
            titleTv.setText(data.getTitle());
            if (!TextUtils.isEmpty(data.getSummary())) {
                summaryTv.setText(data.getSummary());
            } else {
                summaryTv.setText("");
            }
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    RethinkDetailsActivity.start((Activity)v.getContext(), data.getId());
                }
            });
        }
    }
}
