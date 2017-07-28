package com.codyy.erpsportal.commons.controllers.viewholders.onlineclass;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.customized.SchoolRank;
import com.codyy.erpsportal.commons.models.entities.onlineclass.SipNetResearch;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 学校排名（专递课堂)(sip)
 * Created by poe on 17-7-24.
 */
public class SchoolRankViewHolder extends BaseRecyclerViewHolder<SchoolRank> {

    @Bind(R.id.tv_number)
    TextView mLineNumberTv;
    @Bind(R.id.tv_school)
    TextView mSchoolTv;
    @Bind(R.id.tv_lesson_count)
    TextView mLessonCountTv;
    @Bind(R.id.tv_teach_count)
    TextView mTeachCountTv;

    public SchoolRankViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_rank_school;
    }


    @Override
    public void setData(int position, SchoolRank data) {
        //do nothing .
        String text = String.valueOf(1+data.getRankPosition());
        Spannable spannable = new SpannableString(text);
        if(data.getRankPosition()<3){
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#ff600")),0,1,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        mLineNumberTv.setText(spannable);
        mSchoolTv.setText(Html.fromHtml(UIUtils.filterCharacter(data.getSchoolName())));
        mLessonCountTv.setText(String.valueOf(data.getScheduleActivityCount()));
        mTeachCountTv.setText(String.valueOf(data.getTeacherActivityCount()));
    }
}
