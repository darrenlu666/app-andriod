package com.codyy.erpsportal.schooltv.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.widgets.AspectRatioDraweeView;
import com.codyy.erpsportal.schooltv.models.SchoolProgram;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

/**
 * 校园电视台-节目单-viewHolder
 * Created by poe on 17-3-14.
 */

public class SchoolProgramViewHolder extends BaseRecyclerViewHolder<SchoolProgram> {

    AspectRatioDraweeView mSdv;
    TextView mSchoolNameTv;
    TextView mStatusTv;
    TextView mClassTv;

    public SchoolProgramViewHolder(View itemView) {
        super(itemView);
        if(itemView.findViewById(R.id.sdv) !=null) mSdv = (AspectRatioDraweeView) itemView.findViewById(R.id.sdv);
        mSchoolNameTv = (TextView) itemView.findViewById(R.id.school_name_tv);
        mStatusTv = (TextView) itemView.findViewById(R.id.status_tv);
        mClassTv = (TextView) itemView.findViewById(R.id.class_detail_tv);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_school_tv_program_picture;
    }

    @Override
    public void setData(int position, SchoolProgram data) throws Throwable {
        if(null != mSdv){
            ImageFetcher.getInstance(mSchoolNameTv.getContext()).fetchSmall(mSdv,data.getThumbPath(),false);
        }
        mSchoolNameTv.setText(data.getProgramName());
        mClassTv.setText(DateUtil.getDateStr(data.getStartTime(),DateUtil.HH_MM)+"--"+DateUtil.getDateStr(data.getEndTime(),DateUtil.HH_MM));
        //set the state .
        switch (data.getStatus()){
            case SchoolProgram.STATUS_INIT://未开始
//                mStatusTv.setText("未开始");
                mStatusTv.setBackgroundResource(R.drawable.ic_tag_init_red);
                break;
            case SchoolProgram.STATUS_ON://进行中
//                mStatusTv.setText("进行中");
                mStatusTv.setBackgroundResource(R.drawable.ic_tag_on_yellow);
                break;
            case SchoolProgram.STATUS_END://已结束
//                mStatusTv.setText("已结束");
                mStatusTv.setBackgroundResource(R.drawable.ic_tag_end_green);
                break;
        }
        mStatusTv.setText(data.getStatusStr());
    }
}
