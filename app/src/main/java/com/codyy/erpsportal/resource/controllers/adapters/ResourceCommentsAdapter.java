package com.codyy.erpsportal.resource.controllers.adapters;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.resource.controllers.viewholders.CommentViewHolder;
import com.codyy.erpsportal.resource.controllers.viewholders.MoreCommentsViewHolder;
import com.codyy.erpsportal.resource.controllers.viewholders.MoreReliesViewHolder;
import com.codyy.erpsportal.resource.controllers.viewholders.ReplyViewHolder;
import com.codyy.erpsportal.resource.models.entities.Comment;
import com.codyy.erpsportal.resource.models.entities.MoreComments;
import com.codyy.erpsportal.resource.models.entities.MoreRelies;
import com.codyy.erpsportal.resource.models.entities.Reply;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源评论数据适配器
 */
public class ResourceCommentsAdapter extends Adapter<ViewHolder> {

    private final static String TAG = "ResourceCommentsAdapter";

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

    /**
     * 一级评论数量
     */
    private List<Comment> mCommentList = new ArrayList<>();

    private List<Object> mCommentBaseList = new ArrayList<>();

    private SparseArray<ViewHolderCreator<?>> mViewHolderCreators;

    private MoreComments mMoreComments;

    private boolean mHasMoreComments;

    public ResourceCommentsAdapter() {
        mMoreComments = new MoreComments();

        mViewHolderCreators = new SparseArray<>(3);
        mViewHolderCreators.put(TYPE_COMMENT, new EasyVhrCreator<>(CommentViewHolder.class));
        mViewHolderCreators.put(TYPE_REPLY, new EasyVhrCreator<>(ReplyViewHolder.class));
        mViewHolderCreators.put(TYPE_MORE_REPLIES, new EasyVhrCreator<>(MoreReliesViewHolder.class));
        mViewHolderCreators.put(TYPE_MORE_COMMENTS, new EasyVhrCreator<>(MoreCommentsViewHolder.class));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolderCreator<?> creator = mViewHolderCreators.get(viewType);
        return creator.createViewHolder(parent);
    }

    private View inflateView(ViewGroup parent, @LayoutRes int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ((RecyclerViewHolder)holder).setDataToView( mCommentBaseList, position);
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mCommentBaseList.get(position);
        if (item instanceof Comment) {
            return TYPE_COMMENT;
        } else if (item instanceof Reply) {
            return TYPE_REPLY;
        } else if (item instanceof MoreRelies) {
            return TYPE_MORE_REPLIES;
        } else {//if (item instanceof MoreComments)
            return TYPE_MORE_COMMENTS;
        }
    }

    /**
     * 添加评论
     * @param comment
     */
    public void addComment(Comment comment) {
        mCommentList.add(comment);

        if (mHasMoreComments) {
            int positionStart = mCommentBaseList.size() - 1;
            if (positionStart < 0) positionStart = 0;
            mCommentBaseList.add(positionStart, comment);
            if (comment.hasRelies()) {//如果有回复，将回复项们加入列表
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
        return mCommentList == null?0: mCommentList.size();
    }

    public void clear() {
        mCommentList.clear();
        mCommentBaseList.clear();
        mHasMoreComments = false;//最下面的更多项也被清除了，所以此属性得置空
    }

    /**
     * 添加回复
     * @param comment
     * @param newReplies
     */
    public void addReplies(Comment comment, List<Reply> newReplies) {
        int index = mCommentBaseList.indexOf(comment);
        int originalCount = comment.getCurrentCount();
        comment.addReplies(newReplies);
        int start = index + originalCount + 1;
        Cog.d(TAG, "addReplies index=", index, "originalCount", originalCount);
        mCommentBaseList.addAll( start, newReplies);
        notifyItemRangeInserted( start, newReplies.size() + 1);
    }

    public Object getItem(int position) {
        if (mCommentBaseList == null) return null;
        if (position < 0 || position >= mCommentBaseList.size())
            return null;
        return mCommentBaseList.get(position);
    }

    public void remove(int position) {
        Object item = getItem(position);
        if (item instanceof Comment) {
            Comment comment = (Comment) item;
            mCommentList.remove(comment);
            int i = comment.itemCount(),deletingItemCount = comment.itemCount();
            while(i>0) {
                mCommentBaseList.remove(position);
                i--;
            }
            notifyItemRangeRemoved(position, deletingItemCount);
        } else {
            Reply reply = (Reply) item;
            Comment parent = reply.getComment();
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
