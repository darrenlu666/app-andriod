package com.codyy.erpsportal.perlcourseprep.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity.OnFilterObserver;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.perlcourseprep.controllers.activities.PersonalLesPrepContentActivity;
import com.codyy.erpsportal.perlcourseprep.controllers.fragments.LessonPlanListFragment.LessonPlanViewHolder;
import com.codyy.erpsportal.perlcourseprep.models.entities.LessonPlan;
import com.facebook.drawee.view.SimpleDraweeView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

/**
 * 个人备课列表碎片
 * Created by gujiajia on 2016/1/15.
 */
public class LessonPlanListFragment extends LoadMoreFragment<LessonPlan, LessonPlanViewHolder> implements OnFilterObserver {

    private final static String TAG = "LessonPlanListFragment";

    public final static String ARG_USER_INFO = "ARG_USER_INFO";

    private UserInfo mUserInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(ARG_USER_INFO);
        }
    }

    @Override
    protected ViewHolderCreator<LessonPlanViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<LessonPlanViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                if (mUserInfo != null && mUserInfo.isTeacher()) {
                    return R.layout.item_lesson_plan_teacher;
                } else {
                    return R.layout.item_lesson_plan;
                }
            }

            @Override
            protected LessonPlanViewHolder doCreate(View view) {
                return new LessonPlanViewHolder(view, mUserInfo != null && mUserInfo.isTeacher());
            }
        };
    }

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
    }

    @Override
    protected String getUrl() {
        return URLConfig.PERSONAL_LESSON_PLANS;
    }

    @Override
    protected void addParams(Map<String, String> params) {
        if (mUserInfo != null) {
            params.put("uuid", mUserInfo.getUuid());
            if (mUserInfo.isSchool()) {
                params.put("schoolId", mUserInfo.getSchoolId());
            } else if (mUserInfo.isArea()) {
                params.put("baseAreaId", mUserInfo.getBaseAreaId());
            }
        }
    }

    @Override
    protected List<LessonPlan> getList(JSONObject response) {
        return LessonPlan.JSON_PARSER.parseArray(response.optJSONObject("personalPreparation").optJSONArray("list"));
    }

    @Override
    protected boolean checkHasMore(JSONObject response, int itemCount) {
        return response.optJSONObject("personalPreparation").optInt("total") <= itemCount;
    }

    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        Cog.d(TAG, "onFilterConfirmed params=", params);
        if (params != null) {
            updateParamsBaseOnMap(params, "schoolId");
            updateParamsBaseOnMap(params, "baseAreaId");
            updateParamsBaseOnMap(params, "classLevelId", "classlevelId");
            updateParamsBaseOnMap(params, "subjectId");
//            addMapToParam(params);
            loadData(true);
        }
    }

    public static class LessonPlanViewHolder extends RecyclerViewHolder<LessonPlan> {

        private SimpleDraweeView mSubjectDv;
        private TextView mPlpTitleTv;
        private RatingBar mRateRb;
        private TextView mRateTv;
        private TextView mTeacherNameTv;
        private TextView mDateTv;
        private TextView mClickCountTv;
        private TextView mClickCountTitleTv;

        private boolean isTeacher;

        public LessonPlanViewHolder(View itemView, boolean teacher) {
            super(itemView);
            this.isTeacher = teacher;
        }

        @Override
        public void mapFromView(View view) {
            mSubjectDv = (SimpleDraweeView) view.findViewById(R.id.dv_subject);
            mPlpTitleTv = (TextView) view.findViewById(R.id.tv_plp_title);
            mRateRb = (RatingBar) view.findViewById(R.id.rb_rate);
            mRateTv = (TextView) view.findViewById(R.id.tv_rate);
            if (!isTeacher) {
                mTeacherNameTv = (TextView) view.findViewById(R.id.tv_teacher_name);
                mDateTv = (TextView) view.findViewById(R.id.tv_date);
                mClickCountTv = (TextView) view.findViewById(R.id.tv_click_count);
                mClickCountTitleTv = (TextView) view.findViewById(R.id.tv_click_count_title);
            }
        }

        @Override
        public void setDataToView(final LessonPlan data) {
            Context context = itemView.getContext();
            if (!TextUtils.isEmpty(data.getSubjectPic()))
                ImageFetcher.getInstance(mSubjectDv).fetchImage(mSubjectDv, data.getSubjectPic());
//                ImageFetcher.getInstance(mSubjectDv).fetchSmall(mSubjectDv, URLConfig.IMAGE_URL + data.getSubjectPic());
            mPlpTitleTv.setText(data.getTitle());
            if (!isTeacher) {
                mTeacherNameTv.setText(data.getMainTeacher());
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
                mDateTv.setText(fmt.print(data.getTime()));
                mClickCountTv.setText(data.getViewCount());
            }
            mRateRb.setRating(data.getAverageScore() / 2);
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(1);
            numberFormat.setMinimumFractionDigits(0);
            mRateTv.setText(context.getString(R.string.n_score, numberFormat.format(data.getAverageScore())));
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cog.d(TAG, "onLessonPlanClick ");
                    PersonalLesPrepContentActivity.start(itemView.getContext(), data.getId());
                }
            });
        }
    }
}
