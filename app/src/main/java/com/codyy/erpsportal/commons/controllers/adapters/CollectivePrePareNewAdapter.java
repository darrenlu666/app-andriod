package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;

/**
 * Created by yangxinwu on 2015/8/3.
 */
public class CollectivePrePareNewAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;

    public CollectivePrePareNewAdapter(Context context){
        this.mContext = context;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_collective_prepare_lessons, null);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        return convertView;
    }


    private static class ViewHolder {
        private TextView mTitle ;
        private RelativeLayout mVideoCountView;
    }
}
