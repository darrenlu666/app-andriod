package com.codyy.erpsportal.resource.controllers.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator.LayoutId;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtils;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.Linker;
import com.codyy.erpsportal.resource.models.entities.Comment;
import com.codyy.erpsportal.resource.models.entities.DeleteCommentEvent;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 评论视图持有者
 * Created by gujiajia
 */
@LayoutId(R.layout.item_rethink_comment)
public class CommentViewHolder extends BindingRvHolder<Comment> {

    private final static String TAG = "CommentViewHolder";

    /**
     * 用戶头像
     */
    @Bind(R.id.dv_user_photo)
    SimpleDraweeView userPhotoDv;

    /**
     * 评论内容
     */
    @Bind(R.id.tv_content)
    TextView contentTv;

    /**
     * 评论时间
     */
    @Bind(R.id.tv_time)
    TextView timeTv;

    /**
     * 回复按钮
     */
    @Bind(R.id.btn_rely)
    Button relyBtn;

    @Bind(R.id.divider_comment)
    View commentDivider;

    public CommentViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(final Comment comment, final int position) {
        Context context = itemView.getContext();
        ImageFetcher.getInstance(userPhotoDv).fetchImage(userPhotoDv, comment.getUserIcon());
        userPhotoDv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cog.d(TAG, "onUserPhotoClick");
                if (!UserInfo.checkIsManager(comment.getUserType())) {
                    Linker.linkUserIcon((Activity) v.getContext(), comment.getUserId());
                } else {
                    Toast.makeText(v.getContext(), "无法查看管理员信息！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        SpannableStringBuilder ssb = new SpannableStringBuilder(context.getString(
                R.string.comment_lb, comment.getUserRealName(), comment.getContent()));
        ssb.setSpan(new ForegroundColorSpan(0xff222222), 0, comment.getUserRealName().length()+1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        InputUtils.setEmojiSpan(context, ssb, (int) contentTv.getTextSize() + 5);
        contentTv.setText(ssb);
        timeTv.setText(DateUtils.formatLongTime(itemView.getContext(), comment.getCreateTime()));
        if (comment.isMine()) {
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cog.d(TAG, "onCommentClick position=", position);
                    EventBus.getDefault().post(new DeleteCommentEvent(position));
                }
            });
        } else {
            itemView.setOnClickListener(null);
            itemView.setClickable(false);
        }
        relyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cog.d(TAG, "onRelyClick");
                EventBus.getDefault().post(comment);
            }
        });
        if (comment.hasRelies()) {
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
