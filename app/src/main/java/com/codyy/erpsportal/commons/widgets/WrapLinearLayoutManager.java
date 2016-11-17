package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codyy.erpsportal.BuildConfig;

/**
 *
 * Created by gujiajia on 2016/1/20.
 */
public class WrapLinearLayoutManager extends LinearLayoutManager{
    private static final int CHILD_WIDTH = 0;
    private static final int CHILD_HEIGHT = 1;
    private static final int DEFAULT_CHILD_SIZE = 100;

    private final int[] childDimensions = new int[2];

    private int childSize = DEFAULT_CHILD_SIZE;
    private boolean hasChildSize;

    public WrapLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public static int makeUnspecifiedSpec() {
        return View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);

        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);

        final boolean exactWidth = widthMode == View.MeasureSpec.EXACTLY;
        final boolean exactHeight = heightMode == View.MeasureSpec.EXACTLY;

        final int unspecified = makeUnspecifiedSpec();

        if (exactWidth && exactHeight) {
            // 如果高宽有精确值，调默认的“onMeasure”，我们只处理wrap_content
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            return;
        }

        final boolean vertical = getOrientation() == VERTICAL;

        initChildDimensions(widthSize, heightSize, vertical);

        int width = 0;
        int height = 0;

        //可能得到在Recycler中绑定老的（失效）的实体的废弃的view。因为invalidate在onMeasure之后跑。
        //这里手动清除recycler中的item，（不会影响滑动，滑动时不跑onMeasure）
        recycler.clear();

        final int stateItemCount = state.getItemCount();
        final int adapterItemCount = getItemCount();

        //适配器Adapter中的项肯定是最新item数据，但是RecyclerView.State里可能有老的数据（比如切换动画未做完之前）
        //所以要用适配器Adapter中的数据来测量尺寸。
        for (int i = 0; i < adapterItemCount; i++) {
            if (vertical) {//垂直布局
                if (!hasChildSize) {
                    if (i < stateItemCount) {
                        //不应该超出state中item数量，否则会IndexOutOfBoundsException。
                        // 对于这样的item，我们将使用先前计算的尺寸
                        measureChild(recycler, i, widthSpec, unspecified, childDimensions);
                    } else {
                        logMeasureWarning(i);
                    }
                }
                height += childDimensions[CHILD_HEIGHT];
                if (i == 0) {
                    width = childDimensions[CHILD_WIDTH];
                }
                if (height >= heightSize) {
                    break;
                }
            } else {
                if (!hasChildSize) {
                    if (i < stateItemCount) {
                        //同垂直布局时的逻辑，不应该超出state中item数量，否则会IndexOutOfBoundsException。
                        // 对于这样的item，我们将使用先前计算的尺寸
                        measureChild(recycler, i, unspecified, heightSpec, childDimensions);
                    } else {
                        logMeasureWarning(i);
                    }
                }
                width += childDimensions[CHILD_WIDTH];
                if (i == 0) {
                    height = childDimensions[CHILD_HEIGHT];
                }
                if (width >= widthSize) {
                    break;
                }
            }
        }

        if ((vertical && height < heightSize) || (!vertical && width < widthSize)) {

            if (exactWidth) {//固定宽度
                width = widthSize;
            } else {//最终宽度加上左右padding
                width += getPaddingLeft() + getPaddingRight();
            }

            if (exactHeight) {//固定高度
                height = heightSize;
            } else {//最终高度加上上下padding
                height += getPaddingTop() + getPaddingBottom();
            }

            setMeasuredDimension(width, height);
        } else {
            //如果计算的高宽超过了要的最大高宽，那就用默认实现处理。
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }
    }

    private void logMeasureWarning(int child) {
        if (BuildConfig.DEBUG)
            Log.w("LinearLayoutManager", "Cwan't measure child #" + child + ", previously used dimensions will be reused." +
                    "To remove this message either use #setChildSize() method or don't run RecyclerView animations");
    }

    private void initChildDimensions(int width, int height, boolean vertical) {
        if (childDimensions[CHILD_WIDTH] != 0 || childDimensions[CHILD_HEIGHT] != 0) {
            //高或宽有值，无需再设
            return;
        }
        if (vertical) {
            childDimensions[CHILD_WIDTH] = width;
            childDimensions[CHILD_HEIGHT] = childSize;
        } else {
            childDimensions[CHILD_WIDTH] = childSize;
            childDimensions[CHILD_HEIGHT] = height;
        }
    }

    @Override
    public void setOrientation(int orientation) {
        // might be called before the constructor of this class is called
        // noinspection ConstantConditions
        if (childDimensions != null) {
            if (getOrientation() != orientation) {
                childDimensions[CHILD_WIDTH] = 0;
                childDimensions[CHILD_HEIGHT] = 0;
            }
        }
        super.setOrientation(orientation);
    }

    public void clearChildSize() {
        hasChildSize = false;
        setChildSize(DEFAULT_CHILD_SIZE);
    }

    public void setChildSize(int childSize) {
        hasChildSize = true;
        if (this.childSize != childSize) {
            this.childSize = childSize;
            requestLayout();
        }
    }

    private void measureChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] dimensions) {
        final View child = recycler.getViewForPosition(position);

        final RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) child.getLayoutParams();

        final int hPadding = getPaddingLeft() + getPaddingRight();
        final int vPadding = getPaddingTop() + getPaddingBottom();

        final int hMargin = p.leftMargin + p.rightMargin;
        final int vMargin = p.topMargin + p.bottomMargin;

        final int hDecoration = getRightDecorationWidth(child) + getLeftDecorationWidth(child);
        final int vDecoration = getTopDecorationHeight(child) + getBottomDecorationHeight(child);

        final int childWidthSpec = getChildMeasureSpec(widthSpec, hPadding + hMargin + hDecoration, p.width, canScrollHorizontally());
        final int childHeightSpec = getChildMeasureSpec(heightSpec, vPadding + vMargin + vDecoration, p.height, canScrollVertically());

        child.measure(childWidthSpec, childHeightSpec);

        dimensions[CHILD_WIDTH] = getDecoratedMeasuredWidth(child) + p.leftMargin + p.rightMargin;
        dimensions[CHILD_HEIGHT] = getDecoratedMeasuredHeight(child) + p.bottomMargin + p.topMargin;

        recycler.recycleView(child);
    }
}
