package com.codyy.erpsportal.rethink.controllers.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtils;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.Linker;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.rethink.models.entities.DeleteCommentEvent;
import com.codyy.erpsportal.rethink.models.entities.RethinkComment;
import com.facebook.drawee.view.SimpleDraweeView;

import de.greenrobot.event.EventBus;

/**
 * 评论视图持有者
 */
public class CommentViewHolder extends RecyclerViewHolder<RethinkComment> {

    private final static String TAG = "CommentViewHolder";

    /**
     * 用戶头像
     */
    SimpleDraweeView userPhotoDv;

    /**
     * 评论内容
     */
    TextView contentTv;

    /**
     * 评论时间
     */
    TextView timeTv;

    /**
     * 回复按钮
     */
    Button relyBtn;

    View commentDivider;

    /**
     * 上下文
     */
    Context context;

    public CommentViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void mapFromView(View view) {
        context = view.getContext();
        userPhotoDv = (SimpleDraweeView) view.findViewById(R.id.dv_user_photo);
        contentTv = (TextView) view.findViewById(R.id.tv_content);
        timeTv = (TextView) view.findViewById(R.id.tv_time);
        relyBtn = (Button) view.findViewById(R.id.btn_rely);
        commentDivider = view.findViewById(R.id.divider_comment);
    }

    @Override
    public void setDataToView(final RethinkComment rethinkComment, final int position) {
        ImageFetcher.getInstance(userPhotoDv).fetchImage(userPhotoDv, rethinkComment.getUserIcon());
        userPhotoDv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cog.d(TAG, "onUserPhotoClick");
                if (!UserInfo.checkIsManager(rethinkComment.getUserType())) {
                    Linker.linkUserIcon((Activity) v.getContext(), rethinkComment.getUserId());
                } else {
                    Toast.makeText(v.getContext(), "无法查看管理员信息！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        SpannableStringBuilder ssb = new SpannableStringBuilder(context.getString(
                R.string.comment_lb, rethinkComment.getUserRealName(), rethinkComment.getContent()));
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, rethinkComment.getUserRealName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        InputUtils.setEmojiSpan(context, ssb, (int) contentTv.getTextSize() + 5);
//            SpannableString ss = new SpannableString(context.getString(
//                    R.string.comment_lb, data.getUserRealName(), data.getContent()));
//            ss.setSpan(new StyleSpan(Typeface.BOLD), 0, data.getUserRealName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        contentTv.setText(ssb);
        try {
            timeTv.setText(DateUtils.formatLongTime(itemView.getContext(), Long.parseLong(rethinkComment.getCreateTime())));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            String timeStr = DateUtils.formatSimpleTime(itemView.getContext(), rethinkComment.getCreateTime());
            timeTv.setText(timeStr != null? timeStr: "");
        }
        if (rethinkComment.isMine()) {
//            relyBtn.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cog.d(TAG, "onCommentClick position=", position);
                    EventBus.getDefault().post(new DeleteCommentEvent(position));
                }
            });
        } else {
//            relyBtn.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(null);
            itemView.setClickable(false);
        }
        relyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cog.d(TAG, "onRelyClick");
                EventBus.getDefault().post(rethinkComment);
            }
        });
        if (rethinkComment.hasRelies()) {
            commentDivider.setVisibility(View.GONE);
        } else {
            commentDivider.setVisibility(View.VISIBLE);
        }
    }

    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }
}
