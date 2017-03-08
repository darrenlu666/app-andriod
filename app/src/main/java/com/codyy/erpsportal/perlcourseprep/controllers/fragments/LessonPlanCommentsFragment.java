package com.codyy.erpsportal.perlcourseprep.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.perlcourseprep.controllers.fragments.LessonPlanCommentsFragment.CommentViewHolder;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.resource.models.entities.ResourceComment;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.utils.DateUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;

/**
 * 个人备课评论列表
 * Created by gujiajia on 2016/1/21.
 */
public class LessonPlanCommentsFragment extends LoadMoreFragment<ResourceComment, CommentViewHolder> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEager = false;
    }

    @Override
    protected ViewHolderCreator<CommentViewHolder> newViewHolderCreator() {
        return new EasyVhrCreator<>(CommentViewHolder.class);
    }

    @Override
    protected String getUrl() {
        return URLConfig.LESSON_PLAN_COMMENTS;
    }

    @Override
    protected List<ResourceComment> getList(JSONObject response) {
        JsonParser<ResourceComment> commentJsonParser = new JsonParser<ResourceComment>() {
            @Override
            public ResourceComment parse(JSONObject jsonObject) {
                ResourceComment resourceComment = new ResourceComment();
                resourceComment.setContent(optStrOrNull(jsonObject, "content"));
                resourceComment.setTime(jsonObject.optString("createTime"));
                resourceComment.setPhotoUrl(jsonObject.optString("userIcon"));
                resourceComment.setUserName(jsonObject.optString("userRealName"));
                return resourceComment;
            }
        };
        return commentJsonParser.parseArray(response.optJSONArray("list"));
    }

    @LayoutId(R.layout.item_resource_comment)
    public static class CommentViewHolder extends RecyclerViewHolder<ResourceComment> {

        private Context mContext;

        private SimpleDraweeView mUserPhoto;

        private TextView mUserNameTv;

        private TextView mCommentTimeTv;

        private TextView mCommentTv;

        public CommentViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void mapFromView(View view) {
            mContext = view.getContext();
            mUserPhoto = (SimpleDraweeView) view.findViewById(R.id.user_photo);
            mUserNameTv = (TextView) view.findViewById(R.id.user_name);
            mCommentTimeTv = (TextView) view.findViewById(R.id.comment_time);
            mCommentTv = (TextView) view.findViewById(R.id.comment);
        }

        @Override
        public void setDataToView(ResourceComment data) {
            ImageFetcher.getInstance(mContext).fetchImage(mUserPhoto, data.getPhotoUrl());
            mUserNameTv.setText(data.getUserName());
            if (!TextUtils.isEmpty(data.getFormattedTime())) {
                mCommentTimeTv.setText(data.getFormattedTime());
            } else {
                mCommentTimeTv.setText(DateUtils.formatCreateTime(mContext, data.getTime()));
            }
            mCommentTv.setText(data.getContent());
        }
    }
}
