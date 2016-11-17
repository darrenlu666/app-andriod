package com.codyy.erpsportal.perlcourseprep.controllers.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.rethink.controllers.activities.SubjectMaterialPicturesActivity;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity.OnFilterObserver;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreGridFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator.LayoutId;
import com.codyy.erpsportal.perlcourseprep.controllers.fragments.SubjectMaterialFragment.SmViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.perlcourseprep.models.entities.SubjectMaterialPicture;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 学科素材碎片
 */
public class SubjectMaterialFragment extends LoadMoreGridFragment<SubjectMaterialPicture, SmViewHolder> implements OnFilterObserver {

    private final static String TAG = "SubjectMaterialFragment";

    public SubjectMaterialFragment() {}

    private UserInfo mUserInfo;

    public final static String ARG_USER_INFO = "com.codyy.erpsportal.USER_INFO";

    /**
     *
     */
    public static SubjectMaterialFragment newInstance(UserInfo userInfo) {
        SubjectMaterialFragment fragment = new SubjectMaterialFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_USER_INFO, userInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(ARG_USER_INFO);
        }
    }

    @Override
    protected ViewHolderCreator<SmViewHolder> newViewHolderCreator() {
        return new EasyVhrCreator<>(SmViewHolder.class);
    }

    @Override
    protected String getUrl() {
        return URLConfig.SUBJECT_MATERIAL;
    }

    @Override
    protected List<SubjectMaterialPicture> getList(JSONObject response) {
        return SubjectMaterialPicture.JSON_PARSER.parseArray(response.optJSONArray("list"));
    }

    @Override
    protected void addParams(Map<String, String> params) {
        params.put("uuid", mUserInfo.getUuid());
    }

    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        if (params != null) {
            updateParamsBaseOnMap(params, "classLevelId", "baseClasslevelId");
            updateParamsBaseOnMap(params, "subjectId", "baseSubjectId");
            loadData(true);
        }
    }

    /**
     * 学科素材组件保持者
     */
    @LayoutId(R.layout.item_resource)
    public static class SmViewHolder extends RecyclerViewHolder<SubjectMaterialPicture> {

        private SimpleDraweeView mDraweeView;

        private TextView mTitleTv;

        public SmViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void mapFromView(View view) {
            mDraweeView = (SimpleDraweeView) view.findViewById(R.id.res_icon);
            mTitleTv = (TextView) view.findViewById(R.id.title);
        }

        @Override
        public void setDataToView(final List<SubjectMaterialPicture> list, final int position) {
            SubjectMaterialPicture picture = list.get(position);
            mTitleTv.setText(picture.getName());
            ImageFetcher.getInstance(itemView.getContext())
                    .fetchSmall(mDraweeView, picture.getUrl());
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cog.d(TAG, "onSubjectMaterialItemClick position=", position);
                    SubjectMaterialPicturesActivity.start((Activity) itemView.getContext(), list, position);
                }
            });
        }
    }
}
