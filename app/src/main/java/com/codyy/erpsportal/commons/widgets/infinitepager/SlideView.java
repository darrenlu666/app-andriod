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

    private int mAspectRatioWidth = 16;

    private int mAspectRatioHeight = 9;

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);
        int calculatedHeight = originalWidth * mAspectRatioHeight / mAspectRatioWidth;
        int finalWidth, finalHeight;
        finalWidth = originalWidth;
        finalHeight = calculatedHeight;
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setAdapter(SlidePagerAdapter slidePagerAdapter){
        mInfiniteViewPager.setAdapter(slidePagerAdapter);
        mInfiniteViewPager.setIndicator(mIndicator);
        mIndicator.setViewPager(mInfiniteViewPager, slidePagerAdapter.getItemCount() * 200000);
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

}
