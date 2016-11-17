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
                ((AssessmentViewHolder) holder).setData(entity);
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

        public void setData(final Assessment assessment) {
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
                date.setText("排课日期 " + DateUtil.getDateStr(Long.valueOf(assessment.getScheduleDate()),DateUtil.YEAR_MONTH_DAY));
            }
            subjectName.setText("学科 "+Html.fromHtml(assessment.getSubjectName()));
            //set the lesson sequence
            if(!TextUtils.isEmpty(assessment.getClassSeq())){
                seqTextView.setText("第"+ EmumIndex.getIndex(Integer.valueOf(assessment.getClassSeq()))+"节课");
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnitemClick != null) {
                        mOnitemClick.onItemClick(assessment);
                    }
                }
            });
        }

        private String getClassNB(int a) {
            switch (a) {
                case 1:
                    return "第一节课";
                case 2:
                    return "第二节课";
                case 3:
                    return "第三节课";
                case 4:
                    return "第四节课";
                case 5:
                    return "第五节课";
                case 6:
                    return "第六节课";
                case 7:
                    return "第七节课";
                case 8:
                    return "第八节课";
            }
            return null;
        }
    }

    public interface onItemClick {
        void onItemClick(Assessment assessment);
    }
}
