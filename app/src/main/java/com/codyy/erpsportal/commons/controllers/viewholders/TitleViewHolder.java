package com.codyy.erpsportal.commons.controllers.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup.LayoutParams;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.widgets.TitleItemBar;

/**
 * 小标题ViewHolder
 */
public class TitleViewHolder extends RecyclerView.ViewHolder {

    TitleItemBar mTitleItemBar;

    public TitleViewHolder(TitleItemBar itemView) {
        super(itemView);
        mTitleItemBar = itemView;
    }

    public TitleItemBar getTitleItemBar() {
        return mTitleItemBar;
    }

    public static TitleViewHolder create(Context context) {
        TitleItemBar titleItemBar = new TitleItemBar(context);
        titleItemBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                context.getResources().getDimensionPixelSize(R.dimen.title_item_bar_height)));
        return new TitleViewHolder( titleItemBar);
    }
}
