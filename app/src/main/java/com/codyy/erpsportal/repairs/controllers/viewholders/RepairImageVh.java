package com.codyy.erpsportal.repairs.controllers.viewholders;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.repairs.models.entities.UploadingImage;
import com.facebook.drawee.view.DraweeView;

import butterknife.Bind;

/**
 * 图片组持者
 * Created by gujiajia on 2017/3/30.
 */

@LayoutId(R.layout.item_repair_image)
public class RepairImageVh extends BindingRvHolder<UploadingImage> {

    @Bind(R.id.dv_image)
    DraweeView mDraweeView;

    @Bind(R.id.iv_uploading)
    ImageView mUploadingIv;

    @Bind(R.id.ll_cover)
    LinearLayout mCoverLl;

    @Bind(R.id.iv_failed)
    ImageView mFailedIv;

    public RepairImageVh(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(UploadingImage uploadingImage) {
        ImageFetcher.getInstance(mDraweeView).fetchImage(mDraweeView, "file://" + uploadingImage.getPath());
        AnimationDrawable ad = (AnimationDrawable) mUploadingIv.getBackground();
        switch (uploadingImage.getStatus()) {
            case UploadingImage.STATUS_UPLOADING:
                ad.start();
                mCoverLl.setVisibility(View.VISIBLE);
                mFailedIv.setVisibility(View.GONE);
                break;
            case UploadingImage.STATUS_ERROR:
                ad.stop();
                mCoverLl.setVisibility(View.INVISIBLE);
                mFailedIv.setVisibility(View.VISIBLE);
                break;
            default:
                ad.stop();
                mCoverLl.setVisibility(View.INVISIBLE);
                mFailedIv.setVisibility(View.GONE);
        }
    }
}
