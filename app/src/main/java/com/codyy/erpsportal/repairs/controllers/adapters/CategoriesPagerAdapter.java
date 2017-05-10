package com.codyy.erpsportal.repairs.controllers.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.repairs.models.entities.CategoriesItem;

import java.util.List;

/**
 * 分类选择页适配器
 * Created by gujiajia on 2017/3/28.
 */

public class CategoriesPagerAdapter extends PagerAdapter {

    private List<CategoriesItem> mItems;

    private LayoutInflater mInflater;

    public CategoriesPagerAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItems == null? 0: mItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.pager_categories, container, false);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
