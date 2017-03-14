package com.codyy.erpsportal.schooltv.controllers.viewholders;

import android.view.View;

import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.schooltv.models.SchoolVideo;

/**
 * Created by poe on 17-3-14.
 */

public class SchoolVideoViewHolder extends BaseRecyclerViewHolder<SchoolVideo> {

    public SchoolVideoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int obtainLayoutId() {
        return 0;
    }

    @Override
    public void setData(int position, SchoolVideo data) throws Throwable {

    }
}
