package com.codyy.erpsportal.onlinemeetings.controllers.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlinemeetings.models.entities.OnlineUserInfo;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 视频会议-用户item
 * Created by poe on 15-8-10.
 */
public class OnlineUserViewHolder extends BaseRecyclerViewHolder<OnlineUserInfo> {
    private static final String TAG = OnlineUserViewHolder.class.getSimpleName();
    @Bind(R.id.sdv_user_item_start)SimpleDraweeView mHeadSDV;
    @Bind(R.id.img_user_item_end)ImageView mIconState;
    @Bind(R.id.txt_user_item_name)TextView mNameText;
    @Bind(R.id.txt_user_item_desc)TextView mRoleText;
    @Bind(R.id.img_video_state)ImageView mVideoImage;
    private String mUserId;
    private int mRole = -1;//当前用户的角色

    public OnlineUserViewHolder(View itemView ,int role,String userId) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mRole  =   role ;
        this.mUserId = userId;
    }


    @Override
    public int obtainLayoutId() {
        return R.layout.item_online_user;
    }

    @Override
    public void setData(int position , OnlineUserInfo data ) {
        mCurrentPosition    =   position ;
        mData =   data;
        if(null != data){
            int color = mNameText.getContext().getResources().getColor(R.color.md_grey_900) ;
            int resMessage = R.drawable.ic_private_message;
            mVideoImage.setVisibility(View.GONE);
            if(data.isOnline() && data.getRole() != MeetingBase.BASE_MEET_ROLE_3){
                //视频状态 .过滤自己
                if(MeetingBase.BASE_MEET_ROLE_1 ==  data.getRole() && !mUserId.equals(data.getId())){
                    mVideoImage.setVisibility(View.VISIBLE);
                }
                if(data !=null&&data.isOnline() && !mUserId.equals(data.getId())){
                    color = mNameText.getContext().getResources().getColor(R.color.md_black_1000) ;
                    if( mRole!= MeetingBase.BASE_MEET_ROLE_3){
                        resMessage = R.drawable.ic_private_message_press;
                    }
                }
            }else{
                color = mNameText.getContext().getResources().getColor(R.color.md_grey_300) ;
            }

            mNameText.setTextColor(color);
            mRoleText.setTextColor(color);
            mNameText.setText(data.getName()+(mUserId.equals(data.getId())?" (自己)":""));
            mRoleText.setText(MeetingBase.ROLES.get(data.getRole()));
            mIconState.setImageResource(resMessage);
            //icon
            ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mHeadSDV,data.getIcon());
        }
    }
}
