package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codyy.erpsportal.R;


/**
 * Created by yangxinwu on 2015/7/2.
 */
public class GridViewPositionAdapter extends BaseAdapter{
    private Context mContext;
    private int mSelectPostion = -1;// 选中的位置
    private boolean mAllDisabled = false;// 选中的位置
    private int mEnableNumber = -1;// 选中的位置

    public GridViewPositionAdapter(Context context) {
        mContext = context;
    }
    @Override
    public int getCount() {
        return 9;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setClickPosition(int position) {
        mSelectPostion = position;
    }
    public void setAllDisabled(boolean isEnabled) {
        mAllDisabled = isEnabled;
    }


    public void setmEnableNumber(int mEnableNumber) {
        this.mEnableNumber = mEnableNumber;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_gridview2, null);
            viewHolder.tvPosition=(TextView)convertView.findViewById(R.id.tv_position);
            viewHolder.point=(View)convertView.findViewById(R.id.point);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mSelectPostion == position) {
            viewHolder.tvPosition.setTextColor(mContext.getResources().getColor(R.color.remote_text_press));
            viewHolder.tvPosition.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            viewHolder.point.setBackgroundColor(mContext.getResources().getColor(R.color.remote_text_press));
        } else {
            viewHolder.tvPosition.setTextColor(mContext.getResources().getColor(R.color.white));
            viewHolder.tvPosition.setBackgroundColor(mContext.getResources().getColor(R.color.remote_text_bg));
            viewHolder.point.setBackgroundColor(mContext.getResources().getColor(R.color.remote_text_bg));
        }
        return convertView;
    }
    class ViewHolder {
        TextView tvPosition;
        View point;
    }
}