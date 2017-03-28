package com.codyy.erpsportal.repairs.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.repairs.models.entities.InquiryItem;

import org.joda.time.format.DateTimeFormat;

import butterknife.Bind;

/**
 * 处理员回复
 * Created by gujiajia on 2017/3/25.
 */

@LayoutId(R.layout.item_inquiry_reply)
public class InquiryReplyVh extends BindingRvHolder<InquiryItem> {

    @Bind(R.id.tv_name)
    TextView mNameTv;

    @Bind(R.id.tv_time)
    TextView mTimeTv;

    @Bind(R.id.tv_content)
    TextView mContentTv;

    public InquiryReplyVh(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(InquiryItem data) {
        mNameTv.setText( data.getHandlerName());
        mTimeTv.setText( DateTimeFormat.forPattern("YYYY-MM-dd HH:mm")
                .print(data.getTime()));
        mContentTv.setText( data.getContent());
    }
}
