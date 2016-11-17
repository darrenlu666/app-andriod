package com.codyy.erpsportal.onlinemeetings.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.widgets.AsyncTextView;
import com.codyy.erpsportal.commons.widgets.AvatarView;
import com.codyy.erpsportal.commons.widgets.SuperTextView;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by poe on 16-10-11.
 */

public class OnlineChatReceiverViewHolder extends BaseRecyclerViewHolder<ChatMessage> {
    public static final String TAG = "OnlineChatReceiverViewHolder";
    @Bind(R.id.timestamp)TextView mCreateTimeTv;//发言时间
    @Bind(R.id.iv_userhead)AvatarView mHeaderPic;//头像
    @Bind(R.id.tv_userid)AsyncTextView mUserNameTv;//用户名
    @Bind(R.id.tv_chatcontent)SuperTextView mContentTv;//聊天内容

    private String mLastMessageTime ;
    private String mUserId ;

    public OnlineChatReceiverViewHolder(View itemView, String userId) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mUserId = userId;
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.row_received_message;
//        return R.layout.row_sent_message;
    }

    @Override
    public void setData(int position, ChatMessage data) {

        mData = data;
        if(null != data){
            if(getCurrentPosition() == 0){
                mCreateTimeTv.setVisibility(View.VISIBLE);
                mCreateTimeTv.setText(DateUtil.stringToDate(data.getTime()));
            }else{
//                mLastMessageTime = ((ChatMessage)(getAdapter().getData().get(getCurrentPosition()-1))).getTime();
//                if(null != mLastMessageTime && DateUtil.isCloseEnough(Long.parseLong(data.getTime()), Long.parseLong(mLastMessageTime))){
//                    mCreateTimeTv.setVisibility(View.GONE);
//                }else{
                    mCreateTimeTv.setVisibility(View.VISIBLE);
                    mCreateTimeTv.setText(DateUtil.stringToDate(data.getTime()));
//                }
            }

            //头像
            mHeaderPic.setAvatarUrl(data.getHeadUrl());
            if(mUserId.equals(data.getFrom())){
                mUserNameTv.setVisibility(View.GONE);
            }else{
                mUserNameTv.setText(data.getName());
            }
            mContentTv.setText(data.getMsg());
        }

    }
}
