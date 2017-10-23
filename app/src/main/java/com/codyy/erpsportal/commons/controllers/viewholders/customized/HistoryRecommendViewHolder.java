package com.codyy.erpsportal.commons.controllers.viewholders.customized;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClass;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页-直录播课堂-课程回放item
 * Created by poe on 16-6-1.
 */

public class HistoryRecommendViewHolder extends BaseRecyclerViewHolder<HistoryClass> {
    @Bind(R.id.sdv)SimpleDraweeView mSDV;
    @Bind(R.id.tv_school)TextView mSchoolTextView;
    @Bind(R.id.tv_level_subject_teacher)TextView mLevelSTTextView;

    public HistoryRecommendViewHolder(View itemView) {
        super(itemView);
        Cog.i("history:"+itemView.getTop()+"/"+itemView.getLeft()+"/"+itemView.getRight());
        ButterKnife.bind(this,itemView);
    }
    @Override
    public int obtainLayoutId() {
        return R.layout.item_customized_history_class;
    }

    @Override
    public void setData(int position, HistoryClass data) {
        mCurrentPosition = position;
        mData   =   data;
        if(null == data) return;
        ImageFetcher.getInstance(mSchoolTextView.getContext())
                .fetchImageWithDefault(mSDV,data.getThumb(),R.drawable.icon_default_video,true);
        mSchoolTextView.setText(data.getSchoolName());
        StringBuilder jpsb = new StringBuilder("");

        //年級
        if(!TextUtils.isEmpty(data.getClasslevelName())) jpsb.append(data.getClasslevelName());
        //學科
        if(!TextUtils.isEmpty(data.getSubjectName())) {
            if(jpsb.toString().length()>0) jpsb.append("/");
            jpsb.append(data.getSubjectName());
        }
        //姓名
        if(!TextUtils.isEmpty(data.getTeacherName())){
            if(jpsb.toString().length()>0) jpsb.append("/");
            jpsb.append(data.getTeacherName());
        }
        mLevelSTTextView.setText(UiMainUtils.combineStrs(data.getClasslevelName(),data.getSubjectName(),data.getTeacherName()));
    }
}
