package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.ThemeVideo;
import com.codyy.erpsportal.commons.utils.Cog;

import java.util.List;

public class HorizontalListViewAdapter2 extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater;
	private int selectIndex = -1;
	private List<ThemeVideo> videoDetailsList;

	public HorizontalListViewAdapter2(Context context, List<ThemeVideo> videoDetailsList){
		this.mContext = context;
		mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
		this.videoDetailsList = videoDetailsList;
	}
	@Override
	public int getCount() {
		return videoDetailsList.size();
	}
	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.horizontal_list_item_video_view, null);
			holder.mTitle=(TextView)convertView.findViewById(R.id.tv_video_count);
			holder.mVideoCountView=(RelativeLayout)convertView.findViewById(R.id.ll_video_count_view);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		Cog.e("HorizontalListViewAdapter2---",position+"--"+selectIndex);
		if(position == selectIndex){
			holder.mTitle.setTextColor(mContext.getResources().getColor(R.color.video_text_selected));
			holder.mVideoCountView.setBackgroundResource(R.drawable.video_count_bg_selected);
		}else{
			holder.mTitle.setTextColor(mContext.getResources().getColor(R.color.video_text_unselected));
			holder.mVideoCountView.setBackgroundResource(R.drawable.video_count_bg_unselected);
		}
		
		holder.mTitle.setText((position+1)+"");

		return convertView;
	}

	private static class ViewHolder {
		private TextView mTitle ;
		private RelativeLayout mVideoCountView;
	}
	public void setSelectIndex(int i){
		selectIndex = i;
	}
}