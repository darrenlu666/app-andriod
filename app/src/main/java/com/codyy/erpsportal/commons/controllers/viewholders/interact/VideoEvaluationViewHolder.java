package com.codyy.erpsportal.commons.controllers.viewholders.interact;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.AssessmentClassActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import java.text.NumberFormat;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 评课议课-详情-评课详情-打分及基本信息【评论之上的一块】
 * Created by poe on 16-6-28.
 */
public class VideoEvaluationViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar>{
    public static final String TAG = "VideoEvaluationViewHolder";
//    @Bind(R.id.lin_period)LinearLayout mPeriodLinearLayout;
    @Bind(R.id.tv_video_count)TextView mVideoCountTv;
    @Bind(R.id.recycler_view)RecyclerView mRecyclerView;
    @Bind(R.id.btn_download)Button mDownloadBtn;
//    @Bind(R.id.rb_my_rating)RatingBar mRatingBar;//打分组件RatingBar .
//    @Bind(R.id.btn_rating)Button mRatingButton;//打分按钮
    @Bind(R.id.rb_rating_total)RatingBar mTotalRatingBar;
//    @Bind(R.id.tv_my_rating)TextView mScoreTv;
//    @Bind(R.id.tv_rating_total)TextView mTotalRatingTv;
    @Bind(R.id.my_rate_layout)RelativeLayout mScoreRlt;//我的评分
    @Bind(R.id.all_rate_layout)RelativeLayout mTotalScoreRlt;//总评分
//    @Bind(R.id.lin_my_rating)LinearLayout mScoreLinearLayout;
    private BaseRecyclerAdapter<AssessmentDetails.VideoId , SequenceVideoViewHolder> mAdapter;
    private UserInfo mUserInfo ;
    private int mType ;//访问类型
    private IEvaluateActionInterface mActionListener;

    /**
     * @param itemView
     * @param userInfo 用户基本信息
     * @param type 评课类型 {@link AssessmentClassActivity#MASTER}
     */
    public VideoEvaluationViewHolder(View itemView , UserInfo userInfo ,int type , IEvaluateActionInterface actionInterface) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        mUserInfo  =    userInfo ;
        mType      =   type ;
        mActionListener =   actionInterface;
        init(itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.evaluation_lsit_heardview;
    }

    @Override
    public void setData(int position, BaseTitleItemBar input) {

        mCurrentPosition = position;
        mData   =   input;
        if(input instanceof AssessmentDetails){
            AssessmentDetails data = (AssessmentDetails) input;
            List<AssessmentDetails.VideoId> videos = data.getVideoIds() ;
            if(videos == null ) return;
            mVideoCountTv.setText("共" + String.valueOf(videos.size()) + "段>");
            if(videos.size() < 1){
//                mPeriodLinearLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mDownloadBtn.setVisibility(View.GONE);
            }else  if(videos.size() == 1){
//                mPeriodLinearLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                mDownloadBtn.setVisibility(View.VISIBLE);
            }else{
//                mPeriodLinearLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mDownloadBtn.setVisibility(View.GONE);
                mAdapter.setData(data.getVideoIds());
            }

            // TODO: 16-6-29 评分条目是否存在.
            /*** test rating score start*****/
//            if (!UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType()) || "N".equals(data.getIsAttend())) {//如果 不是教师 或 未被邀请
            if (!UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType()) ) {//如果 不是教师 或 未被邀请|| "N".equals(data.getIsAttend())
                /*** test rating score end*****/
                mScoreRlt.setVisibility(View.GONE);
                if ("END".equals(data.getStatus())) {//已结束
                    mTotalScoreRlt.setVisibility(View.VISIBLE);
                    if ("star".equals(data.getScoreType())) {//表现形式为星星
                        mTotalRatingBar.setVisibility(View.VISIBLE);
//                        mTotalRatingTv.setVisibility(View.GONE);
                        double a = Math.ceil(data.getAverageScore()) / 2;
                        mTotalRatingBar.setRating((float) a);
                    } else {
                        double a = data.getAverageScore();
                        NumberFormat numberFormat = NumberFormat.getNumberInstance();
                        numberFormat.setMaximumFractionDigits(1);
//                        mTotalRatingTv.setText(numberFormat.format(a < 0 ? 0 : a) + "分");
                        mTotalRatingBar.setVisibility(View.GONE);
//                        mTotalRatingTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    mTotalScoreRlt.setVisibility(View.GONE);
                }
            } else {
//            mCommentBtn.setVisibility(View.VISIBLE);
                mTotalScoreRlt.setVisibility(View.VISIBLE);
                mScoreRlt.setVisibility(View.VISIBLE);
                if ("END".equals(data.getStatus())) {//评课结束
                    if ("star".equals(data.getScoreType())) {//显示星星 .
                        //隐藏数字分数
//                        mScoreLinearLayout.setVisibility(View.GONE);
//                        mRatingBar.setIsIndicator(true);
//                        mTotalRatingTv.setVisibility(View.GONE);
                        double a = Math.ceil(data.getAverageScore()) / 2;
                        mTotalRatingBar.setRating((float) a);
                        double b = Math.ceil(data.getMyScore()) / 2;
//                        mRatingBar.setRating((float) b);
                    } else {
//                        mRatingBar.setVisibility(View.GONE);
                        mTotalRatingBar.setVisibility(View.GONE);
                        int a = (int) data.getMyScore();
//                        mScoreTv.setText((a < 0 ? 0 : a) + "分");
                        double b = data.getAverageScore();
                        NumberFormat numberFormat = NumberFormat.getNumberInstance();
                        numberFormat.setMaximumFractionDigits(1);
//                        mTotalRatingTv.setText(numberFormat.format(b < 0 ? 0 : b) + "分");
//                        mScoreLinearLayout.setVisibility(View.VISIBLE);
//                        mRatingButton.setVisibility(View.GONE);
//                        mScoreTv.setVisibility(View.VISIBLE);///
//                        mTotalRatingTv.setVisibility(View.VISIBLE);
                    }
                } else {//f
                    if ((int) data.getMyScore() > 0) {
//                        mScoreTv.setVisibility(View.VISIBLE);
//                        mScoreTv.setText((int) data.getMyScore() + "分");
                    } else {
//                        mScoreTv.setVisibility(View.GONE);
                    }
                    mTotalScoreRlt.setVisibility(View.GONE);
                    if ("star".equals(data.getScoreType())) {
//                        mScoreLinearLayout.setVisibility(View.GONE);
//                        mRatingBar.setVisibility(View.VISIBLE);
                    } else {
//                        mRatingButton.setVisibility(View.VISIBLE);
//                        mScoreLinearLayout.setVisibility(View.VISIBLE);
//                        mRatingBar.setVisibility(View.GONE);
                    }
                    if (data.getMyScore() >= 0) {
                        double a = Math.ceil(data.getMyScore()) / 2;
//                        mRatingBar.setRating((float) a);
                    } else {
//                        mRatingBar.setRating(0);
                    }
                }
            }
            if (mType == AssessmentClassActivity.MASTER) {
                mScoreRlt.setVisibility(View.GONE);
            }
        }
    }

    public void setActionListener(IEvaluateActionInterface actionListener) {
        this.mActionListener = mActionListener;
    }

    private void init(View itemView) {
        LinearLayoutManager manager = new LinearLayoutManager(itemView.getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<SequenceVideoViewHolder>() {
            @Override
            public SequenceVideoViewHolder createViewHolder(ViewGroup parent, int viewType) {
                SequenceVideoViewHolder viewHolder =  new SequenceVideoViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_list_item_video_view,null));
                return viewHolder ;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        //打分
        /*mRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mActionListener) mActionListener.writeScore();
            }
        });*/
        //下载
        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssessmentDetails data = (AssessmentDetails) mData;
                if(data.getVideoIds() != null && data.getVideoIds().size() == 1){
                    if(null != mActionListener) mActionListener.onDownload(data.getVideoIds().get(0));
                }
            }
        });
        //星星打分
       /* mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(null != mActionListener) mActionListener.onRatingClick(ratingBar , rating ,fromUser);
            }
        });*/
    }

    /**
     * 一些监听反馈事件 .
     */
    public interface  IEvaluateActionInterface{
        /**
         * 点击了某段视频
         * @param position 点击位置
         * @param video
         */
        void onVideoClicked(int position ,AssessmentDetails.VideoId video);

        /**
         * 去打分
         */
        void writeScore();

        /***
         * 打分中
         * @param ratingBar 控件RatingBar
         * @param rating
         * @param fromUser
         */
        void onRatingClick(RatingBar ratingBar, float rating, boolean fromUser);

        /**
         * 下载单一的视频
         * @param data
         */
        void onDownload(AssessmentDetails.VideoId data);
    }
}
