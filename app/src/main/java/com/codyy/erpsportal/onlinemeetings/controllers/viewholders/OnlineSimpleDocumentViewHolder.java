package com.codyy.erpsportal.onlinemeetings.controllers.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.MeetingShow;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 视频会议演示文档item
 * Created by poe on 15-8-10.
 */
public class OnlineSimpleDocumentViewHolder extends BaseRecyclerViewHolder<MeetingShow> {

    private final static String TAG = OnlineSimpleDocumentViewHolder.class.getSimpleName();
    @Bind(R.id.img_item_delete) ImageView mDeleteImageView;
    @Bind(R.id.txt_item_title) TextView mNameText;

    public OnlineSimpleDocumentViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_online_show_document;
    }

    @Override
    public void setData(int position , MeetingShow data) {
        mCurrentPosition    =   position ;
        mData   =   data;
        if(null != data){
            mNameText.setText(data.getShowTitle());
            //icon
            if(data.isShowDelete()){
                mDeleteImageView.setVisibility(View.VISIBLE);
            }else{
                mDeleteImageView.setVisibility(View.GONE);
            }
        }
    }
}
