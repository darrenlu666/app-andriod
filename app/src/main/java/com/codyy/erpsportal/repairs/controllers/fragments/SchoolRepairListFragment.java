package com.codyy.erpsportal.repairs.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity.OnFilterObserver;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerAdapter.OnItemClickListener;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
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

/**
 * 有报修记录的学校列表碎片
 */
public class SchoolRepairListFragment extends LoadMoreFragment<RepairSchool,RepairSchoolVh> implements OnFilterObserver {

    private UserInfo mUserInfo;

    public SchoolRepairListFragment() {
    }

    public static SchoolRepairListFragment newInstance() {
        SchoolRepairListFragment fragment = new SchoolRepairListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(Extra.USER_INFO);
        }
    }

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    protected ViewHolderCreator<RepairSchoolVh> newViewHolderCreator() {
        return new EasyVhrCreator<>(RepairSchoolVh.class);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnItemClickListener(new OnItemClickListener<RepairSchool>() {
            @Override
            public void onItemClick(int position, RepairSchool repairSchool) {
                SchoolRepairsActivity.start(getContext(),
                        mUserInfo,
                        repairSchool.getSchoolId(),
                        repairSchool.getSchoolName());
            }
        });
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_REPAIRS_SCHOOLS;
    }

    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", mUserInfo.getUuid());
    }

    @Override
    protected List<RepairSchool> getList(JSONObject response) {
        Type listType = new TypeToken<List<RepairSchool>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(response.optJSONArray("data").toString(), listType);
    }

    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        updateParamsBaseOnMap(params, "baseAreaId");
        loadData(true);
    }

    @LayoutId(R.layout.item_repair_school)
    public static class RepairSchoolVh extends BindingRvHolder<RepairSchool>{

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
        public void setDataToView(final RepairSchool repairSchool) {
            Context context = itemView.getContext();
            mNameTv.setText(repairSchool.getAreaName() + "-" + repairSchool.getSchoolName());
            mTotalCountTv.setText(context.getString(R.string.malfunction_count, repairSchool.getAllCount()));
            String handledCountStr = context.getString(R.string.handled_count, repairSchool.getDealCount());
            SpannableStringBuilder ssb = new SpannableStringBuilder(handledCountStr);
            ForegroundColorSpan foreColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_color));
            ssb.setSpan(foreColorSpan, 7, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mHandledCountTv.setText(ssb);
        }
    }
}
