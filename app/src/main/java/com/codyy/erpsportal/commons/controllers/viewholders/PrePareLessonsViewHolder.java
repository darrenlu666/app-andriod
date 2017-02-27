package com.codyy.erpsportal.commons.controllers.viewholders;

import android.content.Context;
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


/**
 * Created by yangxinwu on 2015/8/3.
 */
public class PrePareLessonsViewHolder extends AbsViewHolder<PreparationEntity> {
    ImageView headerImage;
    TextView title;
    TextView teachNameDesc;
    TextView teachName;
    TextView date;
    TextView subject;
    TextView tvTimeText;
    RatingBar ratingBar;
    RelativeLayout rlDate;
    ViewGroup rlScore;
//    TextView tvScore;

    @Override
    public int obtainLayoutId() {
        return R.layout.item_collective_prepare;
    }

    @Override
    public void mapFromView(View view) {
        headerImage =(ImageView)view.findViewById(R.id.iv_state);
        title =(TextView)view.findViewById(R.id.tv_title);
        teachNameDesc = (TextView) view.findViewById(R.id.tv_name_text);
        teachName =(TextView)view.findViewById(R.id.tv_name);
        date =(TextView)view.findViewById(R.id.tv_date);
        subject=(TextView)view.findViewById(R.id.tv_subject);
        tvTimeText=(TextView)view.findViewById(R.id.tv_date_text);
        ratingBar=(RatingBar)view.findViewById(R.id.rb_star);
        rlDate=(RelativeLayout)view.findViewById(R.id.rl_date);
        rlScore=(ViewGroup)view.findViewById(R.id.rl_star);
//        tvScore = (TextView) view.findViewById(R.id.tv_score);
    }

    @Override
    public void setDataToView(PreparationEntity data, Context context) {
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
//        tvScore.setText(context.getString(R.string.d_score, data.getAverageScore()));
    }
}
