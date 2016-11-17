package com.codyy.erpsportal.exam.controllers.activities.media.audio;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;

import java.util.List;

/**
 * @author lijian
 */
public class MMAudioListAdapter extends MMBaseRecyclerViewAdapter<MMAudioBean> {
    public MMAudioListAdapter(List<MMAudioBean> list, Context context) {
        super(list, context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.multimedia_item_audio_list, parent, false);
        return new NormalRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        ((NormalRecyclerViewHolder) viewHolder).tvName.setText(list.get(position).getName());
        ((NormalRecyclerViewHolder) viewHolder).tvTime.setText(mContext.getString(R.string.exam_audio_time, list.get(position).getTime()));
        ((NormalRecyclerViewHolder) viewHolder).tvDate.setText(mContext.getString(R.string.exam_audio_date, list.get(position).getDate()));
    }

    private static class NormalRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvTime;
        TextView tvDate;

        private NormalRecyclerViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_audio_name);
            tvTime = (TextView) view.findViewById(R.id.tv_audio_time);
            tvDate = (TextView) view.findViewById(R.id.tv_audio_date);
        }
    }
}
