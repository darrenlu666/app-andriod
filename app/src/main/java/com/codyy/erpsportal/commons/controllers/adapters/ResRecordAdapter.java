package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;
import com.codyy.erpsportal.commons.models.entities.VideoBarMapInfo;

import java.util.List;

/**
 * Created by yangxinwu on 2015/9/7.
 */
public class ResRecordAdapter extends BaseAdapter{
    private RemoteDirectorConfig mConfig;
    private Context mContext;
    private List<VideoBarMapInfo> mVideoBarData;
    private LayoutInflater listContainer;


    public ResRecordAdapter(Context context, List<VideoBarMapInfo> mVideoBarData, RemoteDirectorConfig mConfig) {
        this.mContext = context;
        this.mVideoBarData = mVideoBarData;
        this.listContainer = LayoutInflater.from(context);
        this.mConfig=mConfig;
    }
    @Override
    public int getCount() {
        return mVideoBarData.size();
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
            convertView = View.inflate(mContext, R.layout.item_res_record, null);
            viewHolder = new ViewHolder();
            viewHolder.cb_res=(CheckBox)convertView.findViewById(R.id.ck_res);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        VideoBarMapInfo mapInfo = mVideoBarData.get(position);
        viewHolder.cb_res.setText(mapInfo.getTitle());
        if(mapInfo.isVideoRecord()) {
            viewHolder.cb_res.setChecked(true);
        }
        return convertView;
    }
    class ViewHolder {
        CheckBox cb_res;
    }
}