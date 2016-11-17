package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.codyy.erpsportal.R;

/**
 * Created by gujiajia on 2015/9/15.
 */
public class ResourcesSheet extends LinearLayout {

    private TextView mTitleTv;

    private GridView mGridView;

    public ResourcesSheet(Context context) {
        this(context, null);
    }

    public ResourcesSheet(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResourcesSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.resource_sheet, this, true);
        mTitleTv = (TextView) findViewById(R.id.tv_title1);
        mGridView = (GridView) findViewById(R.id.gv_resource1);
    }

    public void setAdapter(ListAdapter adapter) {
        mGridView.setAdapter(adapter);
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mGridView.setOnItemClickListener(onItemClickListener);
    }
}
