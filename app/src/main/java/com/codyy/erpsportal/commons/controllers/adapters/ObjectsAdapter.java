package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * object adapter
 * Created by Jia on 2014/12/11.
 */
public class ObjectsAdapter<T, VH extends AbsViewHolder<T>> extends BaseAdapter {

    protected Context mContext;

    private LayoutInflater mInflater;

    protected List<T> mData;

    private Class<VH> mViewHolderClass;

    private ViewHolderBuilder<VH> mBuilder;

    public ObjectsAdapter(Context context, Class<VH> clazz) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mViewHolderClass = clazz;
    }

    public ObjectsAdapter(Context context, ViewHolderBuilder<VH> builder) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mBuilder = builder;
    }

    @Override
    public int getCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public T getItem(int position) {
        if (mData != null && position >=0 && position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH viewHolder = null;
        if (convertView == null) {
            viewHolder = createViewHolder();
            convertView = mInflater.inflate(viewHolder.obtainLayoutId(), parent, false);
            viewHolder.mapFromView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (VH) convertView.getTag();
        }

        setDataToView(viewHolder, position);
        return convertView;
    }

    protected void setDataToView(VH viewHolder, int position) {
        viewHolder.setDataToView(mData, position, mContext);
    }

    protected VH createViewHolder() {
        if (mBuilder != null) {
            return mBuilder.createViewHolder();
        }
        try {
            return mViewHolderClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setData(List<T> data) {
        this.mData = data;
    }

    public void addData(List<T> data) {
        if (mData != null) {
            mData.addAll(data);
        } else {
            setData( data);
        }

    }

    public void addData(T item) {
        if (mData != null) {
            mData.add(item);
        } else {
            mData = new ArrayList<>();
            mData.add(item);
        }
    }

    public void clearData() {
        if (mData != null)
            mData.clear();
    }

    public List<T> getItems() {
        return mData;
    }

    public void removeItem(int position) {
        if (mData != null) {
            mData.remove(position);
        }
    }

    public void addItem(int position, T item) {
        if (mData != null) {
            mData.add(position, item);
        }
    }

    /**
     * The view holder builder.
     * @param <VH>
     */
    public interface ViewHolderBuilder<VH extends AbsViewHolder>{
        VH createViewHolder();
    }
}
