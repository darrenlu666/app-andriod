package com.codyy.erpsportal.commons.widgets.infinitepager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gujiajia on 2016/5/17.
 */
public class SlidePagerAdapter extends InfinitePagerAdapter {

    private final static String TAG = "SlidePagerAdapter";

    private List mItems;

    private HolderCreator mHolderCreator;

    /**
     * view垃圾桶，用于复用销毁的view，节省不断创建view的开销
     */
    private LinkedList<View> mTrash;

    public SlidePagerAdapter(List item, HolderCreator holderCreator){
        this.mItems = item;
        mTrash = new LinkedList<>();
        mHolderCreator = holderCreator;
    }

    @Override
    public int getItemCount() {
        return mItems == null? 0 : mItems.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view ;
        SlidePagerHolder viewHolder;
        if (!mTrash.isEmpty()) {//垃圾桶里有，则拿出来复用
            view = mTrash.pollLast();
            viewHolder = (SlidePagerHolder) view.getTag();
        } else {
            view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_resource_intro, container, false);
            viewHolder = mHolderCreator.create(view);
            view.setTag(viewHolder);
        }

        Object object = mItems.get(position % mItems.size());
        viewHolder.bindView(object);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView( view);
        mTrash.add( view);
    }
}
