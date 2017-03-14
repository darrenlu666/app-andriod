package com.codyy.erpsportal.schooltv.controllers.viewholders;

import android.view.View;

import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.schooltv.models.SchoolProgram;

/**
 * 校园电视台-节目单-viewHolder
 * Created by poe on 17-3-14.
 */

public class SchoolProgramViewHolder extends BaseRecyclerViewHolder<SchoolProgram> {

    public SchoolProgramViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int obtainLayoutId() {
        return 0;
    }

    @Override
    public void setData(int position, SchoolProgram data) throws Throwable {

    }
}
