package com.codyy.erpsportal.commons.models.engine;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

/**
 * 首页组件填充者
 * Created by gujiajia on 2016/8/19.
 */
public class MainItemFiller<T> {
    private LinearLayout mContainer;
    private View mTitleView;
    private View mEmptyView;
    private List<T> mList;
    private int mLayoutId;
    private ViewStuffer<T> mViewStuffer;
    private Context mContext;

    public MainItemFiller(LinearLayout container, View titleView, View emptyView, List<T> list, ViewStuffer<T> viewStuffer) {
        this.mContainer = container;
        this.mTitleView = titleView;
        this.mEmptyView = emptyView;
        this.mList = list;
        this.mViewStuffer = viewStuffer;
        this.mContext = container.getContext();
        Class<?> clazz = viewStuffer.getClass();
        ItemLayoutId itemLayoutId = clazz.getAnnotation(ItemLayoutId.class);
        if (itemLayoutId != null) mLayoutId = itemLayoutId.value();
    }

    public void doStuffWork() {
        if (mList == null || mList.size() == 0) {
            int groupCount = mContainer.indexOfChild(mEmptyView)
                    - mContainer.indexOfChild(mTitleView) - 1;
            if (groupCount > 0) {
                mContainer.removeViews(mContainer.indexOfChild(mTitleView) + 1, groupCount);
            }
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            int headIndex = mContainer.indexOfChild(mTitleView);
            int tailIndex = mContainer.indexOfChild(mEmptyView);
            int newIndex = headIndex;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            for (int i = 0; i< mList.size(); i++) {
                final T item = mList.get(i);
                newIndex++;//当前项所在View的位置
                if (newIndex >= tailIndex) {//说明需要插入新项
                    View view = inflater.inflate(mLayoutId, mContainer, false);
                    mViewStuffer.onStuffView(view, item);
                    mContainer.addView(view, newIndex);
                } else {//重用已有的项
                    View view = mContainer.getChildAt(newIndex);
                    mViewStuffer.onStuffView(view, item);
                }
                newIndex++;//插入分隔线位置
                if (newIndex >= tailIndex) {//插入分隔线
                    View divider = new View(mContext);
                    divider.setBackgroundColor(Color.LTGRAY);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    mContainer.addView(divider, newIndex, layoutParams);
                }
            }
            //新加载的项数据比老的数据少，把老的多出来的删除（未多出来的上面塞了新数据重用了）
            //没项提醒View的index大于新的项最后位置2以上，说明有多的老项可以删除
            if (tailIndex - newIndex > 2) {
                mContainer.removeViews(newIndex + 1, tailIndex - newIndex - 1);
            }
            mEmptyView.setVisibility(View.GONE);
        }
    }

}
