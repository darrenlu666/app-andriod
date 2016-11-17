package com.codyy.erpsportal.classroom.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.activity.ClassRecordedNoAreaActivity;
import com.codyy.erpsportal.classroom.models.AreaRecordedDetail;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域管理员往期录播  碎片
 * Created by ldh on 2016/7/5.
 */
public class AreaRecordListFragment extends LoadMoreFragment<AreaRecordedDetail.ListEntity, AreaRecordListFragment.AreaRecordedListHolder> implements TabsWithFilterActivity.OnFilterObserver {
    private String mFrom;
    public static final String ARG_FROM = "ARG_FROM";
    private String mAreaId = "";
    private String mSchoolId = "";
    private boolean mIsDirect;
    private String mUuid = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrom = getArguments().getString(ARG_FROM);
        mUuid = UserInfoKeeper.obtainUserInfo().getUuid();
        mAreaId = UserInfoKeeper.obtainUserInfo().getBaseAreaId();
        if (!UserInfoKeeper.obtainUserInfo().isArea()) {
            mSchoolId = UserInfoKeeper.obtainUserInfo().getSchoolId();
        }
    }

    public static AreaRecordListFragment newInstance(String from) {
        Bundle args = new Bundle();
        args.putString(ARG_FROM, from);
        AreaRecordListFragment fragment = new AreaRecordListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected ViewHolderCreator<AreaRecordedListHolder> newViewHolderCreator() {
        return new ViewHolderCreator<AreaRecordedListHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_recorded_list_area;
            }

            @Override
            protected AreaRecordedListHolder doCreate(View view) {
                return new AreaRecordedListHolder(view, getActivity(), mFrom);
            }
        };
    }

    @Override
    protected String getUrl() {
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_RECORD)) {
            return URLConfig.NEW_AREA_RECORD_LIST;
        } else {
            return URLConfig.GET_HISTORY_SCHOOL_RECORD_AREA_LIST;
        }
    }

    @Override
    protected void addParams(Map<String, String> params) {
        params.put("uuid", mUuid);
        params.put("areaId", mAreaId);
        params.put("schoolId", mSchoolId);
        params.put("type", mIsDirect ? "directly" : "nodirectly");
    }

    /**
     * 确认过滤时调用
     *
     * @param params 过滤参数，用于附加到url请求参数列表，实现过滤。
     */
    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        addParam("uuid", mUuid);
        addParam("areaId", mAreaId);
        addParam("schoolId", mSchoolId);
        addParam("type", mIsDirect ? "directly" : "nodirectly");
        loadData(true);
    }

    @Override
    protected List<AreaRecordedDetail.ListEntity> getList(JSONObject response) {
        return AreaRecordedDetail.parseResult(response);
    }

    /**
     * 筛选
     *
     * @param areaid
     * @param schoolId
     * @param isDirect
     */
    public void onFilter(String areaid, String schoolId, boolean isDirect) {
        mAreaId = areaid;
        mSchoolId = (schoolId == null ? "" : schoolId);
        mIsDirect = isDirect;
        Map<String, String> params = new HashMap<>();
        onFilterConfirmed(params);
    }

    public static class AreaRecordedListHolder extends RecyclerViewHolder<AreaRecordedDetail.ListEntity> {
        private RelativeLayout mContainer;
        private SimpleDraweeView mImgSdv;
        private TextView mSchoolNameTv;
        private TextView mAreaTv;
        private Context mContext;
        private String mFrom;

        public AreaRecordedListHolder(View itemView, Context context, String from) {
            super(itemView);
            mContext = context;
            mFrom = from;
        }

        @Override
        public void mapFromView(View view) {
            mContainer = (RelativeLayout) view.findViewById(R.id.rl_item);
            mImgSdv = (SimpleDraweeView) view.findViewById(R.id.sd_record_list_pic);
            mSchoolNameTv = (TextView) view.findViewById(R.id.tv_school_name);
            mAreaTv = (TextView) view.findViewById(R.id.tv_area);
        }

        @Override
        public void setDataToView(final AreaRecordedDetail.ListEntity data) {
            super.setDataToView(data);
            ImageFetcher.getInstance(mContext).fetchSmall(mImgSdv, data.getSchoolImgUrl());
            mSchoolNameTv.setText(data.getSchoolName());
            mAreaTv.setText(data.getArea());
            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//跳转到非区域列表
                    ClassRecordedNoAreaActivity.startActivity(mContext, data.getSchoolId(), mFrom);
                }
            });
        }
    }
}
