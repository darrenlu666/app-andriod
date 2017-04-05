package com.codyy.erpsportal.repairs.controllers.viewholders;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.homework.models.entities.student.ImageDetail;
import com.facebook.drawee.view.DraweeView;

/**
 * 图片组持者
 * Created by gujiajia on 2017/3/30.
 */

@LayoutId(R.layout.item_repair_image)
public class RepairImageVh extends ViewHolder {

    private DraweeView mDraweeView;

    public RepairImageVh(View itemView) {
        super(itemView);
        mDraweeView = (DraweeView) itemView.findViewById(R.id.dv_image);
    }

    public void bindData(ImageDetail imageDetail) {
        ImageFetcher.getInstance(mDraweeView).fetchImage(mDraweeView, "file://" + imageDetail.getPicUrl());
    }
}
