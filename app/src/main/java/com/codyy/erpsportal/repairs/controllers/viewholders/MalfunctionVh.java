package com.codyy.erpsportal.repairs.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.repairs.models.entities.Malfunction;

import butterknife.Bind;

/**
 * 常见问题列表项组持者
 * Created by gujiajia on 2017/3/23.
 */

@LayoutId(R.layout.item_malfunc)
public class MalfunctionVh extends BindingRvHolder<Malfunction> {

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.tv_hits)
    TextView mHitsTv;

    public MalfunctionVh(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(Malfunction data) {
        mTitleTv.setText(data.getTitle());
        mHitsTv.setText(String.valueOf(data.getHits()));
    }
}
