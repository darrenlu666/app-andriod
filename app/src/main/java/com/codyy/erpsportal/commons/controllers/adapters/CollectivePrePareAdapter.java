package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.PreparationEntity;

import java.util.List;

/**
 * Created by yangxinwu on 2015/8/3.
 */
public class CollectivePrePareAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<PreparationEntity> mData;

    public CollectivePrePareAdapter(Context context) {
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<PreparationEntity> getData() {
        return mData;
    }

    public void setData(List<PreparationEntity> data) {
        this.mData = data;
    }

    public void addData(List<PreparationEntity> data) {
        if (mData != null) {
            mData.addAll(data);
        } else {
            setData(mData);
        }

    }

    public void clearData() {
        if (mData != null)
            mData.clear();
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_collective_prepare, null);
            holder.rlDate = (RelativeLayout)convertView.findViewById(R.id.rl_date);
            holder.rlStar = (RelativeLayout)convertView.findViewById(R.id.rl_star);
            holder.ratingBar=(RatingBar)convertView.findViewById(R.id.rb_star);
            holder.headerImage =(ImageView)convertView.findViewById(R.id.iv_state);
            holder.title =(TextView)convertView.findViewById(R.id.tv_title);
            holder.teachName =(TextView)convertView.findViewById(R.id.tv_name);
            holder.date =(TextView)convertView.findViewById(R.id.tv_date);
            holder.dateTitle = (TextView)convertView.findViewById(R.id.tv_date_text);
            //holder.index =(TextView)convertView.findViewById(R.id.tv_index);
            holder.subject=(TextView)convertView.findViewById(R.id.tv_subject);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //PreparationEntity preparationEntity= mData.get(position);
        if (position%3==0/*preparationEntity.getStatus().equals("INIT")*/) {
            holder.headerImage.setBackgroundResource(R.drawable.unstart);
            holder.dateTitle.setText("预约开始时间：");
            holder.rlDate.setVisibility(View.VISIBLE);
            holder.rlStar.setVisibility(View.GONE);
        }else if (position%3==1/*preparationEntity.getStatus().equals("PROGRESS")*/) {
            holder.headerImage.setBackgroundResource(R.drawable.xxhdpi_assessmenting_);
            holder.dateTitle.setText("开始时间：");
            holder.rlDate.setVisibility(View.VISIBLE);
            holder.rlStar.setVisibility(View.GONE);
        }else{
            holder.headerImage.setBackgroundResource(R.drawable.xxhdpi_end);
            holder.rlDate.setVisibility(View.GONE);
            holder.rlStar.setVisibility(View.VISIBLE);
        }
        //holder.title.setText(preparationEntity.getTitle());
        //holder.teachName.setText(preparationEntity.getTeacherName());
        //holder.date.setText(preparationEntity.getStartDate());
        //holder.subject.setText(preparationEntity.getSubjectName());

        return convertView;
    }


    private static class ViewHolder {
        RelativeLayout rlDate;
        RelativeLayout rlStar;
        RatingBar ratingBar;
        ImageView headerImage;
        TextView title;
        TextView teachName;
        TextView date;
        TextView dateTitle;
        TextView index;
        TextView subject;
    }
}
