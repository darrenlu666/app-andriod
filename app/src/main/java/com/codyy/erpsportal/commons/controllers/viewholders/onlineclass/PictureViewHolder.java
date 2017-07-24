package com.codyy.erpsportal.commons.controllers.viewholders.onlineclass;

import android.graphics.drawable.Animatable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.onlineclass.SipNetResearch;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.AspectRatioDraweeView;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.AspectRatioImageView;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 圈组-博文-下一页-热门博文item
 * Created by poe on 16-1-15.
 */
public class PictureViewHolder extends BaseRecyclerViewHolder<SipNetResearch> {

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
    public void setData(int position,SipNetResearch data) {
        //do nothing .
    }
}
