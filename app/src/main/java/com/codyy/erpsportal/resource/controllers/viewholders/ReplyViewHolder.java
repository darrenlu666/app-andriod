package com.codyy.erpsportal.resource.controllers.viewholders;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator.LayoutId;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtils;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.resource.models.entities.DeleteReplyEvent;
import com.codyy.erpsportal.resource.models.entities.Reply;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 二级评论项视图保持者
 * Created by gujiajia on 2016/7/5.
 */
@LayoutId(R.layout.item_rethink_rely)
public class ReplyViewHolder extends BindingRvHolder<Reply> {

    private final static String TAG = "ReplyViewHolder";

    @Bind(R.id.tv_content)
    TextView contentTv;

    @Bind(R.id.tv_time)
    TextView timeTv;

    @Bind(R.id.btn_rely)
    Button replyBtn;

    public ReplyViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(final Reply reply, final int position) {
        Context context = itemView.getContext();
        SpannableStringBuilder ssb = new SpannableStringBuilder(context.getString(
                R.string.rely_lb, reply.getUserRealName(), reply.getReplyToName(), reply.getContent()));
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, reply.getUserRealName().length() + reply.getReplyToName().length() + 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        InputUtils.setEmojiSpan(context, ssb, (int) contentTv.getTextSize() + 5);
        Cog.d(TAG, "setDataToView:", ssb);
        contentTv.setText(ssb);
        timeTv.setText(DateUtils.formatLongTime(itemView.getContext(), reply.getCreateTime()));
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
