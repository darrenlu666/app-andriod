package com.codyy.erpsportal.groups.controllers.viewholders;

import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.my.MyClassRoom;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 圈组空间-详情-item
 * Created by poe on 16-1-15.
 */
public class ClassSpaceViewHolder extends BaseRecyclerViewHolder<MyClassRoom> {

    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_class)TextView mClassNameTv;
    @Bind(R.id.tv_school)TextView mSchoolNameTv;

    public ClassSpaceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_class_space;
    }

    @Override
    public void setData(int position,MyClassRoom data) {
        mCurrentPosition    =   position ;
        this.mData  =   data ;
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getHeadPic());
        mClassNameTv.setText(data.getClassName());
        mSchoolNameTv.setText(data.getSchoolName());
    }
}
