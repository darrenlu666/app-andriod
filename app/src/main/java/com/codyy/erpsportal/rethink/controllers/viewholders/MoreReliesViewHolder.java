package com.codyy.erpsportal.rethink.controllers.viewholders;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.models.entities.MoreRelies;

import de.greenrobot.event.EventBus;

/**
 * 更多回复视图保持者
 * Created by gujiajia on 2016/7/5.
 */
public class MoreReliesViewHolder extends RecyclerViewHolder<MoreRelies> {

    private final static String TAG = "MoreReliesViewHolder";

    private Button moreRepliesBtn;

    public MoreReliesViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void mapFromView(View view) {
        moreRepliesBtn = (Button) view.findViewById(R.id.btn_more_relies);
    }

    @Override
    public void setDataToView(final MoreRelies moreRelies) {
        if (moreRelies.hasMore()) {
            moreRepliesBtn.setVisibility(View.VISIBLE);
            moreRepliesBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cog.d(TAG, "onMoreReplies click");
                    EventBus.getDefault().post(moreRelies);
                }
            });
        } else {
            moreRepliesBtn.setVisibility(View.GONE);
        }
    }
}
