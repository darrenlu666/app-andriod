package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.RateActivity;
import com.codyy.erpsportal.commons.models.entities.StandardDetails;
import com.codyy.erpsportal.commons.widgets.SeekBarView;

import java.util.List;

/**
 * Created by kmdai on 2015/4/24.
 */
public class StandardAdapter extends BaseAdapter {
    private Context context;
    private List<StandardDetails> standardDetailses;
    private Handler mHandler;

    public StandardAdapter(Context context, List<StandardDetails> standardDetailses, Handler mHandler) {
        this.context = context;
        this.standardDetailses = standardDetailses;
        this.mHandler = mHandler;
    }

    @Override
    public int getCount() {
        return standardDetailses.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.rate_layout_item, null);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.rate_item_title);
            viewHolder.title = (TextView) convertView.findViewById(R.id.rate_layout_title);
            viewHolder.cont = (TextView) convertView.findViewById(R.id.rate_item_text_content);
            viewHolder.seekBarView = (SeekBarView) convertView.findViewById(R.id.rate_item_seekbar);
            viewHolder.bg = convertView.findViewById(R.id.rate_item_view_bg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            viewHolder.linearLayout.setVisibility(View.VISIBLE);
            viewHolder.title.setText(Html.fromHtml(standardDetailses.get(position).getItemName()));
        } else {
            if (standardDetailses.get(position - 1).getItemId().equals(standardDetailses.get(position).getItemId())) {
                viewHolder.linearLayout.setVisibility(View.GONE);
            } else {
                viewHolder.linearLayout.setVisibility(View.VISIBLE);
                viewHolder.title.setText(Html.fromHtml(standardDetailses.get(position).getItemName()));
            }
        }
        if (position + 1 < standardDetailses.size()) {
            if (standardDetailses.get(position).getItemId().equals(standardDetailses.get(position + 1).getItemId())) {
                viewHolder.bg.setVisibility(View.GONE);
            } else {
                viewHolder.bg.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.bg.setVisibility(View.GONE);
        }
        if (standardDetailses.get(position).isContent()) {
            viewHolder.cont.setVisibility(View.VISIBLE);
            viewHolder.cont.setText(Html.fromHtml(standardDetailses.get(position).getContent()));
        } else {
            viewHolder.cont.setVisibility(View.GONE);
        }
        viewHolder.seekBarView.setMax(standardDetailses.get(position).getScore());
        viewHolder.seekBarView.setScore(standardDetailses.get(position).getShowScore());
        viewHolder.seekBarView.setOnSeekListener(new SeekBarView.OnSeekListener() {
            @Override
            public void getScore(int score) {
                standardDetailses.get(position).setShowScore(score);
                mHandler.sendEmptyMessage(RateActivity.SCORE_CHANGE);
            }
        });
        return convertView;
    }

    class ViewHolder {
        LinearLayout linearLayout;
        TextView title;
        TextView cont;
        SeekBarView seekBarView;
        View bg;
    }
}
