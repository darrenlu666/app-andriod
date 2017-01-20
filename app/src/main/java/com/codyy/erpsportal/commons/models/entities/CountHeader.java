package com.codyy.erpsportal.commons.models.entities;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;

/**
 * Created by gujiajia on 2017/1/17.
 */

public class CountHeader extends CommentListHeader {

    private int mCount;

    @Override
    public int obtainLayoutId() {
        return R.layout.header_count;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(obtainLayoutId(), parent, false);
        return new CountHeaderViewHolder(view);
    }

    @Override
    public void update(ViewHolder holder) {
        ((CountHeaderViewHolder)holder).setData(mCount);
    }

    public void updateCount(int total) {
        mCount = total;
    }

    private class CountHeaderViewHolder extends ViewHolder{

        private TextView headerTv;

        public CountHeaderViewHolder(View itemView) {
            super(itemView);
            headerTv = (TextView) itemView.findViewById(R.id.tv_count);
        }

        public void setData(int count) {
            String countText = itemView.getResources().getString(R.string.total_comments, count);
            headerTv.setText(countText);
        }
    }
}
