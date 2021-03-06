package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.AssessmentClassActivity;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.Assessment;
import com.codyy.erpsportal.commons.models.entities.EmumIndex;
import com.codyy.erpsportal.commons.utils.UIUtils;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by kmdai on 2015/4/15.
 */
public class AssessmentAdapter extends RefreshBaseAdapter<Assessment> {

    private Context context;
    private int type;
    private onItemClick mOnitemClick;

    public AssessmentAdapter(Context context, List<Assessment> assessments, int type) {
        super(context, assessments);
        this.context = context;
        this.type = type;
    }

    public void setOnItemClick(onItemClick onitemClick) {
        mOnitemClick = onitemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new AssessmentViewHolder(inflater.inflate(R.layout.assessment_class_item, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position, Assessment entity) {
        switch (getItemViewType(position)) {
            case Assessment.TYPE_ASSESSMENT:
                try {
                    ((AssessmentViewHolder) holder).setData(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    class AssessmentViewHolder extends RecyclerView.ViewHolder {
        ImageView headerImage;
        TextView titleName;
        TextView teacherNamme;
        TextView date;
        TextView subjectName;
        TextView seqTextView;
        RatingBar ratingBar;

        public AssessmentViewHolder(View convertView) {
            super(convertView);
            headerImage = (ImageView) convertView.findViewById(R.id.assessment_item_image);
            titleName = (TextView) convertView.findViewById(R.id.assessment_item_text_title);
            teacherNamme = (TextView) convertView.findViewById(R.id.assessment_item_text_teacher);
            date = (TextView) convertView.findViewById(R.id.assessment_item_text_date);
            subjectName = (TextView) convertView.findViewById(R.id.assessment_item_text_subject);
            seqTextView = (TextView) convertView.findViewById(R.id.assessment_item_text_seq);
            ratingBar = (RatingBar) convertView.findViewById(R.id.assessment_item_text_rating);
        }

        public void setData(final Assessment assessment) throws Exception{
            if(null == assessment) return;
            if ("INIT".equals(assessment.getStatus())) {
                headerImage.setImageDrawable(context.getResources().getDrawable(R.drawable.unstart));
            } else if ("PROGRESS".equals(assessment.getStatus())) {
                headerImage.setImageDrawable(context.getResources().getDrawable(R.drawable.xxhdpi_assessmenting_));
            } else if ("END".equals(assessment.getStatus())) {
                headerImage.setImageDrawable(context.getResources().getDrawable(R.drawable.xxhdpi_end));

            } else if ("REJECT".equals(assessment.getStatus())) {
                headerImage.setImageDrawable(context.getResources().getDrawable(R.drawable.xxhdpi_reject));
            } else if ("TIMEOUT".equals(assessment.getStatus())) {
                headerImage.setImageDrawable(context.getResources().getDrawable(R.drawable.xxhdpi_pas_date));
            } else if ("WAIT".equals(assessment.getStatus())) {
                headerImage.setImageDrawable(context.getResources().getDrawable(R.drawable.dormant));
            }
            titleName.setText(Html.fromHtml(assessment.getTitle()));
            if (type == AssessmentClassActivity.MASTER) {
                teacherNamme.setText(Titles.sMasterRoom+" " + Html.fromHtml(assessment.getClassroomName()));
            } else {
                teacherNamme.setText(Titles.sMasterTeacher+" " + Html.fromHtml(assessment.getTeacherName()));
            }

            seqTextView.setVisibility(View.GONE);
            if ("END".equals(assessment.getStatus())) {
                if ("star".equals(assessment.getScoreType())) {
                    float a = (float) Math.ceil(assessment.getAverageScore()) / 2;
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(a);
                    date.setVisibility(View.GONE);
                } else {
                    ratingBar.setVisibility(View.GONE);
                    date.setVisibility(View.VISIBLE);
                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    numberFormat.setMaximumFractionDigits(1);
                    date.setText("评分 " + numberFormat.format(assessment.getAverageScore()) + "/" + assessment.getTotalScore());
                }
            } else {
                date.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.GONE);

                //1. 直播课堂：排课日期
                String strDate = "";
                if(!TextUtils.isEmpty(assessment.getScheduleDate()) && UIUtils.isInteger(assessment.getScheduleDate())){
                    if(UIUtils.isInteger(assessment.getScheduleDate())){
                        if(Assessment.TYPE_LIVE.equals(assessment.getEvaType())){//直播 2015-01-01
                            strDate =DateUtil.getDateStr(Long.valueOf(assessment.getScheduleDate()),DateUtil.YEAR_MONTH_DAY);
                        }else{//录播/优课资源:录播课堂：2015-1-1 9:00
                            strDate =DateUtil.getDateStr(Long.valueOf(assessment.getScheduleDate()),DateUtil.DEF_FORMAT);
                        }
                    }
                }else{
                    strDate = assessment.getScheduleDate();
                }

                if(!TextUtils.isEmpty(assessment.getEvaType())){
                    if(Assessment.TYPE_LIVE.equals(assessment.getEvaType())){//直播课堂
                        date.setText("直播课堂 "+strDate);
                        //set the lesson sequence
                        if(!TextUtils.isEmpty(assessment.getClassSeq())){
                            seqTextView.setVisibility(View.VISIBLE);
                            seqTextView.setText("第"+ EmumIndex.getIndex(Integer.valueOf(assessment.getClassSeq()))+"节");
                        }
                    }else if(Assessment.TYPE_VIDEO.equals(assessment.getEvaType())) {//录播课堂
                        date.setText("录播课堂 "+strDate);

                    }else if(Assessment.TYPE_RESOURCE.equals(assessment.getEvaType())){//优客资源
                        date.setText("优课资源 "+strDate);
                    }else{
                        date.setText("直播课堂 "+strDate);
                    }
                }else{
                    date.setText("直播课堂 "+strDate);
                }
            }
            subjectName.setText("学科 "+Html.fromHtml(assessment.getSubjectName()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnitemClick != null) {
                        mOnitemClick.onItemClick(assessment);
                    }
                }
            });
        }
    }

    public interface onItemClick {
        void onItemClick(Assessment assessment);
    }
}
