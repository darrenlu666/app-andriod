package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.HistoryVideoDetail;

import java.util.List;

/**
 * Created by ldh on 2015/9/15.
 */
public class VideoListFragment extends Fragment {

    private List<HistoryVideoDetail.DataEntity>  pVideoList;
    private View mVideoListView;
    private GridView mVideoListGridView;
    private GridViewAdapter mGridViewAdapter;
    private OnClickListener mOnclickListener;

    public void setVideoList(List<HistoryVideoDetail.DataEntity> videoList){
        pVideoList = videoList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mOnclickListener   = (OnClickListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mVideoListView = inflater.inflate(R.layout.fragment_video_list_gridview, null, false);
        mVideoListGridView = (GridView)mVideoListView.findViewById(R.id.gv_video_list);
        mGridViewAdapter = new GridViewAdapter();
        mVideoListGridView.setAdapter(mGridViewAdapter);

        return mVideoListView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class GridViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return pVideoList == null? 0:pVideoList.size();
        }

        @Override
        public Object getItem(int position) {
            return pVideoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder mViewHolder = null;
            if(convertView == null){
                mViewHolder = new ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_video_list,null);
                mViewHolder.mVideoBtn = (Button)convertView.findViewById(R.id.btn_video_list);
                convertView.setTag(mViewHolder);
            }else{
                mViewHolder = (ViewHolder)convertView.getTag();
            }
            mViewHolder.mVideoBtn.setText((position + 1) + "");

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE); // 画框
            drawable.setStroke(1, Color.GRAY); // 边框粗细及颜色
            drawable.setColor(Color.WHITE); // 边框内部颜色

            mViewHolder.mVideoBtn.setBackgroundDrawable(drawable);

            mViewHolder.mVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "当前播放第" + (position + 1) + "集", Toast.LENGTH_SHORT).show();
//                    HistoryVideoPlayActivity.setVideoPosition(pVideoList.get(position).getLiveAppointmentVideoId());
                    if(null != mOnclickListener ){
                        mOnclickListener.onSelected(pVideoList.get(position).getLiveAppointmentVideoId());
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {
            Button mVideoBtn;
        }
    }


    public interface OnClickListener{
        /**
         *  选择不同的分段 .
         * @param id
         */
        void onSelected(String id);
    }
}
