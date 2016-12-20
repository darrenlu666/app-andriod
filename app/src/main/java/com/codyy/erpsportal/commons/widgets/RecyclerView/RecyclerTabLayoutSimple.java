/**
 * Modify by poe 2015 /8/7
 * Copyright (C) 2015 nshmura
 * Copyright (C) 2015 The Android Open Source Project
 * <p>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codyy.erpsportal.commons.widgets.RecyclerView;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;

public class RecyclerTabLayoutSimple extends RecyclerView {

    protected static final long DEFAULT_SCROLL_DURATION = 200;
    protected static final float DEFAULT_POSITION_THRESHOLD = 0.6f;
    protected static final float POSITION_THRESHOLD_ALLOWABL = 0.001f;
    public static final int MODE_INDICATOR_BOTTOM = 0;//default: bottom to draw line
    public static final int MODE_INDICATOR_TOP = 1;// draw line on the top of slid tab

    protected Paint mIndicatorPaint;
    protected int mTabBackgroundResId;
    protected int mTabMinWidth;
    protected int mTabMaxWidth;
    protected int mTabTextAppearance;
    protected int mTabSelectedTextColor;
    protected boolean mTabSelectedTextColorSet;
    protected int mTabPaddingStart;
    protected int mTabPaddingTop;
    protected int mTabPaddingEnd;
    protected int mTabPaddingBottom;
    protected int mIndicatorHeight;
    //draw line Model
    protected int mIndicatorModel;

    protected LinearLayoutManager mLinearLayoutManager;
    protected RecyclerOnScrollListener mRecyclerOnScrollListener;

    protected static OnItemClickListener mItemClickListener;
    protected Adapter<?> mAdapter;

    protected int mIndicatorPosition;
    protected int mIndicatorOffset;
    protected int mScrollOffset;
    protected float mOldPositionOffset;
    protected float mPositionThreshold;
    protected boolean mRequestScrollToTab;


    public RecyclerTabLayoutSimple(Context context) {
        this(context, null);
    }

    public RecyclerTabLayoutSimple(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerTabLayoutSimple(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mIndicatorPaint = new Paint();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(mLinearLayoutManager);
        setItemAnimator(null);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.rtl_RecyclerTabLayout,
                defStyle, R.style.rtl_RecyclerTabLayout);
        setIndicatorColor(a.getColor(R.styleable.rtl_RecyclerTabLayout_rtl_tabIndicatorColor, 0));
        setIndicatorHeight(a.getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabIndicatorHeight, 0));
        setIndicatorModel(a.getInt(R.styleable.rtl_RecyclerTabLayout_rtl_tabIndicatorModel, MODE_INDICATOR_BOTTOM));

        mTabTextAppearance = a.getResourceId(R.styleable.rtl_RecyclerTabLayout_rtl_tabTextAppearance, R.style.rtl_RecyclerTabLayout_Tab);

        mTabPaddingStart = mTabPaddingTop = mTabPaddingEnd = mTabPaddingBottom = a.getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabPadding, 0);
        mTabPaddingStart = a.getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingStart, mTabPaddingStart);
        mTabPaddingTop = a.getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingTop, mTabPaddingTop);
        mTabPaddingEnd = a.getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingEnd, mTabPaddingEnd);
        mTabPaddingBottom = a.getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingBottom, mTabPaddingBottom);

        if (a.hasValue(R.styleable.rtl_RecyclerTabLayout_rtl_tabSelectedTextColor)) {
            mTabSelectedTextColor = a.getColor(R.styleable.rtl_RecyclerTabLayout_rtl_tabSelectedTextColor, 0);
            mTabSelectedTextColorSet = true;
        }

        mTabMinWidth = a.getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabMinWidth, 0);
        mTabMaxWidth = a.getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabMaxWidth, 0);
        mTabBackgroundResId = a.getResourceId(R.styleable.rtl_RecyclerTabLayout_rtl_tabBackground, 0);
        a.recycle();

        mPositionThreshold = DEFAULT_POSITION_THRESHOLD;
    }

    /**
     * 选中的位置 .
     *
     * @return
     */
    public int getIndicatorPositoin() {
        return mIndicatorPosition;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        super.onDetachedFromWindow();
    }


    public void setIndicatorColor(int color) {
        mIndicatorPaint.setColor(color);
    }

    public int getIndicatorModel() {
        return mIndicatorModel;
    }

    public void setIndicatorModel(int mIndicatorModel) {
        this.mIndicatorModel = mIndicatorModel;
    }

    public void setIndicatorHeight(int indicatorHeight) {
        mIndicatorHeight = indicatorHeight;
    }

    public void setAutoSelectionMode(boolean autoSelect) {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        if (autoSelect) {
            mRecyclerOnScrollListener = new RecyclerOnScrollListener(this, mLinearLayoutManager);
            addOnScrollListener(mRecyclerOnScrollListener);
        }
    }

    public void setPositionThreshold(float positionThreshold) {
        mPositionThreshold = positionThreshold;
    }

    public void setUpWithViewPager(OnItemClickListener itemClickListener) {
        DefaultAdapter adapter = new DefaultAdapter(itemClickListener);
        adapter.setTabPadding(mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd, mTabPaddingBottom);
        adapter.setTabTextAppearance(mTabTextAppearance);
        adapter.setTabSelectedTextColor(mTabSelectedTextColorSet, mTabSelectedTextColor);
        adapter.setTabMaxWidth(mTabMaxWidth);
        adapter.setTabMinWidth(mTabMinWidth);
        adapter.setTabBackgroundResId(mTabBackgroundResId);
        setUpWithAdapter(adapter);
    }

    public void setUpWithAdapter(Adapter<?> adapter) {
        mAdapter = adapter;
        mItemClickListener = adapter.getOnItemClickListener();
        /*if (mViewPager.getAdapter() == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }
        mViewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener(this));*/
        setAdapter(adapter);
        scrollToTab(0);
    }

    public void setCurrentItem(int position, boolean smoothScroll) {
        if (mItemClickListener != null) {
//            mViewPager.setCurrentItem(position, smoothScroll);
//            mItemClickListener.OnClick(position);
            scrollToTab(position);
            return;
        }

        if (smoothScroll && position != mIndicatorPosition) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                startAnimation(position);
            } else {
                scrollToTab(position); //FIXME add animation
            }

        } else {
            scrollToTab(position);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void startAnimation(final int position) {

        float distance = 1;

        View view = mLinearLayoutManager.findViewByPosition(position);
        if (view != null) {
            float currentX = view.getX() + view.getMeasuredWidth() / 2.f;
            float centerX = getMeasuredWidth() / 2.f;
            distance = Math.abs(centerX - currentX) / view.getMeasuredWidth();
        }

        ValueAnimator animator;
        if (position < mIndicatorPosition) {
            animator = ValueAnimator.ofFloat(distance, 0);
        } else {
            animator = ValueAnimator.ofFloat(-distance, 0);
        }
        animator.setDuration(DEFAULT_SCROLL_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollToTab(position, (float) animation.getAnimatedValue(), true);
            }
        });
        animator.start();
    }

    protected void scrollToTab(int position) {
        scrollToTab(position, 0, false);
        mAdapter.setCurrentIndicatorPosition(position);
        mAdapter.notifyDataSetChanged();
    }

    protected void scrollToTab(int position, float positionOffset, boolean fitIndicator) {
        int scrollOffset = 0;

        View selectedView = mLinearLayoutManager.findViewByPosition(position);
        View nextView = mLinearLayoutManager.findViewByPosition(position + 1);

        if (selectedView != null) {
            int width = getMeasuredWidth();
            float scroll1 = width / 2.f - selectedView.getMeasuredWidth() / 2.f;

            if (nextView != null) {
                float scroll2 = width / 2.f - nextView.getMeasuredWidth() / 2.f;

                float scroll = scroll1 + (selectedView.getMeasuredWidth() - scroll2);
                float dx = scroll * positionOffset;
                scrollOffset = (int) (scroll1 - dx);

                mScrollOffset = (int) dx;
                mIndicatorOffset = (int) ((scroll1 - scroll2) * positionOffset);

            } else {
                scrollOffset = (int) scroll1;
                mScrollOffset = 0;
                mIndicatorOffset = 0;
            }
            if (fitIndicator) {
                mScrollOffset = 0;
                mIndicatorOffset = 0;
            }

            if (mAdapter != null && mIndicatorPosition == position) {
                updateCurrentIndicatorPosition(position, positionOffset - mOldPositionOffset,
                        positionOffset);
            }

            mIndicatorPosition = position;

        } else {
            if (getMeasuredWidth() > 0 && mTabMinWidth == mTabMaxWidth) { //fixed size
                int width = mTabMinWidth;
                int offset = (int) (positionOffset * -width);
                int leftOffset = (int) ((getMeasuredWidth() - width) / 2.f);
                scrollOffset = offset + leftOffset;
            }
            mRequestScrollToTab = true;
        }

        mLinearLayoutManager.scrollToPositionWithOffset(position, scrollOffset);

        if (mIndicatorHeight > 0) {
            invalidate();
        }

        mOldPositionOffset = positionOffset;
    }

    protected void updateCurrentIndicatorPosition(int position, float dx, float positionOffset) {
        int indicatorPosition = -1;
        if (dx > 0 && positionOffset >= mPositionThreshold - POSITION_THRESHOLD_ALLOWABL) {
            indicatorPosition = position + 1;

        } else if (dx < 0 && positionOffset <= 1 - mPositionThreshold + POSITION_THRESHOLD_ALLOWABL) {
            indicatorPosition = position;
        }
        if (indicatorPosition >= 0 && indicatorPosition != mAdapter.getCurrentIndicatorPosition()) {
            mAdapter.setCurrentIndicatorPosition(indicatorPosition);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        View view = mLinearLayoutManager.findViewByPosition(mIndicatorPosition);
        if (view == null) {
            if (mRequestScrollToTab) {
                mRequestScrollToTab = false;
                //默认滚动到0
                scrollToTab(0);
            }
            return;
        }
        mRequestScrollToTab = false;

        int left = view.getLeft() + mScrollOffset - mIndicatorOffset;
        int right = view.getRight() + mScrollOffset + mIndicatorOffset;
        int top = getHeight() - mIndicatorHeight;
        int bottom = getHeight();
        if (mIndicatorModel == MODE_INDICATOR_TOP) {
            top = 0;
            bottom = mIndicatorHeight;
        }
        canvas.drawRect(left, top, right, bottom, mIndicatorPaint);
    }

    protected static class RecyclerOnScrollListener extends OnScrollListener {

        protected RecyclerTabLayoutSimple mRecyclerTabLayout;
        protected LinearLayoutManager mLinearLayoutManager;

        public RecyclerOnScrollListener(RecyclerTabLayoutSimple recyclerTabLayout,
                                        LinearLayoutManager linearLayoutManager) {
            mRecyclerTabLayout = recyclerTabLayout;
            mLinearLayoutManager = linearLayoutManager;
        }

        public int mDx;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mDx += dx;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    if (mDx > 0) {
                        selectCenterTabForRightScroll();
                    } else {
                        selectCenterTabForLeftScroll();
                    }
                    mDx = 0;
                    break;
                case SCROLL_STATE_DRAGGING:
                case SCROLL_STATE_SETTLING:
            }
        }

        protected void selectCenterTabForRightScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = first; position <= last; position++) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() + view.getWidth() >= center) {
                    mRecyclerTabLayout.setCurrentItem(position, false);
                    if (null != mItemClickListener) mItemClickListener.OnClick(position);
                    break;
                }
            }
        }

        protected void selectCenterTabForLeftScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = last; position >= first; position--) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() <= center) {
                    mRecyclerTabLayout.setCurrentItem(position, false);
                    if (null != mItemClickListener) mItemClickListener.OnClick(position);
                    break;
                }
            }
        }
    }

    protected static class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private final RecyclerTabLayoutSimple mRecyclerTabLayout;
        private int mScrollState;

        public ViewPagerOnPageChangeListener(RecyclerTabLayoutSimple recyclerTabLayout) {
            mRecyclerTabLayout = recyclerTabLayout;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mRecyclerTabLayout.scrollToTab(position, positionOffset, false);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                if (mRecyclerTabLayout.mIndicatorPosition != position) {
                    mRecyclerTabLayout.scrollToTab(position);
                }
            }
        }
    }

    public static abstract class Adapter<T extends ViewHolder>
            extends RecyclerView.Adapter<T> {

        protected OnItemClickListener mOnItemClickListener;
        protected int mIndicatorPosition;

        public Adapter(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        public OnItemClickListener getOnItemClickListener() {
            return mOnItemClickListener;
        }
        //        public ViewPager getViewPager() {
//            return mViewPager;
//        }

        public void setCurrentIndicatorPosition(int indicatorPosition) {
            mIndicatorPosition = indicatorPosition;
        }

        public int getCurrentIndicatorPosition() {
            return mIndicatorPosition;
        }

    }

    public static class DefaultAdapter
            extends Adapter<DefaultAdapter.ViewHolder> {

        protected static final int MAX_TAB_TEXT_LINES = 2;

        protected int mTabPaddingStart;
        protected int mTabPaddingTop;
        protected int mTabPaddingEnd;
        protected int mTabPaddingBottom;
        protected int mTabTextAppearance;
        protected boolean mTabSelectedTextColorSet;
        protected int mTabSelectedTextColor;
        private int mTabMaxWidth;
        private int mTabMinWidth;
        private int mTabBackgroundResId;

        //data
//        private

        public DefaultAdapter(OnItemClickListener itemClickListener) {
            super(itemClickListener);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TabTextView tabTextView = new TabTextView(parent.getContext());

            if (mTabSelectedTextColorSet) {
                tabTextView.setTextColor(tabTextView.createColorStateList(
                        tabTextView.getCurrentTextColor(), mTabSelectedTextColor));
            }

            ViewCompat.setPaddingRelative(tabTextView, mTabPaddingStart, mTabPaddingTop,
                    mTabPaddingEnd, mTabPaddingBottom);
            tabTextView.setTextAppearance(parent.getContext(), mTabTextAppearance);
            tabTextView.setGravity(Gravity.CENTER);
            tabTextView.setMaxLines(MAX_TAB_TEXT_LINES);
            tabTextView.setEllipsize(TextUtils.TruncateAt.END);
            tabTextView.setMaxWidth(mTabMaxWidth);
            tabTextView.setMinWidth(mTabMinWidth);
            tabTextView.setTextAppearance(tabTextView.getContext(), mTabTextAppearance);
            if (mTabSelectedTextColorSet) {
                tabTextView.setTextColor(tabTextView.createColorStateList(
                        tabTextView.getCurrentTextColor(), mTabSelectedTextColor));
            }
            if (mTabBackgroundResId != 0) {
                tabTextView.setBackgroundDrawable(
                        AppCompatDrawableManager.get().getDrawable(tabTextView.getContext(), mTabBackgroundResId));
            }
            tabTextView.setLayoutParams(createLayoutParamsForTabs());
            return new ViewHolder(tabTextView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CharSequence title = "simple Text :" + position;
            holder.title.setText(title);
            holder.title.setSelected(getCurrentIndicatorPosition() == position);
        }

        @Override
        public int getItemCount() {
            return mOnItemClickListener.getDataSize();
        }

        public void setTabPadding(int tabPaddingStart, int tabPaddingTop, int tabPaddingEnd,
                                  int tabPaddingBottom) {
            mTabPaddingStart = tabPaddingStart;
            mTabPaddingTop = tabPaddingTop;
            mTabPaddingEnd = tabPaddingEnd;
            mTabPaddingBottom = tabPaddingBottom;
        }

        public void setTabTextAppearance(int tabTextAppearance) {
            mTabTextAppearance = tabTextAppearance;
        }

        public void setTabSelectedTextColor(boolean tabSelectedTextColorSet,
                                            int tabSelectedTextColor) {
            mTabSelectedTextColorSet = tabSelectedTextColorSet;
            mTabSelectedTextColor = tabSelectedTextColor;
        }

        public void setTabMaxWidth(int tabMaxWidth) {
            mTabMaxWidth = tabMaxWidth;
        }

        public void setTabMinWidth(int tabMinWidth) {
            mTabMinWidth = tabMinWidth;
        }

        public void setTabBackgroundResId(int tabBackgroundResId) {
            mTabBackgroundResId = tabBackgroundResId;
        }

        protected LayoutParams createLayoutParamsForTabs() {
            return new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView title;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView;
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        getViewPager().setCurrentItem(getAdapterPosition(), true);
                        mOnItemClickListener.OnClick(getAdapterPosition());
                    }
                });
            }
        }
    }


    /**
     * item点击监听
     */
    public interface OnItemClickListener {

        /**
         * click事件
         *
         * @param position
         */
        void OnClick(int position);

        /**
         * 获取数据count
         *
         * @return
         */
        int getDataSize();
    }

    public static class TabTextView extends TextView {

        public TabTextView(Context context) {
            super(context);
        }

        public ColorStateList createColorStateList(int defaultColor, int selectedColor) {
            final int[][] states = new int[2][];
            final int[] colors = new int[2];
            int i = 0;
            states[i] = SELECTED_STATE_SET;
            colors[i] = selectedColor;
            i++;
            // Default enabled state
            states[i] = EMPTY_STATE_SET;
            colors[i] = defaultColor;
            i++;
            return new ColorStateList(states, colors);
        }
    }
}