package com.codyy.erpsportal.repairs.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.repairs.models.entities.InquiryItem;
import com.google.android.flexbox.FlexboxLayout;

import org.joda.time.format.DateTimeFormat;

import butterknife.Bind;

/**
 * 追问
 * Created by gujiajia on 2017/3/25.
 */
@LayoutId(R.layout.item_inquiryer)
public class InquiryVh extends BindingRvHolder<InquiryItem> {

    @Bind(R.id.tv_time)
    TextView mTimeTv;

    @Bind(R.id.tv_content)
    TextView mContentTv;

    @Bind(R.id.fbl_images_container)
    FlexboxLayout mImagesContainerFbl;

    public InquiryVh(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(InquiryItem data) {
        mTimeTv.setText(DateTimeFormat.forPattern("YYYY-MM-dd HH:mm").print(data.getCreateTime()));
        mContentTv.setText(data.getAppendDescription());
    }
}
