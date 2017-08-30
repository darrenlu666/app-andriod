package com.codyy.erpsportal.commons.controllers.viewholders.interact;

import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.onlineteach.models.entities.NetDocument;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 互动听课-详情-文档ViewHolder
 * Created by poe on 16-6-23.
 */
public class DocListViewHolder extends BaseRecyclerViewHolder<Object> {

    @Bind(R.id.tv_resource_name) TextView mSchoolTextView;

    public DocListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_resource_prepare_lseeons;
    }

    @Override
    public void setData(int position, Object data) {
        mCurrentPosition = position;
        mData   =   data;
        if(data instanceof String){
            mSchoolTextView.setText((String) data);
        }else if(data instanceof NetDocument){
            NetDocument document = (NetDocument) data;
            mSchoolTextView.setText(document.getDocumentName());
        }
    }
}
