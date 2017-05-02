package com.codyy.erpsportal.repairs.controllers.viewholders;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.utils.ContextUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.models.entities.InquiryItem;
import com.codyy.erpsportal.resource.controllers.activities.PicturesActivity;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;

import org.joda.time.format.DateTimeFormat;

import java.util.List;

import butterknife.Bind;

/**
 * 追问
 * Created by gujiajia on 2017/3/25.
 */
@LayoutId(R.layout.item_inquiryer)
public class InquiryVh extends BindingRvHolder<InquiryItem> {

    private Context mContext;

    @Bind(R.id.tv_time)
    TextView mTimeTv;

    @Bind(R.id.tv_content)
    TextView mContentTv;

    @Bind(R.id.fbl_images_container)
    FlexboxLayout mImagesContainerFbl;

    public InquiryVh(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
    }

    @Override
    public void setDataToView(InquiryItem inquiryItem) {
        mTimeTv.setText(DateTimeFormat.forPattern("YYYY-MM-dd HH:mm").print(inquiryItem.getCreateTime()));
        mContentTv.setText(inquiryItem.getAppendDescription());
        GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(mContext.getResources());
        hierarchyBuilder.setPlaceholderImage(R.drawable.placeholder_repair, ScaleType.FIT_CENTER)
                .setFailureImage(R.drawable.audio_upload_failure, ScaleType.CENTER_INSIDE);

        final List<String> images = inquiryItem.getImgspath();
        if (images != null && images.size() > 0) {
            int existCount = mImagesContainerFbl.getChildCount();
            int needCount = images.size();
            if (existCount > needCount) {
                mImagesContainerFbl.removeViews( needCount, existCount - needCount);
            }

            for (int i = 0; i < needCount; i++) {
                SimpleDraweeView draweeView;
                if (i < existCount) {//已存在的DraweeView
                    draweeView = (SimpleDraweeView) mImagesContainerFbl.getChildAt(i);
                } else {draweeView = new SimpleDraweeView(itemView.getContext(), hierarchyBuilder.build());
                    draweeView.setAspectRatio(1f);
                    FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                            UIUtils.dip2px(mContext, 72),
                            LayoutParams.WRAP_CONTENT);
                    if (i % 3 == 0) {
                        layoutParams.wrapBefore = true;
                    }
                    mImagesContainerFbl.addView(draweeView, layoutParams);
                }
                ImageFetcher.getInstance(mContext).fetchSmall(draweeView, images.get(i));
                final int index = i;
                draweeView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Activity activity = ContextUtils.getActivity(mContext);
                        PicturesActivity.start(activity, images, index, true);
                    }
                });
            }
            mImagesContainerFbl.setVisibility(View.VISIBLE);
        } else {
            mImagesContainerFbl.setVisibility(View.GONE);
        }
    }
}
