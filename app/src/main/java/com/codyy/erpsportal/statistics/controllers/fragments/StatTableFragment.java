package com.codyy.erpsportal.statistics.controllers.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.statistics.models.entities.StatCell;
import com.codyy.erpsportal.statistics.models.entities.StatTableModel;
import com.codyy.erpsportal.statistics.widgets.FeedbackScrollView;
import com.codyy.erpsportal.statistics.widgets.FeedbackScrollView.OnScrollChangeListener;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by gujiajia on 2016/5/31.
 */
public class StatTableFragment extends Fragment {

    private final static String TAG = "StatTableFragment";

    private ListView mListView;

    private StatisticsAdapter mStatisticsAdapter;

    private FeedbackScrollView mScrollView;

    private TextView mTableTitleTv;

    private StatTableModel<?> mTableModel;

    private OnRowClickListener mOnRowClickListener;

    private ColorStateList mRowTitleColorSl;

    private int mRowTitleColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRowTitleColorSl = getResources().getColorStateList(R.color.sl_tv_stat_table_item);
        mRowTitleColor = getResources().getColor(R.color.main_color);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmet_stat_table, container, false);
        mListView = (ListView) rootView.findViewById(R.id.lv_statistics);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cog.d(TAG, "onItemClick position=" + position);
                if (position != -1 && mOnRowClickListener != null) {
                    mOnRowClickListener.onRowClickListener(position);
                }
            }
        });
        mTableTitleTv = (TextView) rootView.findViewById(R.id.tv_row_title);
        mScrollView = (FeedbackScrollView) rootView.findViewById(R.id.scroll_view);
        mStatisticsAdapter = new StatisticsAdapter(mScrollView);
        mListView.setAdapter(mStatisticsAdapter);
        updateData();
        mScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Cog.d("TableLvActivity" , "ScrollView event=" + event);
                mScrollView.handleTouchEvent(event);
                return true;
            }
        });
        mListView.setOnTouchListener(new OnTouchListener() {

            private float initialX;

            private float initialY;

            private static final int MAX_CLICK_DURATION = 400;

            private static final int MAX_CLICK_DISTANCE = 10;

            /**
             * 记录有没有down事件
             */
            private boolean mHasDown;

            private int mInitialPosition = -1;

            private boolean stayedWithinClickDistance;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Cog.d(TAG, "onTouch event=" + event);
                mListView.onTouchEvent(event);
//                mScrollView.handleTouchEvent(event);
                int position = getTouchPosition(event);
                Cog.d(TAG,"onTouch position=" + position);
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        initialY = event.getY();
                        mScrollView.handleTouchEvent(event);
                        if (position != -1) {
                            TextView rowTitleTv = findRowTitle(position);
                            highlightTextView(rowTitleTv);
                            mInitialPosition = position;
                        }
                        mHasDown = true;
                        stayedWithinClickDistance = true;
                        break;
                    case MotionEvent.ACTION_UP:{
                        long clickDuration = event.getEventTime() - event.getDownTime();
                        if (clickDuration <= MAX_CLICK_DURATION && stayedWithinClickDistance) {
                            mListView.performItemClick(mListView, mInitialPosition, mInitialPosition);
                        }
                        if (mHasDown) mScrollView.handleTouchEvent(event);
                        if (mInitialPosition != -1){
                            TextView rowTitleTv = findRowTitle(mInitialPosition);
                            cancelHighlightTextView(rowTitleTv);
                        }
                        mHasDown = false;
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:
                        if (mHasDown) mScrollView.handleTouchEvent(event);
                        if (mInitialPosition != -1){
                            TextView rowTitleTv = findRowTitle(mInitialPosition);
                            cancelHighlightTextView(rowTitleTv);
                        }
                        mHasDown = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                        if (Math.abs(event.getX() - initialX) > scaledTouchSlop
                                || Math.abs(event.getY() - initialY) > scaledTouchSlop ) {
                            if (mInitialPosition != -1){
                                TextView rowTitleTv = findRowTitle(mInitialPosition);
                                cancelHighlightTextView(rowTitleTv);
                            }
                        }
                        if (stayedWithinClickDistance
                                && distance(initialX, initialY, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                            stayedWithinClickDistance = false;
                        }
                    default:
                        if (mHasDown) mScrollView.handleTouchEvent(event);
                        break;
                }
                return true;
            }
        });
        return rootView;
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void highlightTextView(TextView textView ) {
        if (textView == null) return;
        textView.setTextColor( mRowTitleColor);
    }

    private void cancelHighlightTextView(TextView textView) {
        if (textView == null) return;
        textView.setTextColor( mRowTitleColorSl);
    }

    private TextView findRowTitle(int position){
        if (position == -1) return null;
        int index = position - mListView.getFirstVisiblePosition();
        Cog.d(TAG, "findRowTitle index=" + index);
        if (index < 0 || index >= mListView.getChildCount() ) return null;
        View view = mListView.getChildAt(index);
        return (TextView) view.findViewById(R.id.tv_row_title);
    }

    private int getTouchPosition(MotionEvent event) {
        return mListView.pointToPosition((int)event.getX(), (int)event.getY());
    }

    public void setTableModel(StatTableModel<?> statTableModel) {
        this.mTableModel = statTableModel;
        updateData();
    }

    private void updateData() {
        if (mTableModel == null) return;
        if (mStatisticsAdapter != null) {
            mStatisticsAdapter.setStatTableModel(mTableModel);
            mStatisticsAdapter.notifyDataSetChanged();
        }
        if (mTableTitleTv != null) {
            mTableTitleTv.setEms(mTableModel.getEms());
            mTableTitleTv.setText(mTableModel.getTitle());
        }
    }

    public void setOnRowClickListener(OnRowClickListener onRowClickListener) {
        mOnRowClickListener = onRowClickListener;
    }

    public static StatTableFragment newInstance() {
        return new StatTableFragment();
    }

    class StatisticsAdapter extends BaseAdapter implements OnScrollChangeListener {

        private StatTableModel mTableModel;

//        private String[] mColumns = {"应受邀教室", "实受邀教室", "受邀教室占比", "计划课时" , "实听课时", "实听课时占比", "平均听课"};

        private int[] mColumnWidth;

        private int mCurrentLeft;

        private Set<FeedbackScrollView> mViews = new LinkedHashSet<>();

        private Context mContext;

        private LayoutInflater mLayoutInflater;

        private FeedbackScrollView mScrollView;

        private Drawable mDivider;

        private int mPadding;

        private Drawable mUpIcon;

        private Drawable mDownIcon;

        public StatisticsAdapter(FeedbackScrollView scrollView) {
            mContext = scrollView.getContext();
            mLayoutInflater = LayoutInflater.from(mContext);
            mDivider = scrollView.getResources().getDrawable(R.drawable.divider_column);
            mScrollView = scrollView;
            mScrollView.setOnScrollChangeListener(this);
            mPadding = UIUtils.dip2px(scrollView.getContext(), 8);
            mUpIcon = ResourcesCompat.getDrawable(scrollView.getResources(), R.drawable.ic_increase, null);
            mDownIcon = ResourcesCompat.getDrawable(scrollView.getResources(), R.drawable.ic_decrease, null);
        }

        public void setStatTableModel(StatTableModel tableModel) {
            mTableModel = tableModel;
            initColumnWidth();
            setupColumnTitle();
        }

        private void initColumnWidth() {
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.sp16));
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.dp8);
            mColumnWidth = new int[mTableModel.columnCount()];
            for (int i = 0; i<mTableModel.columnCount(); i++) {
                mColumnWidth[i] = padding
                        + padding
                        + (int)textPaint.measureText(mTableModel.getColumnTitle(i));
            }
        }

        private void setupColumnTitle() {
            LinearLayout columnTitleContainer = (LinearLayout) mScrollView.findViewById(R.id.ll_columns);
            columnTitleContainer.setDividerDrawable(mDivider);
            columnTitleContainer.removeAllViews();
            for (int i = 0; i<mTableModel.columnCount(); i++) {
                TextView textView = new TextView(columnTitleContainer.getContext());
                textView.setPadding(mPadding, 0, mPadding, 0);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                textView.setLines(1);
                textView.setText(mTableModel.getColumnTitle(i));
                columnTitleContainer.addView(textView,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            }
        }

        @Override
        public int getCount() {
            return mTableModel==null? 0: mTableModel.rowCount();
        }

        @Override
        public Object getItem(int position) {
            return mTableModel.getRow(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder viewHolder;
            if ( convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_right_scroll, parent, false);
                viewHolder = new ViewHolder(convertView);
                viewHolder.rowTitleTv.setEms(mTableModel.getEms());
                viewHolder.setColumnWidth(mColumnWidth);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.scrollView.smoothScrollTo(mCurrentLeft, 0);
            viewHolder.rowTitleTv.setText(mTableModel.getRowTitle(position));
            viewHolder.rowTitleTv.setTextColor(mRowTitleColorSl);

            final View finalConvertView = convertView;
            viewHolder.rowTitleTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AbsListView)parent).performItemClick(finalConvertView, position, position);
                }
            });

            for (int i=0; i<mColumnWidth.length; i++) {
                TextView cellTv = viewHolder.cellTvs[i];
                cellTv.setText(mTableModel.getCellStr(position, i));
                int cellStatus = mTableModel.getCellStatus(position, i);
                if (cellStatus == StatCell.STATUS_INCREASE) {
                    cellTv.setCompoundDrawablesWithIntrinsicBounds(null, null, mUpIcon, null);
                } else if (cellStatus == StatCell.STATUS_DECREASE) {
                    cellTv.setCompoundDrawablesWithIntrinsicBounds(null, null, mDownIcon, null);
                } else {
                    cellTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
            addViewToList(viewHolder.scrollView);
            return convertView;
        }

        private void addViewToList(FeedbackScrollView view) {
            mViews.add(view);
        }

        @Override
        public void onScrollChange(View view, int l, int t, int oldl, int oldt) {
            for (FeedbackScrollView scrollView: mViews) {
                if (!view.equals(scrollView)){
                    scrollView.smoothScrollTo(l, 0);
                }
            }
            mCurrentLeft = l;
        }
    }

    class ViewHolder {
        TextView rowTitleTv;
        FeedbackScrollView scrollView;
        LinearLayout columnLl;
        TextView[] cellTvs;
        LayoutInflater layoutInflater;

        public ViewHolder(View contentView){
            layoutInflater = LayoutInflater.from(contentView.getContext());
            findViews(contentView);
            contentView.setTag(this);
        }

        public void findViews(View contentView) {
            scrollView = (FeedbackScrollView) contentView.findViewById(R.id.scroll_view);
            rowTitleTv = (TextView) contentView.findViewById(R.id.tv_row_title);
            columnLl = (LinearLayout) contentView.findViewById(R.id.ll_columns);
        }

        public void setColumnWidth(int[] columnWidth) {
            if (columnWidth == null || columnWidth.length == 0) {
                return;
            }
            cellTvs = new TextView[columnWidth.length];
            for (int i=0; i<columnWidth.length; i++) {
                View view = layoutInflater.inflate(R.layout.item_stat_cell, columnLl, false);
                cellTvs[i] = (TextView) view.findViewById(R.id.tv_cell);
                LayoutParams layoutParams = new LayoutParams(columnWidth[i], LayoutParams.WRAP_CONTENT);
                columnLl.addView(view, layoutParams);
            }
        }
    }

    public interface OnRowClickListener{
        void onRowClickListener(int position);
    }
}
