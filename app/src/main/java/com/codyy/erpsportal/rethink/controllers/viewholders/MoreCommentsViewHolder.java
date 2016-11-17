package com.codyy.erpsportal.rethink.controllers.viewholders;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.models.entities.MoreComments;
import com.codyy.erpsportal.rethink.models.entities.MoreCommentsEvent;

import de.greenrobot.event.EventBus;

/**
 * 更多评论 视图保持器
 * Created by gujiajia on 2016/7/5.
 */
public class MoreCommentsViewHolder extends RecyclerViewHolder<MoreComments> {

    private final static String TAG = "MoreCommentsViewHolder";

    private Button moreBtn;

    public MoreCommentsViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void mapFromView(View view) {
        moreBtn = (Button) view.findViewById(R.id.btn_more);
    }

    @Override
    public void setDataToView(MoreComments data) {
        moreBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cog.d(TAG, "onMoreClick");
                EventBus.getDefault().post(new MoreCommentsEvent());
            }
        });
    }
}
