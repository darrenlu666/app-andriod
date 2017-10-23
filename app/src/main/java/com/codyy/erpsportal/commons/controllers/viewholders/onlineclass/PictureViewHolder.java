package com.codyy.erpsportal.commons.controllers.viewholders.onlineclass;

import android.view.View;
import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.AspectRatioImageView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 圈组-博文-下一页-热门博文item
 * Created by poe on 16-1-15.
 */
public class PictureViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar> {

    @Bind(R.id.sdv)
    AspectRatioImageView mSdv;

    public PictureViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_picture_banner_sip;
    }


    @Override
    public void setData(int position,BaseTitleItemBar data) {
        //do nothing .
    }
}
