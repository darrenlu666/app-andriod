package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.FilterFragment;

import java.util.List;


/**
 * Created by kmdai on 2015/6/10.
 */
public class SingleAdapter extends BaseAdapter {
    private Context context;
    private List<FilterFragment.Status> single;
    private String select = "全部";

    public SingleAdapter(Context context, List<FilterFragment.Status> single) {
        this.context = context;
        this.single = single;
    }

    @Override
    public int getCount() {
        if (single != null) {
            return single.size();
        }
        return 0;
    }

    @Override
    public FilterFragment.Status getItem(int position) {
        return single.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.select_left_item, null);
        TextView textView = (TextView) view.findViewById(R.id.select_left_item_text);
        if (single.get(position).getName().equals(select)) {
            textView.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            textView.setTextColor(Color.parseColor("#707070"));
        }
        textView.setText(single.get(position).getName());
        return view;
    }

    /**
     * 设置选着
     *
     * @param select
     */
    public void setSelect(String select) {
        this.select = select;
    }
}
