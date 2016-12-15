package com.codyy.erpsportal.commons.widgets.infinitepager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.LinePageIndicator;

/**
 * 幻灯片组件
 */
public class SlideView extends FrameLayout {

    private final static String TAG = "SlideView";

    private LinePageIndicator mIndicator;

    private InfiniteViewPager mInfiniteViewPager;

    private OnPageClickListener mOnPageClickListener;

    private float mAspectRatio;

    public SlideView(Context context) {
        super(context);
        init(null, 0);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        LayoutInflater.from(getContext()).inflate(R.layout.slide_view, this, true);
        mInfiniteViewPager = (InfiniteViewPager) findViewById(R.id.view_pager);
        mIndicator = (LinePageIndicator) findViewById(R.id.page_indicator);
        mAspectRatio = 0.5625f;

        Cog.d(TAG, "mIndicator=" + mIndicator);

        mInfiniteViewPager.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SlidePagerAdapter slidePagerAdapter = (SlidePagerAdapter) mInfiniteViewPager.getAdapter();
                int currPos = mInfiniteViewPager.getCurrentItem()
                        % slidePagerAdapter.getItemCount();
                Cog.d(TAG, "onItemClick position=" + currPos);
                if (mOnPageClickListener != null) {
                    mOnPageClickListener.onPageClick( currPos);
                }
            }
        });
    }

    public void setAdapter(SlidePagerAdapter slidePagerAdapter){
        mInfiniteViewPager.setAdapter(slidePagerAdapter);
        mInfiniteViewPager.setIndicator(mIndicator);
        mIndicator.setViewPager(mInfiniteViewPager, slidePagerAdapter.getItemCount() * 200000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newWidth;
        int newHeight;
        newWidth = getMeasuredWidth();
        newHeight = (int) (newWidth * mAspectRatio);
        setMeasuredDimension(newWidth, newHeight);
    }

    public void startToScroll() {
        mInfiniteViewPager.startToScroll();
    }

    public void stopScrolling() {
        mInfiniteViewPager.stopScrolling();
    }

    public void setOnPageClickListener(OnPageClickListener onPageClickListener) {
        mOnPageClickListener = onPageClickListener;
    }

    public interface OnPageClickListener {
        void onPageClick(int position);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

}
