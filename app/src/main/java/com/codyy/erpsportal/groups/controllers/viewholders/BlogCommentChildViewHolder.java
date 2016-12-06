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
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.DateUtils;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.widgets.SuperTextView;
import com.codyy.erpsportal.commons.models.entities.comment.BaseComment;
import java.net.URLDecoder;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 博文评论-二级item - viewHolder .
 * Created by poe on 16-1-15.
 */
public class BlogCommentChildViewHolder extends BaseRecyclerViewHolder<BaseComment> {

    @Bind(R.id.stv_content)SuperTextView mContentTextView;
    @Bind(R.id.tv_create_time)TextView mCreateTimeTextView;

    public BlogCommentChildViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_blog_comment;
    }

    @Override
    public void setData(int position,BaseComment data) {
        mCurrentPosition    =   position;
        mData   =   data ;
        String message = data.getCommentContent();
        message = PullXmlUtils.replaceMsg(message);
        try{
            message = URLDecoder.decode(message);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        String name = data.getRealName()+"回复"+data.getReplyName()+":" ;
        Spannable spannable = new SpannableString(name+message);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),0,name.length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK),0,name.length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mContentTextView.setText(spannable);
        if(!TextUtils.isEmpty(data.getCreateTime())){
            String time = DateUtils.formatLongTime(mContentTextView.getContext() , Long.parseLong(data.getCreateTime())) ;
            mCreateTimeTextView.setText(time);
        }else{
            mCreateTimeTextView.setText("");
        }
    }
}
