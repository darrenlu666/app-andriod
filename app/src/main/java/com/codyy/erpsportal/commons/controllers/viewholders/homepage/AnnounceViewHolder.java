package com.codyy.erpsportal.commons.controllers.viewholders.homepage;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.engine.AnnounceSwitcher;
import com.codyy.erpsportal.commons.models.entities.mainpage.AnnounceParse;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by poe on 17-8-7.
 */

public class AnnounceViewHolder extends BaseRecyclerViewHolder<AnnounceParse> {
    private AnnounceSwitcher mInfosSwitcher;

    @Bind(R.id.iv_little_trumpet)
    ImageView mIvLittleTrumpet;
    @Bind(R.id.ts_info)
    TextSwitcher mTsInfo;
    @Bind(R.id.rl_info_sheet)
    RelativeLayout mRlInfoSheet;

    public AnnounceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_home_announce;
    }

    @Override
    public void setData(int position, AnnounceParse data) throws Throwable {
        if(null != data && data.getData() != null){
            //广告轮转器.
            if(null == mInfosSwitcher){
                mInfosSwitcher = new AnnounceSwitcher(mTsInfo);
                mInfosSwitcher.setInfoArray(data.getData());
                mInfosSwitcher.startSwitch();
            }
        }
    }

}
