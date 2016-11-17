package com.codyy.erpsportal.rethink.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.rethink.models.entities.CommentListHeader;

/**
 * 教学反思评论头
 * Created by gujiajia on 2016/7/5.
 */
public class HeadViewHolder extends RecyclerViewHolder<CommentListHeader> {

    /**
     * 标题
     */
    private TextView titleTv;

    /**
     * 时间
     */
    private TextView timeTv;

    /**
     * 评论数量
     */
    private TextView commentsCountTv;

    public HeadViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void mapFromView(View view) {
        titleTv = (TextView) view.findViewById(R.id.tv_title);
        timeTv = (TextView) view.findViewById(R.id.tv_time);
        commentsCountTv = (TextView) view.findViewById(R.id.tv_comments_count);
    }

    @Override
    public void setDataToView(CommentListHeader data) {
        titleTv.setText(data.getTitle());
        timeTv.setText(data.getTime());
        commentsCountTv.setText(itemView.getContext().getString(R.string.n_comments, data.getCount()));
    }
}
