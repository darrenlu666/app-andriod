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
public class SelectLeftAdapter extends BaseAdapter {
    private Context context;
    private List<AreaBase> areaInfos;
    private String selectName = "全部";
    private String mSelectID;

    public SelectLeftAdapter(Context context, List<AreaBase> areaInfos) {
        this.areaInfos = areaInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return areaInfos.size();
    }

    @Override
    public AreaBase getItem(int position) {
        return areaInfos.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.select_left_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.select_left_item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if ("school".equals(areaInfos.get(position).getType())) {
            viewHolder.name.setText(Html.fromHtml(areaInfos.get(position).getAreaName()));
        } else {
//            if ("全部".equals(areaInfos.get(position).getAreaName()) || "直属校".equals(areaInfos.get(position).getAreaName())) {
            viewHolder.name.setText(Html.fromHtml(areaInfos.get(position).getAreaName()));
//            } else {
//                viewHolder.name.setText(areaInfos.get(position).getAreaName() + areaInfos.get(position).getLevel());
//            }
        }
        if (areaInfos.get(position).getAreaName().equals(selectName)) {
            viewHolder.name.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            viewHolder.name.setTextColor(context.getResources().getColor(R.color.personal_text_color));
        }
        return convertView;
    }

    class ViewHolder {
        TextView name;
    }

    public void setSelectName(String name) {
        this.selectName = name;
    }
}
