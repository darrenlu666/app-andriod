package com.codyy.erpsportal.resource.controllers.viewholders;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.resource.models.entities.Document;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;

/**
 * 文档资源ViewHolder
 * Created by gujiajia on 2016/6/14.
 */
@LayoutId(R.layout.item_resource_document)
public class DocumentViewHolder extends BindingRvHolder<Document> {

    @Bind(R.id.res_icon)
    SimpleDraweeView mThumbDv;

    @Bind(R.id.title)
    TextView mNameTv;

    @Bind(R.id.tv_view_count)
    TextView mViewCountTv;

    @Bind(R.id.tv_download_count)
    TextView mDownloadCountTv;

    public DocumentViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(Document data) {
        if (TextUtils.isEmpty(data.getThumbUrl())) {
            mThumbDv.setImageURI("");
        } else {
            mThumbDv.setImageURI(Uri.parse(data.getThumbUrl()));
        }
        mNameTv.setText(data.getName());
        mViewCountTv.setText(data.getViewCount() + "");
        mDownloadCountTv.setText(data.getDownloadCount() + "");
//        JumpUtils.addGotoDocumentDetailsClickListener(itemView, data);
    }
}
