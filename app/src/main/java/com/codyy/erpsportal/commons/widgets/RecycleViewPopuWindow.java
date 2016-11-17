package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by kmdai on 16-6-24.
 */
public class RecycleViewPopuWindow extends PopupWindow {
    private View mRootView;
    private TextView mTitleDateTV;
    private RecyclerView mRecyclerView;

    public RecycleViewPopuWindow(Context context) {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(UIUtils.dip2px(context, 200));
        View root = LayoutInflater.from(context).inflate(R.layout.recycleview_popu, null);
        setContentView(root);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        setFocusable(true);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }
}
