package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.PreparationEntity;
import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yangxinwu on 2015/8/3.
 */
public class PrePareLessonsViewHolder extends BaseRecyclerViewHolder<PreparationEntity> {
    @Bind(R.id.iv_state) ImageView headerImage;
    @Bind(R.id.tv_title) TextView title;
    @Bind(R.id.tv_name_text) TextView teachNameDesc;
    @Bind(R.id.tv_name) TextView teachName;
    @Bind(R.id.tv_date) TextView date;
    @Bind(R.id.tv_subject) TextView subject;
    @Bind(R.id.tv_date_text) TextView tvTimeText;
    @Bind(R.id.rb_star) RatingBar ratingBar;
    @Bind(R.id.rl_date) RelativeLayout rlDate;
    @Bind(R.id.rl_star) ViewGroup rlScore;

    public PrePareLessonsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_collective_prepare;
    }

    @Override
    public void setData(int position, PreparationEntity data) throws Throwable {
        if(Constants.TYPE_PREPARE_LESSON.equals(data.getFromType())){
            teachNameDesc.setText("主备教师");
        }else{
            teachNameDesc.setText(Titles.sMasterTeacher);//"主讲教师");
        }
        if ("INIT".equals(data.getStatus())) {
            headerImage.setBackgroundResource(R.drawable.unstart);
            rlDate.setVisibility(View.VISIBLE);
            rlScore.setVisibility(View.GONE);
            tvTimeText.setText("预约开始时间");
        } else if ("PROGRESS".equals(data.getStatus())) {
            headerImage.setBackgroundResource(R.drawable.xxhdpi_assessmenting_);
            rlDate.setVisibility(View.VISIBLE);
            rlScore.setVisibility(View.GONE);
            tvTimeText.setText("开始时间");
        } else {
            headerImage.setBackgroundResource(R.drawable.xxhdpi_end);
            rlDate.setVisibility(View.GONE);
            rlScore.setVisibility(View.VISIBLE);
        }
        title.setText(data.getTitle());
        teachName.setText(data.getTeacherName());
        date.setText(data.getStartDate());
        subject.setText(data.getSubjectName());
        ratingBar.setProgress(data.getAverageScore());
    }
}
