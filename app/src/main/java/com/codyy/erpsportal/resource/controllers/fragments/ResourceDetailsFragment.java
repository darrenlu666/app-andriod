package com.codyy.erpsportal.resource.controllers.fragments;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.NumberUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;
import com.codyy.erpsportal.statistics.widgets.OrderLayout;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 资源详情碎片
 * Created by gujiajia
 */
public class ResourceDetailsFragment extends Fragment {

    private static final String TAG = "ResourceDetailsFragment";

    public static final String ARG_IS_MEDIA = "isVideo";

    /**
     * 是否显示分享者
     */
    public static final String ARG_SHOW_SHARER = "showSharer";

    @Bind(R.id.ol_attributes)
    OrderLayout mAttributesOl;

    @Bind(R.id.tv_lb_rate)
    TextView mRateLbTv;

    @Bind(R.id.rb_rate)
    RatingBar mRateRb;

    @Bind(R.id.tv_rate)
    TextView mRateTv;

    @Bind(R.id.tv_publisher)
    TextView mPublisherTv;

    @Bind(R.id.tv_time)
    TextView mTimeTv;

    @Bind(R.id.tv_sharer)
    TextView mSharerTv;

    @Bind(R.id.tv_shared_times)
    TextView mSharedTimesTv;

    @Bind(R.id.tv_view_count)
    TextView mViewCountTv;

    @Bind(R.id.tv_collect_count)
    TextView mCollectCountTv;

    @Bind(R.id.tv_summary)
    TextView mSummaryTv;

    private int mTagHorizontalPadding;

    private int mTagVerticalPadding;

    private boolean mIsMedia = true;

    private boolean mShowSharer;

    public static ResourceDetailsFragment newInstance(boolean isVideo, boolean showSharer) {
        ResourceDetailsFragment fragment = new ResourceDetailsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_MEDIA, isVideo);
        args.putBoolean(ARG_SHOW_SHARER, showSharer);
        fragment.setArguments(args);
        return fragment;
    }

    public ResourceDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cog.d(TAG, "+onCreate");
        mTagHorizontalPadding = UIUtils.dip2px(getContext(), 12);
        mTagVerticalPadding = UIUtils.dip2px(getContext(), 2);
        if (getArguments() != null) {
            mIsMedia = getArguments().getBoolean(ARG_IS_MEDIA);
            mShowSharer = getArguments().getBoolean(ARG_SHOW_SHARER);
        }
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        Cog.d(TAG, "+onInflate");
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ResourceDetailsFragment);
        mIsMedia = a.getBoolean(R.styleable.ResourceDetailsFragment_isMedia, mIsMedia);
        mShowSharer = a.getBoolean(R.styleable.ResourceDetailsFragment_showSharer, mShowSharer);
        a.recycle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Cog.d(TAG, "+onCreateView");
        View view = inflater.inflate(R.layout.fragment_video_details, container, false);
        ButterKnife.bind(this, view);
        if (mShowSharer) {
            mSharerTv.setVisibility(View.VISIBLE);
            mSharedTimesTv.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public void setMedia(boolean media) {
        mIsMedia = media;
    }

    public void setShowSharer(boolean showSharer) {
        mShowSharer = showSharer;
        if (mShowSharer) {
            mSharerTv.setVisibility(View.VISIBLE);
            mSharedTimesTv.setVisibility(View.VISIBLE);
        } else {
            mSharerTv.setVisibility(View.GONE);
            mSharedTimesTv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置资源详情，更新界面
     *
     * @param resourceDetails
     */
    public void setResourceDetails(ResourceDetails resourceDetails) {
        if (mAttributesOl == null || resourceDetails == null) return;
        mAttributesOl.removeAllViews();
        addAttributeTag(resourceDetails.getSemesterName());
        addAttributeTag(resourceDetails.getClassLevelName());
        addAttributeTag(resourceDetails.getSubjectName());
        addAttributeTag(resourceDetails.getVersionName());
        addAttributeTag(resourceDetails.getVolumeName());
        addAttributeTag(resourceDetails.getChapterName());
        addAttributeTag(resourceDetails.getSectionName());
        String[] knowledgeArr = resourceDetails.getKnowledgeNameArr();
        if (knowledgeArr != null) {
            for (String knowledgeName : knowledgeArr) {
                addAttributeTag(knowledgeName);
            }
        }
        float rating = NumberUtils.floatOf(resourceDetails.getEvaluateAvg());
        mRateRb.setRating( NumberUtils.floatOf(resourceDetails.getEvaluateAvg())/2f);
        mRateTv.setText(getString(R.string.n_score, String.format("%.1f", rating)));
        mPublisherTv.setText(getString(R.string.publisher_who, resourceDetails.getUserName()));
        mTimeTv.setText(getString(R.string.publish_time_when,resourceDetails.getCreateTime().substring(0, 10)));
        if (mIsMedia) {
            mViewCountTv.setText(getString(R.string.play_n, resourceDetails.getViewCount()));
        } else {
            mViewCountTv.setText(getString(R.string.view_n, resourceDetails.getViewCount()));
        }
        if (mShowSharer) {
            mSharerTv.setText(getString(R.string.sharer_who, resourceDetails.getSharer()));
            mSharedTimesTv.setText(getString(R.string.shared_n_times, resourceDetails.getSharedTimes()));
        }
        mCollectCountTv.setText(getString(R.string.collect_n, resourceDetails.getFavoriteCount()));
        mSummaryTv.setText(getString(R.string.summary_what, resourceDetails.getDescription()));
    }

    private void addAttributeTag(String tagStr) {
        if (TextUtils.isEmpty(tagStr)) return;
        TextView textView = new TextView(getContext());
        textView.setText(tagStr);
        textView.setPadding(mTagHorizontalPadding, mTagVerticalPadding, mTagHorizontalPadding, mTagVerticalPadding);
        if (VERSION.SDK_INT == VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setPaddingRelative(mTagHorizontalPadding, mTagVerticalPadding, mTagHorizontalPadding, mTagVerticalPadding);
        }
        textView.setBackgroundResource(R.drawable.bg_tag_resource_attr);
        mAttributesOl.addView(textView);
    }

    private String checkNull(String str) {
        return str == null ? "" : str;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
