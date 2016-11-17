package com.codyy.erpsportal.commons.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ActivityThemeActivity;
import com.codyy.erpsportal.commons.controllers.activities.CollectivePrepareLessonsNewActivity;
import com.codyy.erpsportal.commons.controllers.activities.LoginActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleViewHolder;
import com.codyy.erpsportal.commons.utils.NumberUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TitleItemBar;
import com.codyy.erpsportal.commons.widgets.TitleItemBar.OnMoreClickListener;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.EvaluationScore;
import com.codyy.erpsportal.commons.models.entities.TeachingResearchBase;
import com.codyy.erpsportal.commons.models.entities.TeachingResearchPrepare;
import com.codyy.erpsportal.commons.models.entities.TeachingRethink;
import com.codyy.erpsportal.perlcourseprep.controllers.activities.MoreLessonPlansActivity;
import com.codyy.erpsportal.perlcourseprep.controllers.activities.PersonalLesPrepContentActivity;
import com.codyy.erpsportal.rethink.controllers.activities.MoreRethinkListActivity;
import com.codyy.erpsportal.rethink.controllers.activities.RethinkDetailsActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import org.joda.time.LocalDate;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kmdai on 2015/8/10.
 */
public class TeachingResearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<TeachingResearchBase> teachingResearchBases;
    private String baseAreaId;
    private String schoolId;

    public TeachingResearchAdapter(Context context, List<TeachingResearchBase> teachingResearchBases, String baseAreaId, String schoolId) {
        this.mContext = context;
        this.teachingResearchBases = teachingResearchBases;
        this.baseAreaId = baseAreaId;
        this.schoolId = schoolId;
//        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TeachingResearchBase.TITLE_VIEW:
                return TitleViewHolder.create(parent.getContext());
            case TeachingResearchBase.PREPARE_LESSON:
            case TeachingResearchBase.INTERAC_LESSON:
            case TeachingResearchBase.EVALUATION_LESSON:
            case TeachingResearchBase.PERSON_PREPARE:
                return new ViewItem(LayoutInflater.from(mContext).inflate(R.layout.teaching_research_item, parent, false));
            case TeachingResearchBase.DIVIDE_VIEW:
                View view = new View(mContext);
                return new RecyclerView.ViewHolder(view) {
                };
            case TeachingResearchBase.RETHINK_RETHINK:
                return new RethinkHolder(LayoutInflater.from(mContext).inflate(R.layout.teaching_rethink_item, parent, false));
            case TeachingResearchBase.NO_DATA:
                TextView textView = new TextView(mContext);
                textView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(mContext, 40)));
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.ep_tip_bg));
                textView.setText("暂时没有相关内容");
                return new RecyclerView.ViewHolder(textView) {
                };
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TeachingResearchBase.TITLE_VIEW:
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                TitleItemBar titleItemBar = titleViewHolder.getTitleItemBar();
                titleItemBar.setTitle(teachingResearchBases.get(position).getTitleStr());
                titleItemBar.setOnMoreClickListener(new OnMoreClickListener() {
                    @Override
                    public void onMoreClickListener(View view) {
                        if (UserInfoKeeper.getInstance().getUserInfo() != null) {
                            switch (teachingResearchBases.get(position).getTitleType()) {
                                case TeachingResearchBase.PREPARE_LESSON:
                                    CollectivePrepareLessonsNewActivity.start(mContext, TeachingResearchBase.PREPARE_LESSON, schoolId, baseAreaId);
                                    break;
                                case TeachingResearchBase.INTERAC_LESSON:
                                    CollectivePrepareLessonsNewActivity.start(mContext, TeachingResearchBase.INTERAC_LESSON, schoolId, baseAreaId);
                                    break;
                                case TeachingResearchBase.EVALUATION_LESSON:
                                    CollectivePrepareLessonsNewActivity.start(mContext, TeachingResearchBase.EVALUATION_LESSON, schoolId, baseAreaId);
                                    break;
                                case TeachingResearchBase.PERSON_PREPARE://个人备课
                                    MoreLessonPlansActivity.start((Activity) mContext, baseAreaId, schoolId);
                                    break;
                                case TeachingResearchBase.RETHINK_RETHINK:
                                    MoreRethinkListActivity.start((Activity) mContext, baseAreaId, schoolId);
                                    break;
                            }
                        } else {
                            LoginActivity.start((Activity) mContext);
                        }
                    }
                });
                break;
            case TeachingResearchBase.PREPARE_LESSON:
            case TeachingResearchBase.EVALUATION_LESSON:
            case TeachingResearchBase.INTERAC_LESSON:
            case TeachingResearchBase.PERSON_PREPARE:
                final TeachingResearchPrepare teachingResearchPrepare = (TeachingResearchPrepare) teachingResearchBases.get(position);
                ViewItem viewItem = (ViewItem) holder;
                ImageFetcher.getInstance(EApplication.instance()).fetchImage(
                        viewItem.mSimpleDraweeView,teachingResearchPrepare.getSubjectPic());
                viewItem.mTextViewTitle.setText(teachingResearchPrepare.getTitle());
                viewItem.mScore.setText(teachingResearchPrepare.getAverageScore() + ".0分");
                String teacher = "";
                switch (teachingResearchPrepare.getType()) {
                    case TeachingResearchBase.INTERAC_LESSON:
                        teacher = Titles.sMasterTeacher;//"主讲教师 ";
                        viewItem.mRatingBar.setRating(NumberUtils.floatOf(teachingResearchPrepare.getAverageScore()) / 2);
                        break;
                    case TeachingResearchBase.PREPARE_LESSON:
                        teacher = "主备教师";
                        float rating = NumberUtils.floatOf(teachingResearchPrepare.getAverageScore()) / 2;
                        viewItem.mRatingBar.setRating(rating);
                        break;
                    case TeachingResearchBase.EVALUATION_LESSON:
                        teacher = Titles.sMasterTeacher;//"主讲教师 ";
                        if ("SCORE".equals(teachingResearchPrepare.getScoreType())) {
                            viewItem.mRatingBar.setVisibility(View.GONE);
                            viewItem.mScore.setVisibility(View.GONE);
                            viewItem.mTextViewScore.setText("评分   " + teachingResearchPrepare.getAverageScore() + "/" + teachingResearchPrepare.getTotalScore());
                        } else {
                            viewItem.mScore.setVisibility(View.VISIBLE);
                            viewItem.mRatingBar.setVisibility(View.VISIBLE);
                            viewItem.mTextViewScore.setText("评分");
                            float f = NumberUtils.floatOf(teachingResearchPrepare.getAverageScore()) / 2;
                            viewItem.mRatingBar.setRating(f);
                        }
                        break;
                    case TeachingResearchBase.PERSON_PREPARE:
                        teacher = "教师";
                        viewItem.mScore.setVisibility(View.VISIBLE);
                        viewItem.mRatingBar.setVisibility(View.VISIBLE);
                        double c = Math.ceil(Double.valueOf(teachingResearchPrepare.getAverageScore())) / 2;
                        viewItem.mRatingBar.setRating((float) c);
                        break;
                }
                viewItem.mTextViewTeacher.setText(teacher+" " + teachingResearchPrepare.getMainTeacher());
                long startTime = teachingResearchPrepare.getStartTime();
                LocalDate localDate = new LocalDate(startTime);
                viewItem.mTextViewTime.setText(localDate.toString("YYYY-MM-dd") + "\n" + "点击量  " + teachingResearchPrepare.getViewCount());
                viewItem.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!UserInfoKeeper.getInstance().isEmpty()) {
                            switch (getItemViewType(position)) {
                                case TeachingResearchBase.PREPARE_LESSON:
                                    ActivityThemeActivity.start(mContext, ActivityThemeActivity.PREPARE_LESSON, teachingResearchPrepare.getId(), teachingResearchPrepare.getViewCount());
                                    break;
                                case TeachingResearchBase.EVALUATION_LESSON:
                                    EvaluationScore evaluationScore = new EvaluationScore();
                                    evaluationScore.setTotalScore(NumberUtils.floatOf(teachingResearchPrepare.getTotalScore()));
                                    evaluationScore.setScoreType(teachingResearchPrepare.getScoreType());
                                    evaluationScore.setAvgScore(NumberUtils.floatOf(teachingResearchPrepare.getAverageScore()));
                                    ActivityThemeActivity.start(mContext, ActivityThemeActivity.EVALUATION_LESSON,
                                            teachingResearchPrepare.getId(),
                                            teachingResearchPrepare.getViewCount(),
                                            evaluationScore);
                                    break;
                                case TeachingResearchBase.INTERAC_LESSON:
                                    ActivityThemeActivity.start(mContext, ActivityThemeActivity.INTERACT_LESSON, teachingResearchPrepare.getId(), teachingResearchPrepare.getViewCount());
                                    break;
                                case TeachingResearchBase.PERSON_PREPARE:
                                    PersonalLesPrepContentActivity.start(mContext, teachingResearchPrepare.getId());
                                    break;
                            }
                        } else {
                            LoginActivity.start((Activity) mContext);
                        }
                    }
                });
                break;
            case TeachingResearchBase.RETHINK_RETHINK:
                ((RethinkHolder) holder).setData((TeachingRethink) teachingResearchBases.get(position));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return teachingResearchBases.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return teachingResearchBases.size();
    }

    class ViewItem extends RecyclerView.ViewHolder {
        @Bind(R.id.teaching_research_item_simpleDraweeView)SimpleDraweeView mSimpleDraweeView;
        @Bind(R.id.teaching_research_item_title)TextView mTextViewTitle;
        @Bind(R.id.teaching_research_item_teacher)TextView mTextViewTeacher;
        @Bind(R.id.teaching_research_item_rating)RatingBar mRatingBar;
        @Bind(R.id.teaching_research_item_time)TextView mTextViewTime;
        @Bind(R.id.teaching_research_item_score)TextView mTextViewScore;
        @Bind(R.id.teaching_research_item_score_tv)TextView mScore;
        View itemView;

        public ViewItem(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    class RethinkHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mHeadImage;
        TextView mTitle;
        TextView mCount;

        public RethinkHolder(View itemView) {
            super(itemView);
            mHeadImage = (SimpleDraweeView) itemView.findViewById(R.id.teaching_rething_simple);
            mTitle = (TextView) itemView.findViewById(R.id.teaching_rething_title_text);
            mCount = (TextView) itemView.findViewById(R.id.teaching_rething_count_text);
        }

        public void setData(final TeachingRethink teachingRethink) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RethinkDetailsActivity.start((Activity) mContext, teachingRethink.getRethinkId());
                }
            });
            ImageFetcher.getInstance(mHeadImage).fetchImage( mHeadImage,
                    teachingRethink.getSubjectPic());
            mTitle.setText(teachingRethink.getRethinkTitle());
            mCount.setText(teachingRethink.getRethinkContentText());
        }
    }

}
