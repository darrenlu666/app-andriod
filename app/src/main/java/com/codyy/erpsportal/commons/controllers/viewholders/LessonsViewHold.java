package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsShortEntity;
import com.codyy.erpsportal.commons.models.entities.TeachingResearchBase;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import org.joda.time.format.DateTimeFormat;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页－网络授课－集体备课／互动听课／评课议课
 * Created by poe on 3/6/17.
 */
public class LessonsViewHold extends BaseRecyclerViewHolder<PrepareLessonsShortEntity> {
    @Bind(R.id.img_lesson_item) SimpleDraweeView headerImage;
    @Bind(R.id.tv_lesson_title) TextView title;
    @Bind(R.id.tv_teacher) TextView teachName;
    @Bind(R.id.tv_date) TextView date;
    @Bind(R.id.tv_count) TextView clickCount;
    @Bind(R.id.rb_star)  RatingBar ratingBar;
    @Bind(R.id.tv_teacher_view) TextView teacherTitle;
    @Bind(R.id.tv_star) TextView scoreTv;
    @Bind(R.id.tv_rate) TextView rateTv;
    private int mFromType ;

    public LessonsViewHold(View itemView , int fromType) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.mFromType = fromType;
    }


    @Override
    public int obtainLayoutId() {
        return R.layout.item_collective_prepare_lessons;
    }

    @Override
    public void setData(int position, PrepareLessonsShortEntity data) throws Throwable {
        switch (mFromType) {
            case TeachingResearchBase.EVALUATION_LESSON:
                teacherTitle.setText(Titles.sMasterTeacher);//"主讲教师");
                if ("SCORE".equals(data.getScoreType())) {
                    rateTv.setVisibility(View.GONE);
                    ratingBar.setVisibility(View.GONE);
                    scoreTv.setText("评分   " + data.getAverageScore() + "/" + data.getTotalScore());
                } else {
                    rateTv.setVisibility(View.VISIBLE);
                    ratingBar.setVisibility(View.VISIBLE);
                    scoreTv.setText("评分");
                    ratingBar.setRating(data.getAverageScore() / 2f);
                }
                break;
            case TeachingResearchBase.INTERAC_LESSON:
                teacherTitle.setText(Titles.sMasterTeacher);//"主讲教师");
            case TeachingResearchBase.PREPARE_LESSON:

                break;
        }
        rateTv.setText(rateTv.getContext().getString(R.string.f_score, data.getAverageScore()));
        title.setText(data.getTitle());
        teachName.setText(data.getMainTeacher());
        date.setText(DateTimeFormat.forPattern("yyyy-MM-dd").print(data.getStartTime()));
        clickCount.setText(String.valueOf(data.getViewCount()));
        ratingBar.setProgress((int)data.getAverageScore());
        ImageFetcher.getInstance(rateTv.getContext()).fetchSmall(headerImage, data.getSubjectPic());
    }
}