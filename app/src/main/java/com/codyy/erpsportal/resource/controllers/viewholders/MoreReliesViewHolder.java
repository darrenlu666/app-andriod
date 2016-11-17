package com.codyy.erpsportal.resource.controllers.viewholders;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator.LayoutId;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.resource.models.entities.MoreRelies;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 更多回复视图保持者
 * Created by gujiajia on 2016/7/5.
 */
@LayoutId(R.layout.item_more_replies)
public class MoreReliesViewHolder extends BindingRvHolder<MoreRelies> {

    private final static String TAG = "MoreReliesViewHolder";

    @Bind(R.id.btn_more_relies)
    Button moreRepliesBtn;

    public MoreReliesViewHolder(View itemView) {
        super(itemView);
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
