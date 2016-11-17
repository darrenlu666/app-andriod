package com.codyy.erpsportal.commons.controllers.viewholders;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.models.entities.LiveClassListModel;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by caixingming on 2015/5/25.
 */
public class LiveViewHolder extends AbsViewHolder<LiveClassListModel> {
    @Bind(R.id.imgOfLessonItem)
    SimpleDraweeView mImageView;
    @Bind(R.id.txtTitleOfLessonItem)
    TextView mSchoolTextView;
    @Bind(R.id.txtIndexOfLessonItem)
    TextView mIndexTextView;
    @Bind(R.id.txtSubjectOfLessonItem)
    TextView mSubjectTextView;
    @Bind(R.id.txtGradOfLessonItem)
    TextView mGradeTextView;
    @Bind(R.id.txtTeacherOfLessonItem)
    TextView mTeacherTextView;
    @Bind(R.id.txtDes1OfLessonItem)
    TextView mTimeLabelTextView;

    @Override
    public int obtainLayoutId() {
        return R.layout.item_live_lesson;
    }

    @Override
    public void mapFromView(View convertView) {
       //replaced by butterknife..do nothing if you do it by hand just do it...
        ButterKnife.bind(this,convertView);
    }

    @Override
    public void setDataToView(LiveClassListModel data, Context context) {

        mSchoolTextView.setText(data.getSchool());
        mSubjectTextView.setText(data.getSubject());
        mGradeTextView.setText(data.getGrade());
        mTeacherTextView.setText(data.getTeacher());
        int pos = Integer.parseInt(data.getIndex());
        mTimeLabelTextView.setText("/节次");
        mIndexTextView.setText(data.getIndex());

        //set image
        String icon = URLConfig.IMAGE_URL +  StringUtils.replaceSmallPic(data.getIcon());
        Uri uri = Uri.parse(icon);
        mImageView.setImageURI(uri);
        //ImageFetcher.getInstance(context).fetchSmall(mImageView,data.getIcon());  修复bug3457
    }
}