package com.codyy.erpsportal.commons.controllers.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.SchoolNetClassListModel;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ldh on 2015/10/20.
 */
public class NetClassLiveViewHolder extends  AbsViewHolder<SchoolNetClassListModel> {
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
    public void mapFromView(View view) {
        ButterKnife.bind(this,view);
    }

    @Override
    public void setDataToView(SchoolNetClassListModel data, Context context) {
        mSchoolTextView.setText(data.getSchoolName());
        mSubjectTextView.setText(data.getSubjectName());
        mGradeTextView.setText(data.getClasslevelName());
        mTeacherTextView.setText(data.getSpeakerUserName());
        mTimeLabelTextView.setText("/节次");
        mIndexTextView.setText(data.getClassSeq());

        //set image
        String icon = URLConfig.IMAGE_URL +  StringUtils.replaceSmallPic(data.getSubjectPic());
        ImageFetcher.getInstance(context).fetchImage(mImageView,icon);
    }
}
