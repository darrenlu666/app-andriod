package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.EvaluationAllActivity;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;
import com.codyy.erpsportal.commons.models.entities.Comment;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评课议课评论
 * Created by kmdai on 17-3-13.
 */

public class EvaluationCommentFragment extends BaseRefreshFragment<Comment> {
    public final static int GET_COMMENT = 0x001;
    private AssessmentDetails mAssessmentDetails;
    private UserInfo mUserInfo;
    private int mStart;
    /**
     * 每次加载的数量
     */
    private final int mCount = 9;
    /**
     * 结束的位置
     */
    private int mEnd = mStart + mCount;

    public static EvaluationCommentFragment newInstance(AssessmentDetails assessmentDetails) {

        Bundle args = new Bundle();
        args.putParcelable("data", assessmentDetails);
        EvaluationCommentFragment fragment = new EvaluationCommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setURL(URLConfig.GET_COMMENT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mAssessmentDetails = getArguments().getParcelable("data");
    }

    @Override
    public void loadData() {
        if (mDatas == null || mDatas.size() <= 0) {
            mRefreshRecycleView.setRefreshing(true);
            httpConnect(getURL(), getParam(STATE_ON_DOWN_REFRESH), STATE_ON_DOWN_REFRESH);
        }
    }

    @NonNull
    @Override
    public RefreshBaseAdapter<Comment> getAdapter(List<Comment> data) {
        return new CommentAdapter(getContext(), data);
    }

    @NonNull
    @Override
    public Map<String, String> getParam(int state) {
        Map<String, String> data = new HashMap<>();
        switch (state) {
            case STATE_ON_UP_REFRESH:
                mStart = mDatas.size();
                mEnd = mDatas.size() + mCount;
                break;
            case STATE_ON_DOWN_REFRESH:
                mStart = 0;
                mEnd = mStart + mCount;
                break;
        }
        data.put("uuid", mUserInfo.getUuid());
        data.put("evaluationId", mAssessmentDetails.getEvaluationId());
        data.put("start", String.valueOf(mStart));
        data.put("end", String.valueOf(mEnd));
        return data;
    }

    @Override
    protected boolean onRequestSuccess(JSONObject object, int msg) {
        switch (msg) {
            case GET_COMMENT:
                return true;
        }
        return super.onRequestSuccess(object, msg);
    }

    @Override
    protected boolean hasData() {
        if (mDatas.size() < mEnd) {
            return false;
        }
        return true;
    }

    @NonNull
    @Override
    public List<Comment> getDataOnJSON(JSONObject object) {
        List<Comment> comments = new ArrayList<>();
        Comment.getComment(object, comments);
        return comments;
    }

    class CommentAdapter extends RefreshBaseAdapter<Comment> {


        public CommentAdapter(Context mContext, List<Comment> mDatas) {
            super(mContext, mDatas);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType) {
            switch (viewType) {
                case Comment.REFRESH_TYPE_TITLE_VIEW:
                    break;
                case Comment.REFRESH_TYPE_ITEM_VIEW:
                    return new ViewHolder(inflater.inflate(R.layout.evaluation_list_item, parent, false));
            }
            return new ViewHolder(inflater.inflate(R.layout.evaluation_list_item, parent, false));
        }

        @Override
        public void onBindView(RecyclerView.ViewHolder holder, int position, Comment entity) {
            switch (getItemViewType(position)) {
                case Comment.REFRESH_TYPE_ITEM_VIEW:
                    ((ViewHolder) holder).setData(entity);
                    break;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mHeardImage;
        TextView mNameTV;
        TextView mTimeTV;
        RatingBar mRatingBar;
        TextView mScoreTV;
        TextView mContentTV;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EvaluationAllActivity.class);
                    intent.putParcelableArrayListExtra("mComments", (ArrayList<? extends Parcelable>) mDatas);
                    intent.putExtra("assessmentDetails", mAssessmentDetails);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slidemenu_show, R.anim.layout_hide);
                }
            });
            mHeardImage = (SimpleDraweeView) itemView.findViewById(R.id.evaluation_list_item_image);
            mNameTV = (TextView) itemView.findViewById(R.id.evaluation_list_item_text_name);
            mTimeTV = (TextView) itemView.findViewById(R.id.evaluation_list_item_text_time);
            mContentTV = (TextView) itemView.findViewById(R.id.evaluation_list_item_text_cont);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.evaluation_list_item_tatingbar);
            mScoreTV = (TextView) itemView.findViewById(R.id.evaluation_list_item_myscore);
        }

        public void setData(Comment comment) {
            mNameTV.setText(comment.getRealName());
            mTimeTV.setText(comment.getFormattedTime());
            mContentTV.setText(comment.getCommentContent());
            mRatingBar.setNumStars(5);
            mRatingBar.setStepSize(0.5f);
            if (comment.getShowScore() >= 0) {
                if ((mUserInfo.getUuid().equals(comment.getBaseUserId()) || mAssessmentDetails.isScoreVisible()) && !"PROGRESS".equals(mAssessmentDetails.getStatus())) {
                    if (mAssessmentDetails.getScoreType().equals("star")) {
                        mScoreTV.setVisibility(View.GONE);
                        mRatingBar.setVisibility(View.VISIBLE);
                        double a = Math.rint(comment.getShowScore()) / 2;
                        mRatingBar.setRating((float) a);
                    } else {
                        mScoreTV.setVisibility(View.VISIBLE);
                        mRatingBar.setVisibility(View.GONE);
                        mScoreTV.setText(comment.getShowScore() + "分");
                    }
                } else {
                    mScoreTV.setVisibility(View.GONE);
                    mRatingBar.setVisibility(View.GONE);
                }
            } else {
                mScoreTV.setVisibility(View.GONE);
                mRatingBar.setVisibility(View.GONE);
            }
            ImageFetcher.getInstance(getContext()).fetchSmall(mHeardImage, comment.getHeadPic());
        }
    }
}
