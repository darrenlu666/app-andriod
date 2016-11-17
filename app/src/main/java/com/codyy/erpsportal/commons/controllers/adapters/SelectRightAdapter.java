package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.AreaBase;

import java.util.List;


/**
 * Created by kmdai on 2015/4/14.
 */
public class SelectRightAdapter extends BaseAdapter {
    private Context context;
    private List<AreaBase> areaInfos;
    private int mSlectID = -1;

    public SelectRightAdapter(Context context, List<AreaBase> areaInfos) {
        this.areaInfos = areaInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return areaInfos.size();
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
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.select_item_right_layout, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.select_province_text_name);
            viewHolder.level = (TextView) convertView.findViewById(R.id.select_province_text_level);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if ("全国".equals(areaInfos.get(position).getLevel())) {
            viewHolder.level.setVisibility(View.GONE);
        } else {
            viewHolder.level.setVisibility(View.VISIBLE);
            viewHolder.level.setText(areaInfos.get(position).getLevel());
        }
        viewHolder.name.setText(Html.fromHtml(areaInfos.get(position).getAreaName()));
        if (mSlectID == position) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.personal_divide_line));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView level;
    }

    public int getmSlectID() {
        return mSlectID;
    }

    public void setmSlectID(int mSlectID) {
        this.mSlectID = mSlectID;
    }
}
