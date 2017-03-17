package com.codyy.erpsportal.schooltv.controllers.viewholders;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.widgets.AspectRatioDraweeView;
import com.codyy.erpsportal.schooltv.models.SchoolVideo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 校园电视台-往期视频-viewHolder
 * Created by poe on 17-3-14.
 */

public class SchoolVideoViewHolder extends BaseRecyclerViewHolder<SchoolVideo> {

    @Bind(R.id.sdv) AspectRatioDraweeView mSdv;
    @Bind(R.id.school_name_tv) TextView mSchoolNameTv;
    @Bind(R.id.master_tv_tag) TextView mMasterTagTv;
    @Bind(R.id.master_tv) TextView mMasterTv;
    @Bind(R.id.count_tv) TextView mCountTv;
    @Bind(R.id.date_tv) TextView mDateTv;

    public SchoolVideoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_school_tv_history;
    }

    @Override
    public void setData(int position, SchoolVideo data) throws Throwable {

        ImageFetcher.getInstance(mSdv.getContext()).fetchSmall(mSdv,data.getThumbPath());
        mSchoolNameTv.setText(data.getProgramName());
        if(TextUtils.isEmpty(data.getSpeaker())){
            mMasterTagTv.setVisibility(View.INVISIBLE);
            mMasterTv.setVisibility(View.INVISIBLE);
        }else{
            mMasterTagTv.setVisibility(View.VISIBLE);
            mMasterTv.setVisibility(View.VISIBLE);
            mMasterTv.setText(data.getSpeaker());
        }
        mCountTv.setText(data.getViewCnt()+"");
        mDateTv.setText(DateUtil.getDateStr(data.getStartTime(),DateUtil.DEF_FORMAT));
    }
}
