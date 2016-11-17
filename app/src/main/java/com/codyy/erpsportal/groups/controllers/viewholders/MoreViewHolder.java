package com.codyy.erpsportal.groups.controllers.viewholders;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ------------------
 * |      更多       |
 * ------------------
 * 在recyclerView的底部工多使用 .
 * Created by poe on 16-1-19.
 */
public class MoreViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar>{

    @Bind(R.id.content)TextView mTitleTextView;

    public MoreViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_class_blog_more;
    }

    @Override
    public void setData(int position,BaseTitleItemBar data) {
        mCurrentPosition = position;
        mData = data;
        mTitleTextView.setTextColor(Color.BLACK);
        mTitleTextView.setText(data.getBaseTitle());
    }
}
