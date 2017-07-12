package com.codyy.erpsportal.groups.controllers.viewholders;

import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.comment.BaseComment;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 博文评论-二级回复-更多.
 * Created by poe on 16-1-25.
 */
public  class CommentMoreViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar> {

    @Bind(R.id.text) TextView mContentTextView;

    public CommentMoreViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_blog_comment_reply_more;
    }

    @Override
    public void setData(int position,BaseTitleItemBar data) {
        mCurrentPosition    =   position ;
        mData  = data ;
        if(data instanceof BaseComment){
            BaseComment bc = (BaseComment) data;
            mContentTextView.setText(bc.getCommentContent());
        }
    }
}
