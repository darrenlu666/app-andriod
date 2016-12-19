package com.codyy.erpsportal.rethink.controllers.viewholders;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtils;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.rethink.models.entities.DeleteReplyEvent;
import com.codyy.erpsportal.rethink.models.entities.RethinkReply;

import de.greenrobot.event.EventBus;

/**
 * 二级评论项视图保持者
 * Created by gujiajia on 2016/7/5.
 */
public class ReplyViewHolder extends RecyclerViewHolder<RethinkReply> {

    private final static String TAG = "ReplyViewHolder";

    TextView contentTv;

    TextView timeTv;

    Button replyBtn;

    Context context;

    public ReplyViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void mapFromView(View view) {
        context = view.getContext();
        contentTv = (TextView) view.findViewById(R.id.tv_content);
        timeTv = (TextView) view.findViewById(R.id.tv_time);
        replyBtn = (Button) view.findViewById(R.id.btn_rely);
    }

    @Override
    public void setDataToView(final RethinkReply reply, final int position) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(context.getString(
                R.string.rely_lb, reply.getUserRealName(), reply.getReplyToName(), reply.getContent()));
        ssb.setSpan(new ForegroundColorSpan(0xff222222), 0, reply.getUserRealName().length() + reply.getReplyToName().length() + 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        InputUtils.setEmojiSpan(context, ssb, (int) contentTv.getTextSize() + 5);
        Cog.d(TAG, "setDataToView:", ssb);
        contentTv.setText(ssb);
        try {
            timeTv.setText(DateUtils.formatLongTime(itemView.getContext(), Long.parseLong(reply.getCreateTime())));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            String timeStr = DateUtils.formatSimpleTime(itemView.getContext(), reply.getCreateTime());
            timeTv.setText(timeStr != null? timeStr: "");
        }
        if (reply.isMine()) {
//            replyBtn.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cog.d(TAG, "onCommentClick");
                    EventBus.getDefault().post(new DeleteReplyEvent(position));
                }
            });
        } else {
//            replyBtn.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(null);
            itemView.setClickable(false);
        }
        replyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cog.d(TAG, "onReplyClick");
                EventBus.getDefault().post(reply);
            }
        });
    }
}
