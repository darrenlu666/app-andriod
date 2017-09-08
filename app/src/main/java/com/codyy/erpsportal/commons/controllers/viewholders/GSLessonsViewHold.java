package com.codyy.erpsportal.commons.controllers.viewholders;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsShortEntity;
import com.codyy.erpsportal.commons.models.entities.TeachingResearchBase;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import org.joda.time.format.DateTimeFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页－网络授课－集体备课／互动听课／评课议课
 * Created by poe on 3/6/17.
 */
public class GSLessonsViewHold extends BaseRecyclerViewHolder<PrepareLessonsShortEntity> {
    @Bind(R.id.img_lesson_item) SimpleDraweeView headerImage;
    @Bind(R.id.tv_lesson_title) TextView title;
    @Bind(R.id.tv_teacher) TextView teachName;
    @Bind(R.id.tv_date) TextView date;
    @Bind(R.id.tv_count) TextView clickCount;
    @Bind(R.id.rb_star)  RatingBar ratingBar;
    @Bind(R.id.tv_teacher_view) TextView teacherTitle;
    @Bind(R.id.tv_star) TextView scoreTv;
    @Bind(R.id.tv_rate) TextView rateTv;

    public GSLessonsViewHold(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }


    @Override
    public int obtainLayoutId() {
        return R.layout.item_group_school_prepare_lessons;
    }

    @Override
    public void setData(int position, PrepareLessonsShortEntity data) throws Throwable {
        ImageFetcher.getInstance(rateTv.getContext()).fetchSmall(headerImage, data.getSubjectPic());

        String rate = rateTv.getContext().getString(R.string.f_score, data.getAverageScore());
        SpannableStringBuilder ssb = new SpannableStringBuilder(rate);
        ssb.setSpan(new ForegroundColorSpan(UiMainUtils.getColor(R.color.main_color)), 0, String.valueOf(data.getAverageScore()).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        rateTv.setText(ssb);
        title.setText(data.getTitle());
        teachName.setText(data.getMainTeacher());
        date.setText(DateTimeFormat.forPattern("yyyy-MM-dd").print(data.getStartTime()));
        clickCount.setText(String.valueOf(data.getViewCount()));
        ratingBar.setProgress((int)data.getAverageScore());
    }
}