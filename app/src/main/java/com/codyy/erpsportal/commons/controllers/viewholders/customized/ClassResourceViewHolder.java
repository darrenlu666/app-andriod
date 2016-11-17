package com.codyy.erpsportal.commons.controllers.viewholders.customized;

import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.my.ClassResource;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页-专递课堂-课程回放item
 * Created by poe on 16-6-1.
 */
public class ClassResourceViewHolder extends BaseRecyclerViewHolder<ClassResource> {

    @Bind(R.id.sdv)SimpleDraweeView mSDV;
    @Bind(R.id.tv_school)TextView mSchoolTextView;

    public ClassResourceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_class_space_resource;
    }

    @Override
    public void setData(int position, ClassResource data) {
        mCurrentPosition = position;
        mData   =   data;
        if(null == data) return;
        ImageFetcher.getInstance(mSchoolTextView.getContext()).fetchImage(mSDV,data.getResThumb());
        mSchoolTextView.setText(data.getResourceName());
    }
}
