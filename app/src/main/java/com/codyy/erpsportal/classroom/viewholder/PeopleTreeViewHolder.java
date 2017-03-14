package com.codyy.erpsportal.classroom.viewholder;

import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.models.Watcher;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;

/**
 * Created by poe on 17-3-13.
 */

public class PeopleTreeViewHolder extends BaseRecyclerViewHolder<Watcher> {

    public PeopleTreeViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_recycler_title_bar;
    }

    @Override
    public void setData(int position, Watcher data) throws Throwable {

    }

}
