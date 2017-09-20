package com.codyy.erpsportal.commons.widgets.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.tpmp.filterlibrary.viewholders.TitleItemViewHolder;

import java.util.ArrayList;
import java.util.List;

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
    private IMultiLine mIGridLayoutViewHolder;

    public SimpleBisectDivider(Drawable divider, int space, IMultiLine iGridLayoutViewHolder) {
        this(divider, false);
        this.mSpace = space;
        this.mIGridLayoutViewHolder = iGridLayoutViewHolder;
        mLastPosition = 0;

        //IGridLayoutViewHolder 多变的item不需要绘制divider .
        addNoDividers(mIGridLayoutViewHolder.obtainMultiSingleLine());
        addNoDividers(mIGridLayoutViewHolder.obtainMultiInLine());
    }

    private void addNoDividers(int[] types) {
        if (types != null && types.length > 0) {
            for (int type : types) {
                addNoDivider(type);
            }
        }
    }

    public SimpleBisectDivider(Drawable divider, boolean isHeadDraw) {
        this.mDivider = divider;
        this.isHeadDividerEnable = isHeadDraw;
        //不需要绘制divider
        mDividerDisappearCollections.add(TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE);
        mDividerDisappearCollections.add(TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA);
        mDividerDisappearCollections.add(TitleItemViewHolder.ITEM_TYPE_TITLE_MORE);
        mDividerDisappearCollections.add(TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA);
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
        if (gridLayoutManager == null) return;
        //如果下一个item的recyclerType为TitleBar or TitleBarMore 则 返回
        int position = parent.getChildLayoutPosition(view);
        int viewType = parent.getAdapter().getItemViewType(position);

        if (null != mIGridLayoutViewHolder) {
            if (isContains(viewType, mIGridLayoutViewHolder.obtainMultiSingleLine())) {
//                isBigShow = true;
                outRect.top = mSpace;
                outRect.left = mSpace;
                outRect.right = mSpace;
                outRect.bottom = mSpace;
//                Cog.i(TAG,position+"::t/l/r/b==>"+mSpace);
                return;
            } else if (isContains(viewType, mIGridLayoutViewHolder.obtainMultiInLine())) {
                //记录初始位置,仅当第一位ｉｔｅｍ不是{@link HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE}记录下开始的位置.
                if (mLastPosition == 0
                        && parent.getAdapter().getItemViewType(0) != viewType) {
                    mLastPosition = position;
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
     * 判断bigArray是否包含此类型viewType
     * @param viewType
     * @param bigArray
     * @return
     */
    private boolean isContains(int viewType, int[] bigArray) {
        boolean contains = false;
        if(null != bigArray && bigArray.length >0 ){
            for(int type:bigArray){
                if(viewType == type){
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    /**
     * 设置grid等分space
     *
     * @param outRect  item的方框.
     * @param position 　在当前布局下的位置.
     * @param count    一行显示的列数.
     */
    private void setSpace(Rect outRect, int position, int count) {
        if (position < count && !isBigShow) {
            outRect.top = mSpace;
        } else {
            outRect.top = 0;
        }
        outRect.bottom = mSpace;
        int pos = position % count;
//        Log.i(TAG, " pos : " + position + " real pos : " + pos + " count:" + count);
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

//        Cog.i(TAG,position+"::t/l/r/b==>"+outRect.top+"/"+outRect.left+"/"+outRect.right+"/"+outRect.bottom);
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

            for (int i = 0; i < childCount; i++) {

                //判断第一个item的下标是不是0，是则return，不需要draw divider
                if (i == 0 && firstVisiblePosition == 0) {
                    continue;
                }
                //如果下一个item的recyclerType为TitleBar or TitleBarMore 则 返回
                int count = parent.getAdapter().getItemCount();
                boolean isLastTitle = false;
                boolean isNeedJump = false;
                int viewType = parent.getAdapter().getItemViewType(i + firstVisiblePosition);
                if ((i + firstVisiblePosition) < count) {
                    isLastTitle = isLastTitleBar(parent, firstVisiblePosition, viewType, i);
                    if (needDivider(viewType) && !isLastTitle) {
                        isNeedJump = true;
                    }
                }

                //continue to next loop .
                if (isNeedJump) continue;
                View childView = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
                //过滤标题的下方divider 的 padding
                int left = isLastTitle ? 0 : (parent.getPaddingLeft() + childView.getPaddingLeft());
                int bottom = childView.getTop() - params.topMargin;
                //如果当前是大图,则divider需要上移一个space
                if (null != mIGridLayoutViewHolder&& isContains(viewType, mIGridLayoutViewHolder.obtainMultiSingleLine())) {
                    // Log.i(TAG, " last Position : " + mLastPosition);
                    bottom = bottom-mSpace;
                }
                int top = bottom - mDivider.getIntrinsicHeight();
                int right = parent.getWidth() - childView.getPaddingRight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
                Log.i(TAG,"draw divider left padding :"+left+" top padding:"+top);
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
     * 判断上一个viewType是否为标题项.
     *
     * @param parent
     * @param firstVisiblePosition
     * @param currentViewType      当前的viewType
     * @param i
     * @return
     */
    private boolean isLastTitleBar(RecyclerView parent, int firstVisiblePosition, int currentViewType, int i) {
        boolean isNeeded = false;
        int lastPosition = (i - 1);
        if (lastPosition > 0) {
            int lastViewType = parent.getAdapter().getItemViewType(lastPosition + firstVisiblePosition);
//            Log.i(TAG,"lastViewType: "+lastViewType+" / currentViewType: "+currentViewType);
            if (isTitleItem(lastViewType) && lastViewType != currentViewType) {
                //draw divider in the middle of two no divider item which last is a titleBar and current is other item view type .
                isNeeded = true;
            }
        }
//        Log.i(TAG,"isLastTitleBar : "+isNeeded);
        return isNeeded;
    }

    /**
     * 不希望绘制分割线集合
     */
    private List<Integer> mDividerDisappearCollections = new ArrayList<>();

    /**
     * 添加不会绘制分割线的viewType
     *
     * @param viewHolderType 不需要绘制分割线的地方.
     */
    public void addNoDivider(int viewHolderType) {
        if (!mDividerDisappearCollections.contains(viewHolderType)) {
            mDividerDisappearCollections.add(viewHolderType);
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
        if (mDividerDisappearCollections.contains(viewType)) {
            result = true;
        }
        return result;
    }

    /**
     * 是否为标题选项.
     *
     * @param viewType
     * @return
     */
    private boolean isTitleItem(int viewType) {
        boolean result = false;
        switch (viewType) {
            case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE:
            case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA:
            case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
            case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA:
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

    /**
     * 简单的单一类型多行混排
     */
    public abstract static class IGridLayoutViewHolder implements IMultiLine {
        /**
         * 单个item填充整行
         *
         * @return
         */
        public abstract int obtainSingleBigItemViewHolderType();

        /**
         * 多个item填充一行
         *
         * @return
         */
        public abstract int obtainMultiInLineViewHolderType();

        @Override
        public int[] obtainMultiInLine() {
            return new int[]{obtainMultiInLineViewHolderType()};
        }

        @Override
        public int[] obtainMultiSingleLine() {
            return new int[]{obtainSingleBigItemViewHolderType()};
        }
    }

    public interface IMultiLine {

        /**
         * 多个item在统一行之中显示
         *
         * @return
         */
        int[] obtainMultiInLine();

        /**
         * 单行显示一个大的item．
         *
         * @return
         */
        int[] obtainMultiSingleLine();
    }
}
