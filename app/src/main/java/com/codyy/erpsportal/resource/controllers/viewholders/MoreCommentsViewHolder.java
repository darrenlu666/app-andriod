package com.codyy.erpsportal.resource.controllers.viewholders;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.resource.models.entities.MoreComments;
import com.codyy.erpsportal.resource.models.entities.MoreCommentsEvent;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 更多评论 视图保持器
 * Created by gujiajia on 2016/7/5.
 */
@LayoutId(R.layout.item_more_comments)
public class MoreCommentsViewHolder extends BindingRvHolder<MoreComments> {

    private final static String TAG = "MoreCommentsViewHolder";

    @Bind(R.id.btn_more)
    Button moreBtn;

    public MoreCommentsViewHolder(View itemView) {
        super(itemView);
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
