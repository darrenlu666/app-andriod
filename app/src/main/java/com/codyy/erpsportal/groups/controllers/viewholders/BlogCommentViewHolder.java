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
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.DateUtils;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.widgets.SuperTextView;
import com.codyy.erpsportal.commons.models.entities.comment.BaseComment;
import com.facebook.drawee.view.SimpleDraweeView;
import java.net.URLDecoder;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 博文评论-一级item - viewHolder .
 * Created by poe on 16-1-15.
 */
public class BlogCommentViewHolder extends BaseRecyclerViewHolder<BaseComment> {

    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.stv_content)SuperTextView mContentTextView;
    @Bind(R.id.tv_create_time)TextView mCreateTimeTextView;
    @Bind(R.id.tv_reply)TextView mReplyTextView;

    public BlogCommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
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
        message = PullXmlUtils.replaceMsg(message);
        try{
            message = URLDecoder.decode(message);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        Spannable spannable = new SpannableString(data.getRealName()+":"+message);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),0,data.getRealName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK),0,data.getRealName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mContentTextView.setText(spannable);
        //head pic
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getHeadPic());
        if(!TextUtils.isEmpty(data.getCreateTime())){
//            String time = DateUtil.getDateStr(Long.parseLong(data.getCreateTime()),"yyyy-MM-dd HH:mm");
            String time = DateUtils.formatLongTime(mContentTextView.getContext() , Long.parseLong(data.getCreateTime())) ;
            mCreateTimeTextView.setText(time);
        }else{
            mCreateTimeTextView.setText("");
        }

       /* if(null != mUserInfo && mUserInfo.getBaseUserId().equals(data.getBaseUserId())){
            mReplyTextView.setVisibility(View.INVISIBLE);
        }else{
            mReplyTextView.setVisibility(View.VISIBLE);
        }*/
        //自己可以回复自己
        mReplyTextView.setVisibility(View.VISIBLE);
    }
}
