package com.codyy.erpsportal.repairs.controllers.viewholders;

import android.view.View;
import android.widget.CheckedTextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.repairs.models.entities.RepairFilterItem;

/**
 * Created by gujiajia on 2017/3/22.
 */
@LayoutId(R.layout.item_checked_text)
public class CheckedTextViewHolder extends RecyclerViewHolder<RepairFilterItem> {

    private CheckedTextView mContentTv;

    public CheckedTextViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void mapFromView(View view) {
        mContentTv = (CheckedTextView) view;
    }

    public void setDataToView(RepairFilterItem item, boolean selected) {
        mContentTv.setText(item.content());
        mContentTv.setChecked(selected);
    }
}
