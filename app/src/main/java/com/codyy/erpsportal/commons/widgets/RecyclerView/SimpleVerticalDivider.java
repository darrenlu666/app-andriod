package com.codyy.erpsportal.commons.widgets.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolder;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryClassViewHolder;

/**
 * RecyclerView的GridLayoutManager简单分割线 Divider
 * Created by poe on 15-9-8.
 */
public class SimpleVerticalDivider extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private Drawable mHDivider;

    public SimpleVerticalDivider(Drawable divider) {
        this.mDivider = divider;
        mHDivider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mDivider == null) {
            super.getItemOffsets(outRect, view, parent, state);
            return;
        }
    }
    private int getSpanCount(RecyclerView parent)
    {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            drawHorizontal(c, parent,i);
            drawVertical(c, parent,i);
        }
    }

    /**
     * 再次处过滤不希望回执divider的类型 0x100 - 0x200
     *
     * @param viewType
     * @return
     */
    private boolean needDivider(int viewType) {
        boolean result = false;
        switch (viewType) {
            case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE:
            case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA:
            case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
            case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA:
            case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE:
                result = true;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }


    private GridLayoutManager getLayoutManger(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return (GridLayoutManager) layoutManager;
        }
        return null;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent , int i) {
        View childView = parent.getChildAt(i);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
        int left = parent.getPaddingLeft() ;//+ childView.getPaddingLeft();
        int bottom = childView.getBottom() + params.topMargin;
        int top = bottom - mHDivider.getIntrinsicHeight();
        int right = childView.getRight();//-childView.getPaddingRight();
        mHDivider.setBounds(left, top, right, bottom);
        mHDivider.draw(c);
    }


    public void drawVertical(Canvas c, RecyclerView parent ,int i)
    {
//        final int childCount = parent.getChildCount();
//        for (int i = 0; i < childCount; i++)
//        {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
//        }
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount,
                                int childCount)
    {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
            {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else
            {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount)
    {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
            {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

}