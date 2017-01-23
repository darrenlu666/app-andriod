package com.codyy.erpsportal.rethink.controllers.adapters;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.rethink.models.entities.CommentListHeader;
import com.codyy.erpsportal.commons.models.entities.MoreComments;
import com.codyy.erpsportal.commons.models.entities.MoreRelies;
import com.codyy.erpsportal.rethink.models.entities.RethinkComment;
import com.codyy.erpsportal.rethink.models.entities.RethinkReply;
import com.codyy.erpsportal.rethink.controllers.activities.RethinkDetailsActivity.HeaderUpdater;
import com.codyy.erpsportal.rethink.controllers.viewholders.CommentViewHolder;
import com.codyy.erpsportal.rethink.controllers.viewholders.HeadViewHolder;
import com.codyy.erpsportal.rethink.controllers.viewholders.MoreCommentsViewHolder;
import com.codyy.erpsportal.rethink.controllers.viewholders.MoreReliesViewHolder;
import com.codyy.erpsportal.rethink.controllers.viewholders.ReplyViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 教学反思评论数据适配器
 */
public class RethinkCommentsAdapter extends Adapter<RecyclerViewHolder> {

    private final static String TAG = "RethinkCommentsAdapter";

    /**
     * 头类型
     */
    private final static int TYPE_HEAD = 0;


    /**
     * 评论类型
     */
    private final static int TYPE_COMMENT = 1;

    /**
     * 评论回复类型
     */
    private final static int TYPE_REPLY = 3;

    /**
     * 更多回复类型
     */
    private final static int TYPE_MORE_REPLIES = 4;

    /**
     * 更多评论类型
     */
    private final static int TYPE_MORE_COMMENTS = 5;

    private List<RethinkComment> mRethinkComments = new ArrayList<>();

    private List<Object> mCommentBaseList = new ArrayList<>();

    private CommentListHeader mCommentListHeader;

    private MoreComments mMoreComments;

    private boolean mHasMoreComments;

    public RethinkCommentsAdapter() {
        mCommentListHeader = new CommentListHeader();
        mMoreComments = new MoreComments();
        mCommentBaseList.add( mCommentListHeader);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new HeadViewHolder(inflateView(parent, R.layout.header_comment_list));
        } else if (viewType == TYPE_COMMENT) {
            return new CommentViewHolder(inflateView(parent, R.layout.item_rethink_comment));
        } else if (viewType == TYPE_REPLY) {
            return new ReplyViewHolder(inflateView(parent, R.layout.item_rethink_rely));
        } else if (viewType == TYPE_MORE_REPLIES) {
            return new MoreReliesViewHolder(inflateView(parent, R.layout.item_more_replies));
        } else {
            return new MoreCommentsViewHolder(inflateView(parent, R.layout.item_more_comments));
        }
    }

    private View inflateView(ViewGroup parent, @LayoutRes int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.setDataToView( mCommentBaseList, position);
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mCommentBaseList.get(position);
        if (item instanceof RethinkComment) {
            return TYPE_COMMENT;
        } else if (item instanceof RethinkReply) {
            return TYPE_REPLY;
        } else if (item instanceof MoreRelies) {
            return TYPE_MORE_REPLIES;
        } else if (item instanceof MoreComments) {
            return TYPE_MORE_COMMENTS;
        } else {
            return TYPE_HEAD;
        }
    }

    /**
     * 添加评论
     * @param comment
     */
    public void addComment(RethinkComment comment) {
        mRethinkComments.add(comment);

        if (mHasMoreComments) {
            int positionStart = mCommentBaseList.size() - 1;
            mCommentBaseList.add(mCommentBaseList.size() - 1, comment);
            if (comment.hasRelies()) {
                mCommentBaseList.addAll(mCommentBaseList.size() - 1, comment.getReplies());
                mCommentBaseList.add(mCommentBaseList.size() - 1, new MoreRelies(comment));
            }
//                notifyItemRangeInserted(positionStart, comment.itemCount());
        } else {//如果最后没有更多，直接加在列表上面
            int positionStart = mCommentBaseList.size();
            mCommentBaseList.add(comment);
            if (comment.hasRelies()) {
                mCommentBaseList.addAll(comment.getReplies());
                mCommentBaseList.add(new MoreRelies(comment));
            }
//                notifyItemRangeInserted(positionStart, comment.itemCount());
        }
    }

    @Override
    public int getItemCount() {
        return mCommentBaseList.size();
    }

    /**
     * 更新头
     * @param updater
     */
    public void updateHeader(HeaderUpdater updater) {
        updater.update( mCommentListHeader);
        notifyItemChanged(0);
    }

    /**
     * 是否有更多回复
     * @param hasMore
     */
    public void setHasMore(boolean hasMore) {
        if (mHasMoreComments != hasMore) {
            mHasMoreComments = hasMore;
            if (hasMore) {
                mCommentBaseList.add(mMoreComments);
//                    notifyItemInserted(mCommentBaseList.size()-1);
            } else {
                mCommentBaseList.remove(mMoreComments);
//                    notifyItemRemoved(mCommentBaseList.size());
            }
        }
    }

    public int getCommentCount() {
        return mRethinkComments == null?0: mRethinkComments.size();
    }

    public void clear() {
        mRethinkComments.clear();
        int size = mCommentBaseList.size();
        if (size > 1) {
            mCommentBaseList.clear();
            mCommentBaseList.add(mCommentListHeader);
//                notifyItemRangeRemoved(0, size - 1);
        }
        mHasMoreComments = false;
    }

    /**
     * 添加回复
     * @param rethinkComment
     * @param newReplies
     */
    public void addReplies(RethinkComment rethinkComment, List<RethinkReply> newReplies) {
        int index = mCommentBaseList.indexOf(rethinkComment);

        int originalCount = rethinkComment.getCurrentCount();
        rethinkComment.addReplies(newReplies);
//            notifyItemChanged(index+originalCount);
        int start = index + originalCount + 1;
        Cog.d(TAG, "addReplies index=", index, "originalCount", originalCount);
        mCommentBaseList.addAll( start, newReplies);
        notifyItemRangeInserted( start, newReplies.size());
        notifyItemChanged(index + rethinkComment.getCurrentCount() + 1);
    }

    public Object getItem(int position) {
        return mCommentBaseList.get(position);
    }

    public void remove(int position) {
        Object item = getItem(position);
        if (item instanceof RethinkComment) {
            RethinkComment comment = (RethinkComment) item;
            mRethinkComments.remove(comment);
            int i = comment.itemCount(), deletingItemCount = comment.itemCount();
            while(i > 0) {
                mCommentBaseList.remove(position);
                i--;
            }
            notifyItemRangeRemoved(position, deletingItemCount);
        } else {
            RethinkReply reply = (RethinkReply) item;
            RethinkComment parent = reply.getComment();
            mCommentBaseList.remove(position);
            if (parent.getCurrentCount() == 1) {
                mCommentBaseList.remove(position);
                notifyItemRangeRemoved(position, 2);
            } else {
                notifyItemRemoved(position);
            }
            parent.remove(reply);
            notifyItemChanged(position - 1);
        }
        notifyDataSetChanged();
    }
}
