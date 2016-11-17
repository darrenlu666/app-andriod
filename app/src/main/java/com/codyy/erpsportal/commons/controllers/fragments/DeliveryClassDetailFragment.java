package com.codyy.erpsportal.commons.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.models.entities.DeliveryClassDetail;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kmdai on 2015/9/12.
 */
public class DeliveryClassDetailFragment extends Fragment {
    View mRootView;
    @Bind(R.id.delivery_class_text_area)TextView mTextArea;
    @Bind(R.id.delivery_class_text_master_school)TextView mTextMasterSchool;
    @Bind(R.id.delivery_class_text_receiver_school)TextView mTextReceiverSchool;
    @Bind(R.id.delivery_class_text_class_time)TextView mTextClassTime;
    @Bind(R.id.delivery_class_text_play_times)TextView mTextPlayTimes;
    @Bind(R.id.tv_lesson_duration)TextView mDurationTextView;//时长

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = getActivity().getLayoutInflater().inflate(R.layout.delivery_class_detial, null);
        ButterKnife.bind(this, mRootView);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setDeliveryClassDetail(DeliveryClassDetail mDeliveryClassDetail) {
        mTextArea.setText(mDeliveryClassDetail.getAreaName());
        String levelStr= mDeliveryClassDetail.getClasslevelName()+"/"+mDeliveryClassDetail.getSubjectName()+"/"+mDeliveryClassDetail.getTeacherName();
        mTextMasterSchool.setText(mDeliveryClassDetail.getSchoolName()+"\n"+levelStr);
        for (int i = 0; i < mDeliveryClassDetail.getReceiveSchool().size(); i++) {
            mTextReceiverSchool.setText(mTextReceiverSchool.getText() + mDeliveryClassDetail.getReceiveSchool().get(i));
            if (i >= 1 && i < (mDeliveryClassDetail.getReceiveSchool().size() - 1)) {
                mTextReceiverSchool.setText(mTextReceiverSchool.getText() + "\n");
            }
        }
        mTextClassTime.setText(mDeliveryClassDetail.getRealBeginTime());
        mTextPlayTimes.setText(mDeliveryClassDetail.getWatchCount() + "次");
        mDurationTextView.setText(DateUtil.getDuration(mDeliveryClassDetail.getDuration()));
    }
}
