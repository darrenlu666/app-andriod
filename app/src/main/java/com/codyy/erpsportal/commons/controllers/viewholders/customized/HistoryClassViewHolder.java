package com.codyy.erpsportal.commons.controllers.viewholders.customized;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClass;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页-专递课堂-课程回放item
 * Created by poe on 16-6-1.
 */

public class HistoryClassViewHolder extends BaseRecyclerViewHolder<HistoryClass> {
    /**
     * 全屏排列-课程回放
     */
    public static final int ITEM_TYPE_BIG_IN_LINE =0x011;
    /**
     * 两个并排-课程回放
     * 0x110 - 0x200为不希望绘制divider的值
     */
    public static final int ITEM_TYPE_DOUBLE_IN_LINE =0x112;

    @Bind(R.id.sdv)SimpleDraweeView mSDV;
    @Bind(R.id.tv_school)TextView mSchoolTextView;
    @Bind(R.id.tv_level_subject_teacher)TextView mLevelSTTextView;

    public HistoryClassViewHolder(View itemView) {
        super(itemView);
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
        // 16-6-1 set data content .
        ImageFetcher.getInstance(mSchoolTextView.getContext())
                .fetchSmallWithDefault(mSDV,data.getThumb(),R.drawable.icon_default_video,true);
        mSchoolTextView.setText(data.getSchoolName());
        StringBuilder sb = new StringBuilder();
        if(!TextUtils.isEmpty(data.getClasslevelName())) sb.append(data.getClasslevelName());
        if(!TextUtils.isEmpty(data.getSubjectName())) sb.append("/"+data.getSubjectName());
        if(!TextUtils.isEmpty(data.getTeacherName())) sb.append("/"+data.getTeacherName());
        mLevelSTTextView.setText(sb.toString());
    }
}
