package com.codyy.erpsportal.commons.controllers.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.LocationBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ningfeng on 2015/8/11.
 */
public class LocationAdapter extends BaseAdapter {
    List<LocationBean> mData=null;

    public LocationAdapter(List<LocationBean> data)
    {
        this.mData=data;
    }
    @Override
    public int getCount() {
        return this.mData==null?0:this.mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LocationAdapter.ViewHolder viewHolder=null;
        if (view==null)
        {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_location, null);
            viewHolder=new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.tvSchool.setText(mData.get(i).getName());
        return view;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_location.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.tv_school)
        TextView tvSchool;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
