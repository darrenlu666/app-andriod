package com.codyy.erpsportal.groups.controllers.viewholders;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.widgets.SuperTextView;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.comment.BaseComment;
import com.codyy.erpsportal.onlinemeetings.utils.EmojiUtils;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import java.net.URLDecoder;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 博文评论-一级item - viewHolder .
 * Created by poe on 16-1-15.
 */
public class CommentViewHolder extends BaseRecyclerViewHolder<BaseComment> {

    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.stv_content)SuperTextView mContentTextView;
    @Bind(R.id.tv_create_time)TextView mCreateTimeTextView;
    @Bind(R.id.tv_reply)TextView mReplyTextView;
    private UserInfo mUserInfo ;

    private boolean mHideReply;

    public CommentViewHolder(View itemView, boolean hideReply) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mHideReply = hideReply;
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_blog_comment;
    }

    @Override
    public void setData(int pos ,BaseComment data) {
        mCurrentPosition    =   pos ;
        mData   =   data ;
        String message = data.getCommentContent();
        message = EmojiUtils.replaceMsg(message);
        message = URLDecoder.decode(message);
        Spannable spannable = new SpannableString(data.getRealName()+":"+message);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),0,data.getRealName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK),0,data.getRealName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mContentTextView.setText(spannable);
        //head pic
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getHeadPic());
        if(!TextUtils.isEmpty(data.getStrCreateTime())){
            mCreateTimeTextView.setText(data.getStrCreateTime());
        }else{
            mCreateTimeTextView.setText("");
        }

        if (mHideReply) {
            mReplyTextView.setVisibility(View.INVISIBLE);
        } else {
            if (null != mUserInfo && mUserInfo.getBaseUserId().equals(data.getBaseUserId())) {
                mReplyTextView.setVisibility(View.INVISIBLE);
            } else {
                mReplyTextView.setVisibility(View.VISIBLE);
            }
        }

    }
}
