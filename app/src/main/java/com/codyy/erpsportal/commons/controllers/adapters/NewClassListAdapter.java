package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;
import com.codyy.erpsportal.commons.models.entities.VideoBarMapInfo;

import java.util.List;

/**
 * Created by yangxinwu on 2015/7/4.
 */
public class NewClassListAdapter extends BaseAdapter {
    private int mSelectPostion = -1;// 选中的位置
    private RemoteDirectorConfig mConfig;
    private Context mContext;
    private List<VideoBarMapInfo> mVideoBarData;
    private LayoutInflater listContainer;

    public NewClassListAdapter(Context context, List<VideoBarMapInfo> mVideoBarData, RemoteDirectorConfig mConfig) {
        this.mContext = context;
        this.mVideoBarData = mVideoBarData;
        this.listContainer = LayoutInflater.from(context);
        this.mConfig=mConfig;
    }
    public void setSelectPosition(int position) {
        mSelectPostion = position;
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
            convertView = View.inflate(mContext, R.layout.item_gridview, null);
            viewHolder = new ViewHolder();
            viewHolder.btnMode=(Button)convertView.findViewById(R.id.btn_mode);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        VideoBarMapInfo mapInfo = mVideoBarData.get(position);
        viewHolder.btnMode.setText(mapInfo.getTitle());
        if (mSelectPostion == position) {
            viewHolder.btnMode.setTextColor(mContext.getResources().getColor(R.color.remote_text_press));
            viewHolder.btnMode.setBackgroundColor(mContext.getResources().getColor(R.color.black));
        } else {
            viewHolder.btnMode.setTextColor(mContext.getResources().getColor(R.color.white));
            viewHolder.btnMode.setBackgroundColor(mContext.getResources().getColor(R.color.remote_text_bg));
        }
        return convertView;
    }
    class ViewHolder {
        Button btnMode;
    }
}