package com.codyy.erpsportal.homework.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.homework.controllers.activities.WorkItemDetailActivity;

/**
 * 获取总体评价fragment
 * Created by ldh on 2016/1/24.
 */
public class GetOverallCommentFragment extends Fragment {

    private RatingBar mRatingBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_overall_comment_ineditable,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        TextView commentTv = (TextView)view.findViewById(R.id.overall_comment_tv);
        commentTv.setText(getArguments().getString(WorkItemDetailActivity.ARG_STU_ANSWER_COMMENT));

        mRatingBar = (RatingBar) view.findViewById(R.id.rb_rating_total);
        mRatingBar.setRating(getArguments().getInt(WorkItemDetailActivity.ARG_STU_COMMENT_RATING));
    }


}
