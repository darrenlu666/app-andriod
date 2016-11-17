package com.codyy.erpsportal.weibo.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.weibo.models.entities.WeiBoComment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by kmdai on 16-2-19.
 */
public class WeiBoCommentAdapter extends RefreshBaseAdapter<WeiBoComment> {
    private Context mContext;
    private OnGetMoreComment mGetMoreComment;

    public WeiBoCommentAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    public WeiBoCommentAdapter(Context mContext, List<WeiBoComment> mDatas) {
        super(mContext, mDatas);
        this.mContext = mContext;
    }

    public void setGetMoreComment(OnGetMoreComment mGetMoreComment) {
        this.mGetMoreComment = mGetMoreComment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        switch (viewType) {
            case WeiBoComment.TYPE_PARENT:
                return new ParentComment(inflater.inflate(R.layout.weibo_comment_item, parent, false));
            case WeiBoComment.TYPE_CHILD:
                return new ChildComment(inflater.inflate(R.layout.weibo_comment_item_child, parent, false));
            case WeiBoComment.TYPE_MORE:
                return new MoreHolder(inflater.inflate(R.layout.weibo_comment_item_more, parent, false));
        }
        return new RecyclerView.ViewHolder(new View(mContext)) {
        };
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position, WeiBoComment entity) {
        switch (getItemViewType(position)) {
            case WeiBoComment.TYPE_PARENT:
                ((ParentComment) holder).setData(entity);
                break;
            case WeiBoComment.TYPE_CHILD:
                ((ChildComment) holder).setData(entity);
                break;
            case WeiBoComment.TYPE_MORE:
                ((MoreHolder) holder).setData(entity, position);
                break;
        }
    }

    class ParentComment extends RecyclerView.ViewHolder {
        SimpleDraweeView mHead;
        TextView mComment;
        TextView mDate;

        public ParentComment(View itemView) {
            super(itemView);
            mHead = (SimpleDraweeView) itemView.findViewById(R.id.comment_head);
            mComment = (TextView) itemView.findViewById(R.id.comment_content);
            mDate = (TextView) itemView.findViewById(R.id.comment_date);
        }

        public void setData(final WeiBoComment weiBoComment) {
            if (weiBoComment.getHeadPic() != null) {
                mHead.setImageURI(Uri.parse(weiBoComment.getHeadPic()));
            }
            mHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!"AREA_USR".equals(weiBoComment.getUserType()) && !"SCHOOL_USR".equals(weiBoComment.getUserType())) {
                        if (UserInfoKeeper.obtainUserInfo().getBaseUserId().equals(weiBoComment.getBaseUserId())) {
                            MainActivity.start((Activity) mContext, UserInfoKeeper.obtainUserInfo(), 2);
                        } else {//2.访客
                            PublicUserActivity.start((Activity) mContext, weiBoComment.getBaseUserId());
                        }
                    }
                }
            });
            String name = weiBoComment.getRealName() + "：";
            SpannableStringBuilder spannableString = new SpannableStringBuilder(name + weiBoComment.getCommentContent());
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            InputUtils.setEmojiSpan(mContext, spannableString, (int) mComment.getTextSize() + 5);
            mComment.setText(spannableString);
            mDate.setText(DateUtil.formatTime(weiBoComment.getCreateTime()));
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGetMoreComment.onItemClick(weiBoComment);
                }
            });
        }
    }

    class ChildComment extends RecyclerView.ViewHolder {
        TextView mComment;
        TextView mDate;

        public ChildComment(View itemView) {
            super(itemView);
            mComment = (TextView) itemView.findViewById(R.id.comment_content);
            mDate = (TextView) itemView.findViewById(R.id.comment_date);
        }

        public void setData(final WeiBoComment weiBoComment) {
            String comment = weiBoComment.getCommentContent();
            SpannableStringBuilder spannableString = new SpannableStringBuilder(weiBoComment.getRealName() + " " + comment);
            int index = comment.indexOf("：");
            if (index > 0) {
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, index, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            InputUtils.setEmojiSpan(mContext, spannableString, (int) mComment.getTextSize() + 5);
            mComment.setText(spannableString);
            mDate.setText(DateUtil.formatTime(weiBoComment.getCreateTime()));
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGetMoreComment.onItemClick(weiBoComment);
                }
            });
        }
    }

    class MoreHolder extends RecyclerView.ViewHolder {
        View itemView;

        public MoreHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void setData(final WeiBoComment weiBoComment, final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGetMoreComment != null) {
                        weiBoComment.setPosition(position);
                        mGetMoreComment.getMoreComment(weiBoComment, position);
                    }
                }
            });
        }
    }

    public interface OnGetMoreComment {
        void getMoreComment(WeiBoComment weiBoComment, int position);

        void onItemClick(WeiBoComment weiBoComment);
    }
}
