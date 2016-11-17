package com.codyy.erpsportal.perlcourseprep.controllers.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator.LayoutId;
import com.codyy.erpsportal.perlcourseprep.models.entities.CommentsHeader;

import java.text.NumberFormat;

import butterknife.Bind;

/**
 * 教学反思评论头
 * Created by gujiajia on 2016/7/5.
 */
@LayoutId(R.layout.header_pcp_comments)
public class HeaderHolder extends BindingRvHolder<CommentsHeader> {

    /**
     * 总数
     */
    @Bind(R.id.tv_total)
    TextView totalTv;

    /**
     * 评分星
     */
    @Bind(R.id.rb_rate)
    RatingBar rateRb;

    /**
     * 评分
     */
    @Bind(R.id.tv_score)
    TextView scoreTv;

    public HeaderHolder(View itemView) {
        super(itemView);
    }


    @Override
    public void setDataToView(CommentsHeader data) {
        Context context = itemView.getContext();
        totalTv.setText( context.getString(R.string.total_comments, data.getTotal()));
        rateRb.setRating(data.getRate()/2);
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(1);
        numberFormat.setMinimumFractionDigits(0);
        scoreTv.setText( context.getString(R.string.n_score, numberFormat.format(data.getRate())));
//        titleTv.setText(data.getTitle());
//        timeTv.setText(data.getTime());
//        commentsCountTv.setText(itemView.getContext().getString(R.string.n_comments, data.getCount()));
    }
}
