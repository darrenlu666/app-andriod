package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.MapInfo;

import java.util.List;

/**
 * Created by yangxinwu on 2015/7/4.
 */
public class SelectAdapter extends BaseAdapter {
    private Context mContext;
    private List<MapInfo> mData;
    private LayoutInflater listContainer;

    class ViewHolder { // 自定义控件集合
        TextView title;
    }

    public SelectAdapter(Context context, List<MapInfo> data) {
        this.mContext = context;
        this.mData = data;
        this.listContainer = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mData.size();
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            // 获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.pop_item,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MapInfo mapInfo = mData.get(position);
        viewHolder.title.setText(mapInfo.getTitle());
        return convertView;
    }
}