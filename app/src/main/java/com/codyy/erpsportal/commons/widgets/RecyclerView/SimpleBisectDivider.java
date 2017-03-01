package com.codyy.erpsportal.commons.widgets.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryClassViewHolder;

/**
 * RecyclerView的简单分割线 Divider
 * 实现了以下功能：
 * 1.水平分割线
 * 2.gradeItem的等分space
 * <p>
 * {@care 必须是使用了GridLayoutManager布局的RecyclerView}
 * Created by poe on 15-9-8.
 */
public class SimpleBisectDivider extends RecyclerView.ItemDecoration {
    private static final String TAG = "SimpleBisectDivider";
    private Drawable mDivider;
    private boolean isHeadDividerEnable = false;
    private int mSpace = 20;//多个gridView之间的间距　.
    private int mLastPosition = 0;//第一次出现的地方
    private boolean isBigShow = false;//是否有大图片
    private IGridLayoutViewHolder mIGridLayoutViewHolder;

//    public SimpleBisectDivider(Drawable divider) {
//        this(divider, 20);
//    }

    public SimpleBisectDivider(Drawable divider, int space,IGridLayoutViewHolder iGridLayoutViewHolder) {
        this(divider, false);
        this.mSpace = space;
        this.mIGridLayoutViewHolder = iGridLayoutViewHolder ;
        mLastPosition = 0;
    }

    public SimpleBisectDivider(Drawable divider, boolean isHeadDraw) {
        this.mDivider = divider;
        this.isHeadDividerEnable = isHeadDraw;
    }

    public boolean isHeadDividerEnable() {
        return isHeadDividerEnable;
    }

    public void setHeadDividerEnable(boolean headDividerEnable) {
        isHeadDividerEnable = headDividerEnable;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        if (gridLayoutManager == null ) return;
        //如果下一个item的recyclerType为TitleBar or TitleBarMore 则 返回
        int position = parent.getChildLayoutPosition(view);
        int viewType = parent.getAdapter().getItemViewType(position);

        if (null != mIGridLayoutViewHolder) {
            if (mIGridLayoutViewHolder.obtainSingleBigItemViewHolderType() == viewType) {
                isBigShow = true;
                outRect.top = 0;
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = mSpace;
                return;
            } else if (mIGridLayoutViewHolder.obtainMultiInLineViewHolderType() == viewType) {
                //记录初始位置
                if (mLastPosition == 0) {
                    mLastPosition = position;
                    Log.i(TAG, " last Position : " + mLastPosition);
                }
                int count = gridLayoutManager.getSpanCount();
                setSpace(outRect, position - mLastPosition, count);
                return;
            }
        }

        if (mDivider == null) {
            super.getItemOffsets(outRect, view, parent, state);
            return;
        }

        //如果是第一个item，不需要divider，所以直接return
        if (!isHeadDividerEnable && parent.getChildLayoutPosition(view) < 1) {
            return;
        }

        if (needDivider(viewType)) {
            return;
        }

        //相当于给itemView设置margin，给divider预留空间
        int layoutOrientation = getOrientation(parent);
        if (layoutOrientation == LinearLayoutManager.VERTICAL) {
            outRect.top = mDivider.getIntrinsicHeight();
        } else if (layoutOrientation == LinearLayoutManager.HORIZONTAL) {
            outRect.left = mDivider.getIntrinsicWidth();
        }
    }

    /**
     * 设置grid等分space
     * @param outRect
     * @param position
     * @param count
     */
    private void setSpace(Rect outRect, int position, int count) {
        if (position < count && !isBigShow) {
            outRect.top = mSpace;
        } else {
            outRect.top = 0;
        }
        outRect.bottom = mSpace;
        int pos = position % count;
        Log.i(TAG, " pos : " + position + " real pos : " + pos + " count:" + count);
        //起始位置 .
        if (pos == 0) {
            outRect.left = mSpace;
            outRect.right = mSpace / 2;
        } else if (pos < (count - 1)) {//中间的部分
            outRect.left = mSpace / 2;
            outRect.right = mSpace / 2;
        } else {//最右侧
            outRect.left = mSpace / 2;
            outRect.right = mSpace;
        }
    }

    /**
     * 获取布局管理器的方向
     *
     * @param v
     * @return
     */
    private int getOrientation(RecyclerView v) {
        int result = LinearLayoutManager.HORIZONTAL;
        LinearLayoutManager lm = getLinearLayoutManger(v);
        if (lm != null) {
            result = lm.getOrientation();
        }
        return result;
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mDivider == null) {
            return;
        }

        LinearLayoutManager layoutManager = getLinearLayoutManger(parent);
        if (layoutManager == null) {
            return;
        }

        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int orientation = getOrientation(layoutManager);
        int childCount = parent.getChildCount();
        if (orientation == LinearLayoutManager.VERTICAL) {
            int right = parent.getWidth();
            for (int i = 0; i < childCount; i++) {

                //判断第一个item的下标是不是0，是则return，不需要draw divider
                if (i == 0 && firstVisiblePosition == 0) {
                    continue;
                }
                //如果下一个item的recyclerType为TitleBar or TitleBarMore 则 返回
                int count = parent.getAdapter().getItemCount();
                if ((i + firstVisiblePosition) < count) {
                    int viewType = parent.getAdapter().getItemViewType(i + firstVisiblePosition);
                    if (needDivider(viewType)) {
                        continue;
                    }
                }

                View childView = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
                int left = parent.getPaddingLeft() + childView.getPaddingLeft();
                int bottom = childView.getTop() - params.topMargin;
                int top = bottom - mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            for (int i = 0; i < childCount; i++) {
                if (i == 0 && firstVisiblePosition == 0) {
                    continue;
                }

                View childView = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
                int top = parent.getPaddingTop() + childView.getPaddingTop();
                int bottom = childView.getHeight() + parent.getPaddingTop();
                int right = childView.getLeft() - params.leftMargin;
                int left = right - mDivider.getIntrinsicWidth();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
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
            case HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE:
                result = true;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    private LinearLayoutManager getLinearLayoutManger(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            return (LinearLayoutManager) layoutManager;
        }
        return null;
    }


    private int getOrientation(LinearLayoutManager layoutManager) {
        if (layoutManager != null) {
            return layoutManager.getOrientation();
        }
        return -1;
    }

    public interface IGridLayoutViewHolder{
        /**
         * 单个item填充整行
         * @return
         */
        int obtainSingleBigItemViewHolderType();

        /**
         * 多个item填充一行
         * @return
         */
        int obtainMultiInLineViewHolderType();
    }
}
